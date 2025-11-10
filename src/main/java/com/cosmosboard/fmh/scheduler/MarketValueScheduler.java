package com.cosmosboard.fmh.scheduler;

import com.cosmosboard.fmh.dto.request.fms.blackbook.BlackbookSearchRequest;
import com.cosmosboard.fmh.dto.request.fms.blackbook.BlackbookVehicleSearchItem;
import com.cosmosboard.fmh.dto.request.fms.manheim.ManheimSearchRequest;
import com.cosmosboard.fmh.dto.request.fms.manheim.ManheimVehicleSearchItem;
import com.cosmosboard.fmh.dto.response.fms.blackbook.search.BlackbookSearchResponse;
import com.cosmosboard.fmh.dto.response.fms.blackbook.search.BlackbookSearchVehicleData;
import com.cosmosboard.fmh.dto.response.fms.blackbook.search.BlackbookSearchVehicleItem;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchResponse;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchVehicleData;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchVehicleItem;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.MarketValue;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.FMSService;
import com.cosmosboard.fmh.service.MarketValueService;
import com.cosmosboard.fmh.util.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketValueScheduler {
    private static final String LOG_FOUND_VEHICLE = "Found vehicle {}";

    private static final String LOG_NO_ITEMS = "No items found for vehicle {}";

    private static final String LOG_SAVING_MARKET_VALUES = "Saving market values for vehicle {}";

    private static final String LOG_SAVED_MARKET_VALUES = "Saved market values for vehicle {}";

    private static final Integer PAGE_START = 0;

    private static final Integer PAGE_SIZE = 10;

    private static final Float GRADE_MULTIPLIER = 10.0f;

    private final CarService carService;

    private final FMSService fmsService;

    private final MarketValueService marketValueService;

    /**
     * Fetch market values scheduler.
     */
    @Scheduled(cron = "0 0 16 * * *")
    public void fetchManheimMarketValues() {
        log.info("Fetching Manheim market values...");

        int page = PAGE_START;
        int pageSize = PAGE_SIZE;
        Page<Car> carPage;

        do {
            carPage = carService.findAll(PageRequest.of(page, pageSize));
            List<Car> cars = carPage.getContent();

            if (cars.isEmpty()) {
                log.info("No cars found to fetch Manheim market values.");
                break;
            }

            List<ManheimVehicleSearchItem> vehicles = new ArrayList<>();
            cars.forEach(car -> vehicles.add(ManheimVehicleSearchItem.builder()
                .vin(car.getVin())
                .mileage(car.getMileage())
                .grade(car.getCondition() != null ? (int) (car.getCondition() * GRADE_MULTIPLIER) : null)
                .region(car.getRegionType() != null ? car.getRegionType().name() : AppConstants.RegionType.NA.name())
                .build()));

            try {
                ManheimSearchResponse results = fmsService.manheimSearch(ManheimSearchRequest.builder()
                    .vehicles(vehicles)
                    .build());

                final int[] i = {0};
                results.getData().forEach(vehicle -> {
                    log.info(LOG_FOUND_VEHICLE, vehicle.getVehicle().getVin());

                    if (vehicle.getItems() == null || vehicle.getItems().isEmpty()) {
                        log.error(LOG_NO_ITEMS, vehicle.getVehicle().getVin());
                        return;
                    }

                    ManheimSearchVehicleItem bestMatchItem = vehicle.getItems().stream()
                        .filter(ManheimSearchVehicleItem::getBestMatch)
                        .findFirst()
                        .orElse(vehicle.getItems().get(0));

                    log.info("Found best match item {}", bestMatchItem);

                    //✅ MMR settle
                    Car car = cars.get(i[0]);
                    vehicle.getVehicle().setId(car.getId());
                    BigDecimal mmr = bestMatchItem.getAuction().getGood();
                    car.setMmr(mmr != null ? mmr.floatValue() : null);
                    carService.save(car);
                    log.info("Updated MMR for VIN {} with value {}", car.getVin(), car.getMmr());

                    vehicle.getVehicle().setId(cars.get(i[0]).getId());

                    log.info(LOG_SAVING_MARKET_VALUES, vehicle.getVehicle().getVin());
                    marketValueService.create(buildMarketValue(vehicle, cars, bestMatchItem));
                    log.info(LOG_SAVED_MARKET_VALUES, vehicle.getVehicle().getVin());
                    i[0]++;
                });
            } catch (Exception e) {
                log.error("Error fetching Manheim market values: {}", e.getMessage());
            }

            page++;
        } while (carPage.hasNext());

        log.info("Finished fetching Manheim market values.");
    }

    public void fetchBlackbookMarketValues() {
        log.info("Fetching Blackbook market values...");

        int page = PAGE_START;
        int pageSize = PAGE_SIZE;
        Page<Car> carPage;

        do {
            carPage = carService.findAllByStatus(AppConstants.CarStatusEnum.APPROVED, PageRequest.of(page, pageSize));
            List<Car> cars = carPage.getContent();

            if (cars.isEmpty()) {
                log.info("No approved cars found to fetch Blackbook market values.");
                break;
            }

            List<BlackbookVehicleSearchItem> vehicles = new ArrayList<>();
            cars.forEach(car -> vehicles.add(BlackbookVehicleSearchItem.builder()
                    .vin(car.getVin())
                    .mileage(car.getMileage())
                    .grade(car.getCondition() != null ? (int) (car.getCondition() * GRADE_MULTIPLIER) : null)
                    .state(car.getRegionType() != null ? car.getRegionType().name() : "NA")
                    .build()));

            try {
                BlackbookSearchResponse results = fmsService.blackbookSearch(BlackbookSearchRequest.builder()
                        .vehicles(vehicles)
                        .build());

                final int[] i = {0};
                results.getData().forEach(vehicle -> {
                    log.info(LOG_FOUND_VEHICLE, vehicle.getVehicle().getVin());

                    if (vehicle.getItems() == null || vehicle.getItems().isEmpty()) {
                        log.error(LOG_NO_ITEMS, vehicle.getVehicle().getVin());
                        return;
                    }

                    BlackbookSearchVehicleItem bestMatchItem = vehicle.getItems().get(0);

                    Car car = cars.get(i[0]);
                    log.info(LOG_SAVING_MARKET_VALUES, car.getVin());

                    try {
                        MarketValue marketValue = buildMarketValueFromBlackbook(vehicle, car, bestMatchItem);
                        marketValueService.create(marketValue);
                        log.info(LOG_SAVED_MARKET_VALUES, car.getVin());
                    } catch (Exception e) {
                        log.error("Error saving MarketValue for Car ID {}: {}", car.getId(), e.getMessage());
                    }

                    i[0]++;
                });

            } catch (Exception e) {
                log.error("Error fetching Blackbook market values: {}", e.getMessage(), e);
            }

            page++;
        } while (carPage.hasNext());

        log.info("Finished fetching Blackbook market values.");
    }

    /**
     * Build market value from vehicle data and best match item.
     *
     * @param vehicle       vehicle data
     * @param cars          cars
     * @param bestMatchItem best match item
     * @return market value
     */
    public static MarketValue buildMarketValue(ManheimSearchVehicleData vehicle, List<Car> cars,
                                               ManheimSearchVehicleItem bestMatchItem) {
        return MarketValue.builder()
            .car(getCar(vehicle, cars))
            .provider(AppConstants.MarketValueProviderEnum.MANHEIM)
            .auction(bestMatchItem.getAuction().getExcellent())
            .above(bestMatchItem.getAuction().getGood())
            .average(bestMatchItem.getAuction().getGood())
            .below(bestMatchItem.getAuction().getFair())
            .retailAuction(bestMatchItem.getRetail().getExcellent())
            .retailAbove(bestMatchItem.getRetail().getGood())
            .retailAverage(bestMatchItem.getRetail().getGood())
            .retailBelow(bestMatchItem.getRetail().getFair())
            .region(vehicle.getVehicle().getRegion())
            .mileage(vehicle.getVehicle().getMileage())
            .averageMileage(bestMatchItem.getAverageMileage())
            .conditionGrade(bestMatchItem.getConditionGrade())
            .build();
    }

    /**
     * Build market value from vehicle data and best match item.
     *
     * @param vehicle       vehicle data
     * @param cars          cars
     * @param bestMatchItem best match item
     * @return market value
     */
    public static MarketValue buildMarketValue(BlackbookSearchVehicleData vehicle, List<Car> cars,
                                               BlackbookSearchVehicleItem bestMatchItem) {
        ObjectMapper objectMapper = new ObjectMapper();
        String data = null;
        try {
            data = objectMapper.writeValueAsString(bestMatchItem);
        } catch (JsonProcessingException e) {
            log.error("Error serializing best match item: {}", e.getMessage());
        }

        return MarketValue.builder()
            .car(getCar(vehicle, cars))
            .provider(AppConstants.MarketValueProviderEnum.BLACKBOOK)
            .auction(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeXclean()))
            .above(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeClean()))
            .average(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeAvg()))
            .below(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeRough()))
            .retailAuction(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailXclean()))
            .retailAbove(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailClean()))
            .retailAverage(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailAvg()))
            .retailBelow(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailRough()))
            .region(AppConstants.RegionType.NA.name())
            .mileage(vehicle.getVehicle().getMileage())
            .averageMileage(bestMatchItem.getFinalWholeAvg())
            .conditionGrade(vehicle.getVehicle().getGrade() != null ?
                (float) vehicle.getVehicle().getGrade() / GRADE_MULTIPLIER : null)
            .data(data)
            .build();
    }

    private MarketValue buildMarketValueFromBlackbook(BlackbookSearchVehicleData vehicleResult, Car car, BlackbookSearchVehicleItem bestMatchItem) {

        ObjectMapper objectMapper = new ObjectMapper();
        String data = null;
        try {
            data = objectMapper.writeValueAsString(bestMatchItem);
        } catch (JsonProcessingException e) {
            log.error("Error serializing best match item: {}", e.getMessage());
        }

        double finalWholeXclean = bestMatchItem.getAdjustedWholeXclean();
        double finalWholeRough = bestMatchItem.getAdjustedWholeRough();
        double conditionAdjustedWholesale = bestMatchItem.getConditionAdjustedWholesale();

        double x = (finalWholeXclean - conditionAdjustedWholesale) / (finalWholeXclean - finalWholeRough);

        double finalRetailXclean = bestMatchItem.getAdjustedRetailXclean();
        double finalRetailRough = bestMatchItem.getAdjustedRetailRough();

        double conditionAdjustedRetail = finalRetailXclean - (finalRetailXclean - finalRetailRough) * x;

        return MarketValue.builder()
                .car(car)
                .provider(AppConstants.MarketValueProviderEnum.BLACKBOOK)
                .auction(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeXclean()))
                .above(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeClean()))
                .average(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeAvg()))
                .below(BigDecimal.valueOf(bestMatchItem.getAdjustedWholeRough()))
                .retailAuction(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailXclean()))
                .retailAbove(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailClean()))
                .retailAverage(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailAvg()))
                .retailBelow(BigDecimal.valueOf(bestMatchItem.getAdjustedRetailRough()))
                .conditionAdjustedWholesale(BigDecimal.valueOf(conditionAdjustedWholesale))
                .conditionAdjustedRetail(BigDecimal.valueOf(conditionAdjustedRetail))
                .mileage(car.getMileage())
                .conditionGrade(vehicleResult.getVehicle().getGrade() != null ?
                        (float) vehicleResult.getVehicle().getGrade() / GRADE_MULTIPLIER : null)
                .data(data)
                .build();
    }

    /**
     * Get a car from vehicle data.
     *
     * @param vehicle vehicle data
     * @param cars    cars
     * @return car
     */
    private static Car getCar(ManheimSearchVehicleData vehicle, List<Car> cars) {
        return cars.stream()
            .filter(c -> c.getVin().equals(vehicle.getVehicle().getVin()))
            .findFirst()
            .orElseThrow();
    }

    /**
     * Get a car from vehicle data.
     *
     * @param vehicle vehicle data
     * @param cars    cars
     * @return car
     */
    private static Car getCar(BlackbookSearchVehicleData vehicle, List<Car> cars) {
        return cars.stream()
            .filter(c -> c.getId().equals(vehicle.getVehicle().getId()))
            .findFirst()
            .orElseThrow();
    }
}

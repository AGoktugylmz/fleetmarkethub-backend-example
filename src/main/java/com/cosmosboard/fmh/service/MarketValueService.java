package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.MarketValue;
import com.cosmosboard.fmh.repository.jpa.MarketValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketValueService {
    private static final int BATCH_SIZE = 500;

    private final MarketValueRepository marketValueRepository;

    /**
     * Create a new MarketValue
     *
     * @param marketValue MarketValue to create
     * @return Created MarketValue
     */
    public MarketValue create(MarketValue marketValue) {
        return marketValueRepository.save(marketValue);
    }

    /**
     * Find last MarketValues for a car
     *
     * @param id Car id
     * @return Last MarketValues for a car
     */
    public List<MarketValue> findLastMarketValuesForCar(String id) {
        return marketValueRepository.findLastMarketValuesForCar(id);
    }

    public List<MarketValue> findLastMarketValuesForCars(List<String> carIds) {
        if (carIds == null || carIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (carIds.size() > BATCH_SIZE) {
            List<MarketValue> result = new ArrayList<>();
            partitionList(carIds).forEach(batch ->
                    result.addAll(marketValueRepository.findLastMarketValuesForCars(batch))
            );
            return result;
        }

        return marketValueRepository.findLastMarketValuesForCars(carIds);
    }

    private List<List<String>> partitionList(List<String> list) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            partitions.add(list.subList(i, Math.min(i + BATCH_SIZE, list.size())));
        }
        return partitions;
    }
}

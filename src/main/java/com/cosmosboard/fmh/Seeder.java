package com.cosmosboard.fmh;

import com.cosmosboard.fmh.entity.Address;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.CarBrand;
import com.cosmosboard.fmh.entity.CarClass;
import com.cosmosboard.fmh.entity.CarGroup;
import com.cosmosboard.fmh.entity.CarModel;
import com.cosmosboard.fmh.entity.CarModelTrim;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Image;
import com.cosmosboard.fmh.entity.Location;
import com.cosmosboard.fmh.entity.Message;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.OfferConversation;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.repository.jpa.AddressRepository;
import com.cosmosboard.fmh.repository.jpa.CarBrandRepository;
import com.cosmosboard.fmh.repository.jpa.CarClassRepository;
import com.cosmosboard.fmh.repository.jpa.CarGroupRepository;
import com.cosmosboard.fmh.repository.jpa.CarModelRepository;
import com.cosmosboard.fmh.repository.jpa.CarModelTrimRepository;
import com.cosmosboard.fmh.repository.jpa.CategoryRepository;
import com.cosmosboard.fmh.repository.jpa.CityRepository;
import com.cosmosboard.fmh.repository.jpa.CompanyRepository;
import com.cosmosboard.fmh.repository.jpa.CarRepository;
import com.cosmosboard.fmh.repository.jpa.DistrictRepository;
import com.cosmosboard.fmh.repository.jpa.EmployeeRepository;
import com.cosmosboard.fmh.repository.jpa.ImageRepository;
import com.cosmosboard.fmh.repository.jpa.LocationRepository;
import com.cosmosboard.fmh.repository.jpa.MessageRepository;
import com.cosmosboard.fmh.repository.jpa.OfferConversationRepository;
import com.cosmosboard.fmh.repository.jpa.OfferRepository;
import com.cosmosboard.fmh.repository.jpa.RoleRepository;
import com.cosmosboard.fmh.repository.jpa.UserRepository;
import com.cosmosboard.fmh.util.AppConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class Seeder {
    private static final String NAME = "name";

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final CarRepository carRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarClassRepository carClassRepository;
    private final CarGroupRepository carGroupRepository;
    private final CarModelRepository carModelRepository;
    private final CarModelTrimRepository carModelTrimRepository;
    private final OfferRepository offerRepository;
    private final OfferConversationRepository offerConversationRepository;
    private final EmployeeRepository employeeRepository;
    private final ImageRepository imageRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    private final Random randomGenerator = new Random();
    private final Faker faker = new Faker(new Locale("tr", "TR"));

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void run(ApplicationReadyEvent event) {
        if ((Objects.equals(ddl, "create") || Objects.equals(ddl, "create-drop")) &&
                (roleRepository.count() < 1 || userRepository.count() < 1 || cityRepository.count() < 1 ||
                    districtRepository.count() < 1 || categoryRepository.count() < 1 || addressRepository.count() < 1 ||
                    companyRepository.count() < 1 || locationRepository.count() < 1 || carRepository.count() < 1 ||
                    offerRepository.count() < 1 || offerConversationRepository.count() < 1 || employeeRepository.count() < 1)) {
            roleRepository.deleteAll();
            userRepository.deleteAll();
            cityRepository.deleteAll();
            districtRepository.deleteAll();
            categoryRepository.deleteAll();
            addressRepository.deleteAll();
            companyRepository.deleteAll();
            locationRepository.deleteAll();
            carRepository.deleteAll();
            offerRepository.deleteAll();
            offerConversationRepository.deleteAll();
            employeeRepository.deleteAll();
            imageRepository.deleteAll();
            carBrandRepository.deleteAll();
            carClassRepository.deleteAll();
            carGroupRepository.deleteAll();
            carModelRepository.deleteAll();
            carModelTrimRepository.deleteAll();
            messageRepository.deleteAll();
            log.info("Trying to create dummy data...");
            List<Role> roles = createRoles();
            List<User> users = createUsers(roles);
            List<District> cityAndDistricts = createCityAndDistricts();
            List<Category> categories = createCategories();
            List<Address> addressess = createAddressess(users, cityAndDistricts);
            List<Company> companies = createCompanies(cityAndDistricts, categories);
            List<Location> locations = createLocations(companies, cityAndDistricts, categories);
            List<CarBrand> carBrands = createCarBrands();
            List<CarGroup> carGroups = createCarGroups();
            List<CarClass> carClasses = createCarClasses();
            List<CarModel> carModels = createCarModels(carBrands, carGroups, carClasses);
            List<CarModelTrim> carModelTrims = createCarModelTrims(carModels);
            List<Car> cars = createCars(companies, categories, locations, carModels, users, carModelTrims);
            List<Image> images = createImages(cars);
            List<Offer> offers = createOffers(cars, companies);
            createOfferConversations(offers, users);
            List<Employee> employees = createEmployees(users, companies);
            createMessages(users, companies);
            log.info("{} data operations successfully for {} seconds", employees.size(), event.getTimeTaken().getSeconds());
        }
    }

    private List<Role> createRoles() {
        final List<Role> roleList = new ArrayList<>();
        roleList.add(Role.builder().name(AppConstants.RoleEnum.ADMIN).build());
        roleList.add(Role.builder().name(AppConstants.RoleEnum.USER).build());
        roleList.add(Role.builder().name(AppConstants.RoleEnum.CONSULTANT).build());
        return roleRepository.saveAll(roleList);
    }

    private List<User> createUsers(final List<Role> roles) {
        final List<User> userList = new ArrayList<>();
        final Role adminRole = roles.get(0);
        final Role userRole = roles.get(1);

        final String encode = passwordEncoder.encode("Secret1.");
        userList.add(User.builder()
            .name(faker.name().firstName())
            .lastName(faker.name().lastName())
            .email("admin@example.com")
            .roles(List.of(userRole, adminRole))
            .password(encode)
            .emailActivatedAt(LocalDateTime.now())
            .gsm(faker.phoneNumber().phoneNumber())
            .title(faker.name().title())
            .build());
        userList.add(User.builder()
            .name(faker.name().firstName())
            .lastName(faker.name().lastName())
            .email("user@example.com")
            .roles(List.of(userRole))
            .password(encode)
            .emailActivatedAt(LocalDateTime.now())
            .gsm(faker.phoneNumber().phoneNumber())
            .title(faker.name().title())
            .build());
        return userRepository.saveAll(userList);
    }

    private List<District> createCityAndDistricts() {
        List<District> districtList = new ArrayList<>();
        String path = "data/cities.json";
        try {
            Resource resource = new ClassPathResource(path);
            InputStream inputStream = resource.getInputStream();
            JsonNode contextObj = objectMapper.readTree(inputStream);
            for (JsonNode cityObject : contextObj) {
                String cityCode = cityObject.get("code").textValue();
                String cityName = cityObject.get(NAME).textValue();
                JsonNode districts = cityObject.get("districts");
                City city = cityRepository.save(City.builder().name(cityName).code(cityCode).build());
                log.info("Created city: {} - {}...", cityCode, cityName);

                for (JsonNode districtObject : districts) {
                    String districtName = districtObject.get(NAME).textValue();
                    District district = districtRepository.save(District.builder().city(city).name(districtName).build());
                    districtList.add(district);
                    log.info("Creating district: {} for city {}", districtName, cityName);
                }
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.info("Created city and districts.");
        return districtList;
    }

    private List<Category> createCategories() {
        List<Category> categoryList = new ArrayList<>();
        log.info("Creating categories...");
        String path = "data/categories.json";
        try {
            Resource resource = new ClassPathResource(path);
            InputStream inputStream = resource.getInputStream();
            JsonNode contextObj = objectMapper.readTree(inputStream);
            for (JsonNode categoryObject : contextObj) {
                String categoryName = categoryObject.get("name").textValue();
                String categoryDescription = categoryObject.get("description").textValue();
                String categoryContent = categoryObject.get("content").textValue();
                Boolean categoryIsActive = categoryObject.get("isActive").booleanValue();
                Category category = Category.builder()
                        .name(categoryName)
                        .slug(categoryName)
                        .description(faker.gameOfThrones().dragon())
                        .content(faker.gameOfThrones().house())
                        .sort(randomGenerator.nextLong())
                        .isActive(randomGenerator.nextBoolean())
                        .description(categoryDescription)
                        .content(categoryContent).isActive(categoryIsActive).build();
                category = categoryRepository.save(category);
                categoryList.add(category);
                log.info("Created category: {}...", category.getName());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.info("Created categories.");
        return categoryList;
    }

    private List<Address> createAddressess(List<User> userList, List<District> districtList) {
        List<Address> addressList = new ArrayList<>();
        for (User user : userList) {
            District district = districtList.get(randomGenerator.nextInt(districtList.size()));
            addressList.add(Address.builder()
                    .name(faker.superhero().name())
                    .address(faker.address().fullAddress())
                    .user(user).district(district).city(district.getCity()).build());
        }
        log.info("Created addresses.");
        return addressRepository.saveAll(addressList);
    }

    private List<Company> createCompanies(List<District> districtList, List<Category> categories) {
        final List<Company> companyList = new ArrayList<>();
        final District district1 = districtList.get(randomGenerator.nextInt(districtList.size()));
        final Company company1 = Company.builder()
                .name(faker.company().name())
                .description(faker.company().catchPhrase())
                .category(categories.get(randomGenerator.nextInt(categories.size())))
                .build();
        companyList.add(company1);

        final District district2 = districtList.get(randomGenerator.nextInt(districtList.size()));
        final Company company2 = Company.builder()
                .name(faker.company().name())
                .description(faker.company().catchPhrase())
                .category(categories.get(randomGenerator.nextInt(categories.size())))
                .build();
        companyList.add(company2);
        log.info("Created Companies.");
        return companyRepository.saveAll(companyList);
    }

    private List<Location> createLocations(List<Company> companyList, List<District> districtList, List<Category> categoryList) {
        final List<Location> locationList = new ArrayList<>();
        for (Company company : companyList) {
            District district = districtList.get(randomGenerator.nextInt(districtList.size()));
            locationList.add(Location.builder()
                    .company(company)
                    .name(faker.pokemon().name())
                    .description(faker.pokemon().location())
                    .district(district)
                    .city(district.getCity())
                    .isActive(randomGenerator.nextBoolean())
                    .build());
        }
        log.info("Created Locations.");
        return locationRepository.saveAll(locationList);
    }

    private List<CarBrand> createCarBrands() {
        final List<CarBrand> carBrands = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CarBrand carBrand = CarBrand.builder()
                    .name(faker.vehicle().make())
                    .build();
            carBrands.add(carBrand);
        }
        return carBrandRepository.saveAll(carBrands);
    }

    private List<CarGroup> createCarGroups() {
        final List<CarGroup> carGroups = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CarGroup carGroup = CarGroup.builder()
                    .name(faker.vehicle().carType())
                    .build();
            carGroups.add(carGroup);
        }
        return carGroupRepository.saveAll(carGroups);
    }

    private List<CarClass> createCarClasses() {
        final List<CarClass> carClasses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CarClass carClass = CarClass.builder()
                    .name(faker.vehicle().carType())
                    .build();
            carClasses.add(carClass);
        }
        return carClassRepository.saveAll(carClasses);
    }

    private List<CarModel> createCarModels(List<CarBrand> carBrands, List<CarGroup> carGroups, List<CarClass> carClasses) {
        final List<CarModel> carModelList = new ArrayList<>();
        for (CarBrand carBrand: carBrands) {
            final CarModel carModel = CarModel.builder()
                    .name(faker.vehicle().model())
                    .brand(carBrand)
                    .build();
            carModelList.add(carModel);
        }
        return carModelRepository.saveAll(carModelList);
    }

    private List<CarModelTrim> createCarModelTrims(List<CarModel> carModels) {
        final List<CarModelTrim> carModelTrimList = new ArrayList<>();
        for (CarModel carModel: carModels) {
            final CarModelTrim carModelTrim = CarModelTrim.builder()
                    .name(faker.vehicle().model())
                    .model(carModel)
                    .build();
            carModelTrimList.add(carModelTrim);
            final CarModelTrim carModelTrim2 = CarModelTrim.builder()
                    .name(faker.vehicle().model())
                    .model(carModel)
                    .build();
            carModelTrimList.add(carModelTrim2);
        }
        return carModelTrimRepository.saveAll(carModelTrimList);
    }

    private List<Car> createCars(List<Company> companyList, List<Category> categoryList, List<Location> locationList,
                                 List<CarModel> carModels, List<User> users, List<CarModelTrim> carModelTrims) {
        final List<Car> carList = new ArrayList<>();
        companyList = Stream.of(companyList, companyList, companyList).flatMap(Collection::stream).toList();
        for (Company company : companyList) {
            final List<Category> subCategoryList = new ArrayList<>();
            final int randomCategoryInt = randomGenerator.nextInt(categoryList.size());
            for (int i = 0; i <= randomCategoryInt; i++) {
                final Category category = categoryList.get(i);
                if (!subCategoryList.contains(category)) {
                    subCategoryList.add(category);
                }
            }
            final Car car1 = Car.builder()
                    .company(company)
                    .title(faker.brand().car())
                    .content(faker.lorem().paragraph(5))
                    .status(AppConstants.CarStatusEnum.values()[randomGenerator.nextInt(AppConstants.CarStatusEnum.values().length)])
                    .statusMessage(faker.lorem().paragraph(15))
                    .model(carModels.get(randomGenerator.nextInt(carModels.size())))
                    .favoritedUsers(List.of(users.get(randomGenerator.nextInt(users.size()))))
                    .vin(faker.vehicle().vin())
                    .unit(faker.vehicle().carType())
                    .modelYear(faker.timeAndDate().birthday().getYear())
                    .exteriorColor(faker.color().name())
                    .interiorColor(faker.color().name())
                    .defaultMarketValue((float) faker.number().randomNumber(faker.random().nextInt(5, 6), false))
                    .mileage(faker.number().numberBetween(5000, 200000))
                    .reportLink(faker.internet().url())
                    .equipment(faker.lorem().paragraph())
                    .condition((float) faker.number().randomDouble(1, 0, 5))
                    .location(locationList.get(randomGenerator.nextInt(locationList.size())))
                    .trim(carModelTrims.get(randomGenerator.nextInt(carModelTrims.size())))
                    .build();
            carList.add(car1);
        }
        log.info("Created Cars.");
        return carRepository.saveAll(carList);
    }

    private List<Image> createImages(final List<Car> cars) {
        final List<Image> imageList = new ArrayList<>();
        int i = 1;
        for (Car car : Stream.of(cars, cars).flatMap(Collection::stream).toList()) {
            final Image image = Image.builder()
                    .car(car)
                    .url("images/"+i+".jpeg")
                    .status(AppConstants.ImageStatusEnum.ACTIVE)
                    .title(faker.avatar().image())
                    .build();
            i++;
            imageList.add(image);
        }
        return imageRepository.saveAll(imageList);
    }

    private List<Offer> createOffers(List<Car> cars, List<Company> companyList) {
        List<Offer> offerList = new ArrayList<>();
        for (Car car : cars) {
            final Offer offer = Offer.builder()
                    .car(car)
                    .company(companyList.get(randomGenerator.nextInt(companyList.size())))
                    .price(randomBigDecimal())
                    .transactionAt(LocalDateTime.now())
                    .status(AppConstants.OfferStatusEnum.values()[randomGenerator.nextInt(AppConstants.OfferStatusEnum.values().length)])
                    .build();
            offerList.add(offer);
        }
        log.info("Created Offers.");
        return offerRepository.saveAll(offerList);
    }

    private void createOfferConversations(List<Offer> offerList, List<User> userList) {
        List<OfferConversation> offerConversationList = new ArrayList<>();
        for (Offer offer : offerList) {
            OfferConversation offerConversation1 = OfferConversation.builder()
                    .offer(offer)
                    .sender(userList.get(randomGenerator.nextInt(userList.size())))
                    .message(faker.lorem().word())
                    .status(AppConstants.OfferConversationStatusEnum.values()[randomGenerator.nextInt(AppConstants.OfferConversationStatusEnum.values().length)])
                    .build();
            OfferConversation offerConversation2 = OfferConversation.builder()
                    .offer(offer)
                    .sender(userList.get(randomGenerator.nextInt(userList.size())))
                    .message(faker.lorem().word())
                    .status(AppConstants.OfferConversationStatusEnum.values()[randomGenerator.nextInt(AppConstants.OfferConversationStatusEnum.values().length)])
                    .build();
            offerConversationList.add(offerConversation1);
            offerConversationList.add(offerConversation2);
        }
        log.info("Created OfferConversations.");
        offerConversationRepository.saveAll(offerConversationList);
    }

    private List<Employee> createEmployees(List<User> userList, List<Company> companyList) {
        List<Employee> employeeList = new ArrayList<>();
        Company company1 = companyList.get(0);
        employeeList.add(Employee.builder().user(userList.get(0)).company(company1).isOwner(true).build());
        employeeList.add(Employee.builder().user(userList.get(1)).company(company1).isOwner(false).build());
        Company company2 = companyList.get(1);
        employeeList.add(Employee.builder().user(userList.get(1)).company(company2).isOwner(true).build());
        employeeList.add(Employee.builder().user(userList.get(0)).company(company2).isOwner(false).build());
        log.info("Created Employees.");
        return employeeRepository.saveAll(employeeList);
    }

    private List<Message> createMessages(final List<User> users, final List<Company> companies) {
        final User sender = users.get(randomGenerator.nextInt(users.size()));
        final Company fromCompany = companies.get(randomGenerator.nextInt(companies.size()));
        final Company toCompany = companies.get(randomGenerator.nextInt(companies.size()));

        final List<Message> messageList = new ArrayList<>();
        messageList.add(Message.builder()
                .from(fromCompany)
                .to(toCompany)
                .userId(sender.getId())
                .type("private")
                .content(faker.lorem().paragraph())
                .readAt(LocalDateTime.now())
                .build());

        return messageRepository.saveAll(messageList);
    }

    private BigDecimal randomBigDecimal() {
        BigDecimal max = new BigDecimal("100.0");
        return BigDecimal.valueOf(Math.random()).divide(max, RoundingMode.CEILING);
    }
}
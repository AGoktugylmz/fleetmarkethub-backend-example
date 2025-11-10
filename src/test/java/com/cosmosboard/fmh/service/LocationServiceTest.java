package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.location.CreateLocationRequest;
import com.cosmosboard.fmh.dto.request.location.UpdateLocationRequest;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.Location;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for LocationService")
public class LocationServiceTest {
    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock private MessageSourceService messageSourceService;

    @Mock private CityService cityService;

    @Mock private DistrictService districtService;

    @Mock private CategoryService categoryService;

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void given_whenFindAll_thenAssertBody() {
            // Given
            List<Location> locations = List.of(Factory.createLocation(), Factory.createLocation());
            when(locationRepository.findAll()).thenReturn(locations);

            // When
            List<Location> result = locationService.findAll();

            // Then
            assertEquals(locations, result);
        }

    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            String locationId = "locationId";
            Location location = Factory.createLocation();
            when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

            // When
            Location result = locationService.findOneById(locationId);

            // Then
            assertEquals(location, result);
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // Given
            String locationId = "locationId";
            when(locationRepository.findById(locationId)).thenReturn(Optional.empty());
            when(messageSourceService.get("location_not_found")).thenReturn("Location not found");

            // When / Then
            assertThrows(NotFoundException.class, () -> locationService.findOneById(locationId));
        }


    }

    @Nested
    @DisplayName("Test class for save scenarios")
    class SaveTest {
        @Test
        void givenLocation_whenSave_thenAssertBody() {
            // Given
            Location location = Factory.createLocation();
            when(locationRepository.save(location)).thenReturn(location);

            // When
            Location result = locationService.save(location);

            // Then
            assertEquals(location, result);
        }
    }

    @Nested
    @DisplayName("Test class for saveAll scenarios")
    class SaveAllTest {
        @Test
        void givenLocation_whenSaveAll_thenAssertBody() {
            // Given
            List<Location> locations = List.of(Factory.createLocation(), Factory.createLocation());
            when(locationRepository.saveAll(locations)).thenReturn(locations);

            // When
            List<Location> result = locationService.saveAll(locations);

            // Then
            assertEquals(locations, result);
        }

    }

    @Nested
    @DisplayName("Test class for createLocationFromDto scenarios")
    class CreateLocationFromDtoTest {
        @Test
        void givenCreateLocationRequestAndCompanyAnd_whenCreateLocationFromDto_thenAssertBody() {
            // Given
            CreateLocationRequest request = Factory.createCreateLocationRequest();
            Company company = Factory.createCompany();
            City city = Factory.createCity();
            District district = Factory.createDistrict();

            when(cityService.findOneById(request.getCityId())).thenReturn(city);
            when(districtService.findOneById(request.getDistrictId())).thenReturn(district);

            // When
            Location result = locationService.createLocationFromDto(request, company);

            // Then
            assertNotNull(result);
            assertEquals(company, result.getCompany());
            assertEquals(request.getName(), result.getName());
            assertEquals(request.getDescription(), result.getDescription());
            assertEquals(city, result.getCity());
            assertEquals(district, result.getDistrict());
        }

    }

    @Nested
    @DisplayName("Test class for updateLocationFromDto scenarios")
    class UpdateLocationFromDtoTest {
        @Test
        void givenUpdateLocationRequestAndLocation_whenUpdateLocationFromDto_thenAssertBody() {
            // Given
            UpdateLocationRequest request = Factory.createUpdateLocationRequest();
            request.setName("New Name");
            request.setDescription("New Description");
            request.setCityId("newCityId");
            request.setDistrictId("newDistrictId");
            request.setIsActive(true);

            Location existingLocation = Factory.createLocation();
            existingLocation.setName("Old Name");
            existingLocation.setDescription("Old Description");
            existingLocation.setCity(Factory.createCity());
            existingLocation.setDistrict(Factory.createDistrict());
            existingLocation.setIsActive(false);

            when(cityService.findOneById("newCityId")).thenReturn(Factory.createCity());
            when(districtService.findOneById("newDistrictId")).thenReturn(Factory.createDistrict());

            // When
            Location updatedLocation = locationService.updateLocationFromDto(request, existingLocation);

            // Then
            assertEquals("New Name", updatedLocation.getName());
            assertEquals("New Description", updatedLocation.getDescription());
            assertNotNull(updatedLocation.getCity());
            assertNotNull(updatedLocation.getDistrict());
            assertTrue(updatedLocation.getIsActive());

            // Verify
            verify(cityService).findOneById("newCityId");
            verify(districtService).findOneById("newDistrictId");
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenLocation_whenDelete_thenAssertBody() {
            // Given
            Location location = new Location();

            // When
            locationService.delete(location);

            // Then
            verify(locationRepository, times(1)).delete(location);
        }
    }

    @Nested
    @DisplayName("Test class for deleteById scenarios")
    class DeleteByIdTest {
        @Test
        void givenId_whenDeleteById_thenAssertBody() {
            // Given
            String locationId = "locationId";

            // When
            locationService.deleteById(locationId);

            // Then
            verify(locationRepository, times(1)).deleteById(locationId);
        }
    }

}

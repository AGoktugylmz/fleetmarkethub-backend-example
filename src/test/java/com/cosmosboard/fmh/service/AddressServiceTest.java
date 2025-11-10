package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.address.CreateAddressRequest;
import com.cosmosboard.fmh.dto.request.address.UpdateAddressRequest;
import com.cosmosboard.fmh.entity.Address;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.specification.AddressFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AddressService")
class AddressServiceTest {
    @InjectMocks
    private AddressService addressService;
    @Mock
    private AddressRepository addressRepository;
    @Mock private UserService userService;
    @Mock private CityService cityService;
    @Mock private DistrictService districtService;
    @Mock private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(addressRepository.count()).thenReturn(1L);
            // When
            Long count = addressService.count();
            // Then
            assertNotNull(count);
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void given_whenFindAll_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            List<Address> addresses = List.of(address);
            when(addressRepository.findAll()).thenReturn(addresses);
            // When
            List<Address> result = addressService.findAll();
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(address, result.get(0));
            assertEquals(address.getId(), result.get(0).getId());
            assertEquals(address.getUser().getId(), result.get(0).getUser().getId());
            assertEquals(address.getName(), result.get(0).getName());
            assertEquals(address.getCity().getId(), result.get(0).getCity().getId());
            assertEquals(address.getDistrict().getId(), result.get(0).getDistrict().getId());
            assertEquals(address.getAddress(), result.get(0).getAddress());
            assertEquals(address.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findAllWithPagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        void givenAddressCriteriaAndPaginationCriteria_whenFindAllWithPagination_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            Page<Address> page = new PageImpl<>(List.of(address));
            when(addressRepository.findAll(any(AddressFilterSpecification.class),
                    any(Pageable.class))).thenReturn(page);
            // When
            Page<Address> result = addressService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(address, result.getContent().get(0));
            assertEquals(address.getId(), result.getContent().get(0).getId());
            assertEquals(address.getUser().getId(), result.getContent().get(0).getUser().getId());
            assertEquals(address.getName(), result.getContent().get(0).getName());
            assertEquals(address.getCity().getId(), result.getContent().get(0).getCity().getId());
            assertEquals(address.getDistrict().getId(), result.getContent().get(0).getDistrict().getId());
            assertEquals(address.getAddress(), result.getContent().get(0).getAddress());
            assertEquals(address.getCreatedAt(), result.getContent().get(0).getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.getContent().get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            when(addressRepository.findById(any(String.class))).thenReturn(Optional.of(address));
            // When
            Address result = addressService.findOneById(address.getId());
            // Then
            assertNotNull(result);
            assertEquals(address, result);
            assertEquals(address.getId(), result.getId());
            assertEquals(address.getName(), result.getName());
            assertEquals(address.getUser().getId(), result.getUser().getId());
            assertEquals(address.getAddress(), result.getAddress());
            assertEquals(address.getCreatedAt(), result.getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> addressService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("address_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for findOneByIdAndUserId scenarios")
    class FindOneByIdAndUserIdTest {
        @Test
        void givenIdAndUserId_whenFindOneByIdAndUserId_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            when(addressRepository.findByIdAndUserId(any(String.class),
                    any(String.class))).thenReturn(Optional.of(address));
            // When
            Address result = addressService.findOneByIdAndUserId(address.getId(), address.getUser().getId());
            // Then
            assertNotNull(result);
            assertEquals(address, result);
            assertEquals(address.getId(), result.getId());
            assertEquals(address.getName(), result.getName());
            assertEquals(address.getUser().getId(), result.getUser().getId());
            assertEquals(address.getAddress(), result.getAddress());
            assertEquals(address.getCreatedAt(), result.getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenIdAndUserId_whenFindOneByIdAndUserIdWithAddressNotFound_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> addressService.findOneByIdAndUserId("xxx", "xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("address_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenUserIdAndCreateAddressRequest_whenCreateWithExistAddress_thenThrowBadRequestException_FromAddressNameAlreadyExists() {
            // Given
            CreateAddressRequest request = Factory.createCreateAddressRequest();
            Address address = Factory.createAddress();
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(cityService.findOneById(any())).thenReturn(address.getCity());
            when(districtService.findOneByIdAndCityId(any(), any())).thenReturn(address.getDistrict());
            when(addressRepository.existsByNameAndUserId(request.getName(), address.getUser().getId()))
                    .thenReturn(true);
            // When
            Executable closureToTest = () -> addressService.create(address.getUser().getId(), request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("address_name_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
        @Test
        void givenUserIdAndCreateAddressRequest_whenCreate_thenAssertBody() {
            // Given
            CreateAddressRequest request = Factory.createCreateAddressRequest();
            Address address = Factory.createAddress();
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(cityService.findOneById(any())).thenReturn(address.getCity());
            when(districtService.findOneByIdAndCityId(any(), any())).thenReturn(address.getDistrict());
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            // When
            Address result = addressService.create(address.getUser().getId(), request);
            // Then
            assertNotNull(result);
            assertEquals(address, result);
            assertEquals(address.getId(), result.getId());
            assertEquals(address.getName(), result.getName());
            assertEquals(address.getUser().getId(), result.getUser().getId());
            assertEquals(address.getAddress(), result.getAddress());
            assertEquals(address.getCreatedAt(), result.getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUserIdAndUpdateAddressRequest_whenUpdateWithExistingAddressName_thenThrowBadRequestException_FromAddressNameAlreadyExists() {
            // Given
            UpdateAddressRequest request = Factory.createUpdateAddressRequest();
            Address address = Factory.createAddress();
            when(addressRepository.findByIdAndUserId(address.getId(), address.getUser().getId()))
                    .thenReturn(Optional.of(address));
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(addressRepository.existsByNameAndNotUserId(request.getName(), address.getUser().getId()))
                    .thenReturn(true);
            // When
            Executable closureToTest = () -> addressService.update(address.getId(), address.getUser().getId(), request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("address_name_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }

        @Test
        void givenIdAndUserIdAndUpdateAddressRequest_whenUpdateWithAddressWithDistrictId_thenBadRequestExceptionIsThrown() {
            // Given
            Address address = Factory.createAddress();
            UpdateAddressRequest request = Factory.createUpdateAddressRequest();
            request.setCityId("newCityId");
            request.setDistrictId("newDistrictId");

            City city = Factory.createCity();
            city.setId("newCityId");
            District district = Factory.createDistrict();
            district.setId("newDistrictId");

            when(addressRepository.findByIdAndUserId(address.getId(), address.getUser().getId()))
                    .thenReturn(Optional.of(address));
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(addressRepository.existsByNameAndNotUserId(request.getName(), address.getUser().getId()))
                    .thenReturn(false);
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            when(cityService.findOneById(request.getCityId())).thenReturn(city);
            when(districtService.findOneByIdAndCityId(request.getDistrictId(), city.getId())).thenReturn(district);

            // When
            Address result = addressService.update(address.getId(), address.getUser().getId(), request);

            // Then
            assertNotNull(result);
            assertEquals(address, result);
            assertEquals(city, result.getCity());
            assertEquals(district, result.getDistrict());
            verify(cityService, times(1)).findOneById(request.getCityId());
            verify(districtService, times(1)).findOneByIdAndCityId(request.getDistrictId(), city.getId());
        }

        @Test
        void givenIdAndUserIdAndUpdateAddressRequest_whenUpdateWithAddressWithoutDistrictId_thenBadRequestExceptionIsThrown() {
            // Given
            Address address = Factory.createAddress();
            UpdateAddressRequest request = Factory.createUpdateAddressRequest();
            request.setCityId("newCityId");
            request.setDistrictId(null);

            City city = Factory.createCity();
            city.setId("newCityId");

            when(addressRepository.findByIdAndUserId(address.getId(), address.getUser().getId()))
                    .thenReturn(Optional.of(address));
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(addressRepository.existsByNameAndNotUserId(request.getName(), address.getUser().getId()))
                    .thenReturn(false);
            when(cityService.findOneById(request.getCityId())).thenReturn(city);

            // When/Then
            assertThrows(BadRequestException.class, () -> {
                addressService.update(address.getId(), address.getUser().getId(), request);
            });
        }
        @Test
        void givenIdAndUserIdAndUpdateAddressRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateAddressRequest request = new UpdateAddressRequest();
            request.setName("New Address Name");
            request.setAddress("New Address");

            Address address = Factory.createAddress();
            when(addressRepository.findByIdAndUserId(address.getId(), address.getUser().getId()))
                    .thenReturn(Optional.of(address));
            when(userService.findOneById(address.getUser().getId())).thenReturn(address.getUser());
            when(addressRepository.existsByNameAndNotUserId(request.getName(), address.getUser().getId()))
                    .thenReturn(false);
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            // When
            Address result = addressService.update(address.getId(), address.getUser().getId(), request);
            // Then
            assertNotNull(result);
            assertEquals(address, result);
            assertEquals(address.getId(), result.getId());
            assertEquals(address.getName(), result.getName());
            assertEquals(address.getUser().getId(), result.getUser().getId());
            assertEquals(address.getAddress(), result.getAddress());
            assertEquals(address.getCreatedAt(), result.getCreatedAt());
            assertEquals(address.getUpdatedAt(), result.getUpdatedAt());
        }

    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            doNothing().when(addressRepository).delete(address);
            // When
            Executable closureToTest = () -> addressService.delete(address.getId());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenId_whenDelete_thenThrowNotFoundException() {
            // Given
            Address address = Factory.createAddress();
            address.setId(null);
            // When
            Executable closureToTest = () -> addressService.delete(address.getId());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("address_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for deleteByUserId scenarios")
    class DeleteByUserIdTest {
        @Test
        void givenIdAndUserId_whenDeleteByUserId_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            when(addressRepository.findByIdAndUserId(address.getId(), address.getUser().getId()))
                    .thenReturn(Optional.of(address));
            doNothing().when(addressRepository).delete(address);
            // When
            Executable closureToTest = () -> addressService.deleteByUserId(address.getId(), address.getUser().getId());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenIdAndUserId_whenDeleteByUserId_thenThrowNotFoundException() {
            // Given
            Address address = Factory.createAddress();
            address.setId(null);
            // When
            Executable closureToTest = () -> addressService.deleteByUserId(address.getId(), address.getUser().getId());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("address_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }
}

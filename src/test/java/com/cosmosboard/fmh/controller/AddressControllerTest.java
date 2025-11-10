package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.address.CreateAddressRequest;
import com.cosmosboard.fmh.dto.request.address.UpdateAddressRequest;
import com.cosmosboard.fmh.dto.response.address.AddressResponse;
import com.cosmosboard.fmh.dto.response.address.AddressesPaginationResponse;
import com.cosmosboard.fmh.entity.Address;
import com.cosmosboard.fmh.entity.specification.criteria.AddressCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.AddressService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AddressController")
public class AddressControllerTest {
    @InjectMocks
    private AddressController addressController;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("Test class for address list scenarios")
    public class ListTest {
        @Test
        void givenCityIdAndDistrictIdAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenThrowBadRequestException() {
            //Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST");
            //When
            Executable response = () -> addressController.list(null, null, null,
                    null, null, "null", null);
            //Then
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());
        }
        @Test
        void givenCityIdAndDistrictIdAndQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            Page<Address> addresses = new PageImpl<>(List.of(address));
            doReturn(address.getUser()).when(userService).getUser();
            when(addressService.findAll(any(AddressCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(addresses);

            // When
            AddressesPaginationResponse response = addressController.list(null, null, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(1, response.getPage());
            assertEquals(1, response.getPages());
            assertEquals(1, response.getTotal());
            assertEquals(1, response.getItems().size());
            assertEquals(address.getId(), response.getItems().get(0).getId());
            assertEquals(address.getName(), response.getItems().get(0).getName());
            assertEquals(address.getAddress(), response.getItems().get(0).getAddress());
        }
    }

    @Nested
    @DisplayName("Test class for address create scenarios")
    public class CreateTest {
        @Test
        void givenCreateAddressRequest_whenCreate_thenAssertBody() {
            // Given
            CreateAddressRequest request = Factory.createCreateAddressRequest();
            Address address = Factory.createAddress();
            doReturn(address.getUser()).when(userService).getUser();
            when(addressService.create(address.getUser().getId(), request)).thenReturn(address);
            // When
            ResponseEntity<URI> response = addressController.create(request);
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertNotNull(response.getHeaders());
            assertNotNull(response.getHeaders().getLocation());
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Test class for address show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            doReturn(address.getUser()).when(userService).getUser();
            when(addressService.findOneByIdAndUserId(address.getId(), address.getUser().getId())).thenReturn(address);

            // When
            AddressResponse response = addressController.show(address.getId());

            // Then
            assertNotNull(response);
            assertEquals(address.getId(), response.getId());
            assertEquals(address.getName(), response.getName());
            assertEquals(address.getAddress(), response.getAddress());
        }
    }

    @Nested
    @DisplayName("Test class for address update scenarios")
    public class UpdateTest {

        private final UpdateAddressRequest updateAddressRequest = Factory.createUpdateAddressRequest();
        @Test
        void givenIdAndUpdateAddressRequest_whenCreate_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            doReturn(address.getUser()).when(userService).getUser();
            when(addressService.update(address.getId(), address.getUser().getId(), updateAddressRequest)).thenReturn(address);

            // When
            AddressResponse response = addressController.update(address.getId(), updateAddressRequest);

            // Then
            assertNotNull(response);
            assertEquals(address.getId(), response.getId());
            assertEquals(address.getName(), response.getName());
            assertEquals(address.getAddress(), response.getAddress());
        }
    }

    @Nested
    @DisplayName("Test class for address update scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            Address address = Factory.createAddress();
            doReturn(address.getUser()).when(userService).getUser();
            doNothing().when(addressService).deleteByUserId(address.getId(), address.getUser().getId());
            // When
            ResponseEntity<Void> response = addressController.delete(address.getId());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}

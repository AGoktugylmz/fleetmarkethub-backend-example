package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for RoleService")
class RoleServiceTest {
    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(roleService.count()).thenReturn(1L);
            // When
            Long count = roleService.count();
            // Then
            assertNotNull(count);
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findOneByName scenarios")
    class FindOneByNameTest {
        @Test
        void givenName_whenFindOneByName_thenAssertBody() {
            // Given
            Role role = Factory.createRole();
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
            // When
            Role result = roleService.findOneByName(role.getName());
            // Then
            assertNotNull(result);
            assertEquals(role.getId(), result.getId());
            assertEquals(role.getName(), result.getName());
            assertEquals(role.getCreatedAt(), result.getCreatedAt());
            assertEquals(role.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenName_whenFindOneByName_thenThrowNotFoundException() {
            // Given
            Role role = Factory.createRole();
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());
            // When
            Executable closureToTest = () -> roleService.findOneByName(role.getName());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("role_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenRole_whenCreate_thenAssertBody() {
            // Given
            Role role = Factory.createRole();
            when(roleRepository.save(role)).thenReturn(role);
            // When
            Role result = roleService.create(role);
            // Then
            assertNotNull(result);
            assertEquals(role.getId(), result.getId());
            assertEquals(role.getName(), result.getName());
            assertEquals(role.getCreatedAt(), result.getCreatedAt());
            assertEquals(role.getUpdatedAt(), result.getUpdatedAt());
        }
    }
}
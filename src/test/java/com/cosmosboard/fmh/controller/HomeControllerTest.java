package com.cosmosboard.fmh.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for HomeController")
public class HomeControllerTest {
    @InjectMocks
    private HomeController homeController;

    @Nested
    @DisplayName("Test class for Home onlyUser scenario")
    class OnlyUser {
        @Test
        void onlyUser_shouldReturnOk() {
            // When
            String response = homeController.onlyUser();

            // Then
            assertEquals("USER", response);
        }
    }

    @Nested
    @DisplayName("Test class for Home onlyAdmin scenario")
    class OnlyAdmin {
        @Test
        void onlyAdmin_shouldReturnOk() {
            // When
            String result = homeController.onlyAdmin();

            // Then
            assertEquals("ADMIN", result);
        }
    }

    @Nested
    @DisplayName("Test class for Home onlyConsultant scenario")
    class OnlyConsultant {

        @Test
        void onlyConsultant_shouldReturnOk() {
            // When
            String result = homeController.onlyConsultant();

            // Then
            assertEquals("CONSULTANT with owner", result);
        }
    }
}

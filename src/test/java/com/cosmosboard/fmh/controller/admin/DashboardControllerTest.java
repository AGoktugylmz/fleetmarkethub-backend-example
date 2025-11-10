package com.cosmosboard.fmh.controller.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for DashboardController")
public class DashboardControllerTest {
    @InjectMocks
    private DashboardController dashboardController;

    @Nested
    @DisplayName("Test class for dashboard index")
    public class IndexTest {
        @Test
        void givenNoParams_WhenIndex_ThenReturnOk() {
            // When
            ResponseEntity<String> responseEntity = dashboardController.index();

            // Then
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("Admin dashboard", responseEntity.getBody());
        }
    }
}

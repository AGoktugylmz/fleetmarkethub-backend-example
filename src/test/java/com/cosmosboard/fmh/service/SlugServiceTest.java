package com.cosmosboard.fmh.service;

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
@DisplayName("Unit Tests for SlugService")
class SlugServiceTest {
    @InjectMocks
    private SlugService slugService;

    @Nested
    @DisplayName("Test class for generate scenarios")
    class GenerateTest {
        @Test
        void givenText_whenGenerate_thenAssertBody() {
            // Given
            String text = "Lorem ipsum";
            String slug = "lorem-ipsum";
            // When
            String result = slugService.generate(text);
            // Then
            assertEquals(slug, result);
        }
    }
}


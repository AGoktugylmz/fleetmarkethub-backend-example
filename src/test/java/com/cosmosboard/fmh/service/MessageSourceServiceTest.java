package com.cosmosboard.fmh.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for MessageSourceService")
class MessageSourceServiceTest {
    @InjectMocks
    private MessageSourceService messageSourceService;

    @Mock
    private MessageSource messageSource;

    @Nested
    @DisplayName("Test class for get scenarios")
    class GetTest {
        @Test
        @DisplayName("Test get with code, params, locale")
        void givenCodeAndParamsAndLocale_whenGetWithCodeParamsLocale_thenAssertBody() {
            String code = "test.message.code";
            Object[] params = new Object[] { "param1", "param2" };
            String message = "This is a test message";
            Locale locale = Locale.US;
            when(messageSource.getMessage(code, params, locale)).thenReturn(message);

            String result = messageSourceService.get(code, params, locale);

            assertEquals(message, result);
            verify(messageSource).getMessage(code, params, locale);
            verifyNoMoreInteractions(messageSource);
        }

        @Test
        @DisplayName("Test get with code, params, locale")
        void givenCodeAndParamsAndLocale_whenGetWithCodeParamsLocale_thenNoSuchMessageAssertBody() {
            String code = "test.message.code";
            Object[] params = new Object[] { "param1", "param2" };
            Locale locale = Locale.US;
            when(messageSource.getMessage(code, params, locale))
                    .thenThrow(NoSuchMessageException.class);

            String result = messageSourceService.get(code, params, locale);

            assertEquals(code, result);
            verify(messageSource).getMessage(code, params, locale);
            verifyNoMoreInteractions(messageSource);

        }

        @Test
        @DisplayName("Test get with code, params")
        void givenCodeAndParams_whenGetWithCodeParams_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            when(messageSourceService.get(code, new Object[0])).thenReturn(message);
            // When
            String result = messageSourceService.get(code, new Object[0]);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test get with code, locale")
        void givenCodeAndLocale_whenGetWithCodeLocale_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            Locale locale = LocaleContextHolder.getLocale();
            when(messageSourceService.get(code, locale)).thenReturn(message);
            // When
            String result = messageSourceService.get(code, locale);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test get with code")
        void givenCode_whenGetWithCode_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            when(messageSourceService.get(code)).thenReturn(message);
            // When
            String result = messageSourceService.get(code, new Object[0]);
            // Then
            assertEquals(message, result);
        }
    }
}

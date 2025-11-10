package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.Message;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.MessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for MessageService")
public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Nested
    @DisplayName("Test class for save scenarios")
    class SaveTest {
        @Test
        void givenMessage_whenSave_thenAssertBody() {
            // Given
            Message messageToSave = Factory.createMessage();

            when(messageRepository.save(messageToSave)).thenReturn(messageToSave);

            // When
            Message resultMessage = messageService.save(messageToSave);

            // Then
            assertNotNull(resultMessage);

            assertEquals(messageToSave, resultMessage);
            verify(messageRepository).save(messageToSave);
        }
    }
}

package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.event.UserInvitationSendEvent;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.redis.UserInviteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CityService")
public class UserInviteServiceTest {
    @InjectMocks
    private UserInviteService userInviteService;

    @Mock
    private UserInviteRepository userInviteRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Nested
    @DisplayName("Test class for findById scenarios")
    class FindById {
        @Test
        void givenStringId_whenFindById_thenAssertBody() {
            // Given
            String id = "testId";
            UserInvite expectedUserInvite = Factory.createUserInvite();
            when(userInviteRepository.findById(id)).thenReturn(Optional.of(expectedUserInvite));

            // When
            UserInvite result = userInviteService.findById(id);

            // Then
            assertEquals(expectedUserInvite, result);
        }

        @Test
        void givenStringId_whenFindById_thenNotFoundException() {
            // Given
            String id = "testId";
            when(userInviteRepository.findById(id)).thenReturn(Optional.empty());
            when(messageSourceService.get("userInvite_not_found")).thenReturn("Test message");

            // When/Then
            assertThrows(NotFoundException.class, () -> userInviteService.findById(id));
        }
    }

    @Nested
    @DisplayName("Test class for findAllByUserFrom scenarios")
    class FindAllByUserFrom {
        @Test
        void givenUserFrom_whenFindAllByUserFrom_thenAssertBody() {
            // Given
            String userFrom = "testUserFrom";
            UserInvite invite1 = Factory.createUserInvite();
            UserInvite invite2 = Factory.createUserInvite();
            List<UserInvite> expectedInvites = List.of(invite1, invite2);
            when(userInviteRepository.findAllByUserFrom(userFrom)).thenReturn(expectedInvites);

            // When
            List<UserInvite> result = userInviteService.findAllByUserFrom(userFrom);

            // Then
            assertEquals(expectedInvites, result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Test class for findAllByUserTo scenarios")
    class FindAllByUserTo {
        @Test
        void givenUserTo_whenFindAllByUserTo_thenAssertBody() {
            // Given
            String userTo = "testUserTo";
            UserInvite invite1 = Factory.createUserInvite();
            UserInvite invite2 = Factory.createUserInvite();
            List<UserInvite> expectedInvites = List.of(invite1, invite2);
            when(userInviteRepository.findAllByUserTo(userTo)).thenReturn(expectedInvites);

            // When
            List<UserInvite> result = userInviteService.findAllByUserTo(userTo);

            // Then
            assertEquals(expectedInvites, result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Test class for findOptionalUserFromAndUserTo scenarios")
    class FindOptionalUserFromAndUserTo {
        @Test
        void givenUserFromAndUserTo_whenFindOptionalUserFromAndUserTo_thenAssertBody() {
            // Given
            String userFrom = "testUserFrom";
            String userTo = "testUserTo";
            UserInvite expectedInvite = Factory.createUserInvite();
            when(userInviteRepository.findByUserFromAndUserTo(userFrom, userTo)).thenReturn(Optional.of(expectedInvite));


            // When
            Optional<UserInvite> result = userInviteService.findOptionalUserFromAndUserTo(userFrom, userTo);

            // Then
            assertTrue(result.isPresent());
            assertEquals(expectedInvite, result.get());
        }
    }

    @Nested
    @DisplayName("Test class for findByUserFromAndUserTo scenarios")
    class FindByUserFromAndUserTo {
        @Test
        void givenUserFromAndUserTo_whenFindByUserFromAndUserTo_thenAssertBody() {
            // Given
            String userFrom = "testUserFrom";
            String userTo = "testUserTo";
            UserInvite expectedInvite = Factory.createUserInvite();
            when(userInviteRepository.findByUserFromAndUserTo(userFrom, userTo)).thenReturn(Optional.of(expectedInvite));
            UserInviteService userInviteService = new UserInviteService(userInviteRepository, messageSourceService, eventPublisher);

            // When
            UserInvite result = userInviteService.findByUserFromAndUserTo(userFrom, userTo);

            // Then
            assertEquals(expectedInvite, result);
        }

        @Test
        void givenUserFromAndUserTo_whenFindByUserFromAndUserTo_thenNotFoundException() {
            // Given
            String userFrom = "testUserFrom";
            String userTo = "testUserTo";
            when(userInviteRepository.findByUserFromAndUserTo(userFrom, userTo)).thenReturn(Optional.empty());
            when(messageSourceService.get("userInvite_not_found")).thenReturn("UserInvite not found");
            UserInviteService userInviteService = new UserInviteService(userInviteRepository, messageSourceService, eventPublisher);

            // When
            NotFoundException exception = assertThrows(NotFoundException.class, () -> userInviteService.findByUserFromAndUserTo(userFrom, userTo));

            // Then
            assertEquals("UserInvite not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for save scenarios")
    class Save {
        @Test
        void givenUserInvite_whenSave_thenAssertBody() {
            // Given
            UserInvite userInvite = Factory.createUserInvite();
            when(userInviteRepository.save(userInvite)).thenReturn(userInvite);

            // When
            UserInvite result = userInviteService.save(userInvite);

            // Then
            assertEquals(userInvite, result);
        }
    }

    @Nested
    @DisplayName("Test class for publishInvitationEvent scenarios")
    class PublishInvitationEvent {
        @Test
        void givenUserAndUserInvite_whenPublishInvitationEvent_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            UserInvite userInvite = Factory.createUserInvite();


            // When
            userInviteService.publishInvitationEvent(user, userInvite);

            // Then
            verify(eventPublisher).publishEvent(any(UserInvitationSendEvent.class));
        }

    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class Delete {
        @Test
        void givenUserInvite_whenDelete_thenAssertBody () {
            //Given
            UserInvite userInvite = Factory.createUserInvite();

            //When
            userInviteService.delete(userInvite);

            //Then
            verify(userInviteRepository).delete(userInvite);
        }
    }
}
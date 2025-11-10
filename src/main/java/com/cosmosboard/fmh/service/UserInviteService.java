package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.event.UserInvitationSendEvent;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.repository.redis.UserInviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInviteService {
    private final UserInviteRepository userInviteRepository;

    private final MessageSourceService messageSourceService;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Retrieve a user invite by its unique identifier.
     *
     * @param id String.
     * @return UserInvite.
     */
    public UserInvite findById(String id) {
        return userInviteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("userInvite_not_found")));
    }

    /**
     * Retrieve a list of user invites sent by a specific user.
     *
     * @param userFrom String.
     * @return A list of user invites.
     */
    public List<UserInvite> findAllByUserFrom(String userFrom) {
        return userInviteRepository.findAllByUserFrom(userFrom);
    }

    /**
     * Retrieve a list of user invites received by a specific user.
     *
     * @param userTo String.
     * @return A list of user invites.
     */
    public List<UserInvite> findAllByUserTo(String userTo) {
        return userInviteRepository.findAllByUserTo(userTo);
    }

    /**
     * Find and retrieve an optional user invite based on the sender and recipient users.
     *
     * @param userFrom String.
     * @param userTo   String.
     * @return An optional user invite.
     */
    public Optional<UserInvite> findOptionalUserFromAndUserTo(String userFrom, String userTo) {
        return userInviteRepository.findByUserFromAndUserTo(userFrom, userTo);
    }

    /**
     * Find and retrieve a user invite based on the sender and recipient users.
     *
     * @param userFrom String.
     * @param userTo   The unique identifier of the user who received the invite.
     * @return A user invite based on the sender and recipient users.
     * @throws NotFoundException if no matching user invite is found.
     */
    public UserInvite findByUserFromAndUserTo(String userFrom, String userTo) {
        return findOptionalUserFromAndUserTo(userFrom, userTo)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("userInvite_not_found")));
    }

    /**
     * Save a user invite to the database.
     *
     * @param userInvite UserInvite.
     * @return UserInvite.
     */
    public UserInvite save(UserInvite userInvite) {
        return userInviteRepository.save(userInvite);
    }

    /**
     * Publishes a user invitation event.
     *
     * @param user      User.
     * @param userInvite UserInvite.
     */
    public void publishInvitationEvent(User user, UserInvite userInvite) {
        eventPublisher.publishEvent(new UserInvitationSendEvent(this, user, userInvite));
    }

    /**
     * Deletes a user invitation from the repository.
     *
     * @param userInvite UserInvite.
     */
    public void delete(UserInvite userInvite) {
        userInviteRepository.delete(userInvite);
    }
}
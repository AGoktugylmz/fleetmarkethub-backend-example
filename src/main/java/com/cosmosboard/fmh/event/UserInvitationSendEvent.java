package com.cosmosboard.fmh.event;

import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserInvitationSendEvent extends ApplicationEvent {
    private final User user;

    private final UserInvite userInvite;

    public UserInvitationSendEvent(Object source, User user, UserInvite userInvite) {
        super(source);
        this.user = user;
        this.userInvite = userInvite;
    }
}

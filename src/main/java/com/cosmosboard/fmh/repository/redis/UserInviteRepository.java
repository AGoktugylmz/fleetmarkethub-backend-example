package com.cosmosboard.fmh.repository.redis;

import com.cosmosboard.fmh.entity.UserInvite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserInviteRepository extends CrudRepository<UserInvite, String> {

    List<UserInvite> findAllByUserFrom(String userFrom);

    List<UserInvite> findAllByUserTo(String userTo);

    Optional<UserInvite> findByUserFromAndUserTo(String userFrom, String userTo);

}
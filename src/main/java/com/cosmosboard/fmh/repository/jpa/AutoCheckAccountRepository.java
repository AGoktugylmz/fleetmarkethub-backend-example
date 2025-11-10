package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.AutoCheckAccount;
import com.cosmosboard.fmh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface AutoCheckAccountRepository extends JpaRepository<AutoCheckAccount, String>, JpaSpecificationExecutor<AutoCheckAccount> {
    Optional<AutoCheckAccount> findByUser(User user);

    Optional<AutoCheckAccount> findByUserId(String userId);
}

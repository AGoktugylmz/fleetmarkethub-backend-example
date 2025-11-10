package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String>, JpaSpecificationExecutor<Address> {
    boolean existsByNameAndUserId(String name, String userId);

    Optional<Address> findByIdAndUserId(String id, String userId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Address a WHERE a.name = :name AND " +
        "a.user.id <> :userId")
    boolean existsByNameAndNotUserId(@Param("name") String name, @Param("userId") String userId);
}

package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {

    @Query("SELECT m FROM Message m WHERE m.from.id = :companyId OR m.to.id = :companyId")
    List<Message> findMessagesByCompanyId(@Param("companyId") String companyId);

    @Query("SELECT m FROM Message m WHERE (m.from.id = :companyId1 AND m.to.id = :companyId2) OR (m.from.id = :companyId2 AND m.to.id = :companyId1)")
    List<Message> findMessagesBetweenCompanies(@Param("companyId1") String companyId1, @Param("companyId2") String companyId2);
}
package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.SubscriptionTransaction;
import com.cosmosboard.fmh.util.AppConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SubscriptionTransactionRepository extends JpaRepository<SubscriptionTransaction, String>,
    JpaSpecificationExecutor<SubscriptionTransaction> {
    List<SubscriptionTransaction> findAllBySubscriptionIdAndStatus(String subscriptionId,
                                                                   AppConstants.TransactionStatusEnum status);

    @Transactional
    @Modifying
    @Query("UPDATE SubscriptionTransaction st SET st.status = :newStatus " +
        "WHERE st.subscription.id = :subscriptionId AND st.status = :currentStatus")
    void updateStatusBySubscriptionIdAndStatus(String subscriptionId,
                                               AppConstants.TransactionStatusEnum currentStatus,
                                               AppConstants.TransactionStatusEnum newStatus);
}

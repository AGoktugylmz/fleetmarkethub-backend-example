package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.BlackbookCredit;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.repository.jpa.BlackbookCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlackbookCreditService {
    private final BlackbookCreditRepository blackbookCreditRepository;

    /**
     * Add blackbook credit.
     *
     * @param company  Company
     * @param quantity Long
     * @return BlackbookCredit
     */
    public BlackbookCredit add(Company company, Long quantity) {
        log.info("[BlackbookCreditService] Add blackbook credit");
        return blackbookCreditRepository.save(BlackbookCredit.builder().company(company).quantity(quantity).build());
    }

    /**
     * Decrease blackbook credit.
     *
     * @param company  Company
     */
    public void decreaseCredit(Company company) {
        BlackbookCredit newRecord = new BlackbookCredit();
        newRecord.setCompany(company);
        newRecord.setQuantity(-1L);

        blackbookCreditRepository.save(newRecord);
    }

    /**
     * Get net credits by company id.
     *
     * @param companyId String
     * @return Long
     */
    public Long getNetCreditsByCompanyId(String companyId) {
        log.info("[BlackbookCreditService] Get net credits by company id");
        return blackbookCreditRepository.getNetCreditsByCompanyId(companyId);
    }
}

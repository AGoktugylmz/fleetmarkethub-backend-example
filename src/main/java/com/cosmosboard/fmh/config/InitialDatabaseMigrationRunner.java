package com.cosmosboard.fmh.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialDatabaseMigrationRunner {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void runMigrations() {
        try {
            jdbcTemplate.update("""
                ALTER TABLE companies
                ADD COLUMN IF NOT EXISTS is_dealer BOOLEAN NOT NULL DEFAULT FALSE;
                """);

            // Eğer NULL'lar varsa bir sonraki satır onları da düzeltir — zaten DEFAULT eklediğimiz için genelde gerek yok
            jdbcTemplate.update("""
                UPDATE companies
                SET is_dealer = FALSE
                WHERE is_dealer IS NULL;
                """);

            log.info("✅ companies.is_dealer migration applied successfully.");
        } catch (Exception e) {
            log.error("⚠️ Error while applying companies.is_dealer migration: {}", e.getMessage(), e);
        }
    }
}
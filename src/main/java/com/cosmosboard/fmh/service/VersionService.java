package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.Version;
import com.cosmosboard.fmh.repository.jpa.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository versionRepository;

    /**
     * Gets the latest version record.
     *
     * @return The most recent Version object.
     */
    public Version getCurrentVersion() {
        return versionRepository.findTopByOrderByIdDesc();
    }

    /**
     * Updates the current version to a new version string.
     * If no version exists, create a new one.
     *
     * @param newVersion The new version string to set.
     * @return The saved Version object.
     */
    public Version updateVersion(String newVersion) {
        Version existingVersion = versionRepository.findTopByOrderByIdDesc();

        if (existingVersion != null) {
            existingVersion.setVersion(newVersion);
            return versionRepository.save(existingVersion);
        } else {
            Version newVersionEntity = Version.builder()
                    .version(newVersion)
                    .build();
            return versionRepository.save(newVersionEntity);
        }
    }
}

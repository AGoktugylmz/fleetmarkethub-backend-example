package com.cosmosboard.fmh.service.storage;

import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.service.MessageSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@Slf4j
public class StorageFileService extends BaseStorageService {
    /**
     * File system storage service constructor.
     *
     * @param storagePath          String
     * @param messageSourceService MessageSourceService
     */
    public StorageFileService(@Value("${app.storage-path}") String storagePath, MessageSourceService messageSourceService) {
        super(storagePath, messageSourceService);
    }

    /**
     * Make file from storage.
     *
     * @param filename String
     * @return StorageFileService
     */
    public StorageFileService make(String filename) {
        this.setFile(new File(getStoragePath().toString(), filename));

        if (!this.getFile().exists()) {
            throw new NotFoundException(messageSourceService.get("file_not_found"));
        }

        return this;
    }
}

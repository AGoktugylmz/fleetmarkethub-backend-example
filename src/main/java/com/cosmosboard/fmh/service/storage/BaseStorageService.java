package com.cosmosboard.fmh.service.storage;

import com.cosmosboard.fmh.service.MessageSourceService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import java.io.File;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public abstract class BaseStorageService {
    protected final MessageSourceService messageSourceService;

    private final Path storagePath;

    private File file = null;

    /**
     * Base storage service constructor.
     *
     * @param storagePath          String
     * @param messageSourceService MessageSourceService
     */
    BaseStorageService(@Value("${app.storage-path}") String storagePath,
                              MessageSourceService messageSourceService) {
        this.storagePath = Paths.get(storagePath);
        this.messageSourceService = messageSourceService;
    }

    /**
     * Get media type from file mime type.
     *
     * @return MediaType
     */
    public MediaType getMediaType() {
        return MediaType.parseMediaType(getMimeType());
    }

    /**
     * Get file mime type.
     *
     * @return String
     */
    protected String getMimeType() {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return mimeType;
    }
}

package com.cosmosboard.fmh.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
    void init();

    String store(MultipartFile file);

    String store(MultipartFile file, boolean randomFilename);

    String store(MultipartFile file, String path);

    String store(MultipartFile file, String path, boolean randomFilename);

    String store(MultipartFile file, String path, String name);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String path);

    void deleteAll();
}

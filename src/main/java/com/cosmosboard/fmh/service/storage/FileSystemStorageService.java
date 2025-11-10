package com.cosmosboard.fmh.service.storage;

import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.exception.StorageEmptyFileException;
import com.cosmosboard.fmh.exception.StorageException;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.util.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import static com.cosmosboard.fmh.util.AppConstants.RANDOM_FILENAME_LENGTH;

@Service
@Slf4j
public class FileSystemStorageService extends BaseStorageService implements StorageService {
    /**
     * File system storage service constructor.
     *
     * @param storagePath          String
     * @param messageSourceService MessageSourceService
     */
    public FileSystemStorageService(@Value("${app.storage-path}") String storagePath, MessageSourceService messageSourceService) {
        super(storagePath, messageSourceService);
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(this.getStoragePath());
        } catch (IOException e) {
            throw new StorageException(messageSourceService.get("could_not_initialize_storage"));
        }
    }

    @Override
    public String store(MultipartFile file) {
        return store(file, null);
    }

    @Override
    public String store(MultipartFile file, boolean randomFilename) {
        return store(file, null, new RandomStringGenerator(RANDOM_FILENAME_LENGTH).next());
    }

    @Override
    public String store(MultipartFile file, String path) {
        return store(file, path, null);
    }

    @Override
    public String store(MultipartFile file, String path, boolean randomFilename) {
        return store(file, path, new RandomStringGenerator(RANDOM_FILENAME_LENGTH).next());
    }

    @Override
    public String store(MultipartFile file, String path, String name) {
        try {
            if (file == null || file.isEmpty()) {
                throw new StorageEmptyFileException(messageSourceService.get("failed_to_storage_empty_file"));
            }

            Path basePath = this.getStoragePath();
            if (path != null) {
                basePath = Paths.get(this.getStoragePath().toString(), path);
            }

            Files.createDirectories(basePath);

            String filename = file.getResource().getFilename();
            if (filename != null) {
                if (name != null) {
                    String extension = filename.substring(filename.indexOf('.') + 1).toLowerCase();
                    filename = String.format("%s.%s", name, extension);
                }
            } else {
                filename = file.getOriginalFilename();
            }

            Path destinationFile = Paths.get(basePath.toString(), filename)
                .normalize().toAbsolutePath();

            Path parentPath = destinationFile.getParent();
            if (parentPath == null || !parentPath.equals(basePath.toAbsolutePath())) {
                throw new StorageException(messageSourceService.get("cannot_storage_file_outside_current_directory",
                        new String[]{basePath.toAbsolutePath().toString()}));
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch (IOException e) {
            throw new StorageException(messageSourceService.get("failed_to_store_file"));
        }
    }

    @Override
    public Path load(String filename) {
        return getStoragePath().resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        String errorMessage = messageSourceService.get("could_not_read_file", new String[]{filename});

        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new NotFoundException(errorMessage);
            }
        } catch (MalformedURLException e) {
            throw new NotFoundException(errorMessage);
        }
    }

    @Override
    public void delete(String path) throws StorageException {
        File file = new File(Paths.get(getStoragePath().toAbsolutePath().toString(), path).toUri());
        String errorMessage = messageSourceService.get("failed_to_delete_file", new String[]{file.toString()});

        if (!file.isFile()) {
            throw new StorageException(errorMessage);
        }

        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public void deleteAll() throws StorageException {
        FileSystemUtils.deleteRecursively(this.getStoragePath().toFile());
    }
}

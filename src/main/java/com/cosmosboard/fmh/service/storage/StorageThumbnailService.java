package com.cosmosboard.fmh.service.storage;

import com.cosmosboard.fmh.exception.ExpectationException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.service.MessageSourceService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class StorageThumbnailService extends BaseStorageService {
    private static final String IMAGE_CANNOT_BE_PROCESSED = "image_cannot_be_processed";

    private BufferedImage image = null;

    /**
     * File system storage service constructor.
     *
     * @param storagePath          String
     * @param messageSourceService MessageSourceService
     */
    public StorageThumbnailService(@Value("${app.storage-path}") String storagePath, MessageSourceService messageSourceService) {
        super(storagePath, messageSourceService);
    }

    /**
     * Get picture subtype.
     *
     * @return String (jpg, png, gif)
     */
    public String getSubType() {
        return switch (getMimeType()) {
            case MediaType.IMAGE_PNG_VALUE -> "png".toUpperCase();
            case MediaType.IMAGE_GIF_VALUE -> "gif".toUpperCase();
            default -> "jpg".toUpperCase();
        };
    }

    /**
     * Get picture subtype by lowercase option.
     *
     * @return String (jpg, png, gif)
     */
    public String getSubType(boolean lower) {
        if (lower) {
            return getSubType().toLowerCase();
        }

        return getSubType();
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
     * Picture resizer.
     *
     * @param image   BufferedImage
     * @param width   int
     * @param height  int
     * @param quality double
     * @param crop    boolean
     * @return BufferedImage
     */
    private BufferedImage resizer(BufferedImage image, int width, int height, double quality, boolean crop) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.Builder<BufferedImage> thumbnail = Thumbnails.of(image)
                .size(width, height)
                .outputFormat(getSubType())
                .outputQuality(quality)
                .keepAspectRatio(true);

            if (crop) {
                thumbnail.crop(Positions.CENTER);
            }

            thumbnail.toOutputStream(outputStream);

            byte[] data = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

            return ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("Error reading image:", e);
            throw new ExpectationException(messageSourceService.get(IMAGE_CANNOT_BE_PROCESSED));
        }
    }

    /**
     * Make picture from path.
     *
     * @param path String
     * @return StorageThumbnailService
     */
    public StorageThumbnailService make(String path) {
        this.setFile(new File(Paths.get(getStoragePath().toAbsolutePath().toString(), path).toUri()));

        if (!this.getFile().isFile()) {
            log.error("File not found: {}", this.getFile().getAbsolutePath());
            throw new NotFoundException(messageSourceService.get("image_not_found"));
        }

        try {
            image = ImageIO.read(this.getFile());
            return this;
        } catch (IOException e) {
            log.error("Error reading image:", e);
            throw new ExpectationException(messageSourceService.get(IMAGE_CANNOT_BE_PROCESSED));
        }
    }

    /**
     * Resize picture.
     *
     * @param width  int
     * @param height int
     * @return StorageThumbnailService
     */
    public StorageThumbnailService resize(int width, int height) {
        image = resizer(image, width, height, 1, false);

        return this;
    }

    /**
     * Resize picture with quality.
     *
     * @param width   int
     * @param height  int
     * @param quality double
     * @return StorageThumbnailService
     */
    public StorageThumbnailService resize(int width, int height, double quality) {
        image = resizer(image, width, height, quality, false);

        return this;
    }

    /**
     * Crop picture by crop option.
     *
     * @param width   int
     * @param height  int
     * @param quality double
     * @param crop    boolean
     * @return StorageThumbnailService
     */
    public StorageThumbnailService resize(int width, int height, double quality, boolean crop) {
        image = resizer(image, width, height, quality, crop);

        return this;
    }

    /**
     * Picture to buffered image.
     *
     * @return BufferedImage
     */
    public BufferedImage toImage() {
        return image;
    }

    /**
     * Picture to byte array.
     *
     * @return byte[]
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, getSubType(), outputStream);
        } catch (IOException e) {
            log.error("Error writing image:", e);
            throw new ExpectationException(messageSourceService.get(IMAGE_CANNOT_BE_PROCESSED));
        }

        return outputStream.toByteArray();
    }

    /**
     * Save picture to destination path.
     *
     * @param destination String
     */
    public void save(String destination) {
        Path storagePath = getStoragePath();
        Path path = Paths.get(storagePath.toAbsolutePath().toString(), destination);
        Path parentPath = path.getParent();
        try {
            if (parentPath != null) {
                Files.createDirectories(parentPath);
            } else {
                log.warn("The destination path has no parent directory: {}", path);
            }
            ImageIO.write(image, getSubType(), new File(path.toString().toLowerCase()));
        } catch (IOException e) {
            log.error("Error saving image:", e);
            throw new ExpectationException(messageSourceService.get(IMAGE_CANNOT_BE_PROCESSED));
        }
    }
}

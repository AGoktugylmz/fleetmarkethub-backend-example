package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.security.Permit;
import com.cosmosboard.fmh.service.storage.StorageFileService;
import com.cosmosboard.fmh.service.storage.StorageThumbnailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Paths;
import static com.cosmosboard.fmh.util.AppConstants.PICTURES_PATH;

@RestController
@RequestMapping("/v1/media")
@Tag(name = "Media", description = "Media API")
@Permit
public class MediaController extends BaseController {
    private static final double QUALITY = .8;

    private final StorageThumbnailService storageThumbnailService;

    private final StorageFileService storageFileService;

    private final PathMatcher pathMatcher;

    public MediaController(StorageThumbnailService storageThumbnailService, StorageFileService storageFileService, PathMatcher pathMatcher) {
        this.storageThumbnailService = storageThumbnailService;
        this.storageFileService = storageFileService;
        this.pathMatcher = pathMatcher;
    }

    @GetMapping("/picture/show/**")
    @Parameter(name = "path", in = ParameterIn.PATH, description = "Path to picture", required = true,
        schema = @Schema(type = "string", format = "path"))
    @Operation(summary = "Show Picture Endpoint", tags = {"Media"}, responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<byte[]> picture(final HttpServletRequest request) {
        String path = Paths.get(PICTURES_PATH, extractWildcardPath(request)).toString();
        StorageThumbnailService thumbnailService = storageThumbnailService.make(path);

        return new ResponseEntity<>(thumbnailService.toByteArray(), makeHttpHeaders(thumbnailService.getMediaType()),
            HttpStatus.OK);
    }

    @GetMapping("/picture/{width}/{height}/**")
    @Parameter(name = "width", in = ParameterIn.PATH, description = "Resize width", required = true,
        schema = @Schema(type = "Integer"))
    @Parameter(name = "height", in = ParameterIn.PATH, description = "Resize  height", required = true,
        schema = @Schema(type = "Integer"))
    @Parameter(name = "path", in = ParameterIn.PATH, description = "Path to picture", required = true,
        schema = @Schema(type = "string", format = "path"))
    @Operation(summary = "Thumbnail Endpoint", tags = {"Media"}, responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<byte[]> thumbnail(final HttpServletRequest request,
                                            @PathVariable final Integer width,
                                            @PathVariable final Integer height) {
        String path = Paths.get(PICTURES_PATH, extractWildcardPath(request)).toString();
        StorageThumbnailService thumbnailService = storageThumbnailService.make(path)
            .resize(width, height, QUALITY);

        return new ResponseEntity<>(thumbnailService.toByteArray(), makeHttpHeaders(thumbnailService.getMediaType()),
            HttpStatus.OK);
    }

    @GetMapping("/download/**")
    @Parameter(name = "path", in = ParameterIn.PATH, description = "Path to file", required = true,
        schema = @Schema(type = "string", format = "path"))
    @Operation(summary = "File Download Endpoint", tags = {"Media"}, responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FileSystemResource> download(final HttpServletRequest request) {
        StorageFileService fileService = storageFileService.make(extractWildcardPath(request));
        File file = fileService.getFile();

        HttpHeaders headers = makeHttpHeaders(fileService.getMediaType());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

        return new ResponseEntity<>(new FileSystemResource(file), headers, HttpStatus.OK);
    }

    /**
     * Extract wildcard path from request
     *
     * @param request HttpServletRequest for getting attributes
     * @return String value
     */
    private String extractWildcardPath(HttpServletRequest request) {
        String patternAttribute = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String mappingAttribute = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();

        return pathMatcher.extractPathWithinPattern(patternAttribute, mappingAttribute);
    }

    /**
     * Make HTTP headers
     *
     * @param mediaType MediaType object to set headers
     * @return HttpHeaders
     */
    private HttpHeaders makeHttpHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return headers;
    }
}

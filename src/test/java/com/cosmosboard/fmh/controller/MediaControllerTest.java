package com.cosmosboard.fmh.controller;


import com.cosmosboard.fmh.service.storage.StorageFileService;
import com.cosmosboard.fmh.service.storage.StorageThumbnailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import static com.cosmosboard.fmh.util.AppConstants.PICTURES_PATH;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for MediaController")
public class MediaControllerTest {
    @InjectMocks
    MediaController mediaController;

    @Mock
    StorageThumbnailService storageThumbnailService;
    @Mock
    StorageFileService storageFileService;
    @Mock
    PathMatcher pathMatcher;
    @Mock
    HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setup() {
        Mockito.lenient().doReturn("bir").when(httpServletRequest)
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        Mockito.lenient().doReturn("iki").when(httpServletRequest)
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        Mockito.lenient().doReturn("pattern").when(pathMatcher)
                .extractPathWithinPattern("bir", "iki");
    }

    @Nested
    class PictureTest {
        @Test
        @DisplayName("NullPointerException is expected when not valid test setup")
        public void givenHttpServletRequestMockData_whenPicture_thenThrowNPE() {
            // When
            Executable exc = () -> mediaController.picture(httpServletRequest);
            // Then
            Assertions.assertThrows(NullPointerException.class, exc);
        }

        @Test
        @DisplayName("Happy path")
        public void givenHttpServletRequestMockData_whenPicture_thenAssertResponseEntity() {
            // Given
            Mockito.doReturn(storageThumbnailService).when(storageThumbnailService)
                    .make(Paths.get(PICTURES_PATH, "pattern").toString());
            byte[] bytes = "toByteArray".getBytes(StandardCharsets.UTF_8);
            Mockito.doReturn(bytes).when(storageThumbnailService)
                    .toByteArray();
            Mockito.doReturn(MediaType.IMAGE_GIF).when(storageThumbnailService)
                    .getMediaType();
            // When
            ResponseEntity<byte[]> response = mediaController.picture(httpServletRequest);
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getHeaders());
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(bytes, response.getBody());
            Assertions.assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
        }
    }

    @Nested
    class ThumbnailTest {
        @Test
        @DisplayName("Happy path")
        public void givenHttpServletRequest_whenThumbnail_thenAssertResponseEntity() {
            // Given
            Mockito.doReturn(storageThumbnailService).when(storageThumbnailService)
                    .make(Paths.get(PICTURES_PATH, "pattern").toString());
            Mockito.doReturn(storageThumbnailService).when(storageThumbnailService)
                    .resize(1,1,.8);
            byte[] bytes = "toByteArray".getBytes(StandardCharsets.UTF_8);
            Mockito.doReturn(bytes).when(storageThumbnailService).toByteArray();
            Mockito.doReturn(MediaType.IMAGE_GIF).when(storageThumbnailService).getMediaType();
            // When
            ResponseEntity<byte[]> response = mediaController.thumbnail(httpServletRequest,1,1);
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getHeaders());
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(bytes, response.getBody());
            Assertions.assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
        }
    }

    @Nested
    class DownloadTest {
        @Mock
        File file;

        @Test
        @DisplayName("Happy path")
        public void givenHttpServletRequest_whenDownload_thenAssertResponseEntity() {
            // Given
            Mockito.doReturn(storageFileService).when(storageFileService)
                    .make("pattern");
            Mockito.doReturn(file).when(storageFileService).getFile();
            Mockito.doReturn(1L).when(file).length();
            Mockito.doReturn("Name").when(file).getName();
            // When
            ResponseEntity<FileSystemResource> response = mediaController.download(httpServletRequest);
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getHeaders());
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(file, response.getBody().getFile());
            Assertions.assertNotNull(response.getHeaders().getContentDisposition());
            Assertions.assertEquals("Name", response.getHeaders().getContentDisposition().getFilename());
            Assertions.assertEquals("attachment", response.getHeaders().getContentDisposition().getType());
            Assertions.assertEquals(1, response.getHeaders().getContentLength());
        }
    }
}

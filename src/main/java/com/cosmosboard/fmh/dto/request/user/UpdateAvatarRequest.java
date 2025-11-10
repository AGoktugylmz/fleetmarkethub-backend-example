package com.cosmosboard.fmh.dto.request.user;

import com.cosmosboard.fmh.dto.annotation.FileCheck;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateAvatarRequest {
    @NotNull(message = "{not_blank}")
    @FileCheck(message = "{invalid_file_type}", maxSize = 1024 * 1024 * 2, maxSizeMessage = "{file_size_too_large}")
    private MultipartFile file;
}

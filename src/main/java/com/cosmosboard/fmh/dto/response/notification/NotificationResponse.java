package com.cosmosboard.fmh.dto.response.notification;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse extends BaseResponse {

    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "2024-12-18T10:00:00", description = "Creation timestamp", name = "created_at", type = "LocalDateTime")
    private LocalDateTime createdAt;

    @Schema(example = "2024-12-18T12:00:00", description = "Update timestamp", name = "updated_at", type = "LocalDateTime")
    private LocalDateTime updatedAt;

    @Schema(example = "New message received", description = "Content of the notification", name = "content", type = "String")
    private String content;

    @Schema(example = "12345", description = "Content ID associated with the notification", name = "content_id", type = "String")
    private String contentId;

    @Schema(example = "ACTIVE", description = "Status of the notification", name = "status", type = "String")
    private String status;

    @Schema(example = "INFO", description = "Type of the notification", name = "type", type = "String")
    private String type;

    @Schema(example = "https://example.com/notification/12345", description = "URL associated with the notification", name = "url", type = "String")
    private String url;

    public static NotificationResponse convert(final Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .content(notification.getContent())
                .contentId(notification.getContentId())
                .status(notification.getStatus().name())
                .type(notification.getType().name())
                .url(notification.getUrl())
                .build();
    }
}
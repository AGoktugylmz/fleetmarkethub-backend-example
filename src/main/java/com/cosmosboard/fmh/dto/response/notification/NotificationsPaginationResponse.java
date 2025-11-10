package com.cosmosboard.fmh.dto.response.notification;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class NotificationsPaginationResponse extends PaginationResponse<NotificationResponse> {
    public NotificationsPaginationResponse(Page<?> pageModel, List<NotificationResponse> items) {
        super(pageModel, items);
    }
}

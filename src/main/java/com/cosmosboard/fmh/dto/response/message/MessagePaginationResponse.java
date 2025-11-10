package com.cosmosboard.fmh.dto.response.message;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class MessagePaginationResponse extends PaginationResponse<MessageResponse> {
    public MessagePaginationResponse(Page<?> pageModel, List<MessageResponse> items) {
        super(pageModel, items);
    }
}

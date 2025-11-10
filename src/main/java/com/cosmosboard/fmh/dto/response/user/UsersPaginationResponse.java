package com.cosmosboard.fmh.dto.response.user;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class UsersPaginationResponse extends PaginationResponse<UserResponse> {
    public UsersPaginationResponse(Page<?> pageModel, List<UserResponse> items) {
        super(pageModel, items);
    }
}

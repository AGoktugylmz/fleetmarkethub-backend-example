package com.cosmosboard.fmh.dto.response.message;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageGroupResponse extends BaseResponse {
    private String companyId;

    private String companyName;

    private List<MessageResponse> messages;
}

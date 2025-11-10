package com.cosmosboard.fmh.dto.response.message;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponseForMessages;
import com.cosmosboard.fmh.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse extends BaseResponse {

    private String id;

    private CompanyResponseForMessages companyFrom;

    private CompanyResponseForMessages companyTo;

    private String userId;

    private String title;

    private String type;

    private String content;

    private Long createdAt;

    private Long readAt;

    public static MessageResponse convert(final Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .companyFrom(CompanyResponseForMessages.convert(message.getFrom()))
                .companyTo(CompanyResponseForMessages.convert(message.getTo()))
                .userId(message.getUserId())
                .type(message.getType())
                .title(message.getTitle())
                .content(message.getContent())
                .createdAt(message.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .readAt(message.getReadAt() != null ? message.getReadAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
                .build();
    }
}
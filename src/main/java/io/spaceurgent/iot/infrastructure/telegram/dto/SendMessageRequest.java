package io.spaceurgent.iot.infrastructure.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record SendMessageRequest(
        @JsonProperty("chat_id")
        Long chatId,
        String text,
        @JsonProperty("parse_mode")
        String parseMode
) {

    @Builder
    public SendMessageRequest {
    }
}

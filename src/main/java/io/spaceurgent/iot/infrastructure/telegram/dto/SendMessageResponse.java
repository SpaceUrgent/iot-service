package io.spaceurgent.iot.infrastructure.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendMessageResponse(
        Boolean ok,
        @JsonProperty("error_code")
        int errorCode,
        String description,
        Result result
) {

    public record Result(
            @JsonProperty("message_id")
            String messageId
    ) {}
}

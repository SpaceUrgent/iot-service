package io.spaceurgent.iot.infrastructure.telegram;

import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageRequest;
import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
public class TelegramClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SendMessageResponse sendMessage(SendMessageRequest request) {
        log.debug("Send message request: {}", request);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/sendMessage", request, String.class);
        log.debug("Send message response: {}", responseEntity);
        return objectMapper.convertValue(responseEntity.getBody(), SendMessageResponse.class);
    }
}

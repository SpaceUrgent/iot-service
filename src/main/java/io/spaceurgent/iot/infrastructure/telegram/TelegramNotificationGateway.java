package io.spaceurgent.iot.infrastructure.telegram;

import io.spaceurgent.iot.application.interfaces.NotificationGateway;
import io.spaceurgent.iot.domain.model.Notification;
import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageRequest;
import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationGateway implements NotificationGateway {
    private final TelegramConfigProperties properties;
    private final TelegramClient telegramClient;

    @Override
    public Notification send(Notification notification) {
        SendMessageResponse sendMessageResponse;
        try {
            sendMessageResponse = telegramClient.sendMessage(toSendMessageRequest(notification));
            if (Boolean.TRUE.equals(sendMessageResponse.ok())) {
                SendMessageResponse.Result result = sendMessageResponse.result();
                notification.setStatus(Notification.Status.SENT);
                notification.setTrackingId(result.messageId());
            } else {
                notification.setStatus(Notification.Status.ERROR);
                notification.setErrorDescription("%d %s".formatted(sendMessageResponse.errorCode(), sendMessageResponse.description()));
            }
        } catch (Exception e) {
            log.error("Failed to send notification. Error: {}", e.getMessage(), e);
            notification.setStatus(Notification.Status.ERROR);
            notification.setErrorDescription(e.getMessage());
        }
        return notification;
    }

    private SendMessageRequest toSendMessageRequest(Notification notification) {
        return SendMessageRequest.builder()
                .chatId(properties.getChatId())
                .text(notification.getText())
                .parseMode("HTML")
                .build();
    }
}

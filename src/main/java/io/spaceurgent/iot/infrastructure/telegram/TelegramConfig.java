package io.spaceurgent.iot.infrastructure.telegram;

import io.smallrye.config.ConfigMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConfigMapping(prefix = "telegram")
public interface TelegramConfig {

    @NotBlank
    String baseUrl();

    @NotBlank
    String botToken();

    @NotNull
    Long chatId();
}

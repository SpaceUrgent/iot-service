package io.spaceurgent.iot.infrastructure.telegram;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfigProperties {
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String botToken;
    @NotNull
    private Long chatId;
}

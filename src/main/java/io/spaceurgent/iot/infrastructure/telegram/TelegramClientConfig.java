package io.spaceurgent.iot.infrastructure.telegram;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(TelegramConfigProperties.class)
public class TelegramClientConfig {

    @Bean
    public TelegramClient telegramClient(TelegramConfigProperties properties,
                                         ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory("%s%s".formatted(properties.getBaseUrl(), properties.getBotToken()))
        );
        return new TelegramClient(restTemplate, objectMapper);
    }
}

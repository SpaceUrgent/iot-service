package io.spaceurgent.iot.infrastructure.security;

import io.smallrye.config.ConfigMapping;
import jakarta.validation.constraints.NotBlank;

@ConfigMapping(prefix = "iot.security")
public interface IotSecurityConfig {

    @NotBlank
    String username();

    @NotBlank
    String password();
}

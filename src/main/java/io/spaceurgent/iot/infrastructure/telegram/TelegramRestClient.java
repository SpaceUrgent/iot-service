package io.spaceurgent.iot.infrastructure.telegram;

import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageRequest;
import io.spaceurgent.iot.infrastructure.telegram.dto.SendMessageResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "telegram-api")
public interface TelegramRestClient {

    @POST
    @Path("/sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SendMessageResponse sendMessage(SendMessageRequest request);
}

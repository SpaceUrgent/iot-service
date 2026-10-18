package io.spaceurgent.iot.api;

import io.spaceurgent.iot.application.dto.NewEventDto;
import io.spaceurgent.iot.application.service.EventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/api/v1/events")
@ApplicationScoped
public class EventNotificationController {

    @Inject
    EventService eventService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleEventNotification(NewEventDto notificationDto) {
        eventService.handleEvent(notificationDto);
        return Response.status(Response.Status.CREATED).build();
    }
}

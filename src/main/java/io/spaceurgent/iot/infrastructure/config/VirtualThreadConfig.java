package io.spaceurgent.iot.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class VirtualThreadConfig {

    @Produces
    @ApplicationScoped
    @VirtualThreadExecutor
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    public void disposeExecutor(@Disposes @VirtualThreadExecutor ExecutorService executorService) {
        executorService.close();
    }
}

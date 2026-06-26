package com.example.student_api.logging;

import com.example.student_api.util.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationLifecycleLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Server startup complete: service={}", ApiConstants.SERVICE_NAME);
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationShutdown() {
        log.info("Server shutdown initiated: service={}", ApiConstants.SERVICE_NAME);
    }
}

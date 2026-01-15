package com.airflights.flight.messaging;

import com.airflights.flight.messaging.dto.FlightActionRequestedEvent;
import com.airflights.flight.service.FlightService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlightActionEventsListener {

    private final FlightService flightService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${flight.events.topic:flight.events}")
    public void onFlightActionRequested(String payload) {
        FlightActionRequestedEvent event = fromJson(payload, FlightActionRequestedEvent.class);
        flightService.applyAirportAction(event);
    }

    private <T> T fromJson(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException ex) {
            log.error("Invalid flight action event payload: {}", payload);
            throw new IllegalArgumentException("Invalid flight action event payload", ex);
        }
    }
}

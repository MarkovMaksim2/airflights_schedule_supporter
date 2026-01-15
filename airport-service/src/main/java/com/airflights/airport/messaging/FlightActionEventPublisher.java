package com.airflights.airport.messaging;

import com.airflights.airport.messaging.dto.FlightActionRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightActionEventPublisher {

    private final KafkaTemplate<String, FlightActionRequestedEvent> kafkaTemplate;

    @Value("${flight.events.topic:flight.events}")
    private String flightEventsTopic;

    public void publish(FlightActionRequestedEvent event) {
        kafkaTemplate.send(flightEventsTopic, event.eventId(), event);
    }
}

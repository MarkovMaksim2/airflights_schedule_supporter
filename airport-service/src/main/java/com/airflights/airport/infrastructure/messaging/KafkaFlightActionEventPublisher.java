package com.airflights.airport.infrastructure.messaging;

import com.airflights.airport.application.event.FlightActionRequestedEvent;
import com.airflights.airport.application.port.out.FlightActionEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaFlightActionEventPublisher implements FlightActionEventPublisher {

    private final KafkaTemplate<String, FlightActionRequestedEvent> kafkaTemplate;

    @Value("${flight.events.topic:flight.events}")
    private String flightEventsTopic;

    @Override
    public void publish(FlightActionRequestedEvent event) {
        kafkaTemplate.send(flightEventsTopic, event.eventId(), event);
    }
}

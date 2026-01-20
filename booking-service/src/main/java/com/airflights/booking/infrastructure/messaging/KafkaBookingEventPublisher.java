package com.airflights.booking.infrastructure.messaging;

import com.airflights.booking.application.event.BookingCreatedEvent;
import com.airflights.booking.application.port.out.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaBookingEventPublisher implements BookingEventPublisher {

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    @Value("${booking.events.topic:booking.events}")
    private String bookingEventsTopic;

    @Override
    public void publishBookingCreated(BookingCreatedEvent event) {
        kafkaTemplate.send(bookingEventsTopic, event.eventId(), event);
    }
}

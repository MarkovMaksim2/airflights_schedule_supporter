package com.airflights.booking.domain.port;

import com.airflights.booking.domain.event.BookingCreatedEvent;

public interface BookingEventPublisher {
    void publishBookingCreated(BookingCreatedEvent event);
}

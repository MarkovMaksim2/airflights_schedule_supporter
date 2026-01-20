package com.airflights.booking.application.port.out;

import com.airflights.booking.application.event.BookingCreatedEvent;

public interface BookingEventPublisher {
    void publishBookingCreated(BookingCreatedEvent event);
}

package com.airflights.airport.application.port.out;

import com.airflights.airport.application.event.FlightActionRequestedEvent;

public interface FlightActionEventPublisher {
    void publish(FlightActionRequestedEvent event);
}

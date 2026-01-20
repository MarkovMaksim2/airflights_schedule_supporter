package com.airflights.booking.infrastructure.feign;

import com.airflights.booking.application.exception.RemoteServiceUnavailableException;
import com.airflights.booking.application.exception.ResourceNotFoundException;
import com.airflights.booking.application.port.out.FlightVerifierPort;
import feign.FeignException;
import feign.RetryableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightVerifier implements FlightVerifierPort {
    private final FlightClient flightClient;

    @CircuitBreaker(name = "flightClient", fallbackMethod = "remoteUnavailable")
    @Override
    public void ensureFlightExists(Long flightId) {
        try {
            ResponseEntity<Void> resp = flightClient.flightExists(flightId);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Flight not found: " + flightId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Flight not found: " + flightId);
        }
    }

    private void remoteUnavailable(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("flight-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("flight-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("flight-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("flight-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("flight-service unavailable", ex);
    }
}

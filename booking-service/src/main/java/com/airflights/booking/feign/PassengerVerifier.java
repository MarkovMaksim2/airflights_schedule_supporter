package com.airflights.booking.feign;

import com.airflights.booking.exception.ResourceNotFoundException;
import com.airflights.flight.exception.RemoteServiceUnavailableException;
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
public class PassengerVerifier {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passengerClient", fallbackMethod = "remoteUnavailable")
    public void ensurePassengerExists(Long flightId) {
        try {
            ResponseEntity<Void> resp = passengerClient.passengerExists(flightId);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Passenger not found: " + flightId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Passenger not found: " + flightId);
        }
    }

    private void remoteUnavailable(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }
}

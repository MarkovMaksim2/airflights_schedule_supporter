package com.airflights.booking.feign;

import com.airflights.booking.dto.PassengerSummary;
import com.airflights.booking.exception.ResourceNotFoundException;
import com.airflights.booking.exception.RemoteServiceUnavailableException;
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

    @CircuitBreaker(name = "passengerClient", fallbackMethod = "remoteUnavailableByEmail")
    public PassengerSummary getPassengerByEmail(String email) {
        try {
            ResponseEntity<PassengerSummary> resp = passengerClient.getByEmail(email);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ResourceNotFoundException("Passenger not found: " + email);
            }
            return resp.getBody();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Passenger not found: " + email);
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

    private PassengerSummary remoteUnavailableByEmail(String email, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableByEmail(String email, RetryableException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableByEmail(String email, ConnectException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableByEmail(String email, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableByEmail(String email, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }
}

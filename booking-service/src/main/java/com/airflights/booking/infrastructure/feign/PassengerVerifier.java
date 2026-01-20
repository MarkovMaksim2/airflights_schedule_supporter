package com.airflights.booking.infrastructure.feign;

import com.airflights.booking.application.dto.PassengerSummary;
import com.airflights.booking.application.exception.ResourceNotFoundException;
import com.airflights.booking.application.exception.RemoteServiceUnavailableException;
import com.airflights.booking.application.port.out.PassengerVerifierPort;
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
public class PassengerVerifier implements PassengerVerifierPort {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passengerClient", fallbackMethod = "remoteUnavailable")
    @Override
    public void ensurePassengerExists(Long flightId) {
        try {
            ResponseEntity<PassengerSummary> resp = passengerClient.getById(flightId);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ResourceNotFoundException("Passenger not found: " + flightId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Passenger not found: " + flightId);
        }
    }

    @CircuitBreaker(name = "passengerClient", fallbackMethod = "remoteUnavailableById")
    @Override
    public PassengerSummary getPassengerById(Long id) {
        try {
            ResponseEntity<PassengerSummary> resp = passengerClient.getById(id);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ResourceNotFoundException("Passenger not found: " + id);
            }
            return resp.getBody();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Passenger not found: " + id);
        }
    }

    @CircuitBreaker(name = "passengerClient", fallbackMethod = "remoteUnavailableByEmail")
    @Override
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

    private PassengerSummary remoteUnavailableById(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableById(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableById(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableById(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }

    private PassengerSummary remoteUnavailableById(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("passenger-service unavailable", ex);
    }
}

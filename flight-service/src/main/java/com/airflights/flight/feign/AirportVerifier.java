package com.airflights.flight.feign;

import com.airflights.flight.exception.ResourceNotFoundException;
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
public class AirportVerifier {
    private final AirportClient airportClient;

    @CircuitBreaker(name = "airportClient", fallbackMethod = "remoteUnavailable")
    public void ensureAirportExists(Long airportId) {
        try {
            ResponseEntity<Void> resp = airportClient.airportExists(airportId);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Airport not found: " + airportId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Airport not found: " + airportId);
        }
    }

    private void remoteUnavailable(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }
}

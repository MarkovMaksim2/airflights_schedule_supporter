package com.airflights.flight.infrastructure.feign;

import com.airflights.flight.application.dto.AirlineDto;
import com.airflights.flight.application.exception.ResourceNotFoundException;
import com.airflights.flight.application.exception.RemoteServiceUnavailableException;
import com.airflights.flight.application.port.out.AirlineVerifierPort;
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
public class AirlineVerifier implements AirlineVerifierPort {
    private final AirlineClient airlineClient;

    @CircuitBreaker(name = "airlineClient", fallbackMethod = "remoteUnavailable")
    @Override
    public void ensureAirlineExists(Long airlineId) {
        getAirline(airlineId);
    }

    @CircuitBreaker(name = "airlineClient", fallbackMethod = "remoteUnavailableAirline")
    @Override
    public AirlineDto getAirline(Long airlineId) {
        try {
            ResponseEntity<AirlineDto> resp = airlineClient.getAirline(airlineId);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ResourceNotFoundException("Airline not found: " + airlineId);
            }
            return resp.getBody();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Airline not found: " + airlineId);
        }
    }

    private void remoteUnavailable(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private void remoteUnavailable(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private AirlineDto remoteUnavailableAirline(Long id, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private AirlineDto remoteUnavailableAirline(Long id, RetryableException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private AirlineDto remoteUnavailableAirline(Long id, ConnectException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private AirlineDto remoteUnavailableAirline(Long id, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }

    private AirlineDto remoteUnavailableAirline(Long id, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("airline-service unavailable", ex);
    }
}

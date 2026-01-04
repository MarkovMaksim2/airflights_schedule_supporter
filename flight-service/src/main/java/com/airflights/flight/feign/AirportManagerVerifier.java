package com.airflights.flight.feign;

import com.airflights.flight.dto.AirportManagerDto;
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
public class AirportManagerVerifier {
    private final AirportManagerClient airportManagerClient;

    @CircuitBreaker(name = "airportManagerClient", fallbackMethod = "remoteUnavailable")
    public Long getAirportIdByEmail(String email) {
        try {
            ResponseEntity<AirportManagerDto> resp = airportManagerClient.getByEmail(email);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ResourceNotFoundException("Airport manager not found: " + email);
            }
            AirportManagerDto dto = resp.getBody();
            if (dto.getAirportId() == null) {
                throw new ResourceNotFoundException("Airport manager not found: " + email);
            }
            return dto.getAirportId();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Airport manager not found: " + email);
        }
    }

    private Long remoteUnavailable(String email, CallNotPermittedException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private Long remoteUnavailable(String email, RetryableException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private Long remoteUnavailable(String email, ConnectException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private Long remoteUnavailable(String email, SocketTimeoutException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }

    private Long remoteUnavailable(String email, UnknownHostException ex) {
        throw new RemoteServiceUnavailableException("airport-service unavailable", ex);
    }
}

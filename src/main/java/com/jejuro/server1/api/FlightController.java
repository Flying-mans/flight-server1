package com.jejuro.server1.api;

import com.jejuro.server1.dto.FlightDetailDto;
import com.jejuro.server1.service.flight.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("")
    public FlightDetailDto getDetail(
            String flightCode,
            String depDate
    ) {
        FlightDetailDto flightDetail = flightService.getFlightDetail(flightCode, depDate);

        return flightDetail;
    }
}

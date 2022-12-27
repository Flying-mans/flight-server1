package com.jejuro.server1.service.flight;

import com.jejuro.server1.dto.FlightDetailDto;

public interface FlightService {

    public FlightDetailDto getFlightDetail(String flightCode, String depDate);
}

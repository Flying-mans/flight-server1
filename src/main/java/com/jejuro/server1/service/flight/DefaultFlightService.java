package com.jejuro.server1.service.flight;

import com.jejuro.server1.dto.ChartDto;
import com.jejuro.server1.dto.FlightDetailDto;
import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DefaultFlightService implements FlightService{

    private final FlightRepository flightRepository;

    @Override
    public FlightDetailDto getFlightDetail(String flightCode, String depDate) {

        List<Flight> flightList = flightRepository.findByFlightCodeAndDepDate(flightCode, depDate);

        ArrayList<Integer> fees = new ArrayList<>();
        ArrayList<String> days = new ArrayList<>();

        int sum = 0; // 평균 구하기 위한 값
        for (Flight flight : flightList) {
            int fee = flight.getFee();
            sum += fee;

            fees.add(fee);
            days.add(flight.getCollectedDate());
        }

        int highestFee = Collections.max(fees);
        int lowestFee = Collections.min(fees);
        int size = fees.size();
        int averageFee = sum / size;

        int previousFee = 0;
        int changePercentage = 0;
        if (size >= 2) {
            previousFee = fees.get(size-2);
            changePercentage = (fees.get(size-1) - previousFee) / previousFee * 100;
        }

        ChartDto chartDto = new ChartDto(days, fees);
        FlightDetailDto flightDetailDto = new FlightDetailDto(lowestFee, highestFee, averageFee, previousFee, changePercentage, chartDto);

        return flightDetailDto;
    }
}

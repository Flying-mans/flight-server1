package com.jejuro.server1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FlightDetailDto {

    private int lowestFee;
    private int highestFee;
    private int averageFee;
    private int previousFee;
    private int changePercentage;

    private ChartDto chartDto;
}

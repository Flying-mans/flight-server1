package com.jejuro.server1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChartDto {

    private List<String> days;
    private List<Integer> fees;

    public ChartDto(List<String> days, List<Integer> fees) {
        this.days = new ArrayList<>(days);
        this.fees = new ArrayList<>(fees);
    }
}

package com.jejuro.server1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Flight {

    @Id @GeneratedValue
    @Column(name = "flight_id")
    private Long id;

    private String code;

    private String departure;

    private String arrival;

    private String depDate;

    private String depTime;

    private String arrTime;

    private int fee;

    private String collectedDate;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    public Flight(
            String code,
            String departure,
            String arrival,
            String depDate,
            String depTime,
            String arrTime,
            String collectedDate,
            int fee,
            Airline airline) {
        this.code = code;
        this.departure = departure;
        this.arrival = arrival;
        this.depDate = depDate;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.collectedDate = collectedDate;
        this.fee = fee;
        this.airline = airline;
    }
}
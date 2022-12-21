package com.jejuro.server1.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Flight {

    @Id @GeneratedValue
    @Column(name = "FLIGHT_ID")
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
    @JoinColumn(name = "AIRLINE_ID")
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

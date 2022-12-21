package com.jejuro.server1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Airline {

    @Id
    @GeneratedValue
    @Column(name = "AIRLINE_ID")
    private Long id;

    private String code;

    private String name;

    private String engName;

    private String url;
}

package com.jejuro.server1.repository;

import com.jejuro.server1.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Airline findByCode(String code);
}
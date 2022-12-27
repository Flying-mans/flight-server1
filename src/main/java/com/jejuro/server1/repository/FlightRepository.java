package com.jejuro.server1.repository;

import com.jejuro.server1.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("select f from Flight as f where f.code = :flightCode and f.depDate = :depDate order by f.collectedDate desc")
    List<Flight> findByFlightCodeAndDepDate(@Param("flightCode") String flightCode, @Param("depDate") String depDate);
}

package com.jejuro.server1.repository;

import com.jejuro.server1.entity.Flight;
import com.jejuro.server1.entity.FlightList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface FlightListRepository extends JpaRepository<FlightList, Long> {

    @Query("select f from FlightList as f where f.code = :flightCode and f.depDate = :depDate")
    FlightList findByFlightCodeAndDepDate(@Param("flightCode") String flightCode, @Param("depDate") String depDate);

    @Modifying
    @Query("update FlightList f set f.fee = :fee where f.id = :id")
    void updateFee(@Param("fee") int fee ,@Param("id") Long id);
}

package com.jejuro.server1.repository;

import com.jejuro.server1.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByStatus(int status);

//    @Query("update alarm set alarm.status = 0 where a.id = :id")
//    void setStatusDone(@Param("id") Long id);
}

package com.jejuro.server1.repository;

import com.jejuro.server1.entity.Alarm;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByStatus(int status);

    Optional<Alarm> findById(Long id);

    @Modifying
    @Query("update Alarm a set a.status = 0 where a.id = :id")
    void setStatusDone(@Param("id") Long id);
}

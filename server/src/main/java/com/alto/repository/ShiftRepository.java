package com.alto.repository;

import com.alto.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;


public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Shift findByUsername(String username);
    Shift findByOrderid(String orderid);
    @Query("from Shift s where s.shiftStartTime >= :start and s.shiftStartTime <= :end")
    List<Shift> findByDates(Timestamp start, Timestamp end);
    List<Shift> findByTempid(String Tempid);
    @Query("from Shift s where s.shiftStartTimeActual >= :start and s.tempid = :Tempid")
    List<Shift> findByTempidAndDates(String Tempid, Timestamp start);
}


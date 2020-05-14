package com.alto.repository;

import com.alto.model.ShiftBoardRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ShiftBoardRepository extends JpaRepository<ShiftBoardRecord, Long> {
    ShiftBoardRecord findFirstByOrderidAndTempid(String orderid, String tempid);
    List<ShiftBoardRecord> findByActiveTrue();
    List<ShiftBoardRecord> findAllByOrderid(String orderid);
}


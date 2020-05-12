package com.alto.repository;

import com.alto.model.Shift;
import com.alto.model.ShiftBoardRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface ShiftBoardRepository extends JpaRepository<ShiftBoardRecord, Long> {
    ShiftBoardRecord findFirstByByOrderidAndTempid(String orderid, String tempid);
    List<ShiftBoardRecord> findByActiveTrue();
    List<ShiftBoardRecord> findAllByOrderid(String orderid);
}


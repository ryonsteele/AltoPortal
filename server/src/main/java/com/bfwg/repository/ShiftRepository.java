package com.bfwg.repository;

import com.bfwg.model.Shift;
import com.bfwg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Shift findByUsername(String username);
    Shift findByOrderId(String orderId);
}


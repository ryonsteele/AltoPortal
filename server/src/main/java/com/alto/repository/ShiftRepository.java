package com.alto.repository;

import com.alto.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Shift findByUsername(String username);
    Shift findByOrderid(String orderid);
}


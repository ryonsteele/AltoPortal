package com.bfwg.service;

import com.bfwg.model.*;

import java.util.List;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface ShiftService {

  Shift findById(Long id);
  Shift addShift(ShiftRequest request);
  Shift updateShift(ShiftRequest request);

}

package com.alto.service;

import com.alto.model.Shift;
import com.alto.model.ShiftRequest;
import com.alto.model.*;


public interface ShiftService {

  Shift findById(Long id);
  Shift addShift(ShiftRequest request);
  Shift updateShift(ShiftRequest request);
  Shift getShift(String orderid);

}

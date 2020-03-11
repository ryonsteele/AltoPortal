package com.alto.service;

import com.alto.model.Shift;
import com.alto.model.ShiftRequest;
import com.alto.model.*;

import java.util.List;


public interface ShiftService {

  Shift findById(Long id);
  Shift addShift(ShiftRequest request);
  Shift updateShift(ShiftRequest request);
  List<Sessions> sessionsData(SessionsRequest request);
  Shift getShift(String orderid);
  void sendPushNotification(PushMessageRequest message);

}

package com.alto.service;

import com.alto.model.Shift;
import com.alto.model.requests.ClockRequest;
import com.alto.model.requests.ShiftRequest;
import com.alto.model.*;
import com.alto.model.requests.PushMessageRequest;
import com.alto.model.requests.SessionsRequest;
import com.alto.model.response.ClientAddressResponse;
import com.alto.model.response.ShiftResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ShiftService {

  Shift findById(Long id);
  ResponseEntity manualShiftClock(ClockRequest request);
  ResponseEntity addShift(ShiftRequest request);
  ResponseEntity updateShift(ShiftRequest request);
  List<Sessions> sessionsData(SessionsRequest request);
  Shift getShift(String orderid);
  List<ShiftResponse>getScheduled(String tempid);
  Historicals getHistoricals(String tempid);
  List<ShiftResponse>getOpens(String tempid);
  ClientAddressResponse getClient(String clientid);
  void sendPushNotification(PushMessageRequest message);

}

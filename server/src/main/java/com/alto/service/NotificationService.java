package com.alto.service;

import com.alto.model.PushMessageRequest;
import com.alto.model.SentHomeRequest;
import com.alto.model.Shift;
import com.alto.model.ShiftRequest;


public interface NotificationService {

  boolean sendSentHomeEmail(SentHomeRequest request);

}

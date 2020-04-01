package com.alto.service;

import com.alto.model.requests.ApplyRequest;
import com.alto.model.requests.SentHomeRequest;


public interface NotificationService {

  boolean sendSentHomeEmail(SentHomeRequest request);
  boolean sendApplicationEmail(ApplyRequest request);

}

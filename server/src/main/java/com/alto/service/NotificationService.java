package com.alto.service;

import com.alto.model.requests.SentHomeRequest;


public interface NotificationService {

  boolean sendSentHomeEmail(SentHomeRequest request);

}

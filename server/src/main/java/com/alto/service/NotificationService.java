package com.alto.service;

import com.alto.model.requests.ApplyRequest;
import com.alto.model.requests.SentHomeRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


public interface NotificationService {

  boolean sendSentHomeEmail(SentHomeRequest request);
  boolean sendApplicationEmail(ApplyRequest request);
  boolean saveApplication(ApplyRequest request);
  boolean uploadResume(MultipartFile file, String filekey);

}

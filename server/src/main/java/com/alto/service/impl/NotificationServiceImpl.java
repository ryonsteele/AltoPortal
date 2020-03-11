package com.alto.service.impl;


import com.alto.model.*;
import com.alto.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;


@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  JavaMailSender mailSender;


  @Override
  public boolean sendSentHomeEmail(SentHomeRequest request) {

    MimeMessage mimeMessage = mailSender.createMimeMessage();

    try {

      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

      mimeMessageHelper.setSubject("Sent Home Alert");
      mimeMessageHelper.setFrom("alert@altostaffing.com");
      mimeMessageHelper.setTo("ryonsteele@gmail.com");
      mimeMessageHelper.setText("User: " + request.getUsername() + " TempID: " + request.getTempId() +
              " is reporting being sent home from Client: " + request.getClientName());

      mailSender.send(mimeMessageHelper.getMimeMessage());

    } catch (MessagingException e) {
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

}

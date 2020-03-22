package com.alto.service.impl;


import com.alto.model.requests.SentHomeRequest;
import com.alto.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  JavaMailSender mailSender;


  @Override
  public boolean sendSentHomeEmail(SentHomeRequest request) {

    MimeMessage mimeMessage = mailSender.createMimeMessage();


    Properties props = new Properties();
    props.put("mail.smtp.host", "192.168.1.10");
    props.put("mail.smtp.socketFactory.port", "25");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "25");
    Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("webapps@altostaffing.com","W3b@pps");
              }
            });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("test@gmail.com"));
      message.setRecipients(Message.RecipientType.TO,
              InternetAddress.parse("ryonsteele@gmail.com"));
      message.setSubject("Testing Subject");
      message.setText("Test Mail");

      Transport.send(message);

      System.out.println("Done");

    } catch (Exception e) {
      //throw new RuntimeException(e);
      e.printStackTrace();
      return false;
    }

//    try {
//
//      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//
//      mimeMessageHelper.setSubject("Sent Home Alert");
//      mimeMessageHelper.setFrom("webapps@altostaffing.com");
//      mimeMessageHelper.setTo("ryonsteele@gmail.com");
//      mimeMessageHelper.setText("User: " + request.getUsername() + " TempID: " + request.getTempId() +
//              " is reporting being sent home from Client: " + request.getClientName());
//
//      mailSender.send(mimeMessageHelper.getMimeMessage());
//
//    } catch (MessagingException e) {
//      e.printStackTrace();
//      return false;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return false;
//    }
    return true;
  }

}

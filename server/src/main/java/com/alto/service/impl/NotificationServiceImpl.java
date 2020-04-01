package com.alto.service.impl;


import com.alto.model.requests.ApplyRequest;
import com.alto.model.requests.SentHomeRequest;
import com.alto.service.NotificationService;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class NotificationServiceImpl implements NotificationService {



  @Override
  public boolean sendSentHomeEmail(SentHomeRequest request) {

    Properties props = new Properties();
    props.put("mail.smtp.host", "192.168.1.10");
    props.put("mail.smtp.socketFactory.port", "25");
    props.put("mail.smtp.auth", "false");
    props.put("mail.smtp.port", "25");
    props.put("mail.debug", "true");
    Session session = Session.getDefaultInstance(props);

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("alerts@altostaffing.com"));
      message.setRecipients(Message.RecipientType.TO,
              InternetAddress.parse("aharris@altostaffing.com"));
      message.setSubject("Sent Home Alert");
      message.setText("User: " + request.getUsername() + " TempID: " + request.getTempId() +
              " is reporting being sent home from Client: " + request.getClientName());

      Transport.send(message);

      System.out.println("Done");

    } catch (Exception e) {
      //throw new RuntimeException(e);
      e.printStackTrace();
      return false;
    }
    return true;
  }


    @Override
    public boolean sendApplicationEmail(ApplyRequest request) {

      Properties props = new Properties();
      props.put("mail.smtp.host", "192.168.1.10");
      props.put("mail.smtp.socketFactory.port", "25");
      props.put("mail.smtp.auth", "false");
      props.put("mail.smtp.port", "25");
      props.put("mail.debug", "true");
      Session session = Session.getDefaultInstance(props);
      StringBuilder builder = new StringBuilder();

      try {

        builder.append("\nFirst Name: ");
        builder.append(request.getFirstname());
        builder.append("\nLast Name: ");
        builder.append(request.getLastname());
        builder.append(" \nEmail: ");
        builder.append(request.getEmail());
        builder.append(" \nAddress: ");
        builder.append(request.getStreet());
        builder.append(" ");
        builder.append(request.getCity());
        builder.append(" ");
        builder.append(request.getState());
        builder.append(" ");
        builder.append(request.getZip());
        builder.append(" \nPrimary Phone: ");
        builder.append(request.getPrimary());
        builder.append(" \nSecondary Phone: ");
        builder.append(request.getSecondary());
        builder.append(" \nSpecializations: ");
        for(String s : request.getSpecs()){
          builder.append("\n " + s + " ");
        }
        builder.append(" \nCertifications: ");
        for(String c : request.getCerts()){
          builder.append("\n " + c + " ");
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("alerts@altostaffing.com"));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("aharris@altostaffing.com"));
        message.setSubject("Mobile App Application Received");
        message.setText(builder.toString());

        Transport.send(message);

        System.out.println("Done");

      } catch (Exception e) {
        //throw new RuntimeException(e);
        e.printStackTrace();
        return false;
      }

    return true;
  }

}

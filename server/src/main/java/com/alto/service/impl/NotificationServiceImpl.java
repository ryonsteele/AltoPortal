package com.alto.service.impl;


import com.alto.model.requests.ApplyRequest;
import com.alto.model.requests.SentHomeRequest;
import com.alto.repository.CandidateRepository;
import com.alto.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class NotificationServiceImpl implements NotificationService {

  public static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Autowired
  CandidateRepository candidateRepository;

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
              InternetAddress.parse("scheduling@altostaffing.com"));
      message.setSubject("Sent Home Alert");
      message.setText("User: " + request.getUsername() + " TempID: " + request.getTempId() +
              " is reporting being sent home from Client: " + request.getClientName());

      Transport.send(message);

      logger.info("Sent Home Email sent");

    } catch (Exception e) {
      logger.error("Error sending sent home email", e);
      return false;
    }
    return true;
  }


  public boolean saveApplication(ApplyRequest request){

    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    sb.append(" <soap:Envelope");
    sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    sb.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
    sb.append(" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
    sb.append(" <soap:Body>");
    sb.append(" <tssRequest>");
    sb.append(" <requestXmlString xsi:type=\"xsd:string\">");
    sb.append(" <![CDATA[ ");
    sb.append(" <clearviewRequest>");
    sb.append(" <username>lesliekahn</username>");
    sb.append(" <password>January2003!</password>");
    sb.append(" <action>insertTempCandidate</action>");
    sb.append(" <resultType>xml</resultType>");
    sb.append(" <tempRecords>");
    sb.append(" <tempRecord>");
    sb.append(" <firstName>").append(request.getFirstname()).append("</firstName>");
    sb.append(" <lastName>").append(request.getLastname()).append("</lastName>");
    sb.append(" <homeRegion>27</homeRegion>");
    sb.append(" <status>Pending</status>");
    
    if(request.getCerts() != null && !request.getCerts().isEmpty()) {
      sb.append(" <certification>").append(request.getCerts().get(0)).append("</certification>");
    }
    String allSpecs = "See Description";
//    if(request.getCerts() != null && !request.getCerts().isEmpty()) {
//      for (String spec : request.getCerts()) {
//        allSpecs += spec + ",";
//      }
//      allSpecs = allSpecs.trim().substring(0, allSpecs.length() - 1);
//    }
    sb.append(" <specialty>").append(allSpecs).append("</specialty>");
    sb.append(" <email>").append(request.getEmail()).append("</email>");
    sb.append(" <address>").append(request.getStreet()).append("</address>");
    sb.append(" <city>").append(request.getCity()).append("</city>");
    sb.append(" <state>").append(request.getState()).append("</state>");
    sb.append(" <zip>").append(request.getZip()).append("</zip>");
    sb.append(" <phoneNumber>").append(request.getPrimary()).append("</phoneNumber>");
    sb.append(" <cell_phone>").append(request.getSecondary()).append("</cell_phone>");
    sb.append(" <sendWelcome>0</sendWelcome>");
    sb.append(" </tempRecord>").append(" </tempRecords>");
    sb.append(" </clearviewRequest>").append("]]>");
    sb.append(" </requestXmlString>").append(" </tssRequest>");
    sb.append(" </soap:Body>").append(" </soap:Envelope>");

    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);
      headers.add("SOAPAction","http://ctms.contingenttalentmanagement.com/cirrusconcept/clearConnect/2_0/webService.cfc?wsdl");
      HttpEntity<String> requestRSS = new HttpEntity<String>(sb.toString(), headers);
      ResponseEntity<String> response = restTemplate.postForEntity("https://ctms.contingenttalentmanagement.com/cirrusconcept/clearConnect/2_0/webService.cfc", requestRSS, String.class);



      if(response.getStatusCode().is2xxSuccessful()){
        logger.info("Application saved to RSS");
        return true;
      }

    } catch (Exception e) {
      logger.error("Error saving application to company", e);
      return false;
    }
    return false;
  }

  @Override
  public boolean uploadResume(MultipartFile file, String filekey){

//    Candidate can = candidateRepository.findCandidateByFilekey(filekey);
//    if(can == null) return false;
//
//    StringBuilder sb = new StringBuilder();
//    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
//    sb.append(" <soap:Envelope");
//    sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
//    sb.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
//    sb.append(" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
//    sb.append(" <soap:Body>");
//    sb.append(" <tssRequest>");
//    sb.append(" <requestXmlString xsi:type=\"xsd:string\">");
//    sb.append(" <![CDATA[ ");
//    sb.append(" <clearviewRequest>");
//    sb.append(" <username>rsteele</username>");
//    sb.append(" <password>altoApp1!</password>");
//    sb.append(" <action>insertTempCandidate</action>");
//    sb.append(" <resultType>xml</resultType>");
//    sb.append(" <tempRecords>");
//    sb.append(" <tempRecord>");
//    sb.append(" <firstName>").append(can.getFirstname()).append("</firstName>");
//    sb.append(" <lastName>").append(can.getLastname()).append("</lastName>");
//    sb.append(" <homeRegion>27</homeRegion>");
//    sb.append(" <status>Pending</status>");
//    sb.append(" <certification>").append(can.getCerts()).append("</certification>");
//    sb.append(" <specialty>").append(can.getSpecs()).append("</specialty>");
//    sb.append(" <email>").append(can.getEmail()).append("</email>");
//    sb.append(" <address>").append(can.getStreet()).append("</address>");
//    sb.append(" <city>").append(can.getCity()).append("</city>");
//    sb.append(" <state>").append(can.getState()).append("</state>");
//    sb.append(" <zip>").append(can.getZip()).append("</zip>");
//    sb.append(" <phoneNumber>").append(can.getPrimary()).append("</phoneNumber>");
//    sb.append(" <cell_phone>").append(can.getSecondary()).append("</cell_phone>");
//    sb.append(" <sendWelcome>0</sendWelcome>");
//    sb.append(" </tempRecord>").append(" </tempRecords>");
//    sb.append(" </clearviewRequest>").append("]]>");
//    sb.append(" </requestXmlString>").append(" </tssRequest>");
//    sb.append(" </soap:Body>").append(" </soap:Envelope>");
//
//    RestTemplate restTemplate = new RestTemplateBuilder().build();
//
//    try {
//      HttpHeaders headers = new HttpHeaders();
//      headers.setContentType(MediaType.APPLICATION_XML);
//      HttpEntity<String> request = new HttpEntity<String>(sb.toString(), headers);
//      ResponseEntity<String> response = restTemplate.postForEntity("https://ctms.contingenttalentmanagement.com/cirrusconcept/clearConnect/2_0/webService.cfc", request, String.class);
//
//      System.out.println(response.toString());
//
//      if(response.getStatusCode().is2xxSuccessful()){
//        return true;
//      }
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      return false;
//      //todo logger
//      //LOGGER.error("Error getting Embed URL and Token", e);
//    }

    return false;

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
                InternetAddress.parse("scheduling@altostaffing.com"));
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

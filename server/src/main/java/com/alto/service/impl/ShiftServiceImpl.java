package com.alto.service.impl;


import com.alto.model.*;
import com.alto.repository.AppUserRepository;
import com.alto.repository.ShiftRepository;
import com.alto.service.ShiftService;
import com.google.gson.Gson;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;



@Service
public class ShiftServiceImpl implements ShiftService {


  @Autowired
  Environment env;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ShiftRepository shiftRepository;
  @Autowired
  private AppUserRepository appUserRepository;

  final static int BREAK_START = 1;
  final static int BREAK_END = 2;
  final static int SHIFT_END = 3;

  @Override
  public Shift findById(Long id) {
    return null;
  }

  public Shift addShift(ShiftRequest request){
    ShiftResponse started = null;
    Shift saveShift = new Shift();

    //todo externalize
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=lesliekahn&password=Jan242003!&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",request.getTempId().toString());
    getShiftUrl = getShiftUrl.replace("$orderId",request.getOrderId());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();

      try {

        String result = restTemplate.getForObject(getShiftUrl, String.class);
        result = result.replace("[","").replace("]","");

        System.out.println(result);

        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        started = gson.fromJson(result, ShiftResponse.class);

        //HttpEntity<ShiftResponse> apiResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, ShiftResponse.class);
       // ShiftResponse apiResponse = restTemplate.getForObject(getShiftUrl, ShiftResponse.class);
        //started = apiResponse;

      } catch (Exception e) {
        //LOGGER.error("Error getting Embed URL and Token", e);
      }

      //save to repo
      saveShift.setOrderid(request.getOrderId());
      saveShift.setTempid(request.getTempId().toString());
      saveShift.setUsername(request.getUsername());
      saveShift.setShiftStartTime(convertFromString(started.getShiftStartTime()));
      saveShift.setShiftEndTime(convertFromString(started.getShiftEndTime()));
      saveShift.setShiftStartTimeActual(new Timestamp(System.currentTimeMillis()));
      saveShift.setStatus(started.getStatus());
      saveShift.setClientId(started.getClientId());
      saveShift.setClientName(started.getClientName());
      saveShift.setShiftStartSignoff(request.getShiftSignoff());
      saveShift.setFloor(started.getFloor());
      saveShift.setShiftNumber(started.getShiftNumber());
      saveShift.setOrderCertification(started.getOrderCertification());
      saveShift.setOrderSpecialty(started.getOrderSpecialty());
      saveShift.setNote(started.getNote());
      saveShift.setClockInAddress(request.getClockedAddy());
      saveShift.setCheckinLat(request.getLat());
      saveShift.setCheckinLon(request.getLon());


      shiftRepository.saveAndFlush(saveShift);


    } catch(Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error getting Embed URL and Token", e);
    }

    return saveShift;
  }

  public Shift updateShift(ShiftRequest request){
    ShiftResponse started = null;
    Shift updateShift = null;


    try {

      updateShift = shiftRepository.findByOrderid(request.getOrderId());
      if(updateShift == null){
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
      }


      //save to repo
      if(request.getShiftstatuskey() == BREAK_START){
        updateShift.setBreakStartTime(new Timestamp(System.currentTimeMillis()));
      }else if(request.getShiftstatuskey() == BREAK_END){
        updateShift.setBreakEndTime(new Timestamp(System.currentTimeMillis()));
      }else if(request.getShiftstatuskey() == SHIFT_END){
        updateShift.setShiftEndTimeActual(new Timestamp(System.currentTimeMillis()));
        updateShift.setShiftEndSignoff(request.getShiftSignoff());
        updateShift.setClockoutAddress(request.getClockedAddy());
        updateShift.setCheckoutLat(request.getLat());
        updateShift.setCheckoutLon(request.getLon());
      }
      shiftRepository.saveAndFlush(updateShift);


    } catch(Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error getting Embed URL and Token", e);
    }

    return updateShift;
  }

  private Timestamp convertFromString(String input){
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      Date parsedDate = dateFormat.parse(input);
      return new java.sql.Timestamp(parsedDate.getTime());
    } catch(Exception e) { //this generic but you can control another types of exception
      // look the origin of excption
      e.printStackTrace();
    }
    return null;
  }

  public Shift getShift(String orderid){
    return shiftRepository.findByOrderid(orderid);
  }

  public void sendPushNotification(PushMessageRequest message){

    for(String tempid : message.getTemps()){
      AppUser user = appUserRepository.findByTempid(tempid);

      if(user == null) continue;

      if(user.getDevicetype().equalsIgnoreCase("Android") && user.getDevicetoken() != null && user.getDevicetoken().length() > 10){

        sendFMSNotigication(user.getDevicetoken(), message.getMsgBody());
      }else if(user.getDevicetype().equalsIgnoreCase("iOS") && user.getDevicetoken() != null && user.getDevicetoken().length() > 10){

        sendAPNSNotification(user.getDevicetoken(), message.getMsgBody());
      }
    }

//    List<AppUser> allUsers = appUserRepository.findAll();
//
//    for(AppUser u : allUsers){
//      if(u.getDevicetype().equalsIgnoreCase("Android") && u.getDevicetoken().length() > 10){
//
//        sendFMSNotigication(u.getDevicetoken(), message);
//      }else if(u.getDevicetype().equalsIgnoreCase("iOS") && u.getDevicetoken().length() > 10){
//
//        sendAPNSNotification(u.getDevicetoken(), message);
//      }
//    }
  }

  private void sendFMSNotigication(String deviceToken, String messg) {

    try {
      String androidFcmKey = "AAAAiJJmHX4:APA91bGFT2PxR2V8tJZr0JN7PSKVXmCR9BRnhCAR5-bpWGbcAnDdgNla16CUvJvWiGDY8n57YLnOLTcsDVwGC9nYXkH3VGoUm3_vfPqxXENzOgi3JRQRjP_RfbP-_84QCKjwoUO5Lv_l";
      String androidFcmUrl = "https://fcm.googleapis.com/fcm/send";

      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set("Authorization", "key=" + androidFcmKey);
      httpHeaders.set("Content-Type", "application/json");
      JSONObject msg = new JSONObject();
      JSONObject json = new JSONObject();

      msg.put("title", "Urgent Shift Opened!");
      msg.put("body", messg);
      //msg.put("notificationType", "Test");

      json.put("notification", msg);
      json.put("to", deviceToken);


      HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(), httpHeaders);
      String response = restTemplate.postForObject(androidFcmUrl, httpEntity, String.class);
      System.out.println(response);
    } catch (Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error:", e);
    }
  }

  private void sendAPNSNotification(String deviceToken, String messg){

    try {
      InputStream inputStream = new ClassPathResource("push.p12").getInputStream();
      ApnsService service;
      if (Arrays.asList(env.getActiveProfiles()).contains("pro")) {
        service = APNS.newService().withCert(inputStream, "Password@123")
                .withProductionDestination().build();
      } else {
        service = APNS.newService().withCert(inputStream, "Password@123")
                .withSandboxDestination().build();
      }
      String payload = APNS.newPayload().customField("customData",messg)
              .alertBody("Message").build();
      service.push(deviceToken, payload);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

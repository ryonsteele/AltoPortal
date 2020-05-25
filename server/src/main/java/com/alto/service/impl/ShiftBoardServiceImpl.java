package com.alto.service.impl;


import com.alto.model.*;
import com.alto.model.requests.ConfirmationRequest;
import com.alto.model.requests.InterestRequest;
import com.alto.model.requests.PushMessageRequest;
import com.alto.model.requests.SessionUpdateRequest;
import com.alto.model.response.ShiftResponse;
import com.alto.model.response.TempResponse;
import com.alto.repository.ShiftBoardRepository;
import com.alto.repository.ShiftRepository;
import com.alto.service.ShiftBoardService;
import com.alto.service.ShiftService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ShiftBoardServiceImpl implements ShiftBoardService {

  public static final Logger logger = LoggerFactory.getLogger(ShiftBoardServiceImpl.class);


  @Autowired
  private ShiftService shiftService;

  @Autowired
  private ShiftBoardRepository shiftBoardRepository;

  @Autowired
  private ShiftRepository shiftRepository;

  @Override
  public List<ShiftBoardRecord> findAll(){

    return shiftBoardRepository.findByActiveTrue();
  }

  @Override
  public ShiftBoardRecord getRecord(String orderid, String tempid){

    return shiftBoardRepository.findFirstByOrderidAndTempid(orderid,tempid);
  }

  @Override
  public boolean processConfirmation(ConfirmationRequest request){
    PushMessageRequest pushReq = new PushMessageRequest();
    String processOrderId = "";

    for(ShiftBoardRecord rec : request.getRecords()){
      if(rec.getConfirmed()) {
        processOrderId = rec.getOrderid();
      }
    }



    for(ShiftBoardRecord rec : request.getRecords()){
      ShiftBoardRecord currRec = shiftBoardRepository.findFirstByOrderidAndTempid(rec.getOrderid(),rec.getTempid());
      if(currRec == null || !rec.getOrderid().equalsIgnoreCase(processOrderId))  continue;

      if(rec.getConfirmed()) {
        currRec.setConfirmed(true);
        pushReq.setMsgBody("You have been CONFIRMED for shift starting: " + convertEastern(currRec.getShiftStartTime()) + " For: " + currRec.getClientName());

      }else{
        pushReq.setMsgBody("You were NOT CONFIRMED for shift starting: " + convertEastern(currRec.getShiftStartTime()) + " For: " + currRec.getClientName());

      }
      List<String> tempid =  new ArrayList<>();
      tempid.add(rec.getTempid());
      pushReq.setTemps(tempid);
      currRec.setActive(false);
      shiftService.sendPushNotification(pushReq);
      shiftBoardRepository.saveAndFlush(currRec);
    }

    List<ShiftBoardRecord> remainder = shiftBoardRepository.findAllByOrderid(request.getRecords().get(0).getOrderid());

    for(ShiftBoardRecord rec : remainder){
      if(!rec.getOrderid().equalsIgnoreCase(processOrderId))  continue;
      ShiftBoardRecord currRec = shiftBoardRepository.findFirstByOrderidAndTempid(rec.getOrderid(),rec.getTempid());
      if(currRec == null)  continue;

      if(!rec.getConfirmed()) {
         pushReq.setMsgBody("You were NOT CONFIRMED for shift starting: " + currRec.getShiftStartTime() + " For: " + currRec.getClientName());

        List<String> tempid =  new ArrayList<>();
        tempid.add(rec.getTempid());
        pushReq.setTemps(tempid);
        currRec.setActive(false);
        shiftService.sendPushNotification(pushReq);
        shiftBoardRepository.saveAndFlush(currRec);
      }
    }

    return true;
  }

  @Override
  public boolean processRemoval(ConfirmationRequest request){
    PushMessageRequest pushReq = new PushMessageRequest();

    for(ShiftBoardRecord rec : request.getRecords()){
      ShiftBoardRecord currRec = shiftBoardRepository.findFirstByOrderidAndTempid(rec.getOrderid(),rec.getTempid());
      if(currRec == null)  continue;
      shiftBoardRepository.delete(currRec);

      pushReq.setMsgBody("You were NOT CONFIRMED for shift starting: " + convertEastern(currRec.getShiftStartTime()) + " For: " + currRec.getClientName());

      List<String> tempid =  new ArrayList<>();
      tempid.add(rec.getTempid());
      pushReq.setTemps(tempid);
      shiftService.sendPushNotification(pushReq);
    }

    return true;
  }

  private String convertEastern(Timestamp iso){
    if(iso == null) return "";
    long time = iso.getTime();
    Date currentDate = new Date(time);
    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss");

    TimeZone zoneNewYork = TimeZone.getTimeZone("America/New_York");
    df.setTimeZone(zoneNewYork);
    String finale = df.format(currentDate);
    System.out.println(finale);

    return finale;
  }

  @Override
  public ShiftBoardRecord saveRecord(InterestRequest request){

    ShiftResponse started = null;
    TempResponse tempHcs = null;
    ShiftBoardRecord record = new ShiftBoardRecord();
    //todo externalize
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=open&orderId=$orderId&resultType=json";
    String getTempUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getTemps&username=rsteele&password=altoApp1!&tempIdIn="+request.getTempId()+"&resultType=json";

    getShiftUrl = getShiftUrl.replace("$orderId",request.getOrderId());

      try {

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String result = restTemplate.getForObject(getShiftUrl, String.class);
        result = result.replace("[","").replace("]","");

        RestTemplate restTemplateTemp = new RestTemplateBuilder().build();
        String resultTemp = restTemplateTemp.getForObject(getTempUrl, String.class);
        resultTemp = resultTemp.replace("[","").replace("]","");

        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        started = gson.fromJson(result, ShiftResponse.class);

        gson = new Gson(); // Or use new GsonBuilder().create();
        tempHcs = gson.fromJson(resultTemp, TempResponse.class);


      } catch (Exception e) {
        //todo handle this
        logger.error("Error calling HCS for shift and temp details ", e);
      }

      if(started == null){
        return null; //todo handle this
      }
      record.setClientName(started.getClientName());
      record.setOrderid(request.getOrderId());
      record.setShiftEndTime(convertFromString(started.getShiftEndTime()));
      record.setShiftStartTime(convertFromString(started.getShiftStartTime()));
      record.setTempid(request.getTempId().toString());
      record.setConfirmed(false);
      record.setActive(true);
      record.setUsername(request.getUsername());
      if(tempHcs != null) {
        record.setFullName(tempHcs.getFirstName() + " " + tempHcs.getLastName());
      }

      return shiftBoardRepository.saveAndFlush(record);
  }

  public ResponseEntity<?> updateSession(String orderid, SessionUpdateRequest request){

    Shift update = shiftService.getShift(orderid);

    if(request.getShiftstart() != null && !request.getShiftstart().isEmpty()) {
      update.setShiftStartTimeActual(convertFromString(request.getShiftstart()));
    }

    if(request.getShiftend() != null && !request.getShiftend().isEmpty()) {
      update.setShiftEndTimeActual(convertFromString(request.getShiftend()));
    }
    shiftRepository.saveAndFlush(update);

    return new ResponseEntity<Shift>(update, HttpStatus.OK);
  }


  private Timestamp convertFromString(String input){
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      Date parsedDate = dateFormat.parse(input);
      return new java.sql.Timestamp(parsedDate.getTime());
    } catch(Exception e) { //this generic but you can control another types of exception
      logger.error("Error in conversion", e);
    }
    return null;
  }

  private Timestamp convertFromUTCString(String input){
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      Date parsedDate = dateFormat.parse(input);
      return new java.sql.Timestamp(parsedDate.getTime());
    } catch(Exception e) { //this generic but you can control another types of exception
      logger.error("Error in conversion", e);
    }
    return null;
  }

}

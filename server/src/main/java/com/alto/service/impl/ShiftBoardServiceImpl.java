package com.alto.service.impl;


import com.alto.model.*;
import com.alto.model.requests.ConfirmationRequest;
import com.alto.model.requests.InterestRequest;
import com.alto.model.requests.PushMessageRequest;
import com.alto.model.response.ShiftResponse;
import com.alto.model.response.TempResponse;
import com.alto.repository.ShiftBoardRepository;
import com.alto.service.ShiftBoardService;
import com.alto.service.ShiftService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@Service
public class ShiftBoardServiceImpl implements ShiftBoardService {


  @Autowired
  private ShiftService shiftService;

  @Autowired
  private ShiftBoardRepository shiftBoardRepository;

  @Override
  public List<ShiftBoardRecord> findAll(){

    return shiftBoardRepository.findByActiveTrue();
  }

  @Override
  public ShiftBoardRecord getRecord(String orderid, String tempid){

    return shiftBoardRepository.findByOrOrderidAndTempid(orderid,tempid);
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
      ShiftBoardRecord currRec = shiftBoardRepository.findByOrOrderidAndTempid(rec.getOrderid(),rec.getTempid());
      if(currRec == null || !rec.getOrderid().equalsIgnoreCase(processOrderId))  continue;

      if(rec.getConfirmed()) {
        currRec.setConfirmed(true);
        pushReq.setMsgBody("You have been CONFIRMED for shift starting: " + currRec.getShiftStartTime() + " For: " + currRec.getClientName());

      }else{
        pushReq.setMsgBody("You were NOT CONFIRMED for shift starting: " + currRec.getShiftStartTime() + " For: " + currRec.getClientName());

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
      ShiftBoardRecord currRec = shiftBoardRepository.findByOrOrderidAndTempid(rec.getOrderid(),rec.getTempid());
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
        //LOGGER.error("Error getting Embed URL and Token", e);
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

}

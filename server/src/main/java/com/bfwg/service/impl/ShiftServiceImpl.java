package com.bfwg.service.impl;


import com.bfwg.model.Shift;
import com.bfwg.model.ShiftRequest;
import com.bfwg.model.ShiftResponse;
import com.bfwg.repository.ShiftRepository;
import com.bfwg.service.ShiftService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by fan.jin on 2016-10-15.
 */

@Service
public class ShiftServiceImpl implements ShiftService {



  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ShiftRepository shiftRepository;

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

    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=lesliekahn&password=Jan242003!&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",request.getTempId().toString());
    getShiftUrl = getShiftUrl.replace("$orderId",request.getOrderId());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();
      HttpHeaders headers = new HttpHeaders();


      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getShiftUrl);
      HttpEntity<?> entity = new HttpEntity<>(headers);

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

      updateShift = shiftRepository.findByOrderId(request.getOrderId());
      if(updateShift == null){
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
      }

      //save to repo
      updateShift.setStatus(started.getStatus());
      if(request.getShiftstatuskey() == BREAK_START){
        updateShift.setBreakStartTime(new Timestamp(System.currentTimeMillis()));
      }else if(request.getShiftstatuskey() == BREAK_END){
        updateShift.setBreakEndTime(new Timestamp(System.currentTimeMillis()));
      }else if(request.getShiftstatuskey() == SHIFT_END){
        updateShift.setShiftEndTimeActual(new Timestamp(System.currentTimeMillis()));
        updateShift.setShiftEndSignoff(request.getShiftSignoff());
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
}

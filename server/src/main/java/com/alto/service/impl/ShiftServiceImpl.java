package com.alto.service.impl;


import com.alto.OpenShiftCSSRepository;
import com.alto.config.HCSConfiguration;
import com.alto.model.*;
import com.alto.model.requests.ClockRequest;
import com.alto.model.requests.PushMessageRequest;
import com.alto.model.requests.SessionsRequest;
import com.alto.model.requests.ShiftRequest;
import com.alto.model.response.*;
import com.alto.repository.AppUserRepository;
import com.alto.repository.MessageRepository;
import com.alto.repository.ShiftRepository;
import com.alto.repository.UserPreferencesRepository;
import com.alto.service.ShiftService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.internal.Utilities;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.*;

import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.previous;




@EnableAsync
@Service
public class ShiftServiceImpl implements ShiftService {

  public static final Logger logger = LoggerFactory.getLogger(ShiftServiceImpl.class);

  private final static  String CINCY_REGION_TEMP_ID ="7";
  private final static  String CINCY_REGION_TEMP_NAME ="Clinical Temp Cincinnati";
  private final static  String DAYTON_REGION_TEMP_ID ="23";
  private final static  String DAYTON_REGION_TEMP_NAME ="Clinical Temp Dayton";
  private final static  String COLUMBUS_REGION_TEMP_ID ="30";
  private final static  String COLUMBUS_REGION_TEMP_NAME ="Clinical Temp Columbus";
  private final static  String CLEVELAND_REGION_TEMP_NAME ="Clinical Temp Cleveland";
  private final static  String AKRON_REGION_TEMP_NAME ="Clinical Temp Akron";
  private final static  String TOLEDO_REGION_TEMP_NAME ="Clinical Temp Toledo";
  private final static  String INDIANA_REGION_TEMP_NAME ="Clinical Temp Indiana";
  private final static  String ALLIED_REGION_TEMP_NAME ="Allied Health";
  private final static  String MEDICAL_OFF_REGION_TEMP_NAME ="Medical Business Division";
  private final static  String CONTRACT_TRV_REGION_TEMP_NAME ="Clinical Contract Travel";
  private final static  String ALL_REGION_TEMP_ID ="27";
  private final static  String ALL_REGION_TEMP_NAME ="All";

  @Autowired
  Environment env;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ShiftRepository shiftRepository;
  @Autowired
  private AppUserRepository appUserRepository;
  @Autowired
  ResourceLoader resourceLoader;
  @Autowired
  UserPreferencesRepository userPreferencesRepository;
  @Autowired
  MessageRepository messageRepository;
  @Autowired
  HCSConfiguration hcsConfiguration;


  final static int BREAK_START = 1;
  final static int BREAK_END = 2;
  final static int SHIFT_END = 3;

//  @Scheduled(fixedRate = 2000)
//  public void scheduleTaskWithFixedRate() {
//    //logger.info("Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
//    System.out.println("Scheduled load");
//  }

  @Override
  public Shift findById(Long id) {
    return null;
  }

  public ResponseEntity manualShiftClock(ClockRequest req){

    boolean checkout = true;
    ShiftResponse hcsFound = null;
    Shift found = shiftRepository.findTopByOrderid(req.getOrderid());
    String getShiftUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",req.getTempid())
                  .replace("$orderId",req.getOrderid())
                  .replace("$username", hcsConfiguration.getUsername())
                  .replace("$password", hcsConfiguration.getPassword());


    if(found == null){
      found = new Shift();
      checkout = false;
    }
    if(found.getShiftEndTimeActual() != null){
      return new ResponseEntity("invalid", HttpStatus.BAD_REQUEST);
    }
    AppUser user = appUserRepository.findByTempid(req.getTempid());
    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {

      String result = restTemplate.getForObject(getShiftUrl, String.class);
      result = result.replace("[","").replace("]","");

      System.out.println(result);

      Gson gson = new Gson();
      hcsFound = gson.fromJson(result, ShiftResponse.class);

      if(hcsFound == null || hcsFound.getOrderId() == null ||
              (!hcsFound.getStatus().equalsIgnoreCase("open") && !hcsFound.getStatus().equalsIgnoreCase("filled")) ){
        logger.warn("Invalid Manual Clock in operation");
        return new ResponseEntity("invalid", HttpStatus.BAD_REQUEST);
      }

    } catch (Exception e) {
      logger.error("Error Manual clock in: ", e);
    }

    found.setClientId(hcsFound.getClientId());
    found.setClientName(hcsFound.getClientName());
    found.setFloor(hcsFound.getFloor());
    found.setOrderid(hcsFound.getOrderId());
    found.setTempid(req.getTempid());
    found.setUsername(user.getUsername());
    if(hcsFound.getShiftStartTime() != null) {
      found.setShiftStartTime(convertFromString(hcsFound.getShiftStartTime()));
    }
    if(hcsFound.getShiftEndTime() != null) {
      found.setShiftEndTime(convertFromString(hcsFound.getShiftEndTime()));
    }
    if(checkout){
      found.setShiftEndTimeActual(new Timestamp(System.currentTimeMillis()));
      found.setShiftEndSignoff("Alto Dashboard");
    }else{
      found.setShiftStartTimeActual(new Timestamp(System.currentTimeMillis()));
      found.setShiftStartSignoff("Alto Dashboard");
    }
    found.setStatus(hcsFound.getStatus());
    found.setShiftNumber(hcsFound.getShiftNumber());
    found.setOrderCertification(hcsFound.getOrderCertification());
    found.setOrderSpecialty(hcsFound.getOrderSpecialty());
    found.setNote(hcsFound.getNote());

    shiftRepository.saveAndFlush(found);


    return new ResponseEntity("success", HttpStatus.OK);
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  public ResponseEntity addShift(ShiftRequest request){
	logger.debug("Clock In Request began: "+ request.getUsername());
    ShiftResponse started = null;
    Shift saveShift = new Shift();

    String getShiftUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",request.getTempId().toString())
            .replace("$orderId",request.getOrderId())
            .replace("$username", hcsConfiguration.getUsername())
            .replace("$password", hcsConfiguration.getPassword());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();

      try {

        String result = restTemplate.getForObject(getShiftUrl, String.class);
        result = result.replace("[","").replace("]","");

        System.out.println(result);

        Gson gson = new Gson();
        started = gson.fromJson(result, ShiftResponse.class);
        if(!checkGeoFence(request)){
          logger.warn("Geofence restricted clock in/out for user: " + request.getUsername());
          return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

      } catch (Exception e) {
        logger.error("Error calling HCS for Shift Addition", e);
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
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
      getAddressIQ(request.getLat(), request.getLon(), saveShift, false);

    } catch(Exception e) {
      logger.error("Error saving Shift Addition to DB", e);
    }

    logger.debug("Clock In Request End: "+ request.getUsername());
    return new ResponseEntity(saveShift, HttpStatus.OK);
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  public List<ShiftResponse> getScheduled(String tempid){
    List<ShiftResponse> results = new ArrayList<>();

    String getShiftUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=filled&tempId=$tempId&status=filled&orderBy1=shiftStart&orderByDirection1=ASC&shiftStart="+ ZonedDateTime.now( ZoneOffset.UTC ).minusDays(1).format( java.time.format.DateTimeFormatter.ISO_INSTANT )+"&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",tempid)
                  .replace("$username", hcsConfiguration.getUsername())
                  .replace("$password", hcsConfiguration.getPassword());

      RestTemplate restTemplate = new RestTemplateBuilder().build();

      try {
        String result = restTemplate.getForObject(getShiftUrl, String.class);

        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

         results = gson.fromJson(result, userListType);
         if(results == null){
           results = new ArrayList<>();
         }
        results.sort(Comparator.comparing(ShiftResponse::getShiftStartTime));
      } catch (Exception e) {
        logger.error("Error getting scheduled shifts for tempid: "+ tempid, e);
      }
    return results;
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  public Historicals getHistoricals(String tempid){
    List<ShiftResponse> results = new ArrayList<>();
    List<ShiftResponse> finalResults = new ArrayList<>();
    Historicals history = new Historicals();
    List<Shift> worked = new ArrayList<>();
    LocalDateTime nextSaturday;
    LocalDateTime thisPastSunday;

    LocalDateTime today = LocalDateTime.now().with(LocalTime.MIDNIGHT);
    if(today.getDayOfWeek() != SATURDAY) { nextSaturday = today.with(next(SATURDAY));
    }else{ nextSaturday = today; }

    if(today.getDayOfWeek() != SUNDAY) { thisPastSunday = today.with(previous(SUNDAY));
    }else{ thisPastSunday =today; }


    String getShiftUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=filled&tempId=$tempId&status=filled&orderBy1=shiftStart&orderByDirection1=ASC&shiftStart="+ thisPastSunday.toString()+"&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",tempid)
                  .replace("$username", hcsConfiguration.getUsername())
                  .replace("$password", hcsConfiguration.getPassword());
    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {
      String result = restTemplate.getForObject(getShiftUrl, String.class);
      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

      results = gson.fromJson(result, userListType);
      if(results == null){ results = new ArrayList<>(); }
      history.setDateWindowBegin(thisPastSunday.toString());
      history.setDateWindowEnd(nextSaturday.toString());

      double hoursScheduled = 0.0;
      for(ShiftResponse sh : results){

        DateTime start = new DateTime( sh.getShiftStartTime() ) ;
        DateTime end = new DateTime( sh.getShiftEndTime() ) ;
        hoursScheduled += Minutes.minutesBetween(start, end).getMinutes() / 60;
      }
      history.setHoursScheduled(String.valueOf(hoursScheduled));
      worked = shiftRepository.findByTempidAndDates(tempid, Timestamp.valueOf(thisPastSunday));
      double hoursWorked = 0.0;
      for(Shift sh : worked){

        DateTime start = new DateTime(sh.getShiftStartTimeActual());
        DateTime end = new DateTime( sh.getShiftEndTimeActual() ) ;
        hoursWorked += Minutes.minutesBetween(start, end).getMinutes() / 60;
      }
      history.setHoursWorked(String.valueOf(hoursWorked));
    } catch (Exception e) {
      logger.error("Error getting historicals for tempid: "+ tempid, e);
    }

    String getHistShiftUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=filled&tempId=$tempId&status=filled&orderBy1=shiftStart&orderByDirection1=ASC&shiftStart="+ ZonedDateTime.now( ZoneOffset.UTC ).minusDays(14).format( java.time.format.DateTimeFormatter.ISO_INSTANT )+"&resultType=json";
    getHistShiftUrl = getHistShiftUrl.replace("$tempId",tempid)
                      .replace("$username", hcsConfiguration.getUsername())
                      .replace("$password", hcsConfiguration.getPassword());
    restTemplate = new RestTemplateBuilder().build();

    try {
      String result = restTemplate.getForObject(getHistShiftUrl, String.class);
      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

      results = gson.fromJson(result, userListType);
      if(results == null){ results = new ArrayList<>(); }

      results.sort(Comparator.comparing(ShiftResponse::getShiftStartTime));
      for(ShiftResponse sh : results){
        Shift comp = shiftRepository.findTopByOrderid(sh.getOrderId());
        if(comp != null && comp.getShiftEndTimeActual() != null) finalResults.add(sh);
      }
      history.setShifts(finalResults);
    } catch (Exception e) {
      logger.error("Error getting scheduled shifts for tempid: "+ tempid, e);
    }
    return history;
  }


  public List<ShiftResponse> getOpens(String tempid){
	logger.debug("Getting Open Shifts Request Begin: "+ tempid);
    List<ShiftResponse> resultsOpens = new ArrayList<>();

    try {

      resultsOpens = OpenShiftCSSRepository.getInstance().getArray();

      resultsOpens = pruneResults(tempid, resultsOpens);
      resultsOpens.sort(Comparator.comparing(ShiftResponse::getShiftStartTime));

    } catch (Exception e) {
      logger.error("Error getting open shifts for tempid: "+ tempid, e);
    }
    logger.debug("Getting Open Shifts Request End: "+ tempid);
    return resultsOpens;
  }

  public void getOpensData_Scheduled(){
    logger.debug("Getting Open Shifts Request Scheduled Run Begin");
    List<ShiftResponse> resultsOpens = new ArrayList<>();

    String getOpensUrl = hcsConfiguration.getBaseurl() + "getOrders&username=$username&password=$password&status=open&shiftStart="+ ZonedDateTime.now( ZoneOffset.UTC ).format( java.time.format.DateTimeFormatter.ISO_DATE )+"&shiftEnd="+ ZonedDateTime.now( ZoneOffset.UTC ).plusDays(14).format( java.time.format.DateTimeFormatter.ISO_DATE )+"&resultType=json";
    getOpensUrl = getOpensUrl
            .replace("$username", hcsConfiguration.getUsername())
            .replace("$password", hcsConfiguration.getPassword());

    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {
      String resultOpens = restTemplate.getForObject(getOpensUrl, String.class);

      Gson gson = new Gson();
      Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

      resultsOpens = gson.fromJson(resultOpens, userListType);

      resultsOpens.sort(Comparator.comparing(ShiftResponse::getShiftStartTime));
      OpenShiftCSSRepository.getInstance().addAllToArray(resultsOpens);

    } catch (Exception e) {
      logger.error("Error getting open shifts for scheduled ", e);
    }
  }


  public ResponseEntity updateShift(ShiftRequest request){
	logger.debug("Clock Out Request began: "+ request.getUsername());
    ShiftResponse started = null;
    Shift updateShift = null;

    try {

      updateShift = shiftRepository.findTopByOrderid(request.getOrderId());
      if(updateShift == null){
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
      }
      if(!checkGeoFence(request)){
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }

//todo commented break stuff
      //save to repo
//      if(request.getShiftstatuskey() == BREAK_START){
//        updateShift.setBreakStartTime(new Timestamp(System.currentTimeMillis()));
//      }else if(request.getShiftstatuskey() == BREAK_END){
//        updateShift.setBreakEndTime(new Timestamp(System.currentTimeMillis()));
//      }else if(request.getShiftstatuskey() == SHIFT_END){
        updateShift.setShiftEndTimeActual(new Timestamp(System.currentTimeMillis()));
        updateShift.setShiftEndSignoff(request.getShiftSignoff());
        updateShift.setClockoutAddress(request.getClockedAddy());
        updateShift.setCheckoutLat(request.getLat());
        updateShift.setCheckoutLon(request.getLon());
        updateShift.setBreaks(request.getBreaks());
     // }
      shiftRepository.saveAndFlush(updateShift);
      getAddressIQ(request.getLat(), request.getLon(), updateShift, true);

    } catch(Exception e) {
        logger.error("Error updarting shift for: "+ request.getUsername(), e);
    }

    logger.debug("Clock Out Request end: "+ request.getUsername());
    return new ResponseEntity(updateShift, HttpStatus.OK);
  }

  @Async
  void getAddressIQ(String lat, String lon, Shift shift, boolean finalClock){

    String addyResult = "";
    RestTemplate restTemplate = new RestTemplateBuilder().build();


      String url = hcsConfiguration.getLocationAddyUrl();
      url = url.replace("$LAT",lat).replace("$LONG",lon);

    GeoCodeResponse geoResp= restTemplate.getForObject(url, GeoCodeResponse.class);

       if(geoResp != null && !StringUtils.isEmpty(geoResp.getDisplay_name())) {
         addyResult = geoResp.getDisplay_name();
       }

    if(finalClock){
      shift.setClockoutAddress(addyResult);
    }else {
      shift.setClockInAddress(addyResult);
    }

    shiftRepository.saveAndFlush(shift);
  }


  public Shift getShift(String orderid){
    return shiftRepository.findTopByOrderid(orderid);
  }

  private boolean findMatch(List<String> userCerts, String search){
    for(String s : userCerts) {
      if (s.toUpperCase().trim().contains(search.toUpperCase().trim())) return true;
      if(s.contains("STNA") && search.contains("STNA")) return true;
      if(s.contains("LPN") && search.contains("LPN")) return true;
      if(s.contains("CNA") && search.contains("CNA")) return true;
      if(s.contains("RN") && search.contains("RN")) return true;
    }
    return false;
  }

  private List<ShiftResponse> pruneResults(String tempid, List<ShiftResponse> openShifts){

    UserPreferences prefs =  userPreferencesRepository.findTopByTempid(Long.parseLong(tempid));
    openShifts.forEach(s -> s.setStatus(s.getStatus().toLowerCase()));
    if(prefs == null) return openShifts;

    List<ShiftResponse> results = new ArrayList<>();
    List<ShiftResponse> certMatches = new ArrayList<>();
    List<ShiftResponse> regionMatches = new ArrayList<>();
    List<String> userCerts = new ArrayList<>();

    //Get certifications from user preferences
    if(prefs.getCerts() != null && !prefs.getCerts().isEmpty()){
      StringTokenizer tokenizer = new StringTokenizer(prefs.getCerts(), ",", false);
      while (tokenizer.hasMoreTokens()) {
        userCerts.add(tokenizer.nextToken().trim());
      }
    }

    //filter based on home region compared against shift region
    for(ShiftResponse shift : openShifts) {
      if(shift == null || shift.getRegionName() == null) continue;
      if(prefs.getRegion() == null || prefs.getRegion().contains("ALL Regions")){
        regionMatches.add(shift);
        continue;
      }
      switch(shift.getRegionName())
      {
        case CINCY_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Cincinnati")) regionMatches.add(shift);
          break;
        case DAYTON_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Dayton")) regionMatches.add(shift);
          break;
        case COLUMBUS_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Columbus")) regionMatches.add(shift);
          break;
        case CLEVELAND_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Cleveland")) regionMatches.add(shift);
          break;
        case AKRON_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Akron")) regionMatches.add(shift);
          break;
        case INDIANA_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Indiana")) regionMatches.add(shift);
          break;
        case TOLEDO_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Toledo")) regionMatches.add(shift);
          break;
        case ALLIED_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Allied")) regionMatches.add(shift);
          break;
        case MEDICAL_OFF_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Medical Business")) regionMatches.add(shift);
          break;
        case CONTRACT_TRV_REGION_TEMP_NAME:
          if(prefs.getRegion() != null && prefs.getRegion().contains("Contract Travel")) regionMatches.add(shift);
          break;
      }
    }

    //Filter shifts based on certification
    for(ShiftResponse shift : regionMatches) {

      if (shift.getOrderCertification() != null && !shift.getOrderCertification().isEmpty()) {
        StringTokenizer tokenizer = new StringTokenizer(shift.getOrderCertification(), ",", false);
        while (tokenizer.hasMoreTokens()) {
          //userCerts.add(tokenizer.nextToken());
          if (findMatch(userCerts, tokenizer.nextToken())) certMatches.add(shift);
        }
      } else {
        certMatches = openShifts;
      }

    }

    //filter based on days of week from user preferences
    for(ShiftResponse shift : certMatches) {

      DateTime dt = new DateTime( shift.getShiftStartTime() ) ;
      DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE"); // use 'E' for short abbreviation (Mon, Tues, etc)
      String strEnglish = fmt.print(dt);

      switch(strEnglish)
      {
        case "Sun":
          if(prefs.getSunday()) results.add(shift);
          break;
        case "Mon":
          if(prefs.getMonday()) results.add(shift);
          break;
        case "Tue":
          if(prefs.getTuesday()) results.add(shift);
          break;
        case "Wed":
          if(prefs.getWednesday()) results.add(shift);
          break;
        case "Thu":
          if(prefs.getThursday()) results.add(shift);
          break;
        case "Fri":
          if(prefs.getFriday()) results.add(shift);
          break;
        case "Sat":
          if(prefs.getSaturday()) results.add(shift);
          break;
        default:
          results.add(shift);
      }
    }

    return results;
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  public ClientAddressResponse getClient (String clientid){

    ClientResponse client = null;
    ClientAddressResponse addy = new ClientAddressResponse();
    String getClientUrl = hcsConfiguration.getBaseurl() + "getClients&username=$username&password=$password&clientIdIn=$clientId&resultType=json";
    getClientUrl = getClientUrl.replace("$username", hcsConfiguration.getUsername())
                   .replace("$password", hcsConfiguration.getPassword())
                   .replace("$clientId", clientid);
    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {

      String result = restTemplate.getForObject(getClientUrl, String.class);
      result = result.replace("[","").replace("]","");

      System.out.println(result);

      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      client = gson.fromJson(result, ClientResponse.class);

      if(client != null){
        addy.setClientId(clientid);
        addy.setAddress(client.getAddress());
        addy.setAddress2(client.getAddress2());
        addy.setState(client.getState());
        addy.setCity(client.getCity());
        addy.setZip(client.getZip());
        addy.setCounty(client.getCounty());
      }

    } catch (Exception e) {
      logger.error("Error getting client record from HCS", e);
    }
    return addy;
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  private Boolean checkGeoFence(ShiftRequest request){

    ClientResponse client = null;
    List<GeoCodeResponse> geoList = new ArrayList<>();

    String getClientUrl = hcsConfiguration.getBaseurl() + "getClients&username=$username&password=$password&clientIdIn=$clientId&resultType=json";
    getClientUrl = getClientUrl.replace("$username", hcsConfiguration.getUsername())
            .replace("$password", hcsConfiguration.getPassword())
            .replace("$clientId", request.getClientId());
    String getCoordsURL = hcsConfiguration.getLocationUrl();
    RestTemplate restTemplate = new RestTemplateBuilder().build();


      try {

        String result = restTemplate.getForObject(getClientUrl, String.class);
        result = result.replace("[","").replace("]","");


        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        client = gson.fromJson(result, ClientResponse.class);

        getCoordsURL = getCoordsURL.replace("$searchstring", client.getAddress() + " " +client.getState()+ " " + client.getZip());
        String goeResp= restTemplate.getForObject(getCoordsURL, String.class);
        Type userListType = new TypeToken<ArrayList<GeoCodeResponse>>(){}.getType();

        geoList = gson.fromJson(goeResp, userListType);

        for(GeoCodeResponse geo : geoList){
          //Double dist = haversine(39.861742, -84.290875, Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
          Double dist = haversine(Double.parseDouble(request.getLat()), Double.parseDouble(request.getLon()), Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
          if(dist < 4.0){
            return true;
          }
        }
        if(tryGeowithCity(request, client)){
          return true;
        }
        logger.warn("Geo restriction violated user: "+request.getUsername() +" Lat: " + Double.parseDouble(request.getLat()) + " Long: " + Double.parseDouble(request.getLon()) );
      } catch (Exception e) {
        logger.error("Error when checking geofence - ClientID: "+ request.getClientId() + " User: " + request.getUsername(), e);
        logger.error("Failing call: " + getCoordsURL );
      }

    return false;
  }

  private boolean tryGeowithCity(ShiftRequest request, ClientResponse client){


    List<GeoCodeResponse> geoList = new ArrayList<>();
    String getCoordsURL = hcsConfiguration.getLocationUrl();
    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {

      Gson gson = new Gson();
      getCoordsURL = getCoordsURL.replace("$searchstring", client.getAddress() + " " +client.getState()+ " " + client.getCity());
      String goeResp= restTemplate.getForObject(getCoordsURL, String.class);
      Type userListType = new TypeToken<ArrayList<GeoCodeResponse>>(){}.getType();

      geoList = gson.fromJson(goeResp, userListType);

      for(GeoCodeResponse geo : geoList){
        //Double dist = haversine(39.861742, -84.290875, Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
        Double dist = haversine(Double.parseDouble(request.getLat()), Double.parseDouble(request.getLon()), Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
        if(dist < 2.0){
          return true;
        }
      }
    } catch (Exception e) {
      logger.error("Error when checking geofence - ClientID: "+ request.getClientId() + " User: " + request.getUsername(), e);
      logger.error("Failing call: " + getCoordsURL );
    }

    return false;
  }

  public static final double R = 6372.8; // In kilometers
  public static double haversine(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return R * c;
  }

  public List<Sessions> sessionsData(SessionsRequest request){

    Timestamp fromTS1 = null;
    Timestamp fromTS2 = null;
    List<TempResponse>  tempHcs = null;
    List<Sessions> results = new ArrayList<>();
    List<Shift> shifts = new ArrayList<>();

    String getActiveTempsUrl = hcsConfiguration.getBaseurl() +  "getTemps&username=$username&password=$password&statusIn=Active&resultType=json";
    getActiveTempsUrl = getActiveTempsUrl.replace("$username", hcsConfiguration.getUsername())
            .replace("$password", hcsConfiguration.getPassword());
    try {

      SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("E MMM dd yyyy");
      Date lFromDate1 = datetimeFormatter1.parse(request.getStart());
      fromTS1 = new Timestamp(lFromDate1.getTime());

      SimpleDateFormat datetimeFormatter2 = new SimpleDateFormat("E MMM dd yyyy");
      Date lFromDate2 = datetimeFormatter2.parse(request.getEnd());
      fromTS2 = new Timestamp(lFromDate2.getTime());

      RestTemplate restTemplateTemp = new RestTemplateBuilder().build();
      String resultTemp = restTemplateTemp.getForObject(getActiveTempsUrl, String.class);

      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      TempResponse[] mcArray = gson.fromJson(resultTemp, TempResponse[].class);
      tempHcs = new ArrayList<>(Arrays.asList(mcArray));


    shifts = shiftRepository.findByDates(fromTS1, fromTS2);

    for(Shift s : shifts){

      TempResponse match = tempHcs.stream()
              .filter(temp -> s.getTempid().equals(temp.getTempId()))
              .findFirst()
              .orElse(new TempResponse());


        Sessions sess = new Sessions();
//        sess.setBreakEndTime(convertEastern(s.getBreakEndTime()));
//        sess.setBreakStartTime(convertEastern(s.getBreakStartTime()));
        sess.setCheckinLat(s.getCheckinLat());
        sess.setCheckinLon(s.getCheckinLon());
        sess.setCheckoutLat(s.getCheckoutLat());
        sess.setCheckoutLon(s.getCheckoutLon());
        sess.setClientId(s.getClientId());
        sess.setClientName(s.getClientName());
        sess.setClockInAddress(s.getClockInAddress());
        sess.setClockoutAddress(s.getClockoutAddress());
        sess.setFloor(s.getFloor());
        sess.setOrderCertification(s.getOrderCertification());
        sess.setOrderid(s.getOrderid());
        sess.setOrderSpecialty(s.getOrderSpecialty());
        sess.setShiftEndSignoff(s.getShiftEndSignoff());
        sess.setShiftEndTime(convertEastern(s.getShiftEndTime()));
        sess.setShiftEndTimeActual(convertEastern(s.getShiftEndTimeActual()));
        sess.setShiftNumber(s.getShiftNumber());
        sess.setShiftStartSignoff(s.getShiftStartSignoff());
        sess.setShiftStartTime(convertEastern(s.getShiftStartTime()));
        sess.setShiftStartTimeActual(convertEastern(s.getShiftStartTimeActual()));
        sess.setStatus(s.getStatus());
        sess.setTempid(s.getTempid());
        sess.setUsername(s.getUsername());
        sess.setTempName(match.getFirstName() + " " + match.getLastName());
        sess.setBreaks(s.getBreaks());

        results.add(sess);

    }

      java.time.format.DateTimeFormatter fm = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
              results.sort((o1, o2) -> LocalDateTime.parse(o1.getShiftStartTime(), fm)
                      .compareTo(LocalDateTime.parse(o2.getShiftStartTime(), fm)));

    }catch(Exception e){
        logger.error("Error generating session data", e);
    }

    return results;
  }

  private String convertEastern(Timestamp iso){
    if(iso == null) return "";
    long time = iso.getTime();
    Date currentDate = new Date(time);
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);

    TimeZone zoneNewYork = TimeZone.getTimeZone("America/New_York");
    df.setTimeZone(zoneNewYork);
    String finale = df.format(currentDate);
    System.out.println(finale);

    return finale;
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

  public void sendPushNotification(PushMessageRequest message){


    for(String tempid : message.getTemps()){
      AppUser user = appUserRepository.findByTempid(tempid);

      MessageAudit auditor = new MessageAudit();
      auditor.setUsername(message.getAudit());
      auditor.setMessage(message.getMsgBody());
      auditor.setTime(new SimpleDateFormat("MM/dd/yyyy hh.mm a").format(new Date()));
      auditor.setRecipient(tempid);

      if(user == null || user.getDevicetoken() == null ) continue;

      if( user.getDevicetoken() != null && user.getDevicetoken().length() > 10){

        auditor.setSuccess(sendFMSNotigication(user.getDevicetoken(), message.getMsgBody()) );

      }else if(StringUtils.isNotBlank(user.getDevicetoken())){ //try anyway
        auditor.setSuccess(sendFMSNotigication(user.getDevicetoken(), message.getMsgBody()) );

      }else{
        auditor.setSuccess(false);
      }
      messageRepository.saveAndFlush(auditor);
    }


  }

  private boolean sendFMSNotigication(String deviceToken, String messg) {

    try {
      String androidFcmKey = hcsConfiguration.getDroidFCMKey();
      String androidFcmUrl = "https://fcm.googleapis.com/fcm/send";

      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set("Authorization", "key=" + androidFcmKey);
      httpHeaders.set("Content-Type", "application/json");
      JSONObject msg = new JSONObject();
      JSONObject json = new JSONObject();

      msg.put("title", "New message from Alto!");
      msg.put("body", messg);
      //msg.put("notificationType", "Test");

      json.put("notification", msg);
      json.put("to", deviceToken);


      HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(), httpHeaders);
      String response = restTemplate.postForObject(androidFcmUrl, httpEntity, String.class);
      logger.debug("Sent notification with response: " + response);
    } catch (Exception e) {
      logger.error("Error sending notification", e);
      return false;
    }
    return true;
  }
}

package com.alto.service.impl;


import com.alto.model.*;
import com.alto.model.requests.ClockRequest;
import com.alto.model.requests.PushMessageRequest;
import com.alto.model.requests.SessionsRequest;
import com.alto.model.requests.ShiftRequest;
import com.alto.model.response.*;
import com.alto.repository.AppUserRepository;
import com.alto.repository.ShiftRepository;
import com.alto.repository.UserPreferencesRepository;
import com.alto.service.ShiftService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.internal.Utilities;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.*;

import java.util.*;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.previous;





@Service
public class ShiftServiceImpl implements ShiftService {

  private final static  String CINCY_REGION_TEMP_ID ="7";
  private final static  String CINCY_REGION_TEMP_NAME ="Clinical Temp Cincinnati";
  private final static  String DAYTON_REGION_TEMP_ID ="23";
  private final static  String DAYTON_REGION_TEMP_NAME ="Clinical Temp Dayton";
  private final static  String COLUMBUS_REGION_TEMP_ID ="30";
  private final static  String COLUMBUS_REGION_TEMP_NAME ="Clinical Temp Columbus";
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
    Shift found = shiftRepository.findByOrderid(req.getOrderid());
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",req.getTempid());
    getShiftUrl = getShiftUrl.replace("$orderId",req.getOrderid());

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
        return new ResponseEntity("invalid", HttpStatus.BAD_REQUEST);
        //todo logger
      }

    } catch (Exception e) {
      e.printStackTrace();
      //todo logger
      //LOGGER.error("Error getting Embed URL and Token", e);
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

  public ResponseEntity addShift(ShiftRequest request){
    ShiftResponse started = null;
    Shift saveShift = new Shift();

    //todo externalize
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=filled&tempId=$tempId&orderId=$orderId&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",request.getTempId().toString());
    getShiftUrl = getShiftUrl.replace("$orderId",request.getOrderId());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();

      try {

        String result = restTemplate.getForObject(getShiftUrl, String.class);
        result = result.replace("[","").replace("]","");

        System.out.println(result);

        Gson gson = new Gson();
        started = gson.fromJson(result, ShiftResponse.class);
        if(!checkGeoFence(request)){
          return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

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

    return new ResponseEntity(saveShift, HttpStatus.OK);
  }

  public List<ShiftResponse> getScheduled(String tempid){
    List<ShiftResponse> results = new ArrayList<>();

    //todo externalize
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=filled&tempId=$tempId&status=filled&orderBy1=shiftStart&orderByDirection1=ASC&shiftStart="+ ZonedDateTime.now( ZoneOffset.UTC ).format( java.time.format.DateTimeFormatter.ISO_INSTANT )+"&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",tempid);

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
        e.printStackTrace();
        //LOGGER.error("Error getting Embed URL and Token", e);
      }
    return results;
  }

  public Historicals getHistoricals(String tempid){
    List<ShiftResponse> results = new ArrayList<>();
    Historicals history = new Historicals();
    List<Shift> worked = new ArrayList<>();
    LocalDateTime nextSaturday;
    LocalDateTime thisPastSunday;

    LocalDateTime today = LocalDateTime.now().with(LocalTime.MIDNIGHT);
    if(today.getDayOfWeek() != SATURDAY) {
       nextSaturday = today.with(next(SATURDAY));
    }else{
      nextSaturday = today;
    }
    if(today.getDayOfWeek() != SUNDAY) {
       thisPastSunday = today.with(previous(SUNDAY));
    }else{
      thisPastSunday =today;
    }

    //todo externalize
    String getShiftUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=filled&tempId=$tempId&status=filled&orderBy1=shiftStart&orderByDirection1=ASC&shiftStart="+ thisPastSunday.toString()+"&resultType=json";
    getShiftUrl = getShiftUrl.replace("$tempId",tempid);

    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {
      String result = restTemplate.getForObject(getShiftUrl, String.class);

      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

      results = gson.fromJson(result, userListType);
      if(results == null){
        results = new ArrayList<>();
      }
      history.setDateWindowBegin(thisPastSunday.toString());
      history.setDateWindowEnd(nextSaturday.toString());

      double hoursScheduled = 0.0;
      for(ShiftResponse sh : results){

        DateTime start = new DateTime( sh.getShiftStartTime() ) ;
        DateTime end = new DateTime( sh.getShiftEndTime() ) ;

        hoursScheduled += Minutes.minutesBetween(start, end).getMinutes() / 60;
      }
      history.setHoursScheduled(String.valueOf(hoursScheduled));

      worked = shiftRepository.findByTempid(tempid);
      double hoursWorked = 0.0;
      for(Shift sh : worked){

        DateTime start = new DateTime(sh.getShiftStartTimeActual());
        DateTime end = new DateTime( sh.getShiftEndTimeActual() ) ;

        hoursWorked += Minutes.minutesBetween(start, end).getMinutes() / 60;
      }
      history.setHoursWorked(String.valueOf(hoursWorked));


    } catch (Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error getting Embed URL and Token", e);
    }
    return history;
  }

  public List<ShiftResponse> getOpens(String tempid){
    List<ShiftResponse> resultsOpens = new ArrayList<>();
    //todo externalize
    String getOpensUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getOrders&username=rsteele&password=altoApp1!&status=open&shiftStart="+ ZonedDateTime.now( ZoneOffset.UTC ).format( java.time.format.DateTimeFormatter.ISO_DATE )+"&shiftEnd="+ ZonedDateTime.now( ZoneOffset.UTC ).plusDays(14).format( java.time.format.DateTimeFormatter.ISO_DATE )+"&resultType=json";
    getOpensUrl = getOpensUrl.replace("$tempId",tempid);

    RestTemplate restTemplate = new RestTemplateBuilder().build();

    try {
      String resultOpens = restTemplate.getForObject(getOpensUrl, String.class);

      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      Type userListType = new TypeToken<ArrayList<ShiftResponse>>(){}.getType();

      resultsOpens = gson.fromJson(resultOpens, userListType);

      resultsOpens = pruneResults(tempid, resultsOpens);
      resultsOpens.sort(Comparator.comparing(ShiftResponse::getShiftStartTime));

    } catch (Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error getting Embed URL and Token", e);
    }

    return resultsOpens;
  }

  public ResponseEntity updateShift(ShiftRequest request){
    ShiftResponse started = null;
    Shift updateShift = null;

    try {

      updateShift = shiftRepository.findByOrderid(request.getOrderId());
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


    } catch(Exception e) {
      e.printStackTrace();
      //LOGGER.error("Error getting Embed URL and Token", e);
    }

    return new ResponseEntity(updateShift, HttpStatus.OK);
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

  private boolean findMatch(List<String> userCerts, String search){
    for(String s : userCerts) {
      if (s.toUpperCase().trim().contains(search.toUpperCase().trim())) return true;
    }
    return false;
  }

  private List<ShiftResponse> pruneResults(String tempid, List<ShiftResponse> openShifts){

    UserPreferences prefs =  userPreferencesRepository.findByTempid(Long.parseLong(tempid));
    if(prefs == null) return openShifts;

    List<ShiftResponse> results = new ArrayList<>();
    List<ShiftResponse> certMatches = new ArrayList<>();
    List<ShiftResponse> regionMatches = new ArrayList<>();
    List<String> userCerts = new ArrayList<>();

    String region = prefs.getRegion();

    //Get certifications from user preferences
    if(prefs.getCerts() != null && !prefs.getCerts().isEmpty()){
      StringTokenizer tokenizer = new StringTokenizer(prefs.getCerts(), ",", false);
      while (tokenizer.hasMoreTokens()) {
        userCerts.add(tokenizer.nextToken().trim());
      }
    }

    //filter based on home region compared against shift region
    for(ShiftResponse shift : openShifts) {
      switch(shift.getRegionName())
      {
        case CINCY_REGION_TEMP_NAME:
          if(prefs.getRegion().equals(CINCY_REGION_TEMP_ID) || prefs.getRegion().equals(ALL_REGION_TEMP_ID)) regionMatches.add(shift);
          break;
        case DAYTON_REGION_TEMP_NAME:
          if(prefs.getRegion().equals(DAYTON_REGION_TEMP_ID) || prefs.getRegion().equals(ALL_REGION_TEMP_ID)) regionMatches.add(shift);
          break;
        case COLUMBUS_REGION_TEMP_NAME:
          if(prefs.getRegion().equals(COLUMBUS_REGION_TEMP_ID) || prefs.getRegion().equals(ALL_REGION_TEMP_ID)) regionMatches.add(shift);
          break;
        default:
          regionMatches.add(shift);
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

  public ClientAddressResponse getClient (String clientid){

    ClientResponse client = null;
    ClientAddressResponse addy = new ClientAddressResponse();
    String getClientUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getClients&username=rsteele&password=altoApp1!&clientIdIn="+clientid+"&resultType=json";
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

    } catch (Exception e) { //todo logger
      //LOGGER.error("Error getting Embed URL and Token", e);
    }
    return addy;
  }

  private Boolean checkGeoFence(ShiftRequest request){

    ClientResponse client = null;
    List<GeoCodeResponse> geoList = new ArrayList<>();

    String getClientUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getClients&username=rsteele&password=altoApp1!&clientIdIn="+request.getClientId()+"&resultType=json";
    String getCoordsURL = "https://us1.locationiq.com/v1/search.php?key=01564e14da0703&q=$searchstring&format=json";
    RestTemplate restTemplate = new RestTemplateBuilder().build();


      try {

        String result = restTemplate.getForObject(getClientUrl, String.class);
        result = result.replace("[","").replace("]","");

        System.out.println(result);

        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        client = gson.fromJson(result, ClientResponse.class);

       // getCoordsURL = getCoordsURL.replace("$searchstring",client.getAddress());
        getCoordsURL = getCoordsURL.replace("$searchstring", client.getAddress() + " " +client.getCity()+ " " + client.getState());

        //1010 Taywood Rd
        String goeResp= restTemplate.getForObject(getCoordsURL, String.class);
        Type userListType = new TypeToken<ArrayList<GeoCodeResponse>>(){}.getType();

        geoList = gson.fromJson(goeResp, userListType);

        for(GeoCodeResponse geo : geoList){
          //Double dist = haversine(39.861742, -84.290875, Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
          Double dist = haversine(Double.parseDouble(request.getLat()), Double.parseDouble(request.getLon()), Double.parseDouble(geo.getLat()), Double.parseDouble(geo.getLon()));
          if(dist < 0.3){
            return true;
          }
        }



      } catch (Exception e) { //todo logger
        //LOGGER.error("Error getting Embed URL and Token", e);
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
    //todo externalize
    String getActiveTempsUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getTemps&username=rsteele&password=altoApp1!&statusIn=Active&resultType=json";

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

    }catch(Exception e){
      e.printStackTrace();
    }

    shifts = shiftRepository.findByDates(fromTS1, fromTS2);

    for(Shift s : shifts){

      TempResponse match = tempHcs.stream()
              .filter(temp -> s.getTempid().equals(temp.getTempId()))
              .findAny()
              .orElse(null);

      if(match != null) {

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
        //todo else log no matching temp found for shift
    }
    return results;
  }

  private String convertEastern(Timestamp iso){

    long time = iso.getTime();
    Date currentDate = new Date(time);
    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    TimeZone zoneNewYork = TimeZone.getTimeZone("America/New_York");
    df.setTimeZone(zoneNewYork);
    String finale = df.format(currentDate);
    System.out.println(finale);

    return finale;
  }


  public void sendPushNotification(PushMessageRequest message){

    for(String tempid : message.getTemps()){
      AppUser user = appUserRepository.findByTempid(tempid);

      if(user == null || user.getDevicetype() == null || user.getDevicetoken() == null ) continue;

      if(user.getDevicetype().equalsIgnoreCase("Android") && user.getDevicetoken() != null && user.getDevicetoken().length() > 10){

        sendFMSNotigication(user.getDevicetoken(), message.getMsgBody());
        //sendAPNSNotification(user.getDevicetoken(), message.getMsgBody());
      }else if(user.getDevicetype().equalsIgnoreCase("iOS") && user.getDevicetoken() != null && user.getDevicetoken().length() > 10){

        //sendAPNSNotification(user.getDevicetoken(), message.getMsgBody());
        sendFMSNotigication(user.getDevicetoken(), message.getMsgBody());
      }
    }

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

      msg.put("title", "New message from Alto!");
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

//// See documentation on defining a message payload.
//      Message message = Message.builder()
//              .putData("score", "850")
//              .putData("time", "2:45")
//              .setToken(deviceToken)
//              .build();
//
//// Send a message to the device corresponding to the provided
//// registration token.
//      String response = FirebaseMessaging.getInstance().send(message);
//// Response is a message ID string.
//      System.out.println("Successfully sent message: " + response);


      ApnsService service;

      ClassLoader classLoader = new ShiftServiceImpl().getClass().getClassLoader();

      File file = new File(classLoader.getResource("apns.p12").getFile());
      InputStream targetStream = new FileInputStream(file);

      service = APNS.newService().withCert(targetStream, "altoapp")
                .withProductionDestination().build();

      String payload = APNS.newPayload().customField("customData",messg)
              .alertBody("Message").build();
      service.push(Utilities.encodeHex(deviceToken.getBytes()), payload);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

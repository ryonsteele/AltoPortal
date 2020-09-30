package com.alto.service.impl;

import com.alto.config.HCSConfiguration;
import com.alto.model.AppUser;
import com.alto.model.User;
import com.alto.model.UserPreferences;
import com.alto.model.requests.AppUserRequest;
import com.alto.model.requests.PreferencesRequest;
import com.alto.model.requests.ResetRequest;
import com.alto.model.requests.UserRemoveRequest;
import com.alto.model.response.MessageAudit;
import com.alto.model.response.TempResponse;
import com.alto.repository.AppUserRepository;
import com.alto.repository.MessageRepository;
import com.alto.repository.UserPreferencesRepository;
import com.alto.service.AppUserService;
import com.alto.service.AuthorityService;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class AppUserServiceImpl implements AppUserService {

  public static final Logger logger = LoggerFactory.getLogger(AppUserServiceImpl.class);

  @Autowired
  private HCSConfiguration hcsConfiguration;

  @Autowired
  private AppUserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthorityService authService;

  @Autowired
  private UserPreferencesRepository userPreferencesRepository;

  @Autowired
  MessageRepository messageRepository;

  public void resetCredentials() {
    List<AppUser> users = userRepository.findAll();
    for (AppUser user : users) {
      user.setPassword(passwordEncoder.encode("123"));
      userRepository.save(user);
    }
  }

  @Override
  // @PreAuthorize("hasRole('USER')")
  public AppUser findByUsername(String username) throws UsernameNotFoundException {
    AppUser u = userRepository.findByUsernameEquals(username);
    return u;
  }

  @PreAuthorize("hasRole('USER')")
  public AppUser findById(Long id) throws AccessDeniedException {
    AppUser u = userRepository.getOne(id);
    return u;
  }

  //@PreAuthorize("hasRole('USER')")
  public List<AppUser> findAll() throws AccessDeniedException {
    List<AppUser> result = userRepository.findAll();
    return result;
  }

  @Override
  public ResponseEntity updateToken(String user, String token) {

    if(StringUtils.isBlank(user) || StringUtils.isBlank(token)){
      logger.warn("No Username or Token passed when updating token ");
      return new ResponseEntity(BAD_REQUEST);
    }

    AppUser userRec = userRepository.findByUsernameEquals(user.trim().toLowerCase());

    if(userRec != null){
      userRec.setDevicetoken(token);
      return new ResponseEntity(userRepository.saveAndFlush(userRec), OK);
    }
    //logger.error("No record found when updating token for user: " + user);
    return new ResponseEntity(BAD_REQUEST);
  }

  @Override
  public AppUser resetAppUser(ResetRequest request) {

    AppUser userRec = userRepository.findByTempid(request.getTempid());
    if(userRec != null) {
      userRec.setPassword(passwordEncoder.encode(request.getReset().trim()));
      userRepository.saveAndFlush(userRec);
    }

    return userRec;
  }

  @Override
  public ResponseEntity<?> save(AppUserRequest userRequest) {

	//logger.debug("Saving login request");
    boolean freshwipe = userRequest.getFirstTime();
    if(userRequest.getUsername() == null){
      logger.warn("No Username was passed!");
      return new ResponseEntity<String>("Username Required", UNAUTHORIZED);
    }
    userRequest = getTempByUsername(userRequest.getUsername().trim().toLowerCase(), userRequest.getPassword());
    if(userRequest.getFirstname() == null || userRequest.getLastname() == null){
      logger.warn("First and Last Name for username: "+ userRequest.getUsername() +" was not found in HCS");
      return new ResponseEntity<String>("Username Not Found in HCS", UNAUTHORIZED);
    }

    AppUser user = new AppUser();
    AppUser exists = findByUsername(userRequest.getUsername().trim().toLowerCase());

    if(exists != null){
      if(!passwordEncoder.matches(userRequest.getPassword().trim(), exists.getPassword().trim() ) && ( !freshwipe ) ){
        logger.warn("Username: " + userRequest.getUsername() + " Provided a non matching password. Fresh install value: "+ userRequest.getFirstTime() );
        return new ResponseEntity<String>("Password Incorrect, try again or re-install App", UNAUTHORIZED);

      }else if(freshwipe){
        if(StringUtils.isNotBlank(userRequest.getDevicetoken())) exists.setDevicetoken(userRequest.getDevicetoken());
        if(StringUtils.isNotBlank(userRequest.getDevicetype())) exists.setDevicetype(userRequest.getDevicetype());
        if(StringUtils.isNotBlank(userRequest.getPassword().trim())) exists.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.saveAndFlush(exists);
        //logger.debug("Found existing with Freshwipe flag, returned new saved record");
        return new ResponseEntity<AppUser>(exists, OK);

       } else{
        if(StringUtils.isNotBlank(userRequest.getDevicetoken())) exists.setDevicetoken(userRequest.getDevicetoken());
        if(StringUtils.isNotBlank(userRequest.getDevicetype())) exists.setDevicetype(userRequest.getDevicetype());
        userRepository.saveAndFlush(exists);
        //logger.debug("Found existing, returned new saved record");
        return new ResponseEntity<AppUser>(exists, OK);
      }
    }


    AppUser existsCheck = userRepository.findByTempid(userRequest.getTempId());
    if(existsCheck != null){
    	//logger.debug("Found existing by tempid, returned new saved record : " + userRequest.getTempId());
    	//logger.debug("Exists record : " + existsCheck.toString());
    	return new ResponseEntity<AppUser>(exists, OK);
    }

    logger.debug("New User method hit: " + userRequest.getUsername().trim().toLowerCase() + " Tempid: " +userRequest.getTempId());
    user.setUsername(userRequest.getUsername().trim().toLowerCase());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword().trim()));
    user.setFirstname(userRequest.getFirstname());
    user.setLastname(userRequest.getLastname());
    user.setTempid(userRequest.getTempId());
    user.setDevicetype(userRequest.getDevicetype());
    user.setCerts(userRequest.getCerts());

    return new ResponseEntity<AppUser>(userRepository.saveAndFlush(user), OK);
  }

  @Override
  public UserPreferences saveUserPrefs(PreferencesRequest request){

    UserPreferences prefs;

    prefs = userPreferencesRepository.findTopByTempid(request.getTempId());
    if(prefs == null) prefs = new UserPreferences();
    prefs.setTempid(request.getTempId());
    prefs.setUsername(request.getUsername().trim().toLowerCase());
    prefs.setMonday(request.getMon());
    prefs.setTuesday(request.getTue());
    prefs.setWednesday(request.getWed());
    prefs.setThursday(request.getThur());
    prefs.setFriday(request.getFri());
    prefs.setSaturday(request.getSat());
    prefs.setSunday(request.getSun());

    StringBuilder sb = new StringBuilder();
    if(request.getCerts() != null) {
      for (String cert : request.getCerts()) {
        if (sb.length() != 0) {
          sb.append(",");
        }
        sb.append(cert);
      }
      prefs.setCerts(sb.toString());
    }

    if(request.getRegions() != null) {
      StringBuilder sbRegions = new StringBuilder();
      for (String reg : request.getRegions()) {
        if (sbRegions.length() != 0) {
          sbRegions.append(",");
        }
        sbRegions.append(reg);
      }
      prefs.setRegion(sbRegions.toString());
    }
    return userPreferencesRepository.saveAndFlush(prefs);
  }

  @Override
  public UserPreferences fetchUserPrefs(String tempid){
    return userPreferencesRepository.findTopByTempid(Long.parseLong(tempid));
  }

  @Retryable(maxAttempts=5, value = HttpServerErrorException.class,
          backoff = @Backoff(delay = 1000, multiplier = 2))
  private AppUserRequest getTempByUsername(String username){

    TempResponse started = null;
    AppUserRequest userRequest = new AppUserRequest();
    userRequest.setUsername(username);

    //todo implement sessionkey
    String getTempUrl = hcsConfiguration.getBaseurl() + "getTemps&username=$username&password=$password&emailStartsWith=$email&statusIn=Active,Dormant,Inactive&resultType=json";
    getTempUrl = getTempUrl.replace("$email", userRequest.getUsername().trim()).replace("$username", hcsConfiguration.getUsername()).replace("$password", hcsConfiguration.getPassword());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();

        String result = restTemplate.getForObject(getTempUrl, String.class);
        result = result.replace("[","").replace("]","");


        Gson gson = new Gson();
        started = gson.fromJson(result, TempResponse.class);
        if(started == null || started.getTempId() == null || started.getTempId().isEmpty()){
          logger.error("No result found with emailStartsWith in HCS for username: " + username);
          userRequest = tryAuthHCSAltFilter(username);
          throw new ResponseStatusException(BAD_REQUEST);
        }else {
          userRequest.setTempId(started.getTempId());
          userRequest.setFirstname(started.getFirstName());
          userRequest.setLastname(started.getLastName());
          userRequest.setCerts(started.getCertification());
          userRequest.setRegion(started.getHomeRegion());
        }


      } catch (Exception e) {
        tryAuthHCSAltFilter(username);
        logger.error("Error calling HCS with emailStartsWith for username: " + username);
      }

    return userRequest;
  }

  private AppUserRequest tryAuthHCSAltFilter(String username){

    TempResponse started = null;
    AppUserRequest userRequest = new AppUserRequest();
    userRequest.setUsername(username);

    //todo implement sessionkey
    String getTempUrl = hcsConfiguration.getBaseurl() + "getTemps&username=$username&password=$password&emailLike=$email&statusIn=Active,Dormant,Inactive&resultType=json";
    getTempUrl = getTempUrl.replace("$email", userRequest.getUsername().trim()).replace("$username", hcsConfiguration.getUsername()).replace("$password", hcsConfiguration.getPassword());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();

      String result = restTemplate.getForObject(getTempUrl, String.class);
      result = result.replace("[","").replace("]","");


      Gson gson = new Gson(); // Or use new GsonBuilder().create();
      started = gson.fromJson(result, TempResponse.class);
      if(started == null || started.getTempId() == null || started.getTempId().isEmpty()){
        logger.error("HTTP 400 No result found in HCS for username: " + username);
        throw new ResponseStatusException(BAD_REQUEST);
      }
      userRequest.setTempId(started.getTempId());
      userRequest.setFirstname(started.getFirstName());
      userRequest.setLastname(started.getLastName());
      userRequest.setCerts(started.getCertification());
      userRequest.setRegion(started.getHomeRegion());


    } catch (Exception e) {
      logger.error("Error calling emailLike HCS for username: " + username);
    }

    return userRequest;
  }

  private AppUserRequest getTempByUsername(String username, String password){

    AppUserRequest temp = getTempByUsername( username);
    temp.setPassword(password);
    return temp;
  }

  @Override
  public List<MessageAudit> findAllMessages(){
    return messageRepository.findAll();
  }

  @Override
  public List<MessageAudit> findAllMessagesByTempID(String tempId){
    Date now = new Date();
    Date sevenDaysAgo = new Date(now.getTime() - (7 * 86400000));
    return messageRepository.findAllByRecipient(tempId, sevenDaysAgo.toString());
  }

  @Override
  public ResponseEntity<?> removeUser(UserRemoveRequest userRequest) {

    AppUser user = userRepository.findByUsernameEquals(userRequest.getUsername());
    if(user == null){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }else{
      userRepository.delete(user);
      return new ResponseEntity<>( HttpStatus.OK);
    }
  }

}

package com.alto.service.impl;

import com.alto.model.AppUser;
import com.alto.model.UserPreferences;
import com.alto.model.requests.AppUserRequest;
import com.alto.model.requests.PreferencesRequest;
import com.alto.model.response.TempResponse;
import com.alto.repository.AppUserRepository;
import com.alto.repository.UserPreferencesRepository;
import com.alto.service.AppUserService;
import com.alto.service.AuthorityService;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class AppUserServiceImpl implements AppUserService {

  @Autowired
  private AppUserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthorityService authService;

  @Autowired
  private UserPreferencesRepository userPreferencesRepository;

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
    AppUser u = userRepository.findByUsername(username);
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

    AppUser userRec = userRepository.findByUsername(user);

    if(userRec != null){
      userRec.setDevicetoken(token);
      userRepository.saveAndFlush(userRec);
    }

    return new ResponseEntity(BAD_REQUEST);
  }

  @Override
  public AppUser save(AppUserRequest userRequest) {

    if(userRequest.getUsername() == null){
      throw new ResponseStatusException(BAD_REQUEST);
    }
    AppUser user = new AppUser();
    AppUser exists = findByUsername(userRequest.getUsername().trim());
    if(exists != null){
      if(!passwordEncoder.matches(userRequest.getPassword().trim(), exists.getPassword() ) && !userRequest.getFirstTime() ){
        throw new ResponseStatusException(UNAUTHORIZED);
      }else if(userRequest.getFirstTime() ){
        if(StringUtils.isNotBlank(userRequest.getDevicetoken())) exists.setDevicetoken(userRequest.getDevicetoken());
        if(StringUtils.isNotBlank(userRequest.getDevicetype())) exists.setDevicetype(userRequest.getDevicetype());
        if(StringUtils.isNotBlank(userRequest.getPassword().trim())) exists.setPassword(userRequest.getPassword());
        userRepository.saveAndFlush(exists);
        return exists;
       }
      else{
        if(StringUtils.isNotBlank(userRequest.getDevicetoken())) exists.setDevicetoken(userRequest.getDevicetoken());
        if(StringUtils.isNotBlank(userRequest.getDevicetype())) exists.setDevicetype(userRequest.getDevicetype());
        userRepository.saveAndFlush(exists);
        return exists;
      }
    }
    userRequest = getTempByUsername(userRequest.getUsername(), userRequest.getPassword());
    AppUser existsCheck = userRepository.findByTempid(userRequest.getTempId());
    if(existsCheck != null){
      return existsCheck;
    }

    user.setUsername(userRequest.getUsername());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword().trim()));
    user.setFirstname(userRequest.getFirstname());
    user.setLastname(userRequest.getLastname());
    user.setTempid(userRequest.getTempId());
    user.setDevicetype(userRequest.getDevicetype());
    user.setCerts(userRequest.getCerts());

    return userRepository.saveAndFlush(user);
  }

  @Override
  public UserPreferences saveUserPrefs(PreferencesRequest request){

    UserPreferences prefs;

    prefs = userPreferencesRepository.findByTempid(request.getTempId());
    if(prefs == null) prefs = new UserPreferences();
    prefs.setTempid(request.getTempId());
    prefs.setUsername(request.getUsername());
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
    return userPreferencesRepository.findByTempid(Long.parseLong(tempid));
  }

  private AppUserRequest getTempByUsername(String username){

    TempResponse started = null;
    AppUserRequest userRequest = new AppUserRequest();
    userRequest.setUsername(username);

    //todo implement sessionkey
    //todo externalize
    //todo add is active
    String getTempUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getTemps&username=rsteele&password=altoApp1!&emailLike=$email&statusIn=Active&resultType=json";
    getTempUrl = getTempUrl.replace("$email", userRequest.getUsername().trim());
    try {

      RestTemplate restTemplate = new RestTemplateBuilder().build();
      HttpHeaders headers = new HttpHeaders();


      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTempUrl);
      HttpEntity<?> entity = new HttpEntity<>(headers);

        String result = restTemplate.getForObject(getTempUrl, String.class);
        result = result.replace("[","").replace("]","");

        System.out.println(result);

        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        started = gson.fromJson(result, TempResponse.class);
        if(started == null || started.getTempId() == null || started.getTempId().isEmpty()){
          throw new ResponseStatusException(BAD_REQUEST);
        }
        userRequest.setTempId(started.getTempId());
        userRequest.setFirstname(started.getFirstName());
        userRequest.setLastname(started.getLastName());
        userRequest.setCerts(started.getCertification());
        userRequest.setRegion(started.getHomeRegion());

        //HttpEntity<ShiftResponse> apiResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, ShiftResponse.class);
        // ShiftResponse apiResponse = restTemplate.getForObject(getShiftUrl, ShiftResponse.class);
        //started = apiResponse;

      } catch (Exception e) {
      e.printStackTrace();
        //LOGGER.error("Error getting Embed URL and Token", e);
      }

    return userRequest;
  }

  private AppUserRequest getTempByUsername(String username, String password){

    AppUserRequest temp = getTempByUsername( username);
    temp.setPassword(password);
    return temp;
  }

}

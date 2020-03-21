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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

  @PreAuthorize("hasRole('ADMIN')")
  public AppUser findById(Long id) throws AccessDeniedException {
    AppUser u = userRepository.getOne(id);
    return u;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<AppUser> findAll() throws AccessDeniedException {
    List<AppUser> result = userRepository.findAll();
    return result;
  }

  @Override
  public AppUser save(AppUserRequest userRequest) {

    AppUser user = new AppUser();
    AppUser exists = findByUsername(userRequest.getUsername().trim());
    if(exists != null){
      if(!passwordEncoder.matches(userRequest.getPassword().trim(), exists.getPassword() ) ){
        throw new ResponseStatusException(UNAUTHORIZED);
      }else{
        exists.setDevicetoken(userRequest.getDevicetoken());
        exists.setDevicetype(userRequest.getDevicetype());
        userRepository.saveAndFlush(exists);
        return exists;
      }
    }
    userRequest = getTempByUsername(userRequest);


    user.setUsername(userRequest.getUsername());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword().trim()));
    user.setFirstname(userRequest.getFirstname());
    user.setLastname(userRequest.getLastname());
    user.setTempid(userRequest.getTempId());
    user.setDevicetype(userRequest.getDevicetype());

    return userRepository.saveAndFlush(user);
  }

  @Override
  public UserPreferences saveUserPrefs(PreferencesRequest request){

    UserPreferences prefs = new UserPreferences();
    prefs.setTempid(request.getTempId());
    prefs.setUsername(request.getUsername());
    prefs.setMonday(request.getMon());
    prefs.setTuesday(request.getTue());
    prefs.setWednesday(request.getWed());
    prefs.setThursday(request.getThur());
    prefs.setFriday(request.getFri());
    prefs.setSaturday(request.getSat());
    prefs.setSunday(request.getSun());

    return userPreferencesRepository.saveAndFlush(prefs);

  }

  @Override
  public UserPreferences fetchUserPrefs(String tempid){
    return userPreferencesRepository.findByTempid(Long.parseLong(tempid));
  }

  private AppUserRequest getTempByUsername(AppUserRequest userRequest){

    TempResponse started = null;

    //todo implement sessionkey
    //todo externalize
    String getTempUrl = "https://ctms.contingenttalentmanagement.com/CirrusConcept/clearConnect/2_0/index.cfm?action=getTemps&username=rsteele&password=altoApp1!&emailLike=$email&resultType=json";
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

        //HttpEntity<ShiftResponse> apiResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, ShiftResponse.class);
        // ShiftResponse apiResponse = restTemplate.getForObject(getShiftUrl, ShiftResponse.class);
        //started = apiResponse;

      } catch (Exception e) {
        //LOGGER.error("Error getting Embed URL and Token", e);
      }

    return userRequest;
  }

}

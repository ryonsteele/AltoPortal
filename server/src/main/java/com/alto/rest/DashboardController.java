package com.alto.rest;

import com.alto.model.*;
import com.alto.repository.AppUserRepository;
import com.alto.service.AppUserService;
import com.alto.service.ShiftBoardService;
import com.alto.service.ShiftService;
import com.alto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@CrossOrigin
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class DashboardController {

  @Autowired
  private UserService userService;
  @Autowired
  private ShiftService shiftService;
  @Autowired
  private AppUserService appUserService;
  @Autowired
  private ShiftBoardService shiftBoardService;



  @RequestMapping(method = GET, value = "/temps")
  //@PreAuthorize("hasRole('USER')")
  public List<AppUser> loadAllAppUsers() {
    return this.appUserService.findAll();
  }

  @RequestMapping(method = GET, value = "/shifts")
  //@PreAuthorize("hasRole('USER')")
  public List<ShiftBoardRecord> loadAllOpenShifts() {
    return this.shiftBoardService.findAll();
  }

  @RequestMapping(method = POST, value = "/confirmation")
  //@PreAuthorize("hasRole('USER')")
  public ConfirmationRequest user(@RequestBody ConfirmationRequest request) {
    shiftBoardService.processConfirmation(request);
    return request;
  }


  /*
   * We are not using userService.findByUsername here(we could), so it is good that we are making
   * sure that the user has role "ROLE_USER" to access this endpoint.
   */
  @RequestMapping(method = POST, value = "/push")
  //@PreAuthorize("hasRole('USER')")
  public PushMessageRequest user(@RequestBody PushMessageRequest request) {
     shiftService.sendPushNotification(request);
     return request;
  }

}

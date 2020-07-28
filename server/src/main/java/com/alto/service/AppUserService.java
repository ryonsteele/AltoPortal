package com.alto.service;

import com.alto.model.UserPreferences;
import com.alto.model.requests.AppUserRequest;
import com.alto.model.AppUser;
import com.alto.model.requests.PreferencesRequest;
import com.alto.model.requests.ResetRequest;
import com.alto.model.requests.UserRemoveRequest;
import com.alto.model.response.MessageAudit;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface AppUserService {

  void resetCredentials();
  AppUser findById(Long id);

  AppUser findByUsername(String username);
  AppUser resetAppUser(ResetRequest request);
  List<AppUser> findAll();
  List<MessageAudit> findAllMessages();
  List<MessageAudit> findAllMessagesByTempID(String tempId);
  ResponseEntity<?> save(AppUserRequest user);
  ResponseEntity<?> updateToken(String tempid, String token);
  UserPreferences saveUserPrefs(PreferencesRequest request);
  UserPreferences fetchUserPrefs(String tempid);
  ResponseEntity<?> removeUser(UserRemoveRequest request);
}

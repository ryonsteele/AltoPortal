package com.alto.service;

import com.alto.model.UserPreferences;
import com.alto.model.requests.AppUserRequest;
import com.alto.model.AppUser;
import com.alto.model.requests.PreferencesRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface AppUserService {
  void resetCredentials();

  AppUser findById(Long id);

  AppUser findByUsername(String username);

  List<AppUser> findAll();

  AppUser save(AppUserRequest user);

  ResponseEntity<?> updateToken(String tempid, String token);

  UserPreferences saveUserPrefs(PreferencesRequest request);
  UserPreferences fetchUserPrefs(String tempid);
}

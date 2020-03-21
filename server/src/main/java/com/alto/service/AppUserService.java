package com.alto.service;

import com.alto.model.UserPreferences;
import com.alto.model.requests.AppUserRequest;
import com.alto.model.AppUser;
import com.alto.model.requests.PreferencesRequest;

import java.util.List;


public interface AppUserService {
  void resetCredentials();

  AppUser findById(Long id);

  AppUser findByUsername(String username);

  List<AppUser> findAll();

  AppUser save(AppUserRequest user);

  UserPreferences saveUserPrefs(PreferencesRequest request);
  UserPreferences fetchUserPrefs(String tempid);
}

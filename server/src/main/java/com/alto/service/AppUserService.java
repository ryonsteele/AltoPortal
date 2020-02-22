package com.alto.service;

import com.alto.model.AppUserRequest;
import com.alto.model.AppUser;

import java.util.List;


public interface AppUserService {
  void resetCredentials();

  AppUser findById(Long id);

  AppUser findByUsername(String username);

  List<AppUser> findAll();

  AppUser save(AppUserRequest user);
}

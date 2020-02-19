package com.bfwg.service;

import com.bfwg.model.AppUser;
import com.bfwg.model.AppUserRequest;
import com.bfwg.model.User;
import com.bfwg.model.UserRequest;

import java.util.List;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface AppUserService {
  void resetCredentials();

  AppUser findById(Long id);

  AppUser findByUsername(String username);

  List<AppUser> findAll();

  AppUser save(AppUserRequest user);
}

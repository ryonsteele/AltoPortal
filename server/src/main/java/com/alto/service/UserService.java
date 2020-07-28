package com.alto.service;

import java.util.List;

import com.alto.model.User;
import com.alto.model.requests.UserRemoveRequest;
import com.alto.model.requests.UserRequest;
import org.springframework.http.ResponseEntity;

/**
 * Created by fan.jin on 2016-10-15.
 */
public interface UserService {
  void resetCredentials();

  User findById(Long id);

  User findByUsername(String username);

  List<User> findAll();

  User save(UserRequest user);

  ResponseEntity<?> removeUser(UserRemoveRequest request);
}

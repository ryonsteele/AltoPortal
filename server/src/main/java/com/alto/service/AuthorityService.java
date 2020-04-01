package com.alto.service;

import java.util.List;

import com.alto.model.Authority;
import com.alto.model.UserRoleName;

public interface AuthorityService {
  List<Authority> findById(Long id);

  List<Authority> findByname(UserRoleName name);

}

package com.alto.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.alto.model.UserRoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alto.model.Authority;
import com.alto.repository.AuthorityRepository;
import com.alto.service.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService {

  @Autowired
  private AuthorityRepository authorityRepository;

  @Override
  public List<Authority> findById(Long id) {
    // TODO Auto-generated method stub

    Authority auth = this.authorityRepository.getOne(id);
    List<Authority> auths = new ArrayList<>();
    auths.add(auth);
    return auths;
  }

  @Override
  public List<Authority> findByname(UserRoleName name) {
    // TODO Auto-generated method stub
    Authority auth = this.authorityRepository.findByName(name);
    List<Authority> auths = new ArrayList<>();
    auths.add(auth);
    return auths;
  }

}

package com.alto.service.impl;

import java.util.List;

import com.alto.model.Authority;
import com.alto.model.User;
import com.alto.model.UserAuthority;
import com.alto.model.UserRoleName;
import com.alto.model.requests.UserRemoveRequest;
import com.alto.repository.UserAuthorityRepository;
import com.alto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.alto.model.requests.UserRequest;
import com.alto.service.AuthorityService;
import com.alto.service.UserService;


@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthorityService authService;

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  public void resetCredentials() {
    List<User> users = userRepository.findAll();
    for (User user : users) {
      user.setPassword(passwordEncoder.encode("123"));
      userRepository.save(user);
    }
  }

  @Override
  // @PreAuthorize("hasRole('USER')")
  public User findByUsername(String username) throws UsernameNotFoundException {
    User u = userRepository.findByUsername(username);
    return u;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public User findById(Long id) throws AccessDeniedException {
    User u = userRepository.getOne(id);
    return u;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<User> findAll() throws AccessDeniedException {
    List<User> result = userRepository.findAll();
    return result;
  }

  @Override
  public User save(UserRequest userRequest) {
    User user = new User();
    user.setUsername(userRequest.getUserName());
    user.setEmailId(userRequest.getEmail());
    user.setPassword(passwordEncoder.encode(userRequest.getNewPassword()));
    user.setFirstname(userRequest.getFirstName());
    user.setLastname(userRequest.getLastName());
    List<Authority> auth = authService.findByname(UserRoleName.ROLE_USER);
    user.setAuthorities(auth);
    this.userRepository.save(user);
    return user;
  }

  @Override
  public ResponseEntity<?> removeUser(UserRemoveRequest userRequest) {

    User user = userRepository.findByUsername(userRequest.getUsername());
    if(user == null){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }else{
      List<UserAuthority> auth = userAuthorityRepository.findAllByUserid(user.getId());
      if(auth == null){
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }else{
        for(UserAuthority authority : auth) {
          userAuthorityRepository.deleteById(authority.getId());
        }
        userAuthorityRepository.flush();
      }
      userRepository.deleteById(user.getId());
      userRepository.flush();
      return new ResponseEntity<>( HttpStatus.OK);
    }
  }

}

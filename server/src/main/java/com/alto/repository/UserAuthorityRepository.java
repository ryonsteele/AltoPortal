package com.alto.repository;

import com.alto.model.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
  UserAuthority findByUserid(Long name);
  List<UserAuthority> findAllByUserid(Long name);
}

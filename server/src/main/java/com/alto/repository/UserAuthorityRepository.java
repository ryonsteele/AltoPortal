package com.alto.repository;

import com.alto.model.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
  UserAuthority findByUserid(Long name);
}

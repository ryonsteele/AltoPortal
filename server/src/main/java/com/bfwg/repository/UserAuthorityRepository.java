package com.bfwg.repository;

import com.bfwg.model.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
  UserAuthority findByUserid(Long name);
}

package com.alto.repository;

import com.alto.model.Authority;
import com.alto.model.UserRoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
  Authority findByName(UserRoleName name);
}

package com.alto.repository;

import com.alto.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    AppUser findByTempid(String tempid);
}


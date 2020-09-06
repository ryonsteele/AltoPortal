package com.alto.repository;

import com.alto.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    UserPreferences findTopByTempid(Long tempid);
}


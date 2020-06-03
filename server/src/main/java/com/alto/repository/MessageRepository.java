package com.alto.repository;

import com.alto.model.AppUser;
import com.alto.model.response.MessageAudit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<MessageAudit, Long> {

}


package com.alto.repository;

import com.alto.model.AppUser;
import com.alto.model.response.MessageAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MessageRepository extends JpaRepository<MessageAudit, Long> {

    @Query("from MessageAudit m where m.recipient = :tempId and m.time >= :time")
    List<MessageAudit> findAllByRecipient(String tempId, String time);

}


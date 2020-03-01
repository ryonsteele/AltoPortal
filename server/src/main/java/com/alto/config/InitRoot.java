package com.alto.config;


import com.alto.model.*;
import com.alto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class InitRoot {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    UserAuthorityRepository userAuthorityRepository;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    ShiftBoardRepository shiftBoardRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {

        //INSERT INTO user (id, username, password, firstname, lastname) VALUES (1, 'user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Fan', 'Jin');
        // INSERT INTO user (id, username, password, firstname, lastname) VALUES (2, 'admin', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Jing', 'Xiao');
        // INSERT INTO authority (id, name) VALUES (1, 'ROLE_USER');
        // INSERT INTO authority (id, name) VALUES (2, 'ROLE_ADMIN');

        // INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
        // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 1);
        // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);

        userRepository.saveAndFlush(new User("Test","User", "user@test.com", "user", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_USER"));
        userRepository.saveAndFlush(new User("Ryon","Steele", "admin@test.com", "admin", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_ADMIN"));
        authorityRepository.saveAndFlush(new Authority(1L,  UserRoleName.ROLE_USER));
        authorityRepository.saveAndFlush(new Authority(2L,  UserRoleName.ROLE_ADMIN));
        userAuthorityRepository.saveAndFlush(new UserAuthority(1L,1L));
        userAuthorityRepository.saveAndFlush(new UserAuthority(2L,1L));
        userAuthorityRepository.saveAndFlush(new UserAuthority(2L,2L));


        appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "9919", "abc123", "Android"));
        appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "736", "abc123", "Android"));

        shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true ));

    }
}

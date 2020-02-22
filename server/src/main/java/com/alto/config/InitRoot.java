package com.alto.config;


import com.alto.model.Authority;
import com.alto.model.User;
import com.alto.model.UserAuthority;
import com.alto.model.UserRoleName;
import com.alto.repository.AuthorityRepository;
import com.alto.repository.UserRepository;
import com.alto.repository.UserAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitRoot {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    UserAuthorityRepository userAuthorityRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {

        //INSERT INTO user (id, username, password, firstname, lastname) VALUES (1, 'user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Fan', 'Jin');
        // INSERT INTO user (id, username, password, firstname, lastname) VALUES (2, 'admin', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Jing', 'Xiao');
        // INSERT INTO authority (id, name) VALUES (1, 'ROLE_USER');
        // INSERT INTO authority (id, name) VALUES (2, 'ROLE_ADMIN');

        // INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
        // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 1);
        // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);

        userRepository.saveAndFlush(new User("Fan","Jin", "user@test.com", "user", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_USER"));
        userRepository.saveAndFlush(new User("Jing","Xiao", "admin@test.com", "admin", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_ADMIN"));
        authorityRepository.saveAndFlush(new Authority(1L,  UserRoleName.ROLE_USER));
        authorityRepository.saveAndFlush(new Authority(2L,  UserRoleName.ROLE_ADMIN));
        userAuthorityRepository.saveAndFlush(new UserAuthority(1L,1L));
        userAuthorityRepository.saveAndFlush(new UserAuthority(2L,1L));
        userAuthorityRepository.saveAndFlush(new UserAuthority(2L,2L));

    }
}

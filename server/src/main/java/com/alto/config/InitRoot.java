package com.alto.config;


import com.alto.model.*;
import com.alto.repository.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

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
    @Autowired
    ShiftRepository shiftRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://alto-api.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);

            //INSERT INTO user (id, username, password, firstname, lastname) VALUES (1, 'user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Fan', 'Jin');
            // INSERT INTO user (id, username, password, firstname, lastname) VALUES (2, 'admin', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Jing', 'Xiao');
            // INSERT INTO authority (id, name) VALUES (1, 'ROLE_USER');
            // INSERT INTO authority (id, name) VALUES (2, 'ROLE_ADMIN');

            // INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
            // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 1);
            // INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);

            User admin =userRepository.findByUsername("admin");
            if(admin == null) {
                userRepository.saveAndFlush(new User("Test", "User", "user@test.com", "user", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_USER"));
                userRepository.saveAndFlush(new User("Leslie", "Khan", "admin@test.com", "admin", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "ROLE_ADMIN"));
                authorityRepository.saveAndFlush(new Authority(1L, UserRoleName.ROLE_USER));
                authorityRepository.saveAndFlush(new Authority(2L, UserRoleName.ROLE_ADMIN));
                userAuthorityRepository.saveAndFlush(new UserAuthority(1L, 1L));
                userAuthorityRepository.saveAndFlush(new UserAuthority(2L, 1L));
                userAuthorityRepository.saveAndFlush(new UserAuthority(2L, 2L));
            }


//            appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "9919", "abc123", "Android", "STNA"));
//            appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "736", "abc123", "Android", "LPN"));
//            appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "1234", "abc123", "Android", "STNA"));
//            appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "5678", "abc123", "Android", "LPN"));
//            appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "4321", "abc123", "Android", "STNA"));
//            appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "8765", "abc123", "Android", "LPN"));
//            appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "2323", "abc123", "Android", "STNA"));
//            appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "5656", "abc123", "Android", "LPN"));
//            appUserRepository.saveAndFlush(new AppUser("John", "Doe", "test@gmail.com", "1234", "3232", "abc123", "Android", "STNA"));
//            appUserRepository.saveAndFlush(new AppUser("Jane", "Smith", "sophiaslc1977@aol.com", "1234", "6565", "abc123", "Android", "LPN"));
//
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));
//            shiftBoardRepository.saveAndFlush(new ShiftBoardRecord("435848", "sophiaslc1977@aol.com", "Sophia Webb", "736", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "Allen View Nursing Home", false, true));

            //shiftRepository.saveAndFlush(new Shift("123456", "testy@yahoo.com", "10943", "filled", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "signoff", "signoff2", "555", "client name", "order spec", "order cert", "222", "note for shift", "123 main st", "39.477285", "-84.477811", "123 main st", "39.477285", "-84.477811", true));

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

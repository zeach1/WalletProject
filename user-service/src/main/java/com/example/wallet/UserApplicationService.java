package com.example.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserApplicationService implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UserApplicationService.class, args);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User txnServiceUser = User.builder()
                .phoneNumber("txn_Service")
                .password(passwordEncoder.encode("txn123"))
                .authorities(UserConstants.SERVICE_AUTHORITY)
                .email("txn@gmail.com")
                .userIdentifier(UserIdentifier.SERVICE_ID)
                .identifierValue("txn123")
                .build();
        userRepository.save(txnServiceUser);
    }
}

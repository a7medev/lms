package com.lms.seed;

import com.lms.auth.AuthenticationService;
import com.lms.auth.RegisterRequest;
import com.lms.user.Role;
import com.lms.user.User;
import com.lms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public AdminInitializer(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        boolean hasAdmin = userRepository.existsUserByRole(Role.ADMIN);

        if (hasAdmin) {
            return;
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Admin")
                .email("admin@lms.com")
                .password("test1234")
                .birthdate(LocalDateTime.of(1973, Month.OCTOBER, 6, 14, 0))
                .phone("01023456789")
                .role(Role.ADMIN)
                .build();

        User user = authenticationService.createUser(registerRequest, true);

        userRepository.save(user);
    }

}

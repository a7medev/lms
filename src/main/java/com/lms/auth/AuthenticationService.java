package com.lms.auth;

import com.lms.config.JwtService;
import com.lms.user.Role;
import com.lms.user.User;
import com.lms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = createUser(request, false);

        if (request.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only students can register.");
        }

        userRepository.save(user);

        var token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       request.getEmail(),
                       request.getPassword()
               )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse add(RegisterRequest request) {
        var user = createUser(request, true);
        userRepository.save(user);

        var token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public User createUser(RegisterRequest request, boolean isActive) {
        var password = passwordEncoder.encode(request.getPassword());
        var localBirthdate = request.getBirthdate().atZone(ZoneId.systemDefault()).toInstant();
        var birthdate = Date.from(localBirthdate);

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(password)
                .phone(request.getPhone())
                .birthdate(birthdate)
                .role(request.getRole())
                .isActive(isActive)
                .build();
    }
}

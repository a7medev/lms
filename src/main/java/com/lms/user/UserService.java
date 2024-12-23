package com.lms.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.lms.util.AuthUtils.principalToUser;

@Service
@RequiredArgsConstructor
public class UserService {

    public ResponseEntity<String> editUserByAdmin(AdminEditRequest request) {
        Optional<User> entity = userRepository.findByEmail(request.getEmail());
        if(entity.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!!");
        }

        var user = entity.get();

        if (request.getActive() != null) {
            boolean isActive = request.getActive();
            user.setActive(isActive);
        }

        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            user.setRole(role);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Role!!");
        }

        return ResponseEntity.ok("Changed Role Successfully!!");
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(Principal principal) {
        return principalToUser(principal);
    }

    public void editUserDetails(EditRequest request, Principal currentUser) {
        var user = principalToUser(currentUser);

        var name = getValue(user.getName(), request.getName());
        var email = getValue(user.getEmail(), request.getEmail());
        var phone = getValue(user.getPhone(), request.getPhone());
        var password = getPassword(user.getPassword(), request.getPassword());
        var birthdate = getBirthdate(user.getBirthdate(), request.getBirthdate());

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setBirthdate(birthdate);

        userRepository.save(user);
    }

    private String getValue(String previousValue, String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            return previousValue;
        }

        return newValue;
    }

    private LocalDateTime getBirthdate(LocalDateTime previousBirthdate, LocalDateTime newBirthdate) {
        if (newBirthdate == null) {
            return previousBirthdate;
        }
        return newBirthdate;
    }

    private String getPassword(String oldPassword, String newPassword) {
        if(newPassword == null || newPassword.isEmpty()) {
            return oldPassword;
        }
        return passwordEncoder.encode(newPassword);
    }

}

package com.lms.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(Principal currentUser) {
        return (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
    }

    public void editUserDetails(EditRequest request, Principal currentUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

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

    private Date getBirthdate(Date previousBirthdate, LocalDateTime newBirthdate) {
        if (newBirthdate == null) {
            return previousBirthdate;
        }
        var localBirthdate = newBirthdate.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(localBirthdate);
    }

    private String getPassword(String oldPassword, String newPassword) {
        if(newPassword == null || newPassword.isEmpty()) {
            return oldPassword;
        }
        return passwordEncoder.encode(newPassword);
    }
}

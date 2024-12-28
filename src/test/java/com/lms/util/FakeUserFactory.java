package com.lms.util;

import com.lms.user.Role;
import com.lms.user.User;

import java.time.LocalDateTime;
import java.time.Month;

public class FakeUserFactory {
    public static long currentId = 1;

    public static User createFakeUser(Role role) {
        return createFakeUser(role, -1);
    }

    public static User createFakeUser(Role role, long id) {
        var userId = id == -1 ? currentId++ : id;

        return User.builder()
                .id(userId)
                .name("User " + userId)
                .email("user" + userId + "@lms.com")
                .password("test1234")
                .birthdate(LocalDateTime.of(1973, Month.OCTOBER, 6, 14, 0))
                .phone("01023456789")
                .role(role)
                .build();
    }
}

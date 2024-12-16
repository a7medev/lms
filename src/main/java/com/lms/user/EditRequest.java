package com.lms.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private LocalDateTime birthdate;
}

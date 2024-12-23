package com.lms.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        String email = "current_user@example.com";
        user = User
                .builder()
                .name("Old Name")
                .email(email)
                .phone("0987654321")
                .password("oldPassword")
                .birthdate(LocalDateTime.now())
                .role(Role.STUDENT)
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    @Test
    public void givenUserEditUserDetailsEditsSuccessfully() throws Exception {
        String email = "new_user@example.com";
        String password = "newPassword";
        EditRequest editRequest = EditRequest
                .builder()
                .email(email)
                .name("New Name")
                .phone("1234567890")
                .password(password)
                .birthdate(LocalDateTime.now())
                .build();

        UsernamePasswordAuthenticationToken mockPrincipal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        Mockito.when(mockPrincipal.getPrincipal()).thenReturn(user);


        mockMvc.perform(put("/users/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User Details Edited Successfully!!"));

        User updatedUser = userRepository.findByEmail(email).orElseThrow();

        boolean result = passwordEncoder.matches(password, updatedUser.getPassword());

        assertEquals(email, updatedUser.getEmail());
        assertEquals("New Name", updatedUser.getName());
        assertEquals("1234567890", updatedUser.getPhone());
        assertEquals(updatedUser.getBirthdate().toLocalDate(), editRequest.getBirthdate().toLocalDate());
        assertTrue(result);
    }
}
package com.lms.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.config.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    public void givenValidUserThenAuthenticateReturnsToken() throws Exception {
        String email = "admin@lms.com";
        String password = "test1234";

        AuthenticationRequest authRequest = new AuthenticationRequest(email, password);
        AuthenticationResponse authResponse = new AuthenticationResponse("token");

        Mockito.when(authenticationService.authenticate(authRequest)).thenReturn(authResponse);

        MvcResult result = mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        authResponse = objectMapper.readValue(responseContent, AuthenticationResponse.class);

        assertNotNull(authResponse.getToken());
    }

    @Test
    public void givenInvalidUserThenAuthenticateReturnBadRequest() throws Exception {
        String email = "test1@gmail.com";
        String password = "test1234";

        AuthenticationRequest authRequest = new AuthenticationRequest(email, password);

        Mockito.when(authenticationService.authenticate(authRequest)).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password")
        );

        mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        result -> {
                            ResponseStatusException ex = (ResponseStatusException) result.getResolvedException();
                            assert ex != null;
                            assertEquals("Invalid email or password", ex.getReason()); // Verify the error message
                        }
                )
                .andReturn();
    }
}
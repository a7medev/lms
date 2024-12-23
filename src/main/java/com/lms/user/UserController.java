package com.lms.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Principal currentUser) {
        return ResponseEntity.ok(userService.getUser(currentUser));
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editCurrentUserDetails(@RequestBody EditRequest request, Principal currentUser) {
        userService.editUserDetails(request, currentUser);
        return ResponseEntity.ok("User Details Edited Successfully!!");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/edit")
    public ResponseEntity<String> editUserRole(@RequestBody @Validated AdminEditRequest request) {
        return userService.editUserByAdmin(request);
    }
}

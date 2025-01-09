package com.pss.pss_backend.controller;

import com.pss.pss_backend.dto.UserProfileDTO;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(Principal principal) {
        String username = principal.getName();
        Optional<com.pss.pss_backend.model.User> userOptional = userService.getUserByUsername(username);

        if (userOptional.isPresent()) {
            com.pss.pss_backend.model.User user = userOptional.get();
            UserProfileDTO userProfileDTO = userService.toUserProfileDTO(user);
            return ResponseEntity.ok(userProfileDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PutMapping(value = "/profile", consumes={"application/json"})
    public ResponseEntity<?> updateUserProfile(@RequestBody User user, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        currentUser.setFullName(user.getFullName());
        currentUser.setEmail(user.getEmail());
        currentUser.setRole(user.getRole());

        User updatedUser = userService.saveUser(currentUser);
        return ResponseEntity.ok(updatedUser);
    }




}

package com.meetwise.spring_shield.controller;

import com.meetwise.spring_shield.dtos.LoginRequest;
import com.meetwise.spring_shield.dtos.RegisterUserRequest;
import com.meetwise.spring_shield.jwt.JwtUtils;
import com.meetwise.spring_shield.models.Users;
import com.meetwise.spring_shield.services.CustomUserDetailsService;
import com.meetwise.spring_shield.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    @PostMapping("/ ")
    public ResponseEntity<RegisterUserRequest> createUser(@Valid @RequestBody RegisterUserRequest user)
    {
        System.out.println("Cotroller"+user);
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            userService.login(request.getUsername(), request.getPassword());
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
//            Flow:
//            UserController
//              → CustomUserDetailsService.loadUserByUsername(username)
//                  → userRepositorys.findByUsername(username)  // Finds user in database
//                      → if found: returns User entity
//                      → if not found: throws UsernameNotFoundException
//                  → new CustomUserDetails(user)  // Converts User to UserDetails


            String jwt = jwtUtils.generateTokenFrom(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("username", userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/getuser/{username}")
    public ResponseEntity<Users> getUser(@PathVariable String username)
    {
        Users user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/test-admin")
    public String test()
    {
        return "test-admin worked";
    }

    @GetMapping("/test-user")
    public String test2()
    {
        return "test-user worked";
    }

    @GetMapping("/test-user-admin")
    public String test3()
    {
        return "test-user-admin worked";
    }
}

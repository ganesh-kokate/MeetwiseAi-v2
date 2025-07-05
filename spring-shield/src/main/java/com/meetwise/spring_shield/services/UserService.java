package com.meetwise.spring_shield.services;

import com.meetwise.spring_shield.dtos.RegisterUserRequest;
import com.meetwise.spring_shield.exceptionhandling.UserNotFoundException;
import com.meetwise.spring_shield.models.Role;
import com.meetwise.spring_shield.models.Users;
import com.meetwise.spring_shield.repos.RoleRepository;
import com.meetwise.spring_shield.repos.UserRepositorys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositorys userRepositorys;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
   public Users createUser(RegisterUserRequest request)
   {
       System.out.println("PRinting roles"+request);
       Users user =convertDtoToEntity(request);
       System.out.println("PRinting roles"+user);
       user.setPassword(passwordEncoder.encode(user.getPassword()));

       return userRepositorys.save(user);
   }

   public Users getUser(String username)
   {
       Optional<Users> tempuser=  userRepositorys.findByUsername(username);
       if (tempuser.isPresent()) {
           return tempuser.get();
       } else {
           throw new UserNotFoundException(username);
       }
   }

    public void login(String username, String password) {
        System.out.println("authtoken ------------>");
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authInputToken);

        System.out.println("authtoken ------------>"+authInputToken);

        //When authenticationManager.authenticate(authInputToken) is called in the login method, here's what happens
//        Login Flow:
//        1. UserService.login(username, password)
//         → authenticationManager.authenticate()
//           → DaoAuthenticationProvider (this bean)
//            → userDetailsService.loadUserByUsername(username)  // Gets user from DB
//            → passwordEncoder.matches(rawPassword, encodedPassword)  // Checks password
    }

    public Users convertDtoToEntity(RegisterUserRequest request) {
        Users user = new Users();

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setMailId(request.getMailId());
        user.setMobileNumber(request.getMobileNumber());

        // Convert role names to Role entities
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        return user;
    }


}

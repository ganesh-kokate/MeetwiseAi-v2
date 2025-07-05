package com.meetwise.spring_shield.repos;

import com.meetwise.spring_shield.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositorys extends JpaRepository<Users,Integer> {
    Optional<Users> findByUsername(String username);
  //  Optional<Users> findByEmail(String email);
}

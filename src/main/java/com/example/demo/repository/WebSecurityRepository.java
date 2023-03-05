package com.example.demo.repository;

import com.example.demo.entity.AuthLogin;
import com.example.demo.entity.UserRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSecurityRepository extends JpaRepository<AuthLogin,String> {
    AuthLogin findByEmail(String email);
}

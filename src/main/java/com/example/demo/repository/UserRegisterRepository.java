package com.example.demo.repository;

import com.example.demo.entity.UserRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRegisterRepository extends JpaRepository<UserRegister,String> {
      List<UserRegister> findAllByOrderByEmailAsc();
      List<UserRegister> findByEmail(String email);
}

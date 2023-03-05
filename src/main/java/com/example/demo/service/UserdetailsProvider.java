package com.example.demo.service;

import com.example.demo.entity.AuthLogin;
import com.example.demo.repository.WebSecurityRepository;
import com.example.demo.websecurity.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserdetailsProvider implements UserDetailsService {
    @Autowired
    WebSecurityRepository registerRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      AuthLogin login=registerRepository.findByEmail(email);
      if(login==null){
          throw  new UsernameNotFoundException("Username not found");
      }
        return new CustomUserDetails(login);
    }


}

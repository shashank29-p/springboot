package com.example.demo.websecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity extends SecurityConfiguration {
   @Autowired
    private UserDetailsService userDetailsService;
   @Bean
    AuthenticationProvider authenticationProvider(){
       DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
       provider.setUserDetailsService(userDetailsService);
       provider.setPasswordEncoder(new BCryptPasswordEncoder());
       return provider;
   }

    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeHttpRequests().antMatchers("/auth").
                authenticated().anyRequest().authenticated().and()
                .formLogin().loginPage("/login").and().httpBasic()
                .and().logout().logoutUrl("/auth/logout").logoutSuccessUrl("/");
        return http.build();
    }

}

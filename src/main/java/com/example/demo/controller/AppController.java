package com.example.demo.controller;

import com.example.demo.configkeys.Constants;
import com.example.demo.entity.AuthLogin;
import com.example.demo.entity.Email;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRegister;
import com.example.demo.response.Response;
import com.example.demo.service.UserService;
import com.google.common.io.BaseEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AppController {

    @Autowired
    private UserService userservice;

    @Autowired
    private JavaMailSender javaMailSender;
    String code;

    @GetMapping(value = "/index")
    public ModelAndView hello() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @GetMapping(value = "/getUsers")
    public List<User> getUser(User user) {
        return userservice.listAll();
    }

    @PostMapping(value = "/postUser")
    public Response postUser(@RequestBody User user) {
        userservice.save(user);
        Response response = new Response(Constants.USER_ADDED, HttpStatus.OK, Constants.APPLICATION_JSON);
        return response;
    }

    @GetMapping(value = "/getUser/{userid}")
    public ResponseEntity<User> get(@PathVariable Integer userid) {
        try {
            User user = userservice.get(userid);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/putUser/{id}")
    public Response update(@RequestBody User user, @PathVariable Integer id) {
        try {
            if (userservice.get(id) != null) {
                User userToUpdate = userservice.get(id);
                userToUpdate.setName(user.getName());
                userToUpdate.setCity(user.getCity());
                userservice.save(userToUpdate);
                return new Response(Constants.USER_UPDATED, HttpStatus.OK, Constants.APPLICATION_JSON);
            } else {
                return new Response(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND, Constants.APPLICATION_JSON);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public Response delete(@PathVariable Integer id) {
        try {
            if (userservice.get(id) != null) {
                userservice.delete(id);
                return new Response(Constants.USER_DELETED, HttpStatus.OK, Constants.APPLICATION_JSON);
            } else {
                return new Response(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND, Constants.APPLICATION_JSON);
            }
        } catch (NoSuchElementException e) {
            return new Response(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND, Constants.APPLICATION_JSON);
        }
    }

    @DeleteMapping(value = "/deleteAll")
    public Response deleteAll(User user) {
        userservice.deleteAll(user);
        return new Response(Constants.USER_DELETED, HttpStatus.OK, Constants.APPLICATION_JSON);
    }

    @GetMapping(value = "/listUserByEmail")
    public List<UserRegister> userRegisterList() {
        return userservice.findByEmailOrderByEmailAsc();
    }

    @PostMapping(value = "/register" , produces = MediaType.ALL_VALUE
            ,  consumes = MediaType.ALL_VALUE)
    public Response register(@RequestBody UserRegister userRegister) {
        if (userRegister != null) {
            if (userservice.isEmailpresent(userRegister.getEmail()) == true) {
                return new Response(Constants.USER_ALREADY_REGISTERED,  HttpStatus.BAD_REQUEST, Constants.APPLICATION_JSON);
            }
            String password = String.valueOf(userRegister.getPassword());
            try {
                String encodedPass = BaseEncoding.base64().encode(password.getBytes(StandardCharsets.UTF_8));
                //   String encodedPass=bCryptPasswordEncoder.encode(password);
                userRegister.setPassword(encodedPass);
                userservice.register(userRegister);
                return new Response(Constants.USER_REGISTERED, HttpStatus.OK, Constants.APPLICATION_JSON);
            } catch (Exception e) {
                return new Response("Failed to encrypt", HttpStatus.INTERNAL_SERVER_ERROR, Constants.APPLICATION_JSON);
            }
        } else {
            return new Response(Constants.USER_REGISTER_FAILED, HttpStatus.BAD_REQUEST, Constants.APPLICATION_JSON);
        }
    }

    @GetMapping(value = "/register")
    public ModelAndView register(){
        ModelAndView modelAndView= new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping(value = "/login")
    public Response userLogin(@RequestBody AuthLogin authLogin) {
        List<UserRegister> userRegisters = userservice.login(authLogin);
        String usrname = null;
        String pass = null;
        for (UserRegister user : userRegisters) {
            usrname = user.getEmail();
            pass = user.getPassword();
            try {
                byte[] decodePass = BaseEncoding.base64().decode(pass);
                if (usrname.equals(authLogin.getEmail()) && new String(decodePass, "UTF-8").equals(authLogin.getPassword())) {
                    return new Response(Constants.USER_LOGIN_SUCCESS, HttpStatus.OK, Constants.APPLICATION_JSON);
                } else {
                    return new Response(Constants.USER_LOGIN_FAILURE,  HttpStatus.BAD_REQUEST, Constants.APPLICATION_JSON);
                }
            } catch (Exception e) {
                return new Response(Constants.USER_LOGIN_FAILURE, HttpStatus.BAD_REQUEST, Constants.APPLICATION_JSON);
            }
        }
        return new Response(Constants.USER_LOGIN_SUCCESS, HttpStatus.OK, Constants.APPLICATION_JSON);
    }
    @GetMapping(value = "/login")
    public ModelAndView login(){
        ModelAndView modelAndView= new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping(value = "/send/mail")
    public Response sendEmail(@RequestBody Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getEmail().stripTrailing().stripTrailing());
        Random random = new Random();
        code = String.format("%04d", random.nextInt(10000));
        message.setSubject("Authentication code");
        message.setText("Your code is : " + code);
        try {
            javaMailSender.send(message);
            return new Response("Mail sent successfully", HttpStatus.OK, Constants.APPLICATION_JSON);
        } catch (Exception e) {
            return new Response("Error while sending mail", HttpStatus.BAD_GATEWAY, Constants.APPLICATION_JSON);
        }
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }
}

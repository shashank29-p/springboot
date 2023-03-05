package com.example.demo.service;

import com.example.demo.entity.AuthLogin;
import com.example.demo.entity.Email;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRegister;
import com.example.demo.repository.Repository;
import com.example.demo.repository.UserRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private Repository repository;

    @Autowired
    public UserRegisterRepository registerRepository;

    public List<User> listAll() {
        return repository.findAll();
    }

    public void save(User user) {
        repository.save(user);
    }

    public User get(Integer id) {
       return  repository.findById(id).orElseThrow();
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public void deleteAll(User user) {
        repository.deleteAll();
    }

    public void register(UserRegister userRegister) {
        registerRepository.save(userRegister);
    }

    public List<UserRegister> login(AuthLogin authLogin) {
        List<UserRegister> userList =new ArrayList<>();
        registerRepository.findAll().forEach(userList::add);
        return userList;
    }

    public List<UserRegister> findByEmailOrderByEmailAsc(){
        List<UserRegister> userRegisters= new LinkedList<>();
        userRegisters= registerRepository.findAll(Sort.sort(Email.class));
        return userRegisters;
    }

    public boolean isEmailpresent(String email){
        List<UserRegister> userRegisterList= registerRepository.findAll();
        String emailFromDb = null;
        for(UserRegister userRegister:userRegisterList){
            emailFromDb=userRegister.getEmail();
            if(email.equals(emailFromDb)){
                return true;
            }
        }
        return false;
    }

}
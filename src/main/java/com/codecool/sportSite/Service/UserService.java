package com.codecool.sportSite.Service;

import com.codecool.sportSite.Model.User;
import com.codecool.sportSite.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    public void register(Map<String, String> requestParams){
        String username = requestParams.get("username");
        String password = requestParams.get("password");
        String email = requestParams.get("email");
        String firstname = requestParams.get("firstname");
        String lastname = requestParams.get("lastname");
        try {
            if(username.length() > 4 || password.length() > 4) {
                User newUser = new User(firstname, lastname, username, password, email);
                userRepository.save(newUser);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void login(Map<String, String> requestParams, HttpSession session) {
        String username = requestParams.get("username");
        String password = requestParams.get("password");
        try {
            User result = userRepository.findByUsername(username);
            String id = String.valueOf(result.getId());
            if (result.getUsername().equals(username) && result.getPassword().equals(password)) {
                session.setAttribute("userId", id);
            }
        } catch (javax.persistence.NoResultException e) {
            e.printStackTrace();
        }
    }


    public boolean userExists(String username){
        try {
            User result = userRepository.findByUsername(username);
            return false;
        } catch (javax.persistence.NoResultException e){
            System.out.println(e);
            return true;
        }
    }

    public void logoutUser(HttpSession session) {
        session.removeAttribute("userId");
    }

    public int getUserId(HttpSession session) {
        int userId;
        try {
            userId = Integer.parseInt(session.getAttribute("userId").toString());
        } catch (NullPointerException | NumberFormatException e) {
            userId = -1;
        }
        return userId;
    }

    public User getUserById(Long id){
        return userRepository.findOne(id);

    }
}

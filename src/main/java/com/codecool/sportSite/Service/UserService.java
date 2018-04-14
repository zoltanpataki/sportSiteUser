package com.codecool.sportSite.Service;

import com.codecool.sportSite.Model.User;
import com.codecool.sportSite.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    public void register(String userJson){
        JSONObject jsonObject = new JSONObject(userJson);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        String firstname = jsonObject.getString("firstname");
        String lastname = jsonObject.getString("lastname");
        try {
            if(username.length() > 4 || password.length() > 4) {
                User newUser = new User(firstname, lastname, email, username, password);
                userRepository.save(newUser);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String login(Map<String, String> requestParams, HttpSession session) {
        String username = requestParams.get("username");
        String password = requestParams.get("password");
        User result = userRepository.findByUsername(username);
        if (result == null) {
            return "no such user";
        }
        String id = String.valueOf(result.getId());
        if (result.getUsername().equals(username) && result.getPassword().equals(password)) {
            session.setAttribute("userId", id);
        }
        return "Successful login"; // ENUM
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

    public JSONObject getJson(String url) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String jsonText = restTemplate.getForEntity(url, String.class).getBody();
        return new JSONObject(jsonText);
    }
}

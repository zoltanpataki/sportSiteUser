package com.codecool.sportSite.Controller;

import com.auth0.SessionUtils;
import com.codecool.sportSite.Service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, String> register(@RequestBody String userJson) throws NoSuchProviderException, NoSuchAlgorithmException{
        Map<String, String> userResponse = new HashMap<>();
        if (userJson.contains("sub")){
            userResponse = userService.auth0Register(userJson);
            return userResponse;
        } else {
            boolean resultOfregistration = userService.register(userJson);
            if (resultOfregistration){
                userResponse.put("success", "Success registration");
                return userResponse;
            } else {
                userResponse.put("fail", "Registration failed");
                return userResponse;
            }

        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, String> login(@RequestBody String loginJson){
        return userService.login(loginJson);
    }


}

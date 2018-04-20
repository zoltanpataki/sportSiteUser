package com.codecool.sportSite.Controller;

import com.codecool.sportSite.Service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestBody String userJson) throws NoSuchProviderException, NoSuchAlgorithmException{
        userService.register(userJson);
        return "Success registration";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody String loginJson, HttpSession session){
        String result = userService.login(loginJson, session);
        if (result.equals("Successful login")) {
            return "Successful login";
        }
        return "Login failed: " + result;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpSession session) {
        userService.logoutUser(session);
        return new ResponseEntity<>("Success logout", HttpStatus.OK);
    }
}

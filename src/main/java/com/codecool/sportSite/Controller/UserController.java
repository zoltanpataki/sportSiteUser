package com.codecool.sportSite.Controller;

import com.codecool.sportSite.Service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestBody String userJson) {
        userService.register(userJson);
        return "Success registration";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam Map<String, String> reqPar, HttpSession session){
        String result = userService.login(reqPar, session);
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

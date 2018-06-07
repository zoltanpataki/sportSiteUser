package com.codecool.sportSite.Service;

import com.codecool.sportSite.Model.User;
import com.codecool.sportSite.Repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    public boolean register(String userJson) throws NoSuchProviderException, NoSuchAlgorithmException{
        byte[] salt = generateSalt();
        System.out.println(userJson);
        JSONObject jsonObject = new JSONObject(userJson);
        String username = jsonObject.getString("username");
        String password = getSecurePassword(jsonObject.getString("password"), salt);
        String email = jsonObject.getString("email");
        String firstname = jsonObject.getString("firstname");
        String lastname = jsonObject.getString("lastname");
        String picture = "no picture";
        try {
            if(username.length() > 4 && jsonObject.getString("password").length() > 4 &&
                    firstname.length() > 4 && lastname.length() > 4 && email.contains("@")) {
                User newUser = new User(firstname, lastname, email, username, password, salt, picture);
                userRepository.save(newUser);
                return true;
            } else {
                throw new IllegalArgumentException("Registration failed!");
            }
        } catch (IllegalArgumentException e) {
            e.getMessage();
            return false;
        }


    }

    public Map<String, String> auth0Register(String userJson) throws NoSuchProviderException, NoSuchAlgorithmException{
        byte[] salt = generateSalt();
        Map<String, String> userMap = new HashMap<>();
        System.out.println(userJson);
        JSONObject jsonObject = new JSONObject(userJson);
        String firstname = jsonObject.getString("given_name");
        String lastname = jsonObject.getString("family_name");
        String picture = jsonObject.getString("picture");
        String username = jsonObject.getString("nickname");
        String email = "true";
        String password = "no need";
        if (userRepository.findByUsername(username) == null){
            User newUser = new User(firstname, lastname, email, username, password, salt, picture);
            userMap.put("username", username);
            userMap.put("userpicture", picture);
            userRepository.save(newUser);
        } else {
            userMap.put("login", "Already registered user!");
            userMap.put("username", username);
            userMap.put("userpicture", picture);
        }
        return userMap;
    }

    public Map<String, String> login(String loginJson) {
        Map<String, String> userMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject(loginJson);
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        User result = userRepository.findByEmail(email);
        if (result == null){
            userMap.put("fail", "No such user!");
            return userMap;
        } else if (result.getPassword().equals(getSecurePassword(password, result.getSalt()))){
            userMap.put("success", "Success registration");
            return userMap;
        }
        userMap.put("fail", "Unauthorised user!");
        return userMap;
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

    private byte[] generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        final Random r = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        r.nextBytes(salt);
        return salt;
    }

    private static String getSecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public Map<String, String> getUserInfo(String url, String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String value = result.getBody();
        value = StringUtils.substringBetween(value, "{", "}");
        String[] keyValuePairs = value.split(",");
        Map<String,String> userMap = new HashMap<>();

        for(String pair : keyValuePairs)
        {
            String[] entry = pair.split(":", 2);
            System.out.println(Arrays.toString(entry));
            userMap.put(entry[0].trim().replace("\"", ""), entry[1].trim().replace("\"", ""));
        }
        return userMap;
    }
}

package com.codecool.sportSite.Service;

import com.codecool.sportSite.Model.User;
import com.codecool.sportSite.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    public void register(String userJson) throws NoSuchProviderException, NoSuchAlgorithmException{
        byte[] salt = generateSalt();
        JSONObject jsonObject = new JSONObject(userJson);
        String username = jsonObject.getString("username");
        String password = getSecurePassword(jsonObject.getString("password"), salt);
        String email = jsonObject.getString("email");
        String firstname = jsonObject.getString("firstname");
        String lastname = jsonObject.getString("lastname");
        try {
            if(username.length() > 4 && jsonObject.getString("password").length() > 4 &&
                    firstname.length() > 4 && lastname.length() > 4 && email.contains("@")) {
                User newUser = new User(firstname, lastname, email, username, password, salt);
                userRepository.save(newUser);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String login(String loginJson, HttpSession session) {
        JSONObject jsonObject = new JSONObject(loginJson);
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        User result = userRepository.findByEmail(email);
        if (result == null){
            return "no such user";
        }

        String id = String.valueOf(result.getId());
        if (result.getPassword().equals(getSecurePassword(password, result.getSalt()))) {
            session.setAttribute("userId", id);
        }
        return "Successful login"; // ENUM
    }


    public boolean userExists(String username){
        try {
            User result = userRepository.findByEmail(username);
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

    public byte[] generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
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
}

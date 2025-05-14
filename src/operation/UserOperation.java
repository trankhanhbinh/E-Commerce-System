package operation;
import java.io.*;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import model.Admin;
import model.Customer;
import model.User;

public class UserOperation{
    private static UserOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final String ID_PREFIX = "u_";
    private static final Random RANDOM = new Random();

    private UserOperation(){    
    }

    public static UserOperation getInstance(){
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

        private String generateRandomString(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    public String generateUniqueUserId() {
        long number = 1000000000L + (long)(RANDOM.nextDouble() *9000000000L);
        return ID_PREFIX + number;
    }
    
    public String encryptPassword(String userPassword) {
        if (userPassword == null || userPassword.length() < 1) return null;
        StringBuilder encrypted = new StringBuilder("^^");
        String randomChars = generateRandomString(userPassword.length() * 2);
        for (int i = 0; i < userPassword.length(); i++) {
            encrypted.append(randomChars.charAt(i * 2));
            encrypted.append(randomChars.charAt(i * 2 + 1));
            encrypted.append(userPassword.charAt(i));
        }
        encrypted.append("$$");
        return encrypted.toString();
    }

    public String decryptPassword(String encryptedPassword){
        if (encryptedPassword == null || encryptedPassword.length() < 5) return null;
        encryptedPassword = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder decrypted = new StringBuilder();
        for (int i = 2; i < encryptedPassword.length(); i += 3){
            decrypted.append(encryptedPassword.charAt(i));
        }
        return decrypted.toString();
    }

    public boolean validateUsername(String userName){
        return userName != null && userName.matches("[a-zA-Z_]{5,}");
    }
    public boolean validatePassword(String userPassword){
        return userPassword != null && Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$").matcher(userPassword).matches();
    }

    public boolean checkUsernameExist(String userName){
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    String storedUserName = (String) json.get("user_name");
                    if (storedUserName.equals(userName)) {
                        return true;
                    }
                } catch (org.json.simple.parser.ParseException e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }
        return false;
    }


    public User login(String userName, String userPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONParser parser = new JSONParser();
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(line);
                } catch (org.json.simple.parser.ParseException e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                    continue;
                }

                String storedUserName = (String) json.get("user_name");
                if (storedUserName.equals(userName)) {
                    String encryptedPass = (String) json.get("user_password");
                    String decryptedPass = decryptPassword(encryptedPass);
                    
                    if (userPassword.equals(decryptedPass)) {
                        String userId = (String) json.get("user_id");
                        String userRegisterTime = (String) json.get("user_register_time");
                        String userRole = (String) json.get("user_role");

                        if ("admin".equalsIgnoreCase(userRole)) {
                            return new Admin(userId, storedUserName, encryptedPass, userRegisterTime, userRole);
                        } else {
                            String userEmail = (String) json.get("user_email");
                            String userMobile = (String) json.get("user_mobile");
                            return new Customer(userId, storedUserName, encryptedPass, userRegisterTime, userRole, userEmail, userMobile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }
        return null;
    }
}

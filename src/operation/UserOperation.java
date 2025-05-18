package Assignment.src.operation;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import org.json.simple.*;
import org.json.simple.parser.*;

import Assignment.src.model.User;
import Assignment.src.model.Admin;
import Assignment.src.model.Customer;

public class UserOperation {
    private static UserOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final String ID_PREFIX = "u_";
    private static final Random RANDOM = new Random();

    private UserOperation() {}

    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    public String generateUniqueUserId() {
        long number = 1000000000L + RANDOM.nextInt(900000000);
        return ID_PREFIX + number;
    }

    public String encryptPassword(String userPassword) {
        if (userPassword == null || userPassword.isEmpty()) return null;
        StringBuilder encrypted = new StringBuilder("^^");
        String randomChars = generateRandomString(userPassword.length() * 2);
        for (int i = 0; i < userPassword.length(); i++) {
            encrypted.append(randomChars.charAt(i * 2)).append(randomChars.charAt(i * 2 + 1)).append(userPassword.charAt(i));
        }
        encrypted.append("$$");
        return encrypted.toString();
    }

    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.length() < 5) return null;
        encryptedPassword = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder decrypted = new StringBuilder();
        for (int i = 2; i < encryptedPassword.length(); i += 3) {
            decrypted.append(encryptedPassword.charAt(i));
        }
        return decrypted.toString();
    }

    public boolean validateUsername(String userName) {
        return userName != null && userName.matches("[a-zA-Z_]{5,}");
    }

    public boolean validatePassword(String userPassword) {
        return userPassword != null && Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$").matcher(userPassword).matches();
    }

    public boolean checkUsernameExist(String userName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = (JSONObject) new JSONParser().parse(line);
                String storedUserName = (String) json.get("user_name");
                if (storedUserName.equals(userName)) {
                    return true;
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    public User login(String userName, String userPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = (JSONObject) new JSONParser().parse(line);
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
        } catch (IOException | ParseException e) {
            System.err.println("Error during user login: " + e.getMessage());
        }
        return null;
    }
}

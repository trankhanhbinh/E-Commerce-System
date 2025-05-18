package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class User {
    //constructor 
    private static final Set<String> usedUserIds = new HashSet<>(); // Static set to track used userIds
    private String userId;
    private String userName;
    private String userPassword;
    private String userRegisterTime;
    private String userRole;

    public User(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        if (!userId.matches("^u_\\d{10}$")){
            throw new IllegalArgumentException("Invalid userId format. Expected: u_ followed by 10 digits.");
        }
        if (!userRole.equals("customer") && !userRole.equals("admin")) {
            throw new IllegalArgumentException("userRole must be either 'customer' or 'admin'");
        }
        if (usedUserIds.contains(userId)) {
            throw new IllegalArgumentException("UserId already exists");
        }
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
        if (!isLoading && !usedUserIds.contains(userId)) {
            usedUserIds.add(userId);
        }
    }
    
        //default value
    public User(){
        this.userId = "u_0000000000";
        if (usedUserIds.contains(userId)) {
            throw new IllegalArgumentException("Default userId already used.");
        }
        this.userName = "defaultUser";
        this.userPassword = "defaultPass";
        this.userRole = "customer";
        // Set current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        this.userRegisterTime = now.format(formatter);

        usedUserIds.add(userId);
    }

    // getter and setter (if needed)
        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserPassword() { return userPassword; }
        public String getUserRegisterTime() { return userRegisterTime; }
        public String getUserRole() { return userRole; }
        
        public void setUserId(String userId) { this.userId = userId; }
        public void setUserName(String userName) { this.userName = userName; }
        public void setUserPassword(String userPassword) { this.userPassword = userPassword; }
        public void setUserRegisterTime(String userRegisterTime) { this.userRegisterTime = userRegisterTime; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
    // toString method
    @Override
    public String toString(){
        return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", " +
            "\"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
            userId, userName, userPassword, userRegisterTime, userRole);
    }
}

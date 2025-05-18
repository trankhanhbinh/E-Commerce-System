package model;

public class Customer extends User {
    private String userEmail;
    private String userMobile;

    // Original constructor
    public Customer(String userId, String userName, String userPassword, 
            String userRegisterTime, String userRole, String userEmail, String userMobile) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    // Loading constructor
    public Customer(String userId, String userName, String userPassword, 
            String userRegisterTime, String userRole, String userEmail, String userMobile, boolean isLoading) {
        super(userId, userName, userPassword, userRegisterTime, userRole, isLoading);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    public Customer(){
        super(); 
        this.userEmail = "null@gmail.com";
        this.userMobile = "0000000000";
    }

    // getters and setters
    public String getUserEmail() { return userEmail; }
    public String getUserMobile() { return userMobile; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }


    @Override
    public String toString() {
        return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", " +
            "\"user_register_time\":\"%s\", \"user_role\":\"%s\", " +
            "\"user_email\":\"%s\", \"user_mobile\":\"%s\"}",
            getUserId(), getUserName(), getUserPassword(), getUserRegisterTime(), getUserRole(),
            userEmail, userMobile);
    }
}

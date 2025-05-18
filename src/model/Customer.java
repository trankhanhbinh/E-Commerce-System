package model;

public class Customer extends User{
    //constructors
    private String userEmail;
    private String userMobile;

    public Customer(String userId, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail, String userMobile) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }
    // loading constructor
    public Customer(String userId, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail, String userMobile, boolean isLoading) {
        super(userId, userName, userPassword, userRegisterTime, userRole, isLoading);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    public Customer(){
        super() ; //this is default value of superclass
        this.userEmail = "null@gmail.com";
        this.userMobile = "0000000000";
    }

    // getter and setter (if needed)
    public String getUserEmail() { return userEmail; }
    public String getUserMobile() { return userMobile; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    // toString method
    @Override
    public String toString() {
        String base = super.toString();
        base = base.substring(0, base.length() - 1); // erase "}" at the end
        return base + String.format(", \"user_email\":\"%s\", \"user_mobile\":\"%s\"}", userEmail, userMobile);
    }
}

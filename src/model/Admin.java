package model;

public class Admin extends User{
    //constructors
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole){
        super(userId, userName, userPassword, userRegisterTime, userRole);
    }

    public Admin(){
        super();
        super.setUserRole("admin");
    }    

    // toString method
    @Override
    public String toString() {
        return super.toString();
    }
}

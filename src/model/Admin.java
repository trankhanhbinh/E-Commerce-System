package model;

public class Admin extends User{
    //constructors
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole){
        super(userId, userName, userPassword, userRegisterTime, userRole);
    }
    /**
    * fix a bug in updating profile by adding Loading constructor
    * for example like username, once you log out and log in again with
    * the new username it will result in error and crash the run
    * despite successfully saved the data into the .txt file
    * -Khoa-
    */
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole, boolean isLoading){
        super(userId, userName, userPassword, userRegisterTime, userRole, isLoading);
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

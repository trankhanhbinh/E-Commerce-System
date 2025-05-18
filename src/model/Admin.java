package Assignment.src.model;


public class Admin extends User {
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
    }

    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole, boolean isLoading) {
        super(userId, userName, userPassword, userRegisterTime, userRole, isLoading);
    }

    public Admin() {
        super();
        super.setUserRole("admin");
    }    

    @Override
    public String toString() {
        return super.toString();
    }
}

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import data.UserFileWriter;
import model.Admin;
import model.User;

public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();

        users.add(new User("u_1234567890", "Alice", "pass123", "10-05-2025_12:30:00", "customer"));
        users.add(new User("u_0987654321", "Bob", "secret", "09-05-2025_15:00:00", "customer"));
        users.add(new Admin());        
        UserFileWriter.writeUsersToFile(users);

    }
}
=======
package Assignment;

import java.util.List;
import Assignment.src.Interfaceio.IOInterface;
import Assignment.src.operation.UserOperation;

public class Main{
    public static void main(String[] args){
        IOInterface io = IOInterface.getInstance();
        UserOperation userOp = UserOperation.getInstance();
        CustomerOperation customerOp = CustomerOperation.getInstance();
        AdminOperation adminOp = AdminOperation.getInstance();

        io.printMessage("Welcome to the E-Commerce System!");
        while (true) {
            io.mainMenu();
            String[] input = io.getUserInput("Choose an option:", 1);
            String choice = input[0];

            switch (choice){
                case "1":
                    login();
                    break;
                case "2":
                    registerCustomer();
                    break;
                case "3":
                    io.printMessage("Exiting system...");
                    return;
                default:
                    io.printErrorMessage("Main Menu", "Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    private static void login(){
        IOInterface io = IOInterface.getInstance();
        UserOperation userOp = UserOperation.getInstance();
        
        io.printMessage("Login Page");
        String[] credentials = io.getUserInput("Enter username and password:", 2);
        String username = credentials[0];
        String password = credentials[1];

        User user = userOp.login(username, password);
        if (user == null) {
            io.printErrorMessage("Login", "Invalid credentials.");
            return;
        }

        io.printMessage("Login successful!");
        if (user instanceof Admin) {
            adminMenu((Admin) user);
        } else if (user instanceof Customer) {
            customerMenu((Customer) user);
        }
    }

    private static void registerCustomer() {
        IOInterface io = IOInterface.getInstance();
        CustomerOperation customerOp = CustomerOperation.getInstance();

        io.printMessage("Customer Registration");
        String[] details = io.getUserInput("Enter username, password, email, and mobile:", 4);
        String username = details[0];
        String password = details[1];
        String email = details[2];
        String mobile = details[3];

        boolean success = customerOp.registerCustomer(username, password, email, mobile);
        if (success) {
            io.printMessage("Registration successful!");
        } else {
            io.printErrorMessage("Registration", "Failed to register. Please check your details.");
        }
    }

    private static void adminMenu(Admin admin) {
        IOInterface io = IOInterface.getInstance();
        AdminOperation adminOp = AdminOperation.getInstance();

        while (true) {
            io.adminMenu();
            String[] input = io.getUserInput("Choose an option:", 1);
            String choice = input[0];

            switch (choice) {
                case "1":
                    showProducts();
                    break;
                case "2":
                    addCustomers();
                    break;
                case "3":
                    showCustomers();
                    break;
                case "4":
                    showOrders();
                    break;
                case "5":
                    adminOp.registerAdmin();
                    break;
                case "6":
                    io.printMessage("Generating statistical figures...");
                    break;
                case "7":
                    io.printMessage("Deleting all data...");
                    break;
                case "8":
                    io.printMessage("Logging out...");
                    return;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid option.");
            }
        }
    }

    private static void customerMenu(Customer customer){
        IOInterface io = IOInterface.getInstance();
        CustomerOperation customerOp = CustomerOperation.getInstance();

        while (true) {
            io.customerMenu();
            String[] input = io.getUserInput("Choose an option:", 1);
            String choice = input[0];

            switch (choice) {
                case "1":
                    io.printObject(customer);
                    break;
                case "2":
                    updateProfile(customer);
                    break;
                case "3":
                    showProducts();
                    break;
                case "4":
                    showOrders();
                    break;
                case "5":
                    io.printMessage("Generating consumption figures...");
                    break;
                case "6":
                    io.printMessage("Logging out...");
                    return;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid option.");
            }
        }
    }

    private static void updateProfile(Customer customer){
        IOInterface io = IOInterface.getInstance();
        CustomerOperation customerOp = CustomerOperation.getInstance();

        String[] input = io.getUserInput("Enter attribute to update and new value:", 2);
        String attribute = input[0];
        String value = input[1];

        boolean success = customerOp.updateProfile(attribute, value, customer);
        if (success) {
            io.printMessage("Profile updated successfully!");
        } else {
            io.printErrorMessage("Update Profile", "Failed to update.");
        }
    }

    private static void showProducts() {
        IOInterface io = IOInterface.getInstance();
        ProductOperation productOp = ProductOperation.getInstance();

        List<Product> products = productOp.getProductList(1).getProducts();
        io.showList("User", "Products", products, 1, 1);
    }

    private static void showOrders(){
        IOInterface io = IOInterface.getInstance();
        OrderOperation orderOp = OrderOperation.getInstance();

        List<Order> orders = orderOp.getOrderList("customerId", 1).getOrders();
        io.showList("Customer", "Orders", orders, 1, 1);
    }

    private static void addCustomers(){
        IOInterface io = IOInterface.getInstance();
        AdminOperation adminOp = AdminOperation.getInstance();

        io.printMessage("Adding customer...");
        registerCustomer();
    }
}
>>>>>>> c2c7f9527c8220f849fa1394dc284a9c4440df53

package Assignment.src;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import Assignment.src.model.User;
import Assignment.src.model.Order;
import Assignment.src.operation.AdminOperation;
import Assignment.src.operation.CustomerOperation;
import Assignment.src.operation.OrderOperation;
import Assignment.src.operation.ProductOperation;
import Assignment.src.operation.UserOperation;
import javafx.application.Platform;
import Assignment.src.Interfaceio.IOInterface;

public class Main {
    public static void main(String[] args) {
        AdminOperation.getInstance().registerAdmin();

        Scanner scanner = new Scanner(System.in);
        IOInterface io = IOInterface.getInstance();

        System.out.println("Working Directory = " + new File(".").getAbsolutePath());

        Platform.startup(() ->
        {
        // This block will be executed on JavaFX Thread
        });

        boolean exit = false;
        while (!exit) {
            io.mainMenu();
            System.out.print("Enter your option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": // Login
                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();
                    
                    User user = UserOperation.getInstance().login(username, password);
                    if (user == null) {
                        io.printErrorMessage("Login", "Invalid credentials. Please try again.");
                    } else {
                        io.printMessage("Login successful. Welcome, " + user.getUserName() + "!");
                        if (user.getUserRole().equalsIgnoreCase("admin")) {
                            adminMenu(user);
                        } else {
                            customerMenu(user);
                        }
                    }
                    break;
                case "2": // Register new customer
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine().trim();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter mobile number: ");
                    String mobile = scanner.nextLine().trim();
                    
                    boolean regSuccess = CustomerOperation.getInstance().registerCustomer(regUsername, regPassword, email, mobile);
                    if (regSuccess) {
                        io.printMessage("Registration successful. You can now log in.");
                    } else {
                        io.printErrorMessage("Registration", "Registration failed. Username may already exist or data format is incorrect.");
                    }
                    break;
                case "3": // Quit
                    io.printMessage("Goodbye!");
                    exit = true;
                    break;
                default:
                    io.printErrorMessage("Main Menu", "Invalid choice. Please select 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    private static void adminMenu(User adminUser) {
        Scanner scanner = new Scanner(System.in);
        IOInterface io = IOInterface.getInstance();
        boolean logout = false;
        while (!logout) {
            io.adminMenu();
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1": // Show products
                    System.out.print("Enter page number for product list: ");
                    int pPage = Integer.parseInt(scanner.nextLine().trim());
                    ProductOperation.ProductListResult pr = ProductOperation.getInstance().getProductList(pPage);
                    io.showList(adminUser.getUserRole(), "Product", pr.getProductList(), pr.getCurrentPage(), pr.getTotalPages());
                    break;
                case "2": // Add customers
                    System.out.print("Enter new customer's username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String pwd = scanner.nextLine().trim();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter mobile: ");
                    String mobile = scanner.nextLine().trim();
                    if (CustomerOperation.getInstance().registerCustomer(username, pwd, email, mobile)) {
                        io.printMessage("Customer added successfully.");
                    } else {
                        io.printErrorMessage("Add Customer", "Failed to add customer.");
                    }
                    break;
                case "3": // Show customers
                    System.out.print("Enter page number for customer list: ");
                    int cPage = Integer.parseInt(scanner.nextLine().trim());
                    CustomerOperation.CustomerListResult cr = CustomerOperation.getInstance().getCustomerList(cPage);
                    io.showList(adminUser.getUserRole(), "Customer", cr.getCustomerList(), cr.getCurrentPage(), cr.getTotalPages());
                    break;
                case "4": // Show orders
                    System.out.print("Enter page number for order list: ");
                    int oPage = Integer.parseInt(scanner.nextLine().trim());
                    OrderOperation.OrderListResult orderRes = OrderOperation.getInstance().getOrderList("all", oPage);
                    io.showList(adminUser.getUserRole(), "Order", orderRes.getOrderList(), orderRes.getCurrentPage(), orderRes.getTotalPages());
                    break;
                case "5": // Generate test data (for orders)
                    OrderOperation.getInstance().generateTestOrderData();
                    io.printMessage("Test order data generated successfully.");
                    break;
                case "6": // Generate all statistical figures
                    ProductOperation.getInstance().generateCategoryFigure();
                    ProductOperation.getInstance().generateDiscountFigure();
                    ProductOperation.getInstance().generateLikesCountFigure();
                    ProductOperation.getInstance().generateDiscountLikesCountFigure();
                    OrderOperation.getInstance().generateAllCustomersConsumptionFigure();
                    OrderOperation.getInstance().generateAllTop10BestSellersFigure();
                    io.printMessage("All statistical figures generated. Please check the data/figure folder.");
                    break;
                case "7": // Delete all data
                    CustomerOperation.getInstance().deleteAllCustomers();
                    ProductOperation.getInstance().deleteAllProducts();
                    OrderOperation.getInstance().deleteAllOrders();
                    io.printMessage("All data deleted successfully.");
                    break;
                case "8": // Logout
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid option.");
                    break;
            }
        }
    }

    private static void customerMenu(User customerUser) {
        Scanner scanner = new Scanner(System.in);
        IOInterface io = IOInterface.getInstance();
        boolean logout = false;
        while (!logout) {
            io.customerMenu();
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1": // Show profile
                    io.printObject(customerUser);
                    break;
                case "2": // Update profile
                    System.out.print("Enter attribute to update (username, userpassword, useremail, usermobile): ");
                    String attr = scanner.nextLine().trim();
                    System.out.print("Enter new value: ");
                    String newVal = scanner.nextLine().trim();
                    boolean updated = CustomerOperation.getInstance().updateProfile(attr, newVal, (Assignment.src.model.Customer) customerUser);
                    if (updated) {
                        io.printMessage("Profile updated successfully.");
                    } else {
                        io.printErrorMessage("Update Profile", "Update failed. Please check your input.");
                    }
                    break;
                case "3": // Show products
                    System.out.print("Enter page number for product list: ");
                    int pPage = Integer.parseInt(scanner.nextLine().trim());
                    ProductOperation.ProductListResult pr = ProductOperation.getInstance().getProductList(pPage);
                    io.showList(customerUser.getUserRole(), "Product", pr.getProductList(), pr.getCurrentPage(), pr.getTotalPages());
                    break;
                case "4": // Show history orders
                    System.out.print("Enter page number for your orders: ");
                    int oPage = Integer.parseInt(scanner.nextLine().trim());
                    List<Order> orders = OrderOperation.getInstance().getOrderList(customerUser.getUserId(), oPage).getOrderList();
                    io.showList(customerUser.getUserRole(), "Your Order", orders, oPage, 0);
                    break;
                case "5": // Generate consumption figure
                    OrderOperation.getInstance().generateSingleCustomerConsumptionFigure(customerUser.getUserId());
                    io.printMessage("Consumption figure generated. Please check data/figure folder.");
                    break;
                case "6": // Logout
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid option.");
                    break;
            }
        }
    }
}
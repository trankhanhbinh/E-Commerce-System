package Assignment.src.Interfaceio;

import java.util.Scanner;
import java.util.List;

public class IOInterface {
    private static IOInterface instance;
    private Scanner scanner;

    // constructor
    private IOInterface(){
        scanner = new Scanner(System.in);
    }

    public static IOInterface getInstance(){
        if (instance == null){
            instance = new IOInterface();
        }
        return instance;
    }

    // accept user's input
    public String[] getUserInput(String message, int numOfArgs){
        System.out.println(message);
        String input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+");

        // adjust input array size
        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++){
            result[i] = (i < parts.length) ? parts[i] : "";
        }
        return result;
    }

    //main menu
    public void mainMenu(){
        System.out.println("(===== Welcome =====)");
        System.out.println("(1) Login");
        System.out.println("(2) Register");
        System.out.println("(3) Quit");
    }

    //admin menu
    public void adminMenu(){
        System.out.println("(===== Good day, Admin! =====)");
        System.out.println("(1) Show products");
        System.out.println("(2) Add customers");
        System.out.println("(3) Show customers");
        System.out.println("(4) Show orders");
        System.out.println("(5) Generate test data");
        System.out.println("(6) Generate all statistical figures");
        System.out.println("(7) Delete all data");
        System.out.println("(8) Logout");
    }

    // customer menu
    public void customerMenu(){
        System.out.println("(===== Enjoy your shopping :) =====)");
        System.out.println("(1) Show profile");
        System.out.println("(2) Update profile");
        System.out.println("(3) Show products");
        System.out.println("(4) Show history orders");
        System.out.println("(5) Generate all consumption figures");
        System.out.println("(6) Logout");
    }

    // show lists (Customer, Product, Order)
    public void showList(String userRole, String listType, List<?> objectList, int pageNumber, int totalPages){
        System.out.printf("Showing %s List for %s (Page %d/%d)%n", listType, userRole, pageNumber, totalPages);
        for (int i = 0; i < objectList.size(); i++){
            System.out.printf("%d. %s%n", i + 1, objectList.get(i).toString());
        }
    }

    // print error
    public void printErrorMessage(String errorSource, String errorMessage){
        System.err.printf("Error in %s: %s%n", errorSource, errorMessage);
    }

    // print message
    public void printMessage(String message){
        System.out.println(message);
    }

    // print object
    public void printObject(Object targetObject){
        System.out.println(targetObject.toString());
    }
}
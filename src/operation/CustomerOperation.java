package operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.Customer;

public class CustomerOperation{
    private static CustomerOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final int PAGE_SIZE = 10;

    private CustomerOperation(){
    }

    public static CustomerOperation getInstance(){
        if (instance == null){
            instance = new CustomerOperation();
        }
        return instance;
    }

    public boolean validateEmail(String userEmail){
        if (userEmail == null)
            return false;
        return Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$").matcher(userEmail).matches();
    }

    public boolean validateMobile(String userMobile){
        if (userMobile == null)
            return false;
        return Pattern.compile("^(04|03)\\d{8}$").matcher(userMobile).matches();
    }
    
    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile){
        if (checkUsernameExist(userName))
            return false;
        if (!validateEmail(userEmail) || !validateMobile(userMobile))
            return false;
        String userId = generateUniqueUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String registerTime = LocalDateTime.now().format(formatter);
        JSONObject customerObj = new JSONObject();
        customerObj.put("user_id", userId);
        customerObj.put("user_name", userName);
        customerObj.put("user_password", UserOperation.getInstance().encryptPassword(userPassword));
        customerObj.put("user_register_time", registerTime);
        customerObj.put("user_role", "customer");
        customerObj.put("user_email", userEmail);
        customerObj.put("user_mobile", userMobile);
        writeUserToFile(customerObj);
        return true;
    }
    
    public boolean updateProfile(String attributeName, String value, Customer customerObject){
        if (customerObject == null)
            return false;
        boolean valid = false;
        switch (attributeName.toLowerCase()){
            case "username":
                if (validateUsername(value)){
                    customerObject.setUserName(value);
                    valid = true;
                }
                break;
            case "userpassword":
                if (validatePassword(value)) {
                    customerObject.setUserPassword(UserOperation.getInstance().encryptPassword(value));
                    valid = true;
                }
                break;
            case "useremail":
                if (validateEmail(value)){
                    customerObject.setUserEmail(value);
                    valid = true;
                }
                break;
            case "usermobile":
                if (validateMobile(value)){
                    customerObject.setUserMobile(value);
                    valid = true;
                }
                break;
            default:
                valid = false;
        }
        if (valid)
            updateUserInFile(customerObject);
        return valid;
    }
    
    public boolean deleteCustomer(String customerId){
        List<JSONObject> users = readUsersFromFile();
        boolean found = false;
        Iterator<JSONObject> it = users.iterator();
        while (it.hasNext()) {
            JSONObject obj = it.next();
            String role = (String) obj.get("user_role");
            if (role != null && role.equalsIgnoreCase("customer") && obj.get("user_id").equals(customerId)) {
                it.remove();
                found = true;
                break;
            }
        }
        if (found)
            overwriteUsersFile(users);
        return found;
    }
    
    public CustomerListResult getCustomerList(int pageNumber){
        List<Customer> allCustomers = getAllCustomers();
        int totalCustomers = allCustomers.size();
        int totalPages = (totalCustomers + PAGE_SIZE - 1) / PAGE_SIZE;
        if (pageNumber < 1)
            pageNumber = 1;
        if (pageNumber > totalPages && totalPages > 0)
            pageNumber = totalPages;
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalCustomers);
        List<Customer> pageList = new ArrayList<>(allCustomers.subList(startIndex, endIndex));
        return new CustomerListResult(pageList, pageNumber, totalPages);
    }
    
    public void deleteAllCustomers(){
        List<JSONObject> users = readUsersFromFile();
        Iterator<JSONObject> it = users.iterator();
        while (it.hasNext()) {
            JSONObject obj = it.next();
            String role = (String) obj.get("user_role");
            if (role != null && role.equalsIgnoreCase("customer")) {
                it.remove();
            }
        }
        overwriteUsersFile(users);
    }
    
    private List<Customer> getAllCustomers(){
        List<Customer> customers = new ArrayList<>();
        List<JSONObject> users = readUsersFromFile();
        System.out.println("DEBUG: Total user objects read: " + users.size());
        for (JSONObject obj : users) {
            String role = (String) obj.get("user_role");
            if (role != null && role.trim().equalsIgnoreCase("customer")){
                String userId = (String) obj.get("user_id");
                String userName = (String) obj.get("user_name");
                String userPassword = (String) obj.get("user_password");
                String userRegisterTime = (String) obj.get("user_register_time");
                String userEmail = (String) obj.get("user_email");
                String userMobile = (String) obj.get("user_mobile");
                
                Customer customer = new Customer(userId, userName, userPassword, 
                    userRegisterTime, role, userEmail, userMobile, true);
                customers.add(customer);
            }
        }
        System.out.println("DEBUG: Total loaded customers: " + customers.size());
        return customers;
    }
    
    private boolean checkUsernameExist(String userName){
        List<JSONObject> users = readUsersFromFile();
        for (JSONObject obj : users){
            String name = (String) obj.get("user_name");
            if (name.equalsIgnoreCase(userName))
                return true;
        }
        return false;
    }
    
    private String generateUniqueUserId(){
        List<JSONObject> users = readUsersFromFile();
        int maxId = 0;
        for (JSONObject obj : users) {
            String id = (String) obj.get("user_id");
            if (id != null && id.matches("^u_\\d{10}$")){
                int num = Integer.parseInt(id.substring(2));
                if (num > maxId)
                    maxId = num;
            }
        }
        int newId = maxId + 1;
        return String.format("u_%010d", newId);
    }
    
    private String encryptPassword(String userPassword){
        return "^^" + userPassword + "$$";
    }
    
    private boolean validateUsername(String userName){
        if (userName == null)
            return false;
        return userName.matches("[a-zA-Z_]{5,}");
    }
    
    private boolean validatePassword(String userPassword){
        if (userPassword == null)
            return false;
        return userPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$");
    }
    
    private List<JSONObject> readUsersFromFile(){
        List<JSONObject> list = new ArrayList<>();
        File file = new File(USER_FILE);
        if (!file.exists()){
            return list;
        }
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))){
            String line;
            while ((line = reader.readLine()) != null){
                if (line.trim().isEmpty())
                    continue;
                try {
                    JSONObject obj = (JSONObject) parser.parse(line);
                    list.add(obj);
                } catch (ParseException pe) {
                    System.err.println("Error parsing user JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user file: " + e.getMessage());
        }
        return list;
    }
    
    private void writeUserToFile(JSONObject userObj) {
        String orderedJSONString = String.format(
            "{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\","+
            "\"user_register_time\":\"%s\",\"user_role\":\"%s\",\"user_email\":\"%s\",\"user_mobile\":\"%s\"}",
            userObj.get("user_id"),
            userObj.get("user_name"),
            userObj.get("user_password"),
            userObj.get("user_register_time"),
            userObj.get("user_role"),
            userObj.get("user_email"),
            userObj.get("user_mobile")
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE, true))) {
            writer.print("\n");
            writer.print(orderedJSONString);
        } catch (IOException e) {
            System.err.println("Error writing user to file: " + e.getMessage());
        }
    }
    
    private void updateUserInFile(Customer customer){
        List<JSONObject> users = readUsersFromFile();
        for (JSONObject obj : users) {
            if (obj.get("user_id").equals(customer.getUserId())) {
                obj.put("user_name", customer.getUserName());
                obj.put("user_password", customer.getUserPassword());
                obj.put("user_email", customer.getUserEmail());
                obj.put("user_mobile", customer.getUserMobile());
                break;
            }
        }
        overwriteUsersFile(users);
    }
    
    private String getOrderedJSONString(JSONObject obj) {
        return String.format(
            "{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"," +
            "\"user_register_time\":\"%s\",\"user_role\":\"%s\",\"user_email\":\"%s\",\"user_mobile\":\"%s\"}",
            obj.get("user_id"),
            obj.get("user_name"),
            obj.get("user_password"),
            obj.get("user_register_time"),
            obj.get("user_role"),
            obj.get("user_email"),
            obj.get("user_mobile")
        );
    }

    private void overwriteUsersFile(List<JSONObject> users){
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE, false))){
            for (JSONObject obj : users){
                writer.println(getOrderedJSONString(obj));
            }
        } catch (IOException e){
            System.err.println("Error overwriting user file: " + e.getMessage());
        }
    }
    
    public static class CustomerListResult{
        private List<Customer> customerList;
        private int currentPage;
        private int totalPages;
        
        public CustomerListResult(List<Customer> customerList, int currentPage, int totalPages){
            this.customerList = customerList;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
        
        public List<Customer> getCustomerList(){
            return customerList;
        }
        
        public int getCurrentPage(){
            return currentPage;
        }
        
        public int getTotalPages(){
            return totalPages;
        }
    }
}

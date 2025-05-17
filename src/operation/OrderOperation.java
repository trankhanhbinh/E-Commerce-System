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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.Order;

public class OrderOperation{
    private static OrderOperation instance;
    private static final String ORDER_FILE = "data/orders.txt";
    private static final int PAGE_SIZE = 10;
    
    private OrderOperation(){
    }
    
    public static OrderOperation getInstance(){
        if (instance == null){
            instance = new OrderOperation();
        }
        return instance;
    }
    
    public String generateUniqueOrderId(){
        List<JSONObject> jsonList = readOrderJsonFromFile();
        int maxId = 0;
        for (JSONObject obj : jsonList){
            String orderId = (String) obj.get("order_id");
            if (orderId != null && orderId.matches("^o_\\d{5}$")){
                int num = Integer.parseInt(orderId.substring(2));
                if (num > maxId)
                    maxId = num;
            }
        }
        return String.format("o_%05d", maxId + 1);
    }
    
    public boolean createAnOrder(String customerId, String productId, String createTime){
        String orderId = generateUniqueOrderId();
        String orderTime;
        if (createTime == null || createTime.trim().isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
            orderTime = LocalDateTime.now().format(formatter);
        } else {
            orderTime = createTime;
        }
        Order order;
        try {
            order = new Order(orderId, customerId, productId, orderTime);
        } catch (IllegalArgumentException e){
            System.err.println("Error creating order: " + e.getMessage());
            return false;
        }
        return appendOrderToFile(order);
    }
    
    public boolean deleteOrder(String orderId){
        List<Order> orders = readOrdersFromFile();
        boolean found = false;
        Iterator<Order> it = orders.iterator();
        while (it.hasNext()){
            Order order = it.next();
            if (order.getOrderId().equals(orderId)){
                it.remove();
                found = true;
                break;
            }
        }
        if (found){
            overwriteOrdersFile(orders);
        }
        return found;
    }
    
    public OrderListResult getOrderList(String customerId, int pageNumber){
        List<Order> orders = readOrdersFromFile();
        List<Order> customerOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getUserId().equals(customerId)){
                customerOrders.add(order);
            }
        }
        int totalOrders = customerOrders.size();
        int totalPages = (totalOrders + PAGE_SIZE - 1)/PAGE_SIZE;
        if (pageNumber < 1)
            pageNumber = 1;
        if (pageNumber > totalPages && totalPages > 0)
            pageNumber = totalPages;
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalOrders);
        List<Order> pageList = new ArrayList<>(customerOrders.subList(startIndex, endIndex));
        return new OrderListResult(pageList, pageNumber, totalPages);
    }
    
    public void generateTestOrderData(){
        System.out.println("Generating test order data...");
    }
    
    public void generateSingleCustomerConsumptionFigure(String customerId){
        System.out.println("Generating consumption figure for customer " + customerId + "...");
    }
    
    public void generateAllCustomersConsumptionFigure(){
        System.out.println("Generating consumption figure for all customers...");
    }
    
    public void generateAllTop10BestSellersFigure(){
        System.out.println("Generating top 10 best sellers figure...");
    }
    
    public void deleteAllOrders(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDER_FILE, false))){
            writer.print("");
        } catch (IOException e){
            System.err.println("Error deleting all orders: " + e.getMessage());
        }
    }
    
    private List<Order> readOrdersFromFile(){
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDER_FILE);
        if (!file.exists())
            return orders;
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                try {
                    JSONObject obj = (JSONObject) parser.parse(line);
                    String orderId = (String) obj.get("order_id");
                    String userId = (String) obj.get("user_id");
                    String proId = (String) obj.get("pro_id");
                    String orderTime = (String) obj.get("order_time");
                    Order order = new Order(orderId, userId, proId, orderTime);
                    orders.add(order);
                } catch (ParseException pe) {
                    System.err.println("Error parsing order JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders file: " + e.getMessage());
        }
        return orders;
    }
    
    private List<JSONObject> readOrderJsonFromFile(){
        List<JSONObject> list = new ArrayList<>();
        File file = new File(ORDER_FILE);
        if (!file.exists())
            return list;
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))){
            String line;
            while ((line = reader.readLine()) != null){
                if (line.trim().isEmpty())
                    continue;
                try {
                    JSONObject obj = (JSONObject) parser.parse(line);
                    list.add(obj);
                } catch (ParseException pe){
                    System.err.println("Error parsing order JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e){
            System.err.println("Error reading orders file: " + e.getMessage());
        }
        return list;
    }
    
    private boolean appendOrderToFile(Order order){
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDER_FILE, true))){
            writer.println(order.toString());
            return true;
        } catch (IOException e){
            System.err.println("Error writing order to file: " + e.getMessage());
            return false;
        }
    }
    
    private void overwriteOrdersFile(List<Order> orders){
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDER_FILE, false))){
            for (Order order : orders){
                writer.println(order.toString());
            }
        } catch (IOException e){
            System.err.println("Error overwriting orders file: " + e.getMessage());
        }
    }
    
    public static class OrderListResult{
        private List<Order> orderList;
        private int currentPage;
        private int totalPages;
        
        public OrderListResult(List<Order> orderList, int currentPage, int totalPages){
            this.orderList = orderList;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
        
        public List<Order> getOrderList(){
            return orderList;
        }
        
        public int getCurrentPage(){
            return currentPage;
        }
        
        public int getTotalPages(){
            return totalPages;
        }
    }
}

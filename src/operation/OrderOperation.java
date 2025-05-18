package Assignment.src.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;


import Assignment.src.model.Order;
import Assignment.src.model.Product;

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
    
    public void generateTestOrderData() {
    String[] customerIds = {
        "u_0000000002", "u_0000000003", "u_0000000004",
        "u_0000000005", "u_0000000006", "u_0000000007",
        "u_0000000008", "u_0000000009", "u_0000000010",
        "u_0000000011"
    };
    
    var productResult = ProductOperation.getInstance().getProductList(1);
    List<Product> availableProducts = productResult.getProductList();
    if (availableProducts == null || availableProducts.isEmpty()) {
        // add a dummy product.
        availableProducts = new ArrayList<>();
        availableProducts.add(new Product("p_dummy", "dummyModel", "dummyCategory", "Dummy Product", 0.0, 0.0, 0.0, 0));
    }
    
    deleteAllOrders();
    
    java.util.Random rand = new java.util.Random();
    
    for (String customerId : customerIds) {
        int numOrders = 50 + rand.nextInt(151);
        for (int i = 0; i < numOrders; i++) {
            Product p = availableProducts.get(rand.nextInt(availableProducts.size()));
            String productId = p.getProId();
            
            int year = 2024;
            int month = 1 + rand.nextInt(12);
            int day = 1 + rand.nextInt(28);
            int hour = rand.nextInt(24);
            int minute = rand.nextInt(60);
            int second = rand.nextInt(60);
            String orderTime = String.format("%02d-%02d-%04d_%02d:%02d:%02d", day, month, year, hour, minute, second);
            
            createAnOrder(customerId, productId, orderTime);
            }
        }
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
    
        public void generateSingleCustomerConsumptionFigure(final String customerId) {
        Platform.runLater(() -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
            List<Order> orders = readOrdersFromFile();
            Map<String, Double> monthlyConsumption = new HashMap<>();
            for (Order order : orders) {
                if (order.getUserId().equals(customerId)) {
                    try {
                        LocalDateTime orderDate = LocalDateTime.parse(order.getOrderTime(), dateTimeFormatter);
                        String monthYear = String.format("%02d-%d", orderDate.getMonthValue(), orderDate.getYear());
                        double price = 0.0;
                        if (ProductOperation.getInstance().getProductById(order.getProId()) != null) {
                            price = ProductOperation.getInstance().getProductById(order.getProId()).getProCurrentPrice();
                        }
                        monthlyConsumption.put(monthYear, monthlyConsumption.getOrDefault(monthYear, 0.0) + price);
                    } catch (Exception e) {
                    }
                }
            }

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Month-Year");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Consumption ($)");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Monthly Consumption for Customer " + customerId);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Spending");
            for (Map.Entry<String, Double> entry : monthlyConsumption.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);

            Scene scene = new Scene(barChart, 800, 600);
            WritableImage image = scene.snapshot(null);

            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "customer_" + customerId + "_consumption.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void generateAllCustomersConsumptionFigure() {
        Platform.runLater(() -> {
            List<Order> orders = readOrdersFromFile();
            Map<String, Double> consumptionByCustomer = new HashMap<>();
            for (Order order : orders) {
                String custId = order.getUserId();
                double price = 0.0;
                if (ProductOperation.getInstance().getProductById(order.getProId()) != null) {
                    price = ProductOperation.getInstance().getProductById(order.getProId()).getProCurrentPrice();
                }
                consumptionByCustomer.put(custId, consumptionByCustomer.getOrDefault(custId, 0.0) + price);
            }

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Customer ID");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Consumption ($)");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Consumption by Customer");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Consumption");
            for (Map.Entry<String, Double> entry : consumptionByCustomer.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);

            Scene scene = new Scene(barChart, 800, 600);
            WritableImage image = scene.snapshot(null);

            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "all_customers_consumption.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void generateAllTop10BestSellersFigure() {
        Platform.runLater(() -> {
            List<Order> orders = readOrdersFromFile();
            Map<String, Integer> salesCount = new HashMap<>();
            for (Order order : orders) {
                String productId = order.getProId();
                salesCount.put(productId, salesCount.getOrDefault(productId, 0) + 1);
            }
            List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(salesCount.entrySet());
            sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            if (sortedSales.size() > 10) {
                sortedSales = sortedSales.subList(0, 10);
            }

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Product ID");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Orders Count");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Top 10 Best Selling Products");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");
            for (Map.Entry<String, Integer> entry : sortedSales) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);

            Scene scene = new Scene(barChart, 800, 600);
            WritableImage image = scene.snapshot(null);

            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "top10_bestsellers.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
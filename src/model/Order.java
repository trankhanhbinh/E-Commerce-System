package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Order {
    //constructors
    private static final Set<String> usedOrderIds = new HashSet<>();
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    public Order(String orderId, String userId, String proId, String orderTime){
        if (!orderId.matches("^o_\\d{5}$")){
            throw new IllegalArgumentException("Invalid orderId format. Expected: o_ followed by 5 digits.");
        }
        if (usedOrderIds.contains(orderId)) {
            throw new IllegalArgumentException("orderId already exists");
        }
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;

        usedOrderIds.add(userId);
    }

    //default value
    public Order(){
        this.orderId = "o_00000";
        this.userId = "u_0000000000";
        if(usedOrderIds.contains(orderId)) {
            throw new IllegalArgumentException("Default orderId already used.");
        }
        this.proId = "defaultProID";
        //set current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        this.orderTime = now.format(formatter);
        usedOrderIds.add(orderId);
    }

        // getter and setter (if needed)
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getProId() { return proId; }
    public String getOrderTime() { return orderTime; }

    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setProId(String proId) { this.proId = proId; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
    
    // toString method
    @Override
    public String toString() {
        return String.format(
            "{\"order_id\":\"%s\", \"user_id\":\"%s\", \"pro_id\":\"%s\", \"order_time\":\"%s\"}",
            orderId, userId, proId, orderTime);
    }
}

package model;

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
        this.orderId = "u_00000";
        this.userId = "u_0000000000";
    }
}

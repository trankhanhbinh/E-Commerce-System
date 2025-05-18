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
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AdminOperation{
    private static AdminOperation instance;
    private static final String USER_FILE = "data/users.txt";

    private AdminOperation(){
    }

    public static AdminOperation getInstance(){
        if(instance == null){
            instance = new AdminOperation();
        }
        return instance;
    }
    
    public void registerAdmin(){
        List<JSONObject> users = readUsersFromFile();
        for (JSONObject obj : users) {
            String role = (String)obj.get("user_role");
            if (role != null && role.equalsIgnoreCase("admin")){
                return;
            }
        }
        String adminId = "u_0000000001";
        String adminName = "admin";
        String adminPassword = "^^encryptedAdmin$$";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String registerTime = LocalDateTime.now().format(formatter);
        JSONObject adminObj = new JSONObject();
        adminObj.put("user_id", adminId);
        adminObj.put("user_name", adminName);
        adminObj.put("user_password", adminPassword);
        adminObj.put("user_register_time", registerTime);
        adminObj.put("user_role", "admin");
        writeUserToFile(adminObj);
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
                }catch (ParseException pe){
                    System.err.println("Error parsing user JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e){
            System.err.println("Error reading user file: " + e.getMessage());
        }
        return list;
    }

    private void writeUserToFile(JSONObject userObj) {
        String orderedJSONString = String.format(
            "{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"," +
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
}

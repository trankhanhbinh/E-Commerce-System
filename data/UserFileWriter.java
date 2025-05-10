package data;
import java.io.*;
import java.util.*;
import model.User;


public class UserFileWriter {
    public static void writeUsersToFile(List<User> users) {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs(); // make a new file if it doesn't exist
        }
        File file = new File(dir, "users.txt");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(file, false), "UTF-8"))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
            System.out.println("✅ user list has been written to data/users.txt");
        } catch (IOException e) {
            System.err.println("❌ file error: " + e.getMessage());
        }
    }
}

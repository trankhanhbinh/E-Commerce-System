import java.util.ArrayList;
import java.util.List;
import data.UserFileWriter;
import model.User;

public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();

        users.add(new User("u_1234567890", "Alice", "pass123", "10-05-2025_12:30:00", "customer"));
        users.add(new User("u_0987654321", "Bob", "secret", "09-05-2025_15:00:00", "admin"));

        UserFileWriter.writeUsersToFile(users);
    }
}

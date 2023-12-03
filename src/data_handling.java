import java.sql.*;
import java.util.Scanner;

public class data_handling {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/database2";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "12345678";

    // user interface
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {

            boolean running = true;
            while (running) {
                System.out.println("\n1 - Create\n2 - Retrieve\n3 - Update\n4 - Delete\n5 - Show metadata");
                String action = scanner.nextLine();

                switch (action) {
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
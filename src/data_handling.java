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
                    case "1":
                        create(scanner, conn);
                        break;
                    case "2":
                        retrieve(conn);
                        break;
                    case "3":
                        update(scanner, conn);
                        break;
                    case "4":
                        delete(scanner, conn);
                        break;
                    case "5":
                        System.out.print("Enter the name of the table to view its metadata: ");
                        String tableName = scanner.nextLine();
                        showMetaData(conn, tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create(Scanner scanner, Connection conn) throws SQLException {
        System.out.println("Select the type of info to create: \n1. Author \n2. Book \n3. Customer \n4. Order");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("Enter Author ID: ");
                int authorId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Author Name: ");
                String authorName = scanner.nextLine();

                String sqlAuthor = "INSERT INTO Authors (author_id, name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlAuthor)) {
                    pstmt.setInt(1, authorId);
                    pstmt.setString(2, authorName);
                    pstmt.executeUpdate();
                    System.out.println("Author added successfully.");
                }
                break;

            case "2":
                System.out.print("Enter Book ID: ");
                int bookId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Title: ");
                String title = scanner.nextLine();
                System.out.print("Enter Author ID: ");
                int bookAuthorId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Stock: ");
                int stock = Integer.parseInt(scanner.nextLine());

                String sqlBook = "INSERT INTO Books (book_id, title, author_id, stock) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlBook)) {
                    pstmt.setInt(1, bookId);
                    pstmt.setString(2, title);
                    pstmt.setInt(3, bookAuthorId);
                    pstmt.setInt(4, stock);
                    pstmt.executeUpdate();
                    System.out.println("Book added successfully.");
                }
                break;

            case "3":
                System.out.print("Enter Customer ID: ");
                int customerId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Customer Name: ");
                String customerName = scanner.nextLine();

                String sqlCustomer = "INSERT INTO Customers (customer_id, name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCustomer)) {
                    pstmt.setInt(1, customerId);
                    pstmt.setString(2, customerName);
                    pstmt.executeUpdate();
                    System.out.println("Customer added successfully.");
                }
                break;

            case "4":

                System.out.print("Enter Order ID: ");
                int orderId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Customer ID: ");
                int orderCustomerId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Book ID: ");
                int orderBookId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Order Amount: ");
                int orderAmount = Integer.parseInt(scanner.nextLine());

                String checkStockSql = "SELECT stock FROM Books WHERE book_id = ?";
                try (PreparedStatement checkStockStmt = conn.prepareStatement(checkStockSql)) {
                    checkStockStmt.setInt(1, orderBookId);
                    ResultSet rs = checkStockStmt.executeQuery();
                    if (rs.next()) {
                        stock = rs.getInt("stock");
                        if (orderAmount > stock) {
                            System.out.println("Order amount exceeds current stock. Order cannot be placed.");
                        } else {

                            String sqlOrder = "INSERT INTO Orders (order_id, customer_id, book_id, order_number) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertOrderStmt = conn.prepareStatement(sqlOrder)) {
                                insertOrderStmt.setInt(1, orderId);
                                insertOrderStmt.setInt(2, orderCustomerId);
                                insertOrderStmt.setInt(3, orderBookId);
                                insertOrderStmt.setInt(4, orderAmount);
                                insertOrderStmt.executeUpdate();
                                System.out.println("Order added successfully.");
                            }

                            String updateStock = "UPDATE Books SET stock = stock - ? WHERE book_id = ?";
                            try (PreparedStatement updateBookVolumeStmt = conn.prepareStatement(updateStock)) {
                                updateBookVolumeStmt.setInt(1, orderAmount);
                                updateBookVolumeStmt.setInt(2, orderBookId);
                                updateBookVolumeStmt.executeUpdate();
                            }
                        }
                    } else {
                        System.out.println("Book not found.");
                    }
                }
                break;
            default:
                System.out.println("Invalid choice.");

        }
    }

    private static void retrieve(Connection conn) throws SQLException {
        String sql = "SELECT b.title, b.stock, a.name, SUM(o.order_number) as total_orders " +
                "FROM Books b " +
                "JOIN Authors a ON b.author_id = a.author_id " +
                "LEFT JOIN Orders o ON b.book_id = o.book_id " +
                "GROUP BY b.title, b.stock, a.name";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Title: " + rs.getString("title") + ", Stock: " + rs.getInt("stock") +
                        ", Author: " + rs.getString("name") + ", Total Orders: " + rs.getInt("total_orders"));
            }
        }
    }


    private static void update(Scanner scanner, Connection conn) throws SQLException {
        System.out.print("Enter the Book ID to update: ");
        int bookId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new title (press enter to skip): ");
        String newTitle = scanner.nextLine();
        System.out.print("Enter new stock (0 for no change): ");
        int newStock = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE Books SET title = COALESCE(NULLIF(?, ''), title), " +
                "stock = COALESCE(NULLIF(?, 0), stock) WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTitle);
            pstmt.setInt(2, newStock);
            pstmt.setInt(3, bookId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " book(s) updated.");
        }
    }


    private static void delete(Scanner scanner, Connection conn) throws SQLException {
        System.out.print("Enter the Book ID to delete: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " book(s) deleted.");
        }
    }


    private static void showMetaData(Connection conn, String tableName) {
        try {
            displayTableNamesAndStructures(conn);
            displayColumnDetails(conn, tableName);
            displayPrimaryKeyInfo(conn, tableName);
            displayForeignKeyInfo(conn, tableName);
        } catch (SQLException e) {
            System.out.println("Error retrieving metadata for table: " + tableName);
            e.printStackTrace();
        }
    }

    private static void displayTableNamesAndStructures(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                System.out.println("Table: " + tables.getString("TABLE_NAME") + ", Type: " + tables.getString("TABLE_TYPE"));
            }
        }
    }

    private static void displayColumnDetails(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Column details for table: " + tableName);
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            while (columns.next()) {
                System.out.println("Column Name: " + columns.getString("COLUMN_NAME") +
                        ", Type: " + columns.getString("TYPE_NAME") +
                        ", Size: " + columns.getInt("COLUMN_SIZE"));
            }
        }
    }


    private static void displayPrimaryKeyInfo(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Primary Key for table: " + tableName);

        try (ResultSet pKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            while (pKeys.next()) {
                System.out.println("Column Name: " + pKeys.getString("COLUMN_NAME") +
                        ", PK Name: " + pKeys.getString("PK_NAME"));
            }
        }
    }


    private static void displayForeignKeyInfo(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Foreign Keys for table: " + tableName);

        try (ResultSet fKeys = metaData.getImportedKeys(null, null, tableName)) {
            while (fKeys.next()) {
                System.out.println("FK Name: " + fKeys.getString("FK_NAME") +
                        ", FK Column: " + fKeys.getString("FKCOLUMN_NAME") +
                        ", PK Table: " + fKeys.getString("PKTABLE_NAME") +
                        ", PK Column: " + fKeys.getString("PKCOLUMN_NAME"));
            }
        }
    }

}
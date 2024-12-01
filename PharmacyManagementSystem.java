import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/PharmacyDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Santosh@2006";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

class MedicineDAO {
    public void addMedicine(String name, String brand, String type, int quantity, double price, String expiryDate) {
        String sql = "INSERT INTO Medicines (name, brand, type, quantity, price, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, brand);
            pstmt.setString(3, type);
            pstmt.setInt(4, quantity);
            pstmt.setDouble(5, price);
            pstmt.setString(6, expiryDate);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicine added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listMedicines() {
        String sql = "SELECT * FROM Medicines";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder medicines = new StringBuilder();
            while (rs.next()) {
                medicines.append("ID: ").append(rs.getInt("medicine_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Brand: ").append(rs.getString("brand"))
                        .append(", Quantity: ").append(rs.getInt("quantity"))
                        .append(", Price: ").append(rs.getDouble("price"))
                        .append(", Expiry Date: ").append(rs.getDate("expiry_date"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(null, medicines.toString(), "Medicines", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listLessQuantityMedicines() {
        String sql = "SELECT * FROM Medicines WHERE quantity < 10";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder medicines = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                found = true;
                medicines.append("ID: ").append(rs.getInt("medicine_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Brand: ").append(rs.getString("brand"))
                        .append(", Type: ").append(rs.getString("type"))
                        .append(", Expiry Date: ").append(rs.getDate("expiry_date"))
                        .append("\n");
            }
            if (found) {
                JOptionPane.showMessageDialog(null, medicines.toString(), "Medicines with Low Quantity", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No medicines with low quantity found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMedicineQuantity(int medicineId, int quantityToAdd) {
        String sql = "UPDATE Medicines SET quantity = quantity + ? WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityToAdd);
            pstmt.setInt(2, medicineId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Medicine quantity updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Medicine not found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class CustomerDAO {
    public void addCustomer(String name, String contactNumber, String email) {
        String sql = "INSERT INTO Customers (name, contact_number, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contactNumber);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listCustomers() {
        String sql = "SELECT * FROM Customers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder customers = new StringBuilder();
            while (rs.next()) {
                customers.append("ID: ").append(rs.getInt("customer_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Contact: ").append(rs.getString("contact_number"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(null, customers.toString(), "Customers", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class UserDAO {
    public void addUser (String name, String username, String password, String role) {
        String sql = "INSERT INTO Users (name, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "User  added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listUsers() {
        String sql = "SELECT * FROM Users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder users = new StringBuilder();
            while (rs.next()) {
                users.append("ID: ").append(rs.getInt("user_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Username: ").append(rs.getString("username"))
                        .append(", Role: ").append(rs.getString("role"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(null, users.toString(), "Users", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class TransactionDAO {
    public void recordTransaction(int medicineId, int customerId, int quantity) {
        String sql = "{CALL record_transaction(?, ?, ?)}"; // Calls the SQL procedure
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, medicineId);
            cstmt.setInt(2, customerId);
            cstmt.setInt(3, quantity);
            cstmt.execute();
            JOptionPane.showMessageDialog(null, "Transaction recorded successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listTransactions() {
        String sql = "SELECT * FROM Transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder transactions = new StringBuilder();
            while (rs.next()) {
                transactions.append("Transaction ID: ").append(rs.getInt("transaction_id"))
                        .append(", Medicine ID: ").append(rs.getInt("medicine_id"))
                        .append(", Customer ID: ").append(rs.getInt("customer_id"))
                        .append(", Quantity: ").append(rs.getInt("quantity"))
                        .append(", Total Price: ").append(rs.getDouble("total_price"))
                        .append(", Date: ").append(rs.getTimestamp("transaction_date"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(null, transactions.toString(), "Transactions", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class PharmacyManagementSystem extends JFrame {
    private MedicineDAO medicineDAO = new MedicineDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserDAO userDAO = new UserDAO();
    private String userRole;

    public PharmacyManagementSystem() {
        setTitle("Pharmacy Management System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        // Show login dialog first
        showLoginDialog();
    }

    private void showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            authenticateUser (username, password);
        } else {
            System.exit(0); // Exit if cancelled
        }
    }

    private void authenticateUser (String username, String password) {
        String sql = "SELECT role FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("role");
                setupMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                showLoginDialog(); // Show login again
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMainMenu() {
        // Add buttons for each functionality based on user role
        if ("Admin".equals(userRole)) {
            addAdminButtons();
        } else if ("Staff".equals(userRole)) {
            addStaffButtons();
        }
        setVisible(true);
    }

    private void addAdminButtons() {
        // Add buttons for Admin
        JButton addMedicineButton = new JButton("Add Medicine");
        JButton listMedicinesButton = new JButton("List Medicines");
        JButton addCustomerButton = new JButton("Add Customer");
        JButton listCustomersButton = new JButton("List Customers");
        JButton recordTransactionButton = new JButton("Record Transaction");
        JButton listTransactionsButton = new JButton("List Transactions");
        JButton addUserButton = new JButton("Add User");
        JButton listUsersButton = new JButton("List Users");
        JButton listLowQuantityMedicinesButton = new JButton("List Medicines with Low Quantity");
        JButton updateMedicineQuantityButton = new JButton("Update Medicine Quantity");
        JButton exitButton = new JButton("Exit");

        // Add action listeners for buttons
        addMedicineButton.addActionListener(e -> showAddMedicineDialog());
        listMedicinesButton.addActionListener(e -> medicineDAO.listMedicines());
        addCustomerButton.addActionListener(e -> showAddCustomerDialog());
        listCustomersButton.addActionListener(e -> customerDAO.listCustomers());
        recordTransactionButton.addActionListener(e -> showRecordTransactionDialog());
        listTransactionsButton.addActionListener(e -> transactionDAO.listTransactions());
        addUserButton.addActionListener(e -> showAddUserDialog());
        listUsersButton.addActionListener(e -> userDAO.listUsers());
        listLowQuantityMedicinesButton.addActionListener(e -> medicineDAO.listLessQuantityMedicines());
        updateMedicineQuantityButton.addActionListener(e -> showUpdateMedicineQuantityDialog());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the frame
        add(addMedicineButton);
        add(listMedicinesButton);
        add(addCustomerButton);
        add(listCustomersButton);
        add(recordTransactionButton);
        add(listTransactionsButton);
        add(addUserButton);
        add(listUsersButton);
        add(listLowQuantityMedicinesButton);
        add(updateMedicineQuantityButton);
        add(exitButton);
    }

    private void addStaffButtons() {
        // Add buttons for Staff
        JButton addMedicineButton = new JButton("Add Medicine");
        JButton listMedicinesButton = new JButton("List Medicines");
        JButton addCustomerButton = new JButton("Add Customer");
        JButton listCustomersButton = new JButton("List Customers");
        JButton recordTransactionButton = new JButton("Record Transaction");
        JButton listTransactionsButton = new JButton("List Transactions");
        JButton listLowQuantityMedicinesButton = new JButton("List Medicines with Low Quantity");
        JButton updateMedicineQuantityButton = new JButton("Update Medicine Quantity");
        JButton exitButton = new JButton("Exit");

        // Add action listeners for buttons
        addMedicineButton.addActionListener(e -> showAddMedicineDialog());
        listMedicinesButton.addActionListener(e -> medicineDAO.listMedicines());
        addCustomerButton.addActionListener(e -> showAddCustomerDialog());
        listCustomersButton.addActionListener(e -> customerDAO.listCustomers());
        recordTransactionButton.addActionListener(e -> showRecordTransactionDialog());
        listTransactionsButton.addActionListener(e -> transactionDAO.listTransactions());
        listLowQuantityMedicinesButton.addActionListener(e -> medicineDAO.listLessQuantityMedicines());
        updateMedicineQuantityButton.addActionListener(e -> showUpdateMedicineQuantityDialog());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the frame
        add(addMedicineButton);
        add(listMedicinesButton);
        add(addCustomerButton);
        add(listCustomersButton);
        add(recordTransactionButton);
        add(listTransactionsButton);
        add(listLowQuantityMedicinesButton);
        add(updateMedicineQuantityButton);
        add(exitButton);
    }

    private void showAddMedicineDialog() {
        JTextField nameField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expiryDateField = new JTextField();

        Object[] message = {
            "Name:", nameField,
            "Brand:", brandField,
            "Type:", typeField,
            "Quantity:", quantityField,
            "Price:", priceField,
            "Expiry Date (YYYY-MM-DD):", expiryDateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String brand = brandField.getText();
            String type = typeField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            String expiryDate = expiryDateField.getText();

            medicineDAO.addMedicine(name, brand, type, quantity, price, expiryDate);
        }
    }

    private void showAddCustomerDialog() {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
            "Name:", nameField,
            "Contact Number:", contactField,
            "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String contact = contactField.getText();
            String email = emailField.getText();

            customerDAO.addCustomer(name, contact, email);
        }
    }

    private void showRecordTransactionDialog() {
        JTextField medicineIdField = new JTextField();
        JTextField customerIdField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
            "Medicine ID:", medicineIdField,
            "Customer ID:", customerIdField,
            "Quantity:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Record Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int medicineId = Integer.parseInt(medicineIdField.getText());
            int customerId = Integer.parseInt(customerIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            transactionDAO.recordTransaction(medicineId, customerId, quantity);
        }
    }

    private void showAddUserDialog() {
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField roleField = new JTextField();

        Object[] message = {
            "Name:", nameField,
            "Username:", usernameField,
            "Password:", passwordField,
            "Role (Admin/Staff):", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleField.getText();

            userDAO.addUser (name, username, password, role);
        }
    }

    private void showUpdateMedicineQuantityDialog() {
        JTextField medicineIdField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
            "Medicine ID:", medicineIdField,
            "Quantity to Add:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Medicine Quantity", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int medicineId = Integer.parseInt(medicineIdField.getText());
            int quantityToAdd = Integer.parseInt(quantityField.getText());

            medicineDAO.updateMedicineQuantity(medicineId, quantityToAdd);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PharmacyManagementSystem::new);
    }
}
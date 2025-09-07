package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignupPage extends JPanel {
    public SignupPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIComponents.createBrandTitleLabel("Create Account");

        JTextField firstNameField = UIComponents.createTextField(20);
        JTextField lastNameField = UIComponents.createTextField(20);
        JTextField emailField = UIComponents.createTextField(20);
        JTextField phoneField = UIComponents.createTextField(20);
        JPasswordField passwordField = UIComponents.createPasswordField(20);
        JPasswordField confirmPasswordField = UIComponents.createPasswordField(20);
        
        JButton signupBtn = UIComponents.createButton("Sign Up");
        JButton loginBtn = new JButton("Already have an account? Login here");
        loginBtn.setForeground(UIComponents.BRAND_COLOR);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(UIComponents.createLabel("First Name:"), gbc);
        gbc.gridx = 1;
        add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(UIComponents.createLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(UIComponents.createLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(UIComponents.createLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(UIComponents.createLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(UIComponents.createLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(signupBtn, gbc);

        gbc.gridy = 8;
        add(loginBtn, gbc);

        signupBtn.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long accountNumber = generateAccountNumber();

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO AccountDetails(AccountNumber, FirstName, LastName, Email, PhoneNumber, Password, Balance) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setLong(1, accountNumber);
                stmt.setString(2, firstName);
                stmt.setString(3, lastName);
                stmt.setString(4, email);
                stmt.setString(5, phone);
                stmt.setString(6, password);
                stmt.setDouble(7, 0.0);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    UserSession.getInstance().setUserData(
                        accountNumber, firstName, lastName, email, phone, 0.0, null, null, null, null, null, null
                    );

                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully!\nYour Account Number: " + accountNumber + 
                        "\nPlease update your profile to receive your ₦10,000 welcome bonus!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                    firstNameField.setText("");
                    lastNameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");

                    cardLayout.show(mainPanel, "Profile");
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("duplicate key")) {
                    JOptionPane.showMessageDialog(this, "Email already exists!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
    }

    private long generateAccountNumber() {
        long min = 1000000000L;
        long max = 9999999999L;
        long accountNumber;
        boolean isUnique = false;

        try (Connection conn = DatabaseConnection.getConnection()) {
            do {
                accountNumber = min + (long) (Math.random() * (max - min + 1));
                String checkSql = "SELECT COUNT(*) FROM AccountDetails WHERE AccountNumber = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setLong(1, accountNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    isUnique = true;
                }
            } while (!isUnique);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to generate unique account number");
        }

        return accountNumber;
    }
}
package orion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JPanel {
    public LoginPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIComponents.createBrandTitleLabel("ORION");

        JTextField accountNumberField = UIComponents.createTextField(20);
        JPasswordField passwordField = UIComponents.createPasswordField(20);
        JButton loginBtn = UIComponents.createButton("Login");
        JButton signupBtn = new JButton("Don't have an account? Sign up here");
        signupBtn.setForeground(UIComponents.BRAND_COLOR);
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(UIComponents.createLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        add(accountNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(UIComponents.createLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        gbc.gridy = 4;
        add(signupBtn, gbc);

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }
        });

        loginBtn.addActionListener(e -> {
            String accountNumberText = accountNumberField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (accountNumberText.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long accountNumber;
            try {
                accountNumber = Long.parseLong(accountNumberText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid account number!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM AccountDetails WHERE AccountNumber = ? AND Password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setLong(1, accountNumber);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    UserSession.getInstance().setUserData(
                        rs.getLong("AccountNumber"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getDouble("Balance"),
                        rs.getString("ProfileImage"),
                        rs.getString("CardType"),
                        rs.getString("CardBank"),
                        rs.getString("CardNumber"),
                        rs.getString("CardExpiry"),
                        rs.getString("CardCVV")
                    );
                    
                    JOptionPane.showMessageDialog(this, 
                        "Welcome back, " + rs.getString("FirstName") + "!", 
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    accountNumberField.setText("");
                    passwordField.setText("");
                    
                    if (rs.getDouble("Balance") == 0.0) {
                        JOptionPane.showMessageDialog(this, 
                            "Please update your profile to receive your ₦10,000 welcome bonus!", 
                            "Update Profile", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "Profile");
                    } else {
                        cardLayout.show(mainPanel, "Dashboard");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid account number or password!", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        signupBtn.addActionListener(e -> cardLayout.show(mainPanel, "Signup"));
        
        refresh();
    }
    
    private void refresh() {
        SwingUtilities.invokeLater(() -> {
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText("");
                } else if (comp instanceof JPasswordField) {
                    ((JPasswordField) comp).setText("");
                }
            }
        });
    }
}
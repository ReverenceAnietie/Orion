package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TransferPage extends JPanel implements Main.Refreshable {
    public TransferPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIComponents.createTitleLabel("Transfer Money");

        JTextField recipientField = UIComponents.createTextField(20);
        
        String[] banks = {"ORION (Internal)", "Access Bank", "GTBank", "First Bank", "UBA", 
                         "Zenith Bank", "Fidelity Bank", "FCMB", "Sterling Bank", "Wema Bank", 
                         "Union Bank", "Heritage Bank", "Keystone Bank", "Polaris Bank", "Other Bank"};
        JComboBox<String> bankCombo = new JComboBox<>(banks);
        bankCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextField amountField = UIComponents.createTextField(20);
        JTextField descriptionField = UIComponents.createTextField(20);
        
        JButton transferBtn = UIComponents.createButton("Send Money");
        JButton backBtn = UIComponents.createSecondaryButton("Back to Dashboard");

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(UIComponents.createLabel("Recipient's Bank:"), gbc);
        gbc.gridx = 1;
        add(bankCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(UIComponents.createLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        add(recipientField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(UIComponents.createLabel("Amount (₦):"), gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(UIComponents.createLabel("Description (Optional):"), gbc);
        gbc.gridx = 1;
        add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(transferBtn, gbc);

        gbc.gridy = 6;
        add(backBtn, gbc);

        transferBtn.addActionListener(e -> {
            String recipientAccount = recipientField.getText().trim();
            String selectedBank = (String) bankCombo.getSelectedItem();
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (recipientAccount.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in recipient account and amount fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (recipientAccount.length() < 10) {
                JOptionPane.showMessageDialog(this, "Account number must be at least 10 digits!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserSession session = UserSession.getInstance();
            
            if (selectedBank.equals("ORION (Internal)") && 
                recipientAccount.equals(String.valueOf(session.getCurrentAccountNumber()))) {
                JOptionPane.showMessageDialog(this, "Cannot transfer to your own account!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount > session.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedBank.equals("ORION (Internal)")) {
                performInternalTransfer(recipientAccount, amount, description, session, mainPanel, cardLayout);
            } else {
                performExternalTransfer(recipientAccount, selectedBank, amount, description, session, mainPanel, cardLayout);
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        refresh();
    }
    
    private void performInternalTransfer(String recipientAccount, double amount, String description, 
                                         UserSession session, JPanel mainPanel, CardLayout cardLayout) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String checkRecipient = "SELECT AccountNumber, FirstName, LastName FROM AccountDetails WHERE AccountNumber = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkRecipient);
            checkStmt.setLong(1, Long.parseLong(recipientAccount));
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Recipient account not found!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long recipientAccountNum = rs.getLong("AccountNumber");
            String recipientName = rs.getString("FirstName") + " " + rs.getString("LastName");

            String updateSender = "UPDATE AccountDetails SET Balance = Balance - ? WHERE AccountNumber = ?";
            PreparedStatement senderStmt = conn.prepareStatement(updateSender);
            senderStmt.setDouble(1, amount);
            senderStmt.setLong(2, session.getCurrentAccountNumber());
            senderStmt.executeUpdate();

            String updateRecipient = "UPDATE AccountDetails SET Balance = Balance + ? WHERE AccountNumber = ?";
            PreparedStatement recipientStmt = conn.prepareStatement(updateRecipient);
            recipientStmt.setDouble(1, amount);
            recipientStmt.setLong(2, recipientAccountNum);
            recipientStmt.executeUpdate();

            String insertTransaction = "INSERT INTO Transactions(AccountNumber, Type, Amount, RecipientAccount, ReceiverBank, Description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
            transStmt.setLong(1, session.getCurrentAccountNumber());
            transStmt.setString(2, "Transfer Out");
            transStmt.setDouble(3, amount);
            transStmt.setString(4, recipientAccount);
            transStmt.setString(5, "ORION");
            transStmt.setString(6, description.isEmpty() ? "Transfer to " + recipientName : description);
            transStmt.executeUpdate();

            transStmt.setLong(1, recipientAccountNum);
            transStmt.setString(2, "Transfer In");
            transStmt.setString(6, description.isEmpty() ? "Transfer from " + session.getFirstName() + " " + session.getLastName() : description);
            transStmt.executeUpdate();

            conn.commit();

            session.setBalance(session.getBalance() - amount);

            JOptionPane.showMessageDialog(this, 
                "Transfer successful!\n₦" + String.format("%.2f", amount) + " sent to " + recipientName + 
                "\nBank: ORION", 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();
            cardLayout.show(mainPanel, "Dashboard");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid account number format!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Transfer failed: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void performExternalTransfer(String recipientAccount, String bank, double amount, String description, 
                                       UserSession session, JPanel mainPanel, CardLayout cardLayout) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String updateSender = "UPDATE AccountDetails SET Balance = Balance - ? WHERE AccountNumber = ?";
            PreparedStatement senderStmt = conn.prepareStatement(updateSender);
            senderStmt.setDouble(1, amount);
            senderStmt.setLong(2, session.getCurrentAccountNumber());
            senderStmt.executeUpdate();

            String insertTransaction = "INSERT INTO Transactions(AccountNumber, Type, Amount, RecipientAccount, ReceiverBank, Description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
            transStmt.setLong(1, session.getCurrentAccountNumber());
            transStmt.setString(2, "Transfer Out");
            transStmt.setDouble(3, amount);
            transStmt.setString(4, recipientAccount);
            transStmt.setString(5, bank);
            transStmt.setString(6, description.isEmpty() ? "External transfer to " + bank : description);
            transStmt.executeUpdate();

            conn.commit();

            session.setBalance(session.getBalance() - amount);

            JOptionPane.showMessageDialog(this, 
                "External transfer initiated!\n₦" + String.format("%.2f", amount) + " sent to account " + recipientAccount + 
                "\nBank: " + bank + "\n\nNote: External transfers may take 1-3 business days to process.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();
            cardLayout.show(mainPanel, "Dashboard");

        } catch (SQLException ex) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Transfer failed: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void clearFields() {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            }
        }
    }
    
    @Override
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            UserSession session = UserSession.getInstance();
            if (session.isLoggedIn()) {
                clearFields();
            }
        });
    }
}
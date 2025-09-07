package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddMoneyPage extends JPanel implements Main.Refreshable {
    private JComboBox<String> paymentMethodCombo;
    
    public AddMoneyPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIComponents.createTitleLabel("Add Money");

        JTextField amountField = UIComponents.createTextField(20);
        
        String[] paymentMethods = {"Debit", "Credit"};
        paymentMethodCombo = new JComboBox<>(paymentMethods);
        paymentMethodCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        JButton addMoneyBtn = UIComponents.createButton("Add Money");
        JButton backBtn = UIComponents.createSecondaryButton("Back to Dashboard");

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(UIComponents.createLabel("Amount (₦):"), gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(UIComponents.createLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        add(paymentMethodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(UIComponents.createLabel("Description (Optional):"), gbc);
        gbc.gridx = 1;
        add(descScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(addMoneyBtn, gbc);

        gbc.gridy = 5;
        add(backBtn, gbc);

        addMoneyBtn.addActionListener(e -> {
            String amountText = amountField.getText().trim();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter amount to add!", 
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

            if (amount > 10000) {
                JOptionPane.showMessageDialog(this, "Maximum add money limit is ₦10,000 per transaction!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserSession session = UserSession.getInstance();
            if (session.getCardNumber() == null || session.getCardType() == null) {
                JOptionPane.showMessageDialog(this, "No card saved. Please update card details in profile!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "Profile");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                String updateBalance = "UPDATE AccountDetails SET Balance = Balance + ? WHERE AccountNumber = ?";
                PreparedStatement balanceStmt = conn.prepareStatement(updateBalance);
                balanceStmt.setDouble(1, amount);
                balanceStmt.setLong(2, session.getCurrentAccountNumber());
                balanceStmt.executeUpdate();

                String insertAddMoney = "INSERT INTO AddMoney(AccountNumber, Amount, PaymentMethod) VALUES (?, ?, ?)";
                PreparedStatement addMoneyStmt = conn.prepareStatement(insertAddMoney);
                addMoneyStmt.setLong(1, session.getCurrentAccountNumber());
                addMoneyStmt.setDouble(2, amount);
                addMoneyStmt.setString(3, paymentMethod);
                addMoneyStmt.executeUpdate();

                String insertTransaction = "INSERT INTO Transactions(AccountNumber, Type, Amount, Description) VALUES (?, ?, ?, ?)";
                PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
                transStmt.setLong(1, session.getCurrentAccountNumber());
                transStmt.setString(2, "Add Money");
                transStmt.setDouble(3, amount);
                transStmt.setString(4, description.isEmpty() ? "Added money via " + paymentMethod : description);
                transStmt.executeUpdate();

                conn.commit();

                session.setBalance(session.getBalance() + amount);

                JOptionPane.showMessageDialog(this, 
                    "Money added successfully!\n₦" + String.format("%.2f", amount) + " added to your account\n" +
                    "Payment Method: " + paymentMethod, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                amountField.setText("");
                descriptionArea.setText("");
                paymentMethodCombo.setSelectedIndex(0);

                cardLayout.show(mainPanel, "Dashboard");

            } catch (SQLException ex) {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Add money failed: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        refresh();
    }
    
    @Override
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            UserSession session = UserSession.getInstance();
            if (session.isLoggedIn()) {
                if (session.getCardType() != null) {
                    paymentMethodCombo.setSelectedItem(session.getCardType());
                    findAddMoneyButton().setEnabled(true);
                } else {
                    paymentMethodCombo.setSelectedIndex(0);
                    findAddMoneyButton().setEnabled(false);
                }
            }
        });
    }
    
    private JButton findAddMoneyButton() {
        for (Component c : getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals("Add Money")) {
                return (JButton) c;
            }
        }
        return null;
    }
}
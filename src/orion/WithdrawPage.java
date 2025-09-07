package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WithdrawPage extends JPanel implements Main.Refreshable {
    public WithdrawPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIComponents.createTitleLabel("Withdraw Money");

        JTextField amountField = UIComponents.createTextField(20);
        
        String[] banks = {"Access Bank", "GTBank", "First Bank", "UBA", 
                          "Zenith Bank", "Fidelity Bank", "FCMB", "Sterling Bank", "Wema Bank", 
                          "Union Bank", "Heritage Bank", "Keystone Bank", "Polaris Bank", "Other Bank"};
        JComboBox<String> bankCombo = new JComboBox<>(banks);
        bankCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField accountField = UIComponents.createTextField(20);
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        JButton withdrawBtn = UIComponents.createButton("Withdraw");
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
        add(UIComponents.createLabel("Withdrawal Bank:"), gbc);
        gbc.gridx = 1;
        add(bankCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        add(UIComponents.createLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        add(accountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(UIComponents.createLabel("Description (Optional):"), gbc);
        gbc.gridx = 1;
        add(descScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(withdrawBtn, gbc);

        gbc.gridy = 6;
        add(backBtn, gbc);

        withdrawBtn.addActionListener(e -> {
            String amountText = amountField.getText().trim();
            String withdrawBank = (String) bankCombo.getSelectedItem();
            String withdrawAccount = accountField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (amountText.isEmpty() || withdrawAccount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter withdrawal amount and account details!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (withdrawAccount.length() < 10) {
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
            
            if (amount > session.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!\nYour current balance is: ₦" + 
                    String.format("%.2f", session.getBalance()), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount > 5000) {
                JOptionPane.showMessageDialog(this, "Daily withdrawal limit is ₦5,000!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                String updateBalance = "UPDATE AccountDetails SET Balance = Balance - ? WHERE AccountNumber = ?";
                PreparedStatement balanceStmt = conn.prepareStatement(updateBalance);
                balanceStmt.setDouble(1, amount);
                balanceStmt.setLong(2, session.getCurrentAccountNumber());
                balanceStmt.executeUpdate();

                String insertTransaction = "INSERT INTO Transactions(AccountNumber, Type, Amount, RecipientAccount, ReceiverBank, Description) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
                transStmt.setLong(1, session.getCurrentAccountNumber());
                transStmt.setString(2, "Withdrawal");
                transStmt.setDouble(3, amount);
                transStmt.setString(4, withdrawAccount);
                transStmt.setString(5, withdrawBank);
                transStmt.setString(6, description.isEmpty() ? "Cash withdrawal" : description);
                transStmt.executeUpdate();

                conn.commit();

                session.setBalance(session.getBalance() - amount);

                JOptionPane.showMessageDialog(this, 
                    "Withdrawal successful!\n₦" + String.format("%.2f", amount) + " withdrawn from your account\n" +
                    "New balance: ₦" + String.format("%.2f", session.getBalance()), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                amountField.setText("");
                descriptionArea.setText("");
                accountField.setText("");
                bankCombo.setSelectedIndex(0);

                cardLayout.show(mainPanel, "Dashboard");

            } catch (SQLException ex) {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Withdrawal failed: " + ex.getMessage(), 
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
                Component[] components = getComponents();
                for (Component comp : components) {
                    if (comp instanceof JTextField) {
                        ((JTextField) comp).setText("");
                    } else if (comp instanceof JScrollPane) {
                        JScrollPane scroll = (JScrollPane) comp;
                        if (scroll.getViewport().getView() instanceof JTextArea) {
                            ((JTextArea) scroll.getViewport().getView()).setText("");
                        }
                    } else if (comp instanceof JComboBox) {
                        ((JComboBox<?>) comp).setSelectedIndex(0);
                    }
                }
            }
        });
    }
}
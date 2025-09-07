package orion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class HistoryPage extends JPanel implements Main.Refreshable {
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public HistoryPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);

        JLabel titleLabel = UIComponents.createTitleLabel("Transaction History");
        
        String[] columnNames = {"Date", "Type", "Amount (₦)", "Recipient", "Bank", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(30);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(UIComponents.BRAND_COLOR);
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backBtn = UIComponents.createSecondaryButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        bottomPanel.add(backBtn);

        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadTransactionData();
    }

    private void loadTransactionData() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            return;
        }

        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT TransactionDate, Type, Amount, RecipientAccount, ReceiverBank, Description " +
                        "FROM Transactions WHERE AccountNumber = ? ORDER BY TransactionDate DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, session.getCurrentAccountNumber());
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            while (rs.next()) {
                String date = dateFormat.format(rs.getTimestamp("TransactionDate"));
                String type = rs.getString("Type");
                double amount = rs.getDouble("Amount");
                String recipient = rs.getString("RecipientAccount") != null ? rs.getString("RecipientAccount") : "-";
                String bank = rs.getString("ReceiverBank") != null ? rs.getString("ReceiverBank") : "-";
                String description = rs.getString("Description");

                tableModel.addRow(new Object[]{
                    date,
                    type,
                    String.format("₦%.2f", amount),
                    recipient,
                    bank,
                    description
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            loadTransactionData();
        });
    }
}
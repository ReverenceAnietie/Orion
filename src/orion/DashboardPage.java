package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.net.URL;

public class DashboardPage extends JPanel implements Main.Refreshable {
    private JLabel balanceLabel;
    private JLabel welcomeLabel;
    private JButton profileBtn;
    
    public DashboardPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);

        JPanel topPanel = createTopPanel();
        
        JPanel balanceCard = createBalanceCard();
        
        JPanel actionsPanel = createActionsPanel(mainPanel, cardLayout);
        
        JPanel bottomPanel = createBottomPanel(mainPanel, cardLayout);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        contentPanel.add(topPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(balanceCard);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(actionsPanel);

        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateUserInfo();
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        
        welcomeLabel = new JLabel("Welcome!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.BLACK);
        
        profileBtn = new JButton("Profile");
        profileBtn.setFont(new Font("Arial", Font.BOLD, 14));
        profileBtn.setForeground(Color.BLACK);
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        UserSession session = UserSession.getInstance();
        String imgPath = session.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            if (icon.getImage() != null) {
                Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                profileBtn.setIcon(new ImageIcon(scaled));
            } else {
                System.err.println("User profile image could not be loaded: " + imgPath);
                profileBtn.setText("Profile (No Image)");
            }
        } else {
            URL defaultIconUrl = getClass().getResource("resources/defaultprofileicon.png");
            if (defaultIconUrl != null) {
                ImageIcon defaultIcon = new ImageIcon(defaultIconUrl);
                Image scaled = defaultIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                profileBtn.setIcon(new ImageIcon(scaled));
            } else {
                System.err.println("Default profile icon not found at resources/defaultprofileicon.png! Check src/orion/resources/");
                profileBtn.setText("Profile (No Icon)");
            }
        }
        
        profileBtn.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "Profile");
        });
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(profileBtn, BorderLayout.EAST);
        
        return topPanel;
    }
    
    private JPanel createBalanceCard() {
        JPanel balanceCard = UIComponents.createBalanceCard();
        balanceCard.setLayout(new BorderLayout());
        
        JLabel balanceTitleLabel = new JLabel("Current Balance", SwingConstants.CENTER);
        balanceTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceTitleLabel.setForeground(Color.WHITE);
        
        balanceLabel = new JLabel("₦0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 28));
        balanceLabel.setForeground(Color.WHITE);
        
        JPanel balanceContent = new JPanel();
        balanceContent.setLayout(new BoxLayout(balanceContent, BoxLayout.Y_AXIS));
        balanceContent.setBackground(UIComponents.BRAND_COLOR);
        balanceContent.add(Box.createVerticalStrut(10));
        balanceContent.add(balanceTitleLabel);
        balanceContent.add(Box.createVerticalStrut(5));
        balanceContent.add(balanceLabel);
        balanceContent.add(Box.createVerticalStrut(10));
        
        balanceCard.add(balanceContent, BorderLayout.CENTER);
        
        return balanceCard;
    }
    
    private JPanel createActionsPanel(JPanel mainPanel, CardLayout cardLayout) {
        JPanel actionsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        actionsPanel.setBackground(UIComponents.BACKGROUND_WHITE);

        JButton transferBtn = createActionButton("Transfer", true);
        URL transferIconUrl = getClass().getResource("resources/transfericon.png");
        if (transferIconUrl != null) {
            transferBtn.setIcon(new ImageIcon(transferIconUrl));

        } else {
            System.err.println("Transfer icon not found at resources/transfericon.png! Check src/orion/resources/");
            transferBtn.setText("Transfer (No Icon)");
        }
        transferBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 

        JButton addMoneyBtn = createActionButton("Add Money", true);
        URL addMoneyIconUrl = getClass().getResource("resources/addmoneyicon.png");
        if (addMoneyIconUrl != null) {
            addMoneyBtn.setIcon(new ImageIcon(addMoneyIconUrl));

        } else {
            System.err.println("Add Money icon not found at resources/addmoneyicon.png! Check src/orion/resources/");
            addMoneyBtn.setText("Add Money (No Icon)");
        }
        addMoneyBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 

        JButton withdrawBtn = createActionButton("Withdraw", true);
        URL withdrawIconUrl = getClass().getResource("resources/withdrawicon.png");
        if (withdrawIconUrl != null) {
            withdrawBtn.setIcon(new ImageIcon(withdrawIconUrl));
           
        } else {
            System.err.println("Withdraw icon not found at resources/withdrawicon.png! Check src/orion/resources/");
            withdrawBtn.setText("Withdraw (No Icon)");
        }
        withdrawBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 

        JButton historyBtn = createActionButton("History", false);
        URL historyIconUrl = getClass().getResource("resources/historyicon.png");
        if (historyIconUrl != null) {
            historyBtn.setIcon(new ImageIcon(historyIconUrl));
           
        } else {
            System.err.println("History icon not found at resources/historyicon.png! Check src/orion/resources/");
            historyBtn.setText("History (No Icon)");
        }
        historyBtn.setHorizontalTextPosition(SwingConstants.RIGHT);

        JButton aboutBtn = createActionButton("About", false);
        URL aboutIconUrl = getClass().getResource("resources/abouticon.png");
        if (aboutIconUrl != null) {
            aboutBtn.setIcon(new ImageIcon(aboutIconUrl));
            
        } else {
            System.err.println("About icon not found at resources/abouticon.png! Check src/orion/resources/");
            aboutBtn.setText("About (No Icon)");
        }
        aboutBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 

        actionsPanel.add(transferBtn);
        actionsPanel.add(addMoneyBtn);
        actionsPanel.add(withdrawBtn);
        actionsPanel.add(historyBtn);
        actionsPanel.add(aboutBtn);

        transferBtn.addActionListener(e -> {
            updateUserInfo();
            cardLayout.show(mainPanel, "Transfer");
        });
        
        addMoneyBtn.addActionListener(e -> {
            updateUserInfo(); 
            cardLayout.show(mainPanel, "AddMoney");
        });
        
        withdrawBtn.addActionListener(e -> {
            updateUserInfo(); 
            cardLayout.show(mainPanel, "Withdraw");
        });
        
        historyBtn.addActionListener(e -> {
            updateUserInfo(); 
            cardLayout.show(mainPanel, "History");
        });
        
        aboutBtn.addActionListener(e -> {
            updateUserInfo();
            cardLayout.show(mainPanel, "About");
        });
        
        return actionsPanel;
    }
    
    private JButton createActionButton(String text, boolean isPrimary) {
        JButton button = UIComponents.createCardButton(text, isPrimary);
        button.setPreferredSize(new Dimension(150, 80));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        return button;
    }
    
    private JPanel createBottomPanel(JPanel mainPanel, CardLayout cardLayout) {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton logoutBtn = UIComponents.createLogoutButton("Logout");
        bottomPanel.add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                UserSession.getInstance().clearSession();
                cardLayout.show(mainPanel, "Login");
            }
        });
        
        return bottomPanel;
    }
    
    public void updateUserInfo() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + session.getFirstName() + " " + session.getLastName() + "!");
            balanceLabel.setText("₦" + String.format("%.2f", session.getBalance()));
            
            String imgPath = session.getProfileImage();
            if (imgPath != null && !imgPath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imgPath);
                if (icon.getImage() != null) {
                    Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    profileBtn.setIcon(new ImageIcon(scaled));
                    
                } else {
                    System.err.println("User profile image could not be loaded: " + imgPath);
                    profileBtn.setText("Profile (No Image)");
                }
            } else {
                URL defaultIconUrl = getClass().getResource("resources/defaultprofileicon.png");
                if (defaultIconUrl != null) {
                    ImageIcon defaultIcon = new ImageIcon(defaultIconUrl);
                    Image scaled = defaultIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    profileBtn.setIcon(new ImageIcon(scaled));
                    
                } else {
                    System.err.println("Default profile icon not found at resources/defaultprofileicon.png! Check src/orion/resources/");
                    profileBtn.setText("Profile (No Icon)");
                }
            }
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT Balance FROM AccountDetails WHERE AccountNumber = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setLong(1, session.getCurrentAccountNumber());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double currentBalance = rs.getDouble("Balance");
                    session.setBalance(currentBalance);
                    balanceLabel.setText("₦" + String.format("%.2f", currentBalance));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void refresh() {
        updateUserInfo();
    }
}
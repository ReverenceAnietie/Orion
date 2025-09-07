package orion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    private static final String APP_TITLE = "ORION";
    private static final Dimension MIN_SIZE = new Dimension(500, 500);
    private static final Dimension DEFAULT_SIZE = new Dimension(700, 600);
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private Map<String, JPanel> pages;
    private Map<String, Refreshable> refreshablePages;
    
    private DashboardPage dashboardPage;
    private HistoryPage historyPage;
    private ProfilePage profilePage;
    private WithdrawPage withdrawPage;
    private TransferPage transferPage;
    private AddMoneyPage addMoneyPage;
    private AboutPage aboutPage;
    
    public Main() {
        initializeFrame();
        initializeComponents();
        setupEventListeners();
        finalizeSetup();
    }
    
    private static void setSystemLookAndFeel() {
        String osName = System.getProperty("os.name").toLowerCase();
        String lookAndFeelClass = null;
        String lafName = null;
        
        try {
            if (osName.contains("windows")) {
                lookAndFeelClass = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
                lafName = "Windows";
            } else if (osName.contains("mac")) {
                lookAndFeelClass = "com.apple.laf.AquaLookAndFeel";
                lafName = "Aqua";
            } else if (osName.contains("linux") || osName.contains("unix")) {
                lookAndFeelClass = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                lafName = "GTK";
            }
            
            if (lookAndFeelClass != null) {
                UIManager.setLookAndFeel(lookAndFeelClass);
                LOGGER.info(lafName + " look and feel applied successfully");
                return;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "OS-specific look and feel not available: " + lafName, e);
        }
        
        // Fallback 1: Try Nimbus (modern and cross-platform)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            LOGGER.info("Nimbus look and feel applied as fallback");
            return;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Nimbus look and feel not available", e);
        }
        
        // Fallback 2: Try to find system-related LAF
        try {
            UIManager.LookAndFeelInfo[] installedLAFs = UIManager.getInstalledLookAndFeels();
            
            // First, try to find system-related LAF
            for (UIManager.LookAndFeelInfo info : installedLAFs) {
                String name = info.getName().toLowerCase();
                if (name.contains("system") || name.contains("windows") || 
                    name.contains("gtk") || name.contains("aqua")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    LOGGER.info("System-like look and feel applied: " + info.getName());
                    return;
                }
            }
            
            // Then try Nimbus if available
            for (UIManager.LookAndFeelInfo info : installedLAFs) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    LOGGER.info("Nimbus look and feel applied from installed LAFs");
                    return;
                }
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Could not iterate through installed look and feels", e);
        }
        
        // Fallback 3: Use Metal (always available)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            LOGGER.info("Metal look and feel applied as final fallback");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set Metal look and feel - using default", e);
        }
    }

    private void initializeFrame() {
        setTitle(APP_TITLE);
        setSize(DEFAULT_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(MIN_SIZE);
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        pages = new HashMap<>();
        refreshablePages = new HashMap<>();

        LoginPage loginPage = new LoginPage(mainPanel, cardLayout);
        SignupPage signupPage = new SignupPage(mainPanel, cardLayout);
        dashboardPage = new DashboardPage(mainPanel, cardLayout);
        profilePage = new ProfilePage(mainPanel, cardLayout);
        transferPage = new TransferPage(mainPanel, cardLayout);
        addMoneyPage = new AddMoneyPage(mainPanel, cardLayout);
        withdrawPage = new WithdrawPage(mainPanel, cardLayout);
        historyPage = new HistoryPage(mainPanel, cardLayout);
        aboutPage = new AboutPage(mainPanel, cardLayout);

        pages.put("Login", loginPage);
        mainPanel.add(loginPage, "Login");
        pages.put("Signup", signupPage);
        mainPanel.add(signupPage, "Signup");
        pages.put("Dashboard", dashboardPage);
        mainPanel.add(dashboardPage, "Dashboard");
        pages.put("Profile", profilePage);
        mainPanel.add(profilePage, "Profile");
        pages.put("Transfer", transferPage);
        mainPanel.add(transferPage, "Transfer");
        pages.put("AddMoney", addMoneyPage);
        mainPanel.add(addMoneyPage, "AddMoney");
        pages.put("Withdraw", withdrawPage);
        mainPanel.add(withdrawPage, "Withdraw");
        pages.put("History", historyPage);
        mainPanel.add(historyPage, "History");
        pages.put("About", aboutPage);
        mainPanel.add(aboutPage, "About");

        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            if (entry.getValue() instanceof Refreshable) {
                refreshablePages.put(entry.getKey(), (Refreshable) entry.getValue());
            }
        }

        add(mainPanel);
    }

    private void setupEventListeners() {
        mainPanel.addPropertyChangeListener("clientPropertyChange", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                
            }
        });

        for (Map.Entry<String, Refreshable> entry : refreshablePages.entrySet()) {
            JPanel page = pages.get(entry.getKey());
            page.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    entry.getValue().refresh();
                }
            });
        }
    }

    private void finalizeSetup() {
        cardLayout.show(mainPanel, "Login");
        SwingUtilities.invokeLater(this::testDatabaseConnection);
    }

    private void testDatabaseConnection() {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
                    return conn != null && !conn.isClosed();
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        LOGGER.info("Database connection test successful");
                        showWelcomeMessage();
                    } else {
                        throw new Exception("Connection is null or closed");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Database connection failed", e);
                    showDatabaseError(e);
                }
            }
        }.execute();
    }
    
    private void showWelcomeMessage() {
        String message = String.format(
            "Welcome to ORION!\nCopy your account number after creating your account to enable you login"
        );
        
        JOptionPane.showMessageDialog(this, message, "System Ready", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showDatabaseError(Exception e) {
        String message = String.format(
            "Database connection failed!\n\n" +
            "Please ensure:\n" +
            "1. PostgreSQL is running on localhost:5432\n" +
            "2. Database 'ORION_db' exists\n" +
            "3. Username: postgres, Password: Admin\n" +
            "4. Database tables will be created automatically\n" +
            "5. JDBC driver is in classpath\n\n" +
            "Error: %s", 
            e.getMessage()
        );
        
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInitializationError(Exception e) {
        String message = String.format(
            "Failed to initialize application components!\n\n" +
            "Error: %s\n\n" +
            "Please check:\n" +
            "1. All required class files are present\n" +
            "2. UI components are properly configured\n" +
            "3. All dependencies are available", 
            e.getMessage()
        );
        
        JOptionPane.showMessageDialog(null, message, "Initialization Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        
        setSystemLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            DatabaseConnection.ensureDatabaseSchema();
            try {
                Main mainFrame = new Main();
                mainFrame.setVisible(true);
                
                LOGGER.info("ORION started successfully");
                System.out.println("ORION");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Application failed to start", e);
                showStartupError(e);
                System.exit(1);
            }
        });
    }
    
    private static void showStartupError(Exception e) {
        String message = String.format(
            "Application failed to start!\n\n" +
            "Error: %s\n\n" +
            "Please check:\n" +
            "1. All required class files are present\n" +
            "2. PostgreSQL JDBC driver is in classpath\n" +
            "3. Database is properly configured\n" +
            "4. Java version is compatible (Java 8+)\n" +
            "5. All UI component classes are available", 
            e.getMessage()
        );
        
        JOptionPane.showMessageDialog(null, message, "Startup Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public interface Refreshable {
        void refresh();
    }
}
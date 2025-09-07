package orion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ProfilePage extends JPanel implements Main.Refreshable {
    private JLabel accountNumberLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel balanceLabel;
    private JLabel iconLabel;
    
    public ProfilePage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(UIComponents.BACKGROUND_WHITE);
        iconLabel = new JLabel();
        UserSession session = UserSession.getInstance();
        String imgPath = session.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setIcon(new ImageIcon("path/to/default_profile_icon.png")); 
        }
        JLabel titleLabel = UIComponents.createTitleLabel("Profile Information");
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel infoPanel = UIComponents.createWhiteCard();
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        accountNumberLabel = new JLabel();
        nameLabel = new JLabel();
        emailLabel = new JLabel();
        phoneLabel = new JLabel();
        balanceLabel = new JLabel();

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font valueFont = new Font("Arial", Font.PLAIN, 14);

        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setFont(labelFont);
        JLabel nameFieldLabel = new JLabel("Full Name:");
        nameFieldLabel.setFont(labelFont);
        JLabel emailFieldLabel = new JLabel("Email Address:");
        emailFieldLabel.setFont(labelFont);
        JLabel phoneFieldLabel = new JLabel("Phone Number:");
        phoneFieldLabel.setFont(labelFont);
        JLabel balanceFieldLabel = new JLabel("Current Balance:");
        balanceFieldLabel.setFont(labelFont);

        accountNumberLabel.setFont(valueFont);
        nameLabel.setFont(valueFont);
        emailLabel.setFont(valueFont);
        phoneLabel.setFont(valueFont);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(UIComponents.BRAND_COLOR);

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(accLabel, gbc);
        gbc.gridx = 1;
        infoPanel.add(accountNumberLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(nameFieldLabel, gbc);
        gbc.gridx = 1;
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(emailFieldLabel, gbc);
        gbc.gridx = 1;
        infoPanel.add(emailLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(phoneFieldLabel, gbc);
        gbc.gridx = 1;
        infoPanel.add(phoneLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(balanceFieldLabel, gbc);
        gbc.gridx = 1;
        infoPanel.add(balanceLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        JButton refreshBtn = UIComponents.createButton("Refresh Profile");
        JButton editBtn = UIComponents.createButton("Edit Profile");
        JButton backBtn = UIComponents.createSecondaryButton("Back to Dashboard");
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(backBtn);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        loadProfileData();

        refreshBtn.addActionListener(e -> loadProfileData());
        editBtn.addActionListener(e -> editProfile());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
    }

    private void loadProfileData() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            return;
        }

        accountNumberLabel.setText(String.valueOf(session.getCurrentAccountNumber()));
        nameLabel.setText(session.getFirstName() + " " + session.getLastName());
        emailLabel.setText(session.getEmail());
        phoneLabel.setText(session.getPhoneNumber() != null ? session.getPhoneNumber() : "Not set");
        balanceLabel.setText("₦" + String.format("%.2f", session.getBalance()));

        String imgPath = session.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setIcon(new ImageIcon("path/to/default_profile_icon.png"));
        }
    }

    private void editProfile() {
        UserSession session = UserSession.getInstance();

        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Profile", true);
        editDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField firstNameField = UIComponents.createTextField(15);
        firstNameField.setText(session.getFirstName());
        JTextField lastNameField = UIComponents.createTextField(15);
        lastNameField.setText(session.getLastName());
        JTextField emailField = UIComponents.createTextField(20);
        emailField.setText(session.getEmail());
        JTextField phoneField = UIComponents.createTextField(20);
        phoneField.setText(session.getPhoneNumber());

        JButton uploadBtn = UIComponents.createSecondaryButton("Upload Profile Image");
        JLabel imageLabel = new JLabel("No new image selected");
        final String[] newImagePath = {null};

        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(editDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    Path uploadsDir = Paths.get("uploads");
                    if (!Files.exists(uploadsDir)) {
                        Files.createDirectory(uploadsDir);
                    }

                    Path destPath = uploadsDir.resolve(selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                    newImagePath[0] = destPath.toString();
                    imageLabel.setText(selectedFile.getName() + " selected");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Error uploading image: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Card details
        String[] cardTypes = {"Debit", "Credit"};
        JComboBox<String> cardTypeCombo = new JComboBox<>(cardTypes);
        cardTypeCombo.setSelectedItem(session.getCardType());
        cardTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        String[] banks = {"Access Bank", "GTBank", "First Bank", "UBA", 
                          "Zenith Bank", "Fidelity Bank", "FCMB", "Sterling Bank", "Wema Bank", 
                          "Union Bank", "Heritage Bank", "Keystone Bank", "Polaris Bank", "Other Bank"};
        JComboBox<String> cardBankField = new JComboBox<>(banks);
        cardBankField.setSelectedItem(session.getCardBank());
        cardBankField.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField cardNumberField = UIComponents.createTextField(20);
        cardNumberField.setText(session.getCardNumber());
        JTextField cardExpiryField = UIComponents.createTextField(20);
        cardExpiryField.setText(session.getCardExpiry());
        JPasswordField cardCvvField = UIComponents.createPasswordField(20);
        cardCvvField.setText(session.getCardCVV());

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        editDialog.add(UIComponents.createLabel("First Name:"), gbc);
        gbc.gridx = 1;
        editDialog.add(firstNameField, gbc);
        gbc.gridx = 2;
        editDialog.add(UIComponents.createLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        editDialog.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        editDialog.add(UIComponents.createLabel("Email:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        editDialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        editDialog.add(UIComponents.createLabel("Phone Number:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        editDialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        editDialog.add(UIComponents.createLabel("Profile Image:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        editDialog.add(uploadBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        editDialog.add(imageLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 1;
        editDialog.add(UIComponents.createLabel("Card Type:"), gbc);
        gbc.gridx = 1;
        editDialog.add(cardTypeCombo, gbc);
        gbc.gridx = 2;
        editDialog.add(UIComponents.createLabel("Card Bank:"), gbc);
        gbc.gridx = 3;
        editDialog.add(cardBankField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        editDialog.add(UIComponents.createLabel("Card Number:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        editDialog.add(cardNumberField, gbc);

        // Card Expiry and CVV side by side
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 1;
        editDialog.add(UIComponents.createLabel("Expiry (MM/YY):"), gbc);
        gbc.gridx = 1;
        editDialog.add(cardExpiryField, gbc);
        gbc.gridx = 2;
        editDialog.add(UIComponents.createLabel("CVV:"), gbc);
        gbc.gridx = 3;
        editDialog.add(cardCvvField, gbc);

        JButton saveBtn = UIComponents.createButton("Save Changes");
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        editDialog.add(saveBtn, gbc);

        gbc.gridx = 2;
        editDialog.add(cancelBtn, gbc);

        saveBtn.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String cardType = (String) cardTypeCombo.getSelectedItem();
            String cardBank = (String) cardBankField.getSelectedItem();
            String cardNumber = cardNumberField.getText().trim();
            String cardExpiry = cardExpiryField.getText().trim();
            String cardCvv = new String(cardCvvField.getPassword()).trim();

            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "Please fill in all required fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(editDialog, "Please enter a valid email address!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Card validation if provided
            if (!cardNumber.isEmpty()) {
                if (cardNumber.length() < 13 || cardNumber.length() > 19) {
                    JOptionPane.showMessageDialog(editDialog, "Card number must be between 13-19 digits!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cardExpiry.isEmpty() || !cardExpiry.matches("\\d{2}/\\d{2}")) {
                    JOptionPane.showMessageDialog(editDialog, "Expiry must be in MM/YY format!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cardCvv.isEmpty() || cardCvv.length() < 3 || cardCvv.length() > 4) {
                    JOptionPane.showMessageDialog(editDialog, "CVV must be 3-4 digits!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE AccountDetails SET FirstName = ?, LastName = ?, Email = ?, PhoneNumber = ?, ProfileImage = ?, CardType = ?, CardBank = ?, CardNumber = ?, CardExpiry = ?, CardCVV = ? WHERE AccountNumber = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, newImagePath[0] != null ? newImagePath[0] : session.getProfileImage());
                stmt.setString(6, cardType);
                stmt.setString(7, cardBank);
                stmt.setString(8, cardNumber);
                stmt.setString(9, cardExpiry);
                stmt.setString(10, cardCvv);
                stmt.setLong(11, session.getCurrentAccountNumber());
                
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Update session
                    session.setFirstName(firstName);
                    session.setLastName(lastName);
                    session.setEmail(email);
                    session.setPhoneNumber(phone);
                    session.setProfileImage(newImagePath[0] != null ? newImagePath[0] : session.getProfileImage());
                    session.setCardType(cardType);
                    session.setCardBank(cardBank);
                    session.setCardNumber(cardNumber);
                    session.setCardExpiry(cardExpiry);
                    session.setCardCVV(cardCvv);
                    
                    // Check for welcome bonus
                    double currentBalance = session.getBalance();
                    if (currentBalance == 0.0 && !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                        conn.setAutoCommit(false);
                        String bonusSql = "UPDATE AccountDetails SET Balance = Balance + 10000 WHERE AccountNumber = ?";
                        PreparedStatement bonusStmt = conn.prepareStatement(bonusSql);
                        bonusStmt.setLong(1, session.getCurrentAccountNumber());
                        bonusStmt.executeUpdate();
                        
                        String transSql = "INSERT INTO Transactions(AccountNumber, Type, Amount, Description) VALUES (?, ?, ?, ?)";
                        PreparedStatement transStmt = conn.prepareStatement(transSql);
                        transStmt.setLong(1, session.getCurrentAccountNumber());
                        transStmt.setString(2, "Bonus");
                        transStmt.setDouble(3, 10000.0);
                        transStmt.setString(4, "Welcome Bonus");
                        transStmt.executeUpdate();
                        
                        conn.commit();
                        session.setBalance(10000.0);
                        JOptionPane.showMessageDialog(editDialog, "Welcome bonus of ₦10,000 added!", "Bonus Added", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    JOptionPane.showMessageDialog(editDialog, "Profile updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    loadProfileData(); // Refresh the profile data
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("duplicate key")) {
                    JOptionPane.showMessageDialog(editDialog, "Email already exists!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Error updating profile: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> editDialog.dispose());

        editDialog.setSize(600, 500); 
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }
    
    @Override
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            loadProfileData();
            UserSession session = UserSession.getInstance();
            if (session.getBalance() == 0.0) {
                JOptionPane.showMessageDialog(this, "Please update your profile details to receive ₦10,000 welcome bonus!", 
                    "Update Profile", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
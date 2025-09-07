package orion;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AboutPage extends JPanel {
    public AboutPage(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(UIComponents.BACKGROUND_WHITE);

        // App icon placeholder
        JLabel appIconLabel = new JLabel();
        URL appIconUrl = getClass().getResource("resources/wealthhub.JPG");
        if (appIconUrl != null) {
            ImageIcon appIcon = new ImageIcon(appIconUrl);
            Image scaledAppIcon = appIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            appIconLabel.setIcon(new ImageIcon(scaledAppIcon));
        } else {
            System.err.println("App icon not found!");
            appIconLabel.setText("No App Icon");
        }
        titlePanel.add(appIconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));

       
        titlePanel.add(Box.createHorizontalStrut(10));

        JLabel titleLabel = UIComponents.createTitleLabel("ORION");
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel infoPanel = UIComponents.createWhiteCard();
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel versionLabel = new JLabel("JAVA PROJECT");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descriptionLabel = new JLabel("<html><center>ORION is a modern banking solution designed to provide seamless financial services.<br>" +
                                            "Features include:<br>" +
                                            "- Secure money transfers<br>" +
                                            "- Easy account management<br>" +
                                            "- Transaction history tracking<br>" +
                                            "- Profile customization with image upload</center></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel developerLabel = new JLabel("Developed by: Reverence Anietie Etuk");
        developerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        developerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel contactLabel = new JLabel("23/SC/CO/062");
        contactLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contactLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        infoPanel.add(versionLabel, gbc);

        gbc.gridy = 1;
        infoPanel.add(descriptionLabel, gbc);

        gbc.gridy = 2;
        infoPanel.add(developerLabel, gbc);

        gbc.gridy = 3;
        infoPanel.add(contactLabel, gbc);

        JButton backBtn = UIComponents.createSecondaryButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        contentPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UIComponents.BACKGROUND_WHITE);
        bottomPanel.add(backBtn);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
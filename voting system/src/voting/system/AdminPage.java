package voting.system;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPage extends javax.swing.JFrame {

    /**
     * Creates new form AdminPage
     */
    public AdminPage() {
        initComponents();
    }

    /**
     * Initialize components with styling
     */
   private void initComponents() {
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Admin Dashboard");
    setSize(600, 500); // Set custom frame size
    setLocationRelativeTo(null); // Center the frame on the screen

    // Header label
    JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
    headerLabel.setFont(StyleUtil.HEADER_FONT);
    headerLabel.setForeground(StyleUtil.TEXT_COLOR);

    // Buttons
    JButton manageUsersButton = createStyledButton("Manage Users");
    JButton manageElectionsButton = createStyledButton("Manage Elections");
    JButton manageCandidatesButton = createStyledButton("Manage Candidates");
    JButton viewResultsButton = createStyledButton("View Results");
    JButton logoutButton = createStyledButton("Logout");

    // Add Action Listeners
    manageUsersButton.addActionListener(evt -> manageUsersActionPerformed(evt));
    manageElectionsButton.addActionListener(evt -> manageElectionsActionPerformed(evt));
    manageCandidatesButton.addActionListener(evt -> manageCandidatesActionPerformed(evt));
    viewResultsButton.addActionListener(evt -> viewResultsActionPerformed(evt));
    logoutButton.addActionListener(evt -> logoutActionPerformed(evt));

    // Layout
    JPanel mainPanel = new JPanel();
    StyleUtil.stylePanel(mainPanel, StyleUtil.PRIMARY_COLOR);

    GroupLayout layout = new GroupLayout(mainPanel);
    mainPanel.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(100)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(manageUsersButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(manageElectionsButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(manageCandidatesButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewResultsButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
    );

    layout.setVerticalGroup(
        layout.createSequentialGroup()
            .addGap(20)
            .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
            .addGap(30)
            .addComponent(manageUsersButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(manageElectionsButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(manageCandidatesButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(viewResultsButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
    );

    add(mainPanel);
}


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(StyleUtil.BUTTON_FONT);
        button.setBackground(StyleUtil.ACCENT_COLOR);
        button.setForeground(StyleUtil.TEXT_COLOR);
        return button;
    }

    private void manageUsersActionPerformed(ActionEvent evt) {
        new ManageUsersPage().setVisible(true);
    }

    private void manageElectionsActionPerformed(ActionEvent evt) {
        new ManageElectionsPage().setVisible(true);
    }

    private void manageCandidatesActionPerformed(ActionEvent evt) {
        new ManageCandidatesPage().setVisible(true);
    }

    private void viewResultsActionPerformed(ActionEvent evt) {
        new ViewResultsPage().setVisible(true);
    }

    private void logoutActionPerformed(ActionEvent evt) {
        setVisible(false);
        new HomePage().setVisible(true);
    }

    /**
     * Main method
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AdminPage().setVisible(true));
    }
}

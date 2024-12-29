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
     * Initialize components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Admin Dashboard");

        // Header label
        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // Buttons
        JButton manageUsersButton = new JButton("Manage Users");
        JButton manageElectionsButton = new JButton("Manage Elections");
        JButton manageCandidatesButton = new JButton("Manage Candidates");
        JButton viewResultsButton = new JButton("View Results"); // New View Results Button
        JButton logoutButton = new JButton("Logout");

        // Add Action Listeners
        manageUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manageUsersActionPerformed(evt);
            }
        });

        manageElectionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manageElectionsActionPerformed(evt);
            }
        });

        manageCandidatesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manageCandidatesActionPerformed(evt);
            }
        });

        viewResultsButton.addActionListener(new ActionListener() { // Action Listener for View Results
            public void actionPerformed(ActionEvent evt) {
                viewResultsActionPerformed(evt);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        // Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(100)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(manageUsersButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(manageElectionsButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(manageCandidatesButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(viewResultsButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE) // Add View Results Button
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
                .addComponent(viewResultsButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE) // Add View Results Button
                .addGap(20)
                .addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    private void manageUsersActionPerformed(ActionEvent evt) {
        // Open a ManageUsers Page (to be implemented)
        new ManageUsersPage().setVisible(true);
    }

    private void manageElectionsActionPerformed(ActionEvent evt) {
        // Open a ManageElections Page (to be implemented)
        new ManageElectionsPage().setVisible(true);
    }

    private void manageCandidatesActionPerformed(ActionEvent evt) {
        // Open a ManageCandidates Page (to be implemented)
        new ManageCandidatesPage().setVisible(true);
    }

    private void viewResultsActionPerformed(ActionEvent evt) {
        // Open the View Results page
        new ViewResultsPage().setVisible(true);
    }

    private void logoutActionPerformed(ActionEvent evt) {
        // Redirect to login page
        setVisible(false);
        new HomePage().setVisible(true);
    }

    /**
     * Main method
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminPage().setVisible(true);
            }
        });
    }
}

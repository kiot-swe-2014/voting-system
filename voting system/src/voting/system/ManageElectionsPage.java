package voting.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageElectionsPage extends JFrame {

    private JTable electionsTable;
    private DefaultTableModel tableModel;
    private Connection con;

    public ManageElectionsPage() {
        dbconnect();
        initComponents();
        loadElections();
    }

    private void dbconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/voting_system", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageElectionsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setTitle("Manage Elections");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel styling
        JPanel mainPanel = new JPanel();
        StyleUtil.stylePanel(mainPanel, StyleUtil.PRIMARY_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Header label
        JLabel headerLabel = new JLabel("Manage Elections", SwingConstants.CENTER);
        headerLabel.setFont(StyleUtil.HEADER_FONT);
        headerLabel.setForeground(StyleUtil.TEXT_COLOR);

        // Table for displaying elections
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Start Date", "End Date", "Status"}, 0);
        electionsTable = new JTable(tableModel);
        electionsTable.setFont(StyleUtil.BUTTON_FONT);
        electionsTable.setForeground(StyleUtil.TEXT_COLOR);
        electionsTable.setBackground(StyleUtil.SECONDARY_COLOR);
        JScrollPane tableScrollPane = new JScrollPane(electionsTable);

        // Buttons
        JButton addElectionButton = createStyledButton("Add Election");
        JButton editElectionButton = createStyledButton("Edit Election");
        JButton deleteElectionButton = createStyledButton("Delete Election");
        JButton backButton = createStyledButton("Back");

        // Add action listeners
        addElectionButton.addActionListener(e -> addElection());
        editElectionButton.addActionListener(e -> editElection());
        deleteElectionButton.addActionListener(e -> deleteElection());
        backButton.addActionListener(e -> goBack());

        // Layout setup
        JPanel buttonPanel = new JPanel();
        StyleUtil.stylePanel(buttonPanel, StyleUtil.PRIMARY_COLOR);
        buttonPanel.add(addElectionButton);
        buttonPanel.add(editElectionButton);
        buttonPanel.add(deleteElectionButton);
        buttonPanel.add(backButton);

        mainPanel.add(headerLabel);
        mainPanel.add(tableScrollPane);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(StyleUtil.BUTTON_FONT);
        button.setBackground(StyleUtil.ACCENT_COLOR);
        button.setForeground(StyleUtil.TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void loadElections() {
        try {
            tableModel.setRowCount(0); // Clear existing rows
            String query = "SELECT * FROM elections";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("election_name");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{id, name, startDate, endDate, status});
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManageElectionsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addElection() {
        // Create a dialog to get election details
        JTextField nameField = new JTextField(20);
        JTextField startDateField = new JTextField(20);
        JTextField endDateField = new JTextField(20);
        JTextField statusField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Election Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Start Date (yyyy-mm-dd):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (yyyy-mm-dd):"));
        panel.add(endDateField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Add Election", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            String status = statusField.getText();

            try {
                String query = "INSERT INTO elections (election_name, start_date, end_date, status) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, startDate);
                stmt.setString(3, endDate);
                stmt.setString(4, status);
                stmt.executeUpdate();
                loadElections(); // Reload elections after adding
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding election: " + ex.getMessage());
            }
        }
    }

    private void editElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int electionId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String startDate = (String) tableModel.getValueAt(selectedRow, 2);
            String endDate = (String) tableModel.getValueAt(selectedRow, 3);
            String status = (String) tableModel.getValueAt(selectedRow, 4);

            // Create dialog to edit election details
            JTextField nameField = new JTextField(name, 20);
            JTextField startDateField = new JTextField(startDate, 20);
            JTextField endDateField = new JTextField(endDate, 20);
            JTextField statusField = new JTextField(status, 20);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Election Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Start Date:"));
            panel.add(startDateField);
            panel.add(new JLabel("End Date:"));
            panel.add(endDateField);
            panel.add(new JLabel("Status:"));
            panel.add(statusField);

            int option = JOptionPane.showConfirmDialog(this, panel, "Edit Election", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    String query = "UPDATE elections SET election_name = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, nameField.getText());
                    stmt.setString(2, startDateField.getText());
                    stmt.setString(3, endDateField.getText());
                    stmt.setString(4, statusField.getText());
                    stmt.setInt(5, electionId);
                    stmt.executeUpdate();
                    loadElections(); // Reload elections after editing
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error editing election: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election to edit.");
        }
    }

    private void deleteElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int electionId = (int) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this election?", "Delete Election", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM elections WHERE id = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, electionId);
                    stmt.executeUpdate();
                    loadElections(); // Reload elections after deletion
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting election: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election to delete.");
        }
    }

    private void goBack() {
        new AdminPage().setVisible(true); // Opens AdminPage
        dispose(); // Closes the current page
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageElectionsPage().setVisible(true));
    }
}

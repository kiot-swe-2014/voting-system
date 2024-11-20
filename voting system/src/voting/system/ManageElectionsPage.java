package voting.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ManageElectionsPage extends JFrame {

    private JTable electionsTable;
    private DefaultTableModel tableModel;
    private Connection con;
    private PreparedStatement pst;

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

        // Create components
        JLabel headerLabel = new JLabel("Manage Elections", SwingConstants.CENTER);
        headerLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // Table for displaying elections
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Start Date", "End Date", "Status"}, 0);
        electionsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(electionsTable);

        // Buttons for adding, editing, and deleting elections
        JButton addElectionButton = new JButton("Add Election");
        JButton editElectionButton = new JButton("Edit Election");
        JButton deleteElectionButton = new JButton("Delete Election");

        // Add action listeners for buttons
        addElectionButton.addActionListener(e -> addElection());
        editElectionButton.addActionListener(e -> editElection());
        deleteElectionButton.addActionListener(e -> deleteElection());

        // Layout setup
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(100)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(addElectionButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(editElectionButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteElectionButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addGap(30)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(addElectionButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(editElectionButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(deleteElectionButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        );

        pack();
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
        String name = JOptionPane.showInputDialog(this, "Enter Election Name:");
        String startDate = JOptionPane.showInputDialog(this, "Enter Start Date (YYYY-MM-DD):");
        String endDate = JOptionPane.showInputDialog(this, "Enter End Date (YYYY-MM-DD):");
        String status = JOptionPane.showInputDialog(this, "Enter Status:");

        if (name != null && startDate != null && endDate != null && status != null) {
            String query = "INSERT INTO elections (election_name, start_date, end_date, status) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, startDate);
                pst.setString(3, endDate);
                pst.setString(4, status);

                int rowsInserted = pst.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Election added successfully!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding election: " + ex.getMessage());
            }
            loadElections(); // Reload elections after adding a new one
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

            name = JOptionPane.showInputDialog(this, "Edit Election Name:", name);
            startDate = JOptionPane.showInputDialog(this, "Edit Start Date:", startDate);
            endDate = JOptionPane.showInputDialog(this, "Edit End Date:", endDate);
            status = JOptionPane.showInputDialog(this, "Edit Status:", status);

            if (name != null && startDate != null && endDate != null && status != null) {
                String query = "UPDATE elections SET election_name=?, start_date=?, end_date=?, status=? WHERE id=?";
                try {
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setString(1, name);
                    pst.setString(2, startDate);
                    pst.setString(3, endDate);
                    pst.setString(4, status);
                    pst.setInt(5, electionId);

                    int rowsUpdated = pst.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Election updated successfully.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating election: " + ex.getMessage());
                }
                loadElections(); // Reload elections after editing
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election to edit.");
        }
    }

    private void deleteElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int electionId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this election?");
            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM elections WHERE id=?";
                try {
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setInt(1, electionId);

                    int rowsDeleted = pst.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Election deleted successfully.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting election: " + ex.getMessage());
                }
                loadElections(); // Reload elections after deletion
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election to delete.");
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManageElectionsPage().setVisible(true);
            }
        });
    }
}

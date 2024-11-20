/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package voting.system;

/**
 *
 * @author NUREDIN
 */


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageCandidatesPage extends JFrame {

    private JTable candidatesTable;
    private DefaultTableModel tableModel;
    private Connection con;

    public ManageCandidatesPage() {
        dbconnect();
        initComponents();
        loadCandidates();
    }

    private void dbconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/voting_system", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageCandidatesPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setTitle("Manage Candidates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header label
        JLabel headerLabel = new JLabel("Manage Candidates", SwingConstants.CENTER);
        headerLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // Table for displaying candidates
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Election ID", "Party"}, 0);
        candidatesTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(candidatesTable);

        // Buttons
        JButton addCandidateButton = new JButton("Add Candidate");
        JButton deleteCandidateButton = new JButton("Delete Candidate");

        // Add action listeners
        addCandidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCandidate();
            }
        });

        deleteCandidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCandidate();
            }
        });

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
                        .addComponent(addCandidateButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteCandidateButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addGap(30)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(addCandidateButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(deleteCandidateButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    private void loadCandidates() {
        try {
            tableModel.setRowCount(0); // Clear existing rows
            String query = "SELECT * FROM candidates";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int electionId = rs.getInt("election_id");
                String party = rs.getString("party");

                tableModel.addRow(new Object[]{id, name, electionId, party});
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManageCandidatesPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addCandidate() {
        String name = JOptionPane.showInputDialog(this, "Enter Candidate Name:");
        String electionId = JOptionPane.showInputDialog(this, "Enter Election ID:");
        String party = JOptionPane.showInputDialog(this, "Enter Party:");

        if (name != null && electionId != null && party != null) {
            String query = "INSERT INTO candidates (name, election_id, party) VALUES (?, ?, ?)";
            try {
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name);
                pst.setInt(2, Integer.parseInt(electionId));
                pst.setString(3, party);

                int rowsInserted = pst.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Candidate added successfully!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding candidate: " + ex.getMessage());
            }
            loadCandidates(); // Reload candidates after adding a new one
        }
    }

    private void deleteCandidate() {
        int selectedRow = candidatesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int candidateId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this candidate?");
            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM candidates WHERE id=?";
                try {
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setInt(1, candidateId);

                    int rowsDeleted = pst.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Candidate deleted successfully.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting candidate: " + ex.getMessage());
                }
                loadCandidates(); // Reload candidates after deletion
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a candidate to delete.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManageCandidatesPage().setVisible(true);
        });
    }
}


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
import java.sql.*;

public class ViewResultsPage extends JFrame {
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private Connection con;

    public ViewResultsPage() {
        dbconnect();
        initComponents();
        loadResults();
    }

    private void dbconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/voting_system", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
        }
    }

    private void initComponents() {
        setTitle("View Results");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Results table
        tableModel = new DefaultTableModel(new String[]{"Candidate", "Party", "Votes"}, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);

        // Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    private void loadResults() {
        try {
            tableModel.setRowCount(0); // Clear existing rows
            String query = "SELECT c.name, c.party, COUNT(v.id) AS votes " +
                           "FROM candidates c " +
                           "LEFT JOIN votes v ON c.id = v.candidate_id " +
                           "GROUP BY c.id, c.name, c.party";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String candidateName = rs.getString("name");
                String party = rs.getString("party");
                int voteCount = rs.getInt("votes");

                tableModel.addRow(new Object[]{candidateName, party, voteCount});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewResultsPage().setVisible(true));
    }
}


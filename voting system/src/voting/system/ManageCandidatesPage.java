package voting.system;

import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel styling
        JPanel mainPanel = new JPanel();
        StyleUtil.stylePanel(mainPanel, StyleUtil.PRIMARY_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Header label
        JLabel headerLabel = new JLabel("Manage Candidates", SwingConstants.CENTER);
        headerLabel.setFont(StyleUtil.HEADER_FONT);
        headerLabel.setForeground(StyleUtil.TEXT_COLOR);

        // Table for displaying candidates
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Election ID", "Party"}, 0);
        candidatesTable = new JTable(tableModel);
        candidatesTable.setFont(StyleUtil.BUTTON_FONT);
        candidatesTable.setForeground(StyleUtil.TEXT_COLOR);
        candidatesTable.setBackground(StyleUtil.SECONDARY_COLOR);
        JScrollPane tableScrollPane = new JScrollPane(candidatesTable);

        // Buttons
        JButton addCandidateButton = createStyledButton("Add Candidate");
        JButton editCandidateButton = createStyledButton("Edit Candidate");
        JButton deleteCandidateButton = createStyledButton("Delete Candidate");
        JButton backButton = createStyledButton("Back");

        // Add action listeners
        addCandidateButton.addActionListener(e -> addCandidate());
        editCandidateButton.addActionListener(e -> editCandidate());
        deleteCandidateButton.addActionListener(e -> deleteCandidate());
        backButton.addActionListener(e -> {
            new AdminPage().setVisible(true);
            dispose();
        });

        // Layout setup
        JPanel buttonPanel = new JPanel();
        StyleUtil.stylePanel(buttonPanel, StyleUtil.PRIMARY_COLOR);
        buttonPanel.add(addCandidateButton);
        buttonPanel.add(editCandidateButton);
        buttonPanel.add(deleteCandidateButton);
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

    private void editCandidate() {
        int selectedRow = candidatesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int candidateId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            int electionId = (int) tableModel.getValueAt(selectedRow, 2);
            String party = (String) tableModel.getValueAt(selectedRow, 3);

            // Create dialog to edit candidate details
            JTextField nameField = new JTextField(name, 20);
            JTextField electionIdField = new JTextField(String.valueOf(electionId), 20);
            JTextField partyField = new JTextField(party, 20);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Candidate Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Election ID:"));
            panel.add(electionIdField);
            panel.add(new JLabel("Party:"));
            panel.add(partyField);

            int option = JOptionPane.showConfirmDialog(this, panel, "Edit Candidate", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    String query = "UPDATE candidates SET name = ?, election_id = ?, party = ? WHERE id = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, nameField.getText());
                    stmt.setInt(2, Integer.parseInt(electionIdField.getText()));
                    stmt.setString(3, partyField.getText());
                    stmt.setInt(4, candidateId);
                    stmt.executeUpdate();
                    loadCandidates(); // Reload candidates after editing
                    JOptionPane.showMessageDialog(this, "Candidate details updated successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating candidate: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a candidate to edit.");
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

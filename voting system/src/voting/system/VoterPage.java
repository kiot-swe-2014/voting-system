package voting.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoterPage extends JFrame {

    private JTable electionsTable;
    private DefaultTableModel electionsTableModel;
    private JTable candidatesTable;
    private DefaultTableModel candidatesTableModel;
    private Connection con;
    private int userId;

    public VoterPage(int userId) {
        this.userId = userId; // Pass the logged-in user ID
        dbconnect();
        initComponents();
        loadElections();
    }

    private void dbconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/voting_system", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(VoterPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setTitle("Voter Page");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header label
        JLabel headerLabel = new JLabel("Welcome, Voter", SwingConstants.CENTER);
        headerLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // Elections table
        electionsTableModel = new DefaultTableModel(new String[]{"Election ID", "Election Name", "Start Date", "End Date", "Status"}, 0);
        electionsTable = new JTable(electionsTableModel);
        JScrollPane electionsScrollPane = new JScrollPane(electionsTable);

        // Candidates table
        candidatesTableModel = new DefaultTableModel(new String[]{"Candidate ID", "Name", "Party", "Description"}, 0);
        candidatesTable = new JTable(candidatesTableModel);
        JScrollPane candidatesScrollPane = new JScrollPane(candidatesTable);

        // Buttons
        JButton viewCandidatesButton = new JButton("View Candidates");
        JButton castVoteButton = new JButton("Cast Vote");

        // Add action listeners
        viewCandidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCandidates();
            }
        });

        castVoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                castVote();
            }
        });

        // Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(electionsScrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                    .addGap(20)
                    .addComponent(candidatesScrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(viewCandidatesButton, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                    .addGap(20)
                    .addComponent(castVoteButton, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(electionsScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(candidatesScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(viewCandidatesButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addComponent(castVoteButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    private void loadElections() {
        try {
            electionsTableModel.setRowCount(0);
            String query = "SELECT id, election_name, start_date, end_date, status FROM elections";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("election_name");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                String status = rs.getBoolean("status") ? "Active" : "Inactive";
                electionsTableModel.addRow(new Object[]{id, name, startDate, endDate, status});
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoterPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void viewCandidates() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int electionId = (int) electionsTableModel.getValueAt(selectedRow, 0);
            try {
                candidatesTableModel.setRowCount(0);
                String query = "SELECT id, name, party, description FROM candidates WHERE election_id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, electionId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String party = rs.getString("party");
                    String description = rs.getString("description");
                    candidatesTableModel.addRow(new Object[]{id, name, party, description});
                }
            } catch (SQLException ex) {
                Logger.getLogger(VoterPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election to view candidates.");
        }
    }

    private void castVote() {
        int selectedElectionRow = electionsTable.getSelectedRow();
        int selectedCandidateRow = candidatesTable.getSelectedRow();

        if (selectedElectionRow >= 0 && selectedCandidateRow >= 0) {
            int electionId = (int) electionsTableModel.getValueAt(selectedElectionRow, 0);
            int candidateId = (int) candidatesTableModel.getValueAt(selectedCandidateRow, 0);

            try {
                // Check if the user has already voted
                String checkQuery = "SELECT * FROM votes WHERE user_id = ? AND poll_id = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, electionId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "You have already voted in this election.");
                    return;
                }

                // Insert vote
                String query = "INSERT INTO votes (user_id, poll_id, choice) VALUES (?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, electionId);
                stmt.setInt(3, candidateId);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Vote cast successfully!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error casting vote: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an election and a candidate to cast your vote.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VoterPage(1).setVisible(true); // Replace with actual user ID
        });
    }
}

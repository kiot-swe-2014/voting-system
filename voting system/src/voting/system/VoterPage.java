package voting.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 500));

        // Header label
        JLabel headerLabel = new JLabel("Welcome, Voter", SwingConstants.CENTER);
        headerLabel.setFont(StyleUtil.HEADER_FONT);
        headerLabel.setForeground(StyleUtil.TEXT_COLOR);

        // Style header panel
        JPanel headerPanel = new JPanel();
        StyleUtil.stylePanel(headerPanel, StyleUtil.PRIMARY_COLOR);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Elections table
        electionsTableModel = new DefaultTableModel(new String[]{"Election ID", "Election Name", "Start Date", "End Date"}, 0);
        electionsTable = new JTable(electionsTableModel);
        JScrollPane electionsScrollPane = new JScrollPane(electionsTable);
        electionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        electionsTable.setBackground(StyleUtil.SECONDARY_COLOR);
        electionsTable.setSelectionBackground(StyleUtil.ACCENT_COLOR);

        // Candidates table
        candidatesTableModel = new DefaultTableModel(new String[]{"Candidate ID", "Name", "Party", "Description"}, 0);
        candidatesTable = new JTable(candidatesTableModel);
        JScrollPane candidatesScrollPane = new JScrollPane(candidatesTable);
        candidatesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        candidatesTable.setBackground(StyleUtil.SECONDARY_COLOR);
        candidatesTable.setSelectionBackground(StyleUtil.ACCENT_COLOR);

        // Buttons
        JButton viewCandidatesButton = new JButton("View Candidates");
        JButton castVoteButton = new JButton("Cast Vote");

        // Style buttons
        viewCandidatesButton.setFont(StyleUtil.BUTTON_FONT);
        viewCandidatesButton.setBackground(StyleUtil.ACCENT_COLOR);
        viewCandidatesButton.setForeground(Color.WHITE);
        viewCandidatesButton.setFocusPainted(false);
        viewCandidatesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        castVoteButton.setFont(StyleUtil.BUTTON_FONT);
        castVoteButton.setBackground(StyleUtil.ACCENT_COLOR);
        castVoteButton.setForeground(Color.WHITE);
        castVoteButton.setFocusPainted(false);
        castVoteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        // Footer panel for buttons
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        StyleUtil.stylePanel(footerPanel, StyleUtil.PRIMARY_COLOR);
        footerPanel.add(viewCandidatesButton);
        footerPanel.add(castVoteButton);

        // Add components to the frame
        add(electionsScrollPane, BorderLayout.CENTER);
        add(candidatesScrollPane, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        pack();
    }

    private void loadElections() {
        try {
            electionsTableModel.setRowCount(0); // Clear existing rows

            // Query to filter elections where the current date is within the start and end date
            String query = "SELECT id, election_name, start_date, end_date " +
                           "FROM elections " +
                           "WHERE CURDATE() BETWEEN start_date AND end_date";

            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("election_name");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                electionsTableModel.addRow(new Object[]{id, name, startDate, endDate});
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
                String checkQuery = "SELECT * FROM votes WHERE user_id = ? AND election_id = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, electionId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "You have already voted in this election.");
                    return;
                }

                // Insert vote
                String query = "INSERT INTO votes (user_id, election_id, candidate_id) VALUES (?, ?, ?)";
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

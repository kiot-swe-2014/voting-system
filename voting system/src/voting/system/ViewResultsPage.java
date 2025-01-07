package voting.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewResultsPage extends JFrame {
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private Connection con;

    // Constants for styling
    private static final Color HEADER_COLOR = new Color(0, 102, 204);
    private static final Color BUTTON_COLOR = new Color(0, 153, 255);
    private static final Color TABLE_BG_COLOR = new Color(245, 245, 245);
    private static final Color SELECTION_COLOR = new Color(0, 102, 204);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

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
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 400));

        // Header
        JLabel headerLabel = new JLabel("Voting Results", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(HEADER_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // Results table
        tableModel = new DefaultTableModel(new String[]{"Candidate", "Party", "Votes"}, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);
        resultsTable.setFont(TABLE_FONT);
        resultsTable.setBackground(TABLE_BG_COLOR);
        resultsTable.setForeground(Color.BLACK);
        resultsTable.setSelectionBackground(SELECTION_COLOR);
        resultsTable.setSelectionForeground(Color.WHITE);

        // Adding the table to the center of the frame
        add(tableScrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(HEADER_COLOR);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(BUTTON_FONT);
        refreshButton.setBackground(BUTTON_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadResults());

        // Back to Admin Page button
        JButton backButton = new JButton("Back to Admin");
        backButton.setFont(BUTTON_FONT);
        backButton.setBackground(BUTTON_COLOR);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new AdminPage().setVisible(true);
            this.dispose();
        });

        footerPanel.add(refreshButton);
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);

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

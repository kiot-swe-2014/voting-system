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
        // Add election implementation here
    }

    private void editElection() {
        // Edit election implementation here
    }

    private void deleteElection() {
        // Delete election implementation here
    }

    private void goBack() {
        new AdminPage().setVisible(true); // Opens AdminPage
        dispose(); // Closes the current page
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageElectionsPage().setVisible(true));
    }
}

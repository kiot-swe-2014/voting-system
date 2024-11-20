/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package voting.system;

/**
 *
 * @author NUREDIN
 */


import com.mysql.cj.xdevapi.Statement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageUsersPage extends javax.swing.JFrame {
    
    private JTable userTable;

    public ManageUsersPage() {
        initComponents();
        dbconnect();
        loadUsersIntoTable((DefaultTableModel) userTable.getModel());
        
 // Populate table when page loads


    }
    
           Connection con;
    PreparedStatement pst;
    
     public void dbconnect(){
     
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost/voting_system","root","");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
            
       
    }


    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manage Users");

        // Header
        JLabel headerLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        headerLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        // Table for users
        String[] columnNames = {"User ID", "Username", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
         userTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(userTable);

        // Buttons
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton backButton = new JButton("Back");

        // Button Actions
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addUserActionPerformed(evt);
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editUserActionPerformed(evt, userTable);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteUserActionPerformed(evt, userTable, tableModel);
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        // Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(headerLabel)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(headerLabel)
                .addGap(20)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(editButton)
                    .addComponent(deleteButton)
                    .addComponent(backButton))
        );

        pack();
    }

   private void addUserActionPerformed(ActionEvent evt) {
    String username = JOptionPane.showInputDialog("Enter Username:");
    String password = JOptionPane.showInputDialog("Enter Password:");
    String role = JOptionPane.showInputDialog("Enter Role (admin/voter):");

    if (username == null || password == null || role == null) {
        JOptionPane.showMessageDialog(this, "Operation cancelled.");
        return;
    }

    String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

    try {
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password); // Hash password for security
        pst.setString(3, role);

        int rowsInserted = pst.executeUpdate();
        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "User added successfully!");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
    }
}


private void editUserActionPerformed(ActionEvent evt, JTable userTable) {
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        return;
    }

    DefaultTableModel tableModel = (DefaultTableModel) userTable.getModel();
    int userId = (int) tableModel.getValueAt(selectedRow, 0); // Assuming ID is in the first column

    String newUsername = JOptionPane.showInputDialog("Enter new username:");
    String newRole = JOptionPane.showInputDialog("Enter new role (admin/voter):");

    if (newUsername == null || newRole == null) {
        JOptionPane.showMessageDialog(this, "Operation cancelled.");
        return;
    }

    String query = "UPDATE users SET username = ?, role = ? WHERE id = ?";

    try {
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, newUsername);
        pst.setString(2, newRole);
        pst.setInt(3, userId);

        int rowsUpdated = pst.executeUpdate();
        if (rowsUpdated > 0) {
            tableModel.setValueAt(newUsername, selectedRow, 1); // Update username in table
            tableModel.setValueAt(newRole, selectedRow, 3);     // Update role in table
            JOptionPane.showMessageDialog(this, "User updated successfully!");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
    }
}


  private void deleteUserActionPerformed(ActionEvent evt, JTable userTable, DefaultTableModel tableModel) {
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?");
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    int userId = (int) tableModel.getValueAt(selectedRow, 0); // Assuming ID is in the first column

    String query = "DELETE FROM users WHERE id = ?";

    try {
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, userId);

        int rowsDeleted = pst.executeUpdate();
        if (rowsDeleted > 0) {
            tableModel.removeRow(selectedRow); // Remove from table view
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
    }
}

    private void backActionPerformed(ActionEvent evt) {
        new AdminPage().setVisible(true);
        this.dispose(); // Close ManageUsersPage
    }
    
private void loadUsersIntoTable(DefaultTableModel tableModel) {
        String query = "SELECT id, username, role FROM users";

        try (PreparedStatement pst = con.prepareStatement(query);
             java.sql.ResultSet rs = pst.executeQuery()) {

            // Clear existing rows before loading data
            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String role = rs.getString("role");

                tableModel.addRow(new Object[]{id, username, role});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }




    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageUsersPage().setVisible(true);
            }
        });
    }
}
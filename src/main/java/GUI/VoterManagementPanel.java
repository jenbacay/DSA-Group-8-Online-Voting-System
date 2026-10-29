package GUI;

import Database.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class VoterManagementPanel extends JPanel implements ActionListener {

    private JLabel titleLabel, firstNameLabel, lastNameLabel, emailLabel, passwordLabel;
    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, updateButton, deleteButton, clearButton;
    private JTable voterTable;
    private DefaultTableModel tableModel;
    private final Color darkBlue = new Color(20, 55, 85);
    private final Color background = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);

    public VoterManagementPanel() {

        setLayout(null);
        setBackground(background);

        titleLabel = new JLabel("VOTER REGISTRATION");
        titleLabel.setBounds(25, 15, 650, 35);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);
        add(titleLabel);

        firstNameLabel = createLabel("First Name:", 25, 65, 100);
        firstNameField = createTextField(125, 62, 190);

        lastNameLabel = createLabel("Last Name:", 350, 65, 100);
        lastNameField = createTextField(450, 62, 220);

        emailLabel = createLabel("Email:", 25, 110, 100);
        emailField = createTextField(125, 107, 190);

        passwordLabel = createLabel("Password:", 350, 110, 100);

        passwordField = new JPasswordField();
        passwordField.setBounds(450, 107, 220, 30);
        passwordField.setBorder(new LineBorder(Color.GRAY));
        add(passwordField);

        registerButton = createButton("Register", 25);
        updateButton = createButton("Update", 125);
        deleteButton = createButton("Delete", 225);
        clearButton = createButton("Clear", 325);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Voter ID",
                    "First Name",
                    "Last Name",
                    "Email",
                    "Registered"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        voterTable = new JTable(tableModel);
        voterTable.setFont(new Font("Arial", Font.PLAIN, 12));
        voterTable.setRowHeight(24);
        voterTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        voterTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 11));
        voterTable.getTableHeader().setReorderingAllowed(false);

        voterTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        voterTable.getColumnModel().getColumn(1).setPreferredWidth(105);
        voterTable.getColumnModel().getColumn(2).setPreferredWidth(105);
        voterTable.getColumnModel().getColumn(3).setPreferredWidth(190);
        voterTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(voterTable);
        scrollPane.setBounds(25, 205, 655, 320);
        scrollPane.setBorder(
                new LineBorder(new Color(125, 145, 165)));
        add(scrollPane);

        registerButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);

        voterTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = voterTable.getSelectedRow();

                if (row >= 0) {
                    firstNameField.setText(
                            tableModel.getValueAt(row, 1).toString());

                    lastNameField.setText(
                            tableModel.getValueAt(row, 2).toString());

                    emailField.setText(
                            tableModel.getValueAt(row, 3).toString());

                    // Hindi ipinapakita o nilo-load ang password.
                    passwordField.setText("");
                }
            }
        });

        loadVoters();
    }

    private JLabel createLabel(
            String text, int x, int y, int width) {

        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, 25);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(darkBlue);
        add(label);

        return label;
    }

    private JTextField createTextField(
            int x, int y, int width) {

        JTextField field = new JTextField();
        field.setBounds(x, y, width, 30);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(new LineBorder(Color.GRAY));
        add(field);

        return field;
    }

    private JButton createButton(String text, int x) {

        JButton button = new JButton(text);
        button.setBounds(x, 155, 90, 35);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(darkBlue);
        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setBorder(
                new LineBorder(new Color(150, 170, 190)));
        add(button);

        return button;
    }

    private String hashPassword(char[] password)
            throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
                password, salt, 120000, 256);

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return "PBKDF2120000"
                + Base64.getEncoder().encodeToString(salt)
                + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    private void loadVoters() {

        tableModel.setRowCount(0);

        String sql = "SELECT voter_id, first_name, last_name, "
                   + "email, registered "
                   + "FROM voters ORDER BY voter_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    tableModel.addRow(new Object[]{
                        rs.getInt("voter_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("registered")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Error loading voters: " + e.getMessage());
        }
    }

    private void registerVoter() {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.length == 0) {

            showWarning(
                    "Please complete all fields before registering.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showWarning("Please enter a valid email address.");
            return;
        }

        String passwordHash;

        try {
            passwordHash = hashPassword(password);
        } catch (Exception e) {
            showError("Could not secure the password.");
            return;
        } finally {
            java.util.Arrays.fill(password, '\0');
        }

        String sql = "INSERT INTO voters "
                   + "(first_name, last_name, email, password_hash, registered) "
                   + "VALUES (?, ?, ?, ?, 'Yes')";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, passwordHash);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this, "Voter registered successfully!");

            clearFields();
            loadVoters();

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                showError(
                        "This email may already be registered. "
                        + "Please use another email address.");
            } else {
                showError("Error registering voter: " + e.getMessage());
            }
        }
    }

    private void updateVoter() {

        int row = voterTable.getSelectedRow();

        if (row == -1) {
            showWarning("Please select a voter from the table.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()) {

            showWarning(
                    "First name, last name, and email are required.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showWarning("Please enter a valid email address.");
            return;
        }

        int voterId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        boolean changePassword = password.length > 0;
        String passwordHash = null;

        if (changePassword) {
            try {
                passwordHash = hashPassword(password);
            } catch (Exception e) {
                showError("Could not secure the password.");
                return;
            } finally {
                java.util.Arrays.fill(password, '\0');
            }
        }

        String sql;

        if (changePassword) {
            sql = "UPDATE voters SET first_name = ?, last_name = ?, "
                + "email = ?, password_hash = ? WHERE voter_id = ?";
        } else {
            sql = "UPDATE voters SET first_name = ?, last_name = ?, "
                + "email = ? WHERE voter_id = ?";
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);

                if (changePassword) {
                    ps.setString(4, passwordHash);
                    ps.setInt(5, voterId);
                } else {
                    ps.setInt(4, voterId);
                }

                int updated = ps.executeUpdate();

                if (updated == 0) {
                    showWarning("Voter not found.");
                    loadVoters();
                    clearFields();
                    return;
                }
            }

            JOptionPane.showMessageDialog(
                    this, "Voter updated successfully!");

            clearFields();
            loadVoters();

        } catch (SQLException e) {
            showError("Error updating voter: " + e.getMessage());
        }
    }

    private void deleteVoter() {

        int row = voterTable.getSelectedRow();

        if (row == -1) {
            showWarning("Please select a voter from the table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this voter?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int voterId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String sql = "DELETE FROM voters WHERE voter_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, voterId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this, "Voter deleted successfully!");

            clearFields();
            loadVoters();

        } catch (SQLException e) {
            showError(
                    "Error deleting voter: " + e.getMessage()
                    + "\nThe voter may already be linked to a vote.");
        }
    }

    private void clearFields() {

        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        passwordField.setText("");

        voterTable.clearSelection();
        firstNameField.requestFocusInWindow();
    }

    private void showWarning(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Notice",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == registerButton) {

            registerVoter();

        } else if (e.getSource() == updateButton) {

            updateVoter();

        } else if (e.getSource() == deleteButton) {

            deleteVoter();

        } else if (e.getSource() == clearButton) {

            clearFields();
        }
    }
}




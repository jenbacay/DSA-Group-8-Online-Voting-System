package GUI;

import Database.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class VoterManagementPanel extends JPanel implements ActionListener {

    private JLabel titleLabel, firstNameLabel, lastNameLabel, usernameLabel, emailLabel;
    private JTextField firstNameField, lastNameField, usernameField, emailField;
    private JButton updateButton, deleteButton, clearButton, refreshButton;
    private JTable voterTable;
    private DefaultTableModel tableModel;
    private final Color darkBlue = new Color(20, 55, 85);
    private final Color background = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);

    public VoterManagementPanel() {

        setLayout(null);
        setBackground(background);

        titleLabel = new JLabel("VOTER MANAGEMENT");
        titleLabel.setBounds(25, 15, 650, 35);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);
        add(titleLabel);

        firstNameLabel = createLabel("First Name:", 25, 65, 100);
        firstNameField = createTextField(125, 62, 190);

        lastNameLabel = createLabel("Last Name:", 350, 65, 100);
        lastNameField = createTextField(450, 62, 220);

        usernameLabel = createLabel("Username:", 25, 110, 100);
        usernameField = createTextField(125, 107, 190);
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(230, 230, 230));

        emailLabel = createLabel("Email:", 350, 110, 100);
        emailField = createTextField(450, 107, 220);

        updateButton = createButton("Update", 25);
        deleteButton = createButton("Delete", 125);
        clearButton = createButton("Clear", 225);
        refreshButton = createButton("Refresh", 325);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Voter ID",
                    "First Name",
                    "Last Name",
                    "Username",
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
        voterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        voterTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 11));

        voterTable.getTableHeader().setReorderingAllowed(false);

        voterTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        voterTable.getColumnModel().getColumn(1).setPreferredWidth(95);
        voterTable.getColumnModel().getColumn(2).setPreferredWidth(95);
        voterTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        voterTable.getColumnModel().getColumn(4).setPreferredWidth(155);
        voterTable.getColumnModel().getColumn(5).setPreferredWidth(75);

        JScrollPane scrollPane = new JScrollPane(voterTable);
        scrollPane.setBounds(25, 205, 655, 320);
        scrollPane.setBorder(
                new LineBorder(new Color(125, 145, 165)));

        add(scrollPane);

        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        refreshButton.addActionListener(this);

        voterTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = voterTable.getSelectedRow();

                if (row >= 0) {

                    firstNameField.setText(
                            tableModel.getValueAt(row, 1).toString());

                    lastNameField.setText(
                            tableModel.getValueAt(row, 2).toString());

                    usernameField.setText(
                            tableModel.getValueAt(row, 3).toString());

                    Object email = tableModel.getValueAt(row, 4);

                    emailField.setText(
                            email == null ? "" : email.toString());
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

    private void loadVoters() {

        tableModel.setRowCount(0);

        String sql = "SELECT v.voter_id, v.first_name, "
                + "v.last_name, a.username, v.email, "
                + "v.registered "
                + "FROM voters v "
                + "INNER JOIN accounts a "
                + "ON v.account_id = a.account_id "
                + "WHERE a.role = 'VOTER' "
                + "ORDER BY v.voter_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String status = rs.getBoolean("registered")
                            ? "Yes" : "No";

                    tableModel.addRow(new Object[]{
                        rs.getString("voter_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        status
                    });
                }
            }

        } catch (SQLException e) {
            showError("Error loading voters: " + e.getMessage());
        }
    }

    public void refreshVoters() {
        loadVoters();
    }

    private boolean isDuplicateName(
            Connection con,
            String firstName,
            String lastName,
            String currentVoterID) throws SQLException {

        String sql = "SELECT COUNT(*) FROM voters "
                + "WHERE LOWER(TRIM(first_name)) = LOWER(?) "
                + "AND LOWER(TRIM(last_name)) = LOWER(?) "
                + "AND voter_id <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, currentVoterID);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private void updateVoter() {

        int row = voterTable.getSelectedRow();

        if (row == -1) {
            showWarning("Please select a voter from the table.");
            return;
        }

        String voterID = tableModel.getValueAt(row, 0).toString();

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

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

        String fullName = firstName + " " + lastName;

        String accountIDSQL =
                "SELECT account_id FROM voters WHERE voter_id = ?";

        String updateAccountSQL =
                "UPDATE accounts SET full_name = ?, email = ? "
                + "WHERE account_id = ? AND role = 'VOTER'";

        String updateVoterSQL =
                "UPDATE voters SET first_name = ?, "
                + "last_name = ?, email = ? "
                + "WHERE voter_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            con.setAutoCommit(false);

            try {

                if (isDuplicateName(
                        con, firstName, lastName, voterID)) {

                    con.rollback();

                    showWarning(
                            "Another voter already has this "
                            + "first name and last name.");
                    return;
                }

                int accountID;

                try (PreparedStatement ps =
                        con.prepareStatement(accountIDSQL)) {

                    ps.setString(1, voterID);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Voter account was not found.");
                        }

                        accountID = rs.getInt("account_id");
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(updateAccountSQL)) {

                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setInt(3, accountID);

                    int updated = ps.executeUpdate();

                    if (updated != 1) {
                        throw new SQLException(
                                "Could not update voter account.");
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(updateVoterSQL)) {

                    ps.setString(1, firstName);
                    ps.setString(2, lastName);
                    ps.setString(3, email);
                    ps.setString(4, voterID);

                    int updated = ps.executeUpdate();

                    if (updated != 1) {
                        throw new SQLException(
                                "Could not update voter information.");
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this, "Voter updated successfully!");

                clearFields();
                loadVoters();

            } catch (SQLException e) {

                con.rollback();

                if ("23000".equals(e.getSQLState())) {
                    showWarning(
                            "This email may already be registered.");
                } else {
                    showError("Error updating voter: "
                            + e.getMessage());
                }

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void deleteVoter() {

        int row = voterTable.getSelectedRow();

        if (row == -1) {
            showWarning("Please select a voter from the table.");
            return;
        }

        String voterID = tableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete voter "
                + voterID + "?\n"
                + "The associated account will also be deleted.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String checkVotesSQL =
                "SELECT COUNT(*) FROM votes WHERE voter_id = ?";

        String deleteAccountSQL =
                "DELETE FROM accounts "
                + "WHERE account_id = ? AND role = 'VOTER'";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                        con.prepareStatement(checkVotesSQL)) {

                    ps.setString(1, voterID);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next() && rs.getInt(1) > 0) {

                            con.rollback();

                            showWarning(
                                    "This voter already has recorded "
                                    + "votes and cannot be deleted.");

                            return;
                        }
                    }
                }

                int accountID;

                String getAccountSQL =
                        "SELECT account_id FROM voters "
                        + "WHERE voter_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(getAccountSQL)) {

                    ps.setString(1, voterID);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Voter was not found.");
                        }

                        accountID = rs.getInt("account_id");
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(deleteAccountSQL)) {

                    ps.setInt(1, accountID);

                    int deleted = ps.executeUpdate();

                    if (deleted != 1) {
                        throw new SQLException(
                                "Could not delete voter account.");
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this, "Voter deleted successfully!");

                clearFields();
                loadVoters();

            } catch (SQLException e) {

                con.rollback();
                showError("Error deleting voter: " + e.getMessage());

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void clearFields() {

        firstNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        emailField.setText("");

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

        if (e.getSource() == updateButton) {

            updateVoter();

        } else if (e.getSource() == deleteButton) {

            deleteVoter();

        } else if (e.getSource() == clearButton) {

            clearFields();

        } else if (e.getSource() == refreshButton) {

            loadVoters();
        }
    }
}



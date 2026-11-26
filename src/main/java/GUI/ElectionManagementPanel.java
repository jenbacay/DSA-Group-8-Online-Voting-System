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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ElectionManagementPanel extends JPanel implements ActionListener {

    private JLabel titleLabel, nameLabel, startLabel, endLabel, descriptionLabel;
    private JTextField nameField, startField, endField;
    private JTextArea descriptionArea;
    private JButton addButton, updateButton, deleteButton, clearButton, startButton, endButton;
    private JTable electionTable;
    private DefaultTableModel tableModel;
    private String selectedElectionId = null;
    private final Color darkBlue = new Color(20, 55, 85);
    private final Color backgroundColor = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);
    private final Color green = new Color(20, 170, 105);
    private final Color red = new Color(210, 65, 65);

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ElectionManagementPanel() {

        setLayout(null);
        setBackground(backgroundColor);

        titleLabel = new JLabel("ELECTION MANAGEMENT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(25, 20, 400, 30);
        add(titleLabel);

        nameLabel = createLabel("Election Name:");
        nameLabel.setBounds(25, 75, 120, 25);
        add(nameLabel);

        nameField = createTextField();
        nameField.setBounds(140, 72, 200, 30);
        add(nameField);

        startLabel = createLabel("Start Date:");
        startLabel.setBounds(365, 75, 100, 25);
        add(startLabel);

        startField = createTextField();
        startField.setBounds(465, 72, 210, 30);
        startField.setText("2026-12-01 08:00:00");
        add(startField);

        endLabel = createLabel("End Date:");
        endLabel.setBounds(25, 115, 120, 25);
        add(endLabel);

        endField = createTextField();
        endField.setBounds(140, 112, 200, 30);
        endField.setText("2026-12-01 17:00:00");
        add(endField);

        descriptionLabel = createLabel("Description:");
        descriptionLabel.setBounds(365, 115, 100, 25);
        add(descriptionLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(
                new LineBorder(new Color(190, 205, 220)));

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setBounds(465, 112, 210, 65);
        add(descriptionScroll);

        addButton = createButton("Add");
        addButton.setBounds(25, 195, 90, 32);
        addButton.addActionListener(this);
        add(addButton);

        updateButton = createButton("Update");
        updateButton.setBounds(125, 195, 90, 32);
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = createButton("Delete");
        deleteButton.setBounds(225, 195, 90, 32);
        deleteButton.addActionListener(this);
        add(deleteButton);

        clearButton = createButton("Clear");
        clearButton.setBounds(325, 195, 90, 32);
        clearButton.addActionListener(this);
        add(clearButton);

        startButton = createButton("Start Election");
        startButton.setBounds(425, 195, 120, 32);
        startButton.setBackground(green);
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(this);
        add(startButton);

        endButton = createButton("End Election");
        endButton.setBounds(555, 195, 120, 32);
        endButton.setBackground(red);
        endButton.setForeground(Color.WHITE);
        endButton.addActionListener(this);
        add(endButton);

        String[] columns = {
            "Election ID",
            "Election Name",
            "Start Date",
            "End Date",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        electionTable = new JTable(tableModel);
        electionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        electionTable.setRowHeight(25);
        electionTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        electionTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        electionTable.getTableHeader().setBackground(darkBlue);
        electionTable.getTableHeader().setForeground(Color.WHITE);
        electionTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(electionTable);
        scrollPane.setBounds(25, 245, 655, 290);
        scrollPane.setBorder(
                new LineBorder(new Color(200, 210, 220)));

        add(scrollPane);

        electionTable.getSelectionModel()
                .addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = electionTable.getSelectedRow();

                if (row >= 0) {

                    selectedElectionId =
                            tableModel.getValueAt(row, 0).toString();

                    loadSelectedElection();
                }
            }
        });

        loadElections();
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(darkBlue);

        return label;
    }

    private JTextField createTextField() {

        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(
                new LineBorder(new Color(190, 205, 220)));

        return field;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(buttonColor);
        button.setForeground(darkBlue);
        button.setFocusPainted(false);
        button.setBorder(
                new LineBorder(new Color(150, 170, 190)));

        return button;
    }

    private boolean validateElection() {

        String name = nameField.getText().trim();
        String start = startField.getText().trim();
        String end = endField.getText().trim();

        if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all required fields.",
                    "Notice",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

        try {

            LocalDateTime startDate =
                    LocalDateTime.parse(start, formatter);

            LocalDateTime endDate =
                    LocalDateTime.parse(end, formatter);

            if (!endDate.isAfter(startDate)) {

                JOptionPane.showMessageDialog(
                        this,
                        "End date and time must be later than "
                        + "the start date and time.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);

                return false;
            }

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format!\n\n"
                    + "Please use:\n"
                    + "yyyy-MM-dd HH:mm:ss\n\n"
                    + "Example: 2026-12-01 08:00:00",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
    }

    private void addElection() {

        if (!validateElection()) {
            return;
        }

        String sql = "INSERT INTO elections "
                + "(election_name, description, "
                + "start_datetime, end_datetime) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, nameField.getText().trim());
                pst.setString(2, descriptionArea.getText().trim());
                pst.setString(3, startField.getText().trim());
                pst.setString(4, endField.getText().trim());

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Election added successfully!");

                clearFields();
                loadElections();
            }

        } catch (SQLException e) {

            showError("Error adding election: " + e.getMessage());
        }
    }

    private void updateElection() {

        if (selectedElectionId == null) {

            showWarning(
                    "Please select an election from the table first.");

            return;
        }

        if (!validateElection()) {
            return;
        }

        String sql = "UPDATE elections SET "
                + "election_name = ?, description = ?, "
                + "start_datetime = ?, end_datetime = ? "
                + "WHERE election_id = ? "
                + "AND status = 'NOT STARTED'";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, nameField.getText().trim());
                pst.setString(2, descriptionArea.getText().trim());
                pst.setString(3, startField.getText().trim());
                pst.setString(4, endField.getText().trim());
                pst.setInt(5, Integer.parseInt(selectedElectionId));

                int result = pst.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Election updated successfully!");

                } else {

                    showWarning(
                            "Only elections that have not started "
                            + "can be updated.");
                }

                clearFields();
                loadElections();
            }

        } catch (SQLException e) {

            showError("Error updating election: " + e.getMessage());
        }
    }

    private void deleteElection() {

        if (selectedElectionId == null) {

            showWarning(
                    "Please select an election from the table first.");

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this election?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM elections "
                + "WHERE election_id = ? "
                + "AND status = 'NOT STARTED'";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, Integer.parseInt(selectedElectionId));

                int result = pst.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Election deleted successfully!");

                } else {

                    showWarning(
                            "Only elections that have not started "
                            + "can be deleted.");
                }

                clearFields();
                loadElections();
            }

        } catch (SQLException e) {

            showError(
                    "Error deleting election: " + e.getMessage()
                    + "\n\nThe election may have related records.");
        }
    }

    private void startElection() {

        if (selectedElectionId == null) {

            showWarning("Please select an election first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to start this election?",
                "Start Election",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE elections SET status = 'ONGOING' "
                + "WHERE election_id = ? "
                + "AND status = 'NOT STARTED' "
                + "AND NOW() >= start_datetime "
                + "AND NOW() < end_datetime";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, Integer.parseInt(selectedElectionId));

                int result = pst.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Election started successfully!");

                } else {

                    showWarning(
                            "The election cannot be started.\n\n"
                            + "Check the following:\n"
                            + "- Election has not started yet.\n"
                            + "- Current date and time is within "
                            + "the election schedule.");
                }

                clearFields();
                loadElections();
            }

        } catch (SQLException e) {

            showError("Error starting election: " + e.getMessage());
        }
    }

    private void endElection() {

        if (selectedElectionId == null) {

            showWarning("Please select an election first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to end this election?",
                "End Election",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE elections SET status = 'ENDED' "
                + "WHERE election_id = ? "
                + "AND status = 'ONGOING'";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, Integer.parseInt(selectedElectionId));

                int result = pst.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Election ended successfully!");

                } else {

                    showWarning(
                            "Only ongoing elections can be ended.");
                }

                clearFields();
                loadElections();
            }

        } catch (SQLException e) {

            showError("Error ending election: " + e.getMessage());
        }
    }

    private void loadSelectedElection() {

        String sql = "SELECT * FROM elections "
                + "WHERE election_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, Integer.parseInt(selectedElectionId));

                try (ResultSet rs = pst.executeQuery()) {

                    if (rs.next()) {

                        nameField.setText(
                                rs.getString("election_name"));

                        descriptionArea.setText(
                                rs.getString("description"));

                        startField.setText(
                                rs.getString("start_datetime"));

                        endField.setText(
                                rs.getString("end_datetime"));
                    }
                }
            }

        } catch (SQLException e) {

            showError(
                    "Error loading election details: "
                    + e.getMessage());
        }
    }

    public void loadElections() {

        tableModel.setRowCount(0);

        String sql = "SELECT election_id, election_name, "
                + "start_datetime, end_datetime, status "
                + "FROM elections "
                + "ORDER BY election_id DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                showError("Could not connect to the database.");
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Object[] row = {
                        rs.getInt("election_id"),
                        rs.getString("election_name"),
                        rs.getString("start_datetime"),
                        rs.getString("end_datetime"),
                        rs.getString("status")
                    };

                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {

            showError("Error loading elections: " + e.getMessage());
        }
    }

    public void refreshElections() {
        loadElections();
    }

    private void clearFields() {

        nameField.setText("");
        startField.setText("");
        endField.setText("");
        descriptionArea.setText("");

        selectedElectionId = null;
        electionTable.clearSelection();
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

        if (e.getSource() == addButton) {

            addElection();

        } else if (e.getSource() == updateButton) {

            updateElection();

        } else if (e.getSource() == deleteButton) {

            deleteElection();

        } else if (e.getSource() == clearButton) {

            clearFields();

        } else if (e.getSource() == startButton) {

            startElection();

        } else if (e.getSource() == endButton) {

            endElection();
        }
    }
}




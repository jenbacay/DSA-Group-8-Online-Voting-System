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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ElectionManagementPanel extends JPanel implements ActionListener {

    private JLabel titleLabel, nameLabel, typeLabel, startLabel, endLabel, descriptionLabel;
    private JTextField nameField, startField, endField;
    private JComboBox<String> typeComboBox;
    private JTextArea descriptionArea;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JButton startButton, endButton;
    private JTable electionTable;
    private DefaultTableModel tableModel;
    private Integer selectedElectionId = null;
    private final Color darkBlue = new Color(20, 55, 85);
    private final Color backgroundColor = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);

    public ElectionManagementPanel() {
        setLayout(null);
        setBackground(backgroundColor);

        titleLabel = new JLabel("ELECTION MANAGEMENT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(25, 20, 400, 30);
        add(titleLabel);

        nameLabel = createLabel("Election Name:");
        nameLabel.setBounds(25, 75, 110, 25);
        add(nameLabel);

        nameField = createTextField();
        nameField.setBounds(140, 72, 200, 30);
        add(nameField);

        typeLabel = createLabel("Election Type:");
        typeLabel.setBounds(365, 75, 100, 25);
        add(typeLabel);

        typeComboBox = new JComboBox<>(new String[]{
            "SK",
            "Barangay"
        });
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        typeComboBox.setBounds(465, 72, 210, 30);
        add(typeComboBox);

        startLabel = createLabel("Start Date:");
        startLabel.setBounds(25, 115, 110, 25);
        add(startLabel);

        startField = createTextField();
        startField.setBounds(140, 112, 200, 30);
        startField.setToolTipText("Format: yyyy-MM-dd HH:mm:ss");
        add(startField);

        endLabel = createLabel("End Date:");
        endLabel.setBounds(365, 115, 100, 25);
        add(endLabel);

        endField = createTextField();
        endField.setBounds(465, 112, 210, 30);
        endField.setToolTipText("Format: yyyy-MM-dd HH:mm:ss");
        add(endField);

        descriptionLabel = createLabel("Description:");
        descriptionLabel.setBounds(25, 155, 110, 25);
        add(descriptionLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new LineBorder(new Color(190, 205, 220)));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBounds(140, 152, 535, 65);
        add(descriptionScroll);

        addButton = createButton("Add");
        addButton.setBounds(25, 235, 90, 32);
        addButton.addActionListener(this);
        add(addButton);

        updateButton = createButton("Update");
        updateButton.setBounds(125, 235, 90, 32);
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = createButton("Delete");
        deleteButton.setBounds(225, 235, 90, 32);
        deleteButton.addActionListener(this);
        add(deleteButton);

        clearButton = createButton("Clear");
        clearButton.setBounds(325, 235, 90, 32);
        clearButton.addActionListener(this);
        add(clearButton);

        startButton = createButton("Start Election");
        startButton.setBounds(425, 235, 120, 32);
        startButton.addActionListener(this);
        add(startButton);

        endButton = createButton("End Election");
        endButton.setBounds(555, 235, 120, 32);
        endButton.addActionListener(this);
        add(endButton);

        String[] columns = {
            "Election ID",
            "Election Name",
            "Election Type",
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
        electionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        electionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        electionTable.getTableHeader().setBackground(darkBlue);
        electionTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(electionTable);
        scrollPane.setBounds(25, 285, 650, 250);
        scrollPane.setBorder(new LineBorder(new Color(200, 210, 220)));
        add(scrollPane);

        electionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = electionTable.getSelectedRow();

                if (row >= 0) {
                    int modelRow = electionTable.convertRowIndexToModel(row);

                    selectedElectionId = Integer.parseInt(
                            tableModel.getValueAt(modelRow, 0).toString());

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
        field.setBorder(new LineBorder(new Color(190, 205, 220)));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(buttonColor);
        button.setForeground(darkBlue);
        button.setFocusPainted(false);
        return button;
    }

    private boolean validateElection() {
        String name = nameField.getText().trim();
        String start = startField.getText().trim();
        String end = endField.getText().trim();

        if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please complete the election name and date fields.");
            return false;
        }

        if (name.length() > 100) {
            JOptionPane.showMessageDialog(
                    this,
                    "Election name must not exceed 100 characters.");
            return false;
        }

        try {
            LocalDateTime startDate =
                    LocalDateTime.parse(start.replace(" ", "T"));

            LocalDateTime endDate =
                    LocalDateTime.parse(end.replace(" ", "T"));

            if (!endDate.isAfter(startDate)) {
                JOptionPane.showMessageDialog(
                        this,
                        "End date must be later than start date.");
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format.\n"
                    + "Please use: yyyy-MM-dd HH:mm:ss");
            return false;
        }

        return true;
    }

    private Timestamp getTimestamp(String value) {
        LocalDateTime dateTime =
                LocalDateTime.parse(value.trim().replace(" ", "T"));

        return Timestamp.valueOf(dateTime);
    }

    private void insertDefaultPositions(
            Connection con,
            int electionId,
            String electionType) throws SQLException {

        String sql = "INSERT INTO election_positions "
                + "(election_id, position_name, max_selections) "
                + "VALUES (?, ?, ?)";

        String[][] positions;

        if (electionType.equalsIgnoreCase("SK")) {
            positions = new String[][]{
                {"SK Chairperson", "1"},
                {"SK Kagawad", "7"},
                {"SK Treasurer", "1"},
                {"SK Secretary", "1"}
            };

        } else if (electionType.equalsIgnoreCase("Barangay")) {
            positions = new String[][]{
                {"Punong Barangay", "1"},
                {"Barangay Kagawad", "7"}
            };

        } else {
            throw new SQLException("Unsupported election type.");
        }

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (String[] position : positions) {
                pst.setInt(1, electionId);
                pst.setString(2, position[0]);
                pst.setInt(3, Integer.parseInt(position[1]));
                pst.addBatch();
            }

            pst.executeBatch();
        }
    }

    private void addElection() {
        if (!validateElection()) {
            return;
        }

        String sql = "INSERT INTO elections "
                + "(election_name, election_type, description, "
                + "start_datetime, end_datetime) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int electionId;

                try (PreparedStatement pst = con.prepareStatement(
                        sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    pst.setString(1, nameField.getText().trim());
                    pst.setString(2, typeComboBox.getSelectedItem().toString());
                    pst.setString(3, descriptionArea.getText().trim());
                    pst.setTimestamp(4, getTimestamp(startField.getText()));
                    pst.setTimestamp(5, getTimestamp(endField.getText()));

                    pst.executeUpdate();

                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Unable to retrieve the new election ID.");
                        }

                        electionId = rs.getInt(1);
                    }
                }

                insertDefaultPositions(
                        con,
                        electionId,
                        typeComboBox.getSelectedItem().toString());

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Election added successfully!\n"
                        + "Default positions have been created.");

                clearFields();
                loadElections();

            } catch (SQLException e) {
                con.rollback();
                JOptionPane.showMessageDialog(
                        this,
                        "Error adding election: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage());
        }
    }

    private void updateElection() {
        if (selectedElectionId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an election from the table first.");
            return;
        }

        if (!validateElection()) {
            return;
        }

        String checkSql = "SELECT status FROM elections WHERE election_id = ?";

        String updateSql = "UPDATE elections "
                + "SET election_name = ?, description = ?, "
                + "start_datetime = ?, end_datetime = ? "
                + "WHERE election_id = ? AND status = 'NOT STARTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkSql)) {

            checkPst.setInt(1, selectedElectionId);

            try (ResultSet rs = checkPst.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Election not found.");
                    return;
                }

                if (!"NOT STARTED".equalsIgnoreCase(rs.getString("status"))) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Only elections that have not started can be updated.");
                    return;
                }
            }

            try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                pst.setString(1, nameField.getText().trim());
                pst.setString(2, descriptionArea.getText().trim());
                pst.setTimestamp(3, getTimestamp(startField.getText()));
                pst.setTimestamp(4, getTimestamp(endField.getText()));
                pst.setInt(5, selectedElectionId);

                int result = pst.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Election updated successfully!");

                    clearFields();
                    loadElections();

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to update election.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error updating election: " + e.getMessage());
        }
    }

    private void deleteElection() {
        if (selectedElectionId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an election from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete this election?\n\n"
                + "This will also delete:\n"
                + "- All votes under this election\n"
                + "- All candidates under this election\n"
                + "- All election positions\n"
                + "- All voter election status records\n\n"
                + "This action cannot be undone.",
                "Confirm Election Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int electionID = selectedElectionId;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String deleteVotesSQL =
                        "DELETE FROM votes "
                        + "WHERE election_id = ? "
                        + "OR candidate_id IN "
                        + "(SELECT candidate_id FROM candidates "
                        + "WHERE election_id = ?)";

                try (PreparedStatement pst =
                        con.prepareStatement(deleteVotesSQL)) {

                    pst.setInt(1, electionID);
                    pst.setInt(2, electionID);
                    pst.executeUpdate();
                }

                String deleteStatusSQL =
                        "DELETE FROM voter_election_status "
                        + "WHERE election_id = ?";

                try (PreparedStatement pst =
                        con.prepareStatement(deleteStatusSQL)) {

                    pst.setInt(1, electionID);
                    pst.executeUpdate();
                }

                String deleteCandidatesSQL =
                        "DELETE FROM candidates WHERE election_id = ?";

                try (PreparedStatement pst =
                        con.prepareStatement(deleteCandidatesSQL)) {

                    pst.setInt(1, electionID);
                    pst.executeUpdate();
                }

                String deletePositionsSQL =
                        "DELETE FROM election_positions WHERE election_id = ?";

                try (PreparedStatement pst =
                        con.prepareStatement(deletePositionsSQL)) {

                    pst.setInt(1, electionID);
                    pst.executeUpdate();
                }

                String deleteElectionSQL =
                        "DELETE FROM elections WHERE election_id = ?";

                try (PreparedStatement pst =
                        con.prepareStatement(deleteElectionSQL)) {

                    pst.setInt(1, electionID);

                    if (pst.executeUpdate() != 1) {
                        throw new SQLException("Election was not found.");
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Election and all its related records "
                        + "were deleted successfully!");

                clearFields();
                loadElections();

            } catch (SQLException e) {
                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Error deleting election.\n\n"
                        + e.getMessage(),
                        "Deletion Failed",
                        JOptionPane.ERROR_MESSAGE);

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage());
        }
    }

    private void startElection() {
        if (selectedElectionId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an election first.");
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

        String sql = "UPDATE elections "
                + "SET status = 'ONGOING' "
                + "WHERE election_id = ? AND status = 'NOT STARTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, selectedElectionId);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Election started successfully!");

                clearFields();
                loadElections();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Election cannot be started. "
                        + "It may have already started or ended.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error starting election: " + e.getMessage());
        }
    }

    private void endElection() {
        if (selectedElectionId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an election first.");
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

        String sql = "UPDATE elections "
                + "SET status = 'ENDED' "
                + "WHERE election_id = ? AND status = 'ONGOING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, selectedElectionId);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Election ended successfully!");

                clearFields();
                loadElections();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Only ongoing elections can be ended.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error ending election: " + e.getMessage());
        }
    }

    private void loadElections() {
        tableModel.setRowCount(0);

        String sql = "SELECT election_id, election_name, election_type, "
                + "start_datetime, end_datetime, status "
                + "FROM elections ORDER BY election_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("election_id"),
                    rs.getString("election_name"),
                    rs.getString("election_type"),
                    rs.getTimestamp("start_datetime"),
                    rs.getTimestamp("end_datetime"),
                    rs.getString("status")
                };

                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading elections: " + e.getMessage());
        }
    }

    private void loadSelectedElection() {
        if (selectedElectionId == null) {
            return;
        }

        String sql = "SELECT * FROM elections WHERE election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, selectedElectionId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    nameField.setText(rs.getString("election_name"));

                    typeComboBox.setSelectedItem(
                            rs.getString("election_type"));

                    typeComboBox.setEnabled(false);

                    descriptionArea.setText(rs.getString("description"));

                    startField.setText(
                            rs.getTimestamp("start_datetime").toString());

                    endField.setText(
                            rs.getTimestamp("end_datetime").toString());
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading selected election: " + e.getMessage());
        }
    }

    public void refreshElections() {
        clearFields();
        loadElections();
    }

    private void clearFields() {
        nameField.setText("");
        startField.setText("");
        endField.setText("");
        descriptionArea.setText("");

        typeComboBox.setSelectedIndex(0);
        typeComboBox.setEnabled(true);

        selectedElectionId = null;
        electionTable.clearSelection();
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

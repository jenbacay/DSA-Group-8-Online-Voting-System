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

public class CandidatePanel extends JPanel implements ActionListener {

    private JLabel titleLabel, electionLabel, nameLabel, partyLabel, positionLabel;
    private JComboBox<ElectionItem> electionComboBox;
    private JComboBox<PositionItem> positionComboBox;
    private JTextField nameField, partyField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private String selectedCandidateId = null;
    private boolean loadingElections = false;
    private Color darkBlue = new Color(20, 55, 85);
    private Color backgroundColor = new Color(240, 246, 252);
    private Color buttonColor = new Color(220, 233, 245);

    public CandidatePanel() {

        setLayout(null);
        setBackground(backgroundColor);

        titleLabel = new JLabel("CANDIDATE MANAGEMENT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(25, 20, 400, 30);
        add(titleLabel);

        electionLabel = createLabel("Select Election:");
        electionLabel.setBounds(25, 65, 110, 25);
        add(electionLabel);

        electionComboBox = new JComboBox<>();
        electionComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        electionComboBox.setBounds(140, 62, 410, 32);
        add(electionComboBox);

        nameLabel = createLabel("Candidate Name:");
        nameLabel.setBounds(25, 110, 120, 25);
        add(nameLabel);

        nameField = createTextField();
        nameField.setBounds(140, 107, 180, 30);
        add(nameField);

        partyLabel = createLabel("Partylist:");
        partyLabel.setBounds(340, 110, 70, 25);
        add(partyLabel);

        partyField = createTextField();
        partyField.setBounds(400, 107, 150, 30);
        add(partyField);

        positionLabel = createLabel("Position:");
        positionLabel.setBounds(25, 155, 100, 25);
        add(positionLabel);

        positionComboBox = new JComboBox<>();
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        positionComboBox.setBounds(140, 152, 180, 32);
        add(positionComboBox);

        addButton = createButton("Add");
        addButton.setBounds(25, 200, 90, 32);
        addButton.addActionListener(this);
        add(addButton);

        updateButton = createButton("Update");
        updateButton.setBounds(125, 200, 90, 32);
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = createButton("Delete");
        deleteButton.setBounds(225, 200, 90, 32);
        deleteButton.addActionListener(this);
        add(deleteButton);

        clearButton = createButton("Clear");
        clearButton.setBounds(325, 200, 90, 32);
        clearButton.addActionListener(this);
        add(clearButton);

        String[] columns = {
            "Candidate ID",
            "Candidate Name",
            "Partylist",
            "Position",
            "Position ID"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 12));
        candidateTable.setRowHeight(25);

        candidateTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        candidateTable.getTableHeader().setBackground(darkBlue);
        candidateTable.getTableHeader().setForeground(Color.WHITE);

        candidateTable.removeColumn(
                candidateTable.getColumnModel().getColumn(4));

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBounds(25, 250, 655, 285);
        scrollPane.setBorder(
                new LineBorder(new Color(200, 210, 220)));

        add(scrollPane);

        loadElections();

        electionComboBox.addActionListener(e -> {

            if (!loadingElections) {

                clearFields();
                loadPositions();
                loadCandidates();
            }
        });

        candidateTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = candidateTable.getSelectedRow();

                if (row >= 0) {

                    int modelRow =
                            candidateTable.convertRowIndexToModel(row);

                    selectedCandidateId =
                            tableModel.getValueAt(modelRow, 0).toString();

                    nameField.setText(
                            tableModel.getValueAt(modelRow, 1).toString());

                    partyField.setText(
                            tableModel.getValueAt(modelRow, 2).toString());

                    int positionId = Integer.parseInt(
                            tableModel.getValueAt(modelRow, 4).toString());

                    selectPositionById(positionId);
                }
            }
        });

        loadPositions();
        loadCandidates();
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

        return button;
    }

    private static class ElectionItem {

        int id;
        String name;

        public ElectionItem(int id, String name) {

            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class PositionItem {

        int id;
        String name;
        int maxSelections;

        public PositionItem(int id, String name, int maxSelections) {

            this.id = id;
            this.name = name;
            this.maxSelections = maxSelections;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private Integer getSelectedElectionId() {

        ElectionItem selected =
                (ElectionItem) electionComboBox.getSelectedItem();

        if (selected == null || selected.id == -1) {
            return null;
        }

        return selected.id;
    }

    private PositionItem getSelectedPosition() {

        PositionItem selected =
                (PositionItem) positionComboBox.getSelectedItem();

        if (selected == null || selected.id == -1) {
            return null;
        }

        return selected;
    }

    private void loadElections() {

        loadingElections = true;

        electionComboBox.removeAllItems();

        electionComboBox.addItem(
                new ElectionItem(-1, "Select Election"));

        String sql = "SELECT election_id, election_name "
                   + "FROM elections "
                   + "ORDER BY election_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                electionComboBox.addItem(
                        new ElectionItem(
                                rs.getInt("election_id"),
                                rs.getString("election_name")
                        )
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading elections: " + e.getMessage());
        }

        electionComboBox.setSelectedIndex(0);

        loadingElections = false;
    }

    private void loadPositions() {

        positionComboBox.removeAllItems();

        positionComboBox.addItem(
                new PositionItem(-1, "Select Position", 0));

        Integer electionId = getSelectedElectionId();

        if (electionId == null) {
            return;
        }

        String sql = "SELECT position_id, position_name, max_selections "
                   + "FROM election_positions "
                   + "WHERE election_id = ? "
                   + "ORDER BY position_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, electionId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    positionComboBox.addItem(
                            new PositionItem(
                                    rs.getInt("position_id"),
                                    rs.getString("position_name"),
                                    rs.getInt("max_selections")
                            )
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading positions: " + e.getMessage());
        }
    }

    private void selectPositionById(int positionId) {

        for (int i = 0; i < positionComboBox.getItemCount(); i++) {

            PositionItem item = positionComboBox.getItemAt(i);

            if (item.id == positionId) {

                positionComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private String generateCandidateID(Connection con)
            throws SQLException {

        String sql = "SELECT MAX(CAST(SUBSTRING(candidate_id, 2) "
                   + "AS UNSIGNED)) AS last_id "
                   + "FROM candidates "
                   + "WHERE candidate_id REGEXP '^C[0-9]+$'";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                int lastID = rs.getInt("last_id");

                if (rs.wasNull()) {
                    lastID = 0;
                }

                return String.format("C%03d", lastID + 1);
            }
        }

        return "C001";
    }

    private boolean validateCandidate() {

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();

        Integer electionId = getSelectedElectionId();
        PositionItem position = getSelectedPosition();

        if (electionId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an election.");

            return false;
        }

        if (name.isEmpty() || party.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the candidate name and partylist.");

            return false;
        }

        if (position == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate position.");

            return false;
        }

        if (name.length() > 100 || party.length() > 100) {

            JOptionPane.showMessageDialog(
                    this,
                    "Candidate name and partylist must not exceed 100 characters.");

            return false;
        }

        return true;
    }

    private void addCandidate() {

        if (!validateCandidate()) {
            return;
        }

        String sql = "INSERT INTO candidates "
                   + "(candidate_id, name, party, position_id) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            String candidateID = generateCandidateID(con);

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, candidateID);
                pst.setString(2, nameField.getText().trim());
                pst.setString(3, partyField.getText().trim());
                pst.setInt(4, getSelectedPosition().id);

                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Candidate added successfully!\n\n"
                    + "Candidate ID: " + candidateID);

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding candidate: " + e.getMessage());
        }
    }

    private void updateCandidate() {

        if (selectedCandidateId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate from the table first.");

            return;
        }

        if (!validateCandidate()) {
            return;
        }

        String sql = "UPDATE candidates c "
                   + "INNER JOIN election_positions ep "
                   + "ON c.position_id = ep.position_id "
                   + "SET c.name = ?, c.party = ?, c.position_id = ? "
                   + "WHERE c.candidate_id = ? "
                   + "AND ep.election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nameField.getText().trim());
            pst.setString(2, partyField.getText().trim());
            pst.setInt(3, getSelectedPosition().id);
            pst.setString(4, selectedCandidateId);
            pst.setInt(5, getSelectedElectionId());

            int result = pst.executeUpdate();

            if (result > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Candidate updated successfully!");

                clearFields();
                loadCandidates();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Candidate not found in the selected election.");
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating candidate: " + e.getMessage());
        }
    }

    private void deleteCandidate() {

        if (selectedCandidateId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate from the table first.");

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete candidate "
                + selectedCandidateId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE c FROM candidates c "
                   + "INNER JOIN election_positions ep "
                   + "ON c.position_id = ep.position_id "
                   + "WHERE c.candidate_id = ? "
                   + "AND ep.election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, selectedCandidateId);
            pst.setInt(2, getSelectedElectionId());

            int result = pst.executeUpdate();

            if (result > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Candidate deleted successfully!");

                clearFields();
                loadCandidates();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Candidate not found in the selected election.");
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete candidate.\n"
                    + "The candidate may already be connected "
                    + "to a vote record.\n\n"
                    + e.getMessage());
        }
    }

    public void loadCandidates() {

        tableModel.setRowCount(0);

        Integer electionId = getSelectedElectionId();

        if (electionId == null) {
            return;
        }

        String sql = "SELECT c.candidate_id, c.name, c.party, "
                   + "ep.position_name, c.position_id "
                   + "FROM candidates c "
                   + "INNER JOIN election_positions ep "
                   + "ON c.position_id = ep.position_id "
                   + "WHERE ep.election_id = ? "
                   + "ORDER BY c.party, ep.position_name, c.candidate_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, electionId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Object[] row = {

                        rs.getString("candidate_id"),
                        rs.getString("name"),
                        rs.getString("party"),
                        rs.getString("position_name"),
                        rs.getInt("position_id")
                    };

                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading candidates: " + e.getMessage());
        }
    }

    public void refreshCandidates() {

        clearFields();
        loadElections();
        loadPositions();
        loadCandidates();
    }

    private void clearFields() {

        nameField.setText("");
        partyField.setText("");

        if (positionComboBox.getItemCount() > 0) {
            positionComboBox.setSelectedIndex(0);
        }

        selectedCandidateId = null;

        candidateTable.clearSelection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {

            addCandidate();

        } else if (e.getSource() == updateButton) {

            updateCandidate();

        } else if (e.getSource() == deleteButton) {

            deleteCandidate();

        } else if (e.getSource() == clearButton) {

            clearFields();
        }
    }
}



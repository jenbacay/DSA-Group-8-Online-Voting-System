
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

    private JLabel titleLabel, nameLabel, partyLabel, positionLabel;
    private JTextField nameField, partyField;
    private JComboBox<String> positionComboBox;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private int selectedCandidateId = -1;
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

        nameLabel = createLabel("Candidate Name:");
        nameLabel.setBounds(25, 75, 120, 25);
        add(nameLabel);

        nameField = createTextField();
        nameField.setBounds(140, 72, 180, 30);
        add(nameField);

        partyLabel = createLabel("Partylist:");
        partyLabel.setBounds(340, 75, 70, 25);
        add(partyLabel);

        partyField = createTextField();
        partyField.setBounds(400, 72, 150, 30);
        add(partyField);

        positionLabel = createLabel("Position:");
        positionLabel.setBounds(25, 120, 100, 25);
        add(positionLabel);

        String[] positions = {
            "Select Position",
            "SK Chairperson",
            "SK Kagawad",
            "SK Treasurer",
            "SK Secretary"
        };

        positionComboBox = new JComboBox<>(positions);
        positionComboBox.setBounds(140, 117, 180, 32);
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        add(positionComboBox);

        
        addButton = createButton("Add");
        addButton.setBounds(25, 165, 90, 32);
        addButton.addActionListener(this);
        add(addButton);

        updateButton = createButton("Update");
        updateButton.setBounds(125, 165, 90, 32);
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = createButton("Delete");
        deleteButton.setBounds(225, 165, 90, 32);
        deleteButton.addActionListener(this);
        add(deleteButton);

        clearButton = createButton("Clear");
        clearButton.setBounds(325, 165, 90, 32);
        clearButton.addActionListener(this);
        add(clearButton);

        String[] columns = {
            "Candidate ID",
            "Candidate Name",
            "Partylist",
            "Position"
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

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBounds(25, 215, 655, 320);
        scrollPane.setBorder(new LineBorder(new Color(200, 210, 220)));
        add(scrollPane);

        candidateTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                int row = candidateTable.getSelectedRow();

                if (row >= 0) {
                    selectedCandidateId =
                            Integer.parseInt(
                                    tableModel.getValueAt(row, 0).toString());

                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString());

                    partyField.setText(
                            tableModel.getValueAt(row, 2).toString());

                    positionComboBox.setSelectedItem(
                            tableModel.getValueAt(row, 3).toString());
                }
            }
        });

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

    private int getPositionLimit(String position) {

        if (position.equals("SK Chairperson")
                || position.equals("SK Treasurer")
                || position.equals("SK Secretary")) {

            return 1;

        } else if (position.equals("SK Kagawad")) {

            return 7;
        }

        return 0;
    }

    private int getCandidateCount(String party, String position,
                                  int excludeCandidateId) {

        String sql;

        if (excludeCandidateId == -1) {
            sql = "SELECT COUNT(*) FROM candidates "
                    + "WHERE party = ? AND position = ?";
        } else {
            sql = "SELECT COUNT(*) FROM candidates "
                    + "WHERE party = ? AND position = ? "
                    + "AND candidate_id <> ?";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, party);
            pst.setString(2, position);

            if (excludeCandidateId != -1) {
                pst.setInt(3, excludeCandidateId);
            }

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Error checking candidate limit: " + e.getMessage());
        }

        return -1;
    }

    private boolean validateCandidate(int excludeCandidateId) {

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();
        String position = positionComboBox.getSelectedItem().toString();

        if (name.isEmpty() || party.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please enter the candidate name and partylist.");

            return false;
        }

        if (position.equals("Select Position")) {

            JOptionPane.showMessageDialog(this,
                    "Please select a candidate position.");

            return false;
        }

        int limit = getPositionLimit(position);

        int currentCount = getCandidateCount(
                party, position, excludeCandidateId);

        if (currentCount == -1) {
            return false;
        }

        if (currentCount >= limit) {

            JOptionPane.showMessageDialog(this,
                    "This partylist has already reached the maximum "
                    + "number of candidates for " + position + ".\n\n"
                    + "Maximum allowed: " + limit,
                    "Candidate Limit Reached",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
    }

    private void addCandidate() {

        if (!validateCandidate(-1)) {
            return;
        }

        String sql = "INSERT INTO candidates (name, party, position) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nameField.getText().trim());
            pst.setString(2, partyField.getText().trim());
            pst.setString(3,
                    positionComboBox.getSelectedItem().toString());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Candidate added successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Error adding candidate: " + e.getMessage());
        }
    }

    private void updateCandidate() {

        if (selectedCandidateId == -1) {

            JOptionPane.showMessageDialog(this,
                    "Please select a candidate from the table first.");

            return;
        }

        if (!validateCandidate(selectedCandidateId)) {
            return;
        }

        String sql = "UPDATE candidates SET name = ?, party = ?, "
                + "position = ? WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nameField.getText().trim());
            pst.setString(2, partyField.getText().trim());
            pst.setString(3,
                    positionComboBox.getSelectedItem().toString());
            pst.setInt(4, selectedCandidateId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Candidate updated successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Error updating candidate: " + e.getMessage());
        }
    }

    private void deleteCandidate() {

        if (selectedCandidateId == -1) {

            JOptionPane.showMessageDialog(this,
                    "Please select a candidate from the table first.");

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this candidate?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM candidates WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, selectedCandidateId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Candidate deleted successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Unable to delete candidate. The candidate may "
                    + "already be connected to a vote record.\n\n"
                    + e.getMessage());
        }
    }

    public void loadCandidates() {

        tableModel.setRowCount(0);

        String sql = "SELECT candidate_id, name, party, position "
                + "FROM candidates ORDER BY party, position, candidate_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getString("position")
                };

                tableModel.addRow(row);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Error loading candidates: " + e.getMessage());
        }
    }

    public void refreshCandidates() {
        loadCandidates();
    }

    private void clearFields() {

        nameField.setText("");
        partyField.setText("");
        positionComboBox.setSelectedIndex(0);

        selectedCandidateId = -1;
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




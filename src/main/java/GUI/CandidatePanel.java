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

    private JLabel titleLabel, nameLabel, partyLabel;
    private JTextField nameField, partyField;

    private JButton addButton, updateButton;
    private JButton deleteButton, clearButton;

    private JTable candidateTable;
    private DefaultTableModel tableModel;

    private final Color darkBlue = new Color(20, 55, 85);
    private final Color background = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);

    public CandidatePanel() {

        setLayout(null);
        setBackground(background);

        titleLabel = new JLabel("CANDIDATE MANAGEMENT");
        titleLabel.setBounds(25, 20, 650, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);
        add(titleLabel);

        nameLabel = new JLabel("Candidate Name:");
        nameLabel.setBounds(25, 85, 115, 25);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(darkBlue);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 82, 180, 32);
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        nameField.setBorder(new LineBorder(Color.GRAY));
        add(nameField);

        partyLabel = new JLabel("Party:");
        partyLabel.setBounds(340, 85, 50, 25);
        partyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        partyLabel.setForeground(darkBlue);
        add(partyLabel);

        partyField = new JTextField();
        partyField.setBounds(390, 82, 160, 32);
        partyField.setFont(new Font("Arial", Font.PLAIN, 13));
        partyField.setBorder(new LineBorder(Color.GRAY));
        add(partyField);

        addButton = createButton("Add", 25);
        updateButton = createButton("Update", 125);
        deleteButton = createButton("Delete", 225);
        clearButton = createButton("Clear", 325);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Candidate ID",
                    "Candidate Name",
                    "Party"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 12));
        candidateTable.setRowHeight(25);
        candidateTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        candidateTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        candidateTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBounds(25, 195, 655, 320);
        scrollPane.setBorder(new LineBorder(
                new Color(125, 145, 165)));

        add(scrollPane);

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);

        candidateTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = candidateTable.getSelectedRow();

                if (row != -1) {

                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString());

                    Object party = tableModel.getValueAt(row, 2);

                    partyField.setText(
                            party == null ? "" : party.toString());
                }
            }
        });

        loadCandidates();
    }

    private JButton createButton(String text, int x) {

        JButton button = new JButton(text);

        button.setBounds(x, 135, 90, 35);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(darkBlue);
        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(
                new Color(150, 170, 190)));

        add(button);

        return button;
    }

    private void loadCandidates() {

        tableModel.setRowCount(0);

        String sql = "SELECT candidate_id, name, party "
                   + "FROM candidates ORDER BY candidate_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to the database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    tableModel.addRow(new Object[]{
                        rs.getInt("candidate_id"),
                        rs.getString("name"),
                        rs.getString("party")
                    });
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading candidates: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCandidate() {

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();

        if (name.isEmpty() || party.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the candidate name and party.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        String sql = "INSERT INTO candidates (name, party) "
                   + "VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to the database.");

                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, party);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this, "Candidate added successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding candidate: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCandidate() {

        int row = candidateTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate from the table.",
                    "No Candidate Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();

        if (name.isEmpty() || party.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the candidate name and party.");

            return;
        }

        int candidateId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String sql = "UPDATE candidates SET name = ?, party = ? "
                   + "WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to the database.");

                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, party);
                ps.setInt(3, candidateId);

                int updated = ps.executeUpdate();

                if (updated == 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Candidate not found. Please refresh the list.");

                    loadCandidates();
                    clearFields();
                    return;
                }
            }

            JOptionPane.showMessageDialog(
                    this, "Candidate updated successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating candidate: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCandidate() {

        int row = candidateTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate from the table.",
                    "No Candidate Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this candidate?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int candidateId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String sql = "DELETE FROM candidates WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to the database.");

                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this, "Candidate deleted successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting candidate: " + e.getMessage()
                    + "\nCheck whether this candidate is already "
                    + "linked to a vote.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {

        nameField.setText("");
        partyField.setText("");

        candidateTable.clearSelection();
        nameField.requestFocusInWindow();
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



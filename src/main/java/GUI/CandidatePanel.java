/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author Administrator
 */

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

public class CandidatePanel extends JFrame
        implements ActionListener {

    private JLabel titleLabel, nameLabel, partyLabel;
    private JTextField nameField, partyField;
    private JButton addButton, updateButton, deleteButton;
    private JButton clearButton, backButton;
    private JTable candidateTable;
    private DefaultTableModel tableModel;

    public Candidate() {

        setTitle("Candidate Management");
        setSize(800, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(
                new Color(230, 242, 255));

        titleLabel = new JLabel("CANDIDATE MANAGEMENT");
        titleLabel.setBounds(20, 15, 740, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 75, 125));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        nameLabel = new JLabel("Candidate Name:");
        nameLabel.setBounds(30, 75, 130, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(155, 75, 200, 32);
        nameField.setBorder(new LineBorder(Color.GRAY));
        add(nameField);

        partyLabel = new JLabel("Party:");
        partyLabel.setBounds(380, 75, 60, 25);
        add(partyLabel);

        partyField = new JTextField();
        partyField.setBounds(435, 75, 180, 32);
        partyField.setBorder(new LineBorder(Color.GRAY));
        add(partyField);

        addButton = new JButton("Add");
        addButton.setBounds(30, 125, 100, 35);
        add(addButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(145, 125, 100, 35);
        add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(260, 125, 100, 35);
        add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(375, 125, 100, 35);
        add(clearButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(530, 125, 180, 35);
        add(backButton);

        tableModel = new DefaultTableModel(
                new String[]{"Candidate ID", "Candidate Name", "Party"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBounds(30, 190, 720, 320);
        add(scrollPane);

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        backButton.addActionListener(this);

        candidateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = candidateTable.getSelectedRow();

                if (row != -1) {
                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString());
                    partyField.setText(
                            tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        loadCandidates();
    }

    private void loadCandidates() {

        tableModel.setRowCount(0);

        String sql = "SELECT candidate_id, name, party "
                   + "FROM candidates ORDER BY candidate_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Could not connect to the database.");
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
                    this, "Error loading candidates: " + e.getMessage());
        }
    }

    private void addCandidate() {

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();

        if (name.isEmpty() || party.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter the candidate name and party.");
            return;
        }

        String sql = "INSERT INTO candidates (name, party) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Could not connect to the database.");
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
                    this, "Error adding candidate: " + e.getMessage());
        }
    }

    private void updateCandidate() {

        int row = candidateTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a candidate from the table.");
            return;
        }

        String name = nameField.getText().trim();
        String party = partyField.getText().trim();

        if (name.isEmpty() || party.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter the candidate name and party.");
            return;
        }

        int candidateId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String sql = "UPDATE candidates SET name = ?, party = ? "
                   + "WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Could not connect to the database.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, party);
                ps.setInt(3, candidateId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this, "Candidate updated successfully!");

            clearFields();
            loadCandidates();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error updating candidate: " + e.getMessage());
        }
    }

    private void deleteCandidate() {

        int row = candidateTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a candidate from the table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this candidate?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int candidateId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String sql = "DELETE FROM candidates WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Could not connect to the database.");
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
                    + "\nCheck whether this candidate is already linked "
                    + "to a vote.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        partyField.setText("");
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

        } else if (e.getSource() == backButton) {
            new Dashboard().setVisible(true);
            dispose();
        }
    }
}




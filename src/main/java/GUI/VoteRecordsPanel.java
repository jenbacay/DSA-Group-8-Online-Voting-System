/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author annem
 */
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

public class VoteRecordsPanel extends JPanel implements ActionListener {

    private JLabel titleLabel, searchLabel;
    private JTextField searchField;
    private JButton searchButton, clearButton, refreshButton;
    private JTable voteTable;
    private DefaultTableModel tableModel;
    private final Color darkBlue = new Color(20, 55, 85);
    private final Color background = new Color(240, 246, 252);
    private final Color buttonColor = new Color(220, 233, 245);

    public VoteRecordsPanel() {

        setLayout(null);
        setBackground(background);

        titleLabel = new JLabel("VOTE RECORDS");
        titleLabel.setBounds(25, 20, 650, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);
        add(titleLabel);

        searchLabel = new JLabel("Search Vote:");
        searchLabel.setBounds(25, 85, 100, 25);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchLabel.setForeground(darkBlue);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(125, 82, 200, 32);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(new LineBorder(Color.GRAY));
        add(searchField);

        searchButton = createButton("Search", 340);
        clearButton = createButton("Clear", 440);
        refreshButton = createButton("Refresh", 540);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Vote ID",
                    "Voter ID",
                    "Candidate ID",
                    "Timestamp"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        voteTable = new JTable(tableModel);
        voteTable.setFont(new Font("Arial", Font.PLAIN, 12));
        voteTable.setRowHeight(25);
        voteTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        voteTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        voteTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(voteTable);
        scrollPane.setBounds(25, 140, 655, 375);
        scrollPane.setBorder(new LineBorder(
                new Color(125, 145, 165)));

        add(scrollPane);

        searchButton.addActionListener(this);
        clearButton.addActionListener(this);
        refreshButton.addActionListener(this);
        searchField.addActionListener(this);

        loadVoteRecords("");
    }

    private JButton createButton(String text, int x) {

        JButton button = new JButton(text);

        button.setBounds(x, 82, 90, 32);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(darkBlue);
        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(
                new Color(150, 170, 190)));

        add(button);

        return button;
    }

    private void loadVoteRecords(String keyword) {

        tableModel.setRowCount(0);

        String sql = "SELECT vote_id, voter_id, candidate_id, timestamp "
                   + "FROM votes "
                   + "WHERE CAST(vote_id AS CHAR) LIKE ? "
                   + "OR voter_id LIKE ? "
                   + "OR CAST(candidate_id AS CHAR) LIKE ? "
                   + "ORDER BY timestamp DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to the database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                String search = "%" + keyword + "%";

                ps.setString(1, search);
                ps.setString(2, search);
                ps.setString(3, search);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        tableModel.addRow(new Object[]{
                            rs.getInt("vote_id"),
                            rs.getString("voter_id"),
                            rs.getInt("candidate_id"),
                            rs.getTimestamp("timestamp")
                        });
                    }
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading vote records: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearch() {

        searchField.setText("");
        loadVoteRecords("");
        searchField.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == searchButton
                || e.getSource() == searchField) {

            loadVoteRecords(searchField.getText().trim());

        } else if (e.getSource() == clearButton) {

            clearSearch();

        } else if (e.getSource() == refreshButton) {

            loadVoteRecords(searchField.getText().trim());
        }
    }
}
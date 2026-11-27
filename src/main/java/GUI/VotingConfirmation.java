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
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class VotingConfirmation extends JFrame implements ActionListener {

    private String voterName;
    private String voterID;
    private int electionID;
    private String electionName;

    private List<String> candidateIDs;
    private List<String> selectedCandidates;

    private Runnable completionAction;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel candidatePanel;

    private JLabel titleLabel;
    private JLabel electionLabel;
    private JLabel voterLabel;
    private JLabel instructionLabel;

    private JButton confirmButton;
    private JButton cancelButton;

    private Color darkBlue = new Color(15, 61, 120);
    private Color blue = new Color(18, 130, 220);
    private Color lightBlue = new Color(225, 242, 255);
    private Color backgroundColor = new Color(240, 246, 252);

    public VotingConfirmation(
            String voterName,
            String voterID,
            int electionID,
            String electionName,
            List<String> candidateIDs,
            List<String> selectedCandidates,
            Runnable completionAction) {

        this.voterName = voterName;
        this.voterID = voterID;
        this.electionID = electionID;
        this.electionName = electionName;
        this.candidateIDs = candidateIDs;
        this.selectedCandidates = selectedCandidates;
        this.completionAction = completionAction;

        setTitle("Confirm Your Vote");
        setSize(670, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel(null);
        mainPanel.setBackground(backgroundColor);

        headerPanel = new JPanel(null);
        headerPanel.setBackground(darkBlue);
        headerPanel.setBounds(0, 0, 670, 115);

        titleLabel = new JLabel("VOTING CONFIRMATION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 20, 600, 35);
        headerPanel.add(titleLabel);

        electionLabel = new JLabel("Election: " + electionName);
        electionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        electionLabel.setForeground(Color.WHITE);
        electionLabel.setBounds(30, 62, 600, 25);
        headerPanel.add(electionLabel);

        voterLabel = new JLabel("Voter: " + voterName + " | Voter ID: " + voterID);
        voterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        voterLabel.setForeground(darkBlue);
        voterLabel.setBounds(30, 130, 600, 25);

        instructionLabel = new JLabel("Please review your selected candidates before confirming.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionLabel.setForeground(new Color(80, 80, 80));
        instructionLabel.setBounds(30, 165, 600, 25);

        candidatePanel = new JPanel(null);
        candidatePanel.setBackground(Color.WHITE);
        candidatePanel.setBorder(new LineBorder(new Color(210, 220, 230), 1));

        int y = 15;

        for (int i = 0; i < selectedCandidates.size(); i++) {

            JLabel candidateLabel = new JLabel((i + 1) + ". " + selectedCandidates.get(i));
            candidateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            candidateLabel.setForeground(darkBlue);
            candidateLabel.setBounds(20, y, 570, 30);

            candidatePanel.add(candidateLabel);
            y += 40;
        }

        candidatePanel.setPreferredSize(new java.awt.Dimension(590, Math.max(y + 15, 80)));

        JScrollPane scrollPane = new JScrollPane(candidatePanel);
        scrollPane.setBounds(30, 205, 595, 330);
        scrollPane.setBorder(new LineBorder(new Color(210, 220, 230), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        confirmButton = new JButton("CONFIRM VOTE");
        confirmButton.setBounds(335, 560, 145, 40);
        confirmButton.setBackground(blue);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        confirmButton.addActionListener(this);

        cancelButton = new JButton("CANCEL");
        cancelButton.setBounds(490, 560, 135, 40);
        cancelButton.setBackground(new Color(230, 235, 240));
        cancelButton.setForeground(darkBlue);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.addActionListener(this);

        mainPanel.add(headerPanel);
        mainPanel.add(voterLabel);
        mainPanel.add(instructionLabel);
        mainPanel.add(scrollPane);
        mainPanel.add(confirmButton);
        mainPanel.add(cancelButton);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == confirmButton) {

            if (candidateIDs == null || candidateIDs.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No candidates were selected.",
                        "Invalid Vote",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to submit your vote?\nYou cannot change it after confirmation.",
                    "Confirm Vote",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                saveVotes();
            }

        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    private void saveVotes() {

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String electionQuery =
                    "SELECT status, start_datetime, end_datetime " +
                    "FROM elections WHERE election_id = ? FOR UPDATE";

            try (PreparedStatement pst = con.prepareStatement(electionQuery)) {

                pst.setInt(1, electionID);

                try (ResultSet rs = pst.executeQuery()) {

                    if (!rs.next()) {
                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "The selected election could not be found.",
                                "Election Not Found",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    String status = rs.getString("status");
                    Timestamp startTimestamp = rs.getTimestamp("start_datetime");
                    Timestamp endTimestamp = rs.getTimestamp("end_datetime");

                    LocalDateTime now = LocalDateTime.now();

                    boolean isOpen = "ONGOING".equalsIgnoreCase(status)
                            && startTimestamp != null
                            && endTimestamp != null
                            && !now.isBefore(startTimestamp.toLocalDateTime())
                            && now.isBefore(endTimestamp.toLocalDateTime());

                    if (!isOpen) {
                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "This election is no longer open for voting.",
                                "Voting Closed",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                }
            }

            String duplicateQuery =
                    "SELECT COUNT(*) FROM voter_election_status " +
                    "WHERE voter_id = ? AND election_id = ?";

            try (PreparedStatement pst = con.prepareStatement(duplicateQuery)) {

                pst.setString(1, voterID);
                pst.setInt(2, electionID);

                try (ResultSet rs = pst.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "You have already voted in this election.",
                                "Already Voted",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                }
            }

            String candidateQuery =
                    "SELECT COUNT(*) FROM candidates c " +
                    "INNER JOIN election_positions ep " +
                    "ON c.position_id = ep.position_id " +
                    "WHERE c.candidate_id = ? AND ep.election_id = ?";

            for (String candidateID : candidateIDs) {

                try (PreparedStatement pst = con.prepareStatement(candidateQuery)) {

                    pst.setString(1, candidateID);
                    pst.setInt(2, electionID);

                    try (ResultSet rs = pst.executeQuery()) {

                        if (rs.next() && rs.getInt(1) == 0) {
                            con.rollback();

                            JOptionPane.showMessageDialog(
                                    this,
                                    "One or more selected candidates do not belong to this election.",
                                    "Invalid Candidate",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                    }
                }
            }

            String insertVote =
                    "INSERT INTO votes " +
                    "(voter_id, election_id, candidate_id, timestamp) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

            try (PreparedStatement pst = con.prepareStatement(insertVote)) {

                for (String candidateID : candidateIDs) {

                    pst.setString(1, voterID);
                    pst.setInt(2, electionID);
                    pst.setString(3, candidateID);

                    pst.addBatch();
                }

                pst.executeBatch();
            }

            String insertStatus =
                    "INSERT INTO voter_election_status " +
                    "(voter_id, election_id, voted_at) " +
                    "VALUES (?, ?, CURRENT_TIMESTAMP)";

            try (PreparedStatement pst = con.prepareStatement(insertStatus)) {

                pst.setString(1, voterID);
                pst.setInt(2, electionID);

                pst.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Your vote has been successfully submitted!",
                    "Vote Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (completionAction != null) {
                completionAction.run();
            }

        } catch (SQLException ex) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while submitting your vote:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

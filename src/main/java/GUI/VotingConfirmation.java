package GUI;

import Database.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VotingConfirmation extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, successLabel, messageLabel;
    private JLabel nameLabel, idLabel, dateLabel, nameValue, idValue;
    private JLabel candidatesLabel;
    private JPanel mainPanel, headerPanel, checkPanel, voterPanel, circle;
    private JTextArea candidatesArea;
    private JScrollPane candidateScrollPane;
    private JButton confirmButton;

    private Color green = new Color(24, 165, 100);
    private Color blue = new Color(18, 130, 220);
    private Color darkBlue = new Color(15, 61, 120);
    private Color lightBlue = new Color(225, 242, 255);
    private Color borderColor = new Color(190, 215, 240);

    private String voterName;
    private String voterID;
    private List<String> selectedCandidates;

    public VotingConfirmation(
            String voterName,
            String voterID,
            List<String> selectedCandidates) {

        this.voterName = voterName;
        this.voterID = voterID;
        this.selectedCandidates = selectedCandidates == null
                ? new ArrayList<>()
                : new ArrayList<>(selectedCandidates);

        setTitle("Online Voting System - Vote Confirmation");
        setSize(670, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(lightBlue);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(borderColor, 1));
        mainPanel.setBounds(10, 10, 634, 590);
        add(mainPanel);

        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBounds(20, 10, 594, 65);
        mainPanel.add(headerPanel);

        titleLabel = new JLabel("ONLINE VOTING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(10, 0, 574, 35);
        headerPanel.add(titleLabel);

        subtitleLabel = new JLabel("VOTE CONFIRMATION");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        subtitleLabel.setForeground(blue);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(10, 35, 574, 25);
        headerPanel.add(subtitleLabel);

        checkPanel = new JPanel();
        checkPanel.setLayout(null);
        checkPanel.setBackground(Color.WHITE);
        checkPanel.setBounds(250, 75, 120, 100);
        mainPanel.add(checkPanel);

        circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(green);
                g2.fillOval(10, 5, 95, 95);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(
                    7,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
                ));

                int[] x = {35, 52, 82};
                int[] y = {53, 70, 36};

                g2.drawPolyline(x, y, 3);
                g2.dispose();
            }
        };

        circle.setOpaque(false);
        circle.setBounds(0, 0, 120, 105);
        checkPanel.add(circle);

        successLabel = new JLabel("Review Your Selections");
        successLabel.setFont(new Font("Arial", Font.BOLD, 23));
        successLabel.setForeground(green);
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        successLabel.setBounds(30, 175, 574, 30);
        mainPanel.add(successLabel);

        messageLabel = new JLabel(
            "Please review your selected candidates before confirming."
        );
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(30, 205, 574, 25);
        mainPanel.add(messageLabel);

        voterPanel = new JPanel();
        voterPanel.setLayout(null);
        voterPanel.setBackground(lightBlue);
        voterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        voterPanel.setBounds(30, 240, 574, 70);
        mainPanel.add(voterPanel);

        nameLabel = new JLabel("Voter Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(darkBlue);
        nameLabel.setBounds(15, 10, 110, 20);
        voterPanel.add(nameLabel);

        nameValue = new JLabel(voterName);
        nameValue.setFont(new Font("Arial", Font.PLAIN, 13));
        nameValue.setForeground(darkBlue);
        nameValue.setBounds(130, 10, 410, 20);
        voterPanel.add(nameValue);

        idLabel = new JLabel("Voter ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        idLabel.setForeground(darkBlue);
        idLabel.setBounds(15, 38, 110, 20);
        voterPanel.add(idLabel);

        idValue = new JLabel(voterID);
        idValue.setFont(new Font("Arial", Font.PLAIN, 13));
        idValue.setForeground(darkBlue);
        idValue.setBounds(130, 38, 410, 20);
        voterPanel.add(idValue);

        candidatesLabel = new JLabel("YOUR SELECTED CANDIDATES");
        candidatesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        candidatesLabel.setForeground(darkBlue);
        candidatesLabel.setBounds(30, 320, 574, 25);
        mainPanel.add(candidatesLabel);

        candidatesArea = new JTextArea();
        candidatesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        candidatesArea.setForeground(darkBlue);
        candidatesArea.setBackground(new Color(242, 249, 253));
        candidatesArea.setEditable(false);
        candidatesArea.setLineWrap(true);
        candidatesArea.setWrapStyleWord(true);
        candidatesArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        if (!this.selectedCandidates.isEmpty()) {
            for (String candidate : this.selectedCandidates) {
                candidatesArea.append(candidate + "\n\n");
            }
        } else {
            candidatesArea.setText("No candidates were provided.");
        }

        candidateScrollPane = new JScrollPane(candidatesArea);
        candidateScrollPane.setBorder(new LineBorder(borderColor));
        candidateScrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        candidateScrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        candidateScrollPane.setBounds(30, 350, 574, 130);
        mainPanel.add(candidateScrollPane);

        dateLabel = new JLabel("Please confirm your selections.");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBounds(30, 490, 574, 20);
        mainPanel.add(dateLabel);

        confirmButton = new JButton("CONFIRM");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 15));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(blue);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setBounds(205, 525, 220, 42);
        mainPanel.add(confirmButton);

        confirmButton.addActionListener(this);
    }

    private List<String> extractCandidateIDs() {

        Set<String> candidateIDs = new LinkedHashSet<>();

        Pattern pattern = Pattern.compile(
            "^.*?:\\s*([^\\s]+)\\s+-\\s+.*$"
        );

        for (String candidate : selectedCandidates) {

            Matcher matcher = pattern.matcher(candidate);

            if (matcher.matches()) {
                candidateIDs.add(matcher.group(1));
            }
        }

        return new ArrayList<>(candidateIDs);
    }

    private boolean saveVotes() {

        List<String> candidateIDs = extractCandidateIDs();

        if (candidateIDs.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No valid candidate selections were found.",
                "Invalid Selections",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (candidateIDs.size() != selectedCandidates.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Some candidate selections could not be identified.",
                "Invalid Selections",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        String electionSQL =
            "SELECT election_id FROM elections " +
            "WHERE status = 'ONGOING'";

        String duplicateSQL =
            "SELECT COUNT(*) FROM votes v " +
            "INNER JOIN candidates c ON v.candidate_id = c.candidate_id " +
            "INNER JOIN election_positions ep ON c.position_id = ep.position_id " +
            "WHERE v.voter_id = ? AND ep.election_id = ?";

        String candidateSQL =
            "SELECT COUNT(*) FROM candidates c " +
            "INNER JOIN election_positions ep ON c.position_id = ep.position_id " +
            "WHERE c.candidate_id = ? AND ep.election_id = ?";

        String insertSQL =
            "INSERT INTO votes (voter_id, candidate_id, `timestamp`) " +
            "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            con.setAutoCommit(false);

            try {
                int electionID;

                try (PreparedStatement ps = con.prepareStatement(electionSQL);
                     ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                            "There is no ongoing election."
                        );
                    }

                    electionID = rs.getInt("election_id");

                    if (rs.next()) {
                        throw new SQLException(
                            "More than one election is marked as ongoing. " +
                            "Please ask the administrator to check the election status."
                        );
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(duplicateSQL)) {

                    ps.setString(1, voterID);
                    ps.setInt(2, electionID);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new SQLException(
                                "You have already submitted your vote for this election."
                            );
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(candidateSQL)) {

                    for (String candidateID : candidateIDs) {

                        ps.setString(1, candidateID);
                        ps.setInt(2, electionID);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || rs.getInt(1) == 0) {
                                throw new SQLException(
                                    "Candidate " + candidateID +
                                    " does not belong to the ongoing election."
                                );
                            }
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(insertSQL)) {

                    for (String candidateID : candidateIDs) {
                        ps.setString(1, voterID);
                        ps.setString(2, candidateID);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                con.commit();
                return true;

            } catch (SQLException ex) {
                con.rollback();
                throw ex;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Voting Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == confirmButton) {

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit your votes? " +
                "You cannot vote again in this election.",
                "Confirm Votes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {

                confirmButton.setEnabled(false);

                if (saveVotes()) {

                    successLabel.setText("Votes Successfully Submitted!");
                    messageLabel.setText(
                        "Your votes have been saved successfully."
                    );

                    dateLabel.setText(
                        "Your vote has been recorded in the database."
                    );

                    JOptionPane.showMessageDialog(
                        this,
                        "Your votes have been submitted successfully!",
                        "Voting Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    VoterDashboard dashboard = new VoterDashboard();
                    dashboard.setVisible(true);
                    this.dispose();

                } else {
                    confirmButton.setEnabled(true);
                }
            }
        }
    }
}

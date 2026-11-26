package GUI;

import Database.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CastVotePanel extends JFrame implements ActionListener {

    private JLabel titleLabel, voterIDLabel, electionLabel;
    private JPanel mainPanel, headerPanel, candidatePanel;
    private JScrollPane scrollPane;
    private JButton submitButton, backButton;

    private Map<String, ButtonGroup> positionGroups;
    private Map<String, List<AbstractButton>> positionControls;
    private Map<AbstractButton, Candidate> candidateSelections;
    private Map<String, Integer> positionLimits;

    private Color darkBlue = new Color(15, 61, 120);
    private Color blue = new Color(18, 130, 220);
    private Color lightBlue = new Color(225, 242, 255);
    private Color borderColor = new Color(220, 225, 230);

    private String voterName;
    private String voterID;
    private String electionName;
    private int electionID;
    private String electionStatus;

    public CastVotePanel(
            String voterName,
            String voterID,
            int electionID,
            String electionName,
            String electionStatus) {

        this.voterName = voterName;
        this.voterID = voterID;
        this.electionID = electionID;
        this.electionName = electionName;
        this.electionStatus = electionStatus;

        setTitle("Online Voting System - Cast Your Vote");
        setSize(670, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(lightBlue);

        mainPanel = new JPanel(new BorderLayout(15, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBounds(0, 0, 654, 461);
        add(mainPanel);

        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("Welcome, " + voterName + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);

        voterIDLabel = new JLabel("Voter ID: " + voterID);
        voterIDLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        voterIDLabel.setForeground(new Color(50, 90, 140));

        electionLabel = new JLabel("Election: " + electionName);
        electionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        electionLabel.setForeground(new Color(35, 85, 130));

        JLabel instructionLabel = new JLabel(
            "Select candidates for each position."
        );
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(voterIDLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(electionLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(instructionLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        candidatePanel = new JPanel();
        candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.Y_AXIS));
        candidatePanel.setBackground(Color.WHITE);
        candidatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        positionGroups = new LinkedHashMap<>();
        positionControls = new LinkedHashMap<>();
        candidateSelections = new LinkedHashMap<>();
        positionLimits = new LinkedHashMap<>();

        scrollPane = new JScrollPane(candidatePanel);
        scrollPane.setBorder(new LineBorder(borderColor, 1));
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttonPanel.setBackground(Color.WHITE);

        submitButton = new JButton("Submit Vote");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(blue);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(145, 38));

        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(darkBlue);
        backButton.setBackground(lightBlue);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(155, 38));

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(this);
        backButton.addActionListener(this);

        loadCandidates();
    }

    private void loadCandidates() {

        candidatePanel.removeAll();
        positionGroups.clear();
        positionControls.clear();
        candidateSelections.clear();
        positionLimits.clear();

        submitButton.setEnabled(true);

        if (!isVotingOpen()) {
            showNoCandidatesMessage("This election is not open for voting.");
            return;
        }

        if (hasAlreadyVoted()) {
            showNoCandidatesMessage(
                "You have already submitted your vote for this election."
            );
            return;
        }

        Map<String, List<Candidate>> candidateList = new LinkedHashMap<>();

        String sql =
            "SELECT ep.position_name, ep.max_selections, " +
            "c.candidate_id, c.name, c.party " +
            "FROM candidates c " +
            "INNER JOIN election_positions ep " +
            "ON c.position_id = ep.position_id " +
            "WHERE ep.election_id = ? " +
            "ORDER BY ep.position_id, c.party, c.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            ps.setInt(1, electionID);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String position = rs.getString("position_name");
                    int maxSelections = rs.getInt("max_selections");

                    Candidate candidate = new Candidate(
                        rs.getString("candidate_id"),
                        rs.getString("name"),
                        rs.getString("party"),
                        position
                    );

                    if (!candidateList.containsKey(position)) {
                        candidateList.put(position, new ArrayList<>());
                        positionLimits.put(
                            position,
                            Math.max(maxSelections, 1)
                        );
                    }

                    candidateList.get(position).add(candidate);
                }
            }

            if (candidateList.isEmpty()) {
                showNoCandidatesMessage(
                    "No candidates are available for this election."
                );
                return;
            }

            for (String position : candidateList.keySet()) {

                List<Candidate> candidates = candidateList.get(position);
                int maxSelections = positionLimits.get(position);

                JLabel positionLabel = new JLabel(position);
                positionLabel.setFont(new Font("Arial", Font.BOLD, 16));
                positionLabel.setForeground(darkBlue);
                positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                candidatePanel.add(positionLabel);
                candidatePanel.add(Box.createVerticalStrut(8));

                JPanel positionPanel = new JPanel();
                positionPanel.setLayout(
                    new BoxLayout(positionPanel, BoxLayout.Y_AXIS)
                );
                positionPanel.setBackground(Color.WHITE);
                positionPanel.setBorder(
                    BorderFactory.createCompoundBorder(
                        new LineBorder(borderColor, 1),
                        new EmptyBorder(10, 12, 10, 12)
                    )
                );
                positionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                positionPanel.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 400)
                );

                JLabel limitLabel = new JLabel(
                    maxSelections == 1
                        ? "Select 1 candidate"
                        : "Select up to " + maxSelections + " candidates"
                );

                limitLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                limitLabel.setForeground(Color.GRAY);

                positionPanel.add(limitLabel);
                positionPanel.add(Box.createVerticalStrut(8));

                List<AbstractButton> controls = new ArrayList<>();
                positionControls.put(position, controls);

                if (maxSelections == 1) {

                    ButtonGroup group = new ButtonGroup();
                    positionGroups.put(position, group);

                    for (Candidate candidate : candidates) {

                        JRadioButton radioButton =
                            new JRadioButton(candidate.toString());

                        styleCandidate(radioButton);

                        group.add(radioButton);
                        controls.add(radioButton);
                        candidateSelections.put(radioButton, candidate);
                        positionPanel.add(radioButton);
                    }

                } else {

                    for (Candidate candidate : candidates) {

                        JCheckBox checkBox =
                            new JCheckBox(candidate.toString());

                        styleCandidate(checkBox);

                        checkBox.addActionListener(e -> {

                            int selectedCount = 0;

                            for (AbstractButton button : controls) {
                                if (button.isSelected()) {
                                    selectedCount++;
                                }
                            }

                            if (selectedCount > maxSelections) {

                                checkBox.setSelected(false);

                                JOptionPane.showMessageDialog(
                                    this,
                                    "You can select up to " +
                                    maxSelections + " candidates for " +
                                    position + ".",
                                    "Selection Limit",
                                    JOptionPane.WARNING_MESSAGE
                                );
                            }
                        });

                        controls.add(checkBox);
                        candidateSelections.put(checkBox, candidate);
                        positionPanel.add(checkBox);
                    }
                }

                candidatePanel.add(positionPanel);
                candidatePanel.add(Box.createVerticalStrut(15));
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading candidates: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            submitButton.setEnabled(false);
        }

        candidatePanel.revalidate();
        candidatePanel.repaint();
    }

    private boolean isVotingOpen() {

        String sql =
            "SELECT status FROM elections WHERE election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                return false;
            }

            ps.setInt(1, electionID);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String status = rs.getString("status");

                    return status != null &&
                        (status.equalsIgnoreCase("ONGOING") ||
                         status.equalsIgnoreCase("OPEN"));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to check election status: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        return false;
    }

    private boolean hasAlreadyVoted() {

        String sql =
            "SELECT COUNT(*) FROM voter_election_status " +
            "WHERE voter_id = ? AND election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                return true;
            }

            ps.setString(1, voterID);
            ps.setInt(2, electionID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to check voting status: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            return true;
        }
    }

    private void showNoCandidatesMessage(String message) {

        candidatePanel.removeAll();

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        candidatePanel.add(messageLabel);

        submitButton.setEnabled(false);

        candidatePanel.revalidate();
        candidatePanel.repaint();
    }

    private void styleCandidate(AbstractButton button) {

        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(new Color(30, 55, 85));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private boolean validateSelections() {

        for (String position : positionControls.keySet()) {

            List<AbstractButton> controls = positionControls.get(position);
            int selectedCount = 0;

            for (AbstractButton button : controls) {
                if (button.isSelected()) {
                    selectedCount++;
                }
            }

            if (selectedCount == 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a candidate for " + position + ".",
                    "Incomplete Vote",
                    JOptionPane.WARNING_MESSAGE
                );

                return false;
            }

            if (selectedCount > positionLimits.get(position)) {
                return false;
            }
        }

        return true;
    }

    private List<String> getSelectedCandidateIDs() {

        List<String> candidateIDs = new ArrayList<>();

        for (AbstractButton button : candidateSelections.keySet()) {

            if (button.isSelected()) {
                candidateIDs.add(
                    candidateSelections.get(button).candidateID
                );
            }
        }

        return candidateIDs;
    }

    private List<String> getSelectedCandidates() {

        List<String> selectedCandidates = new ArrayList<>();

        for (AbstractButton button : candidateSelections.keySet()) {

            if (button.isSelected()) {

                Candidate candidate = candidateSelections.get(button);

                selectedCandidates.add(
                    candidate.position + ": " +
                    candidate.candidateID + " - " +
                    candidate.name + " (" +
                    candidate.party + ")"
                );
            }
        }

        return selectedCandidates;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitButton) {

            if (!validateSelections()) {
                return;
            }

            if (!isVotingOpen() || hasAlreadyVoted()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Voting is no longer available for this election.",
                    "Voting Unavailable",
                    JOptionPane.WARNING_MESSAGE
                );

                submitButton.setEnabled(false);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to review and confirm your selections?",
                "Review Votes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {

                VotingConfirmation confirmation =
                    new VotingConfirmation(
                        voterName,
                        voterID,
                        electionID,
                        electionName,
                        getSelectedCandidateIDs(),
                        getSelectedCandidates()
                    );

                confirmation.setVisible(true);
                dispose();
            }

        } else if (e.getSource() == backButton) {

            VoterDashboard dashboard =
                new VoterDashboard(voterName, voterID);

            dashboard.setVisible(true);
            dispose();
        }
    }

    private static class Candidate {

        private String candidateID;
        private String name;
        private String party;
        private String position;

        public Candidate(
            String candidateID,
            String name,
            String party,
            String position
        ) {
            this.candidateID = candidateID;
            this.name = name;
            this.party = party;
            this.position = position;
        }

        @Override
        public String toString() {
            return candidateID + " - " + name + " (" + party + ")";
        }
    }
}


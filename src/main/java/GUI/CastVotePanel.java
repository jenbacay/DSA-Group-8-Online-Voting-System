package GUI;

import Database.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public CastVotePanel() {
        this("Voter", "Not provided");
    }

    public CastVotePanel(String voterName, String voterID) {

        this.voterName = voterName;
        this.voterID = voterID;

        setTitle("Online Voting System - Cast Your Vote");
        setSize(670, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(lightBlue);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 10));
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

        electionLabel = new JLabel("Election: Loading...");
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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
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

        Map<String, List<Candidate>> candidateList = new LinkedHashMap<>();

        String sql =
            "SELECT e.election_name, ep.position_name, ep.max_selections, " +
            "c.candidate_id, c.name, c.party " +
            "FROM candidates c " +
            "INNER JOIN election_positions ep " +
            "ON c.position_id = ep.position_id " +
            "INNER JOIN elections e " +
            "ON ep.election_id = e.election_id " +
            "WHERE e.status = 'ONGOING' " +
            "ORDER BY ep.position_id, c.party, c.name";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                electionLabel.setText("Database connection unavailable");
                showMessage("Database connection failed.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                String electionName = null;

                while (rs.next()) {

                    if (electionName == null) {
                        electionName = rs.getString("election_name");
                    }

                    String position = rs.getString("position_name");
                    int maxSelections = rs.getInt("max_selections");

                    if (maxSelections < 1) {
                        maxSelections = 1;
                    }

                    Candidate candidate = new Candidate(
                        rs.getString("candidate_id"),
                        rs.getString("name"),
                        rs.getString("party"),
                        position
                    );

                    if (!candidateList.containsKey(position)) {
                        candidateList.put(position, new ArrayList<>());
                        positionLimits.put(position, maxSelections);
                    }

                    candidateList.get(position).add(candidate);
                }

                if (electionName == null) {
                    electionLabel.setText("No ongoing election");
                    showNoCandidatesMessage();
                    return;
                }

                electionLabel.setText("Election: " + electionName);
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

                        JRadioButton radioButton = new JRadioButton(
                            candidate.toString()
                        );

                        styleCandidate(radioButton);
                        group.add(radioButton);
                        controls.add(radioButton);
                        candidateSelections.put(radioButton, candidate);
                        positionPanel.add(radioButton);
                    }

                } else {

                    for (Candidate candidate : candidates) {

                        JCheckBox checkBox = new JCheckBox(
                            candidate.toString()
                        );

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

            if (candidateList.isEmpty()) {
                showNoCandidatesMessage();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading candidates: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        candidatePanel.revalidate();
        candidatePanel.repaint();
    }

    private void showNoCandidatesMessage() {

        candidatePanel.removeAll();

        JLabel messageLabel = new JLabel(
            "No candidates are available for an ongoing election."
        );
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        candidatePanel.add(messageLabel);
        candidatePanel.revalidate();
        candidatePanel.repaint();

        submitButton.setEnabled(false);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Notice",
            JOptionPane.WARNING_MESSAGE
        );
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

            int maxSelections = positionLimits.get(position);

            if (selectedCount > maxSelections) {
                JOptionPane.showMessageDialog(
                    this,
                    "You can select up to " + maxSelections +
                    " candidates for " + position + ".",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
        }

        return true;
    }

    private List<String> getSelectedCandidates() {

        List<String> selectedCandidates = new ArrayList<>();

        for (String position : positionControls.keySet()) {

            List<AbstractButton> controls = positionControls.get(position);

            for (AbstractButton button : controls) {

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
        }

        return selectedCandidates;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitButton) {

            if (!validateSelections()) {
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit your selections?",
                "Confirm Votes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {

                List<String> selectedCandidates = getSelectedCandidates();

                VotingConfirmation confirmation = new VotingConfirmation(
                    voterName,
                    voterID,
                    selectedCandidates
                );

                confirmation.setVisible(true);
                this.dispose();
            }

        } else if (e.getSource() == backButton) {

            VoterDashboard voterDashboard = new VoterDashboard(
                voterName,
                voterID
            );

            voterDashboard.setVisible(true);
            this.dispose();
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

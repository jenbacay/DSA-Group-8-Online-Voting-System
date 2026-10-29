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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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

    private JLabel titleLabel, voterIDLabel, selectLabel;
    private JPanel mainPanel, headerPanel, candidatePanel;
    private JScrollPane scrollPane;
    private JButton submitButton, backButton;
    private Map<String, ButtonGroup> positionGroups;
    private Map<String, List<JCheckBox>> kagawadCheckBoxes;
    private Color darkBlue = new Color(15, 61, 120);
    private Color blue = new Color(18, 130, 220);
    private Color lightBlue = new Color(225, 242, 255);
    private Color borderColor = new Color(220, 225, 230);
    private String voterName;
    private String voterID;
    private String[] positions = {
        "SK Chairperson",
        "SK Kagawad",
        "SK Treasurer",
        "SK Secretary"
    };

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
        headerPanel.setLayout(new BoxLayout(
                headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("Welcome, " + voterName + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);

        voterIDLabel = new JLabel("Voter ID: " + voterID);
        voterIDLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        voterIDLabel.setForeground(new Color(50, 90, 140));

        JLabel instructionLabel = new JLabel(
                "Select candidates for each position.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(voterIDLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(instructionLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        candidatePanel = new JPanel();
        candidatePanel.setLayout(new BoxLayout(
                candidatePanel, BoxLayout.Y_AXIS));
        candidatePanel.setBackground(Color.WHITE);
        candidatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        positionGroups = new LinkedHashMap<>();
        kagawadCheckBoxes = new LinkedHashMap<>();

        scrollPane = new JScrollPane(candidatePanel);
        scrollPane.setBorder(
                new LineBorder(borderColor, 1));
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(
                java.awt.FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        submitButton = new JButton("Submit Vote");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(blue);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
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
        kagawadCheckBoxes.clear();

        Map<String, List<String>> candidateList =
                new LinkedHashMap<>();

        for (String position : positions) {
            candidateList.put(position, new ArrayList<>());
        }

        String sql = "SELECT candidate_id, name, party, position "
                   + "FROM candidates ORDER BY candidate_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String candidateID =
                            rs.getString("candidate_id");
                    String name = rs.getString("name");
                    String party = rs.getString("party");
                    String position = rs.getString("position");

                    if (candidateList.containsKey(position)) {

                        String candidateInfo =
                                candidateID + " - " + name
                                + " (" + party + ")";

                        candidateList.get(position).add(
                                candidateInfo);
                    }
                }
            }

            for (String position : positions) {

                JLabel positionLabel = new JLabel(position);
                positionLabel.setFont(
                        new Font("Arial", Font.BOLD, 16));
                positionLabel.setForeground(darkBlue);
                positionLabel.setAlignmentX(
                        Component.LEFT_ALIGNMENT);

                candidatePanel.add(positionLabel);
                candidatePanel.add(
                        Box.createVerticalStrut(8));

                JPanel positionPanel = new JPanel();
                positionPanel.setLayout(new BoxLayout(
                        positionPanel, BoxLayout.Y_AXIS));
                positionPanel.setBackground(Color.WHITE);
                positionPanel.setBorder(
                        BorderFactory.createCompoundBorder(
                                new LineBorder(borderColor, 1),
                                new EmptyBorder(10, 12, 10, 12)));
                positionPanel.setAlignmentX(
                        Component.LEFT_ALIGNMENT);

                positionPanel.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 300));

                List<String> candidates =
                        candidateList.get(position);

                if (candidates.isEmpty()) {

                    JLabel noCandidates = new JLabel(
                            "No candidates available.");
                    noCandidates.setFont(
                            new Font("Arial", Font.PLAIN, 13));
                    positionPanel.add(noCandidates);

                } else if (position.equals("SK Kagawad")) {

                    List<JCheckBox> checkBoxes =
                            new ArrayList<>();

                    kagawadCheckBoxes.put(
                            position, checkBoxes);

                    for (String candidate : candidates) {

                        JCheckBox checkBox =
                                new JCheckBox(candidate);

                        styleCandidate(checkBox);

                        checkBox.addActionListener(e -> {

                            int selectedCount = 0;

                            for (JCheckBox cb : checkBoxes) {
                                if (cb.isSelected()) {
                                    selectedCount++;
                                }
                            }

                            if (selectedCount > 7) {
                                checkBox.setSelected(false);

                                JOptionPane.showMessageDialog(
                                        this,
                                        "You can select up to "
                                        + "7 SK Kagawad candidates.");
                            }
                        });

                        checkBoxes.add(checkBox);
                        positionPanel.add(checkBox);
                    }

                } else {

                    ButtonGroup group = new ButtonGroup();
                    positionGroups.put(position, group);

                    for (String candidate : candidates) {

                        JRadioButton radioButton =
                                new JRadioButton(candidate);

                        styleCandidate(radioButton);

                        group.add(radioButton);
                        positionPanel.add(radioButton);
                    }
                }

                candidatePanel.add(positionPanel);
                candidatePanel.add(
                        Box.createVerticalStrut(15));
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading candidates: "
                    + ex.getMessage());
        }

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
        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private boolean validateSelections() {

        for (String position : positions) {

            if (position.equals("SK Kagawad")) {
                continue;
            }

            ButtonGroup group = positionGroups.get(position);

            if (group == null || group.getSelection() == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a candidate for "
                        + position + ".",
                        "Incomplete Vote",
                        JOptionPane.WARNING_MESSAGE);

                return false;
            }
        }

        List<JCheckBox> checkBoxes =
                kagawadCheckBoxes.get("SK Kagawad");

        int selectedCount = 0;

        if (checkBoxes != null) {
            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    selectedCount++;
                }
            }
        }

        if (selectedCount > 7) {

            JOptionPane.showMessageDialog(
                    this,
                    "You can select up to 7 SK Kagawad candidates.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
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
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                    List<String> selectedCandidates = new java.util.ArrayList<>();
                    selectedCandidates.add(
                        "SK Chairperson: C001 - Maria Santos (Party List 1)");
                    
                    VotingConfirmation confirmation = new VotingConfirmation(
                      voterName,
                      voterID,
                      selectedCandidates);

                    confirmation.setVisible(true);
                    this.dispose();

            }

        } else if (e.getSource() == backButton) {

            VoterDashboard voterDashboard = new VoterDashboard();
            voterDashboard.setVisible(true);
            this.dispose();
        }
    }
}
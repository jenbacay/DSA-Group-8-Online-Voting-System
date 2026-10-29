/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author annem
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VotingConfirmation extends JFrame
        implements ActionListener {

    private JLabel titleLabel, subtitleLabel;
    private JLabel successLabel, messageLabel;
    private JLabel nameLabel, idLabel, dateLabel;
    private JLabel nameValue, idValue;
    private JLabel candidatesLabel;

    private JPanel mainPanel, headerPanel;
    private JPanel checkPanel, voterPanel, circle;

    private JTextArea candidatesArea;
    private JScrollPane candidateScrollPane;

    private JButton confirmButton;

    private Color green = new Color(24, 165, 100);
    private Color blue = new Color(18, 130, 220);
    private Color darkBlue = new Color(15, 61, 120);
    private Color lightBlue = new Color(225, 242, 255);
    private Color borderColor = new Color(190, 215, 240);

    public VotingConfirmation(
            String voterName,
            String voterID,
            List<String> selectedCandidates) {

        setTitle("Online Voting System - Vote Confirmation");
        setSize(670, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(lightBlue);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(borderColor, 1));
        mainPanel.setBounds(10, 10, 634, 590);
        add(mainPanel);

        // Header
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

        // Green check mark
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
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(green);
                g2.fillOval(10, 5, 95, 95);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(
                        7,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                int[] x = {35, 52, 82};
                int[] y = {53, 70, 36};

                g2.drawPolyline(x, y, 3);
                g2.dispose();
            }
        };

        circle.setOpaque(false);
        circle.setBounds(0, 0, 120, 105);
        checkPanel.add(circle);

        // Success message
        successLabel = new JLabel("Selections Submitted!");
        successLabel.setFont(new Font("Arial", Font.BOLD, 23));
        successLabel.setForeground(green);
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        successLabel.setBounds(30, 175, 574, 30);
        mainPanel.add(successLabel);

        messageLabel = new JLabel(
                "Please review your selected candidates below.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(30, 205, 574, 25);
        mainPanel.add(messageLabel);

        // Voter information
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

        // Selected candidates label
        candidatesLabel = new JLabel("YOUR SELECTED CANDIDATES");
        candidatesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        candidatesLabel.setForeground(darkBlue);
        candidatesLabel.setBounds(30, 320, 574, 25);
        mainPanel.add(candidatesLabel);

        // Selected candidates text area
        candidatesArea = new JTextArea();
        candidatesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        candidatesArea.setForeground(darkBlue);
        candidatesArea.setBackground(new Color(242, 249, 253));
        candidatesArea.setEditable(false);
        candidatesArea.setLineWrap(true);
        candidatesArea.setWrapStyleWord(true);
        candidatesArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        if (selectedCandidates != null
                && !selectedCandidates.isEmpty()) {

            for (String candidate : selectedCandidates) {
                candidatesArea.append(candidate + "\n\n");
            }

        } else {
            candidatesArea.setText(
                    "No candidates were provided.");
        }

        candidateScrollPane = new JScrollPane(candidatesArea);
        candidateScrollPane.setBorder(
                new LineBorder(borderColor, 1));
        candidateScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        candidateScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        candidateScrollPane.setBounds(30, 350, 574, 130);
        mainPanel.add(candidateScrollPane);

        // Date and time
        String dateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        dateLabel = new JLabel("Date & Time: " + dateTime);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBounds(30, 490, 574, 20);
        mainPanel.add(dateLabel);

        // Confirm button
        confirmButton = new JButton("CONFIRM");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 15));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(blue);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setBounds(205, 525, 220, 42);
        mainPanel.add(confirmButton);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == confirmButton) {

            VoterDashboard voterDashboard =
                    new VoterDashboard();

            voterDashboard.setVisible(true);
            this.dispose();
        }
    }

    // Test the confirmation window
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                List<String> sampleCandidates =
                        new java.util.ArrayList<>();

                sampleCandidates.add(
                        "SK Chairperson: C001 - Maria Santos (Party List 1)");

                sampleCandidates.add(
                        "SK Kagawad: C002 - Juan Dela Cruz (Party List 2)");

                sampleCandidates.add(
                        "SK Treasurer: C003 - Ana Garcia (Party List 1)");

                sampleCandidates.add(
                        "SK Secretary: C004 - Pedro Reyes (Party List 2)");

                VotingConfirmation confirmation =
                        new VotingConfirmation(
                                "Sample Voter",
                                "V001",
                                sampleCandidates);

                confirmation.setVisible(true);
            }
        });
    }
}

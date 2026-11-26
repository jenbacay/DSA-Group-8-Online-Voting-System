package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class VoterDashboard extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, welcomeLabel, voterIdLabel;
    private JButton candidatesButton, voteButton, resultsButton, logoutButton;

    private String voterName;
    private String voterID;

    private final Color darkBlue = new Color(20, 55, 85);
    private final Color lightBlue = new Color(230, 242, 255);

    public VoterDashboard() {
        this("Voter", "Not provided");
    }

    public VoterDashboard(String voterName, String voterID) {
        this.voterName = voterName == null || voterName.trim().isEmpty()
                ? "Voter" : voterName.trim();

        this.voterID = voterID == null || voterID.trim().isEmpty()
                ? "ID not received" : voterID.trim();

        setTitle("Voter Dashboard");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(lightBlue);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(35, 25, 415, 515);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        add(mainPanel);

        titleLabel = new JLabel("VOTER DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(35, 25, 345, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("ONLINE VOTING SYSTEM");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(35, 60, 345, 20);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(subtitleLabel);

        welcomeLabel = new JLabel("Welcome, " + this.voterName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(darkBlue);
        welcomeLabel.setBounds(25, 105, 365, 30);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel);

        voterIdLabel = new JLabel("VOTER ID: " + this.voterID);
        voterIdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        voterIdLabel.setForeground(new Color(35, 125, 225));
        voterIdLabel.setBounds(25, 140, 365, 25);
        voterIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(voterIdLabel);

        candidatesButton = createButton("♙   LIST OF CANDIDATES",
                65, 195, new Color(35, 125, 225));

        voteButton = createButton("✓   CAST VOTE",
                65, 265, new Color(20, 170, 105));

        resultsButton = createButton("▤   ELECTION RESULTS",
                65, 335, new Color(115, 75, 210));

        logoutButton = createButton("↪   LOG OUT",
                65, 425, new Color(220, 70, 70));
    }

    private JButton createButton(String text, int x, int y, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 285, 50);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(this);
        addButtonToPanel(button);
        return button;
    }

    private void addButtonToPanel(JButton button) {
        getContentPane().getComponent(0);
        ((JPanel) getContentPane().getComponent(0)).add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == candidatesButton) {

            VoterCandidatePanel panel =
                    new VoterCandidatePanel(voterName, voterID);
            panel.setVisible(true);
            dispose();

        } else if (e.getSource() == voteButton) {

            CastVotePanel panel =
                    new CastVotePanel(voterName, voterID);
            panel.setVisible(true);
            dispose();

        } else if (e.getSource() == resultsButton) {

            VoterElectionResultsPanel panel =
                    new VoterElectionResultsPanel(voterName, voterID);
            panel.setVisible(true);
            dispose();

        } else if (e.getSource() == logoutButton) {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                LoginPanel login = new LoginPanel();
                login.setVisible(true);
                dispose();
            }
        }
    }
}


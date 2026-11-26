package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class VoterDashboard extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, welcomeLabel;
    private JButton candidatesButton, voteButton, resultsButton, logoutButton;

    private String voterName;
    private String voterID;

    public VoterDashboard() {
        this("Voter", "Not provided");
    }

    public VoterDashboard(String voterName, String voterID) {
        this.voterName = voterName;
        this.voterID = voterID;

        setTitle("Voter Dashboard");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 242, 255));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(35, 25, 415, 515);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        add(mainPanel);

        titleLabel = new JLabel("VOTER DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(20, 55, 85));
        titleLabel.setBounds(45, 30, 330, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("ONLINE VOTING SYSTEM");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(45, 65, 330, 20);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(subtitleLabel);

        welcomeLabel = new JLabel(
            "<html><center>Welcome, " + this.voterName
            + "!<br>Voter ID: " + this.voterID + "</center></html>"
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        welcomeLabel.setForeground(new Color(35, 85, 130));
        welcomeLabel.setBounds(35, 110, 345, 50);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel);

        candidatesButton = new JButton("LIST OF CANDIDATES");
        candidatesButton.setBounds(65, 190, 285, 50);
        candidatesButton.setFont(new Font("Arial", Font.BOLD, 14));
        candidatesButton.setBackground(new Color(35, 125, 225));
        candidatesButton.setForeground(Color.WHITE);
        candidatesButton.setFocusPainted(false);
        candidatesButton.addActionListener(this);
        mainPanel.add(candidatesButton);

        voteButton = new JButton("CAST VOTE");
        voteButton.setBounds(65, 260, 285, 50);
        voteButton.setFont(new Font("Arial", Font.BOLD, 14));
        voteButton.setBackground(new Color(20, 170, 105));
        voteButton.setForeground(Color.WHITE);
        voteButton.setFocusPainted(false);
        voteButton.addActionListener(this);
        mainPanel.add(voteButton);

        resultsButton = new JButton("ELECTION RESULTS");
        resultsButton.setBounds(65, 330, 285, 50);
        resultsButton.setFont(new Font("Arial", Font.BOLD, 14));
        resultsButton.setBackground(new Color(115, 75, 210));
        resultsButton.setForeground(Color.WHITE);
        resultsButton.setFocusPainted(false);
        resultsButton.addActionListener(this);
        mainPanel.add(resultsButton);

        logoutButton = new JButton("LOG OUT");
        logoutButton.setBounds(65, 420, 285, 40);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setBackground(new Color(220, 70, 70));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        mainPanel.add(logoutButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == candidatesButton) {
            VoterCandidatePanel panel = new VoterCandidatePanel(
                voterName,
                voterID
            );
            panel.setVisible(true);
            this.dispose();

        } else if (e.getSource() == voteButton) {
            CastVotePanel panel = new CastVotePanel(
                voterName,
                voterID
            );
            panel.setVisible(true);
            this.dispose();

        } else if (e.getSource() == resultsButton) {
            JFrame frame = new JFrame("Election Results");
            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);

            ResultAnalyticsPanels resultPanel = new ResultAnalyticsPanels();
            resultPanel.setBounds(0, 0, 900, 600);
            frame.add(resultPanel);

            frame.setVisible(true);
            this.dispose();

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
                this.dispose();
            }
        }
    }
}


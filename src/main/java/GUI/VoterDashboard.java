package GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class VoterDashboard extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, welcomeLabel, voterIdLabel;
    private JButton candidatesButton, voteButton, resultsButton, logoutButton;
    private JPanel mainPanel;

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

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(35, 25, 415, 515);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        add(mainPanel);

        titleLabel = new JLabel("ONLINE VOTING SYSTEM");
        titleLabel.setBounds(35, 25, 345, 35);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("VOTER DASHBOARD");
        subtitleLabel.setBounds(35, 60, 345, 20);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(subtitleLabel);

        welcomeLabel = new JLabel("Welcome, " + this.voterName + "!");
        welcomeLabel.setBounds(25, 105, 365, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 19));
        welcomeLabel.setForeground(darkBlue);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel);

        voterIdLabel = new JLabel("VOTER ID: " + this.voterID);
        voterIdLabel.setBounds(25, 140, 365, 25);
        voterIdLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        voterIdLabel.setForeground(Color.GRAY);
        voterIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(voterIdLabel);

        candidatesButton = createButton(
                "LIST OF CANDIDATES",
                65, 195,
                new Color(35, 125, 225),
                "candidates"
        );

        voteButton = createButton(
                "CAST VOTE",
                65, 265,
                new Color(20, 170, 105),
                "vote"
        );

        resultsButton = createButton(
                "ELECTION RESULTS",
                65, 335,
                new Color(115, 75, 210),
                "results"
        );

        logoutButton = createButton(
                "LOG OUT",
                65, 425,
                new Color(220, 70, 70),
                "logout"
        );
    }

    private JButton createButton(String text, int x, int y,
            Color color, String iconType) {

        JButton button = new JButton(text);

        button.setBounds(x, y, 285, 50);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setIcon(new DashboardIcon(iconType));
        button.setIconTextGap(18);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(5, 20, 5, 10));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        mainPanel.add(button);

        return button;
    }

    private class DashboardIcon implements Icon {

        private String type;

        public DashboardIcon(String type) {
            this.type = type;
        }

        @Override
        public int getIconWidth() {
            return 25;
        }

        @Override
        public int getIconHeight() {
            return 25;
        }

        @Override
        public void paintIcon(java.awt.Component component,
                Graphics graphics, int x, int y) {

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(
                    2.2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            switch (type) {

                case "candidates":
                    g2.drawOval(x + 8, y + 2, 9, 9);
                    g2.drawArc(x + 4, y + 12, 17, 12, 0, 180);
                    g2.drawOval(x + 1, y + 6, 6, 6);
                    g2.drawArc(x - 1, y + 14, 10, 9, 0, 180);
                    break;

                case "vote":
                    int[] xPoints = {
                        x + 12, x + 22, x + 12, x + 2
                    };

                    int[] yPoints = {
                        y + 2, y + 12, y + 22, y + 12
                    };

                    g2.drawPolygon(xPoints, yPoints, 4);
                    g2.drawLine(x + 7, y + 12, x + 11, y + 16);
                    g2.drawLine(x + 11, y + 16, x + 18, y + 8);
                    break;

                case "results":
                    g2.drawLine(x + 3, y + 22, x + 23, y + 22);
                    g2.drawLine(x + 5, y + 22, x + 5, y + 14);
                    g2.drawLine(x + 11, y + 22, x + 11, y + 9);
                    g2.drawLine(x + 17, y + 22, x + 17, y + 4);
                    g2.drawLine(x + 23, y + 22, x + 23, y + 11);
                    break;

                case "logout":
                    g2.drawRoundRect(x + 2, y + 3, 13, 19, 2, 2);
                    g2.drawLine(x + 11, y + 12, x + 23, y + 12);
                    g2.drawLine(x + 18, y + 7, x + 23, y + 12);
                    g2.drawLine(x + 18, y + 17, x + 23, y + 12);
                    break;
            }

            g2.dispose();
        }
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

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                LoginPanel login = new LoginPanel();
                login.setVisible(true);
                dispose();
            }
        }
    }
}



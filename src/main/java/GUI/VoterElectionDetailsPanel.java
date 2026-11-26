
package GUI;

import Database.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class VoterElectionDetailsPanel extends JPanel implements ActionListener {

    private String voterName;
    private String voterID;
    private String electionName;
    private String description;
    private String electionStatus;

    private int electionID;

    private Runnable backAction;

    private JButton backButton, candidatesButton, voteButton, resultsButton;

    private JLabel statusLabel;

    private JPanel contentPanel;

    public VoterElectionDetailsPanel(
            String voterName,
            String voterID,
            int electionID,
            String electionName,
            String description,
            String electionStatus,
            Runnable backAction) {

        this.voterName = voterName;
        this.voterID = voterID;
        this.electionID = electionID;
        this.electionName = electionName;
        this.description = description;
        this.electionStatus = electionStatus;
        this.backAction = backAction;

        setLayout(null);
        setBackground(new Color(240, 246, 252));

        createComponents();
        checkVotingStatus();
    }

    private void createComponents() {

        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(new Color(20, 55, 85));
        headerPanel.setBounds(0, 0, 900, 100);

        backButton = new JButton("Back");
        backButton.setBounds(25, 30, 100, 35);
        backButton.addActionListener(this);

        JLabel titleLabel = new JLabel("ELECTION DETAILS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(150, 25, 450, 40);

        headerPanel.add(backButton);
        headerPanel.add(titleLabel);

        contentPanel = new JPanel(null);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new LineBorder(
                new Color(210, 220, 230), 1, true
        ));
        contentPanel.setBounds(45, 130, 790, 450);

        JLabel electionLabel = new JLabel(electionName);
        electionLabel.setFont(new Font("Arial", Font.BOLD, 25));
        electionLabel.setForeground(new Color(20, 55, 85));
        electionLabel.setBounds(35, 30, 700, 40);

        JLabel descriptionLabel = new JLabel(
                "<html>" + (description == null ? "" : description)
                + "</html>"
        );
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionLabel.setForeground(Color.GRAY);
        descriptionLabel.setBounds(35, 75, 700, 45);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBounds(35, 135, 300, 30);

        JLabel sectionLabel = new JLabel("AVAILABLE SECTIONS");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 17));
        sectionLabel.setForeground(new Color(20, 55, 85));
        sectionLabel.setBounds(35, 190, 400, 30);

        candidatesButton = createButton(
                "List of Candidates", 35, 240
        );

        voteButton = createButton(
                "Cast Vote", 35, 305
        );

        resultsButton = createButton(
                "Election Results", 35, 370
        );

        contentPanel.add(electionLabel);
        contentPanel.add(descriptionLabel);
        contentPanel.add(statusLabel);
        contentPanel.add(sectionLabel);
        contentPanel.add(candidatesButton);
        contentPanel.add(voteButton);
        contentPanel.add(resultsButton);

        add(headerPanel);
        add(contentPanel);
    }

    private JButton createButton(String text, int x, int y) {

        JButton button = new JButton(text);

        button.setBounds(x, y, 720, 45);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(35, 125, 225));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(this);

        return button;
    }

    private void checkVotingStatus() {

        boolean alreadyVoted = false;

        String sql = "SELECT id FROM voter_election_status "
                   + "WHERE voter_id = ? AND election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, voterID);
            pst.setInt(2, electionID);

            try (ResultSet rs = pst.executeQuery()) {
                alreadyVoted = rs.next();
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to check voting status: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            voteButton.setVisible(false);
            return;
        }

        boolean isOpen = checkCurrentElectionStatus();

        if (!isOpen) {
            statusLabel.setText("ELECTION CLOSED OR NOT YET OPEN");
            statusLabel.setForeground(new Color(200, 65, 65));
            voteButton.setVisible(false);

        } else if (alreadyVoted) {
            statusLabel.setText("YOU HAVE ALREADY VOTED");
            statusLabel.setForeground(new Color(20, 160, 90));
            voteButton.setVisible(false);

        } else {
            statusLabel.setText("OPEN FOR VOTING");
            statusLabel.setForeground(new Color(20, 160, 90));
            voteButton.setVisible(true);
        }
    }

    private boolean checkCurrentElectionStatus() {

        String sql = "SELECT status, start_datetime, end_datetime "
                   + "FROM elections WHERE election_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, electionID);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String status = rs.getString("status");

                    Timestamp start = rs.getTimestamp("start_datetime");
                    Timestamp end = rs.getTimestamp("end_datetime");

                    Timestamp now = new Timestamp(
                            System.currentTimeMillis()
                    );

                    return "ONGOING".equals(status)
                            && !now.before(start)
                            && now.before(end);
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to verify election: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == backButton) {

            backAction.run();

        } else if (e.getSource() == candidatesButton) {

            JOptionPane.showMessageDialog(
                    this,
                    "Candidate panel will be connected next."
            );

        } else if (e.getSource() == voteButton) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cast Vote panel will be connected next."
            );

        } else if (e.getSource() == resultsButton) {

            JOptionPane.showMessageDialog(
                    this,
                    "Election Results panel will be connected next."
            );
        }
    }
}



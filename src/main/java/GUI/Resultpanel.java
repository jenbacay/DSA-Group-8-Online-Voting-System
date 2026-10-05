/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author ronne
 */
import javax.swing.*;
import java.awt.*;

public class Resultpanel extends JFrame {

    private int totalVotes = 0;
    private int candidateA = 0;
    private int candidateB = 0;
    private int candidateC = 0;

   Resultpanel() {

        setTitle("Online Voting System - Voting Summary");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        //TITLE
        JLabel title = new JLabel("Voting Summary", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel(
                "Current voting results",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(title);
        headerPanel.add(subtitle);

        //TOTAL VOTES
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BorderLayout());
        totalPanel.setBorder(
                BorderFactory.createTitledBorder("Total Votes")
        );

        JLabel totalVotesLabel = new JLabel(
                String.valueOf(totalVotes),
                SwingConstants.CENTER
        );

        totalVotesLabel.setFont(
                new Font("Arial", Font.BOLD, 40)
        );

        totalPanel.add(totalVotesLabel, BorderLayout.CENTER);

        // CANDIDATES
        JPanel candidatePanel = new JPanel();
        candidatePanel.setLayout(new GridLayout(3, 1, 10, 10));
        candidatePanel.setBorder(
                BorderFactory.createTitledBorder("Candidate Vote Count")
        );

        candidatePanel.add(
                createCandidateRow("Candidate A", candidateA)
        );

        candidatePanel.add(
                createCandidateRow("Candidate B", candidateB)
        );

        candidatePanel.add(
                createCandidateRow("Candidate C", candidateC)
        );

        //REFRESH BUTTON
        JButton refreshButton = new JButton("Refresh Results");

        refreshButton.addActionListener(e -> {
            // Later, this button will retrieve everything in the database hehe
            JOptionPane.showMessageDialog(
                    this,
                    "No database connected yet.\nShowing default values of 0.",
                    "Voting Results",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);

        //ADD EVERYTHING
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(totalPanel, BorderLayout.CENTER);
        mainPanel.add(candidatePanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    //Creates one candidate row
    private JPanel createCandidateRow(String candidateName, int votes) {

        JPanel panel = new JPanel(new BorderLayout());

        JLabel nameLabel = new JLabel(candidateName);
        nameLabel.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        JLabel votesLabel = new JLabel(
                votes + " votes",
                SwingConstants.RIGHT
        );

        votesLabel.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(votesLabel, BorderLayout.EAST);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Resultpanel ();
        });
    }
}
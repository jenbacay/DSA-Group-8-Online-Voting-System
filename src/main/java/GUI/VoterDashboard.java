/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author annem
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class VoterDashboard extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, welcomeLabel;
    private JButton candidatesButton, voteButton;
    private JButton resultsButton, logoutButton;

    public VoterDashboard() {

        setTitle("Online Voting System - Voter Dashboard");
        setSize(500, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(230, 242, 255));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                new LineBorder(new Color(190, 215, 240), 1));
        mainPanel.setBounds(35, 25, 415, 515);
        add(mainPanel);

        titleLabel = new JLabel("ONLINE VOTING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(new Color(25, 75, 125));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(20, 25, 375, 40);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("VOTER DASHBOARD");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(20, 65, 375, 25);
        mainPanel.add(subtitleLabel);

        welcomeLabel = new JLabel("Welcome, Voter!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        welcomeLabel.setForeground(new Color(25, 75, 125));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBounds(20, 105, 375, 30);
        mainPanel.add(welcomeLabel);

        candidatesButton = new JButton("LIST OF CANDIDATES");
        candidatesButton.setFont(new Font("Arial", Font.BOLD, 12));
        candidatesButton.setForeground(Color.WHITE);
        candidatesButton.setBackground(new Color(51, 122, 183));
        candidatesButton.setFocusPainted(false);
        candidatesButton.setBounds(30, 160, 165, 75);
        mainPanel.add(candidatesButton);

        voteButton = new JButton("CAST VOTE");
        voteButton.setFont(new Font("Arial", Font.BOLD, 12));
        voteButton.setForeground(Color.WHITE);
        voteButton.setBackground(new Color(51, 122, 183));
        voteButton.setFocusPainted(false);
        voteButton.setBounds(215, 160, 165, 75);
        mainPanel.add(voteButton);

        resultsButton = new JButton("ELECTION RESULTS");
        resultsButton.setFont(new Font("Arial", Font.BOLD, 12));
        resultsButton.setForeground(Color.WHITE);
        resultsButton.setBackground(new Color(51, 122, 183));
        resultsButton.setFocusPainted(false);
        resultsButton.setBounds(105, 265, 200, 75);
        mainPanel.add(resultsButton);

        logoutButton = new JButton("LOG OUT");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(new Color(25, 75, 125));
        logoutButton.setBackground(new Color(207, 229, 252));
        logoutButton.setFocusPainted(false);
        logoutButton.setBounds(100, 375, 215, 42);
        mainPanel.add(logoutButton);

        candidatesButton.addActionListener(this);
        voteButton.addActionListener(this);
        resultsButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == candidatesButton) {

            JOptionPane.showMessageDialog(
                    this,
                    "List of Candidates selected.");

        } else if (e.getSource() == voteButton) {

           CastVotePanel castvote = new CastVotePanel();
           castvote.setVisible(true);
           this.dispose();

        } else if (e.getSource() == resultsButton) {

            JOptionPane.showMessageDialog(
                    this,
                    "Election Results selected.");

        } else if (e.getSource() == logoutButton) {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {

                LoginPanel loginPanel = new LoginPanel();
                loginPanel.setVisible(true);
                this.dispose();
            }
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author JenJen
 */

public class LoginPanel extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel, loginAsLabel;
    private JLabel usernameLabel, passwordLabel, footerLabel;

    private JRadioButton voterRadioButton, adminRadioButton;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JButton loginButton, signUpButton;
    private ButtonGroup accountTypeGroup;

    public LoginPanel() {

        setTitle("Online Voting System");
        setSize(500, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(230, 242, 255));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        mainPanel.setBounds(35, 25, 415, 515);
        add(mainPanel);

        titleLabel = new JLabel("ONLINE VOTING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(new Color(25, 75, 125));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(20, 25, 375, 40);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("Welcome! Please log in to continue.");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(20, 65, 375, 25);
        mainPanel.add(subtitleLabel);

        loginAsLabel = new JLabel("LOGIN AS");
        loginAsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginAsLabel.setBounds(40, 115, 200, 25);
        mainPanel.add(loginAsLabel);

        voterRadioButton = new JRadioButton("VOTER");
        voterRadioButton.setBounds(40, 145, 120, 30);
        voterRadioButton.setBackground(Color.WHITE);
        mainPanel.add(voterRadioButton);

        adminRadioButton = new JRadioButton("ADMIN");
        adminRadioButton.setBounds(200, 145, 120, 30);
        adminRadioButton.setBackground(Color.WHITE);
        mainPanel.add(adminRadioButton);

        accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(voterRadioButton);
        accountTypeGroup.add(adminRadioButton);
        voterRadioButton.setSelected(true);

        usernameLabel = new JLabel("Username / Voter ID");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        usernameLabel.setBounds(40, 195, 250, 25);
        mainPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBackground(new Color(248, 251, 255));
        usernameField.setBorder(new LineBorder(new Color(180, 205, 230), 1));
        usernameField.setBounds(40, 225, 335, 38);
        mainPanel.add(usernameField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordLabel.setBounds(40, 280, 250, 25);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(248, 251, 255));
        passwordField.setBorder(new LineBorder(new Color(180, 205, 230), 1));
        passwordField.setBounds(40, 310, 335, 38);
        mainPanel.add(passwordField);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(51, 122, 183));
        loginButton.setFocusPainted(false);
        loginButton.setBounds(40, 375, 155, 42);
        mainPanel.add(loginButton);

        signUpButton = new JButton("SIGN UP");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setForeground(new Color(25, 75, 125));
        signUpButton.setBackground(new Color(207, 229, 252));
        signUpButton.setFocusPainted(false);
        signUpButton.setBounds(220, 375, 155, 42);
        mainPanel.add(signUpButton);

        footerLabel = new JLabel("ONLINE VOTING SYSTEM | 2026");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setBounds(20, 445, 375, 25);
        mainPanel.add(footerLabel);

        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {

            String username = usernameField.getText();
            String password = new String(
                    passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter your username and password.");
            } else if (!voterRadioButton.isSelected()
                    && !adminRadioButton.isSelected()) {
                JOptionPane.showMessageDialog(
                        this, "Please select an account type.");
            } else {
                JOptionPane.showMessageDialog(
                        this, "Login button clicked. Database verification is not connected yet.");
            }

        } else if (e.getSource() == signUpButton) {

            VoterRegistrationPanel registration = new VoterRegistrationPanel();
            registration.setVisible(true);
            this.dispose();
        }
    }
}
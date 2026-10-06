/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.*;

/**
 *
 * @author JenJen
 */


public class LoginPanel extends JFrame implements ActionListener{

    private JLabel titleLabel, loginAsLabel, usernameLabel, passwordLabel;
    private JRadioButton voterRadioButton, adminRadioButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton,signUpButton;
    private ButtonGroup accountTypeGroup;
    private final String[] voterUsernames = {"voter1", "voter2"};
    private final String[] voterPasswords = {"pass123", "pass456"};
    private final String[] adminUsernames = {"admin1", "admin2"};
    private final String[] adminPasswords = {"admin123", "admin456"};

    public LoginPanel() {

        setTitle("Online Voting System");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("ONLINE VOTING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 40, 400, 45);
        mainPanel.add(titleLabel);
        
        loginAsLabel = new JLabel("LOGIN AS");
        loginAsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginAsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginAsLabel.setBounds(150, 105, 200, 30);
        mainPanel.add(loginAsLabel);

        voterRadioButton = new JRadioButton("VOTER");
        voterRadioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        voterRadioButton.setBackground(Color.WHITE);
        voterRadioButton.setBounds(150, 140, 90, 30);
        mainPanel.add(voterRadioButton);

        adminRadioButton = new JRadioButton("ADMIN");
        adminRadioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        adminRadioButton.setBackground(Color.WHITE);
        adminRadioButton.setBounds(260, 140, 90, 30);
        mainPanel.add(adminRadioButton);

        accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(voterRadioButton);
        accountTypeGroup.add(adminRadioButton);
        // Select Voter by default
        voterRadioButton.setSelected(true);

        usernameLabel = new JLabel("Username / Voter ID");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setBounds(80, 205, 250, 30);
        mainPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(80, 235, 340, 40);
        mainPanel.add(usernameField);

        passwordLabel = new JLabel("Password" );
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setBounds(80, 300, 250, 30);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(80, 330, 340, 40);
        mainPanel.add(passwordField);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBounds(80, 400, 160, 45);
        mainPanel.add(loginButton);

        signUpButton = new JButton("SIGN UP");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBounds(260, 400, 160, 45);
        mainPanel.add(signUpButton);

        loginButton.addActionListener(this); 
        signUpButton.addActionListener(this);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == loginButton) {
        String usn = usernameField.getText().trim();
        String pwd = new String(passwordField.getPassword()).trim();

        if (usn.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (voterRadioButton.isSelected()) {
            if (isValid(voterUsernames, voterPasswords, usn, pwd)) {
                JOptionPane.showMessageDialog(this, "Login Successful", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The username or password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        else if (adminRadioButton.isSelected()) {
            if (isValid(adminUsernames, adminPasswords, usn, pwd)) {
                JOptionPane.showMessageDialog(this, "Login Successful", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The username or password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    else if (e.getSource() == signUpButton) {
        JOptionPane.showMessageDialog(this, "Voter Sign Up", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
   }
    
   private boolean isValid(String[] usernames, String[] passwords, String usn, String pwd) {
    for (int i = 0; i < usernames.length; i++) {
        if (usernames[i].equals(usn) && passwords[i].equals(pwd)) {
            return true;
        }
    }
    return false;
  }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package GUI;

import javax.swing.*;

/**
 *
 * @author JenJen
 */
public class LoginPanel extends JFrame{
        
   private JLabel lblTitle, lblUsername, lblPassword;
   private JTextField txtUsername;
   private JPasswordField txtPassword;
   private JButton btnLogin;

    public LoginPanel() {

        setTitle("Online Voting System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        lblTitle = new JLabel("ONLINE VOTING SYSTEM");
        lblTitle.setBounds(270, 60, 300, 40);
        add(lblTitle);

        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(180, 150, 100, 30);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(280, 150, 300, 30);
        add(txtUsername);

        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(180, 200, 100, 30);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(280, 200, 300, 30);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(280, 260, 120, 40);
        add(btnLogin);
        
        }     
}



package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class VoterRegistrationPanel extends JFrame implements ActionListener {

    private JTextField fname, lname, user, email;
    private JPasswordField pass;

    private JLabel regis, name1, name2, user1, email1, pass1;

    private JButton sign, clear, backButton;
    private JPanel panel;

    VoterRegistrationPanel() {

        setTitle("Online Voting System");
        setSize(500, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(
                new Color(230, 242, 255));

        panel = new JPanel();
        panel.setBounds(20, 25, 445, 510);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setBorder(new LineBorder(
                new Color(190, 215, 240), 1));
        add(panel);

        regis = new JLabel("REGISTRATION FORM");
        regis.setBounds(0, 25, 445, 40);
        regis.setHorizontalAlignment(SwingConstants.CENTER);
        regis.setFont(new Font("Arial", Font.BOLD, 24));
        regis.setForeground(new Color(31, 58, 95));
        panel.add(regis);

        name1 = new JLabel("First Name:");
        name1.setBounds(35, 95, 100, 30);
        name1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(name1);

        fname = new JTextField();
        fname.setBounds(145, 95, 265, 32);
        styleField(fname);
        placeholder(fname, "First Name");
        panel.add(fname);

        name2 = new JLabel("Last Name:");
        name2.setBounds(35, 150, 100, 30);
        name2.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(name2);

        lname = new JTextField();
        lname.setBounds(145, 150, 265, 32);
        styleField(lname);
        placeholder(lname, "Last Name");
        panel.add(lname);

        user1 = new JLabel("Username:");
        user1.setBounds(35, 205, 100, 30);
        user1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(user1);

        user = new JTextField();
        user.setBounds(145, 205, 265, 32);
        styleField(user);
        placeholder(user, "Username");
        panel.add(user);

        email1 = new JLabel("Email:");
        email1.setBounds(35, 260, 100, 30);
        email1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(email1);

        email = new JTextField();
        email.setBounds(145, 260, 265, 32);
        styleField(email);
        placeholder(email, "Email");
        panel.add(email);

        pass1 = new JLabel("Password:");
        pass1.setBounds(35, 315, 100, 30);
        pass1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(pass1);

        pass = new JPasswordField();
        pass.setBounds(145, 315, 265, 32);
        styleField(pass);
        passwordPlaceholder();
        panel.add(pass);

        sign = new JButton("Sign Up");
        sign.setBounds(35, 385, 115, 40);
        sign.setFont(new Font("Arial", Font.BOLD, 14));
        sign.setBackground(new Color(52, 122, 183));
        sign.setForeground(Color.WHITE);
        sign.setFocusPainted(false);
        panel.add(sign);

        clear = new JButton("Clear");
        clear.setBounds(165, 385, 105, 40);
        clear.setFont(new Font("Arial", Font.BOLD, 14));
        clear.setBackground(new Color(225, 230, 237));
        clear.setForeground(new Color(40, 50, 65));
        clear.setFocusPainted(false);
        panel.add(clear);

        backButton = new JButton("Back to Login");
        backButton.setBounds(285, 385, 125, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBackground(new Color(207, 229, 252));
        backButton.setForeground(new Color(25, 75, 125));
        backButton.setFocusPainted(false);
        panel.add(backButton);

        sign.addActionListener(this);
        clear.addActionListener(this);
        backButton.addActionListener(this);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(new LineBorder(
                new Color(190, 200, 213), 1));
        field.setBackground(new Color(247, 250, 255));
    }

    private void placeholder(JTextField field, String text) {

        field.setText(text);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(text);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void passwordPlaceholder() {

        pass.setEchoChar((char) 0);
        pass.setText("Password");
        pass.setForeground(Color.GRAY);

        pass.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(
                        pass.getPassword()).equals("Password")) {
                    pass.setText("");
                    pass.setEchoChar('\u2022');
                    pass.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (pass.getPassword().length == 0) {
                    pass.setEchoChar((char) 0);
                    pass.setText("Password");
                    pass.setForeground(Color.GRAY);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == clear) {

            fname.setText("");
            lname.setText("");
            user.setText("");
            email.setText("");
            pass.setText("");

            fname.setForeground(Color.BLACK);
            lname.setForeground(Color.BLACK);
            user.setForeground(Color.BLACK);
            email.setForeground(Color.BLACK);

            pass.setEchoChar('\u2022');
            pass.setForeground(Color.BLACK);

            fname.requestFocus();

        } else if (e.getSource() == sign) {

            String firstName = fname.getText().trim();
            String lastName = lname.getText().trim();
            String username = user.getText().trim();
            String emailAddress = email.getText().trim();
            String password = new String(pass.getPassword());

            if (firstName.isEmpty()
                    || firstName.equals("First Name")
                    || lastName.isEmpty()
                    || lastName.equals("Last Name")
                    || username.isEmpty()
                    || username.equals("Username")
                    || emailAddress.isEmpty()
                    || emailAddress.equals("Email")
                    || password.isEmpty()
                    || password.equals("Password")) {

                JOptionPane.showMessageDialog(
                        this, "Please complete all fields.");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration button clicked. "
                        + "Database saving is not connected yet.");
            }

        } else if (e.getSource() == backButton) {

            LoginPanel login = new LoginPanel();
            login.setVisible(true);
            this.dispose();

        }
    }
}

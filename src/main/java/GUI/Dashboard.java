/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author JenJen
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Dashboard extends JFrame implements ActionListener {

    private JPanel sidebar, headerPanel, contentPanel;
    private JLabel adminLabel, welcomeLabel, subtitleLabel;
    private JButton btnDashboard, btnVoters, btnCandidates;
    private JButton btnElection, btnRecords, btnResults, btnLogout;

    private Color darkBlue = new Color(20, 55, 85);
    private Color blue = new Color(35, 125, 225);
    private Color green = new Color(20, 170, 105);
    private Color purple = new Color(115, 75, 210);
    private Color orange = new Color(245, 160, 20);
    private Color background = new Color(240, 246, 252);

    public Dashboard() {

        setTitle("Admin Dashboard");
        setSize(900, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(background);

        sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(darkBlue);
        sidebar.setBounds(0, 0, 190, 600);
        add(sidebar);

        JLabel profileIcon = new JLabel("●");
        profileIcon.setFont(new Font("Arial", Font.BOLD, 24));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setBounds(20, 15, 30, 30);
        sidebar.add(profileIcon);

        adminLabel = new JLabel("Admin");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 15));
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setBounds(55, 15, 110, 30);
        sidebar.add(adminLabel);

        btnDashboard = createButton("Dashboard", 65);
        btnVoters = createButton("Voters", 110);
        btnCandidates = createButton("Candidates", 155);
        btnElection = createButton("Election Management", 200);
        btnRecords = createButton("Vote Records", 245);
        btnResults = createButton("Results & Analytics", 290);
        btnLogout = createButton("Logout", 335);

        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(background);
        contentPanel.setBounds(190, 0, 710, 600);
        add(contentPanel);

        showDashboard();
    }

    private JButton createButton(String text, int y) {

        JButton button = new JButton(text);
        button.setBounds(8, y, 174, 38);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(darkBlue);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        sidebar.add(button);
        button.addActionListener(this);

        return button;
    }

    private void showDashboard() {

        contentPanel.removeAll();

        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBounds(0, 0, 710, 95);
        contentPanel.add(headerPanel);

        welcomeLabel = new JLabel("Welcome, Admin!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(darkBlue);
        welcomeLabel.setBounds(25, 18, 350, 30);
        headerPanel.add(welcomeLabel);

        subtitleLabel = new JLabel("Manage the election system from here.");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(25, 50, 400, 25);
        headerPanel.add(subtitleLabel);

        createCard("Total Voters", "12", "●", blue, 25, 120);

        createCard("Total Candidates", "3", "●", green, 385, 120);

        createCard("Total Votes Cast", "8", "✓", purple, 25, 305);

        createCard("Election Status", "Ongoing", "■", orange, 385, 305);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void createCard(String title, String value, String symbol, Color cardColor, int x, int y) {

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(cardColor);
        card.setBounds(x, y, 300, 155);
        card.setBorder(new LineBorder(cardColor, 1));
        contentPanel.add(card);

        JLabel iconLabel = new JLabel(symbol);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 25));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBounds(20, 12, 50, 35);
        card.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20, 65, 260, 25);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 23));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBounds(20, 95, 260, 35);
        card.add(valueLabel);
    }

    private void showSection(String title, String description) {

        contentPanel.removeAll();

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        heading.setForeground(darkBlue);
        heading.setBounds(30, 30, 600, 40);
        contentPanel.add(heading);

        JLabel message = new JLabel(description);
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        message.setForeground(Color.DARK_GRAY);
        message.setBounds(30, 80, 640, 35);
        contentPanel.add(message);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnDashboard) {

            showDashboard();

        } else if (e.getSource() == btnVoters) {

            showSection("Voters",
                    "Manage registered voters here.");

        } else if (e.getSource() == btnCandidates) {

            CandidatePanel candidates = new CandidatePanel();
            candidates.setVisible(true);
            this.dispose();

        } else if (e.getSource() == btnElection) {

            showSection("Election Management",
                    "Manage election settings and status here.");

        } else if (e.getSource() == btnRecords) {

            showSection("Vote Records",
                    "View recorded votes here.");

        } else if (e.getSource() == btnResults) {

            showSection("Results & Analytics",
                    "View election results and vote counts here.");

        } else if (e.getSource() == btnLogout) {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                LoginPanel login = new LoginPanel();
                login.setVisible(true);
                this.dispose();
            }
        }
    }

    public static void main(String[] args) {

        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
    }
}

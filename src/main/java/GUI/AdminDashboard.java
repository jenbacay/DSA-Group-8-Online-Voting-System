/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package GUI;

import Database.DBConnection;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class AdminDashboard extends JFrame implements ActionListener {

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

    public AdminDashboard() {

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

        JLabel profileIcon = new JLabel(new DashboardIcon("user", Color.WHITE, 25));
        profileIcon.setBounds(20, 15, 30, 30);
        sidebar.add(profileIcon);

        adminLabel = new JLabel("Admin");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 15));
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setBounds(55, 15, 110, 30);
        sidebar.add(adminLabel);

        btnDashboard = createButton("Dashboard", "home", 65);
        btnVoters = createButton("Voters", "users", 108);
        btnCandidates = createButton("Candidates", "user", 151);
        btnElection = createButton("Election Management", "calendar", 194);
        btnRecords = createButton("Vote Records", "clipboard", 237);
        btnResults = createButton("Results & Analytics", "chart", 280);
        btnLogout = createButton("Logout", "logout", 323);

        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(background);
        contentPanel.setBounds(190, 0, 710, 600);
        add(contentPanel);

        showDashboard();
    }

    private JButton createButton(String text, String iconName, int y) {

        JButton button = new JButton(
            text,
            new DashboardIcon(iconName, Color.WHITE, 19)
        );

        button.setBounds(8, y, 174, 38);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(darkBlue);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(13);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

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

        subtitleLabel = new JLabel(
            "Manage the election system from here."
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(25, 50, 400, 25);
        headerPanel.add(subtitleLabel);

        createCard(
            "Total Voters",
            getCount("SELECT COUNT(*) FROM voters"),
            "users",
            blue,
            25,
            120
        );

        createCard(
            "Total Candidates",
            getCount("SELECT COUNT(*) FROM candidates"),
            "user",
            green,
            385,
            120
        );

        createCard(
            "Total Votes Cast",
            getCount("SELECT COUNT(*) FROM votes"),
            "vote",
            purple,
            25,
            305
        );

        createCard(
            "Election Status",
            getElectionStatus(),
            "calendar",
            orange,
            385,
            305
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String getCount(String query) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println(
                "Dashboard count error: " + e.getMessage()
            );
        }

        return "0";
    }

    private String getElectionStatus() {

        String sql =
            "SELECT status FROM elections " +
            "ORDER BY election_id DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (Exception e) {
            System.out.println(
                "Election status error: " + e.getMessage()
            );
        }

        return "Not Set";
    }

    private void createCard(
        String title,
        String value,
        String iconName,
        Color cardColor,
        int x,
        int y
    ) {

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(cardColor);
        card.setBounds(x, y, 300, 155);
        card.setBorder(new LineBorder(cardColor, 1));
        contentPanel.add(card);

        JLabel iconLabel = new JLabel(
            new DashboardIcon(iconName, Color.WHITE, 32)
        );
        iconLabel.setBounds(20, 12, 45, 40);
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

    private void displayPanel(JPanel panel) {

        contentPanel.removeAll();

        panel.setBounds(0, 0, 710, 600);
        contentPanel.add(panel);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnDashboard) {

            showDashboard();

        } else if (e.getSource() == btnVoters) {

            displayPanel(new VoterManagementPanel());

        } else if (e.getSource() == btnCandidates) {

            displayPanel(new CandidatePanel());

        } else if (e.getSource() == btnElection) {

            displayPanel(new ElectionManagementPanel());

        } else if (e.getSource() == btnRecords) {

            displayPanel(new VoteRecordsPanel());

        } else if (e.getSource() == btnResults) {

            displayPanel(new ResultAnalyticsPanels());

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

    private static class DashboardIcon implements Icon {

        private String type;
        private Color color;
        private int size;

        public DashboardIcon(String type, Color color, int size) {
            this.type = type;
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(
            java.awt.Component component,
            Graphics g,
            int x,
            int y
        ) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                1.8f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
            ));

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = size;
            int h = size;

            switch (type) {

                case "user":
                    g2.drawOval(w / 3, 2, w / 3, h / 3);
                    g2.drawArc(w / 6, h / 2, 2 * w / 3,
                        h / 2, 0, 180);
                    break;

                case "users":
                    g2.drawOval(w / 4, 2, w / 4, w / 4);
                    g2.drawOval(w / 2, 5, w / 4, w / 4);
                    g2.drawArc(0, h / 2, w / 2, h / 2, 0, 180);
                    g2.drawArc(w / 3, h / 2, w / 2, h / 2, 0, 180);
                    break;

                case "home":
                    int[] hx = {2, w / 2, w - 2};
                    int[] hy = {h / 2, 2, h / 2};
                    g2.drawPolyline(hx, hy, 3);
                    g2.drawRect(w / 5, h / 2, 3 * w / 5, h / 2 - 2);
                    g2.drawRect(2 * w / 5, 2 * h / 3, w / 5, h / 3);
                    break;

                case "calendar":
                    g2.drawRoundRect(2, 5, w - 4, h - 7, 4, 4);
                    g2.drawLine(2, h / 3, w - 2, h / 3);
                    g2.drawLine(w / 4, 2, w / 4, h / 4);
                    g2.drawLine(3 * w / 4, 2, 3 * w / 4, h / 4);
                    g2.drawLine(w / 3, h / 2, w / 3, h / 2);
                    g2.drawLine(2 * w / 3, h / 2, 2 * w / 3, h / 2);
                    break;

                case "clipboard":
                    g2.drawRoundRect(w / 5, h / 6, 3 * w / 5, 4 * h / 5, 3, 3);
                    g2.drawRoundRect(w / 3, 1, w / 3, h / 4, 3, 3);
                    g2.drawLine(w / 3, h / 2, 2 * w / 3, h / 2);
                    g2.drawLine(w / 3, 2 * h / 3, 2 * w / 3, 2 * h / 3);
                    break;

                case "chart":
                    g2.drawLine(2, h - 2, 2, 2);
                    g2.drawLine(2, h - 2, w - 2, h - 2);
                    g2.drawRect(w / 5, h / 2, w / 6, h / 3);
                    g2.drawRect(w / 2, h / 3, w / 6, h / 2);
                    g2.drawRect(4 * w / 5 - 3, h / 5, w / 6, 4 * h / 5 - 2);
                    break;

                case "vote":
                    g2.drawRoundRect(2, 2, w - 4, h - 4, 4, 4);
                    g2.drawLine(w / 5, h / 2, 2 * w / 5, 2 * h / 3);
                    g2.drawLine(2 * w / 5, 2 * h / 3, 4 * w / 5, h / 3);
                    break;

                case "logout":
                    g2.drawRoundRect(w / 2, 2, w / 2 - 2, h - 4, 3, 3);
                    g2.drawLine(2, h / 2, 3 * w / 4, h / 2);
                    g2.drawLine(w / 2, h / 4, 3 * w / 4, h / 2);
                    g2.drawLine(w / 2, 3 * h / 4, 3 * w / 4, h / 2);
                    break;
            }

            g2.dispose();
        }
    }
}
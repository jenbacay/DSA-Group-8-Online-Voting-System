package GUI;

import Database.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.border.*;

public class VoterDashboard extends JFrame implements ActionListener {

    private String voterName;
    private String voterID;

    private JPanel mainPanel, headerPanel, contentPanel, electionPanel;
    private JButton logoutButton, refreshButton;

    private CardLayout cardLayout;

    private static final String DASHBOARD = "DASHBOARD";
    private static final String DETAILS = "DETAILS";

    public VoterDashboard(String voterName, String voterID) {

        this.voterName = voterName;
        this.voterID = voterID;

        setTitle("Voter Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();

        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(240, 246, 252));

        createDashboard();
        mainPanel.add(createDetailsContainer(), DETAILS);

        add(mainPanel);

        loadElections();

        setVisible(true);
    }

    private void createDashboard() {

        JPanel dashboardPanel = new JPanel(null);
        dashboardPanel.setBackground(new Color(240, 246, 252));

        headerPanel = new JPanel(null);
        headerPanel.setBackground(new Color(20, 55, 85));
        headerPanel.setBounds(0, 0, 900, 100);

        JLabel titleLabel = new JLabel("VOTER DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 15, 400, 35);

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + voterName + " | Voter ID: " + voterID
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(32, 55, 500, 25);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(770, 30, 100, 35);
        logoutButton.addActionListener(this);
        logoutButton.setBackground(new Color(220, 65, 65));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);

        headerPanel.add(titleLabel);
        headerPanel.add(welcomeLabel);
        headerPanel.add(logoutButton);

        JLabel sectionLabel = new JLabel("MY AVAILABLE ELECTIONS");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sectionLabel.setForeground(new Color(20, 55, 85));
        sectionLabel.setBounds(35, 125, 400, 35);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(745, 125, 110, 35);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(20, 160, 90));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(this);

        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                refreshButton.setBackground(new Color(15, 135, 75));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(new Color(20, 160, 90));
            }
        });

        electionPanel = new JPanel();
        electionPanel.setLayout(
                new BoxLayout(electionPanel, BoxLayout.Y_AXIS)
        );
        electionPanel.setBackground(new Color(240, 246, 252));
        electionPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        JScrollPane scrollPane = new JScrollPane(electionPanel);
        scrollPane.setBounds(30, 175, 825, 400);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dashboardPanel.add(headerPanel);
        dashboardPanel.add(sectionLabel);
        dashboardPanel.add(refreshButton);
        dashboardPanel.add(scrollPane);

        mainPanel.add(dashboardPanel, DASHBOARD);
    }

    private JPanel createDetailsContainer() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 246, 252));

        return panel;
    }

    private void loadElections() {

        electionPanel.removeAll();

        String sql = "SELECT election_id, election_name, description, "
                   + "start_datetime, end_datetime, status "
                   + "FROM elections ORDER BY start_datetime DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            int count = 0;

            while (rs.next()) {

                int electionID = rs.getInt("election_id");
                String name = rs.getString("election_name");
                String description = rs.getString("description");

                Timestamp startTimestamp =
                        rs.getTimestamp("start_datetime");

                Timestamp endTimestamp =
                        rs.getTimestamp("end_datetime");

                if (startTimestamp == null || endTimestamp == null) {
                    continue;
                }

                LocalDateTime start =
                        startTimestamp.toLocalDateTime();

                LocalDateTime end =
                        endTimestamp.toLocalDateTime();

                String status = rs.getString("status");

                String displayStatus = getElectionStatus(
                        status, start, end
                );

                electionPanel.add(createElectionCard(
                        electionID,
                        name,
                        description,
                        displayStatus
                ));

                electionPanel.add(Box.createVerticalStrut(15));

                count++;
            }

            if (count == 0) {

                JLabel emptyLabel = new JLabel(
                        "No elections available at the moment."
                );

                emptyLabel.setFont(
                        new Font("Arial", Font.PLAIN, 16)
                );

                emptyLabel.setForeground(Color.GRAY);

                electionPanel.add(emptyLabel);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load elections: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        electionPanel.revalidate();
        electionPanel.repaint();
    }

    private String getElectionStatus(
            String status,
            LocalDateTime start,
            LocalDateTime end) {

        LocalDateTime now = LocalDateTime.now();

        if ("ENDED".equalsIgnoreCase(status) || !now.isBefore(end)) {
            return "CLOSED";
        }

        if ("NOT STARTED".equalsIgnoreCase(status)
                || now.isBefore(start)) {
            return "UPCOMING";
        }

        if ("ONGOING".equalsIgnoreCase(status)
                && !now.isBefore(start)
                && now.isBefore(end)) {
            return "OPEN";
        }

        return "UPCOMING";
    }

    private JPanel createElectionCard(
            int electionID,
            String name,
            String description,
            String status) {

        JPanel card = new JPanel(null);

        card.setPreferredSize(new Dimension(790, 155));
        card.setMaximumSize(new Dimension(790, 155));
        card.setMinimumSize(new Dimension(790, 155));

        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(
                new Color(210, 220, 230), 1, true
        ));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 19));
        nameLabel.setForeground(new Color(20, 55, 85));
        nameLabel.setBounds(20, 15, 550, 30);

        JLabel descriptionLabel = new JLabel(
                description == null ? "" : description
        );
        descriptionLabel.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );
        descriptionLabel.setForeground(Color.GRAY);
        descriptionLabel.setBounds(20, 50, 550, 25);

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setBounds(20, 90, 150, 25);

        if (status.equals("OPEN")) {
            statusLabel.setForeground(new Color(20, 160, 90));
        } else if (status.equals("UPCOMING")) {
            statusLabel.setForeground(new Color(230, 150, 20));
        } else {
            statusLabel.setForeground(new Color(200, 65, 65));
        }

        JButton viewButton = new JButton(
                status.equals("CLOSED")
                        ? "VIEW RESULTS"
                        : "VIEW ELECTION"
        );

        viewButton.setBounds(610, 55, 155, 40);
        viewButton.setBackground(new Color(35, 125, 225));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorderPainted(false);

        viewButton.addActionListener(e -> openElection(
                electionID, name, description, status
        ));

        card.add(nameLabel);
        card.add(descriptionLabel);
        card.add(statusLabel);
        card.add(viewButton);

        return card;
    }

    private void openElection(
            int electionID,
            String name,
            String description,
            String status) {

        VoterElectionDetailsPanel detailsPanel =
                new VoterElectionDetailsPanel(
                        voterName,
                        voterID,
                        electionID,
                        name,
                        description,
                        status,
                        () -> {
                            cardLayout.show(mainPanel, DASHBOARD);
                            loadElections();
                        }
                );

        JPanel detailsContainer =
                (JPanel) mainPanel.getComponent(1);

        detailsContainer.removeAll();
        detailsContainer.add(detailsPanel, BorderLayout.CENTER);

        detailsContainer.revalidate();
        detailsContainer.repaint();

        cardLayout.show(mainPanel, DETAILS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == logoutButton) {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPanel();
            }

        } else if (e.getSource() == refreshButton) {

            loadElections();
        }
    }
}

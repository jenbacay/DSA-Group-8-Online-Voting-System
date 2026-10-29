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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ResultAnalyticsPanels extends JPanel
        implements ActionListener {

    private CardLayout cardLayout;

    private JPanel resultsPanel;
    private JPanel analyticsPanel;
    private JPanel barChartPanel;
    private JPanel pieChartPanel;

    private JPanel summaryPanel;
    private JPanel rankingPanel;

    private JComboBox<String> positionComboBox;

    private JLabel summaryVotesLabel;
    private JLabel leadingCandidateLabel;
    private JLabel analyticsVotersLabel;
    private JLabel analyticsVotesLabel;
    private JLabel analyticsTurnoutLabel;
    private JLabel rankingTitleLabel;

    private JTable rankingTable;
    private DefaultTableModel rankingModel;

    private JButton showAnalyticsButton;
    private JButton backButton;
    private JButton refreshResultsButton;
    private JButton refreshAnalyticsButton;

    private Color darkBlue = new Color(25, 75, 125);
    private Color blue = new Color(40, 125, 220);
    private Color green = new Color(35, 165, 100);
    private Color lightBlue = new Color(240, 246, 252);
    private Color borderBlue = new Color(190, 215, 240);

    private String[] positions = {
        "SK Chairperson",
        "SK Kagawad",
        "SK Treasurer",
        "SK Secretary"
    };

    private String[] candidateIDs = {
        "C001", "C002",
        "C003", "C004", "C005", "C006",
        "C007", "C008", "C009", "C010",
        "C011", "C012", "C013", "C014",
        "C015", "C016", "C017", "C018",
        "C019", "C020"
    };

    private String[] candidateNames = {
        "Ana Lopez", "Ben Santos",
        "Carla Mendoza", "David Reyes",
        "Ella Cruz", "Frank Garcia",
        "Grace Flores", "Henry Ramos",
        "Ivy Torres", "Jack Dela Cruz",
        "Karen Diaz", "Leo Bautista",
        "Mia Rivera", "Noah Castillo",
        "Olivia Perez", "Paul Fernandez",
        "Queenie Lim", "Ryan Gomez",
        "Sofia Aquino", "Tony Villanueva"
    };

    private String[] candidateParties = {
        "Party List 1", "Party List 2",

        "Party List 1", "Party List 1",
        "Party List 1", "Party List 1",
        "Party List 1", "Party List 1",
        "Party List 1", "Party List 1",

        "Party List 2", "Party List 2",
        "Party List 2", "Party List 2",
        "Party List 2", "Party List 2",
        "Party List 2", "Party List 2",

        "Party List 1", "Party List 2"
    };

    private String[] candidatePositions = {
        "SK Chairperson", "SK Chairperson",

        "SK Kagawad", "SK Kagawad",
        "SK Kagawad", "SK Kagawad",
        "SK Kagawad", "SK Kagawad",
        "SK Kagawad", "SK Kagawad",

        "SK Kagawad", "SK Kagawad",
        "SK Kagawad", "SK Kagawad",
        "SK Kagawad", "SK Kagawad",

        "SK Treasurer", "SK Treasurer",
        "SK Secretary", "SK Secretary"
    };

    private int[] candidateVotes = {
        65, 55,
        35, 32, 30, 28, 25, 23, 21, 19,
        18, 17, 16, 15, 14, 13, 12, 11,
        40, 38
    };

    private int totalRegisteredVoters = 100;
    private int totalVotersWhoVoted = 75;

    public ResultAnalyticsPanels() {

        setLayout(new CardLayout());
        setBackground(lightBlue);
        setPreferredSize(new Dimension(690, 510));

        cardLayout = (CardLayout) getLayout();

        createResultsPanel();
        createAnalyticsPanel();

        add(resultsPanel, "RESULTS");
        add(analyticsPanel, "ANALYTICS");

        updateResults();
        updateRankings();
        updateAnalytics();

        cardLayout.show(this, "RESULTS");
    }

    private void createResultsPanel() {

        resultsPanel = new JPanel();
        resultsPanel.setLayout(null);
        resultsPanel.setBackground(lightBlue);

        JLabel titleLabel = new JLabel("ELECTION RESULTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(20, 10, 500, 32);
        resultsPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel(
                "Candidate votes and election results summary");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(20, 42, 550, 22);
        resultsPanel.add(subtitleLabel);

        JLabel positionLabel = new JLabel("Select Position:");
        positionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        positionLabel.setBounds(20, 70, 100, 28);
        resultsPanel.add(positionLabel);

        positionComboBox = new JComboBox<>(positions);
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        positionComboBox.setBounds(120, 70, 190, 28);
        positionComboBox.addActionListener(this);
        resultsPanel.add(positionComboBox);

    
        summaryPanel = new JPanel();
        summaryPanel.setLayout(null);
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new LineBorder(borderBlue, 1));
        summaryPanel.setBounds(20, 108, 650, 65);
        resultsPanel.add(summaryPanel);

        JLabel summaryHeading = new JLabel("RESULTS SUMMARY");
        summaryHeading.setFont(new Font("Arial", Font.BOLD, 12));
        summaryHeading.setForeground(darkBlue);
        summaryHeading.setBounds(10, 5, 200, 20);
        summaryPanel.add(summaryHeading);

        JLabel totalVotesText = new JLabel("Total votes:");
        totalVotesText.setFont(new Font("Arial", Font.PLAIN, 11));
        totalVotesText.setBounds(10, 32, 80, 20);
        summaryPanel.add(totalVotesText);

        summaryVotesLabel = new JLabel("0 votes");
        summaryVotesLabel.setFont(new Font("Arial", Font.BOLD, 13));
        summaryVotesLabel.setForeground(blue);
        summaryVotesLabel.setBounds(85, 32, 100, 20);
        summaryPanel.add(summaryVotesLabel);

        leadingCandidateLabel = new JLabel("Leading candidate: None");
        leadingCandidateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        leadingCandidateLabel.setBounds(220, 32, 410, 20);
        summaryPanel.add(leadingCandidateLabel);

        
        barChartPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(darkBlue);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("VOTES PER CANDIDATE", 12, 22);

                String selectedPosition =
                        (String) positionComboBox.getSelectedItem();

                int maxVotes = 1;
                int count = 0;

                for (int i = 0; i < candidateVotes.length; i++) {
                    if (candidatePositions[i].equals(selectedPosition)) {
                        count++;

                        if (candidateVotes[i] > maxVotes) {
                            maxVotes = candidateVotes[i];
                        }
                    }
                }

                int chartLeft = 28;
                int chartRight = getWidth() - 12;
                int chartTop = 42;
                int chartBottom = getHeight() - 30;
                int chartHeight = chartBottom - chartTop;

                for (int i = 0; i <= 4; i++) {
                    int y = chartTop + i * chartHeight / 4;
                    g2.setColor(new Color(225, 235, 245));
                    g2.drawLine(chartLeft, y, chartRight, y);
                }

                if (count == 0) {
                    g2.setColor(Color.GRAY);
                    g2.drawString("No results available.", 15, 65);
                    return;
                }

                int slotWidth = (chartRight - chartLeft) / count;
                int barWidth = Math.max(
                        5, Math.min(20, slotWidth - 5));

                int index = 0;

                for (int i = 0; i < candidateVotes.length; i++) {

                    if (!candidatePositions[i].equals(selectedPosition)) {
                        continue;
                    }

                    int barHeight = candidateVotes[i]
                            * chartHeight / maxVotes;

                    int x = chartLeft + index * slotWidth
                            + (slotWidth - barWidth) / 2;

                    int y = chartBottom - barHeight;

                    if (candidateParties[i].equals("Party List 1")) {
                        g2.setColor(blue);
                    } else {
                        g2.setColor(green);
                    }

                    g2.fillRect(x, y, barWidth, barHeight);

                    g2.setColor(darkBlue);
                    g2.setFont(new Font("Arial", Font.BOLD, 9));
                    g2.drawString(
                            String.valueOf(candidateVotes[i]),
                            x, Math.max(chartTop + 10, y - 3));

                    g2.setFont(new Font("Arial", Font.PLAIN, 8));
                    g2.drawString(
                            candidateIDs[i],
                            x - 3, chartBottom + 15);

                    index++;
                }
            }
        };

        barChartPanel.setLayout(null);
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(new LineBorder(borderBlue, 1));
        barChartPanel.setBounds(20, 183, 315, 270);
        resultsPanel.add(barChartPanel);

        rankingPanel = new JPanel();
        rankingPanel.setLayout(null);
        rankingPanel.setBackground(Color.WHITE);
        rankingPanel.setBorder(new LineBorder(borderBlue, 1));
        rankingPanel.setBounds(345, 183, 325, 270);
        resultsPanel.add(rankingPanel);

        rankingTitleLabel = new JLabel("CANDIDATE RANKINGS");
        rankingTitleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        rankingTitleLabel.setForeground(darkBlue);
        rankingTitleLabel.setBounds(8, 5, 310, 22);
        rankingPanel.add(rankingTitleLabel);

        String[] columns = {
            "Rank", "ID", "Candidate", "Party List", "Votes"
        };

        rankingModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(rankingModel);
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 10));
        rankingTable.setRowHeight(22);
        rankingTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 10));
        rankingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(45);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        JScrollPane rankingScrollPane = new JScrollPane(rankingTable);
        rankingScrollPane.setBounds(5, 30, 313, 230);
        rankingPanel.add(rankingScrollPane);

        refreshResultsButton = new JButton("REFRESH RESULTS");
        refreshResultsButton.setFont(new Font("Arial", Font.BOLD, 11));
        refreshResultsButton.setForeground(Color.WHITE);
        refreshResultsButton.setBackground(blue);
        refreshResultsButton.setFocusPainted(false);
        refreshResultsButton.setBounds(20, 465, 180, 32);
        refreshResultsButton.addActionListener(this);
        resultsPanel.add(refreshResultsButton);

        showAnalyticsButton = new JButton("SHOW ANALYTICS");
        showAnalyticsButton.setFont(new Font("Arial", Font.BOLD, 11));
        showAnalyticsButton.setForeground(Color.WHITE);
        showAnalyticsButton.setBackground(green);
        showAnalyticsButton.setFocusPainted(false);
        showAnalyticsButton.setBounds(470, 465, 200, 32);
        showAnalyticsButton.addActionListener(this);
        resultsPanel.add(showAnalyticsButton);
    }

   
    private void createAnalyticsPanel() {

        analyticsPanel = new JPanel();
        analyticsPanel.setLayout(null);
        analyticsPanel.setBackground(lightBlue);

        JLabel titleLabel = new JLabel("ELECTION ANALYTICS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(20, 10, 500, 32);
        analyticsPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel(
                "Voter participation and turnout overview");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(20, 42, 550, 22);
        analyticsPanel.add(subtitleLabel);

        JPanel analyticsSummaryPanel = new JPanel();
        analyticsSummaryPanel.setLayout(null);
        analyticsSummaryPanel.setBackground(Color.WHITE);
        analyticsSummaryPanel.setBorder(
                new LineBorder(borderBlue, 1));
        analyticsSummaryPanel.setBounds(20, 80, 650, 85);
        analyticsPanel.add(analyticsSummaryPanel);

        addAnalyticsValue(
                analyticsSummaryPanel, "TOTAL VOTERS",
                String.valueOf(totalRegisteredVoters), 10, 12);

        addAnalyticsValue(
                analyticsSummaryPanel, "TOTAL VOTES CAST",
                String.valueOf(totalVotersWhoVoted), 220, 12);

        double turnout = 0;

        if (totalRegisteredVoters > 0) {
            turnout = totalVotersWhoVoted * 100.0
                    / totalRegisteredVoters;
        }

        addAnalyticsValue(
                analyticsSummaryPanel, "TURNOUT RATE",
                String.format("%.2f%%", turnout), 430, 12);

  
        pieChartPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(darkBlue);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("VOTER TURNOUT", 15, 25);

                int total = totalRegisteredVoters;
                int voted = Math.min(
                        totalRegisteredVoters,
                        Math.max(0, totalVotersWhoVoted));

                int notVoted = Math.max(0, total - voted);

                if (total <= 0) {
                    g2.setColor(Color.GRAY);
                    g2.drawString(
                            "No voter data available.", 20, 60);
                    return;
                }

                int size = Math.min(205, getHeight() - 60);
                int x = 45;
                int y = 40;

                int votedAngle = (int) Math.round(
                        voted * 360.0 / total);

                int notVotedAngle = 360 - votedAngle;

                g2.setColor(green);
                g2.fillArc(
                        x, y, size, size, 90, -votedAngle);

                g2.setColor(new Color(220, 230, 240));
                g2.fillArc(
                        x, y, size, size,
                        90 - votedAngle, -notVotedAngle);

                int legendX = Math.max(x + size + 35, 280);

                g2.setFont(new Font("Arial", Font.PLAIN, 13));

                g2.setColor(green);
                g2.fillRect(legendX, 75, 16, 16);

                g2.setColor(darkBlue);
                g2.drawString(
                        "Voters who voted: " + voted,
                        legendX + 25, 88);

                g2.setColor(new Color(220, 230, 240));
                g2.fillRect(legendX, 115, 16, 16);

                g2.setColor(darkBlue);
                g2.drawString(
                        "Voters who did not vote: " + notVoted,
                        legendX + 25, 128);

                double rate = voted * 100.0 / total;

                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString(
                        String.format("Turnout: %.2f%%", rate),
                        legendX, 170);
            }
        };

        pieChartPanel.setLayout(null);
        pieChartPanel.setBackground(Color.WHITE);
        pieChartPanel.setBorder(new LineBorder(borderBlue, 1));
        pieChartPanel.setBounds(20, 180, 650, 270);
        analyticsPanel.add(pieChartPanel);

        backButton = new JButton("BACK TO RESULTS");
        backButton.setFont(new Font("Arial", Font.BOLD, 11));
        backButton.setForeground(darkBlue);
        backButton.setBackground(new Color(207, 229, 252));
        backButton.setFocusPainted(false);
        backButton.setBounds(20, 465, 180, 32);
        backButton.addActionListener(this);
        analyticsPanel.add(backButton);

        refreshAnalyticsButton = new JButton("REFRESH ANALYTICS");
        refreshAnalyticsButton.setFont(new Font("Arial", Font.BOLD, 11));
        refreshAnalyticsButton.setForeground(Color.WHITE);
        refreshAnalyticsButton.setBackground(blue);
        refreshAnalyticsButton.setFocusPainted(false);
        refreshAnalyticsButton.setBounds(450, 465, 220, 32);
        refreshAnalyticsButton.addActionListener(this);
        analyticsPanel.add(refreshAnalyticsButton);
    }

    private void addAnalyticsValue(
            JPanel panel, String title, String value,
            int x, int y) {

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setBounds(x, y, 190, 20);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(darkBlue);
        valueLabel.setBounds(x, y + 25, 190, 30);
        panel.add(valueLabel);

        if (title.equals("TOTAL VOTERS")) {
            analyticsVotersLabel = valueLabel;
        } else if (title.equals("TOTAL VOTES CAST")) {
            analyticsVotesLabel = valueLabel;
        } else if (title.equals("TURNOUT RATE")) {
            analyticsTurnoutLabel = valueLabel;
        }
    }

    private void updateResults() {

        String selectedPosition =
                (String) positionComboBox.getSelectedItem();

        int totalPositionVotes = 0;
        int highestVotes = -1;
        String leadingCandidate = "None";

        for (int i = 0; i < candidateVotes.length; i++) {

            if (candidatePositions[i].equals(selectedPosition)) {

                totalPositionVotes += candidateVotes[i];

                if (candidateVotes[i] > highestVotes) {
                    highestVotes = candidateVotes[i];
                    leadingCandidate = candidateNames[i];
                }
            }
        }

        summaryVotesLabel.setText(
                totalPositionVotes + " votes");

        leadingCandidateLabel.setText(
                "Leading: " + leadingCandidate);

        barChartPanel.repaint();
    }

    private void updateRankings() {

        rankingModel.setRowCount(0);

        String selectedPosition =
                (String) positionComboBox.getSelectedItem();

        int count = 0;

        for (int i = 0; i < candidatePositions.length; i++) {
            if (candidatePositions[i].equals(selectedPosition)) {
                count++;
            }
        }

        int[] indexes = new int[count];
        int index = 0;


        for (int i = 0; i < candidatePositions.length; i++) {
            if (candidatePositions[i].equals(selectedPosition)) {
                indexes[index] = i;
                index++;
            }
        }

     
        for (int i = 0; i < count - 1; i++) {

            for (int j = 0; j < count - i - 1; j++) {

                if (candidateVotes[indexes[j]]
                        < candidateVotes[indexes[j + 1]]) {

                    int temp = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = temp;
                }
            }
        }

      
        for (int i = 0; i < count; i++) {

            int candidateIndex = indexes[i];

            rankingModel.addRow(new Object[]{
                i + 1,
                candidateIDs[candidateIndex],
                candidateNames[candidateIndex],
                candidateParties[candidateIndex],
                candidateVotes[candidateIndex]
            });
        }

        rankingTitleLabel.setText(
                "RANKINGS - " + selectedPosition.toUpperCase());
    }
    
    private void updateAnalytics() {

        analyticsVotersLabel.setText(
                String.valueOf(totalRegisteredVoters));

        analyticsVotesLabel.setText(
                String.valueOf(totalVotersWhoVoted));

        double turnout = 0;

        if (totalRegisteredVoters > 0) {
            turnout = totalVotersWhoVoted * 100.0
                    / totalRegisteredVoters;
        }

        analyticsTurnoutLabel.setText(
                String.format("%.2f%%", turnout));

        pieChartPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == positionComboBox) {

            updateResults();
            updateRankings();

        } else if (e.getSource() == showAnalyticsButton) {

            updateAnalytics();
            cardLayout.show(this, "ANALYTICS");

        } else if (e.getSource() == backButton) {

            updateResults();
            updateRankings();
            cardLayout.show(this, "RESULTS");

        } else if (e.getSource() == refreshResultsButton) {

            updateResults();
            updateRankings();

            JOptionPane.showMessageDialog(
                    this,
                    "Results and rankings refreshed.");

        } else if (e.getSource() == refreshAnalyticsButton) {

            updateAnalytics();

            JOptionPane.showMessageDialog(
                    this,
                    "Analytics refreshed.");
        }
    }
}




package GUI;

import Database.DBConnection;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoterElectionResultsPanel extends JFrame implements ActionListener {

    private JPanel mainPanel, summaryPanel, barChartPanel, rankingPanel;
    private JComboBox<String> positionComboBox;
    private JLabel summaryVotesLabel, leadingCandidateLabel, rankingTitleLabel;
    private JTable rankingTable;
    private DefaultTableModel rankingModel;
    private JButton backButton, refreshButton;

    private String voterName;
    private String voterID;
    private int electionID;
    private Runnable backAction;

    private final Color darkBlue = new Color(25, 75, 125);
    private final Color blue = new Color(40, 125, 220);
    private final Color green = new Color(35, 165, 100);
    private final Color lightBlue = new Color(240, 246, 252);
    private final Color borderBlue = new Color(190, 215, 240);

    private String[] positions = {
        "SK Chairperson",
        "SK Kagawad",
        "SK Treasurer",
        "SK Secretary"
    };

    private List<String[]> candidateResults = new ArrayList<>();

    public VoterElectionResultsPanel(
            String voterName,
            String voterID,
            int electionID,
            Runnable backAction) {

        this.voterName = voterName == null || voterName.trim().isEmpty()
                ? "Voter" : voterName.trim();

        this.voterID = voterID == null || voterID.trim().isEmpty()
                ? "ID not received" : voterID.trim();

        this.electionID = electionID;
        this.backAction = backAction;

        setTitle("Election Results");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        getContentPane().setBackground(lightBlue);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(20, 20, 845, 525);
        mainPanel.setBackground(lightBlue);
        mainPanel.setBorder(new LineBorder(borderBlue, 1));
        add(mainPanel);

        createResultsPanel();
        loadResults();
    }

    private void createResultsPanel() {

        JLabel titleLabel = new JLabel("ELECTION RESULTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(20, 10, 500, 32);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel(
                "Voter: " + voterName + " | Voter ID: " + voterID
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(20, 42, 600, 22);
        mainPanel.add(subtitleLabel);

        JLabel positionLabel = new JLabel("Select Position:");
        positionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        positionLabel.setBounds(20, 70, 100, 28);
        mainPanel.add(positionLabel);

        positionComboBox = new JComboBox<>(positions);
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        positionComboBox.setBounds(120, 70, 190, 28);
        positionComboBox.addActionListener(this);
        mainPanel.add(positionComboBox);

        summaryPanel = new JPanel();
        summaryPanel.setLayout(null);
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new LineBorder(borderBlue, 1));
        summaryPanel.setBounds(20, 108, 805, 65);
        mainPanel.add(summaryPanel);

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
        leadingCandidateLabel.setBounds(220, 32, 570, 20);
        summaryPanel.add(leadingCandidateLabel);

        barChartPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(darkBlue);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("VOTES PER CANDIDATE", 12, 22);

                int chartLeft = 35;
                int chartRight = getWidth() - 15;
                int chartTop = 45;
                int chartBottom = getHeight() - 40;
                int chartHeight = chartBottom - chartTop;

                int maxVotes = 1;

                for (String[] candidate : candidateResults) {
                    int votes = Integer.parseInt(candidate[4]);
                    maxVotes = Math.max(maxVotes, votes);
                }

                for (int i = 0; i <= 4; i++) {

                    int y = chartTop + i * chartHeight / 4;

                    g2.setColor(new Color(225, 235, 245));
                    g2.drawLine(chartLeft, y, chartRight, y);
                }

                if (candidateResults.isEmpty()) {

                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString(
                            "No candidate results available.",
                            15, 75
                    );

                    return;
                }

                int count = candidateResults.size();

                int slotWidth = Math.max(
                        1,
                        (chartRight - chartLeft) / count
                );

                int barWidth = Math.max(
                        5,
                        Math.min(25, slotWidth - 5)
                );

                String firstParty = candidateResults.get(0)[3];

                int index = 0;

                for (String[] candidate : candidateResults) {

                    int votes = Integer.parseInt(candidate[4]);

                    int barHeight = votes * chartHeight / maxVotes;

                    int x = chartLeft + index * slotWidth
                            + (slotWidth - barWidth) / 2;

                    int y = chartBottom - barHeight;

                    if (candidate[3].equalsIgnoreCase(firstParty)) {
                        g2.setColor(blue);
                    } else {
                        g2.setColor(green);
                    }

                    g2.fillRect(x, y, barWidth, barHeight);

                    g2.setColor(darkBlue);
                    g2.setFont(new Font("Arial", Font.BOLD, 9));

                    g2.drawString(
                            String.valueOf(votes),
                            x,
                            Math.max(chartTop + 10, y - 3)
                    );

                    g2.setFont(new Font("Arial", Font.PLAIN, 8));

                    g2.drawString(
                            candidate[0],
                            x - 3,
                            chartBottom + 15
                    );

                    index++;
                }
            }
        };

        barChartPanel.setLayout(null);
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(new LineBorder(borderBlue, 1));
        barChartPanel.setBounds(20, 183, 390, 270);
        mainPanel.add(barChartPanel);

        rankingPanel = new JPanel();
        rankingPanel.setLayout(null);
        rankingPanel.setBackground(Color.WHITE);
        rankingPanel.setBorder(new LineBorder(borderBlue, 1));
        rankingPanel.setBounds(420, 183, 405, 270);
        mainPanel.add(rankingPanel);

        rankingTitleLabel = new JLabel("CANDIDATE RANKINGS");
        rankingTitleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        rankingTitleLabel.setForeground(darkBlue);
        rankingTitleLabel.setBounds(8, 5, 385, 22);
        rankingPanel.add(rankingTitleLabel);

        String[] columns = {
            "Rank",
            "ID",
            "Candidate",
            "Party List",
            "Votes"
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
        rankingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        rankingTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 10)
        );

        rankingTable.getTableHeader().setBackground(darkBlue);
        rankingTable.getTableHeader().setForeground(Color.WHITE);

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(55);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(115);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(105);
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(55);

        JScrollPane rankingScrollPane = new JScrollPane(rankingTable);
        rankingScrollPane.setBounds(5, 30, 393, 230);
        rankingPanel.add(rankingScrollPane);

        refreshButton = new JButton("REFRESH RESULTS");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 11));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(blue);
        refreshButton.setFocusPainted(false);
        refreshButton.setBounds(20, 465, 180, 32);
        refreshButton.addActionListener(this);
        mainPanel.add(refreshButton);

        backButton = new JButton("BACK TO ELECTION");
        backButton.setFont(new Font("Arial", Font.BOLD, 11));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(darkBlue);
        backButton.setFocusPainted(false);
        backButton.setBounds(625, 465, 200, 32);
        backButton.addActionListener(this);
        mainPanel.add(backButton);
    }

    private void loadResults() {

        candidateResults.clear();
        rankingModel.setRowCount(0);

        String selectedPosition =
                (String) positionComboBox.getSelectedItem();

        if (selectedPosition == null) {
            return;
        }

        String sql =
                "SELECT c.candidate_id, c.name, c.party, "
                + "ep.position_name, COUNT(v.vote_id) AS total_votes "
                + "FROM candidates c "
                + "INNER JOIN election_positions ep "
                + "ON c.position_id = ep.position_id "
                + "LEFT JOIN votes v "
                + "ON c.candidate_id = v.candidate_id "
                + "WHERE ep.position_name = ? AND ep.election_id = ? "
                + "GROUP BY c.candidate_id, c.name, c.party, ep.position_name "
                + "ORDER BY total_votes DESC, c.candidate_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, selectedPosition);
            pst.setInt(2, electionID);

            try (ResultSet rs = pst.executeQuery()) {

                int rank = 1;
                int totalPositionVotes = 0;
                String leadingCandidate = "None";
                int highestVotes = -1;

                while (rs.next()) {

                    String candidateID =
                            rs.getString("candidate_id");

                    String candidateName =
                            rs.getString("name");

                    String party =
                            rs.getString("party");

                    String position =
                            rs.getString("position_name");

                    int votes =
                            rs.getInt("total_votes");

                    totalPositionVotes += votes;

                    if (votes > highestVotes) {
                        highestVotes = votes;
                        leadingCandidate = candidateName;
                    }

                    String[] candidate = {
                        candidateID,
                        candidateName,
                        position,
                        party,
                        String.valueOf(votes)
                    };

                    candidateResults.add(candidate);

                    rankingModel.addRow(new Object[]{
                        rank,
                        candidateID,
                        candidateName,
                        party,
                        votes
                    });

                    rank++;
                }

                summaryVotesLabel.setText(
                        totalPositionVotes + " votes"
                );

                if (highestVotes > 0) {

                    leadingCandidateLabel.setText(
                            "Leading: " + leadingCandidate
                    );

                } else if (candidateResults.isEmpty()) {

                    leadingCandidateLabel.setText(
                            "Leading candidate: None"
                    );

                } else {

                    leadingCandidateLabel.setText(
                            "Leading candidate: No votes yet"
                    );
                }

                rankingTitleLabel.setText(
                        "RANKINGS - " + selectedPosition.toUpperCase()
                );

                barChartPanel.repaint();
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load election results.\n"
                    + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == positionComboBox) {

            loadResults();

        } else if (e.getSource() == refreshButton) {

            loadResults();

            JOptionPane.showMessageDialog(
                    this,
                    "Election results refreshed."
            );

        } else if (e.getSource() == backButton) {

            if (backAction != null) {
                backAction.run();
            }

            dispose();
        }
    }
}




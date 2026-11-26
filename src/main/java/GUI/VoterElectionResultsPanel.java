
package GUI;

import Database.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class VoterElectionResultsPanel extends JFrame implements ActionListener {

    private JLabel titleLabel, subtitleLabel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton backButton, refreshButton;

    private String voterName;
    private String voterID;

    private final Color darkBlue = new Color(20, 55, 85);
    private final Color blue = new Color(35, 125, 225);
    private final Color background = new Color(230, 242, 255);

    public VoterElectionResultsPanel(String voterName, String voterID) {

        this.voterName = voterName;
        this.voterID = voterID;

        setTitle("Election Results");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(background);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(20, 20, 845, 525);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        add(mainPanel);

        titleLabel = new JLabel("ELECTION RESULTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(darkBlue);
        titleLabel.setBounds(30, 20, 785, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel(
                "Official voting results | Voter: " + voterName
                + " | ID: " + voterID
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(30, 60, 785, 25);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(subtitleLabel);

        String[] columns = {
            "Candidate ID",
            "Candidate Name",
            "Partylist",
            "Position",
            "Total Votes"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        resultsTable.setRowHeight(30);
        resultsTable.setSelectionBackground(new Color(210, 230, 250));
        resultsTable.setSelectionForeground(Color.BLACK);
        resultsTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        resultsTable.getTableHeader().setBackground(darkBlue);
        resultsTable.getTableHeader().setForeground(Color.WHITE);
        resultsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBounds(30, 110, 785, 330);
        scrollPane.setBorder(new LineBorder(new Color(210, 220, 230)));
        mainPanel.add(scrollPane);

        refreshButton = new JButton("⟳  REFRESH RESULTS");
        refreshButton.setBounds(445, 465, 180, 40);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(blue);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(this);
        mainPanel.add(refreshButton);

        backButton = new JButton("←  BACK TO DASHBOARD");
        backButton.setBounds(235, 465, 195, 40);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(darkBlue);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        mainPanel.add(backButton);

        loadResults();
    }

    private void loadResults() {

        tableModel.setRowCount(0);

        String sql = "SELECT c.candidate_id, c.name, c.party, "
                   + "c.position, COUNT(v.vote_id) AS total_votes "
                   + "FROM candidates c "
                   + "LEFT JOIN votes v ON c.candidate_id = v.candidate_id "
                   + "GROUP BY c.candidate_id, c.name, c.party, c.position "
                   + "ORDER BY c.position ASC, total_votes DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getString("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getString("position"),
                    rs.getInt("total_votes")
                };

                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No election results are available yet.",
                        "Election Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load election results.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == refreshButton) {

            loadResults();

        } else if (e.getSource() == backButton) {

            VoterDashboard dashboard =
                    new VoterDashboard(voterName, voterID);

            dashboard.setVisible(true);
            dispose();
        }
    }
}



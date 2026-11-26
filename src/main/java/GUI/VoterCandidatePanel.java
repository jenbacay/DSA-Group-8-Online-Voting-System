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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class VoterCandidatePanel extends JFrame implements ActionListener {

    private JLabel titleLabel, electionLabel, welcomeLabel;
    private JTextField searchField;
    private JButton searchButton, refreshButton, backButton;
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private String voterName;
    private String voterID;

    public VoterCandidatePanel(String voterName, String voterID) {
        this.voterName = voterName;
        this.voterID = voterID;

        setTitle("List of Candidates");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 242, 255));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(20, 20, 845, 520);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(new Color(190, 215, 240), 1));
        add(mainPanel);

        titleLabel = new JLabel("LIST OF CANDIDATES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(20, 55, 85));
        titleLabel.setBounds(25, 20, 795, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        welcomeLabel = new JLabel(
            "Voter: " + voterName + "     |     Voter ID: " + voterID
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(80, 80, 80));
        welcomeLabel.setBounds(35, 65, 770, 25);
        mainPanel.add(welcomeLabel);

        electionLabel = new JLabel("Election: Loading...");
        electionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        electionLabel.setForeground(new Color(35, 85, 130));
        electionLabel.setBounds(35, 100, 770, 25);
        mainPanel.add(electionLabel);

        JLabel searchLabel = new JLabel("Search Candidate:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchLabel.setBounds(35, 145, 130, 25);
        mainPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBounds(165, 145, 400, 30);
        mainPanel.add(searchField);

        searchButton = new JButton("SEARCH");
        searchButton.setBounds(580, 145, 100, 30);
        searchButton.setBackground(new Color(35, 125, 225));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(this);
        mainPanel.add(searchButton);

        refreshButton = new JButton("REFRESH");
        refreshButton.setBounds(690, 145, 110, 30);
        refreshButton.setBackground(new Color(20, 170, 105));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(this);
        mainPanel.add(refreshButton);

        String[] columns = {
            "Candidate ID",
            "Candidate Name",
            "Partylist",
            "Position"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 13));
        candidateTable.setRowHeight(30);
        candidateTable.setSelectionBackground(new Color(210, 230, 250));
        candidateTable.setSelectionForeground(Color.BLACK);
        candidateTable.setGridColor(new Color(220, 225, 230));

        JTableHeader header = candidateTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(20, 55, 85));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(tableModel);
        candidateTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBounds(35, 195, 770, 250);
        scrollPane.setBorder(new LineBorder(new Color(210, 220, 230)));
        mainPanel.add(scrollPane);

        backButton = new JButton("BACK TO DASHBOARD");
        backButton.setBounds(320, 465, 205, 35);
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setBackground(new Color(20, 55, 85));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        mainPanel.add(backButton);

        loadCandidates();
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);
        electionLabel.setText("Election: Loading...");

        String sql =
            "SELECT e.election_name, c.candidate_id, c.name, c.party, " +
            "ep.position_name " +
            "FROM candidates c " +
            "INNER JOIN election_positions ep " +
            "ON c.position_id = ep.position_id " +
            "INNER JOIN elections e " +
            "ON ep.election_id = e.election_id " +
            "WHERE e.status = 'ONGOING' " +
            "ORDER BY ep.position_id, c.party, c.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            boolean hasCandidates = false;
            String electionName = "";

            while (rs.next()) {
                hasCandidates = true;
                electionName = rs.getString("election_name");

                tableModel.addRow(new Object[]{
                    rs.getString("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getString("position_name")
                });
            }

            if (hasCandidates) {
                electionLabel.setText("Election: " + electionName);
            } else {
                electionLabel.setText("No ongoing election");
                JOptionPane.showMessageDialog(
                    this,
                    "There are currently no candidates available for an ongoing election.",
                    "No Candidates",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load candidates: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void searchCandidates() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword))
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == searchButton) {
            searchCandidates();

        } else if (e.getSource() == refreshButton) {
            searchField.setText("");
            sorter.setRowFilter(null);
            loadCandidates();

        } else if (e.getSource() == backButton) {
            VoterDashboard dashboard = new VoterDashboard(
                voterName,
                voterID
            );
            dashboard.setVisible(true);
            this.dispose();
        }
    }
}



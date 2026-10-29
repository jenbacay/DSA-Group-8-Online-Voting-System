/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author JenJen
 */
import java.sql.Timestamp;

public class Vote {

    private String voteId;
    private String voterId;
    private String candidateId;
    private Timestamp timestamp;

    public Vote(String voteId, String voterId,
                String candidateId, Timestamp timestamp) {

        this.voteId = voteId;
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.timestamp = timestamp;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
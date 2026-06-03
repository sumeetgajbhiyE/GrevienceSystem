package model;

import java.time.LocalDateTime;

public class StatusHistory {

    private int id;
    private int grievanceId;
    private String status;
    private LocalDateTime changedAt;
    private String remarks;

    public StatusHistory() {
    }

    public StatusHistory(int grievanceId, String status, String remarks) {
        this.grievanceId = grievanceId;
        this.status = status;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public int getGrievanceId() {
        return grievanceId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGrievanceId(int grievanceId) {
        this.grievanceId = grievanceId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChangedAt(LocalDateTime dt) {
        this.changedAt = dt;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return String.format("[%s] Status changed to '%s' — %s", changedAt, status, remarks);
    }
}

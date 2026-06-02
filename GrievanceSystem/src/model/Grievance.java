package model;

import java.time.LocalDate;

public class Grievance {
    private int id;
    private int citizenId;
    private int categoryId;
    private int officerId;
    private String description;
    private String status;
    private LocalDate submittedDate;
    private int priority;

    // For display purposes (joined fields)
    private String citizenName;
    private String categoryName;
    private int categoryWeight;

    public Grievance() {}

    public Grievance(int citizenId, int categoryId, String description, LocalDate submittedDate) {
        this.citizenId     = citizenId;
        this.categoryId    = categoryId;
        this.description   = description;
        this.submittedDate = submittedDate;
        this.status        = "Submitted";
    }

    // ── Getters ──────────────────────────────────
    public int getId()                  { return id; }
    public int getCitizenId()           { return citizenId; }
    public int getCategoryId()          { return categoryId; }
    public int getOfficerId()           { return officerId; }
    public String getDescription()      { return description; }
    public String getStatus()           { return status; }
    public LocalDate getSubmittedDate() { return submittedDate; }
    public int getPriority()            { return priority; }
    public String getCitizenName()      { return citizenName; }
    public String getCategoryName()     { return categoryName; }
    public int getCategoryWeight()      { return categoryWeight; }

    // ── Setters ──────────────────────────────────
    public void setId(int id)                           { this.id = id; }
    public void setCitizenId(int citizenId)             { this.citizenId = citizenId; }
    public void setCategoryId(int categoryId)           { this.categoryId = categoryId; }
    public void setOfficerId(int officerId)             { this.officerId = officerId; }
    public void setDescription(String description)      { this.description = description; }
    public void setStatus(String status)                { this.status = status; }
    public void setSubmittedDate(LocalDate date)        { this.submittedDate = date; }
    public void setPriority(int priority)               { this.priority = priority; }
    public void setCitizenName(String name)             { this.citizenName = name; }
    public void setCategoryName(String name)            { this.categoryName = name; }
    public void setCategoryWeight(int weight)           { this.categoryWeight = weight; }

    @Override
    public String toString() {
        return String.format("Grievance{id=%d, category='%s', status='%s', priority=%d, date=%s}",
                id, categoryName, status, priority, submittedDate);
    }
}

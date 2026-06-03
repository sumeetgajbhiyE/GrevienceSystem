package service;

import algorithm.GrievanceMinHeap;
import dao.*;
import model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * GrievanceService — Business Logic Layer
 * Connects Console Menu ↔ DAOs ↔ Heap
 */
public class GrievanceService {

    private final GrievanceDAO     grievanceDAO     = new GrievanceDAO();
    private final CategoryDAO      categoryDAO      = new CategoryDAO();
    private final CitizenDAO       citizenDAO       = new CitizenDAO();
    private final OfficerDAO       officerDAO       = new OfficerDAO();
    private final StatusHistoryDAO statusHistoryDAO = new StatusHistoryDAO();

    private final GrievanceMinHeap heap = new GrievanceMinHeap();

    // ── On startup: load all active grievances into heap ────

    public void initHeap() {
        List<Grievance> active = grievanceDAO.getAllActiveGrievances();
        heap.buildHeap(active);
        System.out.println("[Service] Heap initialized with " + active.size() + " active grievances.");
    }

    // ── 1. Submit Grievance ─────────────────────────────────

    public int submitGrievance(int citizenId, int categoryId, String description) {
        Category cat = categoryDAO.getCategoryById(categoryId);
        if (cat == null) {
            System.out.println("[Service] Invalid category ID.");
            return -1;
        }

        Grievance g = new Grievance(citizenId, categoryId, description, LocalDate.now());
        g.setCategoryName(cat.getName());
        g.setCategoryWeight(cat.getWeight());

        // Calculate priority BEFORE insert
        int priority = GrievanceMinHeap.calculatePriority(g);
        g.setPriority(priority);

        int id = grievanceDAO.insertGrievance(g);
        if (id > 0) {
            g.setId(id);
            heap.insert(g);
            statusHistoryDAO.logStatus(id, "Submitted", "Grievance submitted by citizen.");
            System.out.println("[Service] Grievance #" + id + " submitted. Priority: " + priority);
        }
        return id;
    }

    // ── 2. Officer View — Top 10 by Priority ───────────────

    public Grievance[] getTop10ForOfficer() {
        // Rebuild heap fresh from DB to apply latest aging
        List<Grievance> active = grievanceDAO.getAllActiveGrievances();
        heap.buildHeap(active);
        return heap.getTopN(10);
    }

    // ── 3. Update Status ────────────────────────────────────

    public boolean updateStatus(int grievanceId, String newStatus, String remarks) {
        boolean updated = grievanceDAO.updateStatus(grievanceId, newStatus);
        if (updated) {
            statusHistoryDAO.logStatus(grievanceId, newStatus, remarks);

            // If resolved, increment officer's count
            if ("Resolved".equalsIgnoreCase(newStatus)) {
                Grievance g = grievanceDAO.getGrievanceById(grievanceId);
                if (g != null && g.getOfficerId() > 0) {
                    officerDAO.incrementResolvedCount(g.getOfficerId());
                }
                // Rebuild heap without this grievance
                initHeap();
            }
        }
        return updated;
    }

    // ── 4. View History of One Grievance ───────────────────

    public List<StatusHistory> getGrievanceHistory(int grievanceId) {
        return statusHistoryDAO.getHistoryByGrievanceId(grievanceId);
    }

    // ── 5. Analytics ────────────────────────────────────────

    public List<String[]> getAnalytics() {
        return grievanceDAO.getGrievancesPerCategory();
    }

    // ── Bonus: Leaderboard ──────────────────────────────────

    public List<Officer> getLeaderboard() {
        return officerDAO.getLeaderboard();
    }

    // ── Helper lookups for menu ─────────────────────────────

    public List<Category> getAllCategories()    { return categoryDAO.getAllCategories(); }
    public List<Citizen>  getAllCitizens()      { return citizenDAO.getAllCitizens(); }
    public List<Officer>  getAllOfficers()      { return officerDAO.getAllOfficers(); }
    public List<Grievance> getAllGrievances()   { return grievanceDAO.getAllGrievances(); }
    public Grievance getGrievanceById(int id)  { return grievanceDAO.getGrievanceById(id); }
    public GrievanceMinHeap getHeap()          { return heap; }
}

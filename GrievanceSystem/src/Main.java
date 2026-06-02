import html.HTMLGenerator;
import model.*;
import service.GrievanceService;
import util.DBConnection;

import java.util.List;
import java.util.Scanner;

/**
 * ============================================================
 *  Project P6 — Grievance Priority Queue
 *  Soft Polynomials Pvt. Ltd.
 *  Team: Amit + Sumeet
 *
 *  Entry Point — Console Menu
 * ============================================================
 */
public class Main {

    private static final GrievanceService service = new GrievanceService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     CITIZEN GRIEVANCE MANAGEMENT SYSTEM      ║");
        System.out.println("║          Soft Polynomials Pvt. Ltd.          ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // Load grievances into heap on startup
        service.initHeap();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1  -> submitGrievance();
                case 2  -> officerView();
                case 3  -> updateStatus();
                case 4  -> viewGrievanceHistory();
                case 5  -> generateAnalytics();
                case 6  -> searchByID();
                case 7  -> showLeaderboard();
                case 8  -> showHeapDebug();
                case 0  -> { running = false; shutdown(); }
                default -> System.out.println("  ❌ Invalid option. Try again.");
            }
        }
    }

    // ── Menu ────────────────────────────────────────────────

    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│               MAIN MENU                 │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│  1. Submit Grievance                     │");
        System.out.println("│  2. Officer View (Top 10 by Priority)    │");
        System.out.println("│  3. Update Grievance Status              │");
        System.out.println("│  4. View History of a Grievance          │");
        System.out.println("│  5. Generate Analytics Report (HTML)     │");
        System.out.println("│  6. Search Grievance by ID               │");
        System.out.println("│  7. Officer Leaderboard                  │");
        System.out.println("│  8. [Debug] Show Heap Array State        │");
        System.out.println("│  0. Exit                                 │");
        System.out.println("└─────────────────────────────────────────┘");
    }

    // ── 1. Submit Grievance ─────────────────────────────────

    private static void submitGrievance() {
        System.out.println("\n── SUBMIT GRIEVANCE ──────────────────────");

        // Show citizens
        List<Citizen> citizens = service.getAllCitizens();
        System.out.println("Available Citizens:");
        citizens.forEach(c -> System.out.printf("  [%d] %s (%s)%n", c.getId(), c.getName(), c.getContact()));

        int citizenId = readInt("Select Citizen ID: ");

        // Show categories
        List<Category> cats = service.getAllCategories();
        System.out.println("\nCategories:");
        cats.forEach(c -> System.out.printf("  [%d] %-10s (weight=%d)%n", c.getId(), c.getName(), c.getWeight()));

        int catId = readInt("Select Category ID: ");

        System.out.print("Enter description: ");
        String desc = scanner.nextLine().trim();

        int id = service.submitGrievance(citizenId, catId, desc);
        if (id > 0) {
            System.out.println("\n  ✅ Grievance submitted successfully! ID = " + id);
        } else {
            System.out.println("\n  ❌ Submission failed.");
        }
    }

    // ── 2. Officer View ─────────────────────────────────────

    private static void officerView() {
        System.out.println("\n── TOP 10 GRIEVANCES BY PRIORITY ────────");
        Grievance[] top10 = service.getTop10ForOfficer();

        if (top10.length == 0) {
            System.out.println("  No active grievances.");
            return;
        }

        System.out.printf("%-4s %-6s %-12s %-10s %-8s %-12s%n",
                "Rank", "GID", "Category", "Priority", "Status", "Date");
        System.out.println("─".repeat(60));

        for (int i = 0; i < top10.length; i++) {
            Grievance g = top10[i];
            if (g == null) continue;
            System.out.printf("%-4d %-6d %-12s %-10d %-8s %-12s%n",
                    i + 1, g.getId(), g.getCategoryName(),
                    g.getPriority(), g.getStatus(), g.getSubmittedDate());
        }

        // Also write to HTML
        HTMLGenerator.writePriorityDashboard(top10);
        System.out.println("\n  📄 priority_dashboard.html generated.");
    }

    // ── 3. Update Status ────────────────────────────────────

    private static void updateStatus() {
        System.out.println("\n── UPDATE GRIEVANCE STATUS ───────────────");
        int id = readInt("Enter Grievance ID: ");

        Grievance g = service.getGrievanceById(id);
        if (g == null) { System.out.println("  ❌ Grievance not found."); return; }

        System.out.println("  Current status: " + g.getStatus());
        System.out.println("  [1] Submitted   [2] In Progress   [3] Resolved   [4] Reopened");
        int opt = readInt("Select new status: ");

        String[] statuses = {"Submitted", "In Progress", "Resolved", "Reopened"};
        if (opt < 1 || opt > 4) { System.out.println("  ❌ Invalid."); return; }

        String newStatus = statuses[opt - 1];
        System.out.print("Enter remarks: ");
        String remarks = scanner.nextLine().trim();

        boolean ok = service.updateStatus(id, newStatus, remarks);
        System.out.println(ok ? "  ✅ Status updated to: " + newStatus : "  ❌ Update failed.");
    }

    // ── 4. View History ─────────────────────────────────────

    private static void viewGrievanceHistory() {
        System.out.println("\n── GRIEVANCE HISTORY ─────────────────────");
        int id = readInt("Enter Grievance ID: ");

        var history = service.getGrievanceHistory(id);
        if (history.isEmpty()) {
            System.out.println("  No history found for GID=" + id);
            return;
        }

        System.out.println("  History for Grievance #" + id + ":");
        System.out.println("─".repeat(60));
        history.forEach(sh -> System.out.println("  " + sh));
    }

    // ── 5. Analytics ────────────────────────────────────────

    private static void generateAnalytics() {
        System.out.println("\n── ANALYTICS REPORT ──────────────────────");
        var data       = service.getAnalytics();
        var leaderboard = service.getLeaderboard();

        System.out.printf("%-15s %-10s %-15s%n", "Category", "Total", "Avg Days");
        System.out.println("─".repeat(45));
        data.forEach(r -> System.out.printf("%-15s %-10s %-15s%n", r[0], r[1], r[2] + " days"));

        HTMLGenerator.writeAnalytics(data, leaderboard);
        System.out.println("\n  📄 analytics.html generated.");
    }

    // ── 6. Search by ID ─────────────────────────────────────

    private static void searchByID() {
        System.out.println("\n── SEARCH GRIEVANCE ──────────────────────");
        int id = readInt("Enter Grievance ID: ");
        Grievance g = service.getGrievanceById(id);
        if (g == null) {
            System.out.println("  ❌ Not found.");
        } else {
            System.out.println("  " + g);
            System.out.println("  Description: " + g.getDescription());
            System.out.println("  Citizen    : " + g.getCitizenName());
            System.out.println("  Category   : " + g.getCategoryName() + " (weight=" + g.getCategoryWeight() + ")");
            System.out.println("  Priority   : " + g.getPriority());
            System.out.println("  Status     : " + g.getStatus());
        }
    }

    // ── 7. Leaderboard ──────────────────────────────────────

    private static void showLeaderboard() {
        System.out.println("\n── OFFICER LEADERBOARD (This Month) ─────");
        var leaders = service.getLeaderboard();
        if (leaders.isEmpty()) {
            System.out.println("  No data yet.");
            return;
        }
        System.out.printf("%-6s %-20s %-15s %-10s%n", "Rank", "Officer", "Department", "Resolved");
        System.out.println("─".repeat(55));
        for (int i = 0; i < leaders.size(); i++) {
            Officer o = leaders.get(i);
            System.out.printf("%-6d %-20s %-15s %-10d%n",
                    i + 1, o.getName(), o.getDepartment(), o.getResolvedCount());
        }
    }

    // ── 8. Heap Debug (Viva helper) ──────────────────────────

    private static void showHeapDebug() {
        System.out.println("\n── HEAP ARRAY STATE (Debug/Viva) ─────────");
        service.getHeap().printHeapArray();
    }

    // ── Helpers ──────────────────────────────────────────────

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void shutdown() {
        System.out.println("\n  Goodbye! Closing DB connection...");
        DBConnection.closeConnection();
        System.out.println("  System exited.");
    }
}

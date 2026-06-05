
import html.HTMLGenerator;
import model.*;
import service.GrievanceService;
import util.DBConnection;
import java.awt.Desktop;
import java.io.File;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final GrievanceService service = new GrievanceService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("╔=============================================╗");
        System.out.println("║     CITIZEN GRIEVANCE MANAGEMENT SYSTEM     ║");
        System.out.println("║         Soft Polynomials Pvt. Ltd.          ║");
        System.out.println("╚=============================================╝");

        service.initHeap();

        boolean running = true;

        while (running) {

            printMenu();

            int choice = readInt("Enter choice: ");

            switch (choice) {

                case 1:
                    submitGrievance();
                    break;

                case 2:
                    officerView();
                    break;

                case 3:
                    updateStatus();
                    break;

                case 4:
                    viewGrievanceHistory();
                    break;

                case 5:
                    generateAnalytics();
                    break;

                case 6:
                    searchByID();
                    break;

                case 7:
                    showLeaderboard();
                    break;

                case 8:
                    showHeapDebug();
                    break;

                case 0:
                    running = false;
                    shutdown();
                    break;

                default:
                    System.out.println("❌ Invalid option.");
            }
        }
    }

    private static void printMenu() {

        System.out.println("\n┌ =======================================┐");
        System.out.println("│               MAIN MENU                 │");
        System.out.println("├-----------------------------------------┤");
        System.out.println("│ 1. Submit Grievance                     │");
        System.out.println("│ 2. Officer View (Top 10 by Priority)    │");
        System.out.println("│ 3. Update Grievance Status              │");
        System.out.println("│ 4. View History of a Grievance          │");
        System.out.println("│ 5. Generate Analytics Report (HTML)     │");
        System.out.println("│ 6. Search Grievance by ID               │");
        System.out.println("│ 7. Officer Leaderboard                  │");
        System.out.println("│ 8. Show Heap Array State                │");
        System.out.println("│ 0. Exit                                 │");
        System.out.println("└=========================================┘");
    }

    private static void submitGrievance() {

        System.out.println("\n--- SUBMIT GRIEVANCE ---");

        List<Citizen> citizens = service.getAllCitizens();

        System.out.println("Available Citizens:");

        for (Citizen c : citizens) {
            System.out.printf("[%d] %s (%s)%n",
                    c.getId(),
                    c.getName(),
                    c.getContact());
        }

        int citizenId = readInt("Select Citizen ID: ");

        List<Category> categories = service.getAllCategories();

        System.out.println("\nAvailable Categories:");

        for (Category c : categories) {
            System.out.printf("[%d] %s (Weight=%d)%n",
                    c.getId(),
                    c.getName(),
                    c.getWeight());
        }

        int categoryId = readInt("Select Category ID: ");
         List<Officer> officers = service.getAllOfficers();
        System.out.println("\nAvailable Officers:");
        for (Officer o : officers) {
            System.out.printf("[%d] %s | %s | Resolved=%d%n", 
                o.getId(), o.getName(), o.getDepartment(), o.getResolvedCount());
        }
        int officerId = readInt("Select Officer ID: ");
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

              int grievanceId = service.submitGrievance(
                        citizenId,
                        categoryId,
                        officerId,
                        description
                );

        if (grievanceId > 0) {
            System.out.println("✅ Grievance Submitted. ID = " + grievanceId);

            // Refresh dashboard and open browser
            service.refreshPriorityDashboard();
            openHtmlFile("priority_dashboard.html");
        } else {
            System.out.println("❌ Submission Failed.");
        }
    }

    private static void officerView() {

        System.out.println("\n--- TOP PRIORITY GRIEVANCES ---");

        Grievance[] grievances
                = service.getTop10ForOfficer();

        if (grievances.length == 0) {
            System.out.println("No Active Grievances.");
            return;
        }

        for (int i = 0; i < grievances.length; i++) {

            if (grievances[i] == null) {
                continue;
            }

            Grievance g = grievances[i];

            System.out.printf(
                    "%d. GID=%d | Category=%s | Priority=%d | Status=%s%n",
                    i + 1,
                    g.getId(),
                    g.getCategoryName(),
                    g.getPriority(),
                    g.getStatus()
            );
        }

        HTMLGenerator.writePriorityDashboard(grievances);

        System.out.println("priority_dashboard.html generated.");
        openHtmlFile("priority_dashboard.html");
    }

    private static void updateStatus() {

        int grievanceId
                = readInt("Enter Grievance ID: ");

        Grievance grievance
                = service.getGrievanceById(grievanceId);

        if (grievance == null) {

            System.out.println("❌ Grievance Not Found.");
            return;
        }

        System.out.println("Current Status: "
                + grievance.getStatus());

        System.out.println("1. Submitted");
        System.out.println("2. In Progress");
        System.out.println("3. Resolved");
        System.out.println("4. Reopened");

        int option
                = readInt("Select New Status: ");

        String status;

        switch (option) {

            case 1:
                status = "Submitted";
                break;

            case 2:
                status = "In Progress";
                break;

            case 3:
                status = "Resolved";
                break;

            case 4:
                status = "Reopened";
                break;

            default:
                System.out.println("Invalid Status.");
                return;
        }

        System.out.print("Enter Remarks: ");
        String remarks = scanner.nextLine();

        boolean success
                = service.updateStatus(
                        grievanceId,
                        status,
                        remarks
                );

        if (success) {
            System.out.println("✅ Status Updated.");

            // Refresh dashboard and open browser
            service.refreshPriorityDashboard();
            openHtmlFile("priority_dashboard.html");
        } else {
            System.out.println("❌ Update Failed.");
        }
    }

    private static void viewGrievanceHistory() {

        int grievanceId
                = readInt("Enter Grievance ID: ");

        var history
                = service.getGrievanceHistory(grievanceId);

        if (history.isEmpty()) {

            System.out.println("No History Found.");
            return;
        }

        System.out.println("\nHistory:");

        history.forEach(System.out::println);
    }

    private static void generateAnalytics() {

        System.out.println("\n________________ ANALYTICS REPORT___________________");

        var data = service.getAnalytics();
        var leaderboard = service.getLeaderboard();

        if (data.isEmpty()) {
            System.out.println("No analytics data found.");
            return;
        }

        System.out.printf("%-15s %-10s %-15s%n",
                "Category", "Total", "Avg Days");

        System.out.println("─────────────────────────────────────────");

        for (String[] row : data) {

            System.out.printf("%-15s %-10s %-15s%n",
                    row[0],
                    row[1],
                    row[2] + " days");
        }

        HTMLGenerator.writeAnalytics(data, leaderboard);

        System.out.println("\n📄 analytics.html generated.");
        openHtmlFile("analytics.html");
    }

    private static void searchByID() {

        int id
                = readInt("Enter Grievance ID: ");

        Grievance g
                = service.getGrievanceById(id);

        if (g == null) {

            System.out.println("❌ Not Found.");
            return;
        }

        System.out.println(g);
    }

    private static void showLeaderboard() {

        var officers
                = service.getLeaderboard();

        if (officers.isEmpty()) {

            System.err.println("No Data Available.");
            return;
        }

        for (int i = 0; i < officers.size(); i++) {

            Officer o = officers.get(i);

            System.out.printf(
                    "%d. %s | %s | Resolved=%d%n",
                    i + 1,
                    o.getName(),
                    o.getDepartment(),
                    o.getResolvedCount()
            );
        }
    }

    private static void showHeapDebug() {

        if (service.getHeap() != null) {
            service.getHeap().printHeapArray();
        }
    }

    private static int readInt(String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.err.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private static void shutdown() {

        System.out.println(
                "\nClosing Database Connection..."
        );

        DBConnection.closeConnection();

        System.out.println("System Exited.");
    }

    private static void openHtmlFile(String filename) {
        try {
            File htmlFile = new File(filename);
            if (!htmlFile.exists()) {
                System.out.println("⚠️ File not found: " + filename);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(htmlFile.toURI());
                System.out.println("🌐 Opened " + filename + " in browser.");
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", filename});
                System.out.println("🌐 Opened " + filename + " in browser (xdg-open).");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Could not open browser: " + e.getMessage());
            System.out.println("   Please open " + filename + " manually.");
        }
    }
}

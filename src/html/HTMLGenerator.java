package html;

import model.Grievance;
import model.Officer;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HTMLGenerator — writes HTML output files
 * priority_dashboard.html  → current priority queue
 * analytics.html           → category-wise count + avg resolution days
 */
public class HTMLGenerator {

    private static final String STYLE = """
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Segoe UI', sans-serif; background: #f0f4f8; color: #2d3748; }
          header { background: #2b6cb0; color: white; padding: 20px 40px; }
          header h1 { font-size: 1.8rem; }
          header p  { font-size: 0.9rem; opacity: 0.8; margin-top: 4px; }
          .container { max-width: 1100px; margin: 30px auto; padding: 0 20px; }
          table { width: 100%; border-collapse: collapse; background: white;
                  border-radius: 8px; overflow: hidden;
                  box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
          th { background: #2b6cb0; color: white; padding: 12px 16px; text-align: left; }
          td { padding: 11px 16px; border-bottom: 1px solid #e2e8f0; }
          tr:last-child td { border-bottom: none; }
          tr:hover td { background: #ebf4ff; }
          .badge { padding: 3px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; }
          .Submitted  { background: #bee3f8; color: #2a69ac; }
          .In-Progress{ background: #fefcbf; color: #744210; }
          .Resolved   { background: #c6f6d5; color: #22543d; }
          .Reopened   { background: #fed7d7; color: #742a2a; }
          .high  { color: #c53030; font-weight: bold; }
          .med   { color: #c05621; font-weight: bold; }
          .low   { color: #2f855a; }
          footer { text-align: center; padding: 20px; color: #718096; font-size: 0.85rem; }
        </style>
        """;

    /**
     * Write priority_dashboard.html — shows top grievances in priority order.
     */
    public static void writePriorityDashboard(Grievance[] topGrievances) {
        String filename = "priority_dashboard.html";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
          .append("<title>Grievance Priority Dashboard</title>")
          .append(STYLE)
          .append("</head><body>")
          .append("<header>")
          .append("<h1>🏛️ Grievance Priority Dashboard</h1>")
          .append("<p>Generated: ").append(time).append(" | Top priority grievances for officer action</p>")
          .append("</header>")
          .append("<div class='container'>")
          .append("<table>")
          .append("<thead><tr>")
          .append("<th>#</th><th>GID</th><th>Category</th><th>Description</th>")
          .append("<th>Citizen</th><th>Date Submitted</th><th>Priority Score</th><th>Status</th>")
          .append("</tr></thead><tbody>");

        if (topGrievances == null || topGrievances.length == 0) {
            sb.append("<tr><td colspan='8' style='text-align:center;padding:30px;color:#718096;'>")
              .append("No active grievances.</td></tr>");
        } else {
            for (int i = 0; i < topGrievances.length; i++) {
                Grievance g = topGrievances[i];
                if (g == null) continue;

                String priorityClass = g.getPriority() >= 40 ? "high" : g.getPriority() >= 20 ? "med" : "low";
                String statusClass   = g.getStatus().replace(" ", "-");
                String desc = g.getDescription() != null && g.getDescription().length() > 50
                        ? g.getDescription().substring(0, 50) + "..."
                        : g.getDescription();

                sb.append("<tr>")
                  .append("<td>").append(i + 1).append("</td>")
                  .append("<td><b>").append(g.getId()).append("</b></td>")
                  .append("<td>").append(g.getCategoryName()).append("</td>")
                  .append("<td>").append(desc).append("</td>")
                  .append("<td>").append(g.getCitizenName() != null ? g.getCitizenName() : "—").append("</td>")
                  .append("<td>").append(g.getSubmittedDate()).append("</td>")
                  .append("<td class='").append(priorityClass).append("'>").append(g.getPriority()).append("</td>")
                  .append("<td><span class='badge ").append(statusClass).append("'>")
                  .append(g.getStatus()).append("</span></td>")
                  .append("</tr>");
            }
        }

        sb.append("</tbody></table></div>")
          .append("<footer>Soft Polynomials Pvt. Ltd. | Project P6 — Grievance Priority Queue</footer>")
          .append("</body></html>");

        writeFile(filename, sb.toString());
        System.out.println("[HTML] Written: " + filename);
    }

    /**
     * Write analytics.html — category-wise count + avg resolution days + officer leaderboard.
     */
    public static void writeAnalytics(List<String[]> categoryData, List<Officer> leaderboard) {
        String filename = "analytics.html";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
          .append("<title>Grievance Analytics</title>")
          .append(STYLE)
          .append("</head><body>")
          .append("<header>")
          .append("<h1>📊 Grievance Analytics Report</h1>")
          .append("<p>Generated: ").append(time).append("</p>")
          .append("</header>")
          .append("<div class='container'>")

          // Category table
          .append("<h2 style='margin:24px 0 12px;'>Category-wise Summary</h2>")
          .append("<table><thead><tr>")
          .append("<th>Category</th><th>Total Grievances</th><th>Avg Resolution Days</th>")
          .append("</tr></thead><tbody>");

        if (categoryData.isEmpty()) {
            sb.append("<tr><td colspan='3' style='text-align:center;'>No data available.</td></tr>");
        } else {
            for (String[] row : categoryData) {
                sb.append("<tr>")
                  .append("<td>").append(row[0]).append("</td>")
                  .append("<td>").append(row[1]).append("</td>")
                  .append("<td>").append(row[2]).append(" days</td>")
                  .append("</tr>");
            }
        }

        sb.append("</tbody></table>")

          // Officer leaderboard
          .append("<h2 style='margin:32px 0 12px;'>🏆 Officer Leaderboard (This Month)</h2>")
          .append("<table><thead><tr>")
          .append("<th>Rank</th><th>Officer Name</th><th>Department</th><th>Resolved This Month</th>")
          .append("</tr></thead><tbody>");

        if (leaderboard == null || leaderboard.isEmpty()) {
            sb.append("<tr><td colspan='4' style='text-align:center;'>No resolved grievances this month.</td></tr>");
        } else {
            String[] medals = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < leaderboard.size(); i++) {
                Officer o = leaderboard.get(i);
                String rank = i < 3 ? medals[i] : String.valueOf(i + 1);
                sb.append("<tr>")
                  .append("<td>").append(rank).append("</td>")
                  .append("<td>").append(o.getName()).append("</td>")
                  .append("<td>").append(o.getDepartment()).append("</td>")
                  .append("<td><b>").append(o.getResolvedCount()).append("</b></td>")
                  .append("</tr>");
            }
        }

        sb.append("</tbody></table></div>")
          .append("<footer>Soft Polynomials Pvt. Ltd. | Project P6 Analytics</footer>")
          .append("</body></html>");

        writeFile(filename, sb.toString());
        System.out.println("[HTML] Written: " + filename);
    }

    private static void writeFile(String filename, String content) {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(content);
        } catch (IOException e) {
            System.out.println("[HTML] Error writing " + filename + ": " + e.getMessage());
        }
    }
 private static String escapeHtml(String text) {
    return text.replace("&", "&amp;")
               .replace("<", "&lt;")
               .replace(">", "&gt;")
               .replace("\"", "&quot;");
}   
}

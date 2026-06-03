package dao;

import model.Grievance;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GrievanceDAO {

    /**
     * Insert new grievance, return generated ID.
     */
    public int insertGrievance(Grievance g) {
        String sql = "INSERT INTO Grievance (citizen_id, category_id, officer_id, description, status, submitted_date, priority) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, g.getCitizenId());
            ps.setInt(2, g.getCategoryId());
            ps.setInt(3, g.getOfficerId() == 0 ? 1 : g.getOfficerId()); // default officer 1
            ps.setString(4, g.getDescription());
            ps.setString(5, g.getStatus());
            ps.setDate(6, Date.valueOf(g.getSubmittedDate()));
            ps.setInt(7, g.getPriority());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Insert Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Fetch all unresolved grievances with JOIN to Category + Citizen.
     */
    public List<Grievance> getAllActiveGrievances() {
        List<Grievance> list = new ArrayList<>();
        String sql = "SELECT g.*, c.name AS citizen_name, cat.name AS cat_name, cat.weight " +
                     "FROM Grievance g " +
                     "JOIN Citizen c   ON g.citizen_id  = c.id " +
                     "JOIN Category cat ON g.category_id = cat.id " +
                     "WHERE g.status != 'Resolved' " +
                     "ORDER BY g.priority DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Fetch Error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fetch all grievances (including resolved).
     */
    public List<Grievance> getAllGrievances() {
        List<Grievance> list = new ArrayList<>();
        String sql = "SELECT g.*, c.name AS citizen_name, cat.name AS cat_name, cat.weight " +
                     "FROM Grievance g " +
                     "JOIN Citizen c   ON g.citizen_id  = c.id " +
                     "JOIN Category cat ON g.category_id = cat.id " +
                     "ORDER BY g.priority DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Fetch Error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fetch single grievance by ID.
     */
    public Grievance getGrievanceById(int id) {
        String sql = "SELECT g.*, c.name AS citizen_name, cat.name AS cat_name, cat.weight " +
                     "FROM Grievance g " +
                     "JOIN Citizen c   ON g.citizen_id  = c.id " +
                     "JOIN Category cat ON g.category_id = cat.id " +
                     "WHERE g.id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update grievance status.
     */
    public boolean updateStatus(int grievanceId, String newStatus) {
        String sql = "UPDATE Grievance SET status = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, grievanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Update Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update priority score in DB (called after aging recalculation).
     */
    public void updatePriority(int grievanceId, int newPriority) {
        String sql = "UPDATE Grievance SET priority = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, newPriority);
            ps.setInt(2, grievanceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[GrievanceDAO] Priority Update Error: " + e.getMessage());
        }
    }

    /**
     * Analytics: grievance count per category (GROUP BY).
     */
    public List<String[]> getGrievancesPerCategory() {

    List<String[]> data = new ArrayList<>();

    String sql =
            "SELECT cat.name, " +
            "COUNT(g.id) AS total, " +
            "AVG(DATEDIFF(CURDATE(), g.submitted_date)) AS avg_days " +
            "FROM Grievance g " +
            "JOIN Category cat ON g.category_id = cat.id " +
            "GROUP BY cat.name " +
            "ORDER BY total DESC";

    try (Statement stmt = DBConnection.getConnection().createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {

            data.add(new String[]{
                    rs.getString("name"),
                    rs.getString("total"),
                    String.format("%.1f", rs.getDouble("avg_days"))
            });
        }

    } catch (SQLException e) {
        System.out.println("[GrievanceDAO] Analytics Error: "
                + e.getMessage());
    }

    return data;
}
    // ── Helper ──────────────────────────────────────────────

    private Grievance mapRow(ResultSet rs) throws SQLException {
        Grievance g = new Grievance();
        g.setId(rs.getInt("id"));
        g.setCitizenId(rs.getInt("citizen_id"));
        g.setCategoryId(rs.getInt("category_id"));
        g.setOfficerId(rs.getInt("officer_id"));
        g.setDescription(rs.getString("description"));
        g.setStatus(rs.getString("status"));
        g.setSubmittedDate(rs.getDate("submitted_date").toLocalDate());
        g.setPriority(rs.getInt("priority"));
        g.setCitizenName(rs.getString("citizen_name"));
        g.setCategoryName(rs.getString("cat_name"));
        g.setCategoryWeight(rs.getInt("weight"));
        return g;
    }
}

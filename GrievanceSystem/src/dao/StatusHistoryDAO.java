package dao;

import model.StatusHistory;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusHistoryDAO {

    /**
     * Log a new status change for a grievance.
     */
    public void logStatus(int grievanceId, String status, String remarks) {
        String sql = "INSERT INTO StatusHistory (grievance_id, status, remarks) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, grievanceId);
            ps.setString(2, status);
            ps.setString(3, remarks);
            ps.executeUpdate();
            System.out.println("[StatusHistory] Logged: GID=" + grievanceId + " → " + status);
        } catch (SQLException e) {
            System.out.println("[StatusHistoryDAO] Error: " + e.getMessage());
        }
    }

    /**
     * Get full history for one grievance.
     */
    public List<StatusHistory> getHistoryByGrievanceId(int grievanceId) {
        List<StatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM StatusHistory WHERE grievance_id = ? ORDER BY changed_at ASC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, grievanceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StatusHistory sh = new StatusHistory();
                sh.setId(rs.getInt("id"));
                sh.setGrievanceId(rs.getInt("grievance_id"));
                sh.setStatus(rs.getString("status"));
                sh.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
                sh.setRemarks(rs.getString("remarks"));
                list.add(sh);
            }
        } catch (SQLException e) {
            System.out.println("[StatusHistoryDAO] Error: " + e.getMessage());
        }
        return list;
    }
}

package dao;

import model.Officer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfficerDAO {

    public List<Officer> getAllOfficers() {
        List<Officer> list = new ArrayList<>();
        String sql = "SELECT * FROM Officer ORDER BY resolved_count DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Officer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("resolved_count")));
            }
        } catch (SQLException e) {
            System.out.println("[OfficerDAO] Error: " + e.getMessage());
        }
        return list;
    }

    public Officer getOfficerById(int id) {
        String sql = "SELECT * FROM Officer WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Officer(rs.getInt("id"), rs.getString("name"),
                        rs.getString("department"), rs.getInt("resolved_count"));
            }
        } catch (SQLException e) {
            System.out.println("[OfficerDAO] Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Increment resolved count for officer (called when grievance is resolved).
     */
    public void incrementResolvedCount(int officerId) {
        String sql = "UPDATE Officer SET resolved_count = resolved_count + 1 WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, officerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[OfficerDAO] Error: " + e.getMessage());
        }
    }

    /**
     * Top officer leaderboard — most resolved this month.
     */
    public List<Officer> getLeaderboard() {
        List<Officer> list = new ArrayList<>();
        String sql = "SELECT o.id, o.name, o.department, COUNT(g.id) AS resolved " +
                     "FROM Officer o " +
                     "JOIN Grievance g ON g.officer_id = o.id " +
                     "WHERE g.status = 'Resolved' " +
                     "AND MONTH(g.submitted_date) = MONTH(CURDATE()) " +
                     "GROUP BY o.id ORDER BY resolved DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Officer(rs.getInt("id"), rs.getString("name"),
                        rs.getString("department"), rs.getInt("resolved")));
            }
        } catch (SQLException e) {
            System.out.println("[OfficerDAO] Leaderboard Error: " + e.getMessage());
        }
        return list;
    }
}

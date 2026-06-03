package dao;

import model.Citizen;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitizenDAO {

    public int insertCitizen(Citizen c) {
        String sql = "INSERT INTO Citizen (name, contact, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getContact());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.out.println("[CitizenDAO] Error: " + e.getMessage());
        }
        return -1;
    }

    public Citizen getCitizenById(int id) {
        String sql = "SELECT * FROM Citizen WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Citizen(rs.getInt("id"), rs.getString("name"),
                        rs.getString("contact"), rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("[CitizenDAO] Error: " + e.getMessage());
        }
        return null;
    }

    public List<Citizen> getAllCitizens() {
        List<Citizen> list = new ArrayList<>();
        String sql = "SELECT * FROM Citizen";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Citizen(rs.getInt("id"), rs.getString("name"),
                        rs.getString("contact"), rs.getString("email")));
            }
        } catch (SQLException e) {
            System.out.println("[CitizenDAO] Error: " + e.getMessage());
        }
        return list;
    }
}

package dao;

import model.Category;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("weight"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("[CategoryDAO] Error: " + e.getMessage());
        }
        return list;
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM Category WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"), rs.getInt("weight"));
            }
        } catch (SQLException e) {
            System.out.println("[CategoryDAO] Error: " + e.getMessage());
        }
        return null;
    }
}

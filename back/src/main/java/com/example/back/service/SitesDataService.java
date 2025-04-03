package com.example.back.service;

import org.springframework.stereotype.Service;
import java.sql.*;

@Service
public class SitesDataService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/RPS";
    private static final String USER = "root";  // 请修改为你的数据库用户名
    private static final String PASSWORD = "root";  // 请修改为你的数据库密码

    public String getSiteDetailsBySiteId(String SiteId) {
        StringBuilder result = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Database connection established."); // 新增日志
            String selectSQL;
            if (SiteId == null || SiteId.trim().isEmpty()) {
                selectSQL = "SELECT SiteID, Latitude, Longitude, Elevation, Slope, Area_Gages2, Area_GeoSpaFabric FROM sites";
            } else {
                selectSQL = "SELECT SiteID, Latitude, Longitude, Elevation, Slope, Area_Gages2, Area_GeoSpaFabric FROM sites WHERE SiteID = ?";
            }

            try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
                if (SiteId != null && !SiteId.trim().isEmpty()) {
                    stmt.setString(1, SiteId.trim());
                }
                System.out.println(stmt.toString()); // 新增日志

                System.out.println("Statement created and site ID set."); // 新增日志
                System.out.println("Final SQL query: " + selectSQL); // 直接打印最终的 SQL 查询语句
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("Executing query."); // 新增日志
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        String siteId = rs.getString("SiteID");
                        double latitude = rs.getDouble("Latitude");
                        double longitude = rs.getDouble("Longitude");
                        double elevation = rs.getDouble("Elevation");
                        double slope = rs.getDouble("Slope");
                        double areaGages2 = rs.getDouble("Area_Gages2");
                        double areaGeoSpaFabric = rs.getDouble("Area_GeoSpaFabric");

                        result.append(siteId).append(", ")
                              .append(latitude).append(", ")
                              .append(longitude).append(", ")
                              .append(elevation).append(", ")
                              .append(slope).append(", ")
                              .append(areaGages2).append(", ")
                              .append(areaGeoSpaFabric).append("\n");

                        // 打印查询到的数据
//                        System.out.println("SiteID: " + siteId + ", Latitude: " + latitude + ", Longitude: " + longitude + ", Elevation: " + elevation + ", Slope: " + slope + ", Area_Gages2: " + areaGages2 + ", Area_GeoSpaFabric: " + areaGeoSpaFabric);
                    }
                    if (!hasData) {
                        System.out.println("No data found for site ID: " + SiteId); // 新增日志
                    }
                    System.out.println("Query execution completed."); // 新增日志
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while querying database: " + e.getMessage()); // 修改日志
            return "Error occurred while querying database.";
        }
        System.out.println("Returning result: " + result.toString()); // 新增日志
        return result.toString();
    }
}
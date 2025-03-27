package com.example.back.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;



public class SitesdataToSites {
    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/RPS"; // 请确保你的数据库名正确
    private static final String USER = "root"; // 你的 MySQL 用户名
    private static final String PASSWORD = "root"; // 你的 MySQL 密码

    public static void main(String[] args) {
        String filePath = "D:/github_desktop/RPS/runoff_data/camels_attributes_v2.0/camels_topo.txt";
        importData(filePath);
    }

    public static void importData(String filePath) {
        String insertSQL = "INSERT INTO Sites (SiteID, Latitude, Longitude, Elevation, Slope, Area_Gages2, Area_GeoSpaFabric) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader(filePath));
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // 读取文件的第一行（标题行），跳过
            String line = br.readLine();

            // 逐行读取并插入数据库
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length != 7) continue; // 确保数据格式正确

                pstmt.setString(1, values[0]); // SiteID
                pstmt.setDouble(2, Double.parseDouble(values[1])); // Latitude
                pstmt.setDouble(3, Double.parseDouble(values[2])); // Longitude
                pstmt.setDouble(4, Double.parseDouble(values[3])); // Elevation
                pstmt.setDouble(5, Double.parseDouble(values[4])); // Slope
                pstmt.setDouble(6, Double.parseDouble(values[5])); // Area_Gages2
                pstmt.setDouble(7, Double.parseDouble(values[6])); // Area_GeoSpaFabric

                pstmt.executeUpdate();
            }

            System.out.println("数据成功导入到 Sites 表！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


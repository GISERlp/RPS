package com.example.back.util;

import java.io.*;
import java.sql.*;
import java.util.*;

public class RevierdataToSitesrevier {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/RPS";
    private static final String USER = "root";  // 请修改为你的数据库用户名
    private static final String PASSWORD = "root";  // 请修改为你的数据库密码
    private static final String BASE_DIR = "D:/github_desktop/RPS/runoff_data/usgs_streamflow"; // 你的数据文件夹

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            processFolders(conn, BASE_DIR);
            System.out.println("数据导入完成！");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFolders(Connection conn, String baseDir) throws IOException, SQLException {
        File rootDir = new File(baseDir);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.err.println("数据文件夹不存在: " + baseDir);
            return;
        }

        for (File regionFolder : Objects.requireNonNull(rootDir.listFiles(File::isDirectory))) {
            String regionId = regionFolder.getName(); // 子文件夹名称作为 regionID
            System.out.println("处理区域: " + regionId);
            processRegion(conn, regionFolder, regionId);
        }
    }

    private static void processRegion(Connection conn, File regionFolder, String regionId) throws IOException, SQLException {
        for (File file : Objects.requireNonNull(regionFolder.listFiles((dir, name) -> name.endsWith("_streamflow_qc.txt")))) {
            String siteId = file.getName().split("_")[0]; // 站点ID
            System.out.println("导入站点: " + siteId);
            importSiteData(conn, file, siteId, regionId);
        }
    }

    private static void importSiteData(Connection conn, File file, String siteId, String regionId) throws IOException, SQLException {
        String insertRevierSQL = "INSERT INTO SitesRevier (SiteID, RecordDate, Streamflow, QualityFlag, RegionID) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE Streamflow = VALUES(Streamflow), QualityFlag = VALUES(QualityFlag);";

        try (// 删除: PreparedStatement siteStmt = conn.prepareStatement(insertSiteSQL);
             PreparedStatement revierStmt = conn.prepareStatement(insertRevierSQL);
             BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // 读取流量数据
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+"); // 以空格拆分
                if (parts.length < 5) continue;

                String date = parts[1] + "-" + parts[2] + "-" + parts[3]; // YYYY-MM-DD
                double streamflow = Double.parseDouble(parts[4]); // 流量
                String qualityFlag = parts[5]; // 质量标志

                revierStmt.setString(1, siteId);
                revierStmt.setString(2, date);
                revierStmt.setDouble(3, streamflow);
                revierStmt.setString(4, qualityFlag);
                revierStmt.setString(5, regionId);
                revierStmt.addBatch();
            }
            revierStmt.executeBatch();
        }
    }
}


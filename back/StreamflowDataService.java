
public String getStreamflowDataBySiteId(String SiteId) {
    // 这里实现具体的查询逻辑
    // 例如从数据库中查询径流数据
    StringBuilder result = new StringBuilder();
    System.out.println("Starting to fetch streamflow data for site ID: " + SiteId); // 新增日志
    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
        System.out.println("Database connection established."); // 新增日志
        String selectSQL = "SELECT RecordDate, Streamflow, QualityFlag, RegionID FROM sitesrevier WHERE SiteID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, SiteId);
            System.out.println("PreparedStatement created and site ID set."); // 新增日志
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Executing query."); // 新增日志
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    String recordDate = rs.getString("RecordDate");
                    double streamflow = rs.getDouble("Streamflow");
                    String qualityFlag = rs.getString("QualityFlag");
                    String regionId = rs.getString("RegionID"); // 新增获取RegionID
                    result.append(recordDate).append(", ")
                          .append(streamflow).append(", ")
                          .append(qualityFlag).append(", ")
                          .append(regionId).append("\n");
                    // 打印查询到的数据
                    System.out.println("RecordDate: " + recordDate + ", Streamflow: " + streamflow + ", QualityFlag: " + qualityFlag + ", RegionID: " + regionId);
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
    System.out.println("Final query result: " + result.toString()); // 添加打印最终查询结果的语句
    return result.toString();
}
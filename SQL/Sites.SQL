CREATE DATABASE RPS;
USE RPS;

-- 修改 Sites 表，增加 gauge 相关字段
CREATE TABLE Sites (
    SiteID VARCHAR(10) PRIMARY KEY,  -- 站点编号，例如 01013500
    Latitude DECIMAL(9,6),           -- 站点纬度
    Longitude DECIMAL(9,6),          -- 站点经度
    Elevation DECIMAL(7,2),          -- 站点平均高程
    Slope DECIMAL(10,6),              -- 站点平均坡度
    Area_Gages2 DECIMAL(7,2),        -- GAGES2 流域面积
    Area_GeoSpaFabric DECIMAL(7,2),  -- Geospatial Fabric 流域面积
);

-- 创建 SitesRevier 表
CREATE TABLE SitesRevier (
    SiteID VARCHAR(10),      -- 站点编号，外键关联 Sites 表
    RecordDate DATE,         -- 记录日期，例如 1980-01-01
    Streamflow DECIMAL(10,2),-- 流量数据，例如 655.00
    QualityFlag CHAR(2),     -- 质量标志，例如 'A' 或 'Ae'
    RegionID VARCHAR(20),    -- 记录所属区域编号
    PRIMARY KEY (SiteID, RecordDate)
);

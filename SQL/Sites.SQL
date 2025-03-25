CREATE DATABASE RPS;
USE RPS;

-- 创建 Sites 表
CREATE TABLE Sites (
    SiteID VARCHAR(10) PRIMARY KEY,  -- 站点编号，例如 01013500
    FileName VARCHAR(255),           -- 文件名，例如 01013500_streamflow_qc.txt
    LastModified DATETIME,           -- 文件的最后修改时间
    Latitude DECIMAL(9,6),           -- 站点纬度
    Longitude DECIMAL(9,6),          -- 站点经度
    RegionID VARCHAR(20)             -- 站点所属区域编号
);

-- 创建 SitesRevier 表
CREATE TABLE SitesRevier (
    SiteID VARCHAR(10),      -- 站点编号，外键关联 Sites 表
    RecordDate DATE,         -- 记录日期，例如 1980-01-01
    Streamflow DECIMAL(10,2),-- 流量数据，例如 655.00
    QualityFlag CHAR(1),     -- 质量标志，例如 'A'
    RegionID VARCHAR(20),    -- 记录所属区域编号
    PRIMARY KEY (SiteID, RecordDate),
    FOREIGN KEY (SiteID) REFERENCES Sites(SiteID)
);

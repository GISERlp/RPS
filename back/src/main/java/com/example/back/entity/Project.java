package com.example.back.entity;

import lombok.Data;
import java.util.Date;
@Data
public class Project {
    private Integer projectId;
    private String userName;
    private String projectName;
    private String siteId;
    private String model;
    private Date startDate;
    private Date endDate;
    private String description;
    private Date created_at;
    private Date updated_at;



}
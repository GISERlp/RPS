package com.example.back.service;

import com.example.back.mapper.ProjectMapper;
import com.example.back.entity.Project;
import com.example.back.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectMapper projectMapper;

    public boolean existsByUserAndProjectName(String userName, String projectName) {
        return projectMapper.existsByUserAndProjectName(userName, projectName);
    }

    public Result createProject(Project project) {
        // 设置 createdAt 和 updatedAt 字段
        project.setCreated_at(new Date());
        project.setUpdated_at(new Date());
        projectMapper.createProject(project.getUserName(), project.getProjectName(), project.getSiteId(),
                project.getModel(), project.getStartDate(), project.getEndDate(), project.getDescription(), project.getCreated_at(), project.getUpdated_at());
        return Result.success("项目创建成功");
    }

    public List<Project> getProjectsByUserAndProjectName(String user, String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return projectMapper.findByUser(user);
        } else {
            return projectMapper.findByUserAndProjectName(user, projectName);
        }
    }

    public List<Project> getProjectsByUser(String user) {
        return projectMapper.findByUser(user);
    }
}
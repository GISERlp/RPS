package com.example.back.controller;

import com.example.back.dto.Result;
import com.example.back.entity.Project;
import com.example.back.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @RequestMapping(value = "/create")
    public Result createProject(@RequestBody Project project) {
        if(project.getProjectName() == null || project.getProjectName().isEmpty()) {
            return Result.error("项目名不能为空！");
        }
        // 检查项目是否已存在
        if (projectService.existsByUserAndProjectName(project.getUserName(), project.getProjectName())) {
            return Result.error("项目已经存在！");
        }

        projectService.createProject(project);
        return Result.success("项目创建成功！");
    }

    @GetMapping("/getByUserNameAndProjectName")
    public List<Project> getProjectsByUserAndProjectName(@RequestParam String userName, @RequestParam(required = false) String projectName) {
        List<Project> projects = projectService.getProjectsByUserAndProjectName(userName, projectName);
        return projects;
    }
}
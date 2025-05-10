package com.example.back.controller;


import com.example.back.dto.Result;
import com.example.back.entity.Project;
import com.example.back.service.ProjectService;
import jdk.internal.instrumentation.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



@RestController
@RequestMapping("/project")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    ProjectService projectService;
    @Value("${project.root-directory}")
    private String projectRootDirectory;

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

    //    删除项目
    @DeleteMapping("/delete")
    public Result deleteProject(@RequestParam String userName, @RequestParam String projectName) {
        // 检查项目是否存在
        if (!projectService.existsByUserAndProjectName(userName, projectName)) {
            return Result.error("项目不存在！");
        }
        projectService.deleteProject(userName, projectName);
        return Result.success("项目删除成功！");
    }

    @GetMapping("/getByUserNameAndProjectName")
    public List<Project> getProjectsByUserAndProjectName(@RequestParam String userName, @RequestParam(required = false) String projectName) {
        List<Project> projects = projectService.getProjectsByUserAndProjectName(userName, projectName);
        return projects;
    }

    @PostMapping("/createUserWorkspace")
    public Result createUserWorkspace(@RequestParam String userName, @RequestParam String projectName) {
        if(projectService.createUserWorkspace(userName, projectName)) {
        return Result.success("用户工作空间创建成功！");
    }
        return Result.error("用户工作空间创建失败！");
    }
    @PostMapping("/runWorkflow")
    public Result runWorkflow(@RequestParam String userName, @RequestParam String projectName) {
        String workflowpath = projectRootDirectory+ "/" + userName + "/" + projectName + "/workspace/workflow.txt";
        // 可以在这里添加一些检查逻辑，例如检查项目是否存在等
        return projectService.runWorkflow(workflowpath);
    }

    @GetMapping("/getFileData")
    public List<Double> getFileData(@RequestParam String userName, @RequestParam String projectName, @RequestParam String fileName) {
        String filePath = projectRootDirectory + "/" + userName + "/" + projectName + "/result/" + fileName;
        return projectService.getFileData(filePath);
    }

    @PutMapping("/updateProjectConfig")
    public Result updateProjectConfig(@RequestBody Project project) {
        if(!projectService.existsByUserAndProjectName(project.getUserName(), project.getProjectName())){
            return Result.error("项目不存在，请先创建项目!");
        }
        projectService.updateProjectConfig(project);

        return Result.success("项目配置更新成功！");
    }

    @GetMapping("/downloadProjectFile")
    public ResponseEntity<org.springframework.core.io.Resource> downloadProjectFile(
            @RequestParam String userName,
            @RequestParam String projectName,
            @RequestParam String fileName) {

        // ----------------------------------------------------------------------
        // 1. 构建请求文件的绝对路径
        // ----------------------------------------------------------------------
        // 通过组合目录路径来确定文件位置
        //    projectRootDirectory : 项目根目录，例如 /data/projects（通常通过 @Value 或配置文件注入）
        //    userName             : 请求参数，用户工作目录
        //    projectName          : 项目名称，指定该用户的子目录
        //    "result"             : 存放最终结果的文件夹
        //    fileName             : 请求下载的具体文件名（例如 report.pdf）
        // 使用 java.nio.file.Path 来处理文件路径，保证跨平台兼容性。
        String filePath = projectRootDirectory + "/" + userName + "/" + projectName + "/result/" + fileName;
        Path path = Paths.get(filePath);

        // 可选：输出调试信息，检查路径是否正确（实际开发中应使用日志框架）
        System.out.println("Resolved download path: " + filePath);

        // ----------------------------------------------------------------------
        // 2. 判断文件是否存在
        // ----------------------------------------------------------------------
        if (!Files.exists(path)) {
            // 文件不存在 -> 返回 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            // ------------------------------------------------------------------
            // 3. 使用 Spring Resource 来包装文件路径，以便高效地流式传输文件
            // ------------------------------------------------------------------
            org.springframework.core.io.UrlResource resource = new UrlResource(path.toUri());

            // 判断文件是否存在且可读，如果不可读，返回 404
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 文件存在但不可读 -> 404
            }

            // ------------------------------------------------------------------
            // 4. 文件存在且可读取 -> 返回 200 OK 和文件内容
            // ------------------------------------------------------------------
            return ResponseEntity.ok()
                    // 通知浏览器文件作为附件下载，并建议使用原文件名
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    // MIME 类型为 OCTET_STREAM，强制下载（当文件类型无法确定时）
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    // Spring 自动处理流式下载
                    .body(resource);

        } catch (Exception ex) {
            // ------------------------------------------------------------------
            // 5. 处理异常（例如安全异常、IO 错误等）
            // ------------------------------------------------------------------
            // 实际开发中应记录详细异常信息（logger.error("下载失败", ex)）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 发生内部错误 -> 500
        }
    }





}
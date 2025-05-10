package com.example.back.service;

import com.example.back.mapper.ProjectMapper;
import com.example.back.entity.Project;
import com.example.back.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.List;


@Service
public class ProjectService {
    @Autowired
    ProjectMapper projectMapper;
    @Value("${project.root-directory}")
    private String projectRootDirectory;

    @Value("${project.workspace-directory}")
    private String workspaceDirectory;

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

    public void deleteProject(String userName, String projectName) {
        projectMapper.deleteByUserAndProject(userName, projectName);

    }

    public boolean createUserWorkspace(String userName, String projectName) {
        List<Project> projectList = projectMapper.findByUserAndProjectName(userName,projectName);

        if (projectList.isEmpty()) {
            return false;
        }

        Project project = projectList.get(0);
        String basePath = projectRootDirectory + "/" + project.getUserName() + "/" + project.getProjectName();

        // 定义各类子目录路径
        String dataPath = basePath + "/data";
        String workspacePath = basePath + "/workspace";
        String modelPath = basePath + "/model";
        String resultPath = basePath + "/result";
        String logPath = basePath + "/log";
        String configPath = basePath + "/config";

        // 创建必要的文件夹
        createDirectoryIfNotExists(basePath);
        createDirectoryIfNotExists(dataPath);
        createDirectoryIfNotExists(workspacePath);
        createDirectoryIfNotExists(modelPath);
        createDirectoryIfNotExists(resultPath);
        createDirectoryIfNotExists(logPath);
        createDirectoryIfNotExists(configPath);

        writeWorkflowFile(project);



        // 创建配置文件
        String configFilePath = configPath + "/config.txt";
        File configFile = new File(configFilePath);

        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("项目名称: " + project.getProjectName() + "\n");
            writer.write("用户名称: " + project.getUserName() + "\n");
            writer.write("站点ID: " + project.getSiteId() + "\n");
            writer.write("模型: " + project.getModel() + "\n");
            writer.write("开始日期: " + project.getStartDate() + "\n");
            writer.write("结束日期: " + project.getEndDate() + "\n");
            writer.write("描述: " + project.getDescription() + "\n");
            writer.write("创建时间: " + project.getCreated_at() + "\n");
            writer.write("更新时间: " + project.getUpdated_at() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("创建用户工作空间失败", e);
        }

        return true;
    }

    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public Result runWorkflow(String filepath) {
        String command = readCommandFromFile(filepath);
        if (command == null) {
            return Result.error("无法读取"+filepath+"文件中的命令！");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.directory(new File(workspaceDirectory));

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return Result.success("Workflow 执行成功！");
            } else {
                return Result.error("Workflow 执行失败！");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Result.error("Workflow 执行异常：" + e.getMessage());
        }
    }

    private String readCommandFromFile(String filePath) {
        StringBuilder commandBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                commandBuilder.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return commandBuilder.toString().trim();
    }

    public List<Double> getFileData(String filePath) {
        List<String> data = readDataFromFile(filePath);
        if (data == null) {
            throw new RuntimeException("无法读取文件数据！");
        }
        // 将每一行数据进一步拆分为单个元素，并过滤掉空字符串和非数字字符串，转换为Double
        List<Double> flatData = new ArrayList<>();
        for (String line : data) {
            String[] elements = line.split(" ");
            for (String element : elements) {
                if (!element.isEmpty()) {
                    try {
                        double value = Double.parseDouble(element);
                        flatData.add(value);
                    } catch (NumberFormatException e) {
                        // 忽略非数字字符串
                    }
                }
            }
        }
        return flatData;
    }

    private List<String> readDataFromFile(String filePath) {
        List<String> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 去除多余的空格和换行符
                line = line.trim();
                if (!line.isEmpty()) { // 忽略空行
                    // 如果当前行包含有效数据，则添加到列表中
                    dataList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return dataList;
    }

    public void updateProjectConfig(Project project) {
        projectMapper.updateProjectConfig(project.getUserName(), project.getProjectName(), project.getSiteId(),project.getModel(), project.getStartDate(), project.getEndDate(), project.getDescription(),new Date());
        System.out.println("更新成功");
    }

    public void writeWorkflowFile(Project project) {
        String projectFilePath = projectRootDirectory + "/" + project.getUserName() + "/" + project.getProjectName();
        String workflowFilePath = projectFilePath + "/workspace/workflow.txt";

        // 从 Project 对象中提取属性
        String siteId = project.getSiteId();
        String projectName = project.getProjectName();

        // 构建命令字符串
        String command1 = String.format("python workflow.py %s %s/result", siteId, projectFilePath);
        System.out.println(command1);

        // 要写入文件的命令列表
        List<String> commands = new ArrayList<>();
        commands.add(command1);
        // 可以继续添加更多的命令
        // commands.add("python another_script.py arg1 arg2");
        // commands.add("some_other_command");

        // 写入文件
        try (FileWriter writer = new FileWriter(workflowFilePath)) {
            for (String command : commands) {
                writer.write(command + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("写入 workflow 文件失败", e);
        }
    }


}
<template>
  <div>
    <el-container style="justify-content: center">
      <el-header class="projectHeader">
        <div class="custom-style">
          <!-- 项目流程选项 -->
          <el-segmented
            v-model="projectProcess"
            :options="projectProcessOptions"
            size="small"
            @change="onChangeElMain(projectProcess)"
          />
        </div>
      </el-header>

      <!-- 项目主体部分 -->
      <el-main class="projectMain">
        <!-- <ProjectMain :Project="Project" /> -->
        <component
          :is="Feature"
          :Project="Project"
          @viewResult="projectProcess = '运行结果'"
        />
      </el-main>

      <!-- 底部按钮区域 -->
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-left: 40px;
          margin-right: 40px;
        "
      >
        <el-button type="primary" @click="onSave">保存</el-button>
        <el-button @click="onClose">关闭</el-button>
      </div>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
/** ----------------------------- 引入模块 ----------------------------- */
import { onMounted, ref, reactive, computed } from "vue";
import axios from "axios";
import { h } from "vue";
import { ElNotification } from "element-plus";
/** ----------------------------- 引入子组件 ----------------------------- */
import ProjectMain from "../ProjectManagement/ProjectMain.vue";
import ProjectEdit from "../ProjectManagement/ProjectEdit.vue";
import RunProject from "../ProjectManagement/RunProject.vue";
import ProjectResult from "../ProjectManagement/ProjectResult.vue";

/** ----------------------------- 定义组件 ----------------------------- */
const components = {
  ProjectMain,
  ProjectEdit,
  RunProject,
  ProjectResult,
};

/** ----------------------------- 变量定义区 ----------------------------- */

// 项目流程当前状态
const projectProcess = ref("项目一览");

// 项目流程选项
const projectProcessOptions = ["项目一览", "项目配置", "项目运行", "运行结果"];
// 流程对应的组件
const processComponentMap: Record<string, any> = {
  项目一览: ProjectMain,
  项目配置: ProjectEdit,
  项目运行: RunProject,
  运行结果: ProjectResult,
};

// 当前显示的组件
const Feature = computed(() => processComponentMap[projectProcess.value]);

// 定义传入的 props
const props = defineProps({
  currentProject: {
    type: String,
    required: true,
  },
  username: {
    type: String,
    required: true,
  },
});

// 项目数据响应式对象
const Project = reactive({
  user: "",
  projectName: "",
  siteId: "",
  startDate: "",
  endDate: "",
  model: "",
  desc: "",
});

// 定义 emits
const emit = defineEmits(["close"]);

/** ----------------------------- 函数定义区 ----------------------------- */

/**
 * 处理项目流程变化
 */
const onChangeElMain = (selectedProcess: string) => {
  console.log("项目流程切换到:", selectedProcess);
};

//关闭事件处理（触发外部 close 事件）
const onClose = () => {
  ElNotification({
    title: "工作消息",
    message: h("i", { style: "color: teal" }, "项目已关闭！"),
  });
  emit("close");
};
//保存更新项目事件处理
const onSave = async () => {
  try {
    const response = await axios.put(
      "http://localhost:8080/project/updateProjectConfig",
      {
        userName: Project.user,
        projectName: Project.projectName,
        siteId: Project.siteId,
        model: Project.model,
        startDate: Project.startDate,
        endDate: Project.endDate,
        description: Project.desc,
      }
    );

    if (response.status === 200) {
      console.log("项目数据已保存:", Project);
      ElNotification({
        title: "工作消息",
        message: h("i", { style: "color: teal" }, "项目数据已保存！"),
      });
    } else {
      console.error("保存项目数据失败:", response);
      ElNotification({
        title: "错误",
        message: h("i", { style: "color: red" }, "保存项目数据失败！"),
      });
    }
  } catch (error) {
    console.error("保存项目数据时发生错误:", error);
    ElNotification({
      title: "错误",
      message: h("i", { style: "color: red" }, "保存项目数据时发生错误！"),
    });
  }
};

/**
 * 根据用户名与项目名获取项目信息
 */
async function fetchProjectData(username: string, projectName: string) {
  try {
    const response = await axios.get(
      `http://localhost:8080/project/getByUserNameAndProjectName`,
      {
        params: {
          userName: username,
          projectName: projectName,
        },
      }
    );

    if (response.data && response.data.length > 0) {
      return response.data[0];
    } else {
      console.warn("未找到相关项目数据");
      return null;
    }
  } catch (error) {
    console.error("获取项目数据失败:", error);
    return null;
  }
}

/**
 * 页面加载时获取项目数据
 */
onMounted(async () => {
  // 设置初始用户与项目名
  Project.user = props.username;
  Project.projectName = props.currentProject;

  // 获取项目信息
  const projectData = await fetchProjectData(
    props.username,
    props.currentProject
  );

  // 成功获取后赋值到 Project 对象
  if (projectData) {
    Project.siteId = projectData.siteId;
    Project.startDate = projectData.startDate;
    Project.endDate = projectData.endDate;
    Project.model = projectData.model;
    Project.desc = projectData.description;
  } else {
    console.warn("项目不存在或数据获取失败");
  }
});
</script>

<style scoped>
/* 顶部头部样式 */
.projectHeader {
  height: 20px;
  width: 660px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 13px;
}

/* 主体内容样式 */
.projectMain {
  margin-top: 20px;
  padding: 10px;
  height: 360px;
  width: 660px;
  font-size: 18px;
  text-align: center;
  justify-content: center;
}

/* 底部区域样式（暂未使用） */
.projectFooter {
  height: 25px;
  background-color: #f0f0f0;
  text-align: center;
  font-size: 13px;
}

/* Segmented 控件样式定制 */
.custom-style .el-segmented {
  background-color: #fff;
  --el-segmented-item-selected-color: var(--el-text-color-primary);
  --el-segmented-item-selected-bg-color: rgb(235.9, 245.3, 255);
  --el-border-radius-base: 16px;
  width: 400px;
}
</style>

<template>
  <div>
    <el-row>
      <el-col :span="20">
        <el-card class="box-card" shadow="hover">
          <el-table
            :data="projectInfo"
            style="width: 100%; height: 220px; font-size: 12px"
          >
            <el-table-column prop="label" label="属性" width="150" />
            <el-table-column
              prop="value"
              label="值"
              :show-overflow-tooltip="true"
            />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="4" shadow="hover">
        <!-- 运行按钮和状态 -->
        <div style="margin-left: 10px; margin-top: 30px">
          <el-button
            type="primary"
            @click="runModel"
            :loading="running"
            :disabled="running"
          >
            {{ running ? "运行中..." : "开始运行" }}
          </el-button>
          <el-button
            v-if="status === '运行完成'"
            type="success"
            @click="goToResult"
            style="margin-top: 100px; margin-left: -5px"
          >
            查看结果
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 动态进度条和说明 -->
    <el-card
      class="box-card"
      shadow="hover"
      style="height: 80px; margin-top: 10px; padding: 10px"
    >
      <div v-if="running">
        <el-progress
          :percentage="progress"
          :stroke-width="12"
          striped
          striped-flow
        />
        <div style="font-size: 12px; color: #a0cfff">{{ currentLog }}</div>
      </div>
      <div v-else-if="status" style="margin-top: -10px">
        <el-tag :type="statusType" style="font-size: 14px">{{ status }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "ProjectRun",
  props: {
    Project: {
      type: Object,
      required: true,
      default: () => ({
        user: "",
        projectName: "",
        siteId: "",
        startDate: "",
        endDate: "",
        model: "",
        desc: "",
      }),
    },
  },
  data() {
    return {
      config: {
        projectUser: this.Project.user || "",
        projectName: this.Project.projectName || "",
        stationId: this.Project.siteId || "",
        startDate: this.Project.startDate || "",
        endDate: this.Project.endDate || "",
        modelType: this.Project.model || "",
      },
      running: false,
      currentLog: "",
      progress: 0,
      status: "",
    };
  },
  computed: {
    statusType() {
      switch (this.status) {
        case "运行中":
          return "info";
        case "运行完成":
          return "success";
        case "运行失败":
          return "danger";
        default:
          return "";
      }
    },
    projectInfo() {
      return [
        { label: "项目名称", value: this.config.projectName },
        { label: "站点编号", value: this.config.stationId },
        { label: "起始时间", value: this.config.startDate },
        { label: "结束时间", value: this.config.endDate },
        { label: "模型类型", value: this.config.modelType },
      ];
    },
  },
  methods: {
    async runModel() {
      this.running = true;
      this.status = "运行中";
      this.progress = 0;
      this.currentLog = "初始化中...";

      const fakeLogs = [
        { text: "[初始化] 模型参数校验中...", percent: 5 },
        { text: "[准备] 加载输入数据...", percent: 15 },
        { text: "[执行] 模型运行中（25%）", percent: 25 },
        { text: "[执行] 模型运行中（50%）", percent: 50 },
        { text: "[执行] 模型运行中（75%）", percent: 75 },
        { text: "[完成] 模型运行完成，结果已保存。", percent: 100 },
      ];

      try {
        const res1 = await fetch(
          `http://localhost:8080/project/createUserWorkspace?userName=${this.config.projectUser}&projectName=${this.config.projectName}`,
          { method: "POST" }
        );
        if (!res1.ok) throw new Error("创建项目文件失败");

        const res2 = await fetch(
          `http://localhost:8080/project/runWorkflow?userName=${this.config.projectUser}&projectName=${this.config.projectName}`,
          { method: "POST" }
        );
        if (!res2.ok) throw new Error("执行工作流失败");

        let step = 0;
        const logInterval = setInterval(() => {
          if (step < fakeLogs.length) {
            this.currentLog = fakeLogs[step].text;
            this.progress = fakeLogs[step].percent;
            step++;
          } else {
            clearInterval(logInterval);
            this.status = "运行完成";
            this.running = false;
          }
        }, 1000);
      } catch (error) {
        this.currentLog = "[错误] " + error.message;
        this.status = "运行失败";
        this.running = false;
      }
    },
    goToResult() {
      this.$emit("viewResult");
      this.$message.success("跳转到运行结果页面");
      console.log(this.$parent.projectProcess);
    },
  },
};
</script>

<style scoped>
.box-card {
  border-radius: 8px;
  height: 250px;
}
</style>

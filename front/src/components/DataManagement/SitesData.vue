<template>
  <div class="sitedata">
    <!-- 站点数据查询表单和图表 -->
    <el-form :inline="true" :model="formInline" class="demo-form-inline">
      <div class="form-chart-container">
        <!-- 查询表单区域 -->
        <div class="form-section">
          <el-form-item label="所在区域">
            <el-select
              v-model="formInline.region"
              placeholder="区域站点"
              clearable
            >
              <el-option label="南达科塔州" value="南达科塔州" />
              <el-option label="北达科塔州" value="北达科塔州" />
            </el-select>
          </el-form-item>
          <el-form-item label="站点列表">
            <el-select
              v-model="formInline.siteId"
              placeholder="站点列表"
              clearable
            >
              <el-option
                v-for="site in this.siteList"
                :key="site.value"
                :label="site.label"
                :value="site.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="时间起点">
            <el-date-picker
              v-model="formInline.startDate"
              type="date"
              placeholder="选择日期"
              clearable
            />
          </el-form-item>

          <el-form-item label="时间终点">
            <el-date-picker
              v-model="formInline.endDate"
              type="date"
              placeholder="选择日期"
              clearable
            />
          </el-form-item>

          <el-form-item label="站点编号">
            <el-input
              v-model="formInline.siteId"
              placeholder="站点编号"
              clearable
            />
          </el-form-item>
          <!-- 按钮区域 -->
          <div class="button-container">
            <el-button type="primary" @click="onSubmit">查询</el-button>
            <el-button type="primary" @click="onClose">关闭</el-button>
          </div>
        </div>

        <!-- 降水流量图表 -->
        <div class="chart-container">
          <RainfallFlowChart :runoff-data="RunoffData" style="height: 360px" />
        </div>
      </div>
    </el-form>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";
import RainfallFlowChart from "@/views/RainfallFlowChart.vue"; // 引入图表组件
import { h } from "vue";
import { ElNotification } from "element-plus";
export default {
  name: "SitesData",
  components: {
    RainfallFlowChart, // 注册图表组件
  },
  data() {
    return {
      // 表单数据
      formInline: {
        siteId: "",
        region: "",
        startDate: "",
        endDate: "",
      },
      siteList: [], // 存储站点列表
      RunoffData: [], // 存储流量数据
    };
  },
  mounted() {
    this.getSitesData(); // 组件挂载时获取站点数据
  },
  methods: {
    //获取所有站点
    async getSitesData() {
      try {
        const response = await axios.get(
          "http://localhost:8080/sitesdata?siteId="
        );
        if (typeof response.data === "string") {
          // 按行分割数据并提取每行的第一个值作为 id
          this.siteList = response.data.split("\n").map((line) => {
            const [id] = line.split(","); // 提取每行的第一个值
            return { label: id.trim(), value: id.trim() }; // 去除多余空格
          });
        } else {
          console.error("站点数据格式错误");
        }
      } catch (error) {
        console.error("获取站点数据失败:", error);
      }
    },
    // 关闭窗口
    onClose() {
      console.log("关闭弹窗");
      this.$emit("close");
    },

    // 查询站点径流数据
    async onSubmit() {
      const { siteId, region, startDate, endDate } = this.formInline;

      if (!siteId) {
        ElNotification({
          title: "工作消息",
          message: h("i", { style: "color: teal" }, "站点编号不能为空!"),
        });
        console.error("站点编号不能为空");
        return;
      }

      const formattedStartDate = startDate
        ? dayjs(startDate).format("YYYY-MM-DD")
        : "";
      const formattedEndDate = endDate
        ? dayjs(endDate).format("YYYY-MM-DD")
        : "";

      try {
        ElNotification({
          title: "工作消息",
          message: h("i", { style: "color: teal" }, "查询到站点数据!"),
        });
        const response = await axios.get("http://localhost:8080/streamflow", {
          params: {
            siteId,
            region,
            startDate: formattedStartDate,
            endDate: formattedEndDate,
          },
        });
        console.log("查询结果:", response.data);

        // 解析数据格式
        if (typeof response.data === "string") {
          this.RunoffData = response.data.split("\n").map((line) => {
            const [date, value] = line.split(", ");
            return { date, value: parseFloat(value) };
          });
        } else if (Array.isArray(response.data)) {
          this.RunoffData = response.data.map(({ date, value }) => ({
            date,
            value: parseFloat(value),
          }));
        } else {
          ElNotification({
            title: "工作消息",
            message: h("i", { style: "color: teal" }, "查询失败!"),
          });
          console.error("返回数据格式错误");
        }
      } catch (error) {
        ElNotification({
          title: "工作消息",
          message: h("i", { style: "color: teal" }, "查询失败!"),
        });
        console.error("查询失败:", error);
      }
    },
  },
};
</script>

<style scoped>
/* 组件整体样式 */
.sitedata {
  width: 100%;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
}

/* 让表单和图表在同一行展示 */
.form-chart-container {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  width: 100%;
  gap: 20px;
}

/* 查询表单部分 */
.form-section {
  height: 360px !important;
  flex: 1; /* 让表单占据 50% */
  min-width: 300px;
  display: flex;
  flex-wrap: wrap;
  background-color: #f5f5f5; /* 添加灰色背景 */
  padding: 15px; /* 增加内边距 */
  border-radius: 8px; /* 圆角 */
  margin-top: 5px;
}

/* 让表单项两列排列 */
.el-form-item {
  flex: 1 1 45%; /* 让每个输入项占 45%，形成两列 */
  min-width: 150px;
}

/* 图表区域 */
.chart-container {
  height: 360px !important;
  flex: 1; /* 让图表占据 50% */
  display: flex;
  justify-content: center;
  min-width: 500px;
  background-color: #f5f5f5; /* 添加灰色背景 */
  border-radius: 8px; /* 圆角 */
}

/* 按钮区域 */
.button-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-top: 0px;
}

/* 确保图表尺寸适应 */
.chart-container :deep(canvas) {
  max-width: 100%;
  height: auto;
}
</style>

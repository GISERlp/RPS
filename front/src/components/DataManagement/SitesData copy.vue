<template>
  <div class="Sitedata1">
    <el-form
      :inline="true"
      :model="formInline"
      class="demo-form-inline"
      style="
        display: flex;
        align-items: center;
        margin-top: 20px;
        margin-left: 10px;
      "
    >
      <div style="width: 50%">
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
      </div>
      <div class="chart-container">
        <div class="chart"></div>
      </div>
    </el-form>
    <div>
      <el-form-item
        style="
          display: flex;
          justify-content: space-between;
          width: 100%;
          margin-top: 30px;
        "
      >
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="onClose" style="margin-left: auto"
          >关闭</el-button
        >
      </el-form-item>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import * as echarts from "echarts";
import dayjs from "dayjs";

export default {
  name: "SitesData",
  data() {
    return {
      formInline: {
        siteId: "",
        region: "",
        startDate: "",
        endDate: "",
      },
      RunoffData: [],
      chartInstance: null, // 存储 ECharts 实例
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart(); // 初始化图表
      window.addEventListener("resize", this.resizeChart); // 监听窗口大小变化
    });
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeChart);
    if (this.chartInstance) {
      this.chartInstance.dispose(); // 释放 ECharts 资源
      this.chartInstance = null;
    }
  },
  methods: {
    onClose() {
      console.log("关闭弹窗");
      this.$emit("close");
    },
    async onSubmit() {
      const { siteId, region, startDate, endDate } = this.formInline;

      if (!siteId) {
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
        const response = await axios.get("http://localhost:8080/streamflow", {
          params: {
            siteId,
            region,
            startDate: formattedStartDate,
            endDate: formattedEndDate,
          },
        });

        console.log("查询结果:", response.data);

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
          console.error("返回数据格式错误");
          return;
        }

        this.updateChart(); // 更新图表数据
      } catch (error) {
        console.error("查询失败:", error);
      }
    },
    initChart() {
      this.$nextTick(() => {
        const chartDom = this.$el.querySelector(".chart");
        if (!chartDom) {
          console.error("未找到 .chart 容器");
          return;
        }

        this.chartInstance = echarts.init(chartDom);

        this.chartInstance.setOption({
          title: { text: "Flow ", left: "center" },
          grid: { bottom: 80 },
          toolbox: {
            feature: {
              dataZoom: { yAxisIndex: "none" },
              restore: {},
              saveAsImage: {},
            },
          },
          tooltip: {
            trigger: "axis",
            axisPointer: { type: "cross", animation: false },
          },
          legend: { data: ["Flow"], left: 10 },
          dataZoom: [
            { show: true, realtime: true, start: 65, end: 85 },
            { type: "inside", realtime: true, start: 65, end: 85 },
          ],
          xAxis: [{ type: "category", boundaryGap: false, data: [] }],
          yAxis: [{ name: "Flow(m³/s)", type: "value" }],
          series: [{ name: "Flow", type: "line", data: [], areaStyle: {} }],
        });
      });
    },
    updateChart() {
      if (!this.chartInstance) {
        console.error("图表实例未初始化");
        return;
      }

      const dates = this.RunoffData.map((item) => item.date);
      const flowData = this.RunoffData.map((item) => item.value);

      this.chartInstance.setOption(
        {
          xAxis: [{ type: "category", data: dates }],
          series: [
            { name: "Flow", type: "line", data: flowData, areaStyle: {} },
          ],
        }
        // { notMerge: true } // 关键：不合并旧数据，直接覆盖
      );
    },
    resizeChart() {
      if (this.chartInstance) {
        this.chartInstance.resize(); // 调整图表大小
      }
    },
  },
};
</script>

<style>
.Sitedata1 {
  width: 800px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8%;
}

.chart-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.chart {
  width: 100%;
  height: 300px; /* 直接设置固定高度，确保 ECharts 能够正确渲染 */
}

.demo-form-inline .el-input {
  --el-input-width: 220px;
}
.demo-form-inline .el-select {
  --el-select-width: 220px;
}
.el-form-item {
  margin-top: 10px;
}
</style>

<template>
  <div style="height: 330px; padding: 10px">
    <el-row>
      <!-- 左侧按钮 -->
      <el-col span="4" style="margin-left: -15px">
        <el-row class="el-row-tap" style="margin-top: 40px">
          <el-button
            type="primary"
            @click="
              () => {
                activeView = 'result';
                fetchFileData('obs.txt');
              }
            "
            >预测结果</el-button
          >
        </el-row>
        <el-row class="el-row-tap">
          <el-button type="success" @click="activeView = 'analysis'">
            数据分析
          </el-button>
        </el-row>
        <el-row class="el-row-tap">
          <el-button type="warning" @click="activeView = 'download'">
            下载数据
          </el-button>
        </el-row>
      </el-col>

      <!-- 右侧内容区域 -->
      <el-col span="20">
        <!-- 图表展示 -->
        <div class="result-area" v-if="activeView === 'result'">
          <div id="chart" style="width: 100%; height: 100%"></div>
        </div>

        <!-- 数据分析表格 -->
        <div class="result-area" v-if="activeView === 'analysis'">
          <el-table :data="analysisData" style="border-radius: 10px">
            <el-table-column prop="metric" label="指标" />
            <el-table-column prop="value" label="值" />
          </el-table>
        </div>

        <!-- 下载区域 -->
        <div class="result-area" v-if="activeView === 'download'">
          <el-table
            :data="filterTableData"
            style="width: 100%; border-radius: 10px"
          >
            <el-table-column label="文件名" prop="filename" />
            <el-table-column align="right">
              <template #header>
                <el-input
                  v-model="search"
                  size="small"
                  placeholder="File to search"
                />
              </template>
              <template #default="scope">
                <el-button
                  size="small"
                  type="primary"
                  plain
                  @click="Download(scope.row.filename)"
                >
                  Download
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from "vue";
import axios from "axios";
import * as echarts from "echarts";

/** ----------------------------- Props ----------------------------- */
const props = defineProps({
  Project: {
    type: Object as () => {
      user: string;
      projectName: string;
      siteId: string;
      startDate: string;
      endDate: string;
      model: string;
      desc: string;
    },
    required: true,
  },
});

const emit = defineEmits<{
  (e: "close"): void;
  (e: "saved"): void;
}>();

/** ----------------------------- UI 状态 ----------------------------- */
const activeView = ref("result");
const search = ref("");

/** ----------------------------- 下载数据列表 ----------------------------- */
interface ProjectFile {
  filename: string;
}

const tableData: ProjectFile[] = [
  { filename: "pred.txt" },
  { filename: "obs.txt" },
  { filename: "temp.png" },
  { filename: "extreme.png" },
];

const filterTableData = computed(() =>
  tableData.filter(
    (data) =>
      !search.value ||
      data.filename.toLowerCase().includes(search.value.toLowerCase())
  )
);

/** ----------------------------- 数据分析表格 ----------------------------- */
const analysisData = ref([
  { metric: "NSE（纳什效率系数）", value: "" },
  { metric: "RMSE（均方根误差）", value: "" },
  { metric: "偏差（Bias）", value: "" },
]);

/** ----------------------------- 图表配置 ----------------------------- */

/** ----------------------------- 初始化图表 ----------------------------- */
onMounted(() => {});

/** ----------------------------- 数据获取与绑定 ----------------------------- */
async function fetchFileData(fileName: string) {
  try {
    const { user, projectName, startDate } = props.Project;
    const response1 = await axios.get(
      "http://localhost:8080/project/getFileData",
      {
        params: {
          userName: user,
          projectName: projectName,
          startDate: startDate,
          fileName: fileName,
        },
      }
    );
    const response2 = await axios.get(
      "http://localhost:8080/project/getFileData",
      {
        params: {
          userName: user,
          projectName: projectName,
          fileName: "pred.txt",
        },
      }
    );
    //获取径流的模拟以及观测数据
    const obsRunoffdata = response1.data.map(Number);
    const preRunoffData = response2.data.map(Number);
    //计算精度
    calculateAnalysisData(
      preRunoffData.slice(0, 30),
      obsRunoffdata.slice(0, 30)
    );
    //生成时间长度
    const preRunoffDate = generateDateArray(startDate);
    const splitIndex = 29; // 0 基的第 30 天
    const splitDate = preRunoffDate[splitIndex];

    const PreResultOption = {
      // title: {
      //   text: " Flow ",
      //   left: "center",
      // },
      grid: {
        bottom: 80,
      },
      //右上角工具栏
      toolbox: {
        feature: {
          dataZoom: {
            yAxisIndex: "none",
          },
          restore: {},
          saveAsImage: {},
        },
      },
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "cross",
          animation: false,
          label: {
            backgroundColor: "#505765",
          },
        },
      },
      legend: {
        data: ["Obs", "Pre"],
        left: 10,
      },
      //图表下控制时间轴
      dataZoom: [
        {
          show: true,
          realtime: true,
          start: 0,
          end: 100,
        },
        {
          type: "inside",
          realtime: true,
          start: 0,
          end: 100,
        },
      ],
      xAxis: [
        {
          type: "category",
          boundaryGap: false,
          axisLine: { onZero: false },
          // prettier-ignore
          // 设置时间轴数据
          data: preRunoffDate.map(function (str) {
        return str.replace(' ', '\n');
      }),
        },
      ],
      yAxis: [
        {
          name: "Flow(m³/s)",
          // name: "Temp(°C)",
          type: "value",
        },
      ],
      series: [
        // 设置径流数据信息
        {
          name: "Obs",
          type: "line",
          // areaStyle: {},
          lineStyle: {
            width: 1,
          },
          emphasis: {
            focus: "series",
          },
          markArea: {
            silent: true,
            itemStyle: {
              opacity: 0.3,
            },
          },
          // prettier-ignore
          data: obsRunoffdata.slice(0, 30),
        },
        // 设置降水数据信息
        {
          name: "Pre",
          type: "line",
          yAxisIndex: 0,
          // areaStyle: {},
          lineStyle: {
            width: 1,
          },
          emphasis: {
            focus: "series",
          },
          markArea: {
            silent: true,
            itemStyle: {
              opacity: 0.3,
            },
          },
          // prettier-ignore
          data: preRunoffData,
        },
        // —— 新增这一段，用来画分界线 ——
        {
          name: "率定/预测分界",
          type: "line",
          // 这个系列本身不画任何折线
          data: [],
          markLine: {
            symbol: "none", // 不画箭头
            label: {
              show: true,
              position: "end", // 线的顶端显示文字
              formatter: "Calib / Forecast",
            },
            lineStyle: {
              color: "#409EFF", // 红色
              type: "dashed", // 虚线
              width: 2,
            },
            data: [{ xAxis: splitDate }],
          },
        },
      ],
    };

    const myChart = echarts.init(
      document.getElementById("chart") as HTMLDivElement
    );

    if (myChart) {
      myChart.setOption(PreResultOption);
    }
  } catch (error) {
    console.error("Error fetching file data:", error);
  }
}

function generateDateArray(startDateString: string) {
  const startDate = new Date(startDateString);
  // 设置开始日期为前30天
  startDate.setDate(startDate.getDate() - 29);
  const daysToGenerate = 44;
  const dateArray = [];

  for (let i = 0; i < daysToGenerate; i++) {
    const currentDate = new Date(startDate);
    currentDate.setDate(startDate.getDate() + i);
    dateArray.push(currentDate.toISOString().split("T")[0]);
  }

  return dateArray;
}

//计算精度函数：
function calculateAnalysisData(
  preRunoffData: number[],
  obsRunoffdata: number[]
) {
  if (preRunoffData.length !== obsRunoffdata.length) {
    console.error("数据长度不一致，无法计算分析数据");
    return [];
  }

  const n = preRunoffData.length;

  // 计算 NSE（纳什效率系数）
  const obsMean = obsRunoffdata.reduce((sum, val) => sum + val, 0) / n;
  const numerator = obsRunoffdata.reduce(
    (sum, obs, i) => sum + Math.pow(obs - preRunoffData[i], 2),
    0
  );
  const denominator = obsRunoffdata.reduce(
    (sum, obs) => sum + Math.pow(obs - obsMean, 2),
    0
  );
  const nse = 1 - numerator / denominator;

  // 计算 RMSE（均方根误差）
  const rmse = Math.sqrt(
    obsRunoffdata.reduce(
      (sum, obs, i) => sum + Math.pow(obs - preRunoffData[i], 2),
      0
    ) / n
  );

  // 计算 Bias（偏差）
  const bias =
    (obsRunoffdata.reduce((sum, obs) => sum + obs, 0) -
      preRunoffData.reduce((sum, pre) => sum + pre, 0)) /
    obsRunoffdata.reduce((sum, obs) => sum + obs, 0);

  // 更新 analysisData
  analysisData.value = [
    { metric: "NSE（纳什效率系数）", value: nse.toFixed(2) },
    { metric: "RMSE（均方根误差）", value: rmse.toFixed(2) },
    { metric: "偏差（Bias）", value: (bias * 100).toFixed(2) + "%" },
  ];
}

/** ----------------------------- 操作方法 ----------------------------- */
async function Download(filename: string) {
  try {
    const { user, projectName } = props.Project;
    const response = await axios.get(
      "http://localhost:8080/project/downloadProjectFile",
      {
        params: {
          userName: user,
          projectName: projectName,
          fileName: filename,
        },
        responseType: "blob",
      }
    );

    const blob = new Blob([response.data], {
      type: "application/octet-stream",
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error("Error downloading file:", error);
  }
}
</script>

<style scoped>
.el-button {
  margin-bottom: 15px;
}

.el-row-tap {
  margin-bottom: 30px;
}

.result-area {
  height: 300px;
  text-align: center;
  margin-left: 15px;
  width: 500px;
  padding: 5px;
  border-radius: 10px;
  border: 1px solid #d5d7da;
}
</style>

<template>
  <div ref="chartContainer" class="chart"></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "RainfallFlowChart",
  props: {
    runoffData: {
      type: Array,
      required: true,
    },
  },
  watch: {
    runoffData: {
      immediate: true,
      handler(newData) {
        this.updateChart(newData); // 数据变化时更新图表
      },
    },
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      const chartDom = this.$refs.chartContainer;
      this.myChart = echarts.init(chartDom);
      this.myChart.setOption(this.getChartOption([])); // 初始化空数据
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
    },
    updateChart(data) {
      if (this.myChart) {
        const dates = data.map((item) => item.date);
        const values = data.map((item) => item.value);
        this.myChart.setOption(this.getChartOption(dates, values));
      }
    },
    getChartOption(dates = [], values = []) {
      return {
        title: {
          text: "Flow",
          left: "center",
        },
        grid: {
          bottom: 80,
        },
        toolbox: {
          feature: {
            dataZoom: { yAxisIndex: "none" },
            restore: {},
            saveAsImage: {},
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            animation: false,
            label: { backgroundColor: "#505765" },
          },
        },
        legend: {
          data: ["Flow"],
          left: 10,
        },
        dataZoom: [
          { show: true, realtime: true, start: 0, end: 100 },
          { type: "inside", realtime: true, start: 0, end: 100 },
        ],
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
            axisLine: { onZero: false },
            data: dates,
          },
        ],
        yAxis: [{ name: "Flow(m³/s)", type: "value" }],
        series: [
          {
            name: "Flow",
            type: "line",
            areaStyle: {},
            lineStyle: { width: 1 },
            emphasis: { focus: "series" },
            data: values,
          },
        ],
      };
    },
  },
};
</script>

<style scoped>
.chart {
  width: 100%;
  height: 400px;
}
</style>

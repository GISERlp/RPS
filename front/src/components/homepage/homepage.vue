<template>
  <div class="common-layout">
    <el-container>
      <el-header> 径流预测系统 </el-header>
      <el-container>
        <el-main>
          <div id="map" class="map"></div>
          <!-- 确保侧边栏始终位于地图上方 -->
          <div class="overlay-sidebar" style="margin-top: 50px">
            <el-row class="tac">
              <el-col :span="12">
                <h5 class="mb-2"></h5>
                <el-menu
                  default-active="2"
                  class="el-menu-vertical-demo"
                  @open="handleOpen"
                  @close="handleClose"
                >
                  <!-- 项目管理 -->
                  <el-sub-menu index="1">
                    <template #title>
                      <el-icon><location /></el-icon>
                      <span>项目管理</span>
                    </template>
                    <el-menu-item index="1-1" @click="showCreateProject">
                      新建项目
                    </el-menu-item>
                    <el-sub-menu index="1-2">
                      <template #title>项目列表</template>
                      <el-menu-item index="1-2-1">项目一</el-menu-item>
                      <el-menu-item index="1-2-2">项目二</el-menu-item>
                    </el-sub-menu>
                  </el-sub-menu>
                  <!-- 数据分析 -->
                  <el-sub-menu index="2">
                    <template #title>
                      <el-icon><location /></el-icon>
                      <span>数据分析</span>
                    </template>
                    <el-menu-item index="2-1">可视化分析</el-menu-item>
                    <el-menu-item index="2-2">精度评估</el-menu-item>
                  </el-sub-menu>
                  <!-- 数据管理 -->
                  <el-sub-menu index="3">
                    <template #title>
                      <el-icon><location /></el-icon>
                      <span>数据管理</span>
                    </template>
                    <el-menu-item index="3-1" @click="openSitesData"
                      >站点数据</el-menu-item
                    >
                    <el-menu-item index="3-2">数据上传</el-menu-item>
                    <el-menu-item index="3-3">数据下载</el-menu-item>
                  </el-sub-menu>
                </el-menu>
              </el-col>
            </el-row>
          </div>
          <!-- 新建项目弹窗 -->
          <div v-if="showProjectModal" class="project-modal">
            <CreateNewProject @close="closeCreateProject" />
          </div>
          <!-- 站点数据弹窗 -->
          <div v-if="showSitesDataModal" class="SitesData">
            <SitesData @close="closeSitesData" />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from "@element-plus/icons-vue";
import CreateNewProject from "../ProjectManagement/CreateNewProject.vue";
import SitesData from "../DataManagement/SitesData.vue";

export default {
  name: "Homepage",
  components: {
    CreateNewProject,
    SitesData,
  },
  data() {
    return {
      map: null,
      Tianditu: null,
      Tianditu_road: null,
      baseMaps: {
        Tianditu: null,
        Tianditu_road: null,
      },
      showProjectModal: false, // 控制新建项目弹窗显示
      showSitesDataModal: false, // 控制站点数据弹窗显示
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    openSitesData() {
      this.showSitesDataModal = true; // 打开站点数据弹窗
    },
    closeSitesData() {
      this.showSitesDataModal = false; // 关闭站点数据弹窗
    },
    showCreateProject() {
      this.showProjectModal = true;
    },
    closeCreateProject() {
      this.showProjectModal = false;
    },
    // 使用id为map的div容器初始化地图
    initMap() {
      this.Tianditu = new L.supermap.TiandituTileLayer({
        layerType: "img",
        key: "1d109683f4d84198e37a38c442d68311",
      });
      this.Tianditu_road = new L.supermap.TiandituTileLayer({
        layerType: "img",
        isLabel: true,
        key: "1d109683f4d84198e37a38c442d68311",
      });
      this.baseMaps = {
        Tianditu: this.Tianditu,
        Tianditu_road: this.Tianditu_road,
      };

      this.map = L.map("map", {
        center: [24.886566, 102.830513], // 中心位置
        zoom: 11, // 缩放等级
        zoomControl: false,
        crs: L.supermap.CRS.TianDiTu_Mercator, // 使用天地图坐标系
        layers: [this.Tianditu, this.Tianditu_road], // 默认底图
      });

      L.control.layers(this.baseMaps).addTo(this.map);
      // 添加天地图图层
    },
  },
};
</script>

<style scoped>
/* 站点数据弹窗样式 */
.SitesData {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2000; /* 确保弹窗在地图和侧边栏上方 */
  background-color: white;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  width: 400px; /* 设置弹窗宽度 */
  height: auto; /* 自动调整高度 */
}
/* 新建项目弹窗样式 */
.project-modal {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2000; /* 确保弹窗在地图和侧边栏上方 */
  background-color: white;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  width: 600px; /* 设置弹窗宽度 */
  height: auto; /* 自动调整高度 */
}
.close-btn {
  margin-top: 10px;
}
.map {
  height: 100%;
  width: 100%;
  position: relative;
}
.overlay-sidebar {
  position: absolute;
  top: 20px; /* 距离顶部20px */
  left: 15px; /* 距离左侧20px */
  width: 260px;
  height: 380px;
  z-index: 1000; /* 提高层级，确保在地图上方 */
  padding: 10px;
}
.map {
  height: 100%;
  width: 100%;
  position: relative; /* 确保地图不会干扰侧边栏的层级 */
}
.el-header {
  background-color: rgb(159.5, 206.5, 255); /* 浅灰色背景 */
  height: 10vh; /* 占页面高度10% */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: x-large;
  color: #1368c9; /* 字体颜色为白色 */
  font-family: "Microsoft YaHei", sans-serif; /* 设置字体为微软雅黑 */
  font-weight: bold; /* 加粗 */
}

.el-aside {
  /* background-color: #337ecc; */
  height: 90vh; /* 占页面高度90% */
}

.el-main {
  padding: 0px;
  background-color: #147ae1; /* 浅黄色背景 */
  height: 90vh; /* 占页面高度90% */
}

.el-menu-vertical-demo {
  width: 250px;
  min-height: 400px;
  border-radius: 2%;
  /* border: #545c64; */
}
</style>

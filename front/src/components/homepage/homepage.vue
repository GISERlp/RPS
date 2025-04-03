<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <!-- 表头组件 -->
        <el-menu
          style="flex: 1"
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
          @select="handleSelect"
        >
          <el-menu-item index="0"
            ><h1 class="title">流域径流预测系统</h1></el-menu-item
          >
          <div class="flex-grow"></div>
          <!-- 添加占位符以推送菜单项到右边 -->
          <el-menu-item index="1">关于我们</el-menu-item>
          <el-menu-item index="2">使用指南</el-menu-item>
          <el-menu-item index="3">项目中心</el-menu-item>
          <el-sub-menu index="4">
            <template #title>用户管理</template>
            <el-menu-item index="2-1"
              >当前用户:
              <span style="margin-left: 10px; margin-bottom: 1.5px">{{
                this.username
              }}</span></el-menu-item
            >
            <el-menu-item index="2-2" @click="changeUser"
              >切换用户</el-menu-item
            >
          </el-sub-menu>
        </el-menu></el-header
      >
      <el-container>
        <el-main>
          <div id="map" class="map"></div>
          <!-- 确保侧边栏始终位于地图上方 -->
          <div
            v-if="showSidebar"
            class="overlay-sidebar"
            style="margin-top: 50px"
          >
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
                    <el-sub-menu index="1-2" @Click="fetchProjects">
                      <template #title>项目列表</template>
                      <el-menu-item
                        v-for="(project, index) in projectList"
                        :key="index"
                        :index="'1-2-' + (index + 1)"
                      >
                        {{ project }}
                      </el-menu-item>
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
import L from "leaflet";
import axios from "axios";
import {
  Document,
  Menu as IconMenu,
  Location, // 确保引入 Location 图标
  Setting,
} from "@element-plus/icons-vue";
import CreateNewProject from "../ProjectManagement/CreateNewProject.vue";
import SitesData from "../DataManagement/SitesData.vue";

export default {
  name: "Homepage",
  components: {
    CreateNewProject,
    SitesData,
    Location, // 注册 Location 组件
  },
  data() {
    return {
      coordinates: [],
      activeIndex: "1",
      showSidebar: false, // 控制侧边栏显示
      map: null,
      Tianditu: null,
      Map4326: null,
      baseMaps: {
        Tianditu: null,
        Map4326: null,
      },
      showProjectModal: false, // 控制新建项目弹窗显示
      showSitesDataModal: false, // 控制站点数据弹窗显示
      username: this.$route.query.username || "未登录", // 从路由参数中获取用户名
      projectList: [], // 存储项目列表
    };
  },
  mounted() {
    this.fetchProjects(); // 调用接口获取项目列表
    this.getSitesData().then(() => {
      this.initMap(); // 确保站点数据加载完成后初始化地图
    });
  },
  methods: {
    //获取所有站点信息用于地图显示
    async getSitesData() {
      try {
        const response = await axios.get(
          "http://localhost:8080/sitesdata?siteId="
        );
        if (response.data) {
          // 按行分割数据并过滤掉空行
          this.coordinates = response.data
            .split("\n")
            .filter((line) => line.trim() !== "") // 过滤空行
            .map((line) => {
              const [
                SiteID,
                Latitude,
                Longitude,
                Elevation,
                Slope,
                Area_Gages2,
                Area_GeoSpaFabric,
              ] = line.split(",");
              if (!SiteID || !Latitude || !Longitude) {
                console.warn("跳过无效数据行:", line);
                return null; // 跳过无效行
              }
              return {
                name: SiteID.trim(),
                lat: parseFloat(Latitude.trim()),
                lng: parseFloat(Longitude.trim()),
                elevation: parseFloat(Elevation?.trim() || 0),
                slope: parseFloat(Slope?.trim() || 0),
                areaGages2: parseFloat(Area_Gages2?.trim() || 0),
                areaGeoSpaFabric: parseFloat(Area_GeoSpaFabric?.trim() || 0),
              };
            })
            .filter((site) => site !== null); // 过滤掉无效行
          console.log("站点数据:", this.coordinates); // 确保日志打印
        } else {
          console.error("站点数据为空");
        }
      } catch (error) {
        console.error("获取站点数据失败:", error);
      }
    },
    // 切换用户
    changeUser() {
      this.$router.push("/"); // 跳转到登录页面
    },
    // 切换功能区
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      // 切换菜单时控制侧边栏显示
      this.showSidebar = key === "3"; // 仅当点击“项目中心”时显示侧边栏
    },
    // 处理菜单打开和关闭事件
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    // 打开站点数据弹窗
    openSitesData() {
      this.showSitesDataModal = true;
    },
    // 关闭站点数据弹窗
    closeSitesData() {
      this.showSitesDataModal = false;
    },
    // 显示新建项目弹窗
    showCreateProject() {
      this.showProjectModal = true;
    },
    // 关闭新建项目弹窗
    closeCreateProject() {
      this.showProjectModal = false;
    },
    // 获取项目列表
    async fetchProjects() {
      try {
        const response = await fetch(
          `http://localhost:8080/project/getByUserNameAndProjectName?userName=${this.username}`
        );
        if (response.ok) {
          const data = await response.json();
          this.projectList = data.map((project) => project.projectName); // 提取项目名
        } else {
          console.error("Failed to fetch projects:", response.statusText);
        }
      } catch (error) {
        console.error("Error fetching projects:", error);
      }
    },
    // 使用id为map的div容器初始化地图
    initMap() {
      this.Map4326 = new L.supermap.TiledMapLayer(
        "https://iserver.supermap.io/iserver/services/map-world/rest/maps/World"
      );
      this.Tianditu = new L.supermap.TiandituTileLayer({
        layerType: "img",
        key: "1d109683f4d84198e37a38c442d68311",
      });

      // 添加在线图层
      const Aasia_river_10 = L.tileLayer.wms(
        "http://172.21.187.93:7071/geoserver/SEIMS/wms",
        {
          layers: "SEIMS:asia_river_10",
          format: "image/png",
          transparent: true,
          noWarp: true,
        }
      );

      const Northamerica_river_10_shp = L.tileLayer.wms(
        "http://172.21.187.93:7071/geoserver/SEIMS/wms",
        {
          layers: "SEIMS:northamerica_river_10_shp",
          format: "image/png",
          transparent: true,
          noWarp: true,
        }
      );

      const Southamerica_river_10_shp = L.tileLayer.wms(
        "http://172.21.187.93:7071/geoserver/SEIMS/wms",
        {
          layers: "SEIMS:southamerica_river_10_shp",
          format: "image/png",
          transparent: true,
          noWarp: true,
        }
      );

      const Africa_river_12_shp = L.tileLayer.wms(
        "http://172.21.187.93:7071/geoserver/SEIMS/wms",
        {
          layers: "SEIMS:africa_river_12_shp",
          format: "image/png",
          transparent: true,
          noWarp: true,
        }
      );

      const Europe_river_10_shp = L.tileLayer.wms(
        "http://172.21.187.93:7071/geoserver/SEIMS/wms",
        {
          layers: "SEIMS:europe_river_10_shp",
          format: "image/png",
          transparent: true,
          noWarp: true,
        }
      );

      this.baseMaps = {
        天地图影像: this.Tianditu,
        世界地图: this.Map4326,
      };

      const overlayMaps = {
        亚洲河流: Aasia_river_10,
        北美洲河流: Northamerica_river_10_shp,
        南美洲河流: Southamerica_river_10_shp,
        非洲河流: Africa_river_12_shp,
        欧洲河流: Europe_river_10_shp,
      };

      this.map = L.map("map", {
        center: [36.81503, -98.64814], // 中心位置
        zoom: 3, // 缩放等级
        maxZoom: 8, // 最大缩放等级
        minZoom: 2, // 设置最小缩放级别
        zoomControl: false, // 隐藏缩放控件
        crs: L.CRS.EPSG4326,
        layers: [this.Map4326], // 默认底图
      });
      // 添加标记点
      const coordinates = this.coordinates; // 使用 data 中的 coordinates
      coordinates.forEach((coord) => {
        const marker = L.marker([coord.lat, coord.lng]).addTo(this.map);
        // 设置弹窗内容
        marker.bindPopup(`
          <table style="border-collapse: collapse; width: 100%; font-size: 14px; text-align: center; font-family: 'Microsoft YaHei', 'Times New Roman', sans-serif;">
            <tr>
              <th style="border: 1px solid #ddd; padding: 8px; text-align: center;">属性</th>
              <th style="border: 1px solid #ddd; padding: 8px; text-align: center;">值</th>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">站点编号</td>
              <td style="border: 1px solid #ddd; padding: 8px;"><b>${coord.name}</b></td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">纬度</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.lat}</td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">经度</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.lng}</td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">海拔</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.elevation} 米</td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">坡度</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.slope}</td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">流域面积 (Gages2)</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.areaGages2} km²</td>
            </tr>
            <tr>
              <td style="border: 1px solid #ddd; padding: 8px;">流域面积 (GeoSpaFabric)</td>
              <td style="border: 1px solid #ddd; padding: 8px;">${coord.areaGeoSpaFabric} km²</td>
            </tr>
          </table>
        `);
      });

      // 添加图层控制
      L.control.layers(this.baseMaps, overlayMaps).addTo(this.map);
    },
  },
};
</script>

<style>
.title {
  font-size: x-large;
  color: #1280cf; /* 字体颜色为白色 */
  font-family: "Microsoft YaHei", sans-serif; /* 设置字体为微软雅黑 */
  font-weight: bold; /* 加粗 */
}
.flex-grow {
  flex-grow: 1; /* 占据剩余空间 */
}

/* 站点数据弹窗样式 */
.SitesData {
  position: absolute;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  z-index: 2000; /* 确保弹窗在地图和侧边栏上方 */

  border: 1px solid #ccc;
  border-radius: 8px;
}
/* 新建项目弹窗样式 */
.project-modal {
  background-color: #fff;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2000; /* 确保弹窗在地图和侧边栏上方 */
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
  position: absolute; /* 确保地图不会干扰侧边栏的层级 */
}
.el-header {
  background-color: rgb(255, 255, 255); /* 浅灰色背景 */
  display: flex;
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

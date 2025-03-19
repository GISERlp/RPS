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
              <el-col>
                <el-menu
                  active-text-color="#ffd04b"
                  background-color="#545c64"
                  class="el-menu-vertical-demo"
                  default-active="2"
                  text-color="#fff"
                  @open="handleOpen"
                  @close="handleClose"
                >
                  <el-sub-menu index="1">
                    <template #title>
                      <el-icon><location /></el-icon>
                      <span>Navigator One</span>
                    </template>
                    <el-menu-item-group title="Group One">
                      <el-menu-item index="1-1">item one</el-menu-item>
                      <el-menu-item index="1-2">item two</el-menu-item>
                    </el-menu-item-group>
                    <el-menu-item-group title="Group Two">
                      <el-menu-item index="1-3">item three</el-menu-item>
                    </el-menu-item-group>
                    <el-sub-menu index="1-4">
                      <template #title>item four</template>
                      <el-menu-item index="1-4-1">item one</el-menu-item>
                    </el-sub-menu>
                  </el-sub-menu>
                  <el-menu-item index="2">
                    <el-icon><icon-menu /></el-icon>
                    <span>Navigator Two</span>
                  </el-menu-item>
                  <el-menu-item index="3" disabled>
                    <el-icon><document /></el-icon>
                    <span>Navigator Three</span>
                  </el-menu-item>
                  <el-menu-item index="4">
                    <el-icon><setting /></el-icon>
                    <span>Navigator Four</span>
                  </el-menu-item>
                </el-menu>
              </el-col>
            </el-row>
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

export default {
  name: "Homepage",
  data() {
    return {
      map: null,
      url: "https://iserver.supermap.io",
      Tianditu: null,
      China: null,
      ChinaDark: null,
      Tianditu_road: null,
      baseMaps: {
        Tianditu: null,
        China: null,
        ChinaDark: null,
        Tianditu_road: null,
      },
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
    // 使用id为map的div容器初始化地图
    initMap() {
      this.China = new L.supermap.TiledMapLayer(
        this.url + "/iserver/services/map-china400/rest/maps/China",
        { noWrap: true }
      );
      this.ChinaDark = new L.supermap.TiledMapLayer(
        this.url + "/iserver/services/map-china400/rest/maps/ChinaDark",
        { noWrap: true }
      );
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
        China: this.China,
        ChinaDark: this.ChinaDark,
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
/* 确保侧边栏始终位于地图上方 */
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
  background-color: #041d36; /* 浅灰色背景 */
  height: 10vh; /* 占页面高度10% */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: x-large;
  color: #fff; /* 字体颜色为白色 */
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
  border: #545c64;
}
</style>

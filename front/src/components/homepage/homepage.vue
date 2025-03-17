<template>
  <div class="common-layout">
    <el-container>
      <el-header> 径流预测系统 </el-header>
      <el-container>
        <!-- <el-aside width="200px">Aside</el-aside> -->
        <el-main>
          <div id="map" class="map"></div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
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
.map {
  height: 100%;
  width: 100%;
}
.el-header {
  background-color: #a0cfff; /* 浅灰色背景 */
  height: 10vh; /* 占页面高度10% */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: x-large;
  font-family: "Microsoft YaHei", sans-serif; /* 设置字体为微软雅黑 */
  font-weight: bold; /* 加粗 */
}

.el-aside {
  background-color: #337ecc; /* 浅蓝色背景 */
  height: 90vh; /* 占页面高度90% */
}

.el-main {
  padding: 0px;
  background-color: #d9ecff; /* 浅黄色背景 */
  height: 90vh; /* 占页面高度90% */
}
</style>

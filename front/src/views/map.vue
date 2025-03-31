<template>
  <div ref="mapContainer" style="width: 100%; height: 100%"></div>
</template>

<script>
import { onMounted, ref } from "vue";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "@supermap/iclient-leaflet";
export default {
  name: "Map",
  setup() {
    const mapContainer = ref(null);

    onMounted(() => {
      const host = window.isLocal
        ? window.server
        : "https://iserver.supermap.io";
      const url = `${host}/iserver/services/map-china400/rest/maps/China`;

      const map = L.map(mapContainer.value, {
        center: [0, 0],
        zoom: 1,
        minZoom: 1,
        maxZoom: 18,
      });

      new L.supermap.ImageMapLayer(url, { transparent: false }).addTo(map);
    });

    return { mapContainer };
  },
};
</script>

<style scoped>
.map {
  /* Add your styles here */
}
</style>

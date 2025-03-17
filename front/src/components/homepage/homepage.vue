<template>
  <div class="homepage">
    <header class="header">
      <h1>头部模块</h1>
    </header>
    <main class="main-content">
      <div class="left-side">左侧模块</div>
      <div class="center-content">主内容模块</div>
    </main>
    <!-- <footer class="footer">
      <p>底部模块</p>
    </footer> -->
  </div>
</template>

<script>
export default {
  name: "Homepage",
};
</script>

<style scoped>
.homepage {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.header {
  background-color: #ffcccc;
  padding: 20px;
  text-align: center;
}

.main-content {
  display: flex;
  flex: 1;
  background-color: #ccffcc;
}

.left-side {
  flex: 1;
  background-color: #e0e0e0;
  padding: 20px;
}

.center-content {
  flex: 3;
  background-color: #ffffff;
  padding: 20px;
}

.footer {
  background-color: #ccccff;
  padding: 20px;
  text-align: center;
}
</style>

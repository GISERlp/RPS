<template>
  <el-form
    :inline="true"
    :model="formInline"
    class="demo-form-inline"
    style="display: flex; flex-direction: column; align-items: center"
  >
    <el-form-item label="所在区域">
      <el-select v-model="formInline.region" placeholder="区域站点" clearable>
        <el-option label="南达科塔州" value="南达科塔州" />
        <el-option label="北达科塔州" value="北达科塔州" />
      </el-select>
    </el-form-item>
    <el-form-item label="时间起点">
      <el-date-picker
        v-model="formInline.date"
        type="date"
        placeholder="Pick a date"
        clearable
      />
    </el-form-item>
    <el-form-item label="时间终点">
      <el-date-picker
        v-model="formInline.date"
        type="date"
        placeholder="Pick a date"
        clearable
      />
    </el-form-item>
    <el-form-item label="站点编号">
      <el-input v-model="formInline.user" placeholder="站点编号" clearable />
    </el-form-item>
    <el-form-item> </el-form-item>
  </el-form>
  <div>
    <el-form-item
      style="display: flex; justify-content: space-between; width: 100%"
    >
      <el-button type="primary" @click="onSubmit">查询</el-button>
      <el-button type="primary" @click="onclose" style="margin-left: auto"
        >关闭</el-button
      >
    </el-form-item>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";

const formInline = reactive({
  user: "",
  region: "",
  date: "",
});
const emit = defineEmits(["close"]);
const onclose = () => {
  console.log("close!");
  emit("close");
};

const onSubmit = () => {
  console.log("submit!");
};
</script>

<style>
.demo-form-inline .el-input {
  --el-input-width: 220px;
}

.demo-form-inline .el-select {
  --el-select-width: 220px;
}
</style>

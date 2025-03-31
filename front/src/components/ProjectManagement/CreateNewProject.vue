<template>
  <el-form :model="form" label-width="120px">
    <el-form-item label="项目名称 ">
      <el-input v-model="form.projectName" placeholder="请输入项目名称" />
    </el-form-item>
    <el-form-item label="水文站点编号">
      <el-input v-model="form.siteId" placeholder="请输入站点编号" />
    </el-form-item>
    <el-row>
      <el-col :span="12">
        <el-form-item label="时间起点">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="Pick a date"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="时间终点">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="Pick a date"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="预测模型选择">
      <el-radio-group v-model="form.moodel">
        <el-radio label="HBV" />
        <el-radio label="WISE" />
      </el-radio-group>
    </el-form-item>
    <el-form-item label="项目备注">
      <el-input v-model="form.desc" type="textarea" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">创建</el-button>
      <el-button style="margin-left: 300px" @click="onClose">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { useRoute } from "vue-router";
import { defineEmits } from "vue";

const emit = defineEmits(["close"]);

const route = useRoute();
const form = reactive({
  user: route.query.username || "",
  projectName: "",
  siteId: "",
  startDate: "",
  endDate: "",
  moodel: "",
  desc: "",
});

const onSubmit = () => {
  console.log("submit!");
};
const onClose = () => {
  // 触发关闭事件
  emit("close");
};
</script>

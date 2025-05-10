<template>
  <div class="ProjectEdit">
    <el-form :model="props" label-width="120px">
      <el-form-item label="项目名称 ">
        <el-input
          v-model="props.Project.projectName"
          placeholder="请输入项目名称"
          disabled
        />
      </el-form-item>
      <el-form-item label="水文站点编号">
        <el-input v-model="props.Project.siteId" placeholder="请输入站点编号" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="时间起点">
            <el-date-picker
              v-model="props.Project.startDate"
              type="date"
              placeholder="Pick a date"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="时间终点">
            <el-date-picker
              v-model="props.Project.endDate"
              type="date"
              placeholder="Pick a date"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="预测模型选择">
        <el-radio-group v-model="props.Project.model">
          <el-radio label="HBV" />
          <el-radio label="WISE" />
        </el-radio-group>
      </el-form-item>
      <el-form-item label="项目备注">
        <el-input v-model="props.Project.desc" type="textarea" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive } from "vue";
import { defineEmits } from "vue";
import axios from "axios";

const emit = defineEmits(["close"]);

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

onMounted(() => {
  console.log("接收到的项目是:", props.Project);
});
</script>

<style scoped>
.ProjectEdit {
  margin-left: -20px;
}
</style>

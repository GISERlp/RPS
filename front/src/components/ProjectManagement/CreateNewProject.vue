<template>
  <el-form :model="form" label-width="120px">
    <el-form-item label="项目名称 ">
      <el-input v-model="form.projectName" placeholder="请输入项目名称" />
    </el-form-item>
    <el-form-item label="站点编号">
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
      <el-radio-group v-model="form.model">
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
import axios from "axios";
import { ElMessage, messageConfig } from "element-plus";

const emit = defineEmits(["close"]);

const route = useRoute();
const form = reactive({
  user: route.query.username || "",
  projectName: "",
  siteId: "",
  startDate: "",
  endDate: "",
  model: "",
  desc: "",
});

const onSubmit = async () => {
  try {
    // 检查日期有效性
    if (!form.startDate || !form.endDate) {
      ElMessage.error("请填写完整的时间起点和终点!");
      return;
    }

    // 格式化数据为后端要求的格式
    const payload = {
      userName: form.user,
      projectName: form.projectName,
      siteId: form.siteId,
      model: form.model,
      startDate: new Date(form.startDate).toISOString(), // 转换为 ISO 8601 格式
      endDate: new Date(form.endDate).toISOString(), // 转换为 ISO 8601 格式
      description: form.desc,
    };

    const response = await axios.post(
      "http://localhost:8080/project/create",
      payload
    );
    // 处理响应
    if (response.data.code == 200) {
      ElMessage.success("项目创建成功!");
    } else if (response.data.code == 500) {
      ElMessage.error("项目已经存在!");

      return;
    }
  } catch (error) {
    ElMessage.error("项目创建失败!");
    console.error("项目创建失败:", error);
  }
};

const onClose = () => {
  // 触发关闭事件
  emit("close");
};
</script>

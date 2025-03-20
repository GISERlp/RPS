<template>
  <el-form
    ref="ruleFormRef"
    style="max-width: 800px"
    :model="ruleForm"
    :rules="rules"
    label-width="auto"
    class="demo-ruleForm"
    :size="'small'"
    status-icon
  >
    <el-form-item label="项目名称" prop="name">
      <el-input v-model="ruleForm.name" />
    </el-form-item>
    <el-form-item label="模型选择" prop="region">
      <el-select v-model="ruleForm.region" placeholder="请选择项目模型">
        <el-option label="WISE" value="WISE" />
        <el-option label="HBV" value="HBV" />
      </el-select>
    </el-form-item>
    <el-form-item label="站点选择" prop="count">
      <el-select-v2
        v-model="ruleForm.count"
        placeholder="站点编号"
        :options="options"
      />
    </el-form-item>

    <el-form-item label="项目时间" required>
      <el-col :span="11">
        <el-form-item prop="date1">
          <el-date-picker
            v-model="ruleForm.date1"
            type="date"
            aria-label="选择日期"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col class="text-center" :span="2">
        <span class="text-gray-500">-</span>
      </el-col>
      <el-col :span="11">
        <el-form-item prop="date1">
          <el-date-picker
            v-model="ruleForm.date1"
            type="date"
            aria-label="选择日期"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-form-item>

    <el-form-item label="项目备注" prop="desc">
      <el-input v-model="ruleForm.desc" type="textarea" />
    </el-form-item>
    <el-form-item style="display: flex; justify-content: space-between">
      <div>
        <el-button type="primary" @click="submitForm(ruleFormRef)">
          创建
        </el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </div>
      <el-button type="danger" @click="closeForm" style="margin-left: auto">
        关闭
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import type { ComponentSize, FormInstance, FormRules } from "element-plus";

interface RuleForm {
  name: string;
  region: string;
  count: string;
  date1: string;
  date2: string;
  delivery: boolean;
  location: string;
  type: string[];
  resource: string;
  desc: string;
}

const formSize = ref<ComponentSize>("default");
const ruleFormRef = ref<FormInstance>();
const ruleForm = reactive<RuleForm>({
  name: "Hello",
  region: "",
  count: "",
  date1: "",
  date2: "",
  delivery: false,
  location: "",
  type: [],
  resource: "",
  desc: "",
});

const locationOptions = ["家", "公司", "学校"];

const rules = reactive<FormRules<RuleForm>>({
  name: [
    { required: true, message: "请输入活动名称", trigger: "blur" },
    { min: 3, max: 5, message: "长度应为 3 到 5 个字符", trigger: "blur" },
  ],
  region: [
    {
      required: true,
      message: "请选择活动区域",
      trigger: "change",
    },
  ],
  count: [
    {
      required: true,
      message: "请选择活动人数",
      trigger: "change",
    },
  ],
  date1: [
    {
      type: "date",
      required: true,
      message: "请选择日期",
      trigger: "change",
    },
  ],
  date2: [
    {
      type: "date",
      required: true,
      message: "请选择时间",
      trigger: "change",
    },
  ],
  location: [
    {
      required: true,
      message: "请选择活动地点",
      trigger: "change",
    },
  ],
  type: [
    {
      type: "array",
      required: true,
      message: "请至少选择一个活动类型",
      trigger: "change",
    },
  ],
  resource: [
    {
      required: true,
      message: "请选择资源",
      trigger: "change",
    },
  ],
  desc: [{ required: true, message: "请输入活动形式", trigger: "blur" }],
});

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log("提交成功!");
    } else {
      console.log("提交失败!", fields);
    }
  });
};

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

const options = Array.from({ length: 10000 }).map((_, idx) => ({
  value: `${idx + 1}`,
  label: `${idx + 1}`,
}));

const emit = defineEmits(["close"]);

const closeForm = () => {
  // 触发关闭事件
  emit("close");
};
</script>

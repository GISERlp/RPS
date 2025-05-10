<template>
  <div></div>
</template>

<script lang="ts" setup>
/** ----------------------------- 引入模块 ----------------------------- */
import { ref, reactive, computed, onMounted } from "vue";
import axios from "axios";
import { ElNotification } from "element-plus";

/** ----------------------------- 引入子组件 ----------------------------- */

/** ----------------------------- 定义组件映射 ----------------------------- */
const componentsMap = {};

/** ----------------------------- 变量定义区 ----------------------------- */
//静态属性
const var1 = ref("1");
//对象属性
const sharedProps = reactive({
  user: "",
  projectName: "",
});

/** ----------------------------- props / emits ----------------------------- */
const props = defineProps({
  username: { type: String, required: true },
  projectName: { type: String, required: true },
});

const emit = defineEmits(["close", "saved"]);

/** ----------------------------- 生命周期钩子 ----------------------------- */
onMounted(async () => {});

/** ----------------------------- 函数定义区 ----------------------------- */
</script>

<style scoped></style>

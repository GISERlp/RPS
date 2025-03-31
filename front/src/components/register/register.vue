<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">用户注册</h2>
      <el-form
        :model="registerForm"
        ref="formRef"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            type="password"
            v-model="registerForm.password"
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleRegister">注册</el-button>
          <el-button
            style="margin-left: 120px"
            type="primary"
            @click="goToRegister"
            >登录</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const registerForm = ref({
  username: "",
  password: "",
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const formRef = ref(null);
const router = useRouter();

const handleRegister = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const { username, password } = registerForm.value;
      try {
        const response = await fetch("http://localhost:8080/user/add", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ username, password }),
        });
        const result = await response.json();
        if (result.code === "200") {
          ElMessage.success(result.msg || "注册成功！");
          console.log("注册成功:", result);
        } else {
          ElMessage.error(result.msg || "注册失败！");
        }
      } catch (error) {
        ElMessage.error("网络错误，请稍后重试！");
        console.error("注册错误:", error);
      }
    } else {
      ElMessage.error("请填写完整的注册信息！");
    }
  });
};

const goToRegister = () => {
  router.push("/");
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.register-card {
  width: 400px;
}
</style>

<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">用户登录</h2>
      <el-form
        :model="loginForm"
        ref="formRef"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            type="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button style="margin-left: 120px" @click="goToRegister"
            >注册</el-button
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

const loginForm = ref({
  username: "",
  password: "",
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const formRef = ref(null);
const router = useRouter();

const handleLogin = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const { username, password } = loginForm.value;
      try {
        const response = await fetch("http://localhost:8080/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ username, password }),
        });
        const result = await response.json();
        if (result.code === "200") {
          ElMessage.success(result.msg || "登录成功！");
          console.log("登录成功:", result);
          // 将用户名传递给 homepage
          router.push({ path: "/homepage", query: { username } });
        } else {
          ElMessage.error(result.msg || "登录失败！");
        }
      } catch (error) {
        ElMessage.error("网络错误，请稍后重试！");
        console.error("登录错误:", error);
      }
    } else {
      ElMessage.error("请填写完整的登录信息！");
    }
  });
};

const goToRegister = () => {
  router.push("/register");
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-card {
  width: 400px;
}
</style>

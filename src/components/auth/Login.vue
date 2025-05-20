<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h2 class="login-title">用户登录</h2>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>

      <el-form
        ref="loginForm"
        :model="form"
        :rules="rules"
        status-icon
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item
          label="用户名"
          prop="username"
        >
          <el-input
            v-model="form.username"
            placeholder="请输入用户名或邮箱"
            prefix-icon="User"
          ></el-input>
        </el-form-item>

        <el-form-item
          label="密码"
          prop="password"
        >
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码?</el-link>
        </div>

        <el-button type="primary" native-type="submit" class="login-button">登录</el-button>
      </el-form>

      <el-divider>
        <span class="divider-text">或使用第三方账号登录</span>
      </el-divider>

      <div class="third-party-login">
        <el-button class="third-party-button wechat">
          <i class="fab fa-weixin"></i> 微信登录
        </el-button>
        <el-button class="third-party-button qq">
          <i class="fab fa-qq"></i> QQ登录
        </el-button>
      </div>

      <div class="register-link">
        <span>还没有账户?</span>
        <router-link to="/register">
          <el-link type="primary">立即注册</el-link>
        </router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import API from '../../api';

const router = useRouter();
const loginForm = ref(null);
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ]
};

// 登录处理函数
const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value) return;
  
  try {
    await loginForm.value.validate();
    
    // 调用登录API
    const res = await API.auth.login({
      username: form.username,
      password: form.password
    });
    
    ElMessage.success('登录成功');
    
    // 如果选择"记住我"，可以在这里设置更长的token过期时间
    // 或增加其他逻辑
    
    // 跳转到仪表盘页面 - 修改导航方式
    setTimeout(() => {
      router.push({ name: 'dashboard' });
    }, 1500);
  } catch (error) {
    console.error('登录失败', error);
    // 错误处理在API拦截器中已经实现
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 460px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.login-subtitle {
  color: #606266;
  font-size: 14px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.login-button {
  width: 100%;
  margin-bottom: 20px;
}

.divider-text {
  font-size: 14px;
  color: #909399;
}

.third-party-login {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.third-party-button {
  flex: 1;
}

.third-party-button.wechat i {
  color: #07c160;
}

.third-party-button.qq i {
  color: #1677ff;
}

.register-link {
  text-align: center;
  font-size: 14px;
}

.register-link span {
  color: #606266;
  margin-right: 5px;
}
</style> 
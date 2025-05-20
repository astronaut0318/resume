<template>
  <div class="register-container">
    <el-card class="register-card">
      <div class="register-header">
        <h2 class="register-title">用户注册</h2>
        <p class="register-subtitle">创建您的智能简历工场账户</p>
      </div>
      
      <el-form
        ref="registerForm"
        :model="form"
        :rules="rules"
        label-position="top"
        status-icon
        @submit.prevent="handleRegister"
      >
        <el-form-item
          label="用户名"
          prop="username"
        >
          <el-input
            v-model="form.username"
            placeholder="请输入用户名，例如：zhangsan"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item
          label="邮箱"
          prop="email"
        >
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址"
            prefix-icon="Message"
            type="email"
          />
        </el-form-item>
        
        <el-form-item
          label="手机号码"
          prop="phone"
        >
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号码"
            prefix-icon="Iphone"
          />
        </el-form-item>
        
        <el-form-item
          label="密码"
          prop="password"
        >
          <el-input
            v-model="form.password"
            placeholder="请设置密码 (至少8位，包含大小写字母和数字)"
            prefix-icon="Lock"
            show-password
            type="password"
          />
        </el-form-item>
        
        <el-form-item
          label="确认密码"
          prop="confirmPassword"
        >
          <el-input
            v-model="form.confirmPassword"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            type="password"
          />
        </el-form-item>
        
        <el-form-item prop="agree">
          <el-checkbox v-model="form.agree">
            我已阅读并同意 
            <el-link type="primary" :underline="false">服务条款</el-link> 和 
            <el-link type="primary" :underline="false">隐私政策</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit" 
            class="register-button"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-divider>
        <span class="divider-text">或使用第三方账号注册</span>
      </el-divider>
      
      <div class="third-party-register">
        <el-button class="third-party-button wechat">
          <i class="fab fa-weixin"></i> 微信注册
        </el-button>
        <el-button class="third-party-button qq">
          <i class="fab fa-qq"></i> QQ注册
        </el-button>
      </div>
      
      <div class="login-link">
        <span>已有账户? </span>
        <router-link to="/login">
          <el-link type="primary">立即登录</el-link>
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
const registerForm = ref(null);
const form = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agree: false
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少为8个字符', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字', 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ],
  agree: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('您必须同意服务条款和隐私政策'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ]
};

// 注册处理函数
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value) return;
  
  try {
    await registerForm.value.validate();
    
    // 调用注册API
    const res = await API.users.register({
      username: form.username,
      password: form.password,
      email: form.email,
      phone: form.phone
    });
    
    ElMessage.success('注册成功，请登录');
    
    // 跳转到登录页面 - 修改导航方式
    setTimeout(() => {
      router.push({ name: 'login' });
    }, 1500);
  } catch (error) {
    console.error('注册失败', error);
    // 错误处理在API拦截器中已经实现
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #ecf5ff;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 480px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-title {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.register-subtitle {
  color: #606266;
  font-size: 14px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
}

.divider-text {
  font-size: 14px;
  color: #909399;
}

.third-party-register {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 25px;
}

.third-party-button.wechat i {
  color: #07c160;
  margin-right: 5px;
}

.third-party-button.qq i {
  color: #1677ff;
  margin-right: 5px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}

.login-link span {
  color: #606266;
}
</style> 
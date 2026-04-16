<script setup lang="ts">
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useUserStore } from '@/stores/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  account: '',
  password: '',
  remember: userStore.rememberLogin,
})

const rules: FormRules<typeof form> = {
  account: [
    { required: true, message: '请输入用户名、手机号或邮箱', trigger: 'blur' },
    { min: 3, max: 50, message: '账号长度需在 3-50 个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6-20 个字符之间', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  loading.value = true
  try {
    await userStore.login({
      account: form.account.trim(),
      password: form.password,
      remember: form.remember,
    })
    ElMessage.success('登录成功')
    router.replace((route.query.redirect as string) || '/home')
  } catch (error) {
    const message = error instanceof Error ? error.message : '登录失败，请稍后重试'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card glass-panel">
        <div class="hero-section">
          <div class="brand-box">
            <div class="logo"></div>
            <span class="brand-name">极简商城</span>
          </div>
          <h1 class="hero-title">欢迎来到 <br/> 品质空间</h1>
          <p class="hero-text">在这里，探索极简与强大并存的购物新纪元，开启您的专属品质生活。</p>
          
          <div class="features">
            <div class="feature-tag">安全加密</div>
            <div class="feature-tag">极速达</div>
            <div class="feature-tag">贴心服务</div>
          </div>
        </div>

        <div class="form-section">
          <div class="form-header">
            <h2>登录</h2>
            <p>输入您的凭据以继续购物旅程。</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="账号" prop="account">
              <el-input
                v-model="form.account"
                placeholder="用户名 / 邮箱 / 手机号"
                :prefix-icon="User"
                class="custom-input"
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                show-password
                placeholder="您的登录密码"
                :prefix-icon="Lock"
                class="custom-input"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="form.remember">记住我</el-checkbox>
              <RouterLink to="/register" class="link">注册新账号</RouterLink>
            </div>

            <el-button 
              type="primary" 
              class="submit-btn" 
              :loading="loading" 
              @click="handleLogin"
            >
              立即登录
            </el-button>
          </el-form>
          
          <div class="form-footer">
            <p>登录即代表您同意我们的 <a href="#">服务协议</a></p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Decorative elements -->
    <div class="deco-blob blob-1"></div>
    <div class="deco-blob blob-2"></div>
  </div>
</template>

<style scoped lang="scss">
// ... (Styles remain unchanged)
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-main);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.auth-container {
  width: 100%;
  max-width: 1000px;
  z-index: 10;
}

.auth-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 600px;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
}

.hero-section {
  background: var(--color-primary);
  background: linear-gradient(135deg, var(--color-primary), #1a4bc4);
  padding: 60px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .brand-box {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 60px;
    
    .logo {
      width: 32px;
      height: 32px;
      border: 3px solid white;
      border-radius: var(--radius-sm);
    }
    
    .brand-name {
      font-weight: 800;
      letter-spacing: 0.05em;
      font-size: 18px;
    }
  }

  .hero-title {
    font-size: 48px;
    font-weight: 800;
    line-height: 1.2;
    margin-bottom: 24px;
    color: white;
  }

  .hero-text {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.6;
    margin-bottom: 40px;
    max-width: 300px;
  }

  .features {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    
    .feature-tag {
      padding: 6px 14px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 99px;
      font-size: 12px;
      font-weight: 600;
    }
  }
}

.form-section {
  padding: 60px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .form-header {
    margin-bottom: 40px;
    
    h2 {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 8px;
    }
    
    p {
      color: var(--text-muted);
      font-size: 15px;
    }
  }
}

:deep(.custom-input) {
  .el-input__wrapper {
    background-color: var(--slate-50);
    box-shadow: none !important;
    border: 1px solid var(--slate-100);
    border-radius: var(--radius-md);
    padding: 10px 16px;
    transition: all 0.3s ease;
    
    &:hover, &.is-focus {
      border-color: var(--color-primary);
      background-color: white;
    }
  }
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  
  .link {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-primary);
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.submit-btn {
  width: 100%;
  height: 56px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-md);
  box-shadow: 0 10px 20px -5px rgba(0, 47, 167, 0.2);
  margin-bottom: 24px;
}

.form-footer {
  text-align: center;
  font-size: 13px;
  color: var(--slate-400);
  
  a {
    color: var(--slate-600);
    font-weight: 600;
  }
}

.deco-blob {
  position: absolute;
  width: 500px;
  height: 500px;
  filter: blur(80px);
  opacity: 0.4;
  z-index: 1;
  border-radius: 50%;
}

.blob-1 {
  top: -100px;
  right: -100px;
  background: var(--color-primary-soft);
}

.blob-2 {
  bottom: -150px;
  left: -150px;
  background: #4f46e533;
}

@media (max-width: 900px) {
  .auth-card {
    grid-template-columns: 1fr;
  }
  
  .hero-section {
    display: none;
  }
  
  .form-section {
    padding: 40px;
  }
}
</style>

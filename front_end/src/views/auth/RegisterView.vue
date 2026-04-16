<script setup lang="ts">
import { User, Lock, Message, Iphone } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { registerApi } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules: FormRules<typeof form> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度需在 3-20 个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6-20 个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  phone: [
    {
      pattern: /^$|^1\d{10}$/,
      message: '请输入合法的手机号',
      trigger: 'blur',
    },
  ],
  email: [
    {
      type: 'email',
      message: '请输入合法的邮箱地址',
      trigger: 'blur',
    },
  ],
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  if (!form.phone && !form.email) {
    ElMessage.warning('手机号或邮箱至少填写一项')
    return
  }

  loading.value = true
  try {
    await registerApi({
      username: form.username.trim(),
      password: form.password,
      phone: form.phone.trim(),
      email: form.email.trim(),
    })
    ElMessage.success('注册成功，请登录后继续购物')
    router.replace('/login')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card glass-panel">
        <div class="form-section">
          <div class="form-header">
            <div class="brand-mini" @click="router.push('/home')">
              <div class="logo"></div>
              <span>极简商城</span>
            </div>
            <h2>创建账号</h2>
            <p>立即加入我们的品质社区。</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :prefix-icon="User" placeholder="设置您的用户名" class="custom-input" />
            </el-form-item>

            <div class="form-row">
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  show-password
                  :prefix-icon="Lock"
                  placeholder="设置密码"
                  class="custom-input"
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  show-password
                  :prefix-icon="Lock"
                  placeholder="重复密码"
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" :prefix-icon="Iphone" placeholder="可选填" class="custom-input" />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" :prefix-icon="Message" placeholder="可选填" class="custom-input" />
              </el-form-item>
            </div>

            <el-button 
              type="primary" 
              class="submit-btn" 
              :loading="loading" 
              @click="handleRegister"
            >
              立即注册
            </el-button>

            <div class="switch-link">
              已有账号？
              <RouterLink to="/login" class="link">立即登录</RouterLink>
            </div>
          </el-form>
        </div>

        <div class="side-panel">
          <div class="side-content">
            <div class="quote-box">
              <p class="quote">“简约是最高级的复杂。”</p>
              <span class="author">— 列奥纳多·达·芬奇</span>
            </div>
            
            <div class="info-grid">
              <div class="info-item">
                <span class="val">100%</span>
                <span class="lbl">安全保障</span>
              </div>
              <div class="info-item">
                <span class="val">24/7</span>
                <span class="lbl">贴心支持</span>
              </div>
            </div>
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
  max-width: 1100px;
  z-index: 10;
}

.auth-card {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  min-height: 650px;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
}

.form-section {
  padding: 60px 80px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .form-header {
    margin-bottom: 40px;
    
    .brand-mini {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 24px;
      cursor: pointer;
      
      .logo {
        width: 24px;
        height: 24px;
        background: var(--color-primary);
        border-radius: 4px;
      }
      
      span {
        font-weight: 800;
        font-size: 14px;
        letter-spacing: 0.05em;
      }
    }
    
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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

.submit-btn {
  width: 100%;
  height: 56px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-md);
  box-shadow: 0 10px 20px -5px rgba(0, 47, 167, 0.2);
  margin-top: 12px;
  margin-bottom: 24px;
}

.switch-link {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
  
  .link {
    color: var(--color-primary);
    font-weight: 600;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.side-panel {
  background: var(--slate-900);
  padding: 60px;
  color: white;
  display: flex;
  align-items: flex-end;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: url('https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1000&q=80') center;
    background-size: cover;
    opacity: 0.3;
  }
}

.side-content {
  position: relative;
  z-index: 2;
  width: 100%;
}

.quote-box {
  margin-bottom: 60px;
  
  .quote {
    font-size: 24px;
    font-weight: 600;
    font-style: italic;
    line-height: 1.4;
    margin-bottom: 12px;
  }
  
  .author {
    font-size: 14px;
    color: var(--slate-400);
    letter-spacing: 0.05em;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  
  .info-item {
    padding: 20px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: var(--radius-md);
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    .val {
      display: block;
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 4px;
    }
    
    .lbl {
      font-size: 12px;
      color: var(--slate-400);
      text-transform: uppercase;
      letter-spacing: 0.1em;
    }
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

@media (max-width: 1024px) {
  .form-section {
    padding: 40px;
  }
}

@media (max-width: 900px) {
  .auth-card {
    grid-template-columns: 1fr;
  }
  
  .side-panel {
    display: none;
  }
}
</style>

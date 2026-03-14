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
    <div class="auth-panel page-container">
      <section class="hero-panel">
        <span class="hero-badge">Mall Commerce Suite</span>
        <h1>连接用户、商品与订单的商城入口</h1>
        <p>
          以 Vue3 + Pinia + Axios + Element Plus 构建的企业级前端认证页，支持表单校验、状态持久化、鉴权跳转与统一异常处理。
        </p>
        <ul class="hero-points">
          <li>统一登录态管理</li>
          <li>支持记住登录状态</li>
          <li>受保护页面自动拦截</li>
        </ul>
      </section>

      <section class="form-panel">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>登录后进入商城首页、购物车、订单与个人中心。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
          <el-form-item label="账号" prop="account">
            <el-input
              v-model="form.account"
              placeholder="请输入用户名 / 手机号 / 邮箱"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入登录密码"
              :prefix-icon="Lock"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="form-tools">
            <el-checkbox v-model="form.remember">记住登录状态</el-checkbox>
            <RouterLink to="/register">没有账号？去注册</RouterLink>
          </div>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            立即登录
          </el-button>
        </el-form>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
.auth-page {
  display: flex;
  align-items: center;
  min-height: 100vh;
  padding: 32px 0;
}

.auth-panel {
  display: grid;
  grid-template-columns: 1.1fr 460px;
  overflow: hidden;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 28px 70px rgba(15, 23, 42, 0.16);
}

.hero-panel {
  padding: 56px;
  color: #fff;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.22), transparent 22%),
    linear-gradient(145deg, #14317a, #2750c3 50%, #d94f2b);
}

.hero-badge {
  display: inline-flex;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  letter-spacing: 0.1em;
}

.hero-panel h1 {
  margin: 22px 0 18px;
  font-size: clamp(34px, 4vw, 48px);
  line-height: 1.12;
}

.hero-panel p {
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-points {
  display: grid;
  gap: 14px;
  padding: 0;
  margin: 40px 0 0;
  list-style: none;
}

.hero-points li {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
}

.form-panel {
  padding: 56px 42px;
}

.form-header h2 {
  margin: 0;
  font-size: 30px;
}

.form-header p {
  margin: 10px 0 28px;
  color: #6b7280;
}

.form-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  font-size: 14px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
}

@media (max-width: 960px) {
  .auth-panel {
    grid-template-columns: 1fr;
  }

  .hero-panel {
    display: none;
  }

  .form-panel {
    padding: 32px 20px;
  }
}
</style>

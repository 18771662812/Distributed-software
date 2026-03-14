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
    <div class="auth-panel page-container register-layout">
      <section class="form-panel">
        <div class="form-header">
          <h2>创建商城账号</h2>
          <p>推荐方案：注册成功后跳转登录页，再由登录接口统一下发 token，便于鉴权链路一致和审计。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" clearable />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  show-password
                  :prefix-icon="Lock"
                  placeholder="请输入密码"
                  clearable
                />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  show-password
                  :prefix-icon="Lock"
                  placeholder="请再次输入密码"
                  clearable
                />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" :prefix-icon="Iphone" placeholder="请输入手机号" clearable />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" :prefix-icon="Message" placeholder="请输入邮箱" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
            注册并前往登录
          </el-button>

          <div class="switch-link">
            已有账号？
            <RouterLink to="/login">立即登录</RouterLink>
          </div>
        </el-form>
      </section>

      <section class="side-panel">
        <div class="side-card">
          <span class="hero-badge">REGISTER</span>
          <h3>统一认证入口，便于后续扩展短信、邮箱验证码与第三方登录</h3>
          <p>注册页与登录页采用统一视觉语言，减少用户认知成本，也方便团队按模块拆分维护。</p>
        </div>
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

.register-layout {
  grid-template-columns: 1.25fr 0.75fr;
}

.auth-panel {
  display: grid;
  overflow: hidden;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 28px 70px rgba(15, 23, 42, 0.16);
}

.form-panel {
  padding: 48px 42px;
}

.form-header h2 {
  margin: 0;
  font-size: 30px;
}

.form-header p {
  margin: 10px 0 24px;
  color: #6b7280;
}

.submit-btn {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  border-radius: 14px;
}

.switch-link {
  margin-top: 18px;
  color: #6b7280;
  text-align: center;
}

.side-panel {
  padding: 28px;
  background: linear-gradient(145deg, #f4f7ff, #fff4ef);
}

.side-card {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  height: 100%;
  min-height: 100%;
  padding: 32px;
  border-radius: 28px;
  color: #fff;
  background: linear-gradient(160deg, #1f4ed8, #d94f2b);
}

.hero-badge {
  display: inline-flex;
  width: fit-content;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  letter-spacing: 0.1em;
}

.side-card h3 {
  margin: 22px 0 14px;
  font-size: 28px;
  line-height: 1.3;
}

.side-card p {
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
}

@media (max-width: 960px) {
  .register-layout {
    grid-template-columns: 1fr;
  }

  .side-panel {
    display: none;
  }

  .form-panel {
    padding: 32px 20px;
  }
}
</style>

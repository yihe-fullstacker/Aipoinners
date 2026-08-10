<!-- pages/register/register.vue — 注册页 -->
<template>
  <view class="page-shell">
    <NavBar title="注册" :showBack="true" />
    <view class="reg-body">
      <view class="reg-header">
        <text class="reg-title">创建账户</text>
        <text class="reg-sub">注册影智医疗诊断平台</text>
      </view>
      <view class="reg-form">
        <view class="ig"><text class="il">用户名</text><input class="inp" v-model="f.username" placeholder="请输入用户名" placeholder-style="color:#64748B"/></view>
        <view class="ig"><text class="il">密码</text><input class="inp" v-model="f.password" type="password" placeholder="至少6位" placeholder-style="color:#64748B"/></view>
        <view class="ig"><text class="il">手机号</text><input class="inp" v-model="f.phone" type="number" maxlength="11" placeholder="选填" placeholder-style="color:#64748B"/></view>
        <view class="ig"><text class="il">邮箱</text><input class="inp" v-model="f.email" placeholder="选填" placeholder-style="color:#64748B"/></view>
        <button class="sb" :loading="ld" @tap="doRegister">注册</button>
        <view class="ff"><text class="fl" @tap="goLogin">已有账户？返回登录</text></view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { authAPI } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/nav-bar.vue'

const us = useUserStore(); const ld = ref(false)
const f = reactive({ username: '', password: '', phone: '', email: '' })

const doRegister = async () => {
  if (!f.username || f.password.length < 6) {
    uni.showToast({ title: '用户名和密码(≥6位)必填', icon: 'none' }); return
  }
  ld.value = true
  try {
    const r = await authAPI.register({ username: f.username, password: f.password, phone: f.phone, email: f.email })
    if (r.token) {
      us.login(r, r.token)
      uni.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 500)
    }
  } catch (e) { uni.showToast({ title: e?.error || '注册失败', icon: 'none' }) }
  finally { ld.value = false }
}
const goLogin = () => uni.navigateBack()
</script>

<style scoped>
.page-shell { min-height:100vh; background:#040b16; }
.reg-body { padding: 40rpx 36rpx; }
.reg-header { margin-bottom: 48rpx; display:flex; flex-direction:column; gap:12rpx; }
.reg-title { font-size: 48rpx; font-weight: 800; color: #f8fafc; }
.reg-sub { font-size: 26rpx; color: rgba(200,218,242,0.65); }
.reg-form { display:flex; flex-direction:column; gap:24rpx; }
.ig { display:flex; flex-direction:column; gap:10rpx; }
.il { font-size:24rpx; color:rgba(210,225,245,0.75); font-weight:600; }
.inp { height:88rpx; padding:0 24rpx; border-radius:16rpx; border:1px solid rgba(125,211,252,0.12); background:rgba(255,255,255,0.04); color:#f0f6ff; font-size:28rpx; }
.sb { height:92rpx; border-radius:20rpx; background:linear-gradient(135deg,#5eead4,#38bdf8); color:#032032; font-size:30rpx; font-weight:700; border:none; }
.ff { text-align:center; margin-top:20rpx; }
.fl { color:#70d6ff; font-size:26rpx; }
</style>

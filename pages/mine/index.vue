<!-- pages/mine/index.vue — 个人中心，TabBar "我的"，显示用户信息和快捷入口 -->
<template><view class="p"><NavBar title="个人中心"/><scroll-view scroll-y class="b"><view class="profile"><view class="avatar"><text class="av">{{userStore.userInitial}}</text></view><text class="name">{{userStore.userName}}</text><text class="role">{{userStore.userRole==='DOCTOR'?'医生':'患者'}}</text></view><view class="menu"><view v-for="m in menus" :key="m.label" class="menu-item" @tap="m.action"><text class="mi-icon">{{m.icon}}</text><text class="mi-label">{{m.label}}</text><text class="mi-arrow">→</text></view></view><button class="logout-btn" @tap="doLogout">退出登录</button></scroll-view><TabBar/></view></template>
<script setup>
import {useUserStore} from '@/stores/user';import NavBar from '@/components/nav-bar.vue';import TabBar from '@/components/tab-bar.vue'
const userStore=useUserStore()
const menus=[
  {label:'我的报告',icon:'📋',action:()=>uni.navigateTo({url:'/pages/report/list'})},
  {label:'诊疗历史',icon:'🕐',action:()=>uni.navigateTo({url:'/subpkg-patient/history/index'})},
  {label:'课程中心',icon:'📚',action:()=>uni.navigateTo({url:'/subpkg-patient/course/index'})},
  {label:'医生工作站',icon:'🩺',action:()=>uni.navigateTo({url:'/subpkg-doctor/workspace/index'})},
  {label:'关于',icon:'ℹ️',action:()=>uni.navigateTo({url:'/subpkg-patient/mine/about'})},
  {label:'使用说明',icon:'📖',action:()=>uni.navigateTo({url:'/subpkg-patient/mine/instruction'})}
]
const doLogout=()=>{userStore.logout();uni.showToast({title:'已退出',icon:'success'})}
</script>
<style scoped>.p{min-height:100vh;background:#040b16}.b{padding:28rpx;padding-bottom:160rpx}.profile{display:flex;flex-direction:column;align-items:center;padding:48rpx 0;gap:14rpx}.avatar{width:120rpx;height:120rpx;border-radius:50%;background:linear-gradient(135deg,rgba(94,234,212,0.18),rgba(56,189,248,0.28));display:flex;align-items:center;justify-content:center;border:2px solid rgba(125,211,252,0.3)}.av{font-size:48rpx;font-weight:700;color:#fff}.name{font-size:36rpx;font-weight:700;color:#f0f6ff}.role{font-size:24rpx;color:rgba(200,218,242,0.65)}.menu{display:flex;flex-direction:column;gap:4rpx;border-radius:20rpx;overflow:hidden}.menu-item{display:flex;align-items:center;padding:28rpx 24rpx;background:rgba(8,15,26,0.5);border-bottom:1px solid rgba(125,211,252,0.06);gap:16rpx}.mi-icon{font-size:36rpx}.mi-label{flex:1;font-size:28rpx;color:#f0f6ff}.mi-arrow{font-size:28rpx;color:rgba(200,218,242,0.4)}.logout-btn{width:100%;height:88rpx;border-radius:20rpx;background:rgba(220,38,38,0.15);border:1px solid rgba(220,38,38,0.3);color:#fca5a5;font-size:28rpx;font-weight:600;margin-top:48rpx}</style>

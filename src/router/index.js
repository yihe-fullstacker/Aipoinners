//路由配置入口
//作用：定义前端路由规则
//前端路由主要用于页面与页面之间的跳动
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Landing from '../views/Landing.vue'
import Dashboard from '../views/Dashboard.vue'
import Reports from '../views/Reports.vue'
import CaseDetail from '../views/CaseDetail.vue'
import AnalysisReport from '../views/AnalysisReport.vue'
import DeepAnalysis from '../views/DeepAnalysis.vue'
import DeepSeekAnalysis from '../views/DeepSeekAnalysis.vue'
import DoctorDashboard from '../components/DoctorDashboard.vue'
import ExpertUpload from '../components/ExpertUpload.vue'
import PatientHome from '../components/PatientHome.vue'
import PatientHistory from '../components/PatientHistory.vue'
import ConsultationDetail from '../components/ConsultationDetail.vue'
import SmartQaWorkstation from '../components/SmartQaWorkstation.vue'
import DataDashboard from '../views/DataDashboard.vue'
import MedicalCourse from '../components/MedicalCourse.vue'
import Login from '../components/Login.vue'
import Register from '../components/Register.vue'

const routes = [
  {
    path: '/',
    name: 'Root',
    redirect: '/landing'
  },
  {
    path: '/landing',
    name: 'Landing',
    component: Landing,
    meta: { title: '首页' }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Home
  },
  {
    path: '/register',
    name: 'Register',
    component: Home
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/reports',
    name: 'Reports',
    component: Reports,
    meta: { requiresAuth: true }
  },
  {
    path: '/case-detail/:id',
    name: 'CaseDetail',
    component: CaseDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/analysis-report/:id',
    name: 'AnalysisReport',
    component: AnalysisReport,
    meta: { requiresAuth: true }
  },
  {
    path: '/deep-analysis/:id',
    name: 'DeepAnalysis',
    component: DeepAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/deepseek-analysis/:id',
    name: 'DeepSeekAnalysis',
    component: DeepSeekAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/model-evaluation',
    name: 'ModelEvaluation',
    component: () => import('../views/ModelEvaluation.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/active-learning',
    name: 'ActiveLearning',
    component: () => import('../views/ActiveLearning.vue'),
    meta: { requiresAuth: true }
  }
  ,
  {
    path: '/doctor/dashboard',
    name: 'DoctorDashboard',
    component: DoctorDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/patient/home',
    name: 'PatientHome',
    component: PatientHome,
    meta: { requiresAuth: true }
  },
  {
    path: '/upload',
    name: 'UploadImage',
    component: ExpertUpload,
    meta: { requiresAuth: true }
  },
  {
    path: '/patient/history',
    name: 'PatientHistory',
    component: PatientHistory,
    meta: { requiresAuth: true }
  },
  {
    path: '/patient/consultation/:id',
    name: 'ConsultationDetail',
    component: ConsultationDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue')
  },
  {
    path: '/instruction',
    name: 'Instruction',
    component: () => import('../views/Instruction.vue')
  },

  //作用：配置URL路径 /smart-qa 对应到 SmartQaWorkstation.vue 组件,让前端像一个真正的多页面网站，但实际是单页面应用。每个需要独立访问的页面都需要在路由中配置。
  {
    path: '/smart-qa',
    name: 'SmartQaWorkstation',
    component: SmartQaWorkstation,
    meta: { requiresAuth: true }
  },
  {
    path: '/data-dashboard',
    name: 'DataDashboard',
    component: DataDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/medical-course',
    name: 'MedicalCourse',
    component: MedicalCourse,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      }
    }
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('medical_auth_token') !== null
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
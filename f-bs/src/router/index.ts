import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import HomeView from '../views/HomeView.vue'

// 学生专区页面
import StudentOverviewView from '../views/student/StudentOverviewView.vue'
import StudentProfileView from '../views/student/StudentProfileView.vue'
import StudentResumeView from '../views/student/StudentResumeView.vue'
import StudentApplicationsView from '../views/student/StudentApplicationsView.vue'
import StudentInterviewsView from '../views/student/StudentInterviewsView.vue'
import StudentIntentionView from '../views/student/StudentIntentionView.vue'
import StudentJobBoardView from '../views/student/StudentJobBoardView.vue'

// 企业专区页面
import EmployerOverviewView from '../views/employer/EmployerOverviewView.vue'
import EmployerProfileView from '../views/employer/EmployerProfileView.vue'
import EmployerJobsView from '../views/employer/EmployerJobsView.vue'
import EmployerApplicationsView from '../views/employer/EmployerApplicationsView.vue'
import EmployerInterviewsView from '../views/employer/EmployerInterviewsView.vue'
import EmployerTalentView from '../views/employer/EmployerTalentView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: false }, // 首页不需要登录
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { requiresAuth: false },
    },
    // 学生专区路由 - 需要登录且用户角色为学生
    {
      path: '/student/overview',
      name: 'student-overview',
      component: StudentOverviewView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/profile',
      name: 'student-profile',
      component: StudentProfileView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/resume',
      name: 'student-resume',
      component: StudentResumeView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/applications',
      name: 'student-applications',
      component: StudentApplicationsView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/jobs',
      name: 'student-jobs',
      component: StudentJobBoardView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/interviews',
      name: 'student-interviews',
      component: StudentInterviewsView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    {
      path: '/student/intention',
      name: 'student-intention',
      component: StudentIntentionView,
      meta: { requiresAuth: true, role: 'STUDENT' },
    },
    // 企业专区路由 - 需要登录且用户角色为企业
    {
      path: '/employer/overview',
      name: 'employer-overview',
      component: EmployerOverviewView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
    {
      path: '/employer/profile',
      name: 'employer-profile',
      component: EmployerProfileView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
    {
      path: '/employer/jobs',
      name: 'employer-jobs',
      component: EmployerJobsView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
    {
      path: '/employer/applications',
      name: 'employer-applications',
      component: EmployerApplicationsView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
    {
      path: '/employer/interviews',
      name: 'employer-interviews',
      component: EmployerInterviewsView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
    {
      path: '/employer/talent',
      name: 'employer-talent',
      component: EmployerTalentView,
      meta: { requiresAuth: true, role: 'EMPLOYER' },
    },
  ],
})

// 全局路由守卫 - 拦截未登录用户
router.beforeEach((to, from, next) => {
  // 获取用户登录状态
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  
  // 检查目标路由是否需要登录
  const requiresAuth = to.meta.requiresAuth
  
  if (requiresAuth) {
    // 需要登录的路由
    if (!token || !userInfoStr) {
      // 未登录，阻止导航并触发登录对话框
      // 如果是从导航链接点击来的，对话框已经显示了，这里只需要阻止路由跳转
      // 如果是直接通过URL访问，则显示对话框
      if (from.name === undefined || from.path === '/') {
        // 直接访问URL的情况，触发对话框
        const openDialog = (window as any).checkAuthAndNavigate
        if (openDialog) {
          openDialog(to.fullPath)
        }
      }
      // 阻止导航
      next(false)
      return
    }
    
    // 已登录，检查用户角色
    try {
      const userInfo = JSON.parse(userInfoStr)
      const requiredRole = to.meta.role as string | undefined
      
      if (requiredRole && userInfo.role !== requiredRole) {
        // 角色不匹配，提示并返回首页
        alert(`该功能需要 ${requiredRole} 角色权限`)
        next({ name: 'home' })
        return
      }
      
      // 验证通过，允许访问
      next()
    } catch (error) {
      // userInfo 解析失败，清除数据并显示登录对话框
      console.error('用户信息解析失败:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const openDialog = (window as any).checkAuthAndNavigate
      if (openDialog) {
        openDialog(to.fullPath)
      }
      next(false)
    }
  } else {
    // 不需要登录的路由，直接放行
    next()
  }
})

export default router

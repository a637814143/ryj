import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/complete-info',
      name: 'complete-info',
      component: () => import('../views/student/CompleteInfo.vue'),
      meta: {
        requiresAuth: true,
        skipInfoCheck: true // 跳过信息完整性检查
      }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/DashboardView.vue'),
      meta: {
        requiresAuth: true
      }
    },
    // 学生模块路由
    {
      path: '/student',
      component: () => import('../views/student/StudentLayout.vue'),
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: 'dashboard',
          name: 'student-dashboard',
          component: () => import('../views/student/StudentDashboard.vue')
        },
        {
          path: 'personal-info',
          name: 'personal-info',
          component: () => import('../views/student/PersonalInfo.vue')
        },
        {
          path: 'resume',
          name: 'resume',
          component: () => import('../views/student/ResumeManagement.vue')
        },
        {
          path: 'job-intention',
          name: 'job-intention',
          component: () => import('../views/student/JobIntention.vue')
        },
        {
          path: 'recruitment',
          name: 'recruitment',
          component: () => import('../views/student/RecruitmentBrowse.vue')
        },
        {
          path: 'interview',
          name: 'interview',
          component: () => import('../views/student/InterviewManagement.vue')
        },
        {
          path: 'applications',
          name: 'applications',
          component: () => import('../views/student/ApplicationRecords.vue')
        },
        {
          path: 'job/:id',
          name: 'job-detail',
          component: () => import('../views/student/JobDetail.vue')
        },
        {
          path: 'favorites',
          name: 'favorites',
          component: () => import('../views/student/FavoriteJobs.vue')
        },
        {
          path: 'messages',
          name: 'messages',
          component: () => import('../views/student/MessageCenter.vue')
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/student/Settings.vue')
        }
      ]
    }
  ]
})

// 路由守卫 - 只检查登录状态，不限制信息完整性
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    const user = localStorage.getItem('user');
    if (user) {
      // 已登录，直接放行
      next();
    } else {
      // 未登录，重定向到登录页
      next({ path: '/login', query: { redirect: to.fullPath } });
    }
  } else {
    next();
  }
});

export default router

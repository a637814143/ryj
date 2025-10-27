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
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    // 学生专区路由
    {
      path: '/student/overview',
      name: 'student-overview',
      component: StudentOverviewView,
    },
    {
      path: '/student/profile',
      name: 'student-profile',
      component: StudentProfileView,
    },
    {
      path: '/student/resume',
      name: 'student-resume',
      component: StudentResumeView,
    },
    {
      path: '/student/applications',
      name: 'student-applications',
      component: StudentApplicationsView,
    },
    {
      path: '/student/jobs',
      name: 'student-jobs',
      component: StudentJobBoardView,
    },
    {
      path: '/student/interviews',
      name: 'student-interviews',
      component: StudentInterviewsView,
    },
    {
      path: '/student/intention',
      name: 'student-intention',
      component: StudentIntentionView,
    },
  ],
})

export default router

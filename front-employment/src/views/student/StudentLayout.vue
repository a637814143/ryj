<template>
  <div class="student-layout">
    <!-- 顶部导航栏 -->
    <header class="student-header">
      <div class="header-content">
        <div class="logo-section">
          <h1>🎓 学生工作台</h1>
          <span class="welcome-text">欢迎回来，{{ userInfo?.username }}</span>
        </div>
        <div class="header-actions">
          <el-badge :value="notificationCount" class="notification-badge">
            <el-button :icon="Bell" circle />
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="40" :src="userInfo?.avatar || '/default-avatar.png'" />
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  设置
                </el-dropdown-item>
                <el-dropdown-item command="switch" divided>
                  <el-icon><SwitchButton /></el-icon>
                  切换用户
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchFilled /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区 -->
    <div class="student-main">
      <!-- 侧边导航栏 - 卡片式 -->
      <aside class="student-sidebar">
        <div class="sidebar-cards">
          <div 
            v-for="menu in menuItems" 
            :key="menu.path"
            class="menu-card"
            :class="{ active: activeMenu === menu.path }"
            @click="handleMenuSelect(menu.path)"
          >
            <div class="card-icon" :style="{ background: menu.color }">
              <component :is="menu.icon" class="icon" />
            </div>
            <div class="card-content">
              <h4>{{ menu.title }}</h4>
              <p>{{ menu.description }}</p>
            </div>
            <el-badge 
              v-if="menu.badge && menu.badge > 0" 
              :value="menu.badge" 
              class="card-badge" 
            />
          </div>
        </div>
      </aside>

      <!-- 内容区域 -->
      <main class="student-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  Bell, 
  ArrowDown, 
  HomeFilled, 
  User, 
  Document, 
  Star,
  StarFilled,
  Search, 
  Calendar,
  Files,
  Message,
  Setting,
  SwitchButton,
  SwitchFilled
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

const userInfo = ref<any>(null);
const notificationCount = ref(3);

// 当前激活的菜单
const activeMenu = computed(() => route.path);

// 菜单项数据
const menuItems = ref([
  {
    path: '/student/dashboard',
    icon: HomeFilled,
    title: '工作台',
    description: '查看工作概览',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    badge: 0
  },
  {
    path: '/student/personal-info',
    icon: User,
    title: '个人信息',
    description: '管理个人资料',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    badge: 0
  },
  {
    path: '/student/resume',
    icon: Document,
    title: '完善简历',
    description: '让企业更了解你',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    badge: 0
  },
  {
    path: '/student/job-intention',
    icon: Star,
    title: '设置意向',
    description: '找到理想工作',
    color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    badge: 0
  },
  {
    path: '/student/recruitment',
    icon: Search,
    title: '浏览职位',
    description: '发现新机会',
    color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    badge: 0
  },
  {
    path: '/student/interview',
    icon: Calendar,
    title: '面试管理',
    description: '查看面试安排',
    color: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    badge: 0
  },
  {
    path: '/student/applications',
    icon: Files,
    title: '投递记录',
    description: '跟踪申请状态',
    color: 'linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%)',
    badge: 0
  },
  {
    path: '/student/messages',
    icon: Message,
    title: '消息中心',
    description: '查看最新消息',
    color: 'linear-gradient(135deg, #fdcbf1 0%, #e6dee9 100%)',
    badge: notificationCount.value
  }
]);

// 加载用户信息
const loadUserInfo = () => {
  const user = localStorage.getItem('user');
  if (user) {
    userInfo.value = JSON.parse(user);
  }
};

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  router.push(index);
};

// 下拉菜单命令处理
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/student/personal-info');
      break;
    case 'settings':
      ElMessage.info('设置功能开发中...');
      break;
    case 'switch':
      // 切换用户 - 清除当前用户信息并跳转到登录页
      localStorage.removeItem('user');
      localStorage.removeItem('infoCompleted');
      ElMessage.success('已退出当前账号，请重新登录');
      router.push('/login');
      break;
    case 'logout':
      // 退出登录
      localStorage.removeItem('user');
      localStorage.removeItem('infoCompleted');
      ElMessage.success('退出登录成功');
      router.push('/login');
      break;
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.student-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo-section h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.welcome-text {
  font-size: 14px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  cursor: pointer;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background 0.3s;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

:deep(.el-dropdown-menu__item--divided) {
  margin-top: 6px;
  border-top: 1px solid #e4e7ed;
}

.student-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.student-sidebar {
  width: 280px;
  background: #f5f7fa;
  padding: 20px;
  flex-shrink: 0;
  overflow-y: auto;
}

.sidebar-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-card {
  position: relative;
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.menu-card.active {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  border: 2px solid #667eea;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-icon .icon {
  font-size: 24px;
  color: white;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-content h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
}

.card-content p {
  margin: 0;
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-badge {
  position: absolute;
  top: 8px;
  right: 8px;
}

.student-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 滚动条样式 */
.student-sidebar::-webkit-scrollbar {
  width: 6px;
}

.student-sidebar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

.student-sidebar::-webkit-scrollbar-thumb {
  background: rgba(52, 152, 219, 0.5);
  border-radius: 3px;
}

.student-sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(52, 152, 219, 0.7);
}

@media (max-width: 1024px) {
  .welcome-text {
    display: none;
  }
  
  .student-content {
    padding: 16px;
  }
}
</style>


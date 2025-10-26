<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showUserMenu = ref(false)
const userMenuRef = ref<HTMLElement | null>(null)
const isLoggedIn = ref(false)
const currentUser = ref<{ username?: string; fullName?: string } | null>(null)

// 检查登录状态
const checkLoginStatus = () => {
  const userInfo = localStorage.getItem('userInfo')
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!(userInfo && token)
  
  if (userInfo) {
    try {
      currentUser.value = JSON.parse(userInfo)
    } catch (e) {
      currentUser.value = null
    }
  } else {
    currentUser.value = null
  }
}

// 获取显示名称
const displayName = computed(() => {
  if (!isLoggedIn.value || !currentUser.value) {
    return '用户'
  }
  return currentUser.value.fullName || currentUser.value.username || '用户'
})

// 切换用户菜单显示/隐藏
const toggleUserMenu = () => {
  checkLoginStatus() // 每次打开菜单时检查登录状态
  showUserMenu.value = !showUserMenu.value
}

// 关闭用户菜单
const closeUserMenu = () => {
  showUserMenu.value = false
}

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as Node | null
  if (userMenuRef.value && target && !userMenuRef.value.contains(target)) {
    closeUserMenu()
  }
}

// 登录
const login = () => {
  closeUserMenu()
  router.push('/login')
}

// 切换用户
const switchUser = () => {
  closeUserMenu()
  // 清除当前用户信息
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
  isLoggedIn.value = false
  currentUser.value = null
  // 跳转到登录页面
  router.push('/login')
}

// 退出登录
const logout = () => {
  closeUserMenu()
  // 清除用户信息
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
  isLoggedIn.value = false
  currentUser.value = null
  // 跳转到首页
  router.push('/home')
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  checkLoginStatus()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="app-shell">
    <div class="app-shell__backdrop" />
    <header class="app-shell__header">
      <div class="brand">
        <span class="brand__logo">RyJ</span>
        <div class="brand__meta">
          <h1>高校就业管理系统</h1>
          <p>University Career Nexus</p>
        </div>
      </div>

      <nav class="nav">
        <RouterLink to="/home" class="nav-link">首页</RouterLink>
        
        <div class="nav-dropdown">
          <span class="nav-link">学生专区 ▾</span>
          <div class="dropdown-menu">
            <RouterLink to="/student/overview">学生总览</RouterLink>
            <RouterLink to="/student/profile">个人档案</RouterLink>
            <RouterLink to="/student/resume">简历管理</RouterLink>
            <RouterLink to="/student/applications">求职申请</RouterLink>
            <RouterLink to="/student/interviews">面试管理</RouterLink>
            <RouterLink to="/student/intention">就业意向</RouterLink>
          </div>
        </div>
        
        <div class="nav-dropdown">
          <span class="nav-link">职位招聘 ▾</span>
          <div class="dropdown-menu">
            <RouterLink to="/jobs/list">职位列表</RouterLink>
            <RouterLink to="/jobs/search">职位搜索</RouterLink>
            <RouterLink to="/jobs/my-applications">我的申请</RouterLink>
          </div>
        </div>
        
        <div class="nav-dropdown">
          <span class="nav-link">企业专区 ▾</span>
          <div class="dropdown-menu">
            <RouterLink to="/employer/info">企业信息</RouterLink>
            <RouterLink to="/employer/post-job">发布职位</RouterLink>
            <RouterLink to="/employer/applications">应聘管理</RouterLink>
            <RouterLink to="/employer/interviews">面试安排</RouterLink>
          </div>
        </div>
        
        <div class="nav-dropdown">
          <span class="nav-link">就业指导 ▾</span>
          <div class="dropdown-menu">
            <RouterLink to="/guidance/records">指导记录</RouterLink>
            <RouterLink to="/guidance/resources">资源下载</RouterLink>
            <RouterLink to="/guidance/policies">就业政策</RouterLink>
          </div>
        </div>
        
        <RouterLink to="/notifications" class="nav-link">资讯动态</RouterLink>
      </nav>

      <div class="user-menu-container" ref="userMenuRef">
        <button class="user-menu-button" @click="toggleUserMenu">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span>{{ displayName }}</span>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'rotate': showUserMenu }">
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
        </button>

        <transition name="dropdown">
          <div v-if="showUserMenu" class="user-menu-dropdown">
            <!-- 未登录状态 -->
            <template v-if="!isLoggedIn">
              <button class="menu-item" @click="login">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                  <polyline points="10 17 15 12 10 7"></polyline>
                  <line x1="15" y1="12" x2="3" y2="12"></line>
                </svg>
                <span>登录</span>
              </button>
              <button class="menu-item" @click="router.push('/register')">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                  <circle cx="9" cy="7" r="4"></circle>
                  <line x1="19" y1="8" x2="19" y2="14"></line>
                  <line x1="22" y1="11" x2="16" y2="11"></line>
                </svg>
                <span>注册</span>
              </button>
            </template>

            <!-- 已登录状态 -->
            <template v-else>
              <button class="menu-item" @click="switchUser">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                  <circle cx="9" cy="7" r="4"></circle>
                  <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
                <span>切换用户</span>
              </button>
              <button class="menu-item logout" @click="logout">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                  <polyline points="16 17 21 12 16 7"></polyline>
                  <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
                <span>退出登录</span>
              </button>
            </template>
          </div>
        </transition>
      </div>
    </header>

    <main class="app-shell__content">
      <RouterView />
    </main>

    <footer class="app-shell__footer">
      <div class="footer__meta">
        <p>© 2026 高校就业管理系统 · 构建多角色协同的就业服务平台</p>
      </div>
      <div class="footer__links">
        <a href="#">使用指南</a>
        <a href="#">支持与反馈</a>
        <a href="#">隐私政策</a>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app-shell {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: radial-gradient(circle at 10% 20%, rgba(79, 172, 254, 0.18), transparent 40%),
    radial-gradient(circle at 90% 10%, rgba(124, 58, 237, 0.18), transparent 42%),
    #f5f7fb;
  color: #0f172a;
}

.app-shell__backdrop {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(15, 23, 42, 0.08), transparent 60%);
  pointer-events: none;
}

.app-shell__header {
  position: sticky;
  top: 0;
  z-index: 5;
  display: grid;
  grid-template-columns: 280px 1fr auto;
  align-items: center;
  gap: 2rem;
  padding: 1.75rem 4vw;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
}

.brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.brand__logo {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  color: white;
  font-weight: 700;
  font-size: 1.2rem;
  letter-spacing: 0.08em;
}

.brand__meta h1 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
  color: #0f172a;
}

.brand__meta p {
  margin: 0;
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #475569;
}

.nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
}

.nav-link {
  position: relative;
  color: #1e293b;
  text-decoration: none;
  font-weight: 600;
  letter-spacing: 0.03em;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
  white-space: nowrap;
}

.nav-link:hover {
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
}

.nav > a.nav-link.router-link-active {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1), rgba(139, 92, 246, 0.1));
  color: #2563eb;
}

/* 下拉菜单容器 */
.nav-dropdown {
  position: relative;
}

.nav-dropdown:hover .nav-link {
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
}

.nav-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

/* 下拉菜单内容 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 180px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.15);
  padding: 0.5rem;
  z-index: 100;
  border: 1px solid rgba(148, 163, 184, 0.12);
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.25s ease;
  margin-top: 0.5rem;
}

.dropdown-menu a {
  display: block;
  padding: 0.75rem 1rem;
  color: #475569;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.dropdown-menu a:hover {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(139, 92, 246, 0.08));
  color: #2563eb;
}

.dropdown-menu a.router-link-active {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(139, 92, 246, 0.12));
  color: #2563eb;
  font-weight: 600;
}

/* 用户菜单容器 */
.user-menu-container {
  position: relative;
}

.user-menu-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  color: white;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.25);
  transition: all 0.2s ease;
  max-width: 200px;
}

.user-menu-button span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.user-menu-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgba(37, 99, 235, 0.3);
}

.user-menu-button svg:last-child {
  transition: transform 0.3s ease;
}

.user-menu-button svg:last-child.rotate {
  transform: rotate(180deg);
}

/* 用户菜单下拉框 */
.user-menu-dropdown {
  position: absolute;
  top: calc(100% + 0.75rem);
  right: 0;
  min-width: 200px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.15);
  padding: 0.5rem;
  z-index: 100;
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  border: none;
  background: transparent;
  color: #1e293b;
  font-weight: 500;
  font-size: 0.95rem;
  text-align: left;
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(139, 92, 246, 0.08));
  color: #2563eb;
}

.menu-item.logout {
  color: #dc2626;
}

.menu-item.logout:hover {
  background: rgba(220, 38, 38, 0.08);
  color: #b91c1c;
}

.menu-item svg {
  flex-shrink: 0;
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.25s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-5px) scale(0.98);
}

.app-shell__content {
  position: relative;
  z-index: 1;
  flex: 1;
  padding: 2.5rem 4vw 3.5rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.app-shell__footer {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 4vw 2rem;
  color: #475569;
  font-size: 0.85rem;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.85));
}

.footer__links {
  display: flex;
  gap: 1.5rem;
}

.footer__links a {
  color: #475569;
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer__links a:hover {
  color: #1d4ed8;
}

@media (max-width: 1024px) {
  .app-shell__header {
    grid-template-columns: 1fr;
    justify-items: center;
    text-align: center;
    gap: 1.5rem;
  }

  .nav {
    flex-wrap: wrap;
    justify-content: center;
    max-width: 100%;
  }

  .nav-link {
    font-size: 0.85rem;
    padding: 0.6rem 0.8rem;
  }

  .dropdown-menu {
    min-width: 150px;
  }

  .dropdown-menu a {
    font-size: 0.85rem;
    padding: 0.6rem 0.8rem;
  }

  .app-shell__footer {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}

@media (max-width: 640px) {
  .app-shell__content {
    padding: 1.5rem 1.25rem 2.5rem;
  }
}
</style>

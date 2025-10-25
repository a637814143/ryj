<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'

const menu = [
  { name: '概览', path: '/' },
  { name: '学生中心', path: '/students' },
  { name: '岗位广场', path: '/jobs' },
  { name: '企业管理', path: '/employers' },
  { name: '指导服务', path: '/teachers' },
]

const route = useRoute()
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <h1>就业管理系统</h1>
      <nav>
        <RouterLink v-for="item in menu" :key="item.path" :to="item.path" :class="{ active: route.path === item.path }">
          {{ item.name }}
        </RouterLink>
      </nav>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  min-height: 100vh;
  background: #eef2ff;
  color: #0f172a;
}

.sidebar {
  background: #1e293b;
  color: #e2e8f0;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar h1 {
  font-size: 20px;
  margin: 0;
}

.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sidebar a {
  color: inherit;
  text-decoration: none;
  padding: 10px 12px;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.sidebar a:hover {
  background: rgba(148, 163, 184, 0.2);
}

.sidebar a.active {
  background: #2563eb;
  color: #fff;
}

.content {
  overflow-y: auto;
  background: #f1f5f9;
}

@media (max-width: 960px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }

  .sidebar nav {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>

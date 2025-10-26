<template>
  <div class="app">
    <header class="app__header">
      <h1>大学生就业管理系统</h1>
      <nav>
        <RouterLink to="/login">登录</RouterLink>
        <RouterLink to="/register">注册</RouterLink>
      </nav>
    </header>
    <main class="app__content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #eef2ff 0%, #f8fafc 100%);
  color: #1f2937;
}

.app__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.1);
}

.app__header h1 {
  margin: 0;
  font-size: 1.5rem;
}

nav {
  display: flex;
  gap: 1rem;
}

nav a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

nav a.router-link-active,
nav a:hover {
  background-color: #2563eb;
  color: #fff;
}

.app__content {
  display: flex;
  justify-content: center;
  padding: 3rem 1rem;
}

@media (max-width: 600px) {
  .app__header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  nav {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>

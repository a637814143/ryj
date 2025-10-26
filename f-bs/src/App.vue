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
        <RouterLink to="/home">公共首页</RouterLink>
        <RouterLink to="/login">登录</RouterLink>
        <RouterLink to="/register">注册</RouterLink>
      </nav>

      <div class="cta">
        <RouterLink to="/home" class="cta__primary">进入门户</RouterLink>
      </div>
    </header>

    <main class="app-shell__content">
      <RouterView />
    </main>

    <footer class="app-shell__footer">
      <div class="footer__meta">
        <p>© {{ new Date().getFullYear() }} 高校就业管理系统 · 构建多角色协同的就业服务平台</p>
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
  gap: 1.5rem;
  font-size: 0.98rem;
}

.nav a {
  position: relative;
  color: #1e293b;
  text-decoration: none;
  font-weight: 600;
  letter-spacing: 0.04em;
  padding-bottom: 0.35rem;
}

.nav a::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.25s ease;
  border-radius: 999px;
}

.nav a.router-link-active::after,
.nav a:hover::after {
  transform: scaleX(1);
}

.cta__primary {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.6rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  color: white;
  font-weight: 600;
  text-decoration: none;
  box-shadow: 0 12px 25px rgba(37, 99, 235, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cta__primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 32px rgba(37, 99, 235, 0.28);
}

.app-shell__content {
  position: relative;
  z-index: 1;
  flex: 1;
  padding: 2.5rem 4vw 3.5rem;
  display: flex;
  flex-direction: column;
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

<template>
  <div class="app">
    <header class="app__header">
      <h1>高校就业管理系统</h1>
    </header>
    <main class="app__content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #1f2937;
  display: flex;
  flex-direction: column;
}

.app__header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.app__header h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.app__content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem 1rem;
}

@media (max-width: 600px) {
  .app__header {
    padding: 1rem 1.5rem;
  }
  
  .app__header h1 {
    font-size: 1.25rem;
  }
  
  .app__content {
    padding: 1.5rem 1rem;
  }
}
</style>

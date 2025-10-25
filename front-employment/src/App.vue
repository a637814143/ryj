<script setup lang="ts">
import { RouterView } from 'vue-router';
// App.vue 仅作为路由容器，不包含全局导航
</script>

<template>
  <!-- 不显示全局header，各页面自行管理导航 -->
  <RouterView />
</template>

<style scoped>
/* App.vue 不再包含全局样式，各页面自行管理 */
</style>

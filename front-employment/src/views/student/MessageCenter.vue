<template>
  <div class="message-center">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">📬 消息中心</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="message-content">
      <!-- 左侧消息分类 -->
      <el-col :span="6">
        <el-card shadow="hover" class="category-card">
          <el-menu :default-active="activeCategory" @select="handleCategoryChange">
            <el-menu-item index="all">
              <el-icon><Message /></el-icon>
              <span>全部消息</span>
              <el-badge :value="stats.total" class="badge" />
            </el-menu-item>
            <el-menu-item index="system">
              <el-icon><Bell /></el-icon>
              <span>系统通知</span>
              <el-badge :value="stats.system" class="badge" />
            </el-menu-item>
            <el-menu-item index="interview">
              <el-icon><Calendar /></el-icon>
              <span>面试通知</span>
              <el-badge :value="stats.interview" class="badge" />
            </el-menu-item>
            <el-menu-item index="application">
              <el-icon><Document /></el-icon>
              <span>申请进度</span>
              <el-badge :value="stats.application" class="badge" />
            </el-menu-item>
            <el-menu-item index="company">
              <el-icon><OfficeBuilding /></el-icon>
              <span>企业消息</span>
              <el-badge :value="stats.company" class="badge" />
            </el-menu-item>
          </el-menu>

          <el-divider />

          <el-button type="primary" style="width: 100%;" @click="markAllRead">
            全部标为已读
          </el-button>
        </el-card>
      </el-col>

      <!-- 右侧消息列表 -->
      <el-col :span="18">
        <el-card shadow="hover" class="message-list-card">
          <template #header>
            <div class="card-header">
              <span>{{ getCategoryTitle(activeCategory) }}</span>
              <el-button-group>
                <el-button size="small" @click="filterUnread = !filterUnread">
                  {{ filterUnread ? '显示全部' : '只看未读' }}
                </el-button>
                <el-button size="small" :icon="Delete" @click="batchDelete">删除已读</el-button>
              </el-button-group>
            </div>
          </template>

          <div class="message-list">
            <div
              v-for="message in filteredMessages"
              :key="message.id"
              class="message-item"
              :class="{ unread: !message.isRead }"
              @click="viewMessage(message)"
            >
              <div class="message-icon" :style="{ background: getIconColor(message.type) }">
                <el-icon>
                  <component :is="getIconComponent(message.type)" />
                </el-icon>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <h4>{{ message.title }}</h4>
                  <span class="message-time">{{ message.time }}</span>
                </div>
                <p class="message-preview">{{ message.content }}</p>
                <div class="message-tags">
                  <el-tag :type="getMessageType(message.type)" size="small">
                    {{ getTypeName(message.type) }}
                  </el-tag>
                  <el-tag v-if="!message.isRead" type="danger" size="small">未读</el-tag>
                </div>
              </div>
              <div class="message-actions">
                <el-button
                  size="small"
                  :icon="message.isRead ? View : CircleCheck"
                  @click.stop="toggleRead(message)"
                >
                  {{ message.isRead ? '查看' : '标记已读' }}
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  :icon="Delete"
                  @click.stop="deleteMessage(message)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />

          <el-pagination
            v-if="filteredMessages.length > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            style="margin-top: 20px; justify-content: center"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 消息详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="currentMessage?.title" width="700px">
      <div v-if="currentMessage" class="message-detail">
        <div class="detail-meta">
          <el-tag :type="getMessageType(currentMessage.type)" size="small">
            {{ getTypeName(currentMessage.type) }}
          </el-tag>
          <span class="detail-time">{{ currentMessage.time }}</span>
        </div>
        <el-divider />
        <div class="detail-content">
          {{ currentMessage.content }}
        </div>
        <div v-if="currentMessage.actions" class="detail-actions">
          <el-divider />
          <el-button
            v-for="action in currentMessage.actions"
            :key="action.label"
            :type="action.type"
            @click="handleAction(action)"
          >
            {{ action.label }}
          </el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Message,
  Bell,
  Calendar,
  Document,
  OfficeBuilding,
  Delete,
  View,
  CircleCheck
} from '@element-plus/icons-vue';

const router = useRouter();

const activeCategory = ref('all');
const filterUnread = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(15);

const stats = ref({
  total: 8,
  system: 2,
  interview: 3,
  application: 2,
  company: 1
});

const messages = ref([
  {
    id: 1,
    type: 'interview',
    title: '面试邀请 - 阿里巴巴 Java开发工程师',
    content: '恭喜您！您申请的"Java开发工程师"职位已通过初审，我们诚挚邀请您参加面试。面试时间：2024年12月20日 14:00，面试地点：线上面试（腾讯会议）。请提前准备好简历和作品集。',
    time: '2024-12-15 10:30',
    isRead: false,
    actions: [
      { label: '确认参加', type: 'primary', action: 'confirmInterview' },
      { label: '查看详情', type: 'default', action: 'viewInterview' }
    ]
  },
  {
    id: 2,
    type: 'application',
    title: '申请进度更新 - 腾讯科技',
    content: '您申请的"前端开发工程师"职位简历已被查看，HR正在评估中，请耐心等待。',
    time: '2024-12-14 15:20',
    isRead: false
  },
  {
    id: 3,
    type: 'system',
    title: '系统通知：简历优化建议',
    content: '系统检测到您的简历完整度为85%，建议补充项目经历和技能标签，提高简历竞争力。',
    time: '2024-12-13 09:00',
    isRead: true
  },
  {
    id: 4,
    type: 'company',
    title: '字节跳动向您推荐了新职位',
    content: '根据您的求职意向，字节跳动为您推荐了"Python开发工程师"职位，快来看看吧！',
    time: '2024-12-12 16:45',
    isRead: false
  },
  {
    id: 5,
    type: 'interview',
    title: '面试结果通知 - 网易',
    content: '感谢您参加我们的面试。经过综合评估，您已通过本轮面试，我们将安排下一轮面试，请保持电话畅通。',
    time: '2024-12-11 11:20',
    isRead: true
  }
]);

const filteredMessages = computed(() => {
  let result = messages.value;
  
  // 分类筛选
  if (activeCategory.value !== 'all') {
    result = result.filter(msg => msg.type === activeCategory.value);
  }
  
  // 未读筛选
  if (filterUnread.value) {
    result = result.filter(msg => !msg.isRead);
  }
  
  return result;
});

const detailDialogVisible = ref(false);
const currentMessage = ref<any>(null);

const handleCategoryChange = (index: string) => {
  activeCategory.value = index;
  currentPage.value = 1;
};

const getCategoryTitle = (category: string) => {
  const titles: Record<string, string> = {
    all: '全部消息',
    system: '系统通知',
    interview: '面试通知',
    application: '申请进度',
    company: '企业消息'
  };
  return titles[category] || '全部消息';
};

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    system: '系统',
    interview: '面试',
    application: '申请',
    company: '企业'
  };
  return names[type] || '消息';
};

const getMessageType = (type: string) => {
  const types: Record<string, any> = {
    system: 'info',
    interview: 'success',
    application: 'warning',
    company: 'primary'
  };
  return types[type] || 'info';
};

const getIconComponent = (type: string) => {
  const icons: Record<string, any> = {
    system: Bell,
    interview: Calendar,
    application: Document,
    company: OfficeBuilding
  };
  return icons[type] || Message;
};

const getIconColor = (type: string) => {
  const colors: Record<string, string> = {
    system: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    interview: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    application: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    company: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  };
  return colors[type] || '#409eff';
};

const viewMessage = (message: any) => {
  currentMessage.value = message;
  message.isRead = true;
  detailDialogVisible.value = true;
  updateStats();
};

const toggleRead = (message: any) => {
  message.isRead = !message.isRead;
  ElMessage.success(message.isRead ? '已标记为已读' : '已标记为未读');
  updateStats();
};

const deleteMessage = (message: any) => {
  ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = messages.value.findIndex(msg => msg.id === message.id);
    if (index !== -1) {
      messages.value.splice(index, 1);
      ElMessage.success('删除成功');
      updateStats();
    }
  });
};

const markAllRead = () => {
  messages.value.forEach(msg => {
    msg.isRead = true;
  });
  ElMessage.success('已全部标记为已读');
  updateStats();
};

const batchDelete = () => {
  const readMessages = messages.value.filter(msg => msg.isRead);
  if (readMessages.length === 0) {
    ElMessage.warning('没有已读消息可删除');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除 ${readMessages.length} 条已读消息吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    messages.value = messages.value.filter(msg => !msg.isRead);
    ElMessage.success('批量删除成功');
    updateStats();
  });
};

const updateStats = () => {
  const unreadMessages = messages.value.filter(msg => !msg.isRead);
  stats.value.total = unreadMessages.length;
  stats.value.system = unreadMessages.filter(msg => msg.type === 'system').length;
  stats.value.interview = unreadMessages.filter(msg => msg.type === 'interview').length;
  stats.value.application = unreadMessages.filter(msg => msg.type === 'application').length;
  stats.value.company = unreadMessages.filter(msg => msg.type === 'company').length;
};

const handleAction = (action: any) => {
  switch (action.action) {
    case 'confirmInterview':
      ElMessage.success('已确认参加面试');
      router.push('/student/interview');
      break;
    case 'viewInterview':
      router.push('/student/interview');
      break;
  }
  detailDialogVisible.value = false;
};
</script>

<style scoped>
.message-center {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.message-content {
  margin-top: 24px;
}

.category-card {
  position: sticky;
  top: 24px;
}

.badge {
  margin-left: auto;
}

.message-list-card {
  min-height: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.message-item.unread {
  background: #ecf5ff;
  border-color: #b3d8ff;
}

.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.message-preview {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-tags {
  display: flex;
  gap: 8px;
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.message-detail {
  padding: 20px 0;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-time {
  font-size: 14px;
  color: #909399;
}

.detail-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-actions {
  margin-top: 20px;
}
</style>


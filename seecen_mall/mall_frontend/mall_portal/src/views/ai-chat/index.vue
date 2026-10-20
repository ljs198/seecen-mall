<template>
  <div class="ai-chat-page">
    <aside class="session-sidebar">
      <div class="sidebar-header">
        <el-button text :icon="ArrowLeftIcon" @click="goHome">返回商城</el-button>
      </div>
      <el-button type="primary" class="new-chat-btn" @click="startNewChat">
        新建对话
      </el-button>
      <div
        v-for="s in sessions"
        :key="s.sessionId"
        class="session-item"
        :class="{ active: s.sessionId === activeSessionId }"
        @click="selectSession(s.sessionId)"
      >
        <div class="session-title">{{ truncate(s.lastMessage) }}</div>
        <el-button type="danger" text size="small" @click.stop="handleDeleteSession(s.sessionId)">
          删除
        </el-button>
      </div>
    </aside>

    <section class="chat-area">
      <div class="messages-area" ref="messagesContainer">
        <div v-if="messages.length === 0" class="empty-chat">
          向 AI 客服提问，比如“Spring Boot 微服务开发多少钱？”
        </div>
        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          class="message-row"
          :class="msg.role"
        >
          <div class="message-bubble">
            <div class="message-role">{{ msg.role === 'user' ? '我' : 'AI 客服' }}</div>
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="请输入你的问题"
          :disabled="aiResponding"
          @keydown.enter.exact.prevent="handleSend"
        />
        <el-button type="primary" :loading="aiResponding" @click="handleSend">发送</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { deleteSession, getSessionMessages, getSessions } from '../../api/ai'
import { handleSseChat } from '../../composables/useSseChat'

const router = useRouter()
const ArrowLeftIcon = ArrowLeft

const sessions = ref([])
const activeSessionId = ref(null)
const messages = ref([])
const inputText = ref('')
const aiResponding = ref(false)
const messagesContainer = ref(null)

onMounted(() => {
  fetchSessions()
})

async function fetchSessions() {
  const res = await getSessions()
  sessions.value = (res && res.data) || []
}

function startNewChat() {
  activeSessionId.value = null
  messages.value = []
  inputText.value = ''
}

async function selectSession(sessionId) {
  activeSessionId.value = sessionId
  const res = await getSessionMessages(sessionId)
  messages.value = (res && res.data) || []
  nextTick(scrollToBottom)
}

async function handleDeleteSession(sessionId) {
  try {
    await ElMessageBox.confirm('确定删除该对话记录吗？', '删除对话', {
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteSession(sessionId)
  ElMessage.success('对话已删除')
  await fetchSessions()
  if (activeSessionId.value === sessionId) {
    startNewChat()
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || aiResponding.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  aiResponding.value = true
  let assistantIndex = -1

  await handleSseChat({
    sessionId: activeSessionId.value,
    message: text,
    onSessionId: (id) => {
      activeSessionId.value = id
    },
    onContent: (chunk) => {
      if (assistantIndex < 0) {
        messages.value.push({ role: 'assistant', content: '' })
        assistantIndex = messages.value.length - 1
      }
      messages.value[assistantIndex].content += chunk
      nextTick(scrollToBottom)
    },
    onDone: async () => {
      aiResponding.value = false
      await fetchSessions()
    },
    onError: () => {
      messages.value.push({ role: 'assistant', content: '抱歉，AI 响应失败，请稍后再试。' })
      aiResponding.value = false
    }
  })
}

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

function truncate(text) {
  if (!text) return '（空会话）'
  return text.length > 18 ? text.slice(0, 18) + '...' : text
}

/** 关闭 / 离开完整对话页面，返回商城首页 */
function goHome() {
  router.push('/')
}
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  height: calc(100vh - 64px);
  background: linear-gradient(180deg, #1a1b3a 0%, #0f1444 100%);
}
.session-sidebar {
  width: 280px;
  padding: 16px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border-right: 1.5px solid #d4b996;
  overflow-y: auto;
}
.sidebar-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.new-chat-btn {
  width: 100%;
  margin-bottom: 12px;
}
.session-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  transition: background 0.2s;
}
.session-item:hover {
  background: rgba(200, 144, 27, 0.12);
}
.session-item.active {
  background: rgba(200, 144, 27, 0.18);
  border: 1px solid #c8901b;
}
.session-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #5d4e37;
}
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}
.messages-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
.empty-chat {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8b6f47;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-size: 15px;
}
.message-row {
  display: flex;
  margin-bottom: 16px;
}
.message-row.user {
  justify-content: flex-end;
}
.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1px solid #d4b996;
  line-height: 1.6;
  word-break: break-word;
  color: #3e2723;
}
.message-row.user .message-bubble {
  color: #fff;
  background: linear-gradient(135deg, #c8901b 0%, #b87f0a 100%);
  border-color: #b87f0a;
}
.message-row.user .message-role {
  color: rgba(255, 255, 255, 0.85);
}
.message-role {
  font-size: 12px;
  color: #8b6f47;
  margin-bottom: 4px;
}
.input-area {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border-top: 1.5px solid #d4b996;
}
.input-area .el-textarea {
  flex: 1;
}

/* 中秋风格：新建对话 + 发送按钮 */
:deep(.new-chat-btn.el-button--primary),
:deep(.input-area .el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
}

:deep(.new-chat-btn.el-button--primary:hover),
:deep(.input-area .el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

/* 中秋风格：输入框金边 */
:deep(.input-area .el-textarea__inner) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.input-area .el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

@media (max-width: 767px) {
  .session-sidebar {
    display: none;
  }
  .message-bubble {
    max-width: 88%;
  }
}
</style>

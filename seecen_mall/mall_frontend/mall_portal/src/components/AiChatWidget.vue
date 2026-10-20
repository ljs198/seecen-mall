<template>
  <div class="ai-chat-widget" :style="{ left: posX + 'px', top: posY + 'px' }">
    <transition name="fade">
      <div v-if="open" class="widget-panel" :class="{ below: panelBelow, left: panelLeft }">
        <div class="widget-header">
          <span>AI 智能客服</span>
          <div class="widget-header-actions">
            <el-button
              text
              size="small"
              :icon="FullScreenIcon"
              title="打开完整对话"
              @click="openFullChat"
            />
            <el-button text size="small" @click="open = false">×</el-button>
          </div>
        </div>

        <div class="widget-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="widget-empty">
            您好，我是商城 AI 客服，可以问我商品价格或订单状态。
          </div>
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="widget-message"
            :class="msg.role"
          >
            {{ msg.content }}
          </div>
        </div>

        <div class="widget-input">
          <el-input
            v-model="inputText"
            placeholder="输入问题"
            size="small"
            :disabled="aiResponding"
            @keydown.enter.exact.prevent="handleSend"
          />
          <el-button type="primary" size="small" :loading="aiResponding" @click="handleSend">
            发送
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 闲置聊天气泡 -->
    <transition name="bubble">
      <div v-if="isIdle && idleMessage" class="idle-bubble" :class="{ below: bubbleBelow, left: bubbleLeft }">
        {{ idleMessage }}
      </div>
    </transition>

    <button
      v-if="!open"
      class="widget-trigger"
      :class="{ dragging: isDragging, idle: isIdle }"
      @pointerdown.prevent="onPointerDown"
      aria-label="打开AI客服"
    >
      <!-- 拖拽或闲置时显示大兔子（桌宠效果，可爱风） -->
      <div v-if="isDragging || isIdle" class="drag-rabbit">
        <svg viewBox="0 0 80 100" width="80" height="100" aria-hidden="true">
          <!-- 长耳朵（柔软感） -->
          <ellipse cx="28" cy="20" rx="5.5" ry="17" fill="#fff8e7" transform="rotate(-10 28 20)"/>
          <ellipse cx="52" cy="20" rx="5.5" ry="17" fill="#fff8e7" transform="rotate(10 52 20)"/>
          <ellipse cx="28" cy="24" rx="2.8" ry="12" fill="#ffb6c1" transform="rotate(-10 28 24)"/>
          <ellipse cx="52" cy="24" rx="2.8" ry="12" fill="#ffb6c1" transform="rotate(10 52 24)"/>
          <!-- 圆滚滚身体 -->
          <ellipse cx="40" cy="74" rx="18" ry="14" fill="#fff8e7"/>
          <!-- 小爪子 -->
          <ellipse cx="32" cy="84" rx="4" ry="3" fill="#fff8e7"/>
          <ellipse cx="48" cy="84" rx="4" ry="3" fill="#fff8e7"/>
          <!-- 圆脸 -->
          <circle cx="40" cy="44" r="17" fill="#fff8e7"/>
          <!-- 大腮红 -->
          <ellipse cx="27" cy="48" rx="4" ry="3" fill="#ffb6c1" opacity="0.6"/>
          <ellipse cx="53" cy="48" rx="4" ry="3" fill="#ffb6c1" opacity="0.6"/>
          <!-- 大眼睛+双高光 -->
          <circle cx="33" cy="42" r="3" fill="#1a1b3a"/>
          <circle cx="47" cy="42" r="3" fill="#1a1b3a"/>
          <circle cx="34" cy="41" r="1.2" fill="#fff"/>
          <circle cx="48" cy="41" r="1.2" fill="#fff"/>
          <circle cx="32" cy="43.5" r="0.5" fill="#fff"/>
          <circle cx="46" cy="43.5" r="0.5" fill="#fff"/>
          <!-- 小鼻子 -->
          <path d="M38 48 Q40 50 42 48 Q40 52 38 48" fill="#ffb6c1"/>
          <!-- 微笑嘴巴 -->
          <path d="M35 52 Q40 56 45 52" stroke="#1a1b3a" stroke-width="1" fill="none" stroke-linecap="round"/>
          <!-- 小灯笼 -->
          <ellipse cx="40" cy="78" rx="6" ry="8" fill="#c0392b"/>
          <rect x="36" y="71" width="8" height="2" fill="#ffd700"/>
          <rect x="36" y="84" width="8" height="2" fill="#ffd700"/>
          <text x="40" y="81" font-size="6" fill="#ffd700" text-anchor="middle" font-weight="bold">月</text>
          <!-- 小尾巴 -->
          <circle cx="58" cy="74" r="4" fill="#fff8e7"/>
        </svg>
      </div>
      <!-- 原始玉兔图标（可爱风） -->
      <svg v-else viewBox="0 0 40 40" width="32" height="32" aria-hidden="true">
        <!-- 长耳朵（更圆润） -->
        <ellipse cx="15" cy="12" rx="3.5" ry="9" fill="#fff8e7" transform="rotate(-12 15 12)"/>
        <ellipse cx="25" cy="12" rx="3.5" ry="9" fill="#fff8e7" transform="rotate(12 25 12)"/>
        <ellipse cx="15" cy="14" rx="1.8" ry="6" fill="#ffb6c1" transform="rotate(-12 15 14)"/>
        <ellipse cx="25" cy="14" rx="1.8" ry="6" fill="#ffb6c1" transform="rotate(12 25 14)"/>
        <!-- 圆脸 -->
        <circle cx="20" cy="25" r="11" fill="#fff8e7"/>
        <!-- 腮红 -->
        <ellipse cx="13" cy="27" rx="2.5" ry="1.8" fill="#ffb6c1" opacity="0.6"/>
        <ellipse cx="27" cy="27" rx="2.5" ry="1.8" fill="#ffb6c1" opacity="0.6"/>
        <!-- 大眼睛+高光 -->
        <circle cx="16" cy="24" r="2.2" fill="#1a1b3a"/>
        <circle cx="24" cy="24" r="2.2" fill="#1a1b3a"/>
        <circle cx="16.7" cy="23.3" r="0.8" fill="#fff"/>
        <circle cx="24.7" cy="23.3" r="0.8" fill="#fff"/>
        <!-- 小鼻子 -->
        <ellipse cx="20" cy="28" rx="1.2" ry="0.9" fill="#ffb6c1"/>
        <!-- 微笑嘴巴 -->
        <path d="M18 30 Q20 32 22 30" stroke="#1a1b3a" stroke-width="0.8" fill="none" stroke-linecap="round"/>
      </svg>
    </button>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { FullScreen } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { handleSseChat } from '../composables/useSseChat'

const router = useRouter()
const userStore = useUserStore()
const FullScreenIcon = FullScreen
const open = ref(false)

// === 拖拽桌宠功能 ===
const posX = ref(0)
const posY = ref(0)
const isDragging = ref(false)
const dragOffsetX = ref(0)
const dragOffsetY = ref(0)
const dragStartX = ref(0)
const dragStartY = ref(0)
const hasMoved = ref(false)
const mouseDownTime = ref(0)

// === 闲置桌宠动画 ===
const isIdle = ref(false)
const idleMessage = ref('')
const lastInteractTime = ref(Date.now())
let idleTimer = null
let idleTimeout1 = null
let idleTimeout2 = null

// 闲置时随机说的话
const idleMessages = [
  '我是你的AI助手，小兔~',
  '拖动我试试看会发生什么？',
  '商品真多，好难选啊...',
  '这个想买，那个也想买，好纠结啊！',
  '买完这个，这个月钱包又空空了...',
  '今天有什么想问我的吗？',
  '中秋节快乐！要吃月饼吗？',
  '我在这里等你很久啦~',
  '别光顾着逛，也要休息一下哦',
  '猜猜我能帮你做什么？',
  '哇，这个商品好漂亮啊！',
  '购物车是不是又满了？',
  '钱包说：求求你放过我吧~',
  '今天的月亮好圆好亮呀',
  '需要我帮你查订单吗？',
  '月饼什么馅的最好吃呢？',
  '要不要把我加入购物车呀~',
  '今天也是元气满满的一天！',
  '你的购物清单列好了吗？',
  '我记得你上次看中了一个好东西哦',
  '月末了，要理性消费哦~',
  '月圆人团圆，中秋快乐！',
  '我也能帮你推荐商品哦，试试看？',
  '打折的时候最容易冲动消费了...',
  '你今天的心情怎么样呀？',
  '悄悄告诉你，今天有新品上架哦',
  '我在月光下等你好久啦',
  '选礼物的话，我可以帮忙参考~',
  '购物车满了记得结算哦',
  '夜深了，早点休息吧~',
]

// 面板方向：上方不够时往下，左右朝中间展开
const panelBelow = computed(() => {
  // 面板高 480 + 间距 12 = 492
  if (posY.value < 492) {
    return posY.value + 56 + 492 <= window.innerHeight
  }
  return false
})
const panelLeft = computed(() => posX.value + 48 < window.innerWidth / 2)

// 气泡方向：朝中间展开
const bubbleBelow = computed(() => posY.value < 130)
const bubbleLeft = computed(() => posX.value + 48 < window.innerWidth / 2)

onMounted(() => {
  // 初始化到左侧偏中位置
  posX.value = 24
  posY.value = Math.round(window.innerHeight * 0.45)
  window.addEventListener('resize', onResize)
  startIdleTimer()
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  document.removeEventListener('pointermove', onPointerMove)
  document.removeEventListener('pointerup', onPointerUp)
  document.removeEventListener('pointercancel', onPointerUp)
  stopIdleTimer()
})

// === 闲置动画定时器 ===
function startIdleTimer() {
  stopIdleTimer()
  idleTimer = setInterval(() => {
    // 面板打开、正在拖拽或已经在闲置动画中时不触发
    if (open.value || isDragging.value || isIdle.value) {
      lastInteractTime.value = Date.now()
      return
    }
    // 闲置超过 10 秒触发动画
    if (Date.now() - lastInteractTime.value > 10000) {
      triggerIdleAnimation()
    }
  }, 1000)
}

function stopIdleTimer() {
  if (idleTimer) { clearInterval(idleTimer); idleTimer = null }
  if (idleTimeout1) { clearTimeout(idleTimeout1); idleTimeout1 = null }
  if (idleTimeout2) { clearTimeout(idleTimeout2); idleTimeout2 = null }
}

/** 触发闲置动画：变大兔子 + 随机说话，说完恢复原样 */
function triggerIdleAnimation() {
  // 确保大兔子(96x110)完全在视口内
  posX.value = Math.max(0, Math.min(window.innerWidth - 96, posX.value))
  // 确保气泡有空间：上方或下方至少一侧能放下气泡(约130px)
  const minBubble = 130
  const maxPosY = window.innerHeight - 110 - minBubble
  if (maxPosY >= minBubble) {
    posY.value = Math.max(minBubble, Math.min(maxPosY, posY.value))
  } else {
    // 窗口太小，居中
    posY.value = Math.max(0, Math.round((window.innerHeight - 110) / 2))
  }

  isIdle.value = true
  // 随机第一句话
  idleMessage.value = idleMessages[Math.floor(Math.random() * idleMessages.length)]

  // 4秒后换第二句话
  idleTimeout1 = setTimeout(() => {
    if (isIdle.value) {
      let msg = idleMessages[Math.floor(Math.random() * idleMessages.length)]
      // 尽量不重复
      while (msg === idleMessage.value && idleMessages.length > 1) {
        msg = idleMessages[Math.floor(Math.random() * idleMessages.length)]
      }
      idleMessage.value = msg
    }
  }, 4000)

  // 8秒后恢复原样（每句4秒，共两句）
  idleTimeout2 = setTimeout(() => {
    isIdle.value = false
    idleMessage.value = ''
    lastInteractTime.value = Date.now()
  }, 8000)
}

function onResize() {
  // 按大兔子尺寸(96x110)限制，确保闲置/拖拽时也不超出视口
  posX.value = Math.max(0, Math.min(window.innerWidth - 96, posX.value))
  posY.value = Math.max(0, Math.min(window.innerHeight - 110, posY.value))
}

/** 指针按下（鼠标/触摸通用）：开始拖拽，图标变成兔子 */
function onPointerDown(e) {
  // 取消闲置动画
  isIdle.value = false
  idleMessage.value = ''
  if (idleTimeout1) { clearTimeout(idleTimeout1); idleTimeout1 = null }
  if (idleTimeout2) { clearTimeout(idleTimeout2); idleTimeout2 = null }

  isDragging.value = true
  hasMoved.value = false
  mouseDownTime.value = Date.now()
  lastInteractTime.value = Date.now()
  dragStartX.value = e.clientX
  dragStartY.value = e.clientY
  dragOffsetX.value = e.clientX - posX.value
  dragOffsetY.value = e.clientY - posY.value
  // 捕获指针：手指/鼠标移出元素后仍能持续收到移动事件。
  // 必须绑定在 currentTarget（按钮本身），否则拖拽中大小兔子图标切换会让捕获丢失
  try { e.currentTarget.setPointerCapture(e.pointerId) } catch (err) { /* 不支持捕获时忽略 */ }
  document.addEventListener('pointermove', onPointerMove)
  document.addEventListener('pointerup', onPointerUp)
  document.addEventListener('pointercancel', onPointerUp)
}

/** 拖拽中：兔子跟随指针移动 */
function onPointerMove(e) {
  if (!isDragging.value) return
  const dx = e.clientX - dragStartX.value
  const dy = e.clientY - dragStartY.value
  if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
    hasMoved.value = true
  }
  // 按大兔子尺寸(96x110)限制，确保拖拽时不超出视口
  posX.value = Math.max(0, Math.min(window.innerWidth - 96, e.clientX - dragOffsetX.value))
  posY.value = Math.max(0, Math.min(window.innerHeight - 110, e.clientY - dragOffsetY.value))
}

/** 指针松开/被打断：恢复原样，快速点按（<300ms且未移动）才打开聊天 */
function onPointerUp() {
  document.removeEventListener('pointermove', onPointerMove)
  document.removeEventListener('pointerup', onPointerUp)
  document.removeEventListener('pointercancel', onPointerUp)
  isDragging.value = false
  lastInteractTime.value = Date.now()
  const pressDuration = Date.now() - mouseDownTime.value
  // 只有快速点击（按下后300ms内松开且未拖动）才打开聊天
  if (!hasMoved.value && pressDuration < 300) {
    openWidget()
  }
}

/**
 * 跳转到独立的完整对话页面。
 * 完整版页面位于 /ai-chat，带历史会话侧边栏。
 */
function openFullChat() {
  router.push('/ai-chat')
}
const inputText = ref('')
const aiResponding = ref(false)
const messages = ref([])
const sessionId = ref(null)
const messagesContainer = ref(null)

const loggedIn = computed(() => !!userStore.token)

function openWidget() {
  if (!loggedIn.value) {
    ElMessage.warning('请先登录后再使用 AI 客服')
    return
  }
  // 确保聊天面板(360x480)在视口内
  // 面板高 480 + 间距 12 = 492，按钮 56
  if (posY.value < 492 && posY.value + 56 + 492 > window.innerHeight) {
    // 上下都不够，往上移让面板在下方显示
    posY.value = Math.max(0, window.innerHeight - 56 - 492)
  }
  open.value = true
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || aiResponding.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  aiResponding.value = true
  let assistantIndex = -1

  await handleSseChat({
    sessionId: sessionId.value,
    message: text,
    onSessionId: (id) => {
      sessionId.value = id
    },
    onContent: (chunk) => {
      if (assistantIndex < 0) {
        messages.value.push({ role: 'assistant', content: '' })
        assistantIndex = messages.value.length - 1
      }
      messages.value[assistantIndex].content += chunk
      nextTick(scrollToBottom)
    },
    onDone: () => {
      aiResponding.value = false
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
</script>

<style scoped>
.ai-chat-widget {
  position: fixed;
  z-index: 1000;
}

.widget-trigger {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: 2px solid #ffd700;
  background: radial-gradient(circle at 35% 35%, #1a1b3a 0%, #0f1444 100%);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 0 20px rgba(255, 215, 0, 0.5),
    0 4px 12px rgba(26, 27, 58, 0.6);
  transition: all 0.3s;
  animation: moon-glow 3s ease-in-out infinite alternate;
  user-select: none;
  /* 触摸设备上按住兔子拖动时，不要把手指移动当作页面滚动手势 */
  touch-action: none;
}

.widget-trigger:hover {
  transform: scale(1.08);
  box-shadow:
    0 0 30px rgba(255, 215, 0, 0.7),
    0 6px 16px rgba(26, 27, 58, 0.8);
}

/* 拖拽状态：变成兔子，去掉圆形背景 */
.widget-trigger.dragging {
  width: 96px;
  height: 110px;
  background: transparent;
  border-color: transparent;
  box-shadow: none;
  animation: none;
  cursor: grabbing;
  transform: none;
}

.widget-trigger.dragging:hover {
  transform: none;
}

/* 闲置状态：和大兔子一样大，但保持 pointer 光标 */
.widget-trigger.idle {
  width: 96px;
  height: 110px;
  background: transparent;
  border-color: transparent;
  box-shadow: none;
  animation: none;
  cursor: pointer;
  transform: none;
}

.widget-trigger.idle:hover {
  transform: none;
}

/* 闲置聊天气泡 */
.idle-bubble {
  position: absolute;
  right: 0;
  bottom: calc(100% + 16px);
  max-width: 220px;
  min-width: 120px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 2px solid #c8901b;
  border-radius: 16px;
  color: #5d4e37;
  font-size: 17px;
  line-height: 1.6;
  font-family: 'STKaiti', 'KaiTi', serif;
  box-shadow: 0 4px 20px rgba(200, 144, 27, 0.25);
  text-align: center;
  pointer-events: none;
}

/* 气泡尖角（朝下） */
.idle-bubble::after {
  content: '';
  position: absolute;
  bottom: -12px;
  right: 28px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 12px solid #c8901b;
}

.idle-bubble::before {
  content: '';
  position: absolute;
  bottom: -8px;
  right: 30px;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 8px solid #fffdf5;
  z-index: 1;
}

/* 按钮在屏幕上半部分时，气泡显示在下方 */
.idle-bubble.below {
  bottom: auto;
  top: calc(100% + 16px);
}

.idle-bubble.below::after {
  bottom: auto;
  top: -12px;
  border-top: 0;
  border-bottom: 12px solid #c8901b;
}

.idle-bubble.below::before {
  bottom: auto;
  top: -8px;
  border-top: 0;
  border-bottom: 8px solid #fffdf5;
}

/* 按钮靠近左边缘时，气泡左对齐 */
.idle-bubble.left {
  right: auto;
  left: 0;
}

.idle-bubble.left::after {
  right: auto;
  left: 28px;
}

.idle-bubble.left::before {
  right: auto;
  left: 30px;
}

/* 气泡进出动画 */
.bubble-enter-active {
  animation: bubble-pop 0.3s ease-out;
}
.bubble-leave-active {
  animation: bubble-pop 0.3s ease-out reverse;
}
@keyframes bubble-pop {
  from { opacity: 0; transform: scale(0.8) translateY(8px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.drag-rabbit {
  animation: rabbit-float 0.4s ease-in-out infinite alternate;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

@keyframes rabbit-float {
  from { transform: translateY(0); }
  to { transform: translateY(-5px); }
}

@keyframes moon-glow {
  from { box-shadow: 0 0 15px rgba(255, 215, 0, 0.4), 0 4px 12px rgba(26, 27, 58, 0.6); }
  to { box-shadow: 0 0 25px rgba(255, 215, 0, 0.6), 0 4px 12px rgba(26, 27, 58, 0.6); }
}

.widget-panel {
  position: absolute;
  right: 0;
  bottom: calc(100% + 12px);
  width: 360px;
  height: 480px;
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
  border-radius: 12px;
  border: 1.5px solid #d4b996;
  box-shadow:
    0 0 24px rgba(255, 215, 0, 0.2),
    0 8px 32px rgba(26, 27, 58, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 按钮在屏幕上半部分时，面板显示在下方 */
.widget-panel.below {
  bottom: auto;
  top: calc(100% + 12px);
}

/* 按钮在左半边时，面板左对齐朝右展开（朝中间） */
.widget-panel.left {
  right: auto;
  left: 0;
}

.widget-header {
  padding: 12px 16px;
  background: linear-gradient(135deg, #1a1b3a 0%, #2c3e50 100%);
  color: #ffd700;
  border-bottom: 1.5px solid #c8901b;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-family: 'STKaiti', 'KaiTi', serif;
  letter-spacing: 1px;
}
.widget-header :deep(.el-button) {
  color: #fff;
  font-size: 20px;
}
.widget-header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.widget-messages {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  background: linear-gradient(180deg, rgba(253, 246, 227, 0.5) 0%, rgba(255, 253, 245, 0.8) 100%);
}
.widget-empty {
  text-align: center;
  color: #8b6f47;
  font-size: 13px;
  padding: 30px 10px;
  line-height: 1.6;
  font-family: 'STKaiti', 'KaiTi', serif;
}
.widget-message {
  margin-bottom: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  max-width: 80%;
  word-break: break-word;
}
.widget-message.user {
  margin-left: auto;
  background: linear-gradient(135deg, #c8901b 0%, #ffd700 100%);
  color: #1a1b3a;
  border: 1px solid #c8901b;
  font-weight: 500;
}
.widget-message.assistant {
  background: #fffdf5;
  border: 1px solid #d4b996;
  color: #3e2723;
}
.widget-input {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1.5px solid #d4b996;
  background: linear-gradient(180deg, #fffdf5 0%, #fdf6e3 100%);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

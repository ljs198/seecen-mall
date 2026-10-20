import { chatWithAI } from '../api/ai'

/**
 * 处理 AI 聊天 SSE 流（composable 风格函数）。
 *
 * 使用示例：
 *   await handleSseChat({
 *     sessionId: state.sessionId,
 *     message: text,
 *     onSessionId: (id) => { state.sessionId = id },
 *     onContent: (chunk) => { state.reply += chunk },
 *     onDone: () => { loading.value = false }
 *   })
 *
 * @param {Object} options 配置项
 * @param {string} [options.sessionId] 会话 ID
 * @param {string} options.message 用户消息
 * @param {Function} [options.onSessionId] 拿到 sessionId 时触发
 * @param {Function} [options.onContent] 收到一段 AI 内容时触发
 * @param {Function} [options.onDone] 流结束时触发
 * @param {Function} [options.onError] 出错时触发
 * @param {AbortSignal} [options.signal] 用于中断请求
 */
export async function handleSseChat({
  sessionId,
  message,
  onSessionId,
  onContent,
  onDone,
  onError,
  signal
}) {
  try {
    const response = await chatWithAI({
      sessionId: sessionId || undefined,
      message,
      signal
    })
    const reader = response.body?.getReader()
    if (!reader) throw new Error('No response stream')

    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    let streamDone = false

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      // SSE 消息以 \n\n 分隔，但后端用 \n 单换行发送 data: 行，这里按 \n 切分即可
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed) continue
        // 结束标记
        if (trimmed === 'data:[DONE]' || trimmed === 'data: [DONE]') {
          streamDone = true
          break
        }
        if (trimmed.startsWith('data:')) {
          const jsonStr = trimmed.slice(5).trim()
          try {
            const parsed = JSON.parse(jsonStr)
            if (parsed.sessionId) onSessionId?.(parsed.sessionId)
            if (parsed.content) onContent?.(parsed.content)
          } catch (e) {
            // 非 JSON 的 SSE 数据忽略
          }
        }
      }
      if (streamDone) break
    }

    onDone?.()
  } catch (err) {
    if (err.name !== 'AbortError') {
      onError?.(err)
    }
  }
}

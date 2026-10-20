import request from '../utils/request'

/**
 * 用户端 AI 客服 API
 *
 * 注意：SSE 流式聊天不通过 request.js 发送，因为需要读取 response.body 流。
 * 其它会话接口可以用常规 request 走 Result 拦截器。
 */

export const getSessions = () => request.get('/portal/ai/sessions')

export const getSessionMessages = (sessionId) =>
  request.get(`/portal/ai/sessions/${sessionId}/messages`)

export const deleteSession = (sessionId) =>
  request.delete(`/portal/ai/sessions/${sessionId}`)

/**
 * 发送 AI 聊天请求（用 fetch 读取 SSE 流）。
 *
 * @param {Object} params
 * @param {string} [params.sessionId] 会话 ID，留空表示新建会话
 * @param {string} params.message 用户消息
 * @param {AbortSignal} [params.signal] 用于中断请求
 */
export async function chatWithAI({ sessionId, message, signal }) {
  const token = localStorage.getItem('token') || ''
  const baseUrl = import.meta.env.VITE_API_BASE_URL
    ? `${import.meta.env.VITE_API_BASE_URL}/api`
    : '/api'
  const response = await fetch(`${baseUrl}/portal/ai/chat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({ sessionId, message }),
    signal
  })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`)
  }
  return response
}

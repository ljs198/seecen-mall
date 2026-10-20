import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getUserInfo, logout as logoutApi } from '../api/auth'

/**
 * 用户状态 Store（用户商城）
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),
  getters: {
    isLogin: (state) => !!state.token
  },
  actions: {
    async login(form) {
      const res = await loginApi(form)
      this.token = res.data.token
      this.userInfo = {
        userId: res.data.userId,
        username: res.data.username,
        nickname: res.data.nickname,
        role: res.data.role
      }
      localStorage.setItem('token', this.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return res
    },
    async register(form) {
      return registerApi(form)
    },
    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = {
        userId: res.data.userId,
        username: res.data.username,
        nickname: res.data.nickname,
        role: res.data.role
      }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return res
    },
    async logout() {
      try { await logoutApi() } catch (e) {}
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
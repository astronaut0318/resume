import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { viteMockServe } from 'vite-plugin-mock'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 禁用mock服务，使用真实后端
    viteMockServe({
      mockPath: 'src/mock',
      enable: false, // 设置为false，禁用mock数据
      watchFiles: false
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: true,
    hmr: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://localhost:9000', // 确保指向正确的网关服务
        changeOrigin: true,
        rewrite: (path) => path,
        configure: (proxy, options) => {
          proxy.timeout = 60000 // 增加超时时间到60秒
          proxy.agent = null
          proxy.maxSockets = 100
          proxy.keepAlive = true
        }
      }
    }
  }
})

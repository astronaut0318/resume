import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    viteMockServe({
      mockPath: 'src/mock',
      localEnabled: true,
      prodEnabled: false,
      supportTs: false,
      logger: true,
      watchFiles: true,
      injectCode: `
        import { setupProdMockServer } from './mock';
        setupProdMockServer();
      `,
      requestHeaders: {
        'Content-Type': 'application/json'
      },
      responseHeaders: {
        'Content-Type': 'application/json'
      }
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
        target: 'http://127.0.0.1:5173',
        changeOrigin: true,
        rewrite: (path) => path,
        configure: (proxy, options) => {
          proxy.timeout = 30000
          proxy.agent = null
          proxy.maxSockets = 100
          proxy.keepAlive = true
        }
      }
    }
  }
})

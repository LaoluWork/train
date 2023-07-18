const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 关闭浏览器的Uncaught error全屏提示
  devServer: {
    client: {
      overlay: false,
    },
  }
})

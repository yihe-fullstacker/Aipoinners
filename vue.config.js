const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack: (config) => {
    config.module
      .rule('glb')
      .test(/\.(glb|gltf)$/i)
      .type('asset/resource')
      .set('generator', {
        filename: 'assets/models/[name].[contenthash:8][ext]'
      })
  },
  devServer: {
    port: 7070,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})

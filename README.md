# aipionners-porject2

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Deploy to Cloudflare Pages

For a Pages project connected to this GitHub repository, use these build settings:

- Root directory: leave empty (the repository root)
- Build command: `npm run build`
- Build output directory: `dist`
- Node.js version: `20` (set `NODE_VERSION=20` in the Pages environment variables if needed)

`npm run build` regenerates the optimized heart model, builds the Vue app, and rejects
any output file that reaches Cloudflare's 25 MiB per-file limit. The checked output is
`dist`, not the repository root.

### Lints and fixes files
```
npm run lint
```

## 模型配置与切换指南
项目中涉及到大模型 API 的调用，主要配置在后端（AIPioneers）的 `application.properties` 中。

支持的模型清单（以阿里云 DashScope/通义千问 为例）：
- 文本对话模型：`qwen-turbo`, `qwen-plus`, `qwen-max`
- 视觉/多模态模型：`qwen-vl-plus`, `qwen-vl-max`

修改或切换模型请更新如下配置：
```properties
# 文本对话默认使用的模型
openai.chat.options.model-name=qwen-plus
# 如果默认模型不可用（例如提示 Model not exist），系统将自动降级并重试此模型
openai.chat.fallback.model-name=qwen-turbo
# 视觉相关任务默认使用的模型
openai.vision.model-name=qwen-vl-plus
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

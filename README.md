# 医学影像AI分析服务

基于Flask的医学影像分析服务，提供RESTful API接口。

## 安装依赖

```bash
pip install -r requirements.txt
```

## 运行服务

```bash
python app.py
```

服务将在 `http://localhost:5000` 启动。

## API接口

### 1. 健康检查
- **URL**: `/health`
- **方法**: `GET`
- **响应**: 
```json
{
  "status": "ok",
  "message": "AI服务运行正常"
}
```

### 2. 分析医学影像（文件上传）
- **URL**: `/analyze`
- **方法**: `POST`
- **Content-Type**: `multipart/form-data`
- **参数**: `image` (文件)
- **响应**:
```json
{
  "confidence": 0.87,
  "findings": [
    {
      "type": "肺结节",
      "location": "右上肺叶",
      "confidence": 0.85,
      "size": "1.2cm",
      "characteristics": ["边界清晰", "密度均匀"]
    }
  ],
  "recommendations": [
    "建议3-6个月后复查CT",
    "可考虑PET-CT进一步评估"
  ],
  "analysisTime": 2.3,
  "modelUsed": "ResNet50_Medical_v2"
}
```

### 3. 分析医学影像（Base64）
- **URL**: `/analyze_base64`
- **方法**: `POST`
- **Content-Type**: `application/json`
- **请求体**:
```json
{
  "image": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```

## 注意事项

1. 当前实现使用的是预训练的ResNet50模型作为示例
2. 实际项目中应该使用专门训练的医学影像模型
3. 建议使用GPU加速推理（如果可用）
4. 生产环境应该添加认证和限流机制


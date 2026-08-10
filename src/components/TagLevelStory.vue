<template>
  <div class="storybook-demo">
    <h1 class="demo-title">TagLevel 组件交互示例</h1>
    <p class="demo-subtitle">医疗课程等级标签组件库 - CSD (Component Storybook Documentation)</p>
    
    <!-- 基础展示 -->
    <section class="demo-section">
      <h2 class="section-title">基础展示</h2>
      <div class="demo-row">
        <div class="demo-item">
          <span class="demo-label">入门级</span>
          <TagLevel level="beginner" />
        </div>
        <div class="demo-item">
          <span class="demo-label">进阶级</span>
          <TagLevel level="intermediate" />
        </div>
        <div class="demo-item">
          <span class="demo-label">高级</span>
          <TagLevel level="advanced" />
        </div>
      </div>
    </section>

    <!-- 尺寸变体 -->
    <section class="demo-section">
      <h2 class="section-title">尺寸变体 (Size Variants)</h2>
      <div class="demo-row">
        <div class="demo-item">
          <span class="demo-label">Small</span>
          <TagLevel level="beginner" size="small" />
        </div>
        <div class="demo-item">
          <span class="demo-label">Medium</span>
          <TagLevel level="beginner" size="medium" />
        </div>
        <div class="demo-item">
          <span class="demo-label">Large</span>
          <TagLevel level="beginner" size="large" />
        </div>
      </div>
      <div class="demo-row" style="margin-top: 16px;">
        <div class="demo-item">
          <TagLevel level="intermediate" size="small" />
          <TagLevel level="intermediate" size="medium" />
          <TagLevel level="intermediate" size="large" />
        </div>
      </div>
    </section>

    <!-- 带图标 -->
    <section class="demo-section">
      <h2 class="section-title">带图标 (With Icons)</h2>
      <div class="demo-row">
        <div class="demo-item">
          <span class="demo-label">入门级</span>
          <TagLevel level="beginner" :show-icon="true" />
        </div>
        <div class="demo-item">
          <span class="demo-label">进阶级</span>
          <TagLevel level="intermediate" :show-icon="true" />
        </div>
        <div class="demo-item">
          <span class="demo-label">高级</span>
          <TagLevel level="advanced" :show-icon="true" />
        </div>
      </div>
    </section>

    <!-- 自定义文本 -->
    <section class="demo-section">
      <h2 class="section-title">自定义文本 (Custom Text)</h2>
      <div class="demo-row">
        <div class="demo-item">
          <span class="demo-label">基础课程</span>
          <TagLevel level="beginner" text="基础课程" />
        </div>
        <div class="demo-item">
          <span class="demo-label">专业课程</span>
          <TagLevel level="intermediate" text="专业课程" />
        </div>
        <div class="demo-item">
          <span class="demo-label">专家课程</span>
          <TagLevel level="advanced" text="专家课程" />
        </div>
      </div>
    </section>

    <!-- 交互状态 -->
    <section class="demo-section">
      <h2 class="section-title">交互状态 (Interactive States)</h2>
      <p class="section-desc">悬停标签查看效果 (Hover over tags to see effects)</p>
      <div class="demo-row">
        <div class="demo-item">
          <TagLevel level="beginner" />
          <TagLevel level="intermediate" />
          <TagLevel level="advanced" />
        </div>
      </div>
    </section>

    <!-- 灰度模式测试 -->
    <section class="demo-section grayscale-section">
      <h2 class="section-title">灰度模式区分度测试 (Grayscale Mode Test)</h2>
      <p class="section-desc">验证在无颜色情况下的标签区分度 (Verify tag distinction without color)</p>
      <div class="demo-row">
        <div class="demo-item">
          <span class="demo-label">入门级</span>
          <TagLevel level="beginner" />
        </div>
        <div class="demo-item">
          <span class="demo-label">进阶级</span>
          <TagLevel level="intermediate" />
        </div>
        <div class="demo-item">
          <span class="demo-label">高级</span>
          <TagLevel level="advanced" />
        </div>
      </div>
    </section>

    <!-- 对齐测试 -->
    <section class="demo-section">
      <h2 class="section-title">同一行对齐测试 (Alignment Test)</h2>
      <p class="section-desc">验证三种标签在同一行时视觉高度一致 (Verify visual alignment consistency)</p>
      <div class="demo-alignment-test">
        <TagLevel level="beginner" size="medium" />
        <TagLevel level="intermediate" size="medium" />
        <TagLevel level="advanced" size="medium" />
      </div>
      <div class="demo-alignment-test" style="margin-top: 16px;">
        <TagLevel level="beginner" size="small" />
        <TagLevel level="intermediate" size="small" />
        <TagLevel level="advanced" size="small" />
      </div>
    </section>

    <!-- 实际应用场景 -->
    <section class="demo-section">
      <h2 class="section-title">实际应用场景 (Real-World Usage)</h2>
      <div class="demo-card">
        <div class="demo-course-header">
          <span class="demo-course-icon">🧠</span>
          <TagLevel level="beginner" />
        </div>
        <h3 class="demo-course-title">AI医疗基础入门</h3>
        <p class="demo-course-desc">了解人工智能在医疗领域的应用基础，学习AI辅助诊断的基本原理。</p>
        <div class="demo-course-meta">
          <span>⏱️ 2小时</span>
          <span class="highlight-number">12,580</span>人学习
        </div>
      </div>
    </section>

    <!-- API文档 -->
    <section class="demo-section api-docs">
      <h2 class="section-title">API 文档</h2>
      <table class="api-table">
        <thead>
          <tr>
            <th>属性 (Prop)</th>
            <th>类型 (Type)</th>
            <th>默认值 (Default)</th>
            <th>说明 (Description)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><code>level</code></td>
            <td><code>'beginner' | 'intermediate' | 'advanced'</code></td>
            <td><em>required</em></td>
            <td>等级类型</td>
          </tr>
          <tr>
            <td><code>size</code></td>
            <td><code>'small' | 'medium' | 'large'</code></td>
            <td><code>'medium'</code></td>
            <td>标签尺寸</td>
          </tr>
          <tr>
            <td><code>text</code></td>
            <td><code>string</code></td>
            <td><code>''</code></td>
            <td>自定义显示文本</td>
          </tr>
          <tr>
            <td><code>showIcon</code></td>
            <td><code>boolean</code></td>
            <td><code>false</code></td>
            <td>是否显示图标</td>
          </tr>
          <tr>
            <td><code>ariaLabel</code></td>
            <td><code>string</code></td>
            <td><code>''</code></td>
            <td>可访问性标签</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 代码示例 -->
    <section class="demo-section">
      <h2 class="section-title">代码示例 (Code Examples)</h2>
      <div class="code-block">
        <pre><code>&lt;!-- 基础用法 --&gt;
&lt;TagLevel level="beginner" /&gt;

&lt;!-- 带尺寸 --&gt;
&lt;TagLevel level="intermediate" size="large" /&gt;

&lt;!-- 带图标 --&gt;
&lt;TagLevel level="advanced" :show-icon="true" /&gt;

&lt;!-- 自定义文本 --&gt;
&lt;TagLevel level="beginner" text="基础课程" /&gt;</code></pre>
      </div>
    </section>

  </div>
</template>

<script>
import TagLevel from './TagLevel.vue'

export default {
  name: 'TagLevelStory',
  components: { TagLevel }
}
</script>

<style scoped>
@import '../assets/variables.css';

.storybook-demo {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
  background-color: var(--color-background);
  min-height: 100vh;
}

.demo-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.demo-subtitle {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin-bottom: 40px;
}

.demo-section {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-card);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 16px;
}

.section-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 16px;
}

.demo-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: flex-end;
}

.demo-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.demo-label {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.grayscale-section {
  filter: grayscale(100%);
}

.demo-alignment-test {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 16px;
  background: var(--color-background);
  border-radius: var(--radius-button);
}

.demo-card {
  background: var(--color-background);
  border-radius: var(--radius-card);
  padding: 20px;
  max-width: 400px;
}

.demo-course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.demo-course-icon {
  font-size: 36px;
}

.demo-course-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.demo-course-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 12px;
}

.demo-course-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.highlight-number {
  color: var(--color-success);
  font-weight: 700;
}

.api-docs {
  overflow-x: auto;
}

.api-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.api-table th,
.api-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--color-divider);
}

.api-table th {
  background: var(--color-background);
  font-weight: 600;
  color: var(--color-text-primary);
}

.api-table td {
  color: var(--color-text-secondary);
}

.api-table code {
  background: var(--color-background);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  color: var(--color-primary);
}

.code-block {
  background: #1E293B;
  border-radius: var(--radius-card);
  padding: 20px;
  overflow-x: auto;
}

.code-block pre {
  margin: 0;
}

.code-block code {
  color: #E2E8F0;
  font-family: 'Fira Code', monospace;
  font-size: 14px;
  line-height: 1.6;
}
</style>
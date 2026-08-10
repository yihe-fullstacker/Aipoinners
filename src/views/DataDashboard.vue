<template>
  <div class="data-dashboard">
    <!-- 背景装饰 -->
    <div class="bg-decor">
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-brand">
        <svg class="brand-icon" viewBox="0 0 32 32" fill="none">
          <rect x="2" y="2" width="28" height="28" rx="8" fill="url(#brandGrad)"/>
          <path d="M8 22L13 15L17 19L21 12L24 16" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="8" cy="22" r="1.5" fill="white"/>
          <circle cx="13" cy="15" r="1.5" fill="white"/>
          <circle cx="17" cy="19" r="1.5" fill="white"/>
          <circle cx="21" cy="12" r="1.5" fill="white"/>
          <circle cx="24" cy="16" r="1.5" fill="white"/>
          <defs>
            <linearGradient id="brandGrad" x1="0" y1="0" x2="32" y2="32">
              <stop stop-color="#2563EB"/>
              <stop offset="1" stop-color="#7C3AED"/>
            </linearGradient>
          </defs>
        </svg>
        <span class="brand-name">MediAI</span>
        <span class="brand-tag">诊断分析系统</span>
      </div>
      <div class="nav-actions">
        <div class="nav-status">
          <span class="status-dot"></span>
          <span class="status-text">系统运行中</span>
        </div>
        <div class="nav-time">{{ currentTime }}</div>
        <button class="nav-btn" type="button" :aria-expanded="showNotifications" @click="toggleNotifications">
          <svg viewBox="0 0 20 20" fill="none">
            <path d="M10 2a6 6 0 016 6v2.5l1.5 2.5H2.5L4 10.5V8a6 6 0 016-6z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
            <path d="M8 16.5a2 2 0 004 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <span v-if="unreadNotifications > 0" class="nav-badge">{{ unreadNotifications }}</span>
        </button>
        <div v-if="showNotifications" class="popover-panel notification-panel">
          <div class="popover-title">系统通知</div>
          <button
            v-for="item in notifications"
            :key="item.id"
            type="button"
            class="notification-item"
            @click="openNotification(item)"
          >
            <span class="notification-dot" :class="item.level"></span>
            <span>{{ item.title }}</span>
          </button>
        </div>
        <div class="nav-avatar">王</div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="page-title-group">
          <div class="page-eyebrow">
            <svg viewBox="0 0 16 16" fill="none" class="eyebrow-icon">
              <circle cx="8" cy="8" r="3" fill="#22d3ee"/>
              <circle cx="8" cy="8" r="6" stroke="#22d3ee" stroke-width="1" stroke-dasharray="2 2"/>
            </svg>
            实时数据概览
          </div>
          <h1 class="page-title">AI 诊断数据分析中心</h1>
          <p class="page-desc">整合影像诊断、病例追踪与系统性能数据，为医疗决策提供实时支持</p>
        </div>
        <div class="page-actions">
          <button class="btn-secondary" type="button" @click="exportDashboardReport">
            <svg viewBox="0 0 20 20" fill="none" class="btn-icon">
              <path d="M3 10h14M10 3v14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            导出报告
          </button>
          <button class="btn-primary" type="button" :disabled="refreshing" @click="refreshDashboardData">
            <svg viewBox="0 0 20 20" fill="none" class="btn-icon">
              <path d="M4 4v5h5M16 16v-5h-5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4.5 9A6.5 6.5 0 0115.5 9M15.5 11A6.5 6.5 0 014.5 11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            刷新数据
          </button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <!-- 累计诊断病例 -->
        <div class="stat-card card-blue">
          <div class="stat-card-bg"></div>
          <div class="stat-header">
            <div class="stat-icon-wrap icon-blue">
              <svg viewBox="0 0 24 24" fill="none" class="stat-svg">
                <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                <rect x="9" y="3" width="6" height="4" rx="1" stroke="currentColor" stroke-width="1.8"/>
                <path d="M9 12h6M9 16h4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="stat-trend trend-up">
              <svg viewBox="0 0 12 12" fill="none">
                <path d="M2 9L6 4L10 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              +12.5%
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">2,847</div>
            <div class="stat-label">累计诊断病例</div>
          </div>
          <div class="stat-sparkline">
            <svg viewBox="0 0 120 40" preserveAspectRatio="none">
              <defs>
                <linearGradient id="spark1" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#3b82f6" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="#3b82f6" stop-opacity="0"/>
                </linearGradient>
              </defs>
              <path d="M0 32 L20 28 L40 22 L60 18 L80 20 L100 10 L120 6" fill="none" stroke="#3b82f6" stroke-width="2"/>
              <path d="M0 32 L20 28 L40 22 L60 18 L80 20 L100 10 L120 6 L120 40 L0 40Z" fill="url(#spark1)"/>
            </svg>
          </div>
        </div>

        <!-- 诊断准确率 -->
        <div class="stat-card card-green">
          <div class="stat-card-bg"></div>
          <div class="stat-header">
            <div class="stat-icon-wrap icon-green">
              <svg viewBox="0 0 24 24" fill="none" class="stat-svg">
                <path d="M12 2a10 10 0 110 20A10 10 0 0112 2z" stroke="currentColor" stroke-width="1.8"/>
                <path d="M8 12l3 3 5-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-trend trend-up">
              <svg viewBox="0 0 12 12" fill="none">
                <path d="M2 9L6 4L10 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              +2.3%
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">96.8<span class="stat-unit">%</span></div>
            <div class="stat-label">诊断准确率</div>
          </div>
          <div class="stat-sparkline">
            <svg viewBox="0 0 120 40" preserveAspectRatio="none">
              <defs>
                <linearGradient id="spark2" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#10b981" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="#10b981" stop-opacity="0"/>
                </linearGradient>
              </defs>
              <path d="M0 28 L20 24 L40 26 L60 16 L80 14 L100 10 L120 8" fill="none" stroke="#10b981" stroke-width="2"/>
              <path d="M0 28 L20 24 L40 26 L60 16 L80 14 L100 10 L120 8 L120 40 L0 40Z" fill="url(#spark2)"/>
            </svg>
          </div>
        </div>

        <!-- 平均响应时间 -->
        <div class="stat-card card-amber">
          <div class="stat-card-bg"></div>
          <div class="stat-header">
            <div class="stat-icon-wrap icon-amber">
              <svg viewBox="0 0 24 24" fill="none" class="stat-svg">
                <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
                <path d="M12 7v5l3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 3v1M12 20v1M3 12h1M20 12h1" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="stat-trend trend-down">
              <svg viewBox="0 0 12 12" fill="none">
                <path d="M2 4L6 9L10 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              -8.1%
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">2.3<span class="stat-unit">s</span></div>
            <div class="stat-label">平均响应时间</div>
          </div>
          <div class="stat-sparkline">
            <svg viewBox="0 0 120 40" preserveAspectRatio="none">
              <defs>
                <linearGradient id="spark3" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#f59e0b" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="#f59e0b" stop-opacity="0"/>
                </linearGradient>
              </defs>
              <path d="M0 12 L20 16 L40 14 L60 20 L80 18 L100 26 L120 30" fill="none" stroke="#f59e0b" stroke-width="2"/>
              <path d="M0 12 L20 16 L40 14 L60 20 L80 18 L100 26 L120 30 L120 40 L0 40Z" fill="url(#spark3)"/>
            </svg>
          </div>
        </div>

        <!-- 合作医生 -->
        <div class="stat-card card-purple">
          <div class="stat-card-bg"></div>
          <div class="stat-header">
            <div class="stat-icon-wrap icon-purple">
              <svg viewBox="0 0 24 24" fill="none" class="stat-svg">
                <circle cx="9" cy="7" r="3" stroke="currentColor" stroke-width="1.8"/>
                <path d="M3 20c0-3.314 2.686-6 6-6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                <circle cx="17" cy="9" r="2.5" stroke="currentColor" stroke-width="1.8"/>
                <path d="M13 20c0-2.761 1.79-5 4-5s4 2.239 4 5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                <path d="M9 20h8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="stat-trend trend-up">
              <svg viewBox="0 0 12 12" fill="none">
                <path d="M2 9L6 4L10 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              +18.2%
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">156</div>
            <div class="stat-label">合作医生</div>
          </div>
          <div class="stat-sparkline">
            <svg viewBox="0 0 120 40" preserveAspectRatio="none">
              <defs>
                <linearGradient id="spark4" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#8b5cf6" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="#8b5cf6" stop-opacity="0"/>
                </linearGradient>
              </defs>
              <path d="M0 34 L20 30 L40 26 L60 22 L80 18 L100 12 L120 6" fill="none" stroke="#8b5cf6" stroke-width="2"/>
              <path d="M0 34 L20 30 L40 26 L60 22 L80 18 L100 12 L120 6 L120 40 L0 40Z" fill="url(#spark4)"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 图表区 -->
      <div class="charts-section">
        <!-- 折线图 -->
        <div class="chart-card chart-main">
          <div class="chart-header">
            <div class="chart-title-group">
              <div class="chart-dot dot-blue"></div>
              <h3 class="chart-title">每日诊断量趋势</h3>
              <span class="chart-badge">实时</span>
            </div>
            <div class="chart-controls" @click="handleChartControlsClick">
              <button class="tab-btn active">7天</button>
              <button class="tab-btn">30天</button>
              <button class="tab-btn">90天</button>
              <button class="icon-btn" type="button" :aria-expanded="showChartMenu">
                <svg viewBox="0 0 16 16" fill="none">
                  <circle cx="8" cy="4" r="1" fill="currentColor"/>
                  <circle cx="8" cy="8" r="1" fill="currentColor"/>
                  <circle cx="8" cy="12" r="1" fill="currentColor"/>
                </svg>
              </button>
              <div v-if="showChartMenu" class="popover-panel chart-menu">
                <button type="button" @click.stop="exportDashboardReport">导出当前图表</button>
                <button type="button" @click.stop="refreshDashboardData">刷新趋势数据</button>
                <button type="button" @click.stop="viewAllReports">查看原始报告</button>
              </div>
            </div>
          </div>
          <div class="chart-area">
            <div class="chart-y-labels">
              <span>300</span>
              <span>250</span>
              <span>200</span>
              <span>150</span>
              <span>100</span>
              <span>50</span>
            </div>
            <div class="chart-plot">
              <svg viewBox="0 0 560 200" class="line-chart-svg" preserveAspectRatio="none">
                <defs>
                  <linearGradient id="lineGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#3b82f6" stop-opacity="0.25"/>
                    <stop offset="100%" stop-color="#3b82f6" stop-opacity="0"/>
                  </linearGradient>
                  <linearGradient id="lineGrad2" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#8b5cf6" stop-opacity="0.15"/>
                    <stop offset="100%" stop-color="#8b5cf6" stop-opacity="0"/>
                  </linearGradient>
                  <filter id="glow">
                    <feGaussianBlur stdDeviation="3" result="blur"/>
                    <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
                  </filter>
                </defs>
                <!-- 网格线 -->
                <line x1="0" y1="40" x2="560" y2="40" stroke="rgba(148,163,184,0.1)" stroke-width="1"/>
                <line x1="0" y1="80" x2="560" y2="80" stroke="rgba(148,163,184,0.1)" stroke-width="1"/>
                <line x1="0" y1="120" x2="560" y2="120" stroke="rgba(148,163,184,0.1)" stroke-width="1"/>
                <line x1="0" y1="160" x2="560" y2="160" stroke="rgba(148,163,184,0.1)" stroke-width="1"/>
                <!-- 面积 -->
                <path d="M40 148 C100 130, 140 100, 200 88 C260 76, 300 96, 360 68 C400 50, 440 36, 520 22 L520 200 L40 200Z" fill="url(#lineGrad)"/>
                <!-- 对比线 -->
                <path d="M40 168 C100 155, 140 140, 200 130 C260 120, 300 128, 360 112 C400 100, 440 90, 520 78" fill="none" stroke="#8b5cf6" stroke-width="2" stroke-dasharray="5 4" opacity="0.6"/>
                <!-- 主折线 -->
                <path d="M40 148 C100 130, 140 100, 200 88 C260 76, 300 96, 360 68 C400 50, 440 36, 520 22" fill="none" stroke="#3b82f6" stroke-width="2.5" stroke-linecap="round" filter="url(#glow)"/>
                <!-- 数据点 -->
                <circle cx="40" cy="148" r="4.5" fill="#1e293b" stroke="#3b82f6" stroke-width="2.5"/>
                <circle cx="200" cy="88" r="4.5" fill="#1e293b" stroke="#3b82f6" stroke-width="2.5"/>
                <circle cx="360" cy="68" r="4.5" fill="#1e293b" stroke="#3b82f6" stroke-width="2.5"/>
                <circle cx="520" cy="22" r="5.5" fill="#3b82f6" stroke="#93c5fd" stroke-width="2"/>
                <!-- 高亮点标注 -->
                <line x1="520" y1="22" x2="520" y2="200" stroke="rgba(59,130,246,0.2)" stroke-width="1" stroke-dasharray="4 3"/>
              </svg>
            </div>
          </div>
          <div class="chart-x-labels">
            <span>周一</span>
            <span>周二</span>
            <span>周三</span>
            <span>周四</span>
            <span>周五</span>
            <span>周六</span>
            <span>今天</span>
          </div>
          <div class="chart-legend">
            <div class="legend-item">
              <span class="legend-dot" style="background:#3b82f6"></span>
              本周诊断量
            </div>
            <div class="legend-item">
              <span class="legend-dot" style="background:#8b5cf6; opacity:0.6"></span>
              上周对比
            </div>
          </div>
        </div>

        <!-- 右侧面板 -->
        <div class="chart-side">
          <!-- 疾病分布 -->
          <div class="chart-card disease-card">
            <div class="chart-header">
              <div class="chart-title-group">
                <div class="chart-dot dot-purple"></div>
                <h3 class="chart-title">疾病分布</h3>
              </div>
              <button class="text-btn" type="button" @click="viewAllReports">查看全部</button>
            </div>
            <div class="disease-list">
              <div class="disease-item">
                <div class="disease-meta">
                  <div class="disease-dot" style="background:#3b82f6"></div>
                  <span class="disease-name">肺部结节</span>
                  <span class="disease-count">998例</span>
                  <span class="disease-pct">35%</span>
                </div>
                <div class="progress-track">
                  <div class="progress-bar" style="width:35%; background: linear-gradient(90deg,#2563eb,#60a5fa)"></div>
                </div>
              </div>
              <div class="disease-item">
                <div class="disease-meta">
                  <div class="disease-dot" style="background:#10b981"></div>
                  <span class="disease-name">心脏异常</span>
                  <span class="disease-count">797例</span>
                  <span class="disease-pct">28%</span>
                </div>
                <div class="progress-track">
                  <div class="progress-bar" style="width:28%; background: linear-gradient(90deg,#059669,#34d399)"></div>
                </div>
              </div>
              <div class="disease-item">
                <div class="disease-meta">
                  <div class="disease-dot" style="background:#f59e0b"></div>
                  <span class="disease-name">肝脏病变</span>
                  <span class="disease-count">626例</span>
                  <span class="disease-pct">22%</span>
                </div>
                <div class="progress-track">
                  <div class="progress-bar" style="width:22%; background: linear-gradient(90deg,#d97706,#fbbf24)"></div>
                </div>
              </div>
              <div class="disease-item">
                <div class="disease-meta">
                  <div class="disease-dot" style="background:#8b5cf6"></div>
                  <span class="disease-name">其他类型</span>
                  <span class="disease-count">427例</span>
                  <span class="disease-pct">15%</span>
                </div>
                <div class="progress-track">
                  <div class="progress-bar" style="width:15%; background: linear-gradient(90deg,#7c3aed,#a78bfa)"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 系统状态 -->
          <div class="chart-card status-card">
            <div class="chart-header">
              <div class="chart-title-group">
                <div class="chart-dot dot-green"></div>
                <h3 class="chart-title">系统状态</h3>
              </div>
              <span class="status-badge">全部正常</span>
            </div>
            <div class="status-list">
              <div class="status-row">
                <div class="status-info">
                  <svg viewBox="0 0 16 16" fill="none" class="status-icon">
                    <rect x="2" y="3" width="12" height="10" rx="2" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M5 13v2M11 13v2M3 15h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                  推理服务器
                </div>
                <div class="status-bar-wrap">
                  <div class="status-mini-bar"><div style="width:72%; background:#3b82f6"></div></div>
                  <span class="status-val">72%</span>
                </div>
                <span class="status-pill pill-ok">正常</span>
              </div>
              <div class="status-row">
                <div class="status-info">
                  <svg viewBox="0 0 16 16" fill="none" class="status-icon">
                    <ellipse cx="8" cy="5" rx="5" ry="2.5" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M3 5v4c0 1.38 2.24 2.5 5 2.5S13 10.38 13 9V5" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M3 9v2c0 1.38 2.24 2.5 5 2.5S13 11.38 13 11V9" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  医学数据库
                </div>
                <div class="status-bar-wrap">
                  <div class="status-mini-bar"><div style="width:89%; background:#10b981"></div></div>
                  <span class="status-val">89%</span>
                </div>
                <span class="status-pill pill-ok">正常</span>
              </div>
              <div class="status-row">
                <div class="status-info">
                  <svg viewBox="0 0 16 16" fill="none" class="status-icon">
                    <path d="M8 2L10.5 6H13L11 9H13L8 14L3 9H5L3 6H5.5L8 2Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
                  </svg>
                  AI 模型服务
                </div>
                <div class="status-bar-wrap">
                  <div class="status-mini-bar"><div style="width:45%; background:#f59e0b"></div></div>
                  <span class="status-val">45%</span>
                </div>
                <span class="status-pill pill-warn">预警</span>
              </div>
              <div class="status-row">
                <div class="status-info">
                  <svg viewBox="0 0 16 16" fill="none" class="status-icon">
                    <path d="M2 12L6 5L10 8L14 2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="14" cy="2" r="2" fill="currentColor"/>
                  </svg>
                  数据同步
                </div>
                <div class="status-bar-wrap">
                  <div class="status-mini-bar"><div style="width:100%; background:#10b981"></div></div>
                  <span class="status-val">100%</span>
                </div>
                <span class="status-pill pill-ok">正常</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 核心技术亮点 -->
      <section class="features-section">
        <div class="section-header">
          <h2 class="section-title">核心技术亮点</h2>
          <p class="section-desc">基于前沿 AI 技术构建的四大核心能力模块</p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-top">
              <div class="feature-icon-wrap icon-blue-soft">
                <svg viewBox="0 0 32 32" fill="none" class="feature-svg">
                  <circle cx="16" cy="10" r="4" stroke="#3b82f6" stroke-width="2"/>
                  <circle cx="8" cy="22" r="3" stroke="#3b82f6" stroke-width="2"/>
                  <circle cx="24" cy="22" r="3" stroke="#3b82f6" stroke-width="2"/>
                  <path d="M16 14L8 19M16 14L24 19" stroke="#3b82f6" stroke-width="1.8" stroke-linecap="round"/>
                  <path d="M5 22q3 4 6 0M21 22q3 4 6 0" stroke="#60a5fa" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="feature-number">01</div>
            </div>
            <h3 class="feature-title">多模态大模型</h3>
            <p class="feature-desc">融合影像、文本、临床数据的端到端深度学习框架，实现跨模态知识迁移</p>
            <div class="feature-tags">
              <span class="tag">DICOM影像</span>
              <span class="tag">NLP病历</span>
              <span class="tag">结构化数据</span>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-top">
              <div class="feature-icon-wrap icon-cyan-soft">
                <svg viewBox="0 0 32 32" fill="none" class="feature-svg">
                  <circle cx="16" cy="16" r="8" stroke="#06b6d4" stroke-width="2"/>
                  <path d="M16 8v4M16 20v4M8 16h4M20 16h4" stroke="#06b6d4" stroke-width="2" stroke-linecap="round"/>
                  <circle cx="16" cy="16" r="3" fill="#06b6d4" fill-opacity="0.3" stroke="#06b6d4" stroke-width="1.5"/>
                </svg>
              </div>
              <div class="feature-number">02</div>
            </div>
            <h3 class="feature-title">可解释性 AI</h3>
            <p class="feature-desc">Grad-CAM 热图 + SHAP 值分析，让每一步诊断过程透明可追溯、可审计</p>
            <div class="feature-tags">
              <span class="tag">Grad-CAM</span>
              <span class="tag">SHAP分析</span>
              <span class="tag">可视化</span>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-top">
              <div class="feature-icon-wrap icon-amber-soft">
                <svg viewBox="0 0 32 32" fill="none" class="feature-svg">
                  <rect x="6" y="4" width="10" height="14" rx="2" stroke="#f59e0b" stroke-width="2"/>
                  <rect x="16" y="12" width="10" height="14" rx="2" stroke="#f59e0b" stroke-width="2"/>
                  <path d="M11 4V2M21 12V10" stroke="#f59e0b" stroke-width="2" stroke-linecap="round"/>
                  <path d="M11 14l5 2" stroke="#fbbf24" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="feature-number">03</div>
            </div>
            <h3 class="feature-title">医学知识图谱</h3>
            <p class="feature-desc">链接权威医学知识库，整合千万级医学实体关系，提供循证医学证据支持</p>
            <div class="feature-tags">
              <span class="tag">ICD-11编码</span>
              <span class="tag">知识推理</span>
              <span class="tag">循证支持</span>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-top">
              <div class="feature-icon-wrap icon-purple-soft">
                <svg viewBox="0 0 32 32" fill="none" class="feature-svg">
                  <path d="M16 4C9.37 4 4 9.37 4 16s5.37 12 12 12 12-5.37 12-12S22.63 4 16 4z" stroke="#8b5cf6" stroke-width="2"/>
                  <path d="M16 10v6l4 2" stroke="#8b5cf6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M6 16l-4 0M30 16l-4 0" stroke="#a78bfa" stroke-width="1.5" stroke-linecap="round"/>
                  <path d="M9.5 22.8L7 25M22.5 9.2L25 7" stroke="#a78bfa" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="feature-number">04</div>
            </div>
            <h3 class="feature-title">主动学习机制</h3>
            <p class="feature-desc">医生反馈闭环持续优化模型参数，实现诊断能力迭代提升与自适应演进</p>
            <div class="feature-tags">
              <span class="tag">在线学习</span>
              <span class="tag">反馈闭环</span>
              <span class="tag">持续优化</span>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 底部 -->
    <footer class="dashboard-footer">
      <span>© 2025 MediAI 诊断分析系统</span>
      <span class="footer-sep">·</span>
      <span>数据更新于 {{ currentTime }}</span>
      <span class="footer-sep">·</span>
      <span>版本 v3.2.1</span>
    </footer>
    <div v-if="toastMessage" class="dashboard-toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
export default {
  name: 'DataDashboard',
  data() {
    return {
      currentTime: '',
      timer: null,
      refreshing: false,
      activePeriod: 7,
      showNotifications: false,
      showChartMenu: false,
      toastMessage: '',
      toastTimer: null,
      unreadNotifications: 3,
      notifications: [
        { id: 1, title: 'AI模型服务负载较高，请关注响应时延', level: 'warn' },
        { id: 2, title: '今日诊断数据已完成同步', level: 'success' },
        { id: 3, title: '有新的医生反馈待纳入主动学习队列', level: 'info' }
      ]
    }
  },
  mounted() {
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    if (this.toastTimer) {
      clearTimeout(this.toastTimer)
    }
  },
  methods: {
    updateTime() {
      const now = new Date()
      const pad = n => String(n).padStart(2, '0')
      this.currentTime = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
    },
    refreshDashboardData() {
      if (this.refreshing) return
      this.refreshing = true
      this.showChartMenu = false
      setTimeout(() => {
        this.updateTime()
        this.refreshing = false
        this.showToast('数据已刷新')
      }, 650)
    },
    exportDashboardReport() {
      const payload = {
        exportedAt: this.currentTime,
        activePeriod: `${this.activePeriod}天`,
        summary: {
          totalDiagnoses: 2847,
          accuracy: '96.8%',
          averageResponseTime: '2.3s',
          doctors: 156
        },
        diseaseDistribution: [
          { name: '肺部结节', count: 998, percent: '35%' },
          { name: '心脏异常', count: 797, percent: '28%' },
          { name: '肝脏病变', count: 626, percent: '22%' },
          { name: '其他类型', count: 427, percent: '15%' }
        ],
        systemStatus: [
          { name: '推理服务器', usage: '72%', status: '正常' },
          { name: '医学数据库', usage: '89%', status: '正常' },
          { name: 'AI模型服务', usage: '45%', status: '预警' },
          { name: '数据同步', usage: '100%', status: '正常' }
        ]
      }
      const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `medical-dashboard-${Date.now()}.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      this.showChartMenu = false
      this.showToast('报告已导出')
    },
    handleChartControlsClick(event) {
      const button = event.target.closest('button')
      if (!button) return

      if (button.classList.contains('icon-btn')) {
        this.toggleChartMenu()
        return
      }

      if (!button.classList.contains('tab-btn')) return
      const controls = button.closest('.chart-controls')
      controls?.querySelectorAll('.tab-btn').forEach(item => item.classList.remove('active'))
      button.classList.add('active')
      const label = button.textContent || ''
      if (label.includes('90')) {
        this.changePeriod(90)
      } else if (label.includes('30')) {
        this.changePeriod(30)
      } else {
        this.changePeriod(7)
      }
    },
    changePeriod(days) {
      this.activePeriod = days
      this.showChartMenu = false
      this.showToast(`已切换到${days}天趋势`)
    },
    toggleChartMenu() {
      this.showChartMenu = !this.showChartMenu
      this.showNotifications = false
    },
    toggleNotifications() {
      this.showNotifications = !this.showNotifications
      this.showChartMenu = false
      if (this.showNotifications) {
        this.unreadNotifications = 0
      }
    },
    openNotification(item) {
      this.showNotifications = false
      this.showToast(item.title)
    },
    viewAllReports() {
      this.showChartMenu = false
      this.$router.push('/reports')
    },
    showToast(message) {
      this.toastMessage = message
      if (this.toastTimer) {
        clearTimeout(this.toastTimer)
      }
      this.toastTimer = setTimeout(() => {
        this.toastMessage = ''
      }, 2200)
    }
  }
}
</script>

<style scoped>
/* =====================
   CSS 变量 & 重置
===================== */
.data-dashboard {
  --c-bg: #060d1a;
  --c-surface: #0d1f35;
  --c-surface-2: #0f2744;
  --c-border: rgba(148, 163, 184, 0.1);
  --c-border-hover: rgba(148, 163, 184, 0.25);
  --c-text-primary: #e2e8f0;
  --c-text-secondary: #94a3b8;
  --c-text-muted: #475569;
  --c-blue: #3b82f6;
  --c-green: #10b981;
  --c-amber: #f59e0b;
  --c-purple: #8b5cf6;
  --c-cyan: #06b6d4;
  --radius-lg: 20px;
  --radius-md: 14px;
  --radius-sm: 8px;
  --shadow-card: 0 4px 24px rgba(0, 0, 0, 0.4);
  --font-mono: 'SF Mono', 'Fira Code', monospace;

  min-height: 100vh;
  background: var(--c-bg);
  color: var(--c-text-primary);
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Noto Sans SC', system-ui, sans-serif;
  position: relative;
  overflow-x: hidden;
}

/* =====================
   背景装饰
===================== */
.bg-decor {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
}
.orb-1 { width: 600px; height: 600px; background: #2563eb; top: -200px; left: -100px; }
.orb-2 { width: 500px; height: 500px; background: #7c3aed; bottom: -100px; right: -150px; }
.orb-3 { width: 400px; height: 400px; background: #0891b2; top: 40%; left: 40%; }

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.04) 1px, transparent 1px);
  background-size: 40px 40px;
}

/* =====================
   导航栏
===================== */
.top-nav {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  height: 64px;
  background: rgba(6, 13, 26, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--c-border);
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}
.brand-icon { width: 32px; height: 32px; flex-shrink: 0; }
.brand-name {
  font-size: 1.2rem;
  font-weight: 800;
  background: linear-gradient(135deg, #60a5fa, #a78bfa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}
.brand-tag {
  font-size: 0.7rem;
  color: var(--c-text-muted);
  border: 1px solid var(--c-border);
  padding: 2px 8px;
  border-radius: 99px;
}

.nav-actions { display: flex; align-items: center; gap: 16px; }

.nav-actions,
.chart-controls {
  position: relative;
}

.nav-status { display: flex; align-items: center; gap: 6px; font-size: 0.8rem; color: var(--c-text-secondary); }
.status-dot {
  width: 7px; height: 7px; border-radius: 50%;
  background: var(--c-green);
  box-shadow: 0 0 6px var(--c-green);
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.nav-time { font-size: 0.78rem; color: var(--c-text-muted); font-family: var(--font-mono); }

.nav-btn {
  position: relative;
  background: none;
  border: 1px solid var(--c-border);
  border-radius: var(--radius-sm);
  width: 34px; height: 34px;
  display: flex; align-items: center; justify-content: center;
  color: var(--c-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}
.nav-btn:hover { border-color: var(--c-blue); color: var(--c-blue); }
.nav-btn:disabled,
.btn-primary:disabled,
.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
.nav-btn svg { width: 16px; height: 16px; }
.nav-badge {
  position: absolute; top: -5px; right: -5px;
  background: #ef4444;
  color: white;
  font-size: 0.6rem;
  font-weight: 700;
  width: 16px; height: 16px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid var(--c-bg);
}

.nav-avatar {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  cursor: pointer;
}

.popover-panel {
  position: absolute;
  z-index: 30;
  background: rgba(13, 31, 53, 0.96);
  border: 1px solid var(--c-border-hover);
  border-radius: 14px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.38);
  backdrop-filter: blur(14px);
}

.notification-panel {
  top: 44px;
  right: 42px;
  width: 280px;
  padding: 10px;
}

.popover-title {
  padding: 6px 8px 10px;
  font-size: 0.82rem;
  font-weight: 700;
  color: var(--c-text-primary);
  border-bottom: 1px solid var(--c-border);
  margin-bottom: 6px;
}

.notification-item,
.chart-menu button {
  width: 100%;
  border: none;
  background: transparent;
  color: var(--c-text-secondary);
  text-align: left;
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.2s;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 8px;
  font-size: 0.78rem;
  line-height: 1.4;
}

.notification-item:hover,
.chart-menu button:hover {
  background: rgba(59, 130, 246, 0.12);
  color: var(--c-text-primary);
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.notification-dot.warn { background: var(--c-amber); }
.notification-dot.success { background: var(--c-green); }
.notification-dot.info { background: var(--c-blue); }

/* =====================
   主内容
===================== */
.main-content {
  position: relative;
  z-index: 1;
  max-width: 1440px;
  margin: 0 auto;
  padding: 2rem 2rem 1rem;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 2rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.page-eyebrow {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #22d3ee;
  margin-bottom: 0.6rem;
}
.eyebrow-icon { width: 14px; height: 14px; }

.page-title {
  font-size: 2rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  background: linear-gradient(135deg, #e2e8f0 30%, #94a3b8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.4rem;
  line-height: 1.2;
}
.page-desc { font-size: 0.875rem; color: var(--c-text-secondary); max-width: 480px; }

.page-actions { display: flex; gap: 10px; flex-shrink: 0; }

.btn-primary, .btn-secondary {
  display: flex; align-items: center; gap: 6px;
  padding: 0 16px;
  height: 38px;
  border-radius: var(--radius-sm);
  font-size: 0.825rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}
.btn-icon { width: 14px; height: 14px; }
.btn-primary {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: white;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.35);
}
.btn-primary:hover { opacity: 0.9; transform: translateY(-1px); }
.btn-secondary {
  background: rgba(255,255,255,0.05);
  color: var(--c-text-primary);
  border: 1px solid var(--c-border);
}
.btn-secondary:hover { background: rgba(255,255,255,0.08); border-color: var(--c-border-hover); }

/* =====================
   统计卡片
===================== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.25rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  position: relative;
  border-radius: var(--radius-lg);
  padding: 1.4rem 1.4rem 0;
  border: 1px solid var(--c-border);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
  background: var(--c-surface);
}
.stat-card:hover {
  transform: translateY(-4px);
  border-color: var(--c-border-hover);
  box-shadow: var(--shadow-card);
}

.stat-card-bg {
  position: absolute;
  inset: 0;
  opacity: 0.04;
  background: radial-gradient(ellipse at top right, currentColor, transparent 70%);
  pointer-events: none;
}
.card-blue .stat-card-bg { color: var(--c-blue); }
.card-green .stat-card-bg { color: var(--c-green); }
.card-amber .stat-card-bg { color: var(--c-amber); }
.card-purple .stat-card-bg { color: var(--c-purple); }

.stat-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; }

.stat-icon-wrap {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.stat-svg { width: 22px; height: 22px; }
.icon-blue { background: rgba(59,130,246,0.12); color: var(--c-blue); }
.icon-green { background: rgba(16,185,129,0.12); color: var(--c-green); }
.icon-amber { background: rgba(245,158,11,0.12); color: var(--c-amber); }
.icon-purple { background: rgba(139,92,246,0.12); color: var(--c-purple); }

.stat-trend {
  display: flex; align-items: center; gap: 3px;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 99px;
  font-family: var(--font-mono);
}
.stat-trend svg { width: 10px; height: 10px; }
.trend-up { background: rgba(16,185,129,0.12); color: #34d399; }
.trend-down { background: rgba(239,68,68,0.1); color: #f87171; }

.stat-value {
  font-size: 2.2rem;
  font-weight: 900;
  letter-spacing: -0.04em;
  color: var(--c-text-primary);
  line-height: 1;
  margin-bottom: 4px;
}
.stat-unit { font-size: 1.1rem; font-weight: 600; color: var(--c-text-secondary); margin-left: 2px; }
.stat-label { font-size: 0.8rem; color: var(--c-text-secondary); margin-bottom: 1rem; }

.stat-sparkline { height: 52px; margin: 0 -1.4rem; }
.stat-sparkline svg { width: 100%; height: 100%; display: block; }

/* =====================
   图表区域
===================== */
.charts-section {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 1.25rem;
  margin-bottom: 1.5rem;
  align-items: start;
}

.chart-card {
  background: var(--c-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--c-border);
  padding: 1.5rem;
  box-shadow: var(--shadow-card);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
}
.chart-title-group { display: flex; align-items: center; gap: 8px; }
.chart-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.dot-blue { background: var(--c-blue); box-shadow: 0 0 6px var(--c-blue); }
.dot-purple { background: var(--c-purple); box-shadow: 0 0 6px var(--c-purple); }
.dot-green { background: var(--c-green); box-shadow: 0 0 6px var(--c-green); }
.chart-title { font-size: 0.95rem; font-weight: 700; color: var(--c-text-primary); }
.chart-badge {
  font-size: 0.65rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  background: rgba(34, 211, 238, 0.1);
  color: #22d3ee;
  padding: 2px 8px;
  border-radius: 99px;
  border: 1px solid rgba(34, 211, 238, 0.2);
  animation: blink 2s ease-in-out infinite;
}
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }

.chart-controls { display: flex; align-items: center; gap: 4px; }
.chart-menu {
  top: 36px;
  right: 0;
  width: 150px;
  padding: 8px;
}

.chart-menu button {
  padding: 9px 10px;
  font-size: 0.75rem;
}

.tab-btn {
  padding: 4px 12px;
  font-size: 0.78rem;
  font-weight: 600;
  border-radius: 6px;
  border: 1px solid transparent;
  background: none;
  color: var(--c-text-muted);
  cursor: pointer;
  transition: all 0.2s;
}
.tab-btn:hover { color: var(--c-text-secondary); }
.tab-btn.active { background: rgba(59,130,246,0.12); color: var(--c-blue); border-color: rgba(59,130,246,0.25); }
.chart-controls:has(.tab-btn:nth-child(2):hover) .tab-btn.active,
.chart-controls:has(.tab-btn:nth-child(3):hover) .tab-btn.active {
  background: none;
  color: var(--c-text-muted);
  border-color: transparent;
}
.icon-btn {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  background: none; border: 1px solid var(--c-border);
  border-radius: 6px; cursor: pointer;
  color: var(--c-text-muted);
  transition: all 0.2s;
  margin-left: 4px;
}
.icon-btn:hover { border-color: var(--c-border-hover); color: var(--c-text-secondary); }
.icon-btn svg { width: 14px; height: 14px; }

.chart-area { display: flex; gap: 12px; align-items: stretch; margin-bottom: 8px; }
.chart-y-labels {
  display: flex; flex-direction: column; justify-content: space-between;
  font-size: 0.7rem;
  color: var(--c-text-muted);
  font-family: var(--font-mono);
  padding: 4px 0;
  flex-shrink: 0;
}
.chart-plot { flex: 1; height: 200px; }
.line-chart-svg { width: 100%; height: 100%; }

.chart-x-labels {
  display: flex;
  justify-content: space-around;
  font-size: 0.7rem;
  color: var(--c-text-muted);
  padding: 6px 46px 0;
  font-family: var(--font-mono);
}

.chart-legend {
  display: flex; gap: 20px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--c-border);
}
.legend-item {
  display: flex; align-items: center; gap: 6px;
  font-size: 0.78rem;
  color: var(--c-text-secondary);
}
.legend-dot { width: 8px; height: 3px; border-radius: 2px; }

.chart-side { display: flex; flex-direction: column; gap: 1.25rem; }

/* 疾病分布 */
.disease-list { display: flex; flex-direction: column; gap: 1rem; }
.disease-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 0.825rem;
}
.disease-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.disease-name { flex: 1; color: var(--c-text-primary); font-weight: 500; }
.disease-count { color: var(--c-text-muted); font-size: 0.75rem; font-family: var(--font-mono); }
.disease-pct { color: var(--c-text-secondary); font-weight: 700; font-size: 0.78rem; font-family: var(--font-mono); min-width: 36px; text-align: right; }

.progress-track {
  height: 6px;
  background: rgba(255,255,255,0.05);
  border-radius: 99px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  border-radius: 99px;
  transition: width 1.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.text-btn {
  font-size: 0.75rem;
  color: var(--c-blue);
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: opacity 0.2s;
}
.text-btn:hover { opacity: 0.7; }

/* 系统状态 */
.status-badge {
  font-size: 0.7rem;
  font-weight: 700;
  background: rgba(16,185,129,0.1);
  color: var(--c-green);
  border: 1px solid rgba(16,185,129,0.2);
  padding: 3px 10px;
  border-radius: 99px;
}

.status-list { display: flex; flex-direction: column; gap: 12px; }
.status-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.8rem;
}
.status-info {
  display: flex; align-items: center; gap: 6px;
  color: var(--c-text-secondary);
  flex: 1;
  min-width: 0;
  white-space: nowrap;
}
.status-icon { width: 14px; height: 14px; flex-shrink: 0; }
.status-bar-wrap { display: flex; align-items: center; gap: 6px; flex-shrink: 0; }
.status-mini-bar {
  width: 60px; height: 4px;
  background: rgba(255,255,255,0.06);
  border-radius: 2px;
  overflow: hidden;
}
.status-mini-bar > div { height: 100%; border-radius: 2px; transition: width 1s ease; }
.status-val { font-size: 0.7rem; color: var(--c-text-muted); font-family: var(--font-mono); min-width: 28px; }

.status-pill {
  font-size: 0.65rem;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 99px;
  white-space: nowrap;
}
.pill-ok { background: rgba(16,185,129,0.1); color: #34d399; }
.pill-warn { background: rgba(245,158,11,0.1); color: #fbbf24; }

/* =====================
   核心技术亮点
===================== */
.features-section { margin-bottom: 1.5rem; }
.section-header { text-align: center; margin-bottom: 1.75rem; }
.section-title {
  font-size: 1.6rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--c-text-primary);
  margin-bottom: 6px;
}
.section-desc { font-size: 0.875rem; color: var(--c-text-secondary); }

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.25rem;
}

.feature-card {
  background: var(--c-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--c-border);
  padding: 1.5rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.feature-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--radius-lg);
  opacity: 0;
  transition: opacity 0.3s;
  background: linear-gradient(135deg, rgba(59,130,246,0.05), rgba(139,92,246,0.05));
}
.feature-card:hover { transform: translateY(-5px); border-color: var(--c-border-hover); box-shadow: 0 20px 40px rgba(0,0,0,0.4); }
.feature-card:hover::after { opacity: 1; }

.feature-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.2rem;
}

.feature-icon-wrap {
  width: 52px; height: 52px;
  border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
}
.feature-svg { width: 28px; height: 28px; }
.icon-blue-soft { background: rgba(59,130,246,0.1); }
.icon-cyan-soft { background: rgba(6,182,212,0.1); }
.icon-amber-soft { background: rgba(245,158,11,0.1); }
.icon-purple-soft { background: rgba(139,92,246,0.1); }

.feature-number {
  font-size: 0.7rem;
  font-weight: 800;
  font-family: var(--font-mono);
  color: var(--c-text-muted);
  letter-spacing: 0.05em;
}

.feature-title {
  font-size: 1rem;
  font-weight: 700;
  color: var(--c-text-primary);
  margin-bottom: 0.6rem;
  letter-spacing: -0.01em;
}

.feature-desc {
  font-size: 0.82rem;
  color: var(--c-text-secondary);
  line-height: 1.65;
  margin-bottom: 1rem;
}

.feature-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.tag {
  font-size: 0.68rem;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 99px;
  background: rgba(255,255,255,0.04);
  border: 1px solid var(--c-border);
  color: var(--c-text-muted);
  letter-spacing: 0.02em;
}

/* =====================
   底部
===================== */
.dashboard-footer {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 1.25rem 2rem;
  font-size: 0.75rem;
  color: var(--c-text-muted);
  border-top: 1px solid var(--c-border);
  display: flex;
  justify-content: center;
  gap: 8px;
}
.footer-sep { color: var(--c-border); }

.dashboard-toast {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 50;
  max-width: min(360px, calc(100vw - 48px));
  padding: 12px 16px;
  border-radius: 14px;
  background: rgba(15, 39, 68, 0.96);
  border: 1px solid rgba(59, 130, 246, 0.35);
  color: var(--c-text-primary);
  font-size: 0.84rem;
  box-shadow: 0 14px 40px rgba(0, 0, 0, 0.36);
  backdrop-filter: blur(12px);
  animation: toast-in 0.2s ease-out;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* =====================
   响应式
===================== */
@media (max-width: 1280px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .features-grid { grid-template-columns: repeat(2, 1fr); }
  .charts-section { grid-template-columns: 1fr; }
  .chart-side { flex-direction: row; }
  .disease-card, .status-card { flex: 1; }
}

@media (max-width: 768px) {
  .top-nav { padding: 0 1rem; }
  .nav-status, .nav-time { display: none; }
  .main-content { padding: 1.25rem 1rem; }
  .page-header { flex-direction: column; align-items: flex-start; }
  .page-title { font-size: 1.5rem; }
  .stats-grid { grid-template-columns: 1fr 1fr; gap: 0.75rem; }
  .features-grid { grid-template-columns: 1fr; }
  .charts-section { grid-template-columns: 1fr; }
  .chart-side { flex-direction: column; }
}
</style>

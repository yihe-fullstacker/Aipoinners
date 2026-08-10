<template>
  <header class="top-nav" :class="{ 'is-scrolled': isScrolled }">
    <div class="nav-container">
      <button v-if="isLoggedIn && canShowBackBtn" class="nav-back-btn" @click="handleBack">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 12H5M12 5l-7 7 7 7"/>
        </svg>
      </button>

      <router-link to="/landing" class="nav-logo">
        <div class="logo-icon-wrap">
          <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
        </div>
        <div class="logo-text-wrap">
          <span class="logo-name">影智医疗</span>
          <span class="logo-sub">AI · 影像 · 诊断</span>
        </div>
      </router-link>

      <nav class="nav-menu desktop-nav">
        <router-link
          v-for="item in displayNavItems"
          :key="item.key"
          :to="item.to"
          :class="['nav-item', { 'is-active': isActive(item) }]"
        >
          <span class="nav-item-text">{{ item.label }}</span>
          <span class="nav-item-line"></span>
        </router-link>
      </nav>

      <div class="nav-right">
        <button
          class="theme-toggle-btn"
          type="button"
          :aria-pressed="isMedicalTheme ? 'true' : 'false'"
          @click="toggleTheme"
        >
          <span class="theme-toggle-dot"></span>
          <span>{{ isMedicalTheme ? '深色主题' : '科技蓝主题' }}</span>
        </button>

        <template v-if="isLoggedIn">
          <router-link to="/reports" class="history-btn">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10,9 9,9 8,9"/>
            </svg>
            历史报告
          </router-link>

          <div ref="userDropdown" class="user-dropdown" @click.stop>
            <div class="user-avatar" @click.stop="toggleUserMenu">
              <span class="avatar-text">{{ userInitial }}</span>
              <span class="avatar-ring"></span>
            </div>

            <teleport to="body">
              <div v-show="showUserMenu" ref="userMenu" class="user-menu" :style="userMenuStyle" @click.stop>
                <div class="user-info">
                  <div class="user-avatar-sm">{{ userInitial }}</div>
                  <div>
                    <div class="user-name">{{ userName }}</div>
                    <div class="user-role">
                      <span class="role-dot"></span>{{ userRole }}
                    </div>
                  </div>
                </div>
                <div class="menu-divider"></div>
                <button class="menu-item logout-btn" @click="handleLogout">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                    <polyline points="16,17 21,12 16,7"/>
                    <line x1="21" y1="12" x2="9" y2="12"/>
                  </svg>
                  退出登录
                </button>
              </div>
            </teleport>
          </div>
        </template>

        <button
          v-if="isMobile"
          class="hamburger-btn"
          @click="toggleMobileMenu"
          :aria-label="isMenuOpen ? '关闭菜单' : '打开菜单'"
        >
          <span class="hamburger-line" :class="{ 'is-open': isMenuOpen }"></span>
          <span class="hamburger-line" :class="{ 'is-open': isMenuOpen }"></span>
          <span class="hamburger-line" :class="{ 'is-open': isMenuOpen }"></span>
        </button>
      </div>
    </div>

    <transition name="mobile-menu">
      <div v-if="!isLoggedIn && isMobile && isMenuOpen" class="mobile-menu-panel">
        <nav class="mobile-nav">
          <router-link
            v-for="item in displayNavItems"
            :key="item.key"
            :to="item.to"
            class="mobile-nav-item"
            @click="isMenuOpen = false"
          >
            {{ item.label }}
          </router-link>
        </nav>
        <div class="mobile-nav-footer">
          <button type="button" class="mobile-theme-btn" @click="toggleTheme">
            {{ isMedicalTheme ? '切换到深色主题' : '切换到医疗AI主题' }}
          </button>
        </div>
      </div>
    </transition>
  </header>
</template>

<script>
import navigationHistory from '@/utils/navigationHistory'

export default {
  name: 'TopNav',
  data() {
    return {
      isScrolled: false,
      isMenuOpen: false,
      showUserMenu: false,
      userMenuStyle: {
        top: '80px',
        left: '16px',
        minWidth: '236px'
      },
      isMobile: window.innerWidth < 768,
      navItems: [
        { key: 'landing', label: '首页', to: '/landing' },
        { key: 'about', label: '关于', to: '/about' },
        { key: 'instruction', label: '使用说明', to: '/instruction' },
        { key: 'login', label: '登录', to: '/login' },
        { key: 'register', label: '注册', to: '/register' }
      ]
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.state.isLoggedIn || false
    },
    currentTheme() {
      return this.$store.state.theme || 'dark'
    },
    isMedicalTheme() {
      return this.currentTheme === 'medical-ai'
    },
    canShowBackBtn() {
      return navigationHistory.canGoBack()
    },
    user() {
      return this.$store.state.user || {}
    },
    userName() {
      return this.user.fullName || this.user.username || '用户'
    },
    userInitial() {
      const name = this.userName
      return name ? name.charAt(0).toUpperCase() : 'U'
    },
    userRole() {
      const roleMap = {
        DOCTOR: '医生',
        PATIENT: '患者',
        ADMIN: '管理员'
      }
      return roleMap[this.user.role] || '用户'
    },
    displayNavItems() {
      if (this.isLoggedIn) {
        return []
      }
      return this.navItems
    }
  },
  methods: {
    async handleBack() {
      const onConfirm = async () => {
        return new Promise((resolve) => {
          const confirmed = window.confirm('当前页面内容可能未保存，是否继续离开？')
          resolve(confirmed)
        })
      }

      const success = await navigationHistory.goBackStep(this.$router, onConfirm)
      if (!success) {
        console.warn('[TopNav] 返回失败或已取消返回')
      }
    },
    toggleUserMenu() {
      this.showUserMenu = !this.showUserMenu
      if (this.showUserMenu) {
        this.$nextTick(() => {
          this.updateUserMenuPosition()
        })
      }
    },
    handleLogout() {
      this.$store.dispatch('logout')
      this.$router.push('/landing')
      this.showUserMenu = false
    },
    toggleTheme() {
      const nextTheme = this.isMedicalTheme ? 'dark' : 'medical-ai'
      this.$store.dispatch('setTheme', nextTheme)
    },
    toggleMobileMenu() {
      this.isMenuOpen = !this.isMenuOpen
    },
    handleKeydown(e) {
      if (e.key === 'Escape' && this.showUserMenu) {
        this.showUserMenu = false
      }
    },
    isActive(item) {
      return this.$route.path === item.to
    },
    handleResize() {
      this.isMobile = window.innerWidth < 768
      if (!this.isMobile) {
        this.isMenuOpen = false
      }
      if (this.showUserMenu) {
        this.updateUserMenuPosition()
      }
    },
    handleScroll() {
      this.isScrolled = window.scrollY > 20
      if (this.showUserMenu) {
        this.updateUserMenuPosition()
      }
    },
    handleClickOutside(e) {
      const dropdown = this.$refs.userDropdown
      const menu = this.$refs.userMenu
      const clickedDropdown = dropdown && dropdown.contains(e.target)
      const clickedMenu = menu && menu.contains(e.target)
      if (!clickedDropdown && !clickedMenu) {
        this.showUserMenu = false
      }
    },
    updateUserMenuPosition() {
      const dropdown = this.$refs.userDropdown
      if (!dropdown) {
        return
      }

      const rect = dropdown.getBoundingClientRect()
      const menuWidth = Math.min(236, Math.max(220, window.innerWidth - 32))
      const gutter = 16
      const top = Math.round(rect.bottom + 12)
      const maxLeft = window.innerWidth - menuWidth - gutter
      const left = Math.max(gutter, Math.min(maxLeft, Math.round(rect.right - menuWidth)))

      this.userMenuStyle = {
        top: `${top}px`,
        left: `${left}px`,
        minWidth: `${menuWidth}px`
      }
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
    window.addEventListener('scroll', this.handleScroll)
    document.addEventListener('click', this.handleClickOutside)
    document.addEventListener('keydown', this.handleKeydown)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    window.removeEventListener('scroll', this.handleScroll)
    document.removeEventListener('click', this.handleClickOutside)
    document.removeEventListener('keydown', this.handleKeydown)
  }
}
</script>

<style scoped>
.top-nav {
  --nav-bg: var(--ui-nav-bg);
  --nav-bg-scrolled: var(--ui-nav-bg-scrolled);
  --nav-border: var(--ui-nav-border);
  --nav-border-scrolled: var(--ui-nav-border-scrolled);
  --nav-text: var(--ui-text-primary);
  --nav-muted: var(--ui-text-muted);
  --nav-subtle: var(--ui-text-subtle);
  --nav-chip-bg: var(--ui-surface-chip);
  --nav-chip-hover: var(--ui-surface-chip-strong);
  --nav-chip-border: var(--ui-border-soft);
  --nav-shadow: var(--ui-nav-shadow);
  --nav-primary: var(--ui-brand-primary);
  --nav-accent: var(--ui-brand-accent);
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  height: var(--app-top-nav-height, 68px);
  background: var(--nav-bg);
  backdrop-filter: blur(var(--ui-glass-blur));
  -webkit-backdrop-filter: blur(var(--ui-glass-blur));
  border-bottom: 1px solid var(--nav-border);
  z-index: 1200;
  overflow: visible;
  flex-shrink: 0;
  transition: background 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
}

.top-nav.is-scrolled {
  background: var(--nav-bg-scrolled);
  border-bottom-color: var(--nav-border-scrolled);
  box-shadow: var(--nav-shadow);
}

.nav-container {
  max-width: var(--app-shell-max-width, 1280px);
  margin: 0 auto;
  padding: 0 var(--app-shell-gutter, 32px);
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  overflow: visible;
}

.nav-back-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border: 1px solid var(--nav-chip-border);
  background: var(--nav-chip-bg);
  color: var(--nav-muted);
  cursor: pointer;
  border-radius: var(--ui-radius-sm);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.nav-back-btn:hover {
  background: var(--nav-chip-hover);
  color: var(--nav-text);
  border-color: var(--nav-border-scrolled);
  transform: translateX(-2px);
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon-wrap {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, var(--nav-primary) 0%, var(--nav-accent) 100%);
  border-radius: var(--ui-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 0 16px rgba(var(--ui-brand-primary-rgb), 0.28);
}

.logo-img {
  width: 22px;
  height: 22px;
  filter: brightness(0) invert(1);
}

.logo-text-wrap {
  display: flex;
  flex-direction: column;
  line-height: 1;
}

.logo-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--nav-text);
  letter-spacing: 0.02em;
}

.logo-sub {
  font-size: 10px;
  color: rgba(var(--ui-brand-primary-rgb), 0.78);
  font-weight: 500;
  letter-spacing: 0.08em;
  margin-top: 2px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  position: relative;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: var(--nav-muted);
  text-decoration: none;
  transition: color 0.2s ease;
  border-radius: var(--ui-radius-sm);
}

.nav-item:hover {
  color: var(--nav-text);
  background: var(--nav-chip-bg);
}

.nav-item.is-active {
  color: var(--nav-primary);
}

.nav-item-line {
  display: block;
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--nav-primary), var(--nav-accent));
  border-radius: 1px;
  transition: width 0.25s ease;
}

.nav-item.is-active .nav-item-line,
.nav-item:hover .nav-item-line {
  width: calc(100% - 24px);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  position: relative;
  z-index: 1201;
  overflow: visible;
}

.theme-toggle-btn,
.mobile-theme-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 38px;
  padding: 0 16px;
  border-radius: var(--radius-full);
  border: 1px solid var(--nav-chip-border);
  background: var(--nav-chip-bg);
  color: var(--nav-text);
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.25s ease;
}

.theme-toggle-btn:hover,
.mobile-theme-btn:hover {
  background: var(--nav-chip-hover);
  border-color: var(--nav-border-scrolled);
  transform: translateY(-1px);
}

.theme-toggle-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--nav-primary), var(--nav-accent));
  box-shadow: 0 0 12px rgba(var(--ui-brand-primary-rgb), 0.3);
}

.history-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: rgba(var(--ui-brand-primary-rgb), 0.1);
  color: var(--nav-primary);
  border: 1px solid rgba(var(--ui-brand-primary-rgb), 0.2);
  border-radius: var(--ui-radius-sm);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
}

.history-btn:hover {
  background: rgba(var(--ui-brand-primary-rgb), 0.16);
  border-color: rgba(var(--ui-brand-primary-rgb), 0.35);
  transform: translateY(-1px);
}

.user-dropdown {
  position: relative;
  cursor: pointer;
  overflow: visible;
  z-index: 3;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--nav-primary) 0%, var(--nav-accent) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.2s ease;
  box-shadow: 0 0 0 2px rgba(var(--ui-brand-primary-rgb), 0.2);
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 0 0 3px rgba(var(--ui-brand-primary-rgb), 0.3);
}

.avatar-text {
  color: white;
  font-size: 15px;
  font-weight: 700;
}

.user-menu {
  position: fixed;
  width: auto;
  max-width: calc(100vw - 32px);
  background: var(--ui-surface-panel-strong);
  border: 1px solid var(--ui-border-soft);
  border-radius: var(--ui-radius-md);
  box-shadow: var(--ui-shadow-panel-strong);
  backdrop-filter: blur(var(--ui-glass-blur));
  -webkit-backdrop-filter: blur(var(--ui-glass-blur));
  padding: 6px;
  z-index: 99999;
  transform-origin: top right;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to   { opacity: 1; transform: translateY(0); }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
}

.user-avatar-sm {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--nav-primary), var(--nav-accent));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--nav-text);
  margin-bottom: 2px;
}

.user-role {
  font-size: 12px;
  color: var(--nav-subtle);
  display: flex;
  align-items: center;
  gap: 5px;
}

.role-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--nav-accent);
  display: inline-block;
}

.menu-divider {
  height: 1px;
  background: var(--ui-border-soft);
  margin: 4px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 14px;
  border: none;
  background: none;
  font-size: 14px;
  color: var(--nav-muted);
  cursor: pointer;
  transition: all 0.15s ease;
  border-radius: var(--ui-radius-sm);
  text-align: left;
}

.menu-item:hover {
  background: var(--nav-chip-bg);
  color: var(--nav-text);
}

.logout-btn:hover {
  color: #f87171;
  background: rgba(248, 113, 113, 0.08);
}

.hamburger-btn {
  display: none;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  background: var(--nav-chip-bg);
  border: 1px solid var(--nav-chip-border);
  cursor: pointer;
  padding: 8px;
  gap: 5px;
  border-radius: var(--ui-radius-sm);
}

.hamburger-line {
  width: 20px;
  height: 1.5px;
  background: var(--nav-text);
  transition: all 0.3s ease;
  border-radius: 1px;
}

.hamburger-line.is-open:nth-child(1) { transform: rotate(45deg) translate(4.5px, 4.5px); }
.hamburger-line.is-open:nth-child(2) { opacity: 0; }
.hamburger-line.is-open:nth-child(3) { transform: rotate(-45deg) translate(4.5px, -4.5px); }

.mobile-menu-panel {
  position: fixed;
  top: var(--app-top-nav-height, 68px);
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--nav-bg-scrolled);
  z-index: 1190;
  display: flex;
  flex-direction: column;
  padding: 24px 20px;
  backdrop-filter: blur(var(--ui-glass-blur));
}

.mobile-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mobile-nav-item {
  display: block;
  padding: 14px 16px;
  font-size: 16px;
  font-weight: 500;
  color: var(--nav-muted);
  text-decoration: none;
  border-radius: var(--ui-radius-sm);
  transition: all 0.2s ease;
  border-bottom: 1px solid var(--ui-border-soft);
}

.mobile-nav-item:hover {
  color: var(--nav-text);
  background: rgba(var(--ui-brand-primary-rgb), 0.08);
  border-color: transparent;
}

.mobile-nav-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid var(--ui-border-soft);
}

.mobile-login-btn {
  display: block;
  text-align: center;
  padding: 14px;
  border-radius: var(--ui-radius-sm);
  font-size: 15px;
  font-weight: 500;
  color: var(--nav-text);
  text-decoration: none;
  border: 1px solid var(--nav-chip-border);
}

.mobile-register-btn {
  display: block;
  text-align: center;
  padding: 14px;
  border-radius: var(--ui-radius-sm);
  font-size: 15px;
  font-weight: 600;
  color: var(--ui-text-contrast);
  text-decoration: none;
  background: linear-gradient(135deg, var(--nav-primary), var(--nav-accent));
}

@media (max-width: 768px) {
  .desktop-nav { display: none; }
  .hamburger-btn { display: flex; }
  .theme-toggle-btn { display: none; }
  .mobile-theme-btn { width: 100%; }
}

.mobile-menu-enter-active, .mobile-menu-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.mobile-menu-enter-from, .mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>

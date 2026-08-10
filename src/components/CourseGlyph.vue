<template>
  <span
    class="course-glyph"
    :class="[`glyph-${tone}`, compact ? 'is-compact' : '', animated ? 'is-animated' : '']"
    aria-hidden="true"
  >
    <!-- 光晕背景 -->
    <span class="glyph-halo"></span>

    <svg v-if="icon === 'ai'" viewBox="0 0 24 24" fill="none">
      <rect x="5" y="5" width="14" height="14" rx="4" />
      <path d="M9 12h6M12 9v6" />
      <path d="M8 3v3M16 3v3M8 18v3M16 18v3M3 8h3M18 8h3M3 16h3M18 16h3" />
    </svg>
    <svg v-else-if="icon === 'imaging'" viewBox="0 0 24 24" fill="none">
      <rect x="4" y="5" width="16" height="14" rx="4" />
      <circle cx="10" cy="11" r="2.4" />
      <path d="m13.5 14 2-2 2.5 3" />
      <path d="M8 16.5 11 13l2 2.5" />
    </svg>
    <svg v-else-if="icon === 'health'" viewBox="0 0 24 24" fill="none">
      <path d="M12 20s-6.5-4.2-8.5-8A4.9 4.9 0 0 1 12 6.2 4.9 4.9 0 0 1 20.5 12C18.5 15.8 12 20 12 20Z" />
      <path d="M12 8.5v7M8.5 12H15.5" />
    </svg>
    <svg v-else-if="icon === 'data'" viewBox="0 0 24 24" fill="none">
      <path d="M5 19V9M12 19V5M19 19v-8M3 19h18" />
      <circle cx="5" cy="9" r="1.4" />
      <circle cx="12" cy="5" r="1.4" />
      <circle cx="19" cy="11" r="1.4" />
    </svg>
    <svg v-else-if="icon === 'tele'" viewBox="0 0 24 24" fill="none">
      <rect x="6" y="3.5" width="12" height="17" rx="3" />
      <path d="M9 7h6M9 11h6" />
      <circle cx="12" cy="16.5" r="1.2" />
    </svg>
    <svg v-else-if="icon === 'ethics'" viewBox="0 0 24 24" fill="none">
      <path d="M12 4 6 7v5c0 4.2 2.6 6.9 6 8 3.4-1.1 6-3.8 6-8V7l-6-3Z" />
      <path d="M9.5 12 11 13.5 14.5 10" />
    </svg>
    <svg v-else-if="icon === 'lung'" viewBox="0 0 24 24" fill="none">
      <path d="M12 5v6" />
      <path d="M12 11c-1.6-3-4-4-5.8-3.6C4 8 3 10.2 3 12.8c0 3.3 1.9 6 5 6 2.2 0 3.4-1.5 4-3.6" />
      <path d="M12 11c1.6-3 4-4 5.8-3.6C20 8 21 10.2 21 12.8c0 3.3-1.9 6-5 6-2.2 0-3.4-1.5-4-3.6" />
    </svg>
    <svg v-else-if="icon === 'scan'" viewBox="0 0 24 24" fill="none">
      <rect x="4" y="4" width="16" height="16" rx="4" />
      <path d="M8 8h3M13 8h3M8 12h8M8 16h5" />
    </svg>
    <svg v-else-if="icon === 'bone'" viewBox="0 0 24 24" fill="none">
      <path d="M6.2 9.2a2.2 2.2 0 1 1 2.9-3.3l5.8 5.1a2.6 2.6 0 0 1 .4 3.6l-1.3 1.6a2.6 2.6 0 0 1-3.6.4L4.6 11.5a2.2 2.2 0 1 1 1.6-2.3Z" />
      <path d="M17.8 14.8a2.2 2.2 0 1 1-2.9 3.3l-5.8-5.1a2.6 2.6 0 0 1-.4-3.6L10 7.8a2.6 2.6 0 0 1 3.6-.4l5.8 5.1a2.2 2.2 0 1 1-1.6 2.3Z" />
    </svg>
    <svg v-else-if="icon === 'brain'" viewBox="0 0 24 24" fill="none">
      <path d="M9.5 5.5a3 3 0 0 1 5.4 1.8 3.3 3.3 0 0 1 2.7 3.2 3.1 3.1 0 0 1-.6 1.8 3 3 0 0 1 .8 2 3.2 3.2 0 0 1-3.2 3.2H9.2A3.2 3.2 0 0 1 6 14.3c0-.8.3-1.5.8-2A3.1 3.1 0 0 1 6.2 10a3.3 3.3 0 0 1 2.7-3.2 3 3 0 0 1 .6-1.3Z" />
      <path d="M10 9.5c.8 0 1.5.7 1.5 1.5M14 9c0 .9-.7 1.6-1.6 1.6M10 13.8c.3.7 1 1.2 1.9 1.2M14.5 13.2c-.2.8-.9 1.3-1.7 1.3" />
    </svg>
    <svg v-else-if="icon === 'heart'" viewBox="0 0 24 24" fill="none">
      <path d="M12 20s-7-4.3-8.5-8.3A5 5 0 0 1 12 6a5 5 0 0 1 8.5 5.7C19 15.7 12 20 12 20Z" />
      <path d="M12 8.5v7M9.5 12h5" />
    </svg>
    <svg v-else-if="icon === 'device'" viewBox="0 0 24 24" fill="none">
      <rect x="7" y="3" width="10" height="18" rx="3" />
      <path d="M10 7h4M10 11h4M10 15h4" />
    </svg>
    <svg v-else-if="icon === 'privacy'" viewBox="0 0 24 24" fill="none">
      <rect x="4" y="11" width="16" height="9" rx="2" />
      <path d="M8 11V8a4 4 0 1 1 8 0v3" />
      <circle cx="12" cy="15.5" r="1.2" />
    </svg>
    <svg v-else-if="icon === 'calendar'" viewBox="0 0 24 24" fill="none">
      <rect x="4" y="5" width="16" height="15" rx="3" />
      <path d="M8 3v4M16 3v4M4 10h16" />
      <path d="M9 14h2M13 14h2M9 17h2" />
    </svg>
    <svg v-else-if="icon === 'chart'" viewBox="0 0 24 24" fill="none">
      <path d="M4 19h16M7 16v-5M12 16V7M17 16v-8" />
      <path d="m6 9 4-3 4 2 4-3" />
    </svg>
    <svg v-else-if="icon === 'warning'" viewBox="0 0 24 24" fill="none">
      <path d="M12 4 3.5 19h17L12 4Z" />
      <path d="M12 9v4M12 16h.01" />
    </svg>
    <svg v-else-if="icon === 'question'" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="9" />
      <path d="M9.5 9a2.5 2.5 0 1 1 4.3 1.8c-.8.7-1.8 1.1-1.8 2.2" />
      <path d="M12 16.2h.01" />
    </svg>
    <svg v-else viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="9" />
      <path d="M8 12h8M12 8v8" />
    </svg>
  </span>
</template>

<script>
export default {
  name: 'CourseGlyph',
  props: {
    icon: { type: String, default: 'ai' },
    tone: { type: String, default: 'teal' },
    compact: { type: Boolean, default: false },
    animated: { type: Boolean, default: false }
  }
}
</script>

<style scoped>
.course-glyph {
  --glyph-bg: rgba(0,212,180,0.10);
  --glyph-border: rgba(0,212,180,0.22);
  --glyph-color: #5ff0da;
  --glyph-halo: rgba(0,212,180,0.08);
  --glyph-shadow: rgba(0,212,180,0.18);

  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  background: var(--glyph-bg);
  border: 1px solid var(--glyph-border);
  color: var(--glyph-color);
  position: relative;
  box-shadow:
    inset 0 1px 0 rgba(255,255,255,0.06),
    0 8px 20px var(--glyph-shadow);
  transition: all 0.25s ease;
  flex-shrink: 0;
}

.course-glyph:hover {
  transform: translateY(-2px);
  box-shadow:
    inset 0 1px 0 rgba(255,255,255,0.08),
    0 14px 28px var(--glyph-shadow);
  border-color: var(--glyph-color);
}

.glyph-halo {
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: radial-gradient(circle at 30% 20%, rgba(255,255,255,0.06), transparent 60%);
  pointer-events: none;
}

.course-glyph.is-compact {
  width: 38px;
  height: 38px;
  border-radius: 11px;
}

.course-glyph.is-animated {
  animation: glyphPulse 3s ease-in-out infinite;
}

@keyframes glyphPulse {
  0%, 100% { box-shadow: inset 0 1px 0 rgba(255,255,255,0.06), 0 8px 20px var(--glyph-shadow); }
  50% { box-shadow: inset 0 1px 0 rgba(255,255,255,0.06), 0 8px 28px var(--glyph-shadow), 0 0 0 4px var(--glyph-halo); }
}

.course-glyph svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  position: relative;
  z-index: 1;
  transition: transform 0.25s ease;
}

.course-glyph.is-compact svg {
  width: 17px;
  height: 17px;
}

.course-glyph:hover svg {
  transform: scale(1.08);
}

/* ── Tone 变体 ── */
.course-glyph.glyph-teal {
  --glyph-bg: rgba(0,212,180,0.10);
  --glyph-border: rgba(0,212,180,0.22);
  --glyph-color: #5ff0da;
  --glyph-halo: rgba(0,212,180,0.06);
  --glyph-shadow: rgba(0,212,180,0.15);
}

.course-glyph.glyph-blue {
  --glyph-bg: rgba(74,158,255,0.10);
  --glyph-border: rgba(74,158,255,0.22);
  --glyph-color: #82beff;
  --glyph-halo: rgba(74,158,255,0.06);
  --glyph-shadow: rgba(74,158,255,0.15);
}

.course-glyph.glyph-violet {
  --glyph-bg: rgba(167,139,250,0.10);
  --glyph-border: rgba(167,139,250,0.22);
  --glyph-color: #c4b5fd;
  --glyph-halo: rgba(167,139,250,0.06);
  --glyph-shadow: rgba(167,139,250,0.15);
}

.course-glyph.glyph-amber {
  --glyph-bg: rgba(245,158,11,0.10);
  --glyph-border: rgba(245,158,11,0.22);
  --glyph-color: #fcd27a;
  --glyph-halo: rgba(245,158,11,0.06);
  --glyph-shadow: rgba(245,158,11,0.15);
}

.course-glyph.glyph-rose {
  --glyph-bg: rgba(248,113,113,0.10);
  --glyph-border: rgba(248,113,113,0.22);
  --glyph-color: #fca5a5;
  --glyph-halo: rgba(248,113,113,0.06);
  --glyph-shadow: rgba(248,113,113,0.15);
}

.course-glyph.glyph-green {
  --glyph-bg: rgba(16,185,129,0.10);
  --glyph-border: rgba(16,185,129,0.22);
  --glyph-color: #6ee7b7;
  --glyph-halo: rgba(16,185,129,0.06);
  --glyph-shadow: rgba(16,185,129,0.15);
}
</style>
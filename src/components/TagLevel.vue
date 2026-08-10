<template>
  <span :class="['tag-level', `level-${level}`, `size-${size}`]" :aria-label="resolvedAriaLabel">
    <span v-if="showMarker" class="tag-marker" aria-hidden="true"></span>
    <span class="tag-text">{{ displayText }}</span>
  </span>
</template>

<script>
export default {
  name: 'TagLevel',
  props: {
    level: {
      type: String,
      required: true,
      validator: (value) => ['beginner', 'intermediate', 'advanced'].includes(value)
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    text: {
      type: String,
      default: ''
    },
    showIcon: {
      type: Boolean,
      default: true
    },
    ariaLabel: {
      type: String,
      default: ''
    }
  },
  computed: {
    displayText() {
      if (this.text) return this.text

      const texts = {
        beginner: '入门级',
        intermediate: '进阶级',
        advanced: '高级'
      }

      return texts[this.level] || ''
    },
    resolvedAriaLabel() {
      return this.ariaLabel || `课程难度：${this.displayText}`
    },
    showMarker() {
      return this.showIcon
    }
  }
}
</script>

<style scoped>
.tag-level {
  --tag-bg: rgba(255, 255, 255, 0.82);
  --tag-border: rgba(148, 163, 184, 0.24);
  --tag-text: #334155;
  --tag-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85);

  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: 999px;
  border: 1px solid var(--tag-border);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(255, 255, 255, 0.78)),
    var(--tag-bg);
  box-shadow: var(--tag-shadow);
  color: var(--tag-text);
  font-weight: 600;
  letter-spacing: 0.02em;
  white-space: nowrap;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.tag-level:hover {
  transform: translateY(-1px);
}

.tag-level.level-beginner {
  --tag-bg: rgba(236, 253, 245, 0.92);
  --tag-border: rgba(16, 185, 129, 0.18);
  --tag-text: #0f766e;
}

.tag-level.level-intermediate {
  --tag-bg: rgba(239, 246, 255, 0.94);
  --tag-border: rgba(59, 130, 246, 0.18);
  --tag-text: #1d4ed8;
}

.tag-level.level-advanced {
  --tag-bg: rgba(241, 245, 249, 0.96);
  --tag-border: rgba(71, 85, 105, 0.18);
  --tag-text: #0f172a;
}

.tag-level.size-small {
  min-height: 24px;
  padding: 0 10px;
  font-size: 11px;
}

.tag-level.size-medium {
  min-height: 28px;
  padding: 0 12px;
  font-size: 12px;
}

.tag-level.size-large {
  min-height: 34px;
  padding: 0 14px;
  font-size: 13px;
}

.tag-marker {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.92;
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.55);
}

.tag-text {
  line-height: 1;
}

@media (prefers-reduced-motion: reduce) {
  .tag-level,
  .tag-level:hover {
    transform: none;
  }
}
</style>

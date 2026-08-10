<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="modelValue" class="video-modal" @click.self="close">
        <div class="video-modal-content">
          <button class="video-close-btn" type="button" @click="close">×</button>
          <div class="video-iframe-container">
            <iframe
              v-if="activeSrc"
              :key="iframeKey"
              :src="activeSrc"
              scrolling="no"
              frameborder="0"
              allow="autoplay; fullscreen; encrypted-media; picture-in-picture"
              allowfullscreen
              webkitallowfullscreen
              mozallowfullscreen
              referrerpolicy="strict-origin-when-cross-origin"
              class="video-iframe"
            ></iframe>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
export default {
  name: 'VideoModalPlayer',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    src: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  data() {
    return {
      activeSrc: '',
      iframeKey: 0
    }
  },
  watch: {
    modelValue: {
      immediate: true,
      handler(visible) {
        if (visible) {
          this.mountPlayer()
          document.body.style.overflow = 'hidden'
          return
        }

        this.teardownPlayer()
        document.body.style.overflow = ''
      }
    },
    src() {
      if (this.modelValue) {
        this.mountPlayer()
      }
    }
  },
  beforeUnmount() {
    document.body.style.overflow = ''
  },
  methods: {
    close() {
      this.$emit('update:modelValue', false)
    },
    mountPlayer() {
      if (!this.src) {
        this.activeSrc = ''
        return
      }

      const separator = this.src.includes('?') ? '&' : '?'
      const withAutoplay = /(?:\?|&)autoplay=/.test(this.src)
        ? this.src
        : `${this.src}${separator}autoplay=1`
      const refreshSeparator = withAutoplay.includes('?') ? '&' : '?'
      this.iframeKey += 1
      this.activeSrc = `${withAutoplay}${refreshSeparator}_ts=${Date.now()}`
    },
    teardownPlayer() {
      this.activeSrc = ''
    }
  }
}
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.video-modal {
  position: fixed;
  inset: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 30000;
  isolation: isolate;
}

.video-modal-content {
  position: relative;
  width: min(90vw, 960px);
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.45);
}

.video-close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  z-index: 10;
}

.video-iframe-container {
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #000;
}

.video-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
}
</style>

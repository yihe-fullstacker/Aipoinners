<template>
  <div class="heart-scene" :class="{ 'is-ready': isReady, 'has-error': hasError }">
    <div ref="viewportRef" class="heart-scene__viewport"></div>
    <div v-if="!isReady" class="heart-scene__placeholder" aria-hidden="true">
      <span class="placeholder-core"></span>
      <span class="placeholder-ring placeholder-ring--one"></span>
      <span class="placeholder-ring placeholder-ring--two"></span>
    </div>
    <div class="heart-scene__glow heart-scene__glow--blue"></div>
    <div class="heart-scene__glow heart-scene__glow--teal"></div>
    <div class="heart-scene__scan"></div>
    <div class="heart-scene__grain"></div>
  </div>
</template>

<script>
import { onBeforeUnmount, onMounted, ref } from 'vue'

export default {
  name: 'MedicalHeartScene',
  props: {
    modelUrl: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const viewportRef = ref(null)
    const isReady = ref(false)
    const hasError = ref(false)

    let THREE = null
    let scene = null
    let camera = null
    let renderer = null
    let frameId = null
    let resizeObserver = null
    let rootGroup = null
    let currentObject = null
    let disposed = false
    const pointer = { x: 0, y: 0 }

    const createFallbackObject = () => {
      const group = new THREE.Group()

      const shell = new THREE.Mesh(
        new THREE.IcosahedronGeometry(1.02, 3),
        new THREE.MeshStandardMaterial({
          color: '#8adfff',
          emissive: '#3f99ff',
          emissiveIntensity: 0.82,
          metalness: 0.12,
          roughness: 0.24
        })
      )

      const core = new THREE.Mesh(
        new THREE.OctahedronGeometry(0.48, 2),
        new THREE.MeshBasicMaterial({
          color: '#88fff1',
          transparent: true,
          opacity: 0.86
        })
      )

      const ringA = new THREE.Mesh(
        new THREE.TorusGeometry(1.42, 0.03, 24, 160),
        new THREE.MeshBasicMaterial({
          color: '#6ad6ff',
          transparent: true,
          opacity: 0.5
        })
      )

      const ringB = new THREE.Mesh(
        new THREE.TorusGeometry(1.06, 0.024, 24, 160),
        new THREE.MeshBasicMaterial({
          color: '#56e4cd',
          transparent: true,
          opacity: 0.44
        })
      )

      ringA.rotation.x = Math.PI / 2.7
      ringB.rotation.y = Math.PI / 2.35

      group.add(shell)
      group.add(core)
      group.add(ringA)
      group.add(ringB)
      return group
    }

    const applySceneMaterial = (mesh, index) => {
      const material = new THREE.MeshStandardMaterial({
        color: index % 2 === 0 ? '#88dcff' : '#7be8e2',
        emissive: index % 2 === 0 ? '#358dff' : '#2dbba8',
        emissiveIntensity: 0.48,
        metalness: 0.08,
        roughness: 0.3
      })

      mesh.material = material
      mesh.castShadow = false
      mesh.receiveShadow = false
      mesh.frustumCulled = false

      const outline = new THREE.Mesh(
        mesh.geometry,
        new THREE.MeshBasicMaterial({
          color: '#b8f2ff',
          transparent: true,
          opacity: 0.08,
          side: THREE.BackSide
        })
      )
      outline.scale.multiplyScalar(1.025)
      mesh.add(outline)
    }

    const fitObject = (object) => {
      const box = new THREE.Box3().setFromObject(object)
      const size = box.getSize(new THREE.Vector3())
      const center = box.getCenter(new THREE.Vector3())
      const maxDim = Math.max(size.x, size.y, size.z) || 1
      const scale = 2.35 / maxDim

      object.position.sub(center)
      object.scale.setScalar(scale)
      object.position.y -= 0.08
      object.rotation.x = -0.14
      object.rotation.z = 0.08
    }

    const disposeObject = (object) => {
      object?.traverse?.((child) => {
        child.geometry?.dispose?.()
        if (child.material) {
          if (Array.isArray(child.material)) {
            child.material.forEach((item) => item?.dispose?.())
          } else {
            child.material.dispose?.()
          }
        }
      })
    }

    const updateSize = () => {
      if (!viewportRef.value || !renderer || !camera) {
        return
      }

      const { clientWidth, clientHeight } = viewportRef.value
      if (!clientWidth || !clientHeight) {
        return
      }

      camera.aspect = clientWidth / clientHeight
      camera.updateProjectionMatrix()
      renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 1.5))
      renderer.setSize(clientWidth, clientHeight, false)
    }

    const renderLoop = () => {
      if (disposed) {
        return
      }

      frameId = requestAnimationFrame(renderLoop)

      if (rootGroup) {
        const time = performance.now() * 0.001
        rootGroup.rotation.y += 0.0042
        rootGroup.rotation.x = THREE.MathUtils.lerp(rootGroup.rotation.x, -0.12 + pointer.y * 0.12, 0.05)
        rootGroup.rotation.z = THREE.MathUtils.lerp(rootGroup.rotation.z, 0.06 - pointer.x * 0.1, 0.05)
        rootGroup.position.y = THREE.MathUtils.lerp(rootGroup.position.y, Math.sin(time * 1.5) * 0.07, 0.05)
      }

      renderer.render(scene, camera)
    }

    const mountObject = (object) => {
      currentObject = object
      fitObject(currentObject)
      rootGroup.add(currentObject)
      isReady.value = true
      updateSize()
      renderLoop()
    }

    const initRenderer = async () => {
      const [{ GLTFLoader }, threeModule] = await Promise.all([
        import('three/examples/jsm/loaders/GLTFLoader.js'),
        import('three')
      ])

      if (disposed || !viewportRef.value) {
        return null
      }

      THREE = threeModule
      scene = new THREE.Scene()
      camera = new THREE.PerspectiveCamera(30, 1, 0.1, 100)
      camera.position.set(0, 0.08, 6.4)

      renderer = new THREE.WebGLRenderer({
        alpha: true,
        antialias: true,
        powerPreference: 'low-power'
      })
      renderer.outputColorSpace = THREE.SRGBColorSpace
      renderer.setClearColor(0x000000, 0)
      renderer.domElement.style.width = '100%'
      renderer.domElement.style.height = '100%'
      viewportRef.value.appendChild(renderer.domElement)

      scene.add(new THREE.HemisphereLight('#c2f1ff', '#0c2840', 1.75))

      const keyLight = new THREE.DirectionalLight('#b0e9ff', 1.8)
      keyLight.position.set(3.2, 2.8, 4.8)
      scene.add(keyLight)

      const fillLight = new THREE.PointLight('#59e0c9', 1.35, 16, 2)
      fillLight.position.set(-2.3, -1.4, 4.4)
      scene.add(fillLight)

      const rimLight = new THREE.PointLight('#5aa9ff', 1.55, 18, 2)
      rimLight.position.set(2.6, 0.8, 3.6)
      scene.add(rimLight)

      const baseRing = new THREE.Mesh(
        new THREE.RingGeometry(1.44, 1.7, 96),
        new THREE.MeshBasicMaterial({
          color: '#76dcff',
          transparent: true,
          opacity: 0.2,
          side: THREE.DoubleSide
        })
      )
      baseRing.rotation.x = -Math.PI / 2
      baseRing.position.y = -1.5
      scene.add(baseRing)

      rootGroup = new THREE.Group()
      scene.add(rootGroup)

      return { GLTFLoader }
    }

    const loadModel = async (GLTFLoader) => {
      const loader = new GLTFLoader()
      const gltf = await loader.loadAsync(props.modelUrl)
      const object = gltf.scene || gltf.scenes?.[0]
      if (!object) {
        throw new Error('No root scene found in GLB.')
      }

      const meshes = []
      object.traverse((child) => {
        if (child.isMesh) {
          meshes.push(child)
        }
      })

      meshes.forEach((child, index) => {
        applySceneMaterial(child, index)
      })

      if (!meshes.length) {
        throw new Error('No mesh found in GLB.')
      }

      return object
    }

    const initScene = async () => {
      try {
        const modules = await initRenderer()
        if (!modules) {
          return
        }

        try {
          const model = await loadModel(modules.GLTFLoader)
          if (!disposed) {
            mountObject(model)
          }
        } catch (error) {
          console.error('[MedicalHeartScene] Failed to load GLB model:', error)
          hasError.value = true
          if (!disposed) {
            mountObject(createFallbackObject())
          }
        }
      } catch (error) {
        console.error('[MedicalHeartScene] Failed to initialize scene:', error)
        hasError.value = true
      }
    }

    const handlePointerMove = (event) => {
      if (!viewportRef.value) {
        return
      }

      const rect = viewportRef.value.getBoundingClientRect()
      pointer.x = ((event.clientX - rect.left) / rect.width - 0.5) * 2
      pointer.y = ((event.clientY - rect.top) / rect.height - 0.5) * 2
    }

    const handlePointerLeave = () => {
      pointer.x = 0
      pointer.y = 0
    }

    onMounted(() => {
      initScene()

      if (viewportRef.value) {
        viewportRef.value.addEventListener('pointermove', handlePointerMove)
        viewportRef.value.addEventListener('pointerleave', handlePointerLeave)
      }

      resizeObserver = new ResizeObserver(() => {
        updateSize()
      })

      if (viewportRef.value) {
        resizeObserver.observe(viewportRef.value)
      }
    })

    onBeforeUnmount(() => {
      disposed = true

      if (frameId) {
        cancelAnimationFrame(frameId)
      }

      if (viewportRef.value) {
        viewportRef.value.removeEventListener('pointermove', handlePointerMove)
        viewportRef.value.removeEventListener('pointerleave', handlePointerLeave)
      }

      resizeObserver?.disconnect?.()
      disposeObject(currentObject)

      if (renderer) {
        renderer.dispose()
        renderer.forceContextLoss?.()
        if (renderer.domElement?.parentNode) {
          renderer.domElement.parentNode.removeChild(renderer.domElement)
        }
      }
    })

    return {
      viewportRef,
      isReady,
      hasError
    }
  }
}
</script>

<style scoped>
.heart-scene {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100%;
  overflow: hidden;
}

.heart-scene__viewport,
.heart-scene__viewport :deep(canvas) {
  width: 100%;
  height: 100%;
  display: block;
}

.heart-scene__viewport {
  position: relative;
  z-index: 2;
}

.heart-scene__placeholder,
.heart-scene__glow,
.heart-scene__scan,
.heart-scene__grain {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.heart-scene__placeholder {
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-core,
.placeholder-ring {
  position: absolute;
  border-radius: 999px;
}

.placeholder-core {
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(124, 220, 255, 0.68), rgba(25, 108, 199, 0.18) 62%, transparent 76%);
  filter: blur(6px);
}

.placeholder-ring {
  border: 1px solid rgba(125, 225, 255, 0.22);
}

.placeholder-ring--one {
  width: 286px;
  height: 116px;
  transform: rotateX(72deg);
}

.placeholder-ring--two {
  width: 216px;
  height: 86px;
  transform: rotateX(72deg) scale(1.1);
  border-color: rgba(82, 235, 205, 0.28);
}

.heart-scene__glow {
  z-index: 0;
  border-radius: 999px;
  filter: blur(26px);
  opacity: 0.9;
}

.heart-scene__glow--blue {
  background: radial-gradient(circle at 48% 56%, rgba(92, 207, 255, 0.3), transparent 46%);
}

.heart-scene__glow--teal {
  background: radial-gradient(circle at 56% 38%, rgba(72, 228, 200, 0.22), transparent 40%);
}

.heart-scene__scan {
  z-index: 3;
  background:
    linear-gradient(180deg, transparent 0%, rgba(125, 225, 255, 0.04) 42%, rgba(72, 228, 200, 0.05) 58%, transparent 100%);
  mix-blend-mode: screen;
}

.heart-scene__scan::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 224, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 224, 255, 0.04) 1px, transparent 1px);
  background-size: 100% 5px, 36px 100%;
  opacity: 0.3;
}

.heart-scene__grain {
  z-index: 4;
  background-image:
    radial-gradient(circle at 18% 28%, rgba(255, 255, 255, 0.24) 0 2px, transparent 2.6px),
    radial-gradient(circle at 78% 65%, rgba(193, 237, 255, 0.18) 0 2px, transparent 2.8px),
    radial-gradient(circle at 64% 16%, rgba(95, 239, 215, 0.16) 0 1.6px, transparent 2.4px);
  opacity: 0.35;
}
</style>

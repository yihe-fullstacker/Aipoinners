import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const modelPath = path.join(projectRoot, 'public', 'models', '15.glb')
const JSON_CHUNK_TYPE = 0x4e4f534a
const BIN_CHUNK_TYPE = 0x004e4942
const PAGE_ASSET_LIMIT = 25 * 1024 * 1024

function readGlb(filePath) {
  const glb = fs.readFileSync(filePath)
  if (glb.readUInt32LE(0) !== 0x46546c67 || glb.readUInt32LE(4) !== 2) {
    throw new Error('Unsupported GLB header.')
  }

  let json = null
  let bin = null
  let offset = 12

  while (offset < glb.length) {
    const length = glb.readUInt32LE(offset)
    const type = glb.readUInt32LE(offset + 4)
    const data = glb.subarray(offset + 8, offset + 8 + length)

    if (type === JSON_CHUNK_TYPE) {
      json = JSON.parse(data.toString('utf8').replace(/\0+$/g, '').trim())
    } else if (type === BIN_CHUNK_TYPE) {
      bin = Buffer.from(data)
    }

    offset += 8 + length
  }

  if (!json || !bin) {
    throw new Error('The GLB must contain both JSON and BIN chunks.')
  }

  return { glb, json, bin }
}

function accessorArray(gltf, accessorIndex) {
  const accessor = gltf.json.accessors[accessorIndex]
  const bufferView = gltf.json.bufferViews[accessor.bufferView]
  const componentCount = {
    SCALAR: 1,
    VEC2: 2,
    VEC3: 3,
    VEC4: 4
  }[accessor.type]
  const ComponentArray = {
    5120: Int8Array,
    5121: Uint8Array,
    5122: Int16Array,
    5123: Uint16Array,
    5125: Uint32Array,
    5126: Float32Array
  }[accessor.componentType]

  if (!componentCount || !ComponentArray) {
    throw new Error(`Unsupported accessor ${accessorIndex}.`)
  }

  const byteOffset =
    gltf.bin.byteOffset +
    (bufferView.byteOffset || 0) +
    (accessor.byteOffset || 0)
  const length = accessor.count * componentCount
  return new ComponentArray(gltf.bin.buffer, byteOffset, length)
}

function align4(length) {
  return Math.ceil(length / 4) * 4
}

function optimizeModel(gltf) {
  const positions = accessorArray(gltf, 0)
  const normals = accessorArray(gltf, 1)
  const indices = accessorArray(gltf, 3)
  const primitive = gltf.json.meshes[0].primitives[0]

  if (!positions.length || !normals.length || indices.length % 3 !== 0) {
    throw new Error('Unexpected heart mesh attributes.')
  }

  let maxAbsPosition = 0
  for (const value of positions) {
    maxAbsPosition = Math.max(maxAbsPosition, Math.abs(value))
  }
  if (!Number.isFinite(maxAbsPosition) || maxAbsPosition === 0) {
    throw new Error('The model has invalid position bounds.')
  }

  const quantizedPositions = new Int16Array(positions.length)
  const quantizedPositionMin = [Infinity, Infinity, Infinity]
  const quantizedPositionMax = [-Infinity, -Infinity, -Infinity]
  for (let index = 0; index < positions.length; index += 1) {
    const quantized = Math.max(
      -32767,
      Math.min(32767, Math.round((positions[index] / maxAbsPosition) * 32767))
    )
    quantizedPositions[index] = quantized
    const axis = index % 3
    quantizedPositionMin[axis] = Math.min(quantizedPositionMin[axis], quantized)
    quantizedPositionMax[axis] = Math.max(quantizedPositionMax[axis], quantized)
  }

  const quantizedNormals = new Int8Array(normals.length)
  for (let index = 0; index < normals.length; index += 1) {
    quantizedNormals[index] = Math.max(
      -127,
      Math.min(127, Math.round(normals[index] * 127))
    )
  }

  const positionBytes = Buffer.from(
    quantizedPositions.buffer,
    quantizedPositions.byteOffset,
    quantizedPositions.byteLength
  )
  const normalBytes = Buffer.from(
    quantizedNormals.buffer,
    quantizedNormals.byteOffset,
    quantizedNormals.byteLength
  )
  const indexBytes = Buffer.from(
    indices.buffer,
    indices.byteOffset,
    indices.byteLength
  )

  const positionOffset = 0
  const normalOffset = align4(positionOffset + positionBytes.length)
  const indexOffset = align4(normalOffset + normalBytes.length)
  const binaryLength = align4(indexOffset + indexBytes.length)
  const binary = Buffer.alloc(binaryLength)
  positionBytes.copy(binary, positionOffset)
  normalBytes.copy(binary, normalOffset)
  indexBytes.copy(binary, indexOffset)

  const nodes = structuredClone(gltf.json.nodes)
  const node = nodes[0]
  node.matrix = node.matrix.map((value, index) =>
    index < 12 ? value * maxAbsPosition : value
  )

  const positionAccessor = {
    ...gltf.json.accessors[0],
    componentType: 5122,
    normalized: true,
    min: quantizedPositionMin,
    max: quantizedPositionMax,
    bufferView: 0,
    byteOffset: 0
  }
  const normalAccessor = {
    ...gltf.json.accessors[1],
    componentType: 5120,
    normalized: true,
    min: [-127, -127, -127],
    max: [127, 127, 127],
    bufferView: 1,
    byteOffset: 0
  }
  const indexAccessor = {
    ...gltf.json.accessors[3],
    bufferView: 2,
    byteOffset: 0
  }

  return {
    binary,
    maxAbsPosition,
    json: {
      asset: {
        version: '2.0',
        generator: 'AIPioneers heart model optimizer v2'
      },
      extensionsUsed: ['KHR_mesh_quantization'],
      extensionsRequired: ['KHR_mesh_quantization'],
      scene: gltf.json.scene ?? 0,
      scenes: gltf.json.scenes || [{ nodes: [0] }],
      nodes,
      meshes: [
        {
          primitives: [
            {
              attributes: {
                POSITION: 0,
                NORMAL: 1
              },
              indices: 2,
              material: primitive.material ?? 0,
              mode: primitive.mode ?? 4
            }
          ]
        }
      ],
      materials: [
        {
          name: 'heart_geometry',
          pbrMetallicRoughness: {
            baseColorFactor: [0.53, 0.86, 1, 1],
            metallicFactor: 0.08,
            roughnessFactor: 0.3
          }
        }
      ],
      accessors: [positionAccessor, normalAccessor, indexAccessor],
      bufferViews: [
        {
          buffer: 0,
          byteOffset: positionOffset,
          byteLength: positionBytes.length,
          target: 34962
        },
        {
          buffer: 0,
          byteOffset: normalOffset,
          byteLength: normalBytes.length,
          target: 34962
        },
        {
          buffer: 0,
          byteOffset: indexOffset,
          byteLength: indexBytes.length,
          target: 34963
        }
      ],
      buffers: [
        {
          byteLength: binaryLength
        }
      ]
    }
  }
}

function pad(buffer, byte, alignment = 4) {
  const remainder = buffer.length % alignment
  if (!remainder) {
    return buffer
  }
  return Buffer.concat([buffer, Buffer.alloc(alignment - remainder, byte)])
}

function writeGlb(json, binary) {
  const jsonChunk = pad(Buffer.from(JSON.stringify(json), 'utf8'), 0x20)
  const binChunk = pad(binary, 0)
  const totalLength = 12 + 8 + jsonChunk.length + 8 + binChunk.length
  const output = Buffer.alloc(totalLength)

  output.writeUInt32LE(0x46546c67, 0)
  output.writeUInt32LE(2, 4)
  output.writeUInt32LE(totalLength, 8)
  output.writeUInt32LE(jsonChunk.length, 12)
  output.writeUInt32LE(JSON_CHUNK_TYPE, 16)
  jsonChunk.copy(output, 20)
  output.writeUInt32LE(binChunk.length, 20 + jsonChunk.length)
  output.writeUInt32LE(BIN_CHUNK_TYPE, 24 + jsonChunk.length)
  binChunk.copy(output, 28 + jsonChunk.length)

  return output
}

const gltf = readGlb(modelPath)
const isOptimized = (gltf.json.extensionsUsed || []).includes('KHR_mesh_quantization')
const isCurrentVersion = gltf.json.asset?.generator?.endsWith('v2')

if (isOptimized && isCurrentVersion) {
  console.log(`${path.relative(projectRoot, modelPath)} is already optimized.`)
  process.exit(0)
}

let json
let binary
if (isOptimized) {
  const positions = accessorArray(gltf, 0)
  const normals = accessorArray(gltf, 1)
  const positionMin = [Infinity, Infinity, Infinity]
  const positionMax = [-Infinity, -Infinity, -Infinity]
  const normalMin = [Infinity, Infinity, Infinity]
  const normalMax = [-Infinity, -Infinity, -Infinity]

  for (let index = 0; index < positions.length; index += 1) {
    const axis = index % 3
    positionMin[axis] = Math.min(positionMin[axis], positions[index])
    positionMax[axis] = Math.max(positionMax[axis], positions[index])
    normalMin[axis] = Math.min(normalMin[axis], normals[index])
    normalMax[axis] = Math.max(normalMax[axis], normals[index])
  }

  json = structuredClone(gltf.json)
  json.asset.generator = 'AIPioneers heart model optimizer v2'
  json.accessors[0].min = positionMin
  json.accessors[0].max = positionMax
  json.accessors[1].min = normalMin
  json.accessors[1].max = normalMax
  binary = gltf.bin
} else {
  const optimized = optimizeModel(gltf)
  json = optimized.json
  binary = optimized.binary
}

const output = writeGlb(json, binary)
if (output.length >= PAGE_ASSET_LIMIT) {
  throw new Error(
    `Optimized model is still too large: ${(output.length / 1024 / 1024).toFixed(2)} MiB.`
  )
}

const temporaryPath = `${modelPath}.tmp`
fs.writeFileSync(temporaryPath, output)
fs.renameSync(temporaryPath, modelPath)

console.log(
  `Optimized ${path.relative(projectRoot, modelPath)} from ` +
    `${(gltf.glb.length / 1024 / 1024).toFixed(2)} MiB to ` +
    `${(output.length / 1024 / 1024).toFixed(2)} MiB.`
)

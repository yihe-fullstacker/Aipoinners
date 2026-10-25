import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const outputDirectory = path.join(projectRoot, 'dist')
const pageAssetLimit = 25 * 1024 * 1024

function collectFiles(directory) {
  const files = []
  for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
    const entryPath = path.join(directory, entry.name)
    if (entry.isDirectory()) {
      files.push(...collectFiles(entryPath))
    } else {
      files.push(entryPath)
    }
  }
  return files
}

if (!fs.existsSync(path.join(outputDirectory, 'index.html'))) {
  throw new Error('Build output is missing dist/index.html.')
}

const files = collectFiles(outputDirectory)
const oversized = files
  .map((filePath) => ({
    filePath,
    size: fs.statSync(filePath).size
  }))
  .filter(({ size }) => size >= pageAssetLimit)

if (oversized.length) {
  for (const { filePath, size } of oversized) {
    console.error(
      `${path.relative(projectRoot, filePath)} is ${(size / 1024 / 1024).toFixed(2)} MiB`
    )
  }
  throw new Error('Cloudflare Pages only supports files smaller than 25 MiB.')
}

const largest = files
  .map((filePath) => ({
    filePath,
    size: fs.statSync(filePath).size
  }))
  .sort((left, right) => right.size - left.size)[0]

console.log(
  `Cloudflare Pages asset check passed: ${files.length} files, largest is ` +
    `${path.relative(projectRoot, largest.filePath)} at ` +
    `${(largest.size / 1024 / 1024).toFixed(2)} MiB.`
)

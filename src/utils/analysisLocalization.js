const EXACT_TEXT_MAP = new Map([
  ['Benign lesion possibility', '良性病变可能性'],
  ['Inflammatory change possibility', '炎性改变可能性'],
  ['Malignant lesion possibility', '恶性病变可能性'],
  ['Pulmonary nodule', '肺结节'],
  ['Calcified focus', '钙化灶'],
  ['calcified focus', '钙化灶'],
  ['Punctate', '点状'],
  ['Right upper lobe', '右上肺叶'],
  ['Right middle lobe', '右中肺叶'],
  ['Right lower lobe', '右下肺叶'],
  ['Left upper lobe', '左上肺叶'],
  ['Left lower lobe', '左下肺叶'],
  ['Appearance suggests a lower-risk lesion.', '影像表现更倾向于低风险病变。'],
  ['Inflammation remains part of the differential.', '炎性改变仍需纳入鉴别诊断范围。'],
  ['Needs clinical correlation and follow-up.', '建议结合临床表现并进行后续随访。'],
  ['Follow up with a clinician for full interpretation.', '建议结合临床医生进一步解读。'],
  ['Correlate this image with symptoms and prior studies.', '建议结合症状表现和既往检查结果综合判断。'],
  ['Correlate with symptoms, smoking history, and prior studies.', '建议结合症状、吸烟史和既往检查结果综合判断。'],
  ['Repeat CT review in 3 to 6 months.', '建议在 3 到 6 个月内复查 CT。'],
  ['Consider PET-CT if clinical risk remains high.', '如临床风险仍较高，可考虑 PET-CT。'],
  ['Arrange specialist follow-up when symptoms persist.', '如症状持续，建议安排专科随访。'],
  ['No clear abnormality was detected in the current AI review.', '本次 AI 复核未发现明确异常征象。'],
  ['Overall confidence', '总体置信度'],
  ['High confidence', '高置信度'],
  ['Needs review', '建议复核'],
  ['Use with care', '谨慎参考'],
  ['Pending finding', '待补充发现'],
  ['Boundary clarity', '边界清晰度'],
  ['Density variation', '密度变化'],
  ['Boundary contour remains relatively clear.', '病灶边界轮廓整体较为清晰。'],
  ['Local density differs from surrounding tissue.', '局部密度与周围组织存在差异。'],
  ['Lesion', '病灶'],
  ['Finding', '异常征象'],
  ['Boundary', '边界'],
  ['Density', '密度'],
  ['Medium', '中等'],
  ['Region 1', '区域 1'],
  ['Region 2', '区域 2'],
  ['Region 3', '区域 3'],
  ['Region 4', '区域 4']
])

const REPLACEMENT_RULES = [
  [/Feature summary for\s+/gi, '影像特征摘要：'],
  [/Overall confidence:\s*/gi, '总体置信度：'],
  [/Detected\s+(\d+)\s+focus area\(s\):/gi, '检测到 $1 处重点关注区域：'],
  [/requires review/gi, '需要重点复核'],
  [/\bPulmonary nodule\b/gi, '肺结节'],
  [/\bcalcified focus\b/gi, '钙化灶'],
  [/\bpunctate\b/gi, '点状'],
  [/\bprofile\b/gi, '特征'],
  [/\bhigh-risk\b/gi, '高风险'],
  [/\blower-risk\b/gi, '低风险'],
  [/\bdifferential\b/gi, '鉴别诊断'],
  [/\bcalcified\b/gi, '钙化'],
  [/\bcalcification\b/gi, '钙化灶'],
  [/\bnodule\b/gi, '结节'],
  [/\blesion\b/gi, '病变'],
  [/\bmass\b/gi, '肿块'],
  [/\bopacity\b/gi, '密度影'],
  [/\binfiltrate\b/gi, '浸润影'],
  [/\bconsolidation\b/gi, '实变影'],
  [/\beffusion\b/gi, '积液'],
  [/\binflammatory change\b/gi, '炎性改变'],
  [/\binflammation\b/gi, '炎症'],
  [/\bmalignant\b/gi, '恶性'],
  [/\bbenign\b/gi, '良性'],
  [/\bboundary\b/gi, '边界'],
  [/\bdensity\b/gi, '密度'],
  [/\btexture\b/gi, '纹理'],
  [/\bshape\b/gi, '形态'],
  [/\bthe detected region\b/gi, '检出区域'],
  [/Location:\s*/gi, '位置：'],
  [/\s+at\s+/gi, ' 位于 '],
  [/\bleft upper lobe\b/gi, '左上肺叶'],
  [/\bleft lower lobe\b/gi, '左下肺叶'],
  [/\bright upper lobe\b/gi, '右上肺叶'],
  [/\bright middle lobe\b/gi, '右中肺叶'],
  [/\bright lower lobe\b/gi, '右下肺叶'],
  [/\bleft lung\b/gi, '左肺'],
  [/\bright lung\b/gi, '右肺'],
  [/\bupper lobe\b/gi, '上肺叶'],
  [/\blower lobe\b/gi, '下肺叶'],
  [/\bleft\b/gi, '左侧'],
  [/\bright\b/gi, '右侧']
]

const hasChinese = (value = '') => /[\u3400-\u9fff]/.test(value)

const normalizeText = (value) => String(value || '').replace(/\s+/g, ' ').trim()

const replaceExact = (value) => {
  const normalized = normalizeText(value)
  return EXACT_TEXT_MAP.get(normalized) || normalized
}

const localizeText = (value) => {
  if (value == null) return value

  let next = replaceExact(value)
  REPLACEMENT_RULES.forEach(([pattern, replacement]) => {
    next = next.replace(pattern, replacement)
  })

  next = next
    .replace(/:\s*\./g, '。')
    .replace(/\s+\./g, '。')
    .replace(/\.\s*$/g, '。')
    .replace(/\s+,/g, '，')
    .replace(/,\s*/g, '，')
    .replace(/;\s*/g, '；')
    .replace(/\(\s*/g, '（')
    .replace(/\s*\)/g, '）')
    .replace(/\s{2,}/g, ' ')
    .trim()

  if (!hasChinese(next) && next === normalizeText(value)) {
    return next
  }

  return next
}

const localizeFeatureName = (value) => {
  const normalized = normalizeText(value)
  const matchedProfile = normalized.match(/^(.*)\s+profile$/i)
  if (matchedProfile) {
    return `${localizeText(matchedProfile[1])}特征`
  }
  return localizeText(normalized)
}

const localizeList = (list) => {
  if (!Array.isArray(list)) return list
  return list.map((item) => (typeof item === 'string' ? localizeText(item) : item))
}

const localizeFindings = (findings) => {
  if (!Array.isArray(findings)) return findings
  return findings.map((finding) => ({
    ...finding,
    type: localizeText(finding.type),
    location: localizeText(finding.location),
    size: localizeText(finding.size),
    description: localizeText(finding.description),
    severity: localizeText(finding.severity),
    characteristics: localizeList(finding.characteristics)
  }))
}

const localizeFeatures = (features) => {
  if (!Array.isArray(features)) return features
  return features.map((feature) => ({
    ...feature,
    type: localizeText(feature.type),
    name: localizeFeatureName(feature.name),
    description: localizeText(feature.description)
  }))
}

const localizeDifferentialDiagnosis = (diagnoses) => {
  if (!Array.isArray(diagnoses)) return diagnoses
  return diagnoses.map((diagnosis) => ({
    ...diagnosis,
    name: localizeText(diagnosis.name),
    description: localizeText(diagnosis.description),
    supportingFeatures: localizeList(diagnosis.supportingFeatures),
    contradictingFeatures: localizeList(diagnosis.contradictingFeatures)
  }))
}

const localizeAttentionRegions = (regions) => {
  if (!Array.isArray(regions)) return regions
  return regions.map((region) => ({
    ...region,
    type: localizeText(region.type),
    location: localizeText(region.location),
    size: localizeText(region.size),
    description: localizeText(region.description)
  }))
}

export const localizeAnalysisPayload = (payload) => {
  if (!payload || typeof payload !== 'object') return payload

  return {
    ...payload,
    summary: localizeText(payload.summary),
    conclusion: localizeText(payload.conclusion),
    impression: localizeText(payload.impression),
    analysisSummary: localizeText(payload.analysisSummary),
    reportSummary: localizeText(payload.reportSummary),
    diagnosis: localizeText(payload.diagnosis),
    resultText: localizeText(payload.resultText),
    featureDescription: localizeText(payload.featureDescription),
    heatmapDescription: localizeText(payload.heatmapDescription),
    findings: localizeFindings(payload.findings),
    recommendations: localizeList(payload.recommendations),
    attentionRegions: localizeAttentionRegions(payload.attentionRegions),
    features: localizeFeatures(payload.features),
    differentialDiagnosis: localizeDifferentialDiagnosis(payload.differentialDiagnosis)
  }
}

export { localizeText }

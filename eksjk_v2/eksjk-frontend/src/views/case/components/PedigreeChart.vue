<template>
  <div class="pedigree-chart" ref="chartRef">
    <div v-if="!hasData" class="pedigree-empty">
      <el-empty description="暂无家族成员数据，请在表格视图中添加" :image-size="80" />
    </div>
    <svg
      v-else
      :width="svgWidth"
      :height="svgHeight"
      :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
      class="pedigree-svg"
    >
      <!-- 代际标签 -->
      <g class="generation-labels">
        <text
          v-for="gen in layout.generations"
          :key="gen.label"
          :x="30"
          :y="gen.y + 15"
          class="gen-label"
        >{{ gen.label }}</text>
      </g>

      <!-- 连接线层 -->
      <g class="pedigree-lines">
        <line
          v-for="(line, i) in layout.lines"
          :key="'line-' + i"
          :x1="line.x1" :y1="line.y1"
          :x2="line.x2" :y2="line.y2"
          :class="'pedigree-line pedigree-line--' + line.type"
        />
      </g>

      <!-- 节点层 -->
      <g class="pedigree-nodes">
        <!-- 男性节点（方框） -->
        <g v-for="node in layout.maleNodes" :key="node.id" class="pedigree-node" :class="{ 'is-linked': node.linked }">
          <rect
            :x="node.x - NODE_W / 2"
            :y="node.y - NODE_H / 2"
            :width="NODE_W"
            :height="NODE_H"
            :class="nodeShapeClass(node)"
            @click="handleNodeClick(node)"
          />
          <!-- 先证者箭头 -->
          <path
            v-if="node.isProband"
            :d="probandArrowPath(node)"
            class="proband-arrow"
          />
          <!-- 已故斜线 -->
          <line
            v-if="node.isDeceased"
            :x1="node.x - NODE_W / 2" :y1="node.y - NODE_H / 2"
            :x2="node.x + NODE_W / 2" :y2="node.y + NODE_H / 2"
            class="deceased-line"
          />
          <text :x="node.x" :y="node.y + 5" class="node-text" text-anchor="middle">{{ node.shortLabel }}</text>
        </g>

        <!-- 女性节点（圆圈） -->
        <g v-for="node in layout.femaleNodes" :key="node.id" class="pedigree-node" :class="{ 'is-linked': node.linked }">
          <circle
            :cx="node.x"
            :cy="node.y"
            :r="NODE_R"
            :class="nodeShapeClass(node)"
            @click="handleNodeClick(node)"
          />
          <!-- 先证者箭头 -->
          <path
            v-if="node.isProband"
            :d="probandArrowCirclePath(node)"
            class="proband-arrow"
          />
          <!-- 已故斜线 -->
          <line
            v-if="node.isDeceased"
            :x1="node.x - NODE_R" :y1="node.y - NODE_R"
            :x2="node.x + NODE_R" :y2="node.y + NODE_R"
            class="deceased-line"
          />
          <text :x="node.x" :y="node.y + 5" class="node-text" text-anchor="middle">{{ node.shortLabel }}</text>
        </g>

        <!-- 性别未知节点（菱形） -->
        <g v-for="node in layout.unknownNodes" :key="node.id" class="pedigree-node" :class="{ 'is-linked': node.linked }">
          <polygon
            :points="diamondPoints(node)"
            :class="nodeShapeClass(node)"
            @click="handleNodeClick(node)"
          />
          <line
            v-if="node.isDeceased"
            :x1="node.x - NODE_W / 2" :y1="node.y"
            :x2="node.x + NODE_W / 2" :y2="node.y"
            class="deceased-line"
          />
        </g>
      </g>

      <!-- 标签层 -->
      <g class="pedigree-labels">
        <text
          v-for="node in layout.allLabelNodes"
          :key="'label-' + node.id"
          :x="node.x"
          :y="node.labelY"
          class="node-label"
          text-anchor="middle"
        >{{ node.displayLabel }}</text>
        <text
          v-for="node in layout.allLabelNodes.filter(n => n.subLabel)"
          :key="'sublabel-' + node.id"
          :x="node.x"
          :y="node.labelY + 16"
          class="node-sublabel"
          text-anchor="middle"
        >{{ node.subLabel }}</text>
      </g>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// ==================== 常量 ====================
const NODE_W = 44
const NODE_H = 36
const NODE_R = 18
const GEN_GAP = 130
const SIB_GAP = 80
const COUPLE_GAP = 30
const GEN_LABEL_X = 30
const NODE_START_X = 80

const props = defineProps({
  familyMembers: { type: Array, default: () => [] },
  patientSex: { type: String, default: '1' },
  patientId: { type: String, default: '' },
  patientName: { type: String, default: '' },
  patientBirthYear: { type: String, default: '' },
  patientAge: { type: String, default: '' },
  patientHeight: { type: String, default: '' },
  patientDisease: { type: String, default: '' },
  patientDisClass: { type: String, default: '' }
})

const emit = defineEmits(['node-click'])

// ==================== 数据转换 ====================
function buildPedigreeData() {
  const nodes = []

  // 先证者节点
  const proband = {
    id: 'proband',
    sex: props.patientSex,
    isAffected: true,
    isProband: true,
    isDeceased: false,
    generation: 0,
    shortLabel: '先证者',
    displayLabel: props.patientName || '患者',
    subLabel: buildSubLabel(props.patientAge, props.patientHeight),
    linkedPatientId: null,
    linked: false,
    spouseOf: null,
    childOf: null
  }
  nodes.push(proband)

  // 家族成员节点
  props.familyMembers.forEach((m, idx) => {
    const gen = m.generation ?? inferGeneration(m.relationship)
    const node = {
      id: 'member-' + idx,
      sex: m.sex || '0',
      isAffected: m.isAffected === '1',
      isProband: false,
      isDeceased: m.isDeceased === '1',
      generation: gen,
      shortLabel: m.relationship || '亲属',
      displayLabel: m.relationship || '亲属',
      subLabel: buildSubLabel(m.birthYear ? (new Date().getFullYear() - parseInt(m.birthYear)) + '岁' : '', m.height),
      linkedPatientId: m.linkedPatientId || null,
      linked: !!(m.linkedPatientId || m.linkedMedrecNum),
      linkedMedrecNum: m.linkedMedrecNum || '',
      linkedDiseaseType: m.linkedDiseaseType || '',
      spouseOf: null,
      childOf: null,
      rawData: m
    }
    nodes.push(node)
  })

  // 建立配偶关系
  establishCouples(nodes)

  return nodes
}

function inferGeneration(relationship) {
  const gen2Relations = ['祖父', '祖母', '外祖父', '外祖母']
  const gen1Relations = ['父亲', '母亲']
  const gen0Relations = ['兄', '弟', '姐', '妹']
  if (gen2Relations.includes(relationship)) return -2
  if (gen1Relations.includes(relationship)) return -1
  if (gen0Relations.includes(relationship)) return 0
  return 0
}

function establishCouples(nodes) {
  // 匹配配偶：父亲↔母亲, 祖父↔祖母, 外祖父↔外祖母
  const couplePairs = [
    ['父亲', '母亲'],
    ['祖父', '祖母'],
    ['外祖父', '外祖母']
  ]
  couplePairs.forEach(([r1, r2]) => {
    const n1 = nodes.find(n => n.shortLabel === r1)
    const n2 = nodes.find(n => n.shortLabel === r2)
    if (n1 && n2) {
      n1.spouseOf = n2.id
      n2.spouseOf = n1.id
    }
  })

  // 如果先证者有配偶（通过"其他"关系且同代），尝试匹配
  const proband = nodes.find(n => n.isProband)
  const gen0Others = nodes.filter(n => n.generation === 0 && !n.spouseOf && !n.isProband && n.shortLabel === '其他')
  if (proband && gen0Others.length === 1 && proband.sex !== gen0Others[0].sex) {
    proband.spouseOf = gen0Others[0].id
    gen0Others[0].spouseOf = proband.id
  }
}

function buildSubLabel(age, height) {
  const parts = []
  if (age) parts.push(age)
  if (height) parts.push(height + 'cm')
  return parts.join(' ')
}

// ==================== 布局计算 ====================
function computeLayout(nodes) {
  if (nodes.length === 0) return { generations: [], lines: [], maleNodes: [], femaleNodes: [], unknownNodes: [], allLabelNodes: [], width: 400, height: 200 }

  // 1. 按代分组
  const genMap = new Map()
  nodes.forEach(n => {
    if (!genMap.has(n.generation)) genMap.set(n.generation, [])
    genMap.get(n.generation).push(n)
  })

  const genKeys = Array.from(genMap.keys()).sort((a, b) => a - b)

  // 2. 每代内排序（couples first, then singles; left to right by age）
  const allCouples = [] // { couple: [n1,n2], children: [childNodes] in next gen }
  genKeys.forEach(gen => {
    const genNodes = genMap.get(gen)
    // 找出配偶对
    const paired = new Set()
    genNodes.forEach(n => {
      if (paired.has(n.id)) return
      if (n.spouseOf) {
        const spouse = nodes.find(x => x.id === n.spouseOf)
        if (spouse && genNodes.includes(spouse) && !paired.has(spouse.id)) {
          paired.add(n.id)
          paired.add(spouse.id)
          allCouples.push({ couple: [n, spouse], generation: gen, children: [] })
        }
      }
    })
  })

  // 为每对配偶找子女
  allCouples.forEach(c => {
    const childGen = c.generation + 1
    const childNodes = nodes.filter(n => n.generation === childGen && !hasCoupleInGen(n, childGen, allCouples))
    // 简化：同代中不在其他配偶对中的节点即为可能的子女
    const genCoupleIds = new Set()
    allCouples.filter(x => x.generation === childGen).forEach(x => {
      x.couple.forEach(n => genCoupleIds.add(n.id))
    })
    c.children = nodes.filter(n => n.generation === childGen && !genCoupleIds.has(n.id))
  })

  // 3. 计算每代的位置
  const minGen = Math.min(...genKeys)
  const maxGen = Math.max(...genKeys)

  // 计算每代需要的宽度
  const genWidths = new Map()
  genKeys.forEach(gen => {
    const genNodes = genMap.get(gen)
    const couplesInGen = allCouples.filter(c => c.generation === gen)
    const pairedIds = new Set()
    couplesInGen.forEach(c => c.couple.forEach(n => pairedIds.add(n.id)))
    const singles = genNodes.filter(n => !pairedIds.has(n.id))

    let width = 0
    couplesInGen.forEach(c => width += SIB_GAP + COUPLE_GAP)
    singles.forEach(() => width += SIB_GAP)
    genWidths.set(gen, Math.max(width, SIB_GAP * 2))
  })

  const maxGenWidth = Math.max(...Array.from(genWidths.values()), SIB_GAP * 3)

  // 4. 分配坐标
  const svgWidth = Math.max(maxGenWidth + NODE_START_X + 100, 500)
  const svgHeight = (maxGen - minGen + 1) * GEN_GAP + 120
  const centerX = svgWidth / 2

  // 为每代布局节点
  genKeys.forEach(gen => {
    const genNodes = genMap.get(gen)
    const couplesInGen = allCouples.filter(c => c.generation === gen)
    const pairedIds = new Set()
    couplesInGen.forEach(c => c.couple.forEach(n => pairedIds.add(n.id)))
    const singles = genNodes.filter(n => !pairedIds.has(n.id))
    const y = (gen - minGen) * GEN_GAP + 100

    // 计算本代起始 X
    const totalItems = couplesInGen.length + singles.length
    const totalSpan = totalItems * SIB_GAP
    let cursorX = centerX - totalSpan / 2 + SIB_GAP / 2

    // 布局配偶对
    couplesInGen.forEach(c => {
      const [n1, n2] = c.couple
      const coupleCenterX = cursorX + COUPLE_GAP / 2
      n1.x = cursorX
      n1.y = y
      n2.x = cursorX + COUPLE_GAP
      n2.y = y
      cursorX += SIB_GAP
    })

    // 布局单身节点
    singles.forEach(n => {
      n.x = cursorX
      n.y = y
      cursorX += SIB_GAP
    })
  })

  // 5. 生成连线
  const lines = []
  allCouples.forEach(c => {
    if (c.couple.length !== 2) return
    const [n1, n2] = c.couple
    // 婚姻线
    lines.push({ type: 'marriage', x1: n1.x, y1: n1.y, x2: n2.x, y2: n2.y })

    if (c.children.length > 0) {
      const coupleMidX = (n1.x + n2.x) / 2
      const coupleY = n1.y
      const childrenY = c.children[0].y
      const siblingBarY = coupleY + (childrenY - coupleY) / 2

      // 垂线从配偶中间向下到 sibling bar
      lines.push({ type: 'descent', x1: coupleMidX, y1: coupleY, x2: coupleMidX, y2: siblingBarY })

      // Sibling 横线
      const childrenXs = c.children.map(ch => ch.x).sort((a, b) => a - b)
      if (childrenXs.length > 0) {
        const sibLeft = childrenXs[0]
        const sibRight = childrenXs[childrenXs.length - 1]
        lines.push({ type: 'sibship', x1: sibLeft, y1: siblingBarY, x2: sibRight, y2: siblingBarY })

        // 每个子女的垂线
        c.children.forEach(ch => {
          lines.push({ type: 'drop', x1: ch.x, y1: siblingBarY, x2: ch.x, y2: ch.y })
        })
      }
    }
  })

  // 为无配偶的父母节点画到子女的连接（单亲场景）
  genKeys.forEach(gen => {
    const genNodes = genMap.get(gen)
    const couplesInGen = allCouples.filter(c => c.generation === gen)
    const pairedIds = new Set()
    couplesInGen.forEach(c => c.couple.forEach(n => pairedIds.add(n.id)))
    const singles = genNodes.filter(n => !pairedIds.has(n.id))

    singles.forEach(parent => {
      const children = nodes.filter(n => n.generation === gen + 1)
      if (children.length > 0) {
        const childrenY = children[0].y
        const midY = parent.y + (childrenY - parent.y) / 2
        lines.push({ type: 'descent', x1: parent.x, y1: parent.y, x2: parent.x, y2: midY })
        const childrenXs = children.map(ch => ch.x).sort((a, b) => a - b)
        if (childrenXs.length > 0) {
          lines.push({ type: 'sibship', x1: childrenXs[0], y1: midY, x2: childrenXs[childrenXs.length - 1], y2: midY })
          children.forEach(ch => {
            lines.push({ type: 'drop', x1: ch.x, y1: midY, x2: ch.x, y2: ch.y })
          })
        }
      }
    })
  })

  // 6. 添加代标签 Y 和节点 labelY
  nodes.forEach(n => {
    n.labelY = n.y + NODE_H / 2 + 18
  })

  const generationLabels = genKeys.map(g => ({
    label: toRoman(g - minGen + 1),
    y: (g - minGen) * GEN_GAP + 100
  }))

  const maleNodes = nodes.filter(n => n.sex === '1')
  const femaleNodes = nodes.filter(n => n.sex === '2')
  const unknownNodes = nodes.filter(n => n.sex === '0')

  return {
    generations: generationLabels,
    lines,
    maleNodes,
    femaleNodes,
    unknownNodes,
    allLabelNodes: nodes,
    width: svgWidth,
    height: svgHeight
  }
}

function hasCoupleInGen(node, gen, couples) {
  return couples.some(c => c.generation === gen && c.couple.some(n => n.id === node.id))
}

function toRoman(num) {
  const romans = ['', 'I', 'II', 'III', 'IV', 'V', 'VI']
  return romans[num] || String(num)
}

// ==================== 渲染辅助 ====================
function nodeShapeClass(node) {
  const classes = ['pedigree-shape']
  if (node.isAffected) classes.push('is-affected')
  if (node.isProband) classes.push('is-proband')
  if (node.linked) classes.push('is-linked')
  return classes
}

function probandArrowPath(node) {
  const ax = node.x - NODE_W / 2 - 12
  const ay = node.y - NODE_H / 2 - 8
  return `M ${ax},${ay} l 8,-4 l 0,8 Z`
}

function probandArrowCirclePath(node) {
  const ax = node.x - NODE_R - 12
  const ay = node.y - NODE_R - 8
  return `M ${ax},${ay} l 8,-4 l 0,8 Z`
}

function diamondPoints(node) {
  const hw = NODE_W / 2
  const hh = NODE_H / 2
  return `${node.x},${node.y - hh} ${node.x + hw},${node.y} ${node.x},${node.y + hh} ${node.x - hw},${node.y}`
}

function handleNodeClick(node) {
  if (node.linked && node.linkedPatientId && node.linkedDiseaseType) {
    router.push(`/case/${node.linkedDiseaseType}/${node.linkedPatientId}`)
  }
  emit('node-click', node)
}

// ==================== 计算属性 ====================
const pedigreeNodes = computed(() => buildPedigreeData())
const layout = computed(() => computeLayout(pedigreeNodes.value))
const hasData = computed(() => props.familyMembers.length > 0)
const svgWidth = computed(() => layout.value.width)
const svgHeight = computed(() => Math.max(layout.value.height, 300))
</script>

<style scoped>
.pedigree-chart {
  width: 100%;
  overflow-x: auto;
  padding: 16px 0;
}

.pedigree-empty {
  padding: 24px;
}

.pedigree-svg {
  display: block;
  margin: 0 auto;
}

/* 代际标签 */
.gen-label {
  font-size: 13px;
  font-weight: 600;
  fill: #909399;
}

/* 连接线 */
.pedigree-line {
  stroke: #333;
  stroke-width: 1.5;
  fill: none;
}

.pedigree-line--marriage {
  stroke-width: 1.5;
}

.pedigree-line--descent {
  stroke-width: 1.5;
}

.pedigree-line--sibship {
  stroke-width: 1.5;
}

.pedigree-line--drop {
  stroke-width: 1.2;
}

/* 节点形状 */
.pedigree-shape {
  fill: #fff;
  stroke: #333;
  stroke-width: 1.5;
  cursor: default;
  transition: fill 0.2s;
}

.pedigree-node.is-linked .pedigree-shape {
  stroke: #409EFF;
  stroke-width: 2;
  cursor: pointer;
  stroke-dasharray: 6 2;
}

.pedigree-node.is-linked .pedigree-shape:hover {
  fill: #ecf5ff;
}

.pedigree-shape.is-affected {
  fill: #409EFF;
}

.pedigree-shape.is-proband {
  stroke-width: 2.5;
}

.proband-arrow {
  fill: #333;
}

.deceased-line {
  stroke: #333;
  stroke-width: 1.5;
}

/* 节点文字 */
.node-text {
  font-size: 11px;
  fill: #333;
  pointer-events: none;
}

.pedigree-shape.is-affected + .node-text,
.pedigree-shape.is-affected ~ .node-text {
  fill: #fff;
}

/* 标签 */
.node-label {
  font-size: 12px;
  fill: #606266;
  pointer-events: none;
}

.node-sublabel {
  font-size: 11px;
  fill: #909399;
  pointer-events: none;
}
</style>

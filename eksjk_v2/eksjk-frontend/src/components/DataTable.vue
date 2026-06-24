<template>
  <div class="data-table">
    <!-- 表格 -->
    <el-table
      ref="tableRef"
      :data="data"
      :border="border"
      :stripe="stripe"
      :highlight-current-row="highlightCurrentRow"
      :row-key="rowKey"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      @row-dblclick="handleRowDblClick"
      @sort-change="handleSortChange"
    >
      <!-- 多选列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
        align="center"
        fixed="left"
      />

      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
        fixed="left"
        :index="indexMethod"
      />

      <!-- 数据列插槽 -->
      <slot />

      <!-- 空数据提示 -->
      <template #empty>
        <div class="data-table__empty">
          <el-empty :description="emptyText" />
        </div>
      </template>
    </el-table>

    <!-- 分页 -->
    <div v-if="showPagination" class="data-table__pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="currentPageSize"
        :page-sizes="pageSizes"
        :total="total"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
/**
 * 数据表格组件
 * 封装 Element Plus el-table + el-pagination
 */
import { ref, computed } from 'vue'

const props = defineProps({
  /** 表格数据 */
  data: {
    type: Array,
    default: () => []
  },
  /** 是否加载中 */
  loading: {
    type: Boolean,
    default: false
  },
  /** 是否显示边框 */
  border: {
    type: Boolean,
    default: true
  },
  /** 是否显示斑马纹 */
  stripe: {
    type: Boolean,
    default: false
  },
  /** 是否高亮当前行 */
  highlightCurrentRow: {
    type: Boolean,
    default: true
  },
  /** 行数据的 Key */
  rowKey: {
    type: [String, Function],
    default: 'id'
  },
  /** 是否显示多选列 */
  showSelection: {
    type: Boolean,
    default: false
  },
  /** 是否显示序号列 */
  showIndex: {
    type: Boolean,
    default: false
  },
  /** 是否显示分页 */
  showPagination: {
    type: Boolean,
    default: true
  },
  /** 总记录数 */
  total: {
    type: Number,
    default: 0
  },
  /** 当前页码 */
  pageNum: {
    type: Number,
    default: 1
  },
  /** 每页条数 */
  pageSize: {
    type: Number,
    default: 10
  },
  /** 可选的每页条数 */
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  /** 空数据提示文字 */
  emptyText: {
    type: String,
    default: '暂无数据'
  }
})

const emit = defineEmits([
  'update:pageNum',
  'update:pageSize',
  'page-change',
  'selection-change',
  'row-click',
  'row-dblclick',
  'sort-change'
])

const tableRef = ref(null)

const currentPage = computed({
  get: () => props.pageNum,
  set: (val) => emit('update:pageNum', val)
})

const currentPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

/**
 * 序号计算方法（跨页连续）
 */
function indexMethod(index) {
  return (props.pageNum - 1) * props.pageSize + index + 1
}

/**
 * 每页条数变化
 */
function handleSizeChange(size) {
  emit('update:pageSize', size)
  emit('update:pageNum', 1)
  emit('page-change', { pageNum: 1, pageSize: size })
}

/**
 * 页码变化
 */
function handleCurrentChange(page) {
  emit('update:pageNum', page)
  emit('page-change', { pageNum: page, pageSize: props.pageSize })
}

/**
 * 多选变化
 */
function handleSelectionChange(selection) {
  emit('selection-change', selection)
}

/**
 * 行点击
 */
function handleRowClick(row, column, event) {
  emit('row-click', row, column, event)
}

/**
 * 行双击
 */
function handleRowDblClick(row, column, event) {
  emit('row-dblclick', row, column, event)
}

/**
 * 排序变化
 */
function handleSortChange({ column, prop, order }) {
  emit('sort-change', { column, prop, order })
}

// 暴露方法
defineExpose({
  /** 清除多选 */
  clearSelection: () => tableRef.value?.clearSelection(),
  /** 切换行选中状态 */
  toggleRowSelection: (row, selected) => tableRef.value?.toggleRowSelection(row, selected),
  /** 获取表格实例 */
  getTableRef: () => tableRef.value
})
</script>

<style scoped>
.data-table {
  background-color: var(--ek-bg-color);
  border-radius: var(--ek-radius-base);
}

.data-table__empty {
  padding: var(--ek-spacing-xl) 0;
}

.data-table__pagination {
  display: flex;
  justify-content: flex-end;
  padding: var(--ek-spacing-base);
}
</style>

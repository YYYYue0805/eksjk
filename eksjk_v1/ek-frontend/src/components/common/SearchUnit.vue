<template>
  <div class="search-user">
    <div class="first">
      <span class="title">单位名称：</span>
      <el-input style="width: 200px" size="small" clearable v-model="filters.unitName"></el-input>
    </div>

    <div class="seach-button">
      <el-button type="primary" size="small" @click="onSearch">查询</el-button>
      <el-button type="primary" size="small" @click="onReset" plain>重置</el-button>
    </div>
  </div>
</template>

<script>
  export default {
    name: "SearchUnit",
    data() {
      return {
        filters: this.defaultFilters()
      }
    },
    methods: {
      defaultFilters() {
        return {
          unitName: '',
        }
      },
      onSearch() {
        this.$emit('search', this.getFilters())
      },
      onReset() {
        this.filters = this.defaultFilters()
      },
      getFilters() {
        const filters = {}
        for (const key in this.filters) {
          let v = this.filters[key]
          if (v) {
            if (Array.isArray(v)) {
              v = v.join(',')
            }
            filters[key] = v
          }
        }

        return filters
      }
    }
  }
</script>

<style lang="less">
  @import "../../assets/less/variable.less";

  .search-user {
    display: flex;
    flex-wrap: wrap;

    .first {
      // width: 40%;
      margin-right: 1rem;
      margin-bottom: 1rem;
    }

    .title {
      font-size: @font-normal;
      margin-left: 10px;
    }
  }
</style>
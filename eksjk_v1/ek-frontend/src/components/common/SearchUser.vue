<template>
  <div class="search-user">
    <div class="first">
      <span class="title">用户名：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.username"></el-input>

      <span class="title">姓名：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.name"></el-input>

      <span class="title">单位：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.unit"></el-input>

      <span class="title">用户等级：</span>
      <el-select v-model="filters.level" size="small" clearable style="width: 130px">
        <el-option label="普通用户" value="0"></el-option>
        <el-option label="管理员" value="1"></el-option>
      </el-select>
    </div>

    <div class="seach-button">
      <el-button type="primary" size="small" @click="onSearch">查询</el-button>
      <el-button type="primary" size="small" @click="onReset" plain>重置</el-button>
    </div>
  </div>
</template>

<script>
  export default {
    name: "SearchUser",
    data() {
      return {
        filters: this.defaultFilters()
      }
    },
    methods: {
      defaultFilters() {
        return {
          username: '',
          name: '',
          unit: '',
          level: '',
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
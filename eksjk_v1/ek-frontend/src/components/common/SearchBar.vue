<template>
  <div class="search-bar">
    <div class="first">
      <span class="title">病例编号：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.caseNum"></el-input>

      <span class="title">性别：</span>
      <el-select v-model="filters.gender" size="small" clearable style="width: 100px">
        <el-option label="男" value="1"></el-option>
        <el-option label="女" value="2"></el-option>
      </el-select>

      <!-- <span class="title">疾病类型：</span>
      <el-select v-model="filters.disclass" size="small" clearable style="width: 100px">
        <el-option label="性发育异常" value="10000001"></el-option>
        <el-option label="家族性矮小" value="10000002"></el-option>
        <el-option label="中枢性性早熟" value="10000003"></el-option>
        <el-option label="MAS随访" value="10000004"></el-option>
        <el-option label="SGA" value="10000005"></el-option>
        <el-option label="矮小症" value="10000006"></el-option>
      </el-select> -->

      <span class="title">病历号：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.userNum"></el-input>

      <span class="title">姓名：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.name"></el-input>

      <span class="title">上传时间：</span>
      <el-date-picker
        class="timeCheck"
        v-model="filters.createDateRange"
        type="daterange"
        :clearable="false"
        unlink-panels
        size="mini"
        range-separator="~"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
      ></el-date-picker>
    </div>
    <div class="seach-button">
      <el-button type="primary" size="small" @click="onSearch">查询</el-button>
      <el-button type="primary" size="small" @click="onReset" plain>重置</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchBar',
  props: {
    userlevel: String,
  },
  data () {
    return {
      filters: this.defaultFilters()
    }
  },
  methods: {
    defaultFilters () {
      return {
        caseNum: '',
        gender: '',
        // disclass: '',
        //获取存储的疾病类型
        disclass: localStorage.getItem('disclass') || '',
        userNum: '',
        name: '',
        createDateRange: '',
      }
    },
    onSearch () {
      this.$emit('search', this.getFilters())
    },
    onReset () {
      this.filters = this.defaultFilters()
    },
    getFilters () {
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

.search-bar {
  display: flex;
  flex-wrap: wrap;
  .second{
    // width: 40%;
    margin-right: 1rem;
    margin-bottom: 1rem;
  }
  .first{
    // width: 40%;
    margin-right: 1rem;
    margin-bottom: 1rem;
  }
  .title {
    font-size: @font-normal;
    margin-left: 10px;
  }
  .title.left-first {
    // margin-left: 0;
  }
}
</style>
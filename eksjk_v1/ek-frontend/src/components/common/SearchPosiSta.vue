<template>
  <div class="search-user">
    <div class="first">
      <span class="title">病例类型：</span>
      <el-select v-model="filters.organId" size="small" clearable style="width: 100px" v-if="!isSuper && level === 3">
        <el-option
          v-for="item in organList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        >
        </el-option>
      </el-select>
      <el-select v-model="filters.organId" size="small" clearable style="width: 100px" v-else>
        <el-option
          v-for="item in organs"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>

      <span class="title">上传单位：</span>
      <el-input style="width: 100px" size="small" clearable v-model="filters.upunit"></el-input>


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
    name: "SearchPosiSta",
    data() {
      return {
        organs: [{
          id: '10000001',
          name: '性发育异常'
        }, {
          id: '10000002',
          name: '家族性矮小'
        }, {
          id: '10000003',
          name: '中枢性性早熟'
        }],
        filters: this.defaultFilters(),
        isSuper: false,
        level: '',
        createDateRange: '',
        list: [],
        organList: [],
      }
    },
    activated() {
      this.isSuper = this.$store.state.user.isSuper;
      this.level = this.$store.state.user.level;
      this.list = this.$store.state.user.position;
      this.organList = [];
      if (this.list) {
        this.list.forEach(item => {
          if (item === "10000001") {
            this.organList.push({id: item, name: "性发育异常"})
          } else if (item === "10000002") {
            this.organList.push({id: item, name: "家族性矮小"})
          } else if (item === "10000003") {
            this.organList.push({id: item, name: "中枢性性早熟"})
          }
        })
      }
    },
    methods: {
      defaultFilters() {
        return {
          organId: '',
          type: '',
          clinicalDiagnosis: '',
          upunit: '',
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
      margin-right: 1rem;
      margin-bottom: 1rem;
    }

    .title {
      font-size: @font-normal;
      margin-left: 10px;
    }
  }
</style>
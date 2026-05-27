<template>
  <div class="userManager">
    <SearchMenu></SearchMenu>
    <el-row class="user-head">
      <el-col :span="8">
        <el-button @click="getStatisticPosiExcel"
                   size="mini"
                   style="position: absolute;right: 8vw;top: 12px"
                   type="primary"
        >导出Excel
        </el-button>
        <el-button @click='backHistory' size='mini' style="position: absolute;right: 1vw;top: 12px" type="primary">返回
        </el-button>
      </el-col>
    </el-row>

    <SearchPosiSta @search="getStatisticPosi" ref="searchPosiSta"></SearchPosiSta>

    <el-table
      style="min-height: 70vh"
      :data="posiStaNum"
      :header-cell-style="{background:'#f1f1f1', color:'#333','font-weight':'400','font-size':'14px', padding: '5px 0'}"
      align="center"
      border
      highlight-current-row
      reserve-selection="true"
      @sort-change='tableSortChange'
      :default-sort = "{prop: 'sums', order: 'descending'}"
      tooltip-effect="dark">

      <el-table-column align="center" label="序号" type="index" width="300">
        <template slot-scope="scope">{{ scope.$index + (parseInt(currPage)-1)* parseInt(pageSize) }}</template>
      </el-table-column>

      <el-table-column
        label="单位名称"
        property="unitName"
        show-overflow-tooltip
        width="700"
        align="center">
      </el-table-column>

      <el-table-column
        label="总数"
        property="sums"
        show-overflow-tooltip
        sortable='custom'
        width="300"
        align="center">
      </el-table-column>

      <el-table-column
        label="本周上传"
        sortable='custom'
        property="benz"
        width="300"
        align="center">
      </el-table-column>

      <el-table-column
        label="本月上传"
        sortable='custom'
        property="beny"
        width="300"
        align="center">
      </el-table-column>
    </el-table>

    <div align="right" class="block" style="padding:20px 10px 20px 0">
      <!--elementUI的分页控件-->
      <el-pagination
        :current-page.sync="currPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :page-size="pageSize"
        :page-sizes="[10, 15, 20, 30]"
        :total="total"
        layout="total, sizes ,prev, pager, next, jumper">
      </el-pagination>
    </div>

    <div style="width: 100%" align="center">
      <div ref="chart" style="width:95vw;height:100vh"></div>
    </div>
  </div>

</template>

<script>
  import SearchPosiSta from './common/SearchPosiSta'
  import request from "../script/request";
  import SearchMenu from './common/SearchMenu'

  export default {
    name: "StatisticPosi",
    components: {
      SearchPosiSta,
      SearchMenu,
    },
    data() {
      return {
        posiStaNum: [],
        is_active: true,
        total: 0,
        currPage: 1,
        pageSize: 10,
        sortby: '',
        order: '',
      }
    },
    mounted(){
      this.getData()
    },
    created() {
      setTimeout(() => {
        let filters = this.$refs.searchPosiSta.getFilters()
      this.getStatisticPosi(filters)
      }, 500)
    },
    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      getStatisticPosi(filters) {
        filters.currPage = this.currPage; //每次传过去的页数，我这边后端是从1开始分页的
        filters.limit = this.pageSize; //每页需要展示的条数
        filters.sortby = this.sortby;
        filters.order  = this.order;
        request.getStatisticPosi(filters, data => {
          let posiStaNums = [];
          data['contacts'].forEach(item => {
            posiStaNums.push({
              unitName: item[0],
              sums: item[1],
              ruku: item[2],
              shenhez: item[3],
              benz: item[4],
              beny: item[5],
            });
          })
          this.posiStaNum = posiStaNums;
          this.total = data['pagedata'].count;
        }, error => {
          console.log(error)
        })
        this.getData()
      },
      tableSortChange(column) {
        let filters = this.$refs.searchPosiSta.getFilters()
        if (column.order === 'descending') {
          this.sortby = column.prop
          this.order = 'desc'
        } else {
          this.sortby = column.prop
          this.order = 'asc'
        }
        this.getStatisticPosi(filters)
      },
      handleSizeChange(val) {
        let filters = this.$refs.searchPosiSta.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.pageSize = val;
        this.currPage = 1;
        this.getStatisticPosi(filters)
      },
      handleCurrentChange(val) {
        let filters = this.$refs.searchPosiSta.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.currPage = val;
        this.getStatisticPosi(filters)
      },
      getStatisticPosiExcel() {
        this.$message({
          message: '正在请求文件……',
          type: 'info'
        });
        let data = this.$refs.searchPosiSta.getFilters();
        request.loadFile(data, url => {
          // 触发浏览器的文件下载
          let a = document.createElement('a');
          a.href = url;
          a.click();
          this.$message({
            message: '开始下载',
            type: 'success'
          });
        }, error => {
          this.$message('下载失败！');
          console.log(error)
        })
      },
      getData() {
        let filters = this.$refs.searchPosiSta.getFilters()
        filters.currPage = this.currPage;
        filters.limit = this.pageSize;
        request.staPosiNoPage(filters, data => {
          let xArr = [];
          let yArr = [];
          data.contacts.forEach(item => {
            if (item['0'] != "合计") {
              xArr.push(item['0'])
              yArr.push(item['1'])
            }
          })
          this.getEchartData(xArr,yArr)
        })
      },
      getEchartData(xArr,yArr) {
        const chart = this.$refs.chart
        const myChart = this.$echarts.init(chart)
        if (chart) {
          const option = {
            color: ['#3398DB'],
            tooltip: {
              trigger: 'axis',
              axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
              }
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: [
              {
                type: 'category',
                data: xArr,
                axisTick: {
                  alignWithLabel: true
                },
                position: 'bottom',
                axisLabel: {
                  interval:0,
                  formatter: function (value) {
                    var ret = "";//拼接加\n返回的类目项
                    var maxLength = 2;//每项显示文字个数
                    var valLength = value.length;//X轴类目项的文字个数
                    var rowN = Math.ceil(valLength / maxLength); //类目项需要换行的行数
                    if (rowN > 1)//如果类目项的文字大于3,
                    {
                      for (var i = 0; i < rowN; i++) {
                        var temp = "";//每次截取的字符串
                        var start = i * maxLength;//开始截取的位置
                        var end = start + maxLength;//结束截取的位置
                        // 这里也可以加一个是否是最后一行的判断，但是不加也没有影响，那就不加吧
                        temp = value.substring(start, end) + "\n";
                        ret += temp; //凭借最终的字符串
                      }
                      return ret;
                    } else {
                      return value;
                    }
                  }
                },
              }
            ],
            yAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '总数',
                type: 'bar',
                barWidth: '50%',
                data: yArr,
                itemStyle: {
                  normal: {
                    label: {
                      show: true, //开启显示
                      position: 'top', //在上方显示
                      textStyle: { //数值样式
                        color: 'black',
                        fontSize: 16
                      }
                    }
                  }
                },
              }
            ]
          }
          myChart.setOption(option)
          myChart.resize()
          window.addEventListener("resize", function () {
            myChart.resize()
          })
        }
        this.$on('hook:destroyed', () => {
          window.removeEventListener("resize", function () {
            myChart.resize();
          });
        })
      },

    },
  }
</script>

<style lang="less" scoped>
  .userManager {
    .user-head {
      height: 5vh;
      background: #ffffff;
    }

    .user-title {
      margin-left: 2vw;
      font-size: 1.5vw;
      color: rgb(64, 158, 255);
    }
  }
</style>
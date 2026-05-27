<template>
    <div class="TianYuan">
      <div style="width: 100%;height: 5vh">
       <!--  <el-link class="tab-href"
                 type="primary"
                 @click="onUpload"
                 :underline="false"
                 v-show="userlevel == 0"
        >上传
        </el-link> -->
        <!-- <div class="upload-button el-icon-circle-plus-outline"
             @click="onUpload"
             v-show="userlevel == 0"
        ></div> -->
        <el-button  type="primary" size="small" @click="backType">
          返回选择疾病类型
        </el-button>
      </div>
      <el-dialog
        title="删除提醒"
        :visible.sync="visible"
        width="22%"
      >
        <span>确定删除该条信息吗？</span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="doDelete">确 定</el-button>
        </span>
      </el-dialog>
  
      <SearchBar
        style="margin-top: 1vh"
        :userlevel="userlevel.toString()"
        @search="update"
        ref="searchBar">
      </SearchBar>
  
      <el-table
        :data="cases"
        border
        tooltip-effect="dark"
        reserve-selection="true"
        align="center"
        highlight-current-row
        @selection-change="handleSelectionChange"
        :header-cell-style="{background:'#f1f1f1', color:'#333','font-weight':'400','font-size':'14px', padding: '5px 0'}">
  
        <el-table-column
          v-if="this.cases.length > '0'"
          type="selection"
          width="60"
          align="center">
        </el-table-column>
  
        <el-table-column label="序号" type="index" align="center" width="60">
        </el-table-column>
        <el-table-column
          property="oper_per_id"
          label="操作人"
          min-width="120">
        </el-table-column>
        <el-table-column
          property="oper_case_id"
          label="操作病例"
          min-width="80">
        </el-table-column>
  
        <el-table-column
          property="oper_step"
          label="操作步骤"
          min-width="60">
        </el-table-column>
        <el-table-column
          property="oper_data"
          label="操作时间"
          min-width="120">
        </el-table-column>
        <!-- <el-table-column
          property="is_admin_login"
          label="是否管理员操作"
          min-width="160">
        </el-table-column> -->
        <!-- <el-table-column
          fixed="right"
          label="操作">
          <template slot-scope="scope">
            <div>
              <el-tooltip class="item" effect="dark" content="查看" placement="top">
                <i class="el-icon-view" @click="lookDetailClick(scope.row)" type="text" size="small">查看详情</i>
              </el-tooltip>
               <el-tooltip class="item" effect="dark" content="编辑" placement="top">
                <i class="el-icon-edit" @click="upDateClick(scope.row)" type="text" size="small"></i>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" content="删除" placement="top">
                <i class="el-icon-delete" @click="del(scope.row)" type="text" size="small"></i>
              </el-tooltip>
            </div>
          </template>
        </el-table-column> -->
      </el-table>
      <!--    控制分页部分-->
      <div class="block" style="padding:20px 10px 20px 0" align="right">
        <!--elementUI的分页控件-->
        <el-pagination
          :current-page.sync="currPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="[10, 15, 20, 30]"
          :page-size="pageSize"
          layout="total, sizes ,prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>
  </template>
  
  <script>
    import SearchBar from './common/logSearchBar'
    import request from '../script/request'
    import slectCount from '../script/selectCount'
  
    export default {
      name: 'EditLog',
      data() {
        return {
          activeTab: '0',
          cases: [],
          userlevel: '',
          visible: false,
          selectIds: [],
          total: 0,//默认数据总数
          currPage: 1,  //默认第一页
          pageSize: 10, //默认展示10条数据,
          deleteQueryId: '',
          backId: '',
        }
      },
      methods: {
        handleCommand(command) {
          if (command == 'a') {
            this.userManager()
          } else if (command == 'c') {
            this.statistics()
          } else if (command == 'b') {
            this.unit()
          }
        },
        onUpload() {
          //如果本地存储的疾病类型有值跳转到编辑页面
          if (sessionStorage.getItem('OrganSelect')) {
            this.$router.push({name: 'editor', query: {organ: sessionStorage.getItem('OrganSelect')}}) //带参跳转
          }else{
            this.$router.push({name: 'diseaseSelect'}) //带参跳转
          }
        },
  
        update(filters = null) {
          filters.currPage = this.currPage; //每次传过去的页数，我这边后端是从1开始分页的
          filters.limit = this.pageSize; //每页需要展示的条数
          filters.keywords = this.input; //双向绑定的关键字
          filters.disclass=sessionStorage.getItem('OrganSelect')
          request.getLogList(filters, data => {
            let cases = []
            data['contacts'].forEach(item => {
              cases.push({
                oper_per_id: item.oper_per_id,
                oper_case_id:item.oper_case_id,
                oper_step:item.oper_step=='10050001'?'数据修改':'',
                oper_data:item.oper_data,
                // is_admin_login:item.is_admin_login=='1'?'是':'否',
                // sex: (item.sex != null ? (slectCount.sex[item.sex]) : ""),
                // birthTime: (item.birth_time != null ? item.birth_time.substring(0,10) : ""),
                diagnosis: item.diagnosis,
                cesaSec: (item.cesa_sec != null ? (slectCount.cesaSec[item.cesa_sec]) : ""),
                updateTime:item.modify_time,
              
                modify_per:item.modify_per,
                id: item.id,
                disClass: item.dis_class,
                userNum: item.user_num,
              })
            })
            this.cases = cases
            this.total = data['pagedata'].count
          }, error => {
            console.log(error)
          })
        },
        handleSizeChange(val) {
          let filters = this.$refs.searchBar.getFilters()
          filters.status = this.activeTab
          //改变每页显示数量重新请求数据，重置当前页为第一页
          this.pageSize = val;
          this.currPage = 1;
          this.update(filters)
        },
        handleCurrentChange(val) {
          let filters = this.$refs.searchBar.getFilters()
          filters.status = this.activeTab
          //点击改变当前页
          this.currPage = val;
          this.update(filters)
        },
        upDateClick(row) {
          this.$router.push({name: 'editor', query: {'queryId': row.id, 'disClass': row.disClass, 'userNum': row.userNum}})
        },
        del(row) {
          const query = {}
          let v = row['id']
          if (v) {
            if (Array.isArray(v)) {
              v = v.join(',')
            }
            query['queryId'] = v
          }
          this.deleteQueryId = query;
          this.visible = true;
        },
        doDelete() {
          request.delCase(this.deleteQueryId, () => {
            let filters = this.$refs.searchBar.getFilters()
            filters.status = this.activeTab
            this.update(filters)
            this.$message({
              message: '删除成功',
              type: 'success'
            });
          }, error => {
            this.$message('删除失败');
            console.log(error)
          })
          this.deleteQueryId = '';
          this.visible = false;
        },
  
        lookDetailClick(row) {
          this.$router.push({name: 'studentEditor', query: {'queryId': row.id, 'disClass': '10000010', 'birthTime': row.birthTime}})
        },
  
        goDetail(row){
          this.$router.push({name: 'editor', query: {'queryId': row.id, 'disClass': row.disClass,'follow': 'follow', 'birthTime': row.birthTime }})
        },
  
        userManager() {
          this.$router.push({name: 'user'})
        },
  
        statistics() {
          this.$router.push({name: 'statisticPosi'})
        },
  
        unit() {
          this.$router.push({name: 'unit'})
        },
  
        handleSelectionChange(val) {
          let newList = [];
          val.forEach(item => {
            newList.push(item.id)
          })
          this.selectIds = newList;
        },
  
        passUpdate() {
          const user = this.$store.state.user
          let date1 = user.updateTime.substring(0, 10).split('-')
  
          let dateNow = new Date();
          let year = dateNow.getFullYear();
          let month = dateNow.getMonth() + 1 < 10 ? "0" + (dateNow.getMonth() + 1) : dateNow.getMonth() + 1;
          let day = dateNow.getDate() < 10 ? "0" + dateNow.getDate() : dateNow.getDate();
          let date2 = (year + "-" + month + "-" + day).split('-');
  
          date1 = parseInt(date1[0]) * 12 + parseInt(date1[1]);
          date2 = parseInt(date2[0]) * 12 + parseInt(date2[1]);
  
          let date3 = date2 - date1;
          if (date3 > 6) {
            // console.log(date1 + '------' + date2 + '-----' + date3);
            this.$confirm('您已超过6个月未修改密码，请尽快前往个人中心进行修改', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.$router.push({name: 'user-profile'})
            }).catch(() => {
  
            });
          }
        },
        //批量下载
        openDown(){
          // console.log(111)
          if (this.selectIds.length > 0) {
          let selectIds = this.selectIds;
          request.getZipPl({selectIds}, url => {
            // 触发浏览器的文件下载
            let a = document.createElement('a');
            a.href = url;
            // console.log(url)
            a.click();
            this.$message({
              message: '开始下载',
              type: 'success'
            });
          }, error => {
            this.$message('下载失败！');
            console.log(error)
          })
        } else {
          this.$message('请至少选择一个病例进行下载！');
        }
        },
        //返回疾病类型选择
        backType(){
          //清除本地存储
          sessionStorage.removeItem('OrganSelect')
          this.$router.push({name: 'diseaseSelect'}) //带参跳转
        },
  
        //导出Excel
        exportDown() {
          this.$message({
            message: '正在请求文件……',
            type: 'info'
          });
          let data = this.$refs.searchBar.getFilters();
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
      },
  
      activated() {
        let filters = this.$refs.searchBar.getFilters()
        filters.status = this.activeTab
        this.update(filters)
        this.passUpdate()
      },
      components: {
        SearchBar,
      },
    }
  </script>
  
  <style lang="less">
    .TianYuan {
      height: 100%;
      position: relative;
      padding: 5px;
      display: flex;
      flex-direction: column;
  
      .upload-button {
        position: absolute;
        top: 12px;
        right: 65px;
        font-size: 26px;
        border-radius: 12px;
        color: white;
        background-color: #6cb2f8;
  
        &:hover {
          background-color: #409eff;
        }
      }
  
      .tab-button {
        position: absolute;
        top: 12px;
        right: 15px;
        /*font-size: 24px;*/
        /*border-radius: 12px;*/
        color: white;
        background-color: #6cb2f8;
  
        &:hover {
          background-color: #409eff;
        }
      }
  
      .tab-href {
        position: absolute;
        top: 12px;
        right: 25px;
        font-size: 20px;
      }
  
      .tab-badge .el-badge__content {
        margin-top: 4px;
      }
  
      .table-head {
        height: 15px;
        background-color: #cccccc;
        font-size: 13px;
        color: #333;
        border: 1px solid #666;
        font-weight: 400;
      }
    }
  
    .el-tabs__header {
      position: relative;
  
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: #409eff;
      }
  
      .el-tabs__nav {
        position: absolute;
        top: 3px;
        margin-left: 10px;
        padding-right: 10px;
        box-sizing: border-box;
        position: relative;
      }
  
      .el-tabs__item {
        // border-bottom: 1px solid #409EFF ;
        box-sizing: border-box;
        padding: 0 10px !important;
      }
  
      .is-active {
        // box-sizing: border-box;
        border-left: 1px solid #409eff;
        border-right: 1px solid #409eff;
        border-bottom: none;
        border-top: 3px solid #409eff;
        border-radius: 4px 4px 0 0;
        background: #fff;
        z-index: 6;
      }
  
      .el-tabs__active-bar {
        display: none;
        background: #fff;
      }
    }
  
    .el-date-editor.el-range-editor.el-input__inner.timeCheck.el-date-editor--daterange.el-range-editor--mini {
      width: 240px;
    }
  
    .not-operating td:first-child {
      background: url("../assets/img/new.png") no-repeat;
      background-size: 60%;
    }
  
    .el-table .warning-row {
      background: oldlace;
    }
  
    .el-table .success-row {
      background: #f0f9eb;
    }
  
    .item {
      padding: 0 6px 0 6px;
    }
    //批量下载
    .batch-button{
      width: 100px;
      position: absolute;
      top: 10px;
      right: 18%;
    }
    //导出Excel
    .export-button{
      width: 100px;
      position: absolute;
      top: 10px;
      right: 28%;
    }
  </style>
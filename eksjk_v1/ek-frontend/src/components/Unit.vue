<template>
  <div class="unitManager">
    <el-row class="unit-head">
      <el-col :span="16" align="left"><span class="unit-title">单位管理</span></el-col>
      <el-col :span="8">
        <el-button @click="addUnit"
                   size="mini"
                   style="position: absolute;right: 8vw;top: 12px"
                   type="primary"
                   v-show="this.$store.state.user.level == 1"
        >添加单位
        </el-button>
        <el-dialog
          :modal="false"
          :visible.sync="addVisible"
          :close-on-click-modal='false'
          title="添加单位"
          width="35%">
          <InsertUnit ref="insertUnit"></InsertUnit>
          <span class="dialog-footer" slot="footer">
            <el-button @click="addVisible = false">取 消</el-button>
            <el-button @click="doAddUnit" type="primary">确 定</el-button>
          </span>
        </el-dialog>
        <el-button @click='backHistory' size='mini' style="position: absolute;right: 1vw;top: 12px" type="primary">返回
        </el-button>
      </el-col>
    </el-row>

    <SearchUnit @search="getUnitList" ref="searchUnit"></SearchUnit>

    <el-table
      :data="unit"
      :header-cell-style="{background:'#f1f1f1', color:'#333','font-weight':'400','font-size':'14px', padding: '5px 0'}"
      align="center"
      border
      highlight-current-row
      reserve-selection="true"
      tooltip-effect="dark">

      <el-table-column align="center" label="序号" type="index" width="400">
      </el-table-column>


      <el-table-column
        label="单位"
        property="unitName"
        width="1000"
        align="center">
      </el-table-column>

      <el-table-column
        property="isActive"
        fixed="right"
        label="操作"
        width="510"
        align="center">
        <template slot-scope="scope">
          <div>
            <el-tooltip class="item" content="编辑" effect="dark" placement="top">
              <i @click="updateUnit(scope.row)" class="el-icon-edit" size="small" type="text"></i>
            </el-tooltip>
            <el-tooltip class="item" content="删除" effect="dark" placement="top">
              <i @click="deleteUnit(scope.row)" class="el-icon-delete" size="small" type="text"></i>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :modal="false"
      :visible.sync="editorVisible"
      :close-on-click-modal='false'
      title="修改单位信息"
      width="35%">
      <EditorUnit
        ref="editorUnit"
      >
      </EditorUnit>
      <span class="dialog-footer" slot="footer">
        <el-button @click="editorVisible = false">取 消</el-button>
        <el-button @click="editUnit" type="primary">确认修改</el-button>
      </span>
    </el-dialog>

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
  </div>
</template>

<script>
  import SearchUnit from './common/SearchUnit'
  import request from "../script/request";
  import InsertUnit from './common/InsertUnit'
  import EditorUnit from './common/EditorUnit'

  export default {
    name: "Unit",
    data() {
      return {
        addVisible: false,
        editorVisible: false,
        unit: [],
        is_active: true,
        total: 0,
        currPage: 1,
        pageSize: 10,
      }
    },
    created() {
      setTimeout(() => {
        let filters = this.$refs.searchUnit.getFilters()
        this.getUnitList(filters)
      }, 500)
    },
    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      getUnitList(filters) {
        filters.currPage = this.currPage; //每次传过去的页数，我这边后端是从1开始分页的
        filters.limit = this.pageSize; //每页需要展示的条数
        request.getUnitList(filters, data => {
          let unit = [];
          data['contacts'].forEach(item => {
            unit.push({
              unitName: item.unit_name,
              id: item.id,
            });
            this.is_active = item.is_active;
          })
          this.unit = unit;
          this.total = data['pagedata'].count;
        }, error => {
          console.log(error)
        })
      },
      handleSizeChange(val) {
        let filters = this.$refs.searchUnit.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.pageSize = val;
        this.currPage = 1;
        this.getUnitList(filters)
      },
      handleCurrentChange(val) {
        let filters = this.$refs.searchUnit.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.currPage = val;
        this.getUnitList(filters)
      },
      addUnit() {
        if (this.$refs.insertUnit) {
          this.$refs.insertUnit.clear()
        }
        this.addVisible = true;
      },
      doAddUnit() {
        const isRight = this.$refs.insertUnit.chenkFrom();
        if (isRight) {
          request.insertUnit(this.$refs.insertUnit.unitForm, data => {
            console.log(data)
            this.addVisible = false;
            this.$message("添加成功")
            let filters = this.$refs.searchUnit.getFilters()
            this.getUnitList(filters)
          }, error => {
            console.log(error)
            this.$message("添加失败")
          })
        } else {
          this.$message("请补全数据!")
        }
      },
      updateUnit(row) {
        this.editorVisible = true;
        const id = row.id;
        setTimeout(() => {
          this.$refs.editorUnit.getUnitInfo(id);
        }, 10)
      },
      editUnit() {
        const isRight = this.$refs.editorUnit.chenkFrom();
        if (isRight) {
          request.insertUnit(this.$refs.editorUnit.editorForm, data => {
            console.log(data)
            this.$message("修改成功")
            this.editorVisible = false;
            let filters = this.$refs.searchUnit.getFilters()
            this.getUnitList(filters)
          }, error => {
            console.log(error)
            this.$message("修改失败")
          })
        } else {
          this.$message("请补全数据！")
        }
      },
      deleteUnit(row) {
        this.$confirm('是否删除该单位？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const queryId = row.id;
          request.deleteUnit({queryId}, data => {
            console.log(data)
            this.$message("删除成功")
            let filters = this.$refs.searchUnit.getFilters()
            this.getUnitList(filters)
          }, error => {
            console.log(error)
            this.$message("删除失败")
          })
        }).catch(() => {

        });
      },
    },
    components: {
      SearchUnit,
      EditorUnit,
      InsertUnit
    },
  }
</script>

<style lang="less" scoped>
  .unitManager {
    .unit-head {
      height: 5vh;
      background: #ffffff;
    }

    .unit-title {
      margin-left: 2vw;
      font-size: 1.5vw;
      color: rgb(64, 158, 255);
    }
  }
</style>
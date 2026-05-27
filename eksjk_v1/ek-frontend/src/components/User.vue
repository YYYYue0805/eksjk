<template>
  <div class="userManager">
    <el-row class="user-head">
      <el-col :span="16" align="left"><span class="user-title">用户管理</span></el-col>
      <el-col :span="8">
        <el-button @click="addUser"
                   size="mini"
                   style="position: absolute;right: 8vw;top: 12px"
                   type="primary"
                   v-show="this.$store.state.user.level == 1"
        >添加人员
        </el-button>
        <el-dialog
          :modal="false"
          :visible.sync="addVisible"
          :close-on-click-modal='false'
          title="添加人员"
          width="35%">
          <InsertUser ref="insertUser"></InsertUser>
          <span class="dialog-footer" slot="footer">
            <el-button @click="addVisible = false">取 消</el-button>
            <el-button @click="doAddUser" type="primary">确 定</el-button>
          </span>
        </el-dialog>
        <el-button @click='backHistory' size='mini' style="position: absolute;right: 1vw;top: 12px" type="primary">返回
        </el-button>
      </el-col>
    </el-row>

    <SearchUser @search="getUserList" ref="searchUser"></SearchUser>

    <el-table
      :data="users"
      :header-cell-style="{background:'#f1f1f1', color:'#333','font-weight':'400','font-size':'14px', padding: '5px 0'}"
      align="center"
      border
      highlight-current-row
      reserve-selection="true"
      tooltip-effect="dark">

      <el-table-column align="center" label="序号" type="index" width="240">
      </el-table-column>

      <el-table-column
        label="用户名"
        property="userName"
        show-overflow-tooltip
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        label="姓名"
        property="name"
        show-overflow-tooltip
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        label="性别"
        property="sex"
        show-overflow-tooltip
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        label="单位"
        property="unit"
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        label="职称"
        property="professional"
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        label="用户等级"
        property="level"
        width="240"
        align="center">
      </el-table-column>

      <el-table-column
        property="isActive"
        fixed="right"
        label="操作"
        width="230"
        align="center">
        <template slot-scope="scope">
          <div>
            <el-tooltip v-if="scope.row.isActive" class="item" content="禁用" effect="dark" placement="top">
              <i @click="disableUser(scope.row)" class="el-icon-circle-close" size="small" type="text"></i>
            </el-tooltip>
            <el-tooltip v-if="!scope.row.isActive" class="item" content="启用" effect="dark" placement="top">
              <i @click="ableUser(scope.row)" class="el-icon-circle-check" size="small" type="text"></i>
            </el-tooltip>
            <el-tooltip class="item" content="编辑" effect="dark" placement="top">
              <i @click="updateUser(scope.row)" class="el-icon-edit" size="small" type="text"></i>
            </el-tooltip>
            <el-tooltip class="item" content="删除" effect="dark" placement="top">
              <i @click="deleteUser(scope.row)" class="el-icon-delete" size="small" type="text"></i>
            </el-tooltip>
            <el-tooltip class="item" id="QR" content="下载二维码" effect="dark" placement="top">
              <i @click="down(scope.row)" class="el-icon-s-grid" size="small" type="text">
                <qrcode-vue style="display: none;" :value='QRvalue' :size='size'></qrcode-vue>
              </i>
              
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :modal="false"
      :visible.sync="editorVisible"
      :close-on-click-modal='false'
      title="修改人员信息"
      width="35%">
      <EditorUser
        ref="editorUser"
      >
      </EditorUser>
      <span class="dialog-footer" slot="footer">
        <el-button @click="editorVisible = false">取 消</el-button>
        <el-button @click="editUser" type="primary">确认修改</el-button>
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
  import SearchUser from './common/SearchUser'
  import request from "../script/request";
  import InsertUser from './common/InsertUser'
  import EditorUser from './common/EditorUser'
  import slectCount from '../script/selectCount'
  import QrcodeVue from 'qrcode.vue'
  export default {
    name: "User",
    data() {
      return {
        addVisible: false,
        editorVisible: false,
        users: [],
        is_active: true,
        total: 0,
        currPage: 1,
        pageSize: 10,
        QRvalue:'',//二维码数据
        size: 100, //二维码大小
      }
    },
    activated() {
      let filters = this.$refs.searchUser.getFilters()
      this.getUserList(filters)
    },
    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      getUserList(filters) {
        filters.currPage = this.currPage; //每次传过去的页数，我这边后端是从1开始分页的
        filters.limit = this.pageSize; //每页需要展示的条数
        request.getUserList(filters, data => {
          let user = [];
          data['contacts'].forEach(item => {
            user.push({
              userName: item.username,
              name: item.name,
              sex: item.sex,
              unit: item.unit,
              level: (item.level != null ? (slectCount.level[item.level]) : ""),
              professional: (item.professional != null ? (slectCount.professional[item.professional]) : ""),
              isActive: item.is_active,
              id: item.id,
            });
            this.is_active = item.is_active;
          })
          this.users = user;
          this.total = data['pagedata'].count;
        }, error => {
          console.log(error)
        })
      },
      handleSizeChange(val) {
        let filters = this.$refs.searchUser.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.pageSize = val;
        this.currPage = 1;
        this.getUserList(filters)
      },
      handleCurrentChange(val) {
        let filters = this.$refs.searchUser.getFilters()
        //改变每页显示数量重新请求数据，重置当前页为第一页
        this.currPage = val;
        this.getUserList(filters)
      },
      disableUser(row) {
        this.$confirm('是否禁用该账号？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const queryId = row.id;
          const isActive = false;
          let data1 = {queryId, isActive}
          request.userStatus(data1, data => {
            console.log(data)
            this.$message("禁用完成")
            let filters = this.$refs.searchUser.getFilters()
            this.getUserList(filters)
          }, error => {
            console.log(error)
            this.$message("禁用失败")
          })
        }).catch(() => {

        });
      },
      ableUser(row) {
        this.$confirm('是否激活该账号？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const queryId = row.id;
          const isActive = true;
          let data1 = {queryId, isActive}
          request.userStatus(data1, data => {
            console.log(data)
            this.$message("激活完成")
            let filters = this.$refs.searchUser.getFilters()
            this.getUserList(filters)
          }, error => {
            console.log(error)
            this.$message("激活失败")
          })
        }).catch(() => {

        });
      },
      updateUser(row) {
        this.editorVisible = true;
        const id = row.id;
        setTimeout(() => {
          this.$refs.editorUser.getUserInfo(id);
        }, 10)
      },
      editUser() {
        const isRight = this.$refs.editorUser.chenkFrom();
          if (isRight) {
            request.updateUser(this.$refs.editorUser.editorForm, data => {
              console.log(data)
              this.$message("修改成功")
              this.editorVisible = false;
              let filters = this.$refs.searchUser.getFilters()
              this.getUserList(filters)
            }, error => {
              console.log(error)
              this.$message("修改失败")
            })
          } else {
            this.$message("请补全数据！")
          }
      },
      deleteUser(row) {
        this.$confirm('是否删除该账号？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const queryId = row.id;
          request.deleteUser({queryId}, data => {
            console.log(data)
            this.$message("删除成功")
            let filters = this.$refs.searchUser.getFilters()
            this.getUserList(filters)
          }, error => {
            console.log(error)
            this.$message("删除失败")
          })
        }).catch(() => {

        });
      },
      down(row){
      // 将row对象转换为字符串并放入QRvalue
      // this.QRvalue = JSON.stringify(row);
      //   console.log(this.QRvalue)
      // 使用Vue.nextTick确保DOM更新完成
      this.QRvalue='Id:'+row.id
      this.$nextTick(() => {
        //获取canvas标签
        let canvas = document.getElementById('QR').getElementsByTagName('canvas')
        //创建a标签
        let a = document.createElement('a')
        //获取二维码的url并赋值为a.href
        a.href = canvas[0].toDataURL('img/png')
        //设置下载文件的名字
        a.download = '二维码'
       // 等待canvas生成二维码后再执行下载操作
       a.addEventListener('click', () => {
          this.$message.warn('下载中，请稍后...');
        });

        // 添加a标签到body，触发点击事件
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      });
    }, 
      addUser() {
        if (this.$refs.insertUser) {
          this.$refs.insertUser.clear()
        }
        this.addVisible = true;
      },
      doAddUser() {
        const isRight = this.$refs.insertUser.chenkFrom();
        if (isRight) {
          request.insertUser(this.$refs.insertUser.userForm, data => {
            console.log(data)
            this.addVisible = false;
            this.$message("添加成功")
            let filters = this.$refs.searchUser.getFilters()
            this.getUserList(filters)
          }, error => {
            console.log(error)
            if(error.code === 4){
              this.$message("该用户名已存在！")
            }else if(error.code === 7){
              this.$message("该器官终审人已存在！")
            }else {
              this.$message("添加失败")
            }
          })
        } else {
          this.$message("请补全数据!")
        }
      }
    },
    components: {
      InsertUser,
      SearchUser,
      EditorUser,
      QrcodeVue,
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
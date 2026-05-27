<template>
  <div class="select">
    <el-row class="select-head">
      <el-col :span="16" align="left"><span class="select-title">疾病选择</span></el-col>
      <el-col style="position: absolute;">
        <el-button  style="margin-top: 5vh;margin-left: 1.8vw" type="primary" @click='backEditLog'>查询修改日志
        </el-button>
      </el-col>
    </el-row>
    <div class="select-content" align="center">
      <div class="select-main">
        <div class="row">
          <!-- <div class="cellBox">
            <a class="cell-href" href="#" @click="OrganSelect('10000004')">
              <div class="cellContent">
                <div class="cellMain">
                  <p class="title" ref="title">MAS</p>
                </div>
              </div>
            </a>
          </div> -->
          <div class="cellBox">
            <a class="cell-href" href="#" @click="OrganSelect('10000006')">
              <div class="cellContent">
                <div class="cellMain" style="margin-top: 4vh;">
                  <p class="title" ref="title">家族性矮小/遗传性骨病<br/>队列注册登记观察研究 </p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox" >
            <a class="cell-href" href="#" @click="OrganSelect('10000002')"  >
              <div class="cellContent">
                <div class="cellMain">
                  <p class="title">矮小症 </p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox">
            <a class="cell-href" href="#" @click="OrganSelect('10000003')">
              <div class="cellContent">
                <div class="cellMain">
                  <p class="title" ref="title">性早熟</p>
                </div>
              </div>
            </a>
          </div>
          
        </div>
        <div class="row">
          <div class="cellBox" >
            <a class="cell-href" href="#" @click="OrganSelect('10000001')">
              <div class="cellContent">
                <div class="cellMain">
                  <p class="title" ref="title">性发育异常</p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox" >
            <a class="cell-href" href="#" @click="OrganSelect('10000007')">
              <div class="cellContent">
                <div class="cellMain">
                  <p class="title" ref="title">E路童萌</p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox">
            <a class="cell-href" href="#" @click="OrganSelect('10000005')">
              <div class="cellContent">
                <div class="cellMain" style="margin-top: 4vh;">
                  <p class="title" ref="title">小于胎龄儿（SGA）<br/>学龄前随访队列</p>
                </div>
              </div>
            </a>
          </div>
        </div>
        <div class="row">
          <div class="cellBox" >
            <a class="cell-href" href="#" @click="OrganSelect('10000008')">
              <div class="cellContent">
                <div class="cellMain" style="margin-top: 4vh;">
                  <p class="title">高危新生儿（HRI）<br/>生长发育监测队列研究</p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox" >
            <a class="cell-href" href="#" @click="OrganSelect('10000009')">
              <div class="cellContent">
                <div class="cellMain"  style="margin-top: 4vh;">
                  <p class="title">基于人机结合的矮小<br/>诊疗标准构建及其算法设计</p>
                </div>
              </div>
            </a>
          </div>
          <div class="cellBox" >
            <div class="cell-href" style="height: 100%;" @click="OrganSelectT('10000010')">
              <div class="cellContent">
                <div class="cellMain" >
                  <p class="title">天元公学</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- <div class="row">
          
        </div> -->
      </div>
    </div>
  </div>
</template>

<script>

  export default {
    name: "diseaseSelect",
    data() {
      return {
        organs : [],
        pointerEvents : 'none',
      }
    },

    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      OrganSelect(organ) {
        //存储疾病字段
        sessionStorage.setItem('OrganSelect', organ);
        // this.$router.push({name: 'editor', query: {organ: organ}}) //带参跳转
        //跳转首页
        this.$router.push({name: 'home'})
      },
      //天元公学跳转页面
      OrganSelectT(organ) {
        //存储疾病字段
        sessionStorage.setItem('OrganSelect', organ);
        // this.$router.push({name: 'editor', query: {organ: organ}}) //带参跳转
        //跳转首页
        this.$router.push({name: 'tianYuan'})
      },
      backEditLog(){
        this.$router.push({name: 'editLog'})
      }
    }
  }
</script>

<style lang="less">
  .select-head {
    height: 5vh;
    background: #ffffff;
  }

  .select-title {
    margin-left: 2vw;
    font-size: 1.5vw;
    color: rgb(64, 158, 255);
  }

  .select-left {
    padding-left: 2vw;
  }

  .select-content {
    width: 100%;
    margin: 0;
    padding: 0;

    .row {
      padding-top: 8vh;
      margin: 0px;
      width: 100%;
      display: flex;
    }
  }

  .select-main {
    width: 80%;
  }

  .cellBox {
    width: 33%;
    height: 18vh;
  }

  .cell-href {
    text-decoration: none;
  }

  .cellContent {
    width: 80%;
    height: 100%;
    border: #333333 1px solid;
  }

  .stop_cellContent {
    width: 80%;
    height: 100%;
    border: rgb(204, 204, 204) 1px solid;
  }

  .circle {
    border-radius: 50%;
    width: 14vh;
    height: 14vh;
    background-color: #1daaf1;
    position: relative;
    top: -25vh;
  }

  .stop_circle {
    border-radius: 50%;
    width: 14vh;
    height: 14vh;
    background-color: rgb(204, 204, 204);
    position: relative;
    top: -25vh;
  }

  .cellMain {
    margin-top: 8vh;

    .title {
      margin: 0;
      padding: 0;
      font-size: 1.5vw;
      color: #6cb2f8;
    }

    .stop_title {
      margin: 0;
      padding: 0;
      font-size: 1.5vw;
      color: #333333;
    }

    .number {

    }

    .stop_number {
      color: #333333;
    }
  }


</style>
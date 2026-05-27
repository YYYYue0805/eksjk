<template>
    <div class="StudentEditor">
      <el-row class="StudentEditor-head">
        <el-col :span="8" align="left" style="padding-top: 1vh;">
         <!--  <el-button size='mini' style="margin-left: 1vw"
                     @click='addData' type="primary">保存
          </el-button> -->
          <!-- <el-button v-if="$route.query.userNum == undefined" size='mini' @click="resetForm" type="primary">清空</el-button> -->
        </el-col>
        <el-col :span="8" align="center" style="padding-top: 1vh">
          <span class="StudentEditor-title" v-if="this.$route.query.queryId">查看天元公学数据</span>
          <span class="StudentEditor-title" v-else>新增病例</span>
        </el-col>
        <el-col :span="8" align="right" style="padding-top: 1vh;">
          <el-button size='mini' style="margin-right: 1vw" type="primary" @click='backHistory'>返回</el-button>
        </el-col>
      </el-row>
  
      <el-row class="StudentEditor-content">
          <div class="StudentEditor-right">
            <tygx ref="tygx"
                 :isStatic="true"
                 @resetFormLeft="resetFormLeft"
                 :disClass="ruleForm.disClass"
                 :queryId="queryId"
                 :caseNum="ruleForm.case_num"
                 :sex="ruleForm.sex"
                 :birthTime="ruleForm.birthTime"
                 v-if="$route.query.organ == '10000010' || $route.query.disClass == '10000010'"
            ></tygx>
          </div>
      </el-row>
    </div>
  </template>
  
  <script>
    import tygx from "./common/TYGX"
    import request from "../script/request";
    import { regionDataPlus } from "element-china-area-data";
    import {ICDDataArray}  from '../utils/ICDData';  
    export default {
      name: 'StudentEditor',
      components: {tygx},
      mounted() {
        this.allICDData=ICDDataArray
        // console.log(this.allICDData);
      },
      data() {
        return {
          genData:null,
          numberData:{},
          patientArray:[],
          dialogTableVisible:false,
          allICDData: '',//国际疾病数组
          queryId:'',
          cleared: false,
          ruleForm: {
            disClass:'',
            queryId:'',
            case_num:'',
            userNum: '',
            name: '',
            sex: '',
            gonadalSex: '',
            firVisTime:'',
            AGEy:'',
            AGEm:'',
            birthTime:'',
            chiCom:'',
            natPla: [],
            FHt:'',
            MHt:'',
            familyHis:'',
            gesWeek:'',
            BWt:'',
            BL:'',
            cesaSec:'',
            fetProHis:'',
            oldHis:'',
            card: '',
            familyAdress: '',
            contactsName: '',
            relation: '',
            contactsNum: '',
  
            cesaAsphyxia: '',
            medrecNum: '',
  
            enNum: '',
            enTime: '',
            hosName: '',
            firVisAge: '',
            oneTime: '',
            parity: '',
            proNum: '',
            preInf: '',
            ICD:'',//国际疾病分类
          },
          restaurants: [{ "value": ""}],
          options: regionDataPlus,
          pickerOptions: {
            disabledDate(time) {
              return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
            },
          },
        }
      },
      activated() {
        if (this.$route.query.organ) {
          for (let key in this.ruleForm) {
            this.ruleForm[key] = ''
          }
          this.ruleForm.natPla =[];
          this.ruleForm.disClass = this.$route.query.organ;
        } else {
          this.queryId = this.$route.query.queryId;
          this.ruleForm.disClass = this.$route.query.disClass;
          // this.getPatientInfo()
        }
      },
      watch: {
        'ruleForm.queryId'() {
          // this.getPatientInfo()
        },
      },
      methods: {
        handleGenData(data) {
          // 检查数据是否存在且是数组
          if (data && Array.isArray(data)) {
            // 提取每个对象的 Rna 属性，过滤掉可能为undefined的值
            const rnaList = data.map(item => item.genName).filter(Boolean);
            
            // 用逗号拼接成字符串
            const rnaStr = rnaList.join(',');
            
            // console.log('处理后的Rna字符串:', rnaStr);
            
            // 可以将结果保存到父组件的数据中
            this.genData = rnaStr;
          } else {
            // console.log('接收的数据格式不正确');
            this.genData = '';
          }
        },
        formatDate(dateString) {
          if (!dateString) return "";
          const date = new Date(dateString);
          if (isNaN(date.getTime())) return "Invalid Date";
          return date.toLocaleString();
        },
        backHistory() {
          this.$router.push({name: 'tianYuan'})
          this.numberData=[]
        },
  
        querySearchAsync(queryString, cb) {
          this.restaurants = [];
          let userNum = this.ruleForm.userNum;
          let disClass = this.ruleForm.disClass;
          request.getIdList({userNum,disClass}, data => {
            if (data.length > 0) {
              for (let i = 0; i < data.length; i++) {
                this.restaurants[i] = data[i];
              }
              this.restaurants.forEach(item => {
                item.value = item.user_num;
              })
              this.numberData=data
              cb(this.restaurants);
            }else {
              this.restaurants = [];  
              this.numberData=[];
              cb(this.restaurants);
            }
          })
        },
        handleSelect(item) {
          const userNum = item.user_num;
          request.getPatientById({userNum}, data => {
            // console.log(data.ICD);
            this.ruleForm.ICD = data.ICD;
            this.ruleForm.disClass = data.dis_class;
            this.ruleForm.name = data.name;
            this.ruleForm.sex = data.sex;
            this.ruleForm.gonadalSex = data.gonadal_sex;
            this.ruleForm.firVisTime = data.fir_vis_time;
            this.ruleForm.AGEy = data.AGEy;
            this.ruleForm.AGEm = data.AGEm;
            this.ruleForm.birthTime = data.birth_time;
            this.ruleForm.chiCom = data.chi_com;
            if (data.nat_pla) {
              let province = data.nat_pla.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
              this.ruleForm.natPla = province.split(',');
            }
            this.ruleForm.FHt = data.FHt;
            this.ruleForm.MHt = data.MHt;
            this.ruleForm.familyHis = data.family_his;
            this.ruleForm.gesWeek = data.ges_week;
            this.ruleForm.BWt = data.BWt;
            this.ruleForm.BL = data.BL;
            this.ruleForm.cesaSec = data.cesa_sec;
            this.ruleForm.fetProHis = data.fet_pro_his;
            this.ruleForm.oldHis = data.past_his;
            this.ruleForm.card = data.card;
            this.ruleForm.familyAdress = data.fam_adr;
            this.ruleForm.contactsName = data.contacts_name;
            this.ruleForm.relation = data.relation;
            this.ruleForm.contactsNum = data.contacts_num;
            this.ruleForm.case_num = data.case_num;
            this.ruleForm.cesaAsphyxia = data.cesa_asphyxia;
            this.ruleForm.medrecNum = data.medrec_num;
            this.ruleForm.enNum = data.enrollment_num;
            if(data.enrollment_time){
              this.ruleForm.enTime = data.enrollment_time.substring(0,10);
            }
            this.ruleForm.hosName = data.hospital_name;
            this.ruleForm.firVisAge = data.fir_vis_age;
            this.ruleForm.oneTime = data.one_time;
            this.ruleForm.parity = data.parity;
            this.ruleForm.proNum = data.pronum;
            this.ruleForm.preInf = data.pregnancy_infection;
  
            this.ruleForm.queryId = data.id;
            this.ruleForm.queryUId = data.id;
            this.queryId = data.id;
          }, error => {
            console.log(error)
            this.ruleForm.userNum = item.user_num;
          })
        },
        handleClick(data){
          this.openForm(data)
        },
        //失去焦点事件
        handleBlur(){
            this.$refs.autocompleteRef.suggestions = [];
            this.$refs.autocompleteRef.highlightedIndex = -1;
          // console.log('失去焦点事件')
          // console.log(this.numberData,"this.numberData")
          if(this.numberData.length>0){
            this.numberOpen()
          }
        },
        //失去焦点弹窗
        numberOpen() {
          this.$confirm('有相同病历号,是否填写新的病历号?', '提示', {
            showClose:false,
            closeOnClickModal:false,
            closeOnPressEscape:false,
            confirmButtonText: '否',
            cancelButtonText: '合并病历号',
            type: 'warning',
            cancelButtonClass:'cancelButtonClass'
          }).then(() => {
            /* this.$message({
              type: 'success',
              message: '删除成功!'
            }); */
            // console.log("填写新的病历号")
          }).catch(() => {
            /* this.$message({
              type: 'info',
              message: '合并病历号'
            });  */      
            // this.handleSelect(this.numberData[0])   
            /* const userNum = this.numberData[0].user_num;
            let fromData={}
            request.getPatientById({userNum}, data => {
          
              fromData.user_num=data.user_num,
              fromData.name=data.name,
              fromData.sex=data.sex,
              fromData.birthTime=data.birth_time
              this.openForm(fromData)
            }) */
            let userNum = this.ruleForm.userNum;
            let disClass = this.ruleForm.disClass;
            request.getIdList({userNum,disClass}, data => {
              /* if (data.length > 0) {
                for (let i = 0; i < data.length; i++) {
                  fromData.user_num=data[i].user_num,
                  fromData.name=data[i].name,
                  fromData.sex=data[i].sex,
                  fromData.birthTime=data[i].birth_time
                }
              } */
              // this.openForm(data)
              // console.log(data)
              this.patientArray=data
              this.dialogTableVisible=true
            })
          });
          },
          openForm(data){
            // console.log(data,"data")
            this.$alert(`<div class='openForm'>                   
                            <p>病历号：${data.user_num}</p>
                            <p>姓名：${data.name}</p>
                            <p>性别：${data.sex==1?'男':'女'}</p>
                            <p>出生日期：${data.birth_time}</p>
                        </div>`, '请核对病历号信息', {
            dangerouslyUseHTMLString: true,
            showCancelButton:true,
            confirmButtonText: '合并',
            cancelButtonText: '取消合并',
          }).then(() => {
            this.handleSelect(data) 
            this.ruleForm.userNum=data.user_num
            this.dialogTableVisible=false
            this.$message({
              type: 'success',
              message: '合并成功'
            })
          }).catch(() => {
            // console.log("取消合并")
          });
          },
        addData() {
          let data1 = this.ruleForm;
          let data2;
          if (this.ruleForm.disClass === '10000001') {
            data2 =  this.$refs.dsd.getData()
          } else if (this.ruleForm.disClass === '10000002') {
            data2 = this.$refs.fss.addData()
          } else if (this.ruleForm.disClass === '10000003') {
            data2 = this.$refs.cpp.addData()
          } else if (this.ruleForm.disClass === '10000004') {
            data2 = this.$refs.mas.getData()
          }else if (this.ruleForm.disClass === '10000005') {
            data2 = this.$refs.sga.addData()
          }else if (this.ruleForm.disClass === '10000006') {
            data2 = this.$refs.sss.addData()
          } 
          const dataForm = Object.assign(data1, data2);
          request.addCase(dataForm, data => {
              this.ruleForm.case_num = data.case_num;
              this.ruleForm.queryId = data.id;
              this.queryId = data.id;
              this.$message({
                message: '保存成功',
                type: 'success'
              });
            }, error => {
              console.log(error.data)
              this.$message('保存失败！');
            })
        },
        // getPatientInfo(){
        //   let queryId = this.$route.query.queryId;
        //   request.getStudent({queryId} ,data =>{
        //     // console.log(data.ICD);
        //     this.ruleForm.ICD = data.ICD;
        //     this.ruleForm.disClass = data.dis_class;
        //     this.ruleForm.name = data.name;
        //     this.ruleForm.sex = data.sex;
        //     this.ruleForm.gonadalSex = data.gonadal_sex;
        //     if(data.fir_vis_time){
        //       this.ruleForm.firVisTime = data.fir_vis_time.substring(0,10);
        //     }
        //     this.ruleForm.AGEy = data.AGEy;
        //     this.ruleForm.AGEm = data.AGEm;
        //     if(data.birth_time){
        //       this.ruleForm.birthTime = data.birth_time.substring(0,10);
        //     }
        //     this.ruleForm.chiCom = data.chi_com;
        //     if (data.nat_pla) {
        //       let province = data.nat_pla.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
        //       this.ruleForm.natPla = province.split(',');
        //     }
        //     this.ruleForm.FHt = data.FHt;
        //     this.ruleForm.MHt = data.MHt;
        //     this.ruleForm.familyHis = data.family_his;
        //     this.ruleForm.gesWeek = data.ges_week;
        //     this.ruleForm.BWt = data.BWt;
        //     this.ruleForm.BL = data.BL;
        //     this.ruleForm.cesaSec = data.cesa_sec;
        //     this.ruleForm.fetProHis = data.fet_pro_his;
        //     this.ruleForm.oldHis = data.past_his;
        //     this.ruleForm.card = data.card;
        //     this.ruleForm.familyAdress = data.fam_adr;
        //     this.ruleForm.contactsName = data.contacts_name;
        //     this.ruleForm.relation = data.relation;
        //     this.ruleForm.contactsNum = data.contacts_num;
        //     this.ruleForm.case_num = data.case_num;
        //     this.ruleForm.cesaAsphyxia = data.cesa_asphyxia;
        //     this.ruleForm.medrecNum = data.medrec_num;
        //     this.ruleForm.userNum = data.user_num;
        //     this.ruleForm.enNum = data.enrollment_num;
        //     if(data.enrollment_time){
        //       this.ruleForm.enTime = data.enrollment_time.substring(0,10);
        //     }
        //     this.ruleForm.hosName = data.hospital_name;
        //     this.ruleForm.firVisAge = data.fir_vis_age;
        //     this.ruleForm.oneTime = data.one_time;
        //     this.ruleForm.parity = data.parity;
        //     this.ruleForm.proNum = data.pronum;
        //     this.ruleForm.preInf = data.pregnancy_infection;
  
        //     this.ruleForm.queryId = data.id;
        //     this.ruleForm.queryUId = data.id;
        //     this.queryId = data.id;
        //   })
        // },
        getAge() {
            let strDate1 = this.ruleForm.birthTime + "   00:00:00.0";
            let strDate2 = this.ruleForm.firVisTime + "   00:00:00.0";
            strDate1 = strDate1.substring(0, strDate1.lastIndexOf(".")).replace(/-/g, "/ ");
            strDate2 = strDate2.substring(0, strDate2.lastIndexOf(".")).replace(/-/g, "/ ");
            //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
            let date1 = Date.parse(strDate1);
            let date2 = Date.parse(strDate2);
            let day = Math.ceil((date2 - date1) / (60 * 60 * 1000 * 24));
            let year = Math.floor(day / 365);
            let y = day % 365;
            let month = Math.floor(y / 30);
  
            if (year > 0) {
              this.ruleForm.AGEy = year
            } else {
              this.ruleForm.AGEy = 0
            }
            if (month > 0) {
              this.ruleForm.AGEm = month
            } else {
              this.ruleForm.AGEm = 0
            }
          },
        resetForm() {
          if(this.$route.query.organ == '10000010' ){
            this.$refs.tygx.resetForm()
          }
        },
        resetFormLeft() {
           this.ruleForm.ICD = '';
          // this.ruleForm.disClass = '';
          this.ruleForm.name = '';
          this.ruleForm.sex = '';
          this.ruleForm.gonadalSex = '';
          this.ruleForm.firVisTime = '';
          this.ruleForm.AGEy = '';
          this.ruleForm.AGEm ='';
          this.ruleForm.birthTime = '';
          this.ruleForm.chiCom = '';
          this.ruleForm.natPla = [];
          this.ruleForm.FHt = '';
          this.ruleForm.MHt = '';
          this.ruleForm.familyHis = '';
          this.ruleForm.gesWeek ='';
          this.ruleForm.BWt = '';
          this.ruleForm.BL = '';
          this.ruleForm.cesaSec = '';
          this.ruleForm.fetProHis = '';
          this.ruleForm.oldHis = '';
          this.ruleForm.card = '';
          this.ruleForm.familyAdress = '';
          this.ruleForm.contactsName = '';
          this.ruleForm.relation = '';
          this.ruleForm.contactsNum = '';
          this.ruleForm.case_num = '';
          this.ruleForm.cesaAsphyxia ='';
          this.ruleForm.medrecNum = '';
          this.ruleForm.enNum = '';
          this.ruleForm.enTime = '';
          this.ruleForm.hosName = '';
          this.ruleForm.firVisAge = '';
          this.ruleForm.oneTime = '';
          this.ruleForm.parity = '';
          this.ruleForm.proNum = '';
          this.ruleForm.preInf = '';
          this.ruleForm.userNum ='';
          this.ruleForm.queryUId = '';
          // this.queryId = '';
        }
      }
    }
  </script>
  
  <style lang="less">
   //合并弹窗取消按钮样式修改
   .el-message-box{
            .el-message-box__btns{
              ::v-deep .cancelButtonClass{
                background-color: orange !important;
              }
          }
        }
    .StudentEditor {
      .StudentEditor-head {
        min-height: 6vh;
        background: #ffffff;
      }
  
      .StudentEditor-title {
        font-size: 3vh;
        color: rgb(64, 158, 255);
      }
  
      .StudentEditor-left {
        background-color: rgb(236, 245, 255);
  
        .el-scrollbar {
          height: 85vh;
          width: 100%;
        }
  
        .newItem .el-form-item__label {
          font-size: 0.8vw;
        }
      }
  
      .StudentEditor-right {
        height: 85vh;
        overflow: hidden;
      }
  
      .detail-reason {
        padding-left: 10vw;
        font-size: 1.3vw;
        color: red;
      }
  
      .line {
        width: 100%;
        height: 1px;
        background-color: rgb(71, 201, 255);
      }
  
      .row {
        display: flex;
        justify-content: space-between;
        padding-right: 15px;
        height: 5vh;
        align-items: center;
  
        .el-form-item {
          margin: 0px;
          width: 100%;
        }
  
        .el-select {
          width: 100%;
        }
      }
  
      .thyroid {
        height: 97%;
  
        .el-scrollbar {
          height: 100%;
          margin-left: 2vh;
          padding: 10px 0 10px 10px;
          background-color: #ffffff;
        }
  
        .scrollThy .el-scrollbar__wrap {
          overflow-x: hidden;
        }
  
        .thyroid-title {
          font-size: 1vw;
        }
  
        .red {
          color: red;
        }
  
        .div-box {
          width: 95%;
          align-items: center;
          padding: 1rem 0;
          border: 1px solid #ccc;
          border-radius: 0.2rem;
          padding-left: 1vh;
        }
  
        .input-underLine {
          width: 4vw;
          border: 0;
          border-bottom: 1px blueviolet solid;
          outline: none;
          text-align: center
        }
  
        .edit-select {
          margin-right: 1vw;
          width: 7vw;
          border: 1px blueviolet solid;
          outline: none;
          border-radius: 5px;
          text-align: center
        }
  
      }
  
    }
  </style>
<template>
  <div class="detail">
    <el-row class="detail-head">
      <el-col :span="18">
        <div style="width: 100%;display: flex" align="left">
          <div style="width: 20%">
            <span class="detail-title">详情</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6" align="right">
        <el-button size='mini' style="margin-right: 1vw" type="primary" @click='backHistory'>返回</el-button>
      </el-col>
    </el-row>

    <el-container style="height: 86vh">
      <el-aside style="width: 22% ; margin-left: 10px; background-color: #ffffff">
        <div v-if="$route.query.disClass == '10000001'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
              <tr>
                <td>性腺性别：</td>
                <td> {{ ruleForm.gonadalSex }}</td>
              </tr>

            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
<!--              <tr>-->
<!--                <td style="width: 6vw">病历号：</td>-->
<!--                <td> {{ ruleForm.medrecNum }}</td>-->
<!--              </tr>-->
              <tr>
                <td style="width: 6vw">初诊时间：</td>
                <td> {{ ruleForm.firVisTime }}</td>
              </tr>
              <tr>
                <td>出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>年龄：</td>
                <td> {{ ruleForm.AGEy }} 岁 {{ ruleForm.AGEm }} 月</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">主诉：</td>
                <td> {{ ruleForm.chiCom }}</td>
              </tr>
              <tr>
                <td>籍贯：</td>
                <td> {{ ruleForm.natPla }}</td>
              </tr>
              <tr>
                <td>父亲身高：</td>
                <td> {{ ruleForm.FHt }}cm</td>
              </tr>
              <tr>
                <td>母亲身高：</td>
                <td> {{ ruleForm.MHt }}cm</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">家族史：</td>
                <td> {{ ruleForm.familyHis }}</td>
              </tr>
              <tr>
                <td>胎龄周：</td>
                <td v-if="ruleForm.gesWeek === '足月'">{{ ruleForm.gesWeek }} </td>
                <td v-else> {{ ruleForm.gesWeek }}周</td>
              </tr>
              <tr>
                <td>出生体重：</td>
                <td> {{ ruleForm.BWt }}kg</td>
              </tr>
              <tr>
                <td>出生身长：</td>
                <td> {{ ruleForm.BL }}cm</td>
              </tr>
              <tr>
                <td>出生方式：</td>
                <td> {{ ruleForm.cesaSec }}</td>
              </tr>
              <tr>
                <td>保胎史：</td>
                <td> {{ ruleForm.fetProHis }}</td>
              </tr>
              <tr>
                <td>既往史：</td>
                <td> {{ ruleForm.oldHis }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">身份证号码：</td>
                <td> {{ ruleForm.card }}</td>
              </tr>
              <tr>
                <td>家庭地址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
              <tr>
                <td>联系人姓名：</td>
                <td> {{ ruleForm.contactsName }}</td>
              </tr>
              <tr>
                <td>与患者关系：</td>
                <td> {{ ruleForm.relation }}</td>
              </tr>
              <tr>
                <td>联系电话：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>

        <div v-else-if="$route.query.disClass == '10000002'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>初次就诊时间：</td>
                <td> {{ firVisTime }}</td>
              </tr>
              <tr>
                <td>初诊年龄：</td>
                <td> {{ morbidAge }}</td>
              </tr>
              <tr>
                <td>主诉：</td>
                <td> {{ chiefCom }}</td>
              </tr>
              <tr>
                <td>主要诊断：</td>
                <td> {{ mainDia }}</td>
              </tr>
              <tr>
                <td>次要诊断：</td>
                <td> {{ secDia }}</td>
              </tr>
              <tr>
                <td>是否治疗：</td>
                <td> {{ diaPlan }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">身份证号码：</td>
                <td> {{ ruleForm.card }}</td>
              </tr>
              <tr>
                <td>家庭地址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
              <tr>
                <td>联系人姓名：</td>
                <td> {{ ruleForm.contactsName }}</td>
              </tr>
              <tr>
                <td>与患者关系：</td>
                <td> {{ ruleForm.relation }}</td>
              </tr>
              <tr>
                <td>联系电话：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>

        <div v-else-if="$route.query.disClass == '10000003'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>年龄：</td>
                <td> {{ ruleForm.AGEy }} 岁 {{ ruleForm.AGEm }} 月</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">身份证号码：</td>
                <td> {{ ruleForm.card }}</td>
              </tr>
              <tr>
                <td>家庭地址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
              <tr>
                <td>联系人姓名：</td>
                <td> {{ ruleForm.contactsName }}</td>
              </tr>
              <tr>
                <td>与患者关系：</td>
                <td> {{ ruleForm.relation }}</td>
              </tr>
              <tr>
                <td>联系电话：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>

        <div v-else-if="$route.query.disClass == '10000004'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">所在中心：</td>
                <td> {{ ruleForm.hosName }}</td>
              </tr>
              <tr>
                <td>入组时间：</td>
                <td> {{ ruleForm.enTime }}</td>
              </tr>
              <tr>
                <td>入组序号：</td>
                <td> {{ ruleForm.enNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>年龄：</td>
                <td> {{ ruleForm.AGEy }} </td>
              </tr>
              <tr>
                <td>性别：</td>
                <td> {{ ruleForm.sex }} </td>
              </tr>
              <tr>
                <td>出生地：</td>
<!--                <el-cascader size="small" style="width: 100%" :options="options" v-model="ruleForm.natPla"></el-cascader>-->
                <td>{{ ruleForm.natPla }} </td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">确诊时间：</td>
                <td> {{ ruleForm.firVisTime }}</td>
              </tr>
              <tr>
                <td>确诊年龄：</td>
                <td> {{ ruleForm.firVisAge }}</td>
              </tr>
              <tr>
                <td>联系方式：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
              <tr>
                <td>首次提交时间：</td>
                <td> {{ ruleForm.oneTime }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">孕周：</td>
                <td> {{ ruleForm.gesWeek }}</td>
              </tr>
              <tr>
                <td>胎次：</td>
                <td> {{ ruleForm.parity }}</td>
              </tr>
              <tr>
                <td>产次：</td>
                <td> {{ ruleForm.proNum }}</td>
              </tr>
              <tr>
                <td>出生体重：</td>
                <td> {{ ruleForm.BWt }}</td>
              </tr>
              <tr>
                <td>出生身长：</td>
                <td> {{ ruleForm.BL }}</td>
              </tr>
              <tr>
                <td>孕期感染：</td>
                <td> {{ ruleForm.preInf }}</td>
              </tr>
              <tr>
                <td>分娩方式：</td>
                <td> {{ ruleForm.cesaSec }}</td>
              </tr>
              <tr>
                <td>窒息史：</td>
                <td> {{ ruleForm.cesaAsphyxia }}</td>
              </tr>
              <tr>
                <td>既往史：</td>
                <td> {{ ruleForm.oldHis }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>
        <div v-else-if="$route.query.disClass == '10000005'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>初次就诊时间：</td>
                <td> {{ firVisTime }}</td>
              </tr>
              <tr>
                <td>初诊年龄：</td>
                <td> {{ morbidAge }}</td>
              </tr>
              <tr>
                <td>主诉：</td>
                <td> {{ chiefCom }}</td>
              </tr>
              <tr>
                <td>主要诊断：</td>
                <td> {{ mainDia }}</td>
              </tr>
              <tr>
                <td>次要诊断：</td>
                <td> {{ secDia }}</td>
              </tr>
              <tr>
                <td>是否治疗：</td>
                <td> {{ diaPlan }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">身份证号码：</td>
                <td> {{ ruleForm.card }}</td>
              </tr>
              <tr>
                <td>家庭地址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
              <tr>
                <td>联系人姓名：</td>
                <td> {{ ruleForm.contactsName }}</td>
              </tr>
              <tr>
                <td>与患者关系：</td>
                <td> {{ ruleForm.relation }}</td>
              </tr>
              <tr>
                <td>联系电话：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>
        <div v-else-if="$route.query.disClass == '10000006'">
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td>病历号：</td>
                <td> {{ ruleForm.userNum }}</td>
              </tr>
              <tr>
                <td>患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td>国际疾病分类：</td>
                <td> {{ ICD }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td>初次就诊时间：</td>
                <td> {{ firVisTime }}</td>
              </tr>
              <tr>
                <td>初诊年龄：</td>
                <td> {{ morbidAge }}</td>
              </tr>
              <tr>
                <td>主诉：</td>
                <td> {{ chiefCom }}</td>
              </tr>
              <tr>
                <td>主要诊断：</td>
                <td> {{ mainDia }}</td>
              </tr>
              <tr>
                <td>次要诊断：</td>
                <td> {{ secDia }}</td>
              </tr>
              <tr>
                <td>是否治疗：</td>
                <td> {{ diaPlan }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">身份证号码：</td>
                <td> {{ ruleForm.card }}</td>
              </tr>
              <tr>
                <td>家庭地址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
              <tr>
                <td>联系人姓名：</td>
                <td> {{ ruleForm.contactsName }}</td>
              </tr>
              <tr>
                <td>与患者关系：</td>
                <td> {{ ruleForm.relation }}</td>
              </tr>
              <tr>
                <td>联系电话：</td>
                <td> {{ ruleForm.contactsNum }}</td>
              </tr>
            </table>
          </div>
          <div class="detail-left" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">病例编号：</td>
                <td> {{ ruleForm.case_num }}</td>
              </tr>
            </table>
          </div>
        </div>
        <div v-else-if="$route.query.disClass == '10000007'">
          <div class="title">人口学信息</div>
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">患者姓名：</td>
                <td> {{ ruleForm.name }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">年龄：</td>
                <td> {{ ruleForm.age }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">性别：</td>
                <td> {{ ruleForm.sex }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">民族：</td>
                <td> {{ ruleForm.ethnicity }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">出生日期：</td>
                <td> {{ ruleForm.birthTime }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">籍贯：</td>
                <td> {{ ruleForm.natPla }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">家庭住址：</td>
                <td> {{ ruleForm.familyAdress }}</td>
              </tr>
            </table>
          </div>
          <div class="title">病史</div>
          <div class="detail-left_one" align="left">
            <table class="astable">
              <tr>
                <td style="width: 6vw">现病史：</td>
                <td> {{ ruleForm.nowMedHis }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">家族史：</td>
                <td> {{ ruleForm.familyHis }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">出生身长：</td>
                <td> {{ ruleForm.BL }}</td>cm
              </tr>
              <tr>
                <td style="width: 6vw">出生体重：</td>
                <td> {{ ruleForm.BWt }}</td>kg
              </tr>
              <tr>
                <td style="width: 6vw">出生胎龄：</td>
                <td> {{ ruleForm.birthAge }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">出生胎次：</td>
                <td> {{ ruleForm.parity }}</td>
              </tr>
              <tr>
                <td style="width: 6vw">出生产次：</td>
                <td> {{ ruleForm.proNum }}</td>
              </tr>
            </table>
          </div>
        </div>
      </el-aside>

      <el-container direction="vertical">
        <el-main style="padding: 0px;overflow: hidden;">
          <dsd v-if="$route.query.disClass == '10000001'"
               ref="dsd"
               :disClass="ruleForm.disClass"
               :queryId="$route.query.queryId"
               :birthTime="$route.query.birthTime"
               :sex="sex"
               :isStatic="true"
               :default="$route.query.follow"
          ></dsd>
          <fss ref="fss"
               :gesWeek="ruleForm.gesWeek"
               :BWt="ruleForm.BWt"
               :BL="ruleForm.BL"
               :cesaSec="ruleForm.cesaSec"
               :cesaAsphyxia="ruleForm.cesaAsphyxia"
               @dataList="getData"
               :disClass="ruleForm.disClass"
               :queryId="$route.query.queryId"
               :caseNum="ruleForm.case_num"
               :sex="sex"
               :isStatic="true"
               :birthTime="$route.query.birthTime"
               :default="$route.query.follow"
               v-if="$route.query.disClass == '10000002'"
          ></fss>
          <cpp ref="cpp"
               :disClass="ruleForm.disClass"
               :queryId="$route.query.queryId"
               :sex="sex"
               :isStatic="true"
               :default="$route.query.follow"
               :birthTime="$route.query.birthTime"
               v-if="$route.query.disClass == '10000003'"
          ></cpp>
          <mas ref="mas"
               :isStatic="true"
               :queryId="$route.query.queryId"
               :disClass="ruleForm.disClass"
               :sex="sex"
               v-if="$route.query.disClass == '10000004'"
          ></mas>
          <sga ref="sga"
               :gesWeek="ruleForm.gesWeek"
               :BWt="ruleForm.BWt"
               :BL="ruleForm.BL"
               :cesaSec="ruleForm.cesaSec"
               :cesaAsphyxia="ruleForm.cesaAsphyxia"
               @dataList="getData"
               :disClass="ruleForm.disClass"
               :queryId="$route.query.queryId"
               :caseNum="ruleForm.case_num"
               :sex="sex"
               :isStatic="true"
               :birthTime="$route.query.birthTime"
               :default="$route.query.follow"
               v-if="$route.query.disClass == '10000005'"
          ></sga>
          <sss ref="sss"
               :gesWeek="ruleForm.gesWeek"
               :BWt="ruleForm.BWt"
               :BL="ruleForm.BL"
               :cesaSec="ruleForm.cesaSec"
               :cesaAsphyxia="ruleForm.cesaAsphyxia"
               @dataList="getData"
               :disClass="ruleForm.disClass"
               :queryId="$route.query.queryId"
               :caseNum="ruleForm.case_num"
               :sex="sex"
               :isStatic="true"
               :birthTime="$route.query.birthTime"
               :default="$route.query.follow"
               v-if="$route.query.disClass == '10000006'"
          ></sss>
          <eltm ref="eltm"
                :disClass="ruleForm.disClass"
                :queryId="queryId"
                :caseNum="ruleForm.case_num"
                :sex="ruleForm.sex"
                @resetFormLeft="resetFormLeft"
                :birthTime="ruleForm.birthTime"
                v-if="$route.query.organ == '10000007' || $route.query.disClass == '10000007'"
          ></eltm>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  import request from '../script/request'
  // import ImageUpload from "./imageViewer/ImageUpload";
  import slectCount from "../script/selectCount";
  import dsd from "./common/DSD"
  import fss from "./common/FSS"
  import cpp from "./common/CPP"
  import mas from "./common/MAS"
  import sga from './common/SGA'
  import sss from './common/SSS'
  import eltm from "./common/ELTM"
  import { regionDataPlus, CodeToText } from "element-china-area-data";
  import {ICDDataArray}  from '../utils/ICDData';  
  export default {
    name: 'Detail',
    inject: ['reload'],
    components: {dsd, fss, cpp, mas,sga,sss,eltm},
    data() {
      return {
        imageUrl: [''],
        boneAgeImageUrl:'',
        chromImageUrl:'',
        genImageUrl:'',
        patImageUrl:'',
        sex:'',
        ICD:'',
        ruleForm: {
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
          natPla:'',
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
          medrecNum:'',
          cesaAsphyxia:'',

          enNum:'',
          enTime:'',
          hosName:'',
          firVisAge:'',
          oneTime:'',
          parity:'',
          proNum:'',
          preInf:'',

        },
        caseId:"",
        url: '',
        user: '',
        caozuo: '',
        firVisTime:'',
        morbidAge:'',
        chiefCom:'',
        mainDia:'',
        secDia:'',
        diaPlan:'',
        pointerEvents: "none",
        isStatic: {
          type: Boolean,
          default: false
        },
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
          },
        },
        options: regionDataPlus,
        CodeToText: CodeToText,
      }
    },
    activated() {
      this.imageUrl = [''];
      this.boneAgeImageUrl ='';
      this.chromImageUrl='';
      this.genImageUrl='';
      this.patImageUrl='';
      this.ruleForm.queryId = this.$route.query.queryId;
      this.caozuo = this.$route.query.caozuo;
      this.getCase();
    },
    mounted() {
      this.user = this.$store.state.user.name
    },

    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      getCase() {
        const queryId = this.ruleForm.queryId;
        request.getCase({queryId}, data => {
          this.caseId = data.id;
          this.ruleForm.userNum = data.user_num;
          this.getPatientInfo();
        }, error => {
          console.log(error)
        })
      },

      getPatientInfo() {
        let queryId = this.$route.query.queryId;
        request.getCaseDetail({queryId}, data => {
          // this.ICD=data.ICD;
          // console.log(data);
          if(data.ICD!=null && data.ICD.length>0){
            const foundItem = ICDDataArray.find(item => item.value === data.ICD)
            this.ICD = foundItem.label;
          }else{
            this.ICD=''
          }
          // console.log(data);
          this.ruleForm.case_num = data.case_num;
          this.ruleForm.name = data.name;
          this.ruleForm.sex =(data.sex != null ? (slectCount.sex[data.sex]) : "");
          this.sex = data.sex
          this.ruleForm.gonadalSex = (data.gonadal_sex != null ? (slectCount.gonadalSex[data.gonadal_sex]) : ""),
          this.ruleForm.firVisTime = (data.fir_vis_time != null ? (data.fir_vis_time.substring(0,10)) : ""),
          this.ruleForm.AGEy = data.AGEy;
          this.ruleForm.AGEm = data.AGEm;
          this.ruleForm.birthTime =  (data.birth_time != null ? (data.birth_time.substring(0,10)) : ""),
          this.ruleForm.chiCom = data.chi_com;
          if (data.nat_pla) {
            let province = data.nat_pla.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
            this.ruleForm.natPla = province.split(',');
            let s1 = this.CodeToText[this.ruleForm.natPla[0]];
            let s2 = this.CodeToText[this.ruleForm.natPla[1]];
            let s3 = this.CodeToText[this.ruleForm.natPla[2]];
            this.ruleForm.natPla = s1+ "-" +s2+ "-" +s3
          }else {
            this.ruleForm.natPla = '';
          }
          this.ruleForm.FHt = data.FHt;
          this.ruleForm.MHt = data.MHt;
          if(data.family_his == "1"){
            this.ruleForm.familyHis = "无"
          }else {
            this.ruleForm.familyHis = data.family_his;
          }
          this.ruleForm.gesWeek = data.ges_week;
          this.ruleForm.BWt = data.BWt;
          this.ruleForm.BL = data.BL;
          this.ruleForm.cesaSec = (data.cesa_sec != null ? (slectCount.cesaSec[data.cesa_sec]) : ""),
          this.ruleForm.fetProHis = (data.fet_pro_his != null ? (slectCount.fetProHis[data.fet_pro_his]) : "");
          this.ruleForm.oldHis = data.past_his;
          this.ruleForm.card = data.card;
          this.ruleForm.familyAdress = data.fam_adr;
          this.ruleForm.contactsName = data.contacts_name;
          this.ruleForm.relation = data.relation;
          this.ruleForm.contactsNum = data.contacts_num;
          this.ruleForm.cesaAsphyxia = (data.cesa_asphyxia != null ? (slectCount.asph[data.cesa_asphyxia]) : "");
          this.ruleForm.medrecNum = data.medrec_num;

          this.ruleForm.enNum = data.enrollment_num;
          if(data.enrollment_time){
            this.ruleForm.enTime = data.enrollment_time.substring(0,10);
          }
          this.ruleForm.hosName = data.hospital_name;
          this.ruleForm.firVisAge = data.fir_vis_age;
          if(data.one_time){
            this.ruleForm.oneTime = data.one_time.substring(0,10);
          }
          this.ruleForm.parity = data.parity;
          this.ruleForm.proNum = data.pronum;
          this.ruleForm.preInf = (data.pregnancy_infection != null ? (slectCount.preInf[data.pregnancy_infection]) : "");


        })
      },
      getData(firVisTime,morbidAge,chiefCom,mainDia,secDia,diaPlan){
        this.firVisTime = firVisTime;
        this.morbidAge = morbidAge;
        this.chiefCom = chiefCom;
        if(mainDia){
          let str = JSON.stringify(mainDia)
          this.mainDia = str.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
        }
        this.secDia = secDia;
        if(diaPlan && diaPlan !== '1'){
          this.diaPlan = "已治疗"
        }else {
          this.diaPlan = "未治疗"
        }
      },
    }
  }
</script>

<style lang="less" scoped>
  .detail{
    background: #f0f0f0;
    padding: 10px;
    height: 100%;
  }
  .detail-head {
    min-height: 5vh;
    padding: 0 0 0 10px;
  }
  .detail-title {
    font-size: 1.5vw;
    color: rgb(102, 102, 102);
  }

  .detail-reason {
    font-size: 1.2vw;
    color: rgb(64, 158, 255);
    text-decoration: none;
    margin-left: 8vw;
  }

  .red{
    color: red;
  }
  .detail-left_one{
    padding-left: 2vw;
    background-color: #ecf5ff;
    border: 0;
    border-bottom: #ebeef5 1px solid;
    padding: 24px 0 24px 2vw ;
  }
  .title{
    padding-top: 10px;
    background-color: #ecf5ff;
    font-size: 20px;
  }
  .detail-left{
    padding-left: 2vw;
    background-color: #ffffff;
    border: 0;
    border-bottom: #ebeef5 1px solid;
    padding: 24px 0 24px 2vw ;
  }
  .detail-left_bottom{
    padding-left: 2vw;
    background-color: #ffffff;
    border: 0;
    padding: 24px 0 24px 2vw ;
  }
  .astable{
    width: 100%;
    word-break:break-all;
    font-size: 0.8vw;
    padding: 0;
    margin: 0;
  }
  .astable tr{
    height: 25px;
    width: 100%;
  }
</style>
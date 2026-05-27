<template>
  <div class="editor">
    <el-row class="editor-head">
      <el-col :span="8" align="left" style="padding-top: 1vh;">
        <el-button size='mini' style="margin-left: 1vw"
                   @click='addData' type="primary">保存
        </el-button>
        <el-button v-if="$route.query.userNum == undefined" size='mini' @click="resetForm" type="primary">清空</el-button>
      </el-col>
      <el-col :span="8" align="center" style="padding-top: 1vh">
        <span class="editor-title" v-if="this.$route.query.queryId">修改上传</span>
        <span class="editor-title" v-else>新增病例</span>
      </el-col>
      <el-col :span="8" align="right" style="padding-top: 1vh;">
        <el-button size='mini' style="margin-right: 1vw" type="primary" @click='backHistory'>返回</el-button>
      </el-col>
    </el-row>

    <el-row class="editor-content">
      <el-col :span="8">
        <div class="editor-left">
          <el-scrollbar>
            <el-form v-if="$route.query.organ == '10000001' || $route.query.disClass == '10000001'" ref="ruleForm" :model="ruleForm" label-width="8vw" style="padding-right: 1vh">
              <div class="row chuzhenyiyuan">
                <el-form-item label="病历号：" class="newItem" prop="userNum">
                  <el-autocomplete  ref="autocompleteRef" style="width: 100%" size="small" v-model="ruleForm.userNum"  :trigger-on-focus="false" :fetch-suggestions="querySearchAsync"  @select="handleSelect" @blur="handleBlur"></el-autocomplete>
                </el-form-item>
              </div>
              <div class="row jianchayiyuan">
                <el-form-item label="患者姓名：" class="newItem" prop="name">
                  <el-input size="small" v-model="ruleForm.name"></el-input>
                </el-form-item>
                <el-form-item label="国际疾病分类：" class="newItem" prop="IDE">
                  <el-select style="width: 150px;" size="small" clearable filterable v-model="ruleForm.ICD">
                    <el-option
                      v-for="item in allICDData"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>

<!--              <div class="row">-->
<!--                <el-form-item label="病历号：" class="newItem"  prop="medrecNum">-->
<!--                  <el-input v-model="ruleForm.medrecNum" size="small"></el-input>-->
<!--                </el-form-item>-->
<!--              </div>-->

              <div class="row">
                <el-form-item label="社会性别：" class="newItem" prop="sex">
                  <el-select v-model="ruleForm.sex" size="small">
                    <el-option label="男" value="1"></el-option>
                    <el-option label="女" value="2"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="性腺性别：" class="newItem" prop="gonadalSex">
                  <el-select v-model="ruleForm.gonadalSex" size="small">
                    <el-option label="男" value="1"></el-option>
                    <el-option label="女" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row jianchayiyuan">
                <el-form-item label="初诊时间：" class="newItem" prop="firVisTime">
                  <el-date-picker
                      v-model="ruleForm.firVisTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>

                <el-form-item label="出生日期：" class="newItem" prop="birthTime">
                  <el-date-picker
                      v-model="ruleForm.birthTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      @change="getAge"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="年龄：" class="newItem">
                  <input v-model="ruleForm.AGEy" style="width: 4vw;border: 0;outline: none;" readonly/>岁
                  <input v-model="ruleForm.AGEm" style="width: 4vw;border: 0;outline: none;" readonly/>月
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="主诉：" class="newItem" prop="chiCom">
                  <el-input v-model="ruleForm.chiCom" size="small"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="籍贯：" class="newItem" prop="natPla">
                  <el-cascader size="small" style="width: 100%" :options="options" v-model="ruleForm.natPla"
                      placeholder="请选择地区"
                  >
                  </el-cascader>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="父亲身高：" class="newItem" prop="FHt">
                  <el-input v-model="ruleForm.FHt" style="width: 70%" size="small" @blur="validateFatherHeight"></el-input>
                  cm
                </el-form-item>
                <el-form-item label="母亲身高：" class="newItem" prop="MHt">
                  <el-input v-model="ruleForm.MHt" style="width: 70%" size="small" @blur="validateMotherHeight"></el-input>
                  cm
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="家族史：" class="newItem" prop="familyHis">
                  <el-select
                      placeholder="（异常请输入具体情况）"
                      allow-create
                      filterable
                      default-first-option
                      v-model="ruleForm.familyHis" size="small">
                    <el-option label="无" value="1"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="胎龄：" class="newItem" prop="gesWeek">
                  <el-input v-model="ruleForm.gesWeek" style="width: 70%" size="small"></el-input>
                  周
                </el-form-item>
                <el-form-item label="出生方式：" class="newItem" prop="cesaSec">
                  <el-select v-model="ruleForm.cesaSec" size="small">
                    <el-option label="剖宫产" value="1"></el-option>
                    <el-option label="自然" value="0"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="出生体重：" class="newItem" prop="BWt">
                  <el-input v-model="ruleForm.BWt" style="width: 70%" size="small"></el-input>
                  kg
                </el-form-item>
                <el-form-item label="出生身长：" class="newItem" prop="BL">
                  <el-input v-model="ruleForm.BL" style="width: 70%" size="small"></el-input>
                  cm
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="保胎史：" class="newItem" prop="fetProHis">
                  <el-select
                      placeholder="（有请输入具体情况）"
                      allow-create
                      filterable
                      default-first-option
                      v-model="ruleForm.fetProHis" size="small">
                    <el-option label="无" value="1"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="既往史：" class="newItem" prop="oldHis">
                  <el-input v-model="ruleForm.oldHis" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="身份证号码：" class="newItem" prop="card">
                  <el-input v-model="ruleForm.card" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="家庭地址：" class="newItem" prop="familyAdress">
                  <el-input v-model="ruleForm.familyAdress" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="联系人姓名：" class="newItem" prop="contactsName">
                  <el-input v-model="ruleForm.contactsName" style="width: 70%" size="small"></el-input>
                </el-form-item>
                <el-form-item label="与患者关系：" class="newItem" prop="relation">
                  <el-input v-model="ruleForm.relation" style="width: 70%" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="联系电话：" class="newItem" prop="contactsNum">
                  <el-input v-model="ruleForm.contactsNum" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="line"></div>
              <div class="row">
                <el-form-item label="病例编号：" class="newItem" prop="case_num">
                  <el-input
                      ref="huanzheid"
                      size="small"
                      class="yangben"
                      v-model="ruleForm.case_num"
                      placeholder="自动生成"
                      disabled
                  ></el-input>
                </el-form-item>
              </div>
            </el-form>
            <el-form v-else-if="$route.query.organ == '10000004' || $route.query.disClass == '10000004'" ref="ruleForm" :model="ruleForm" label-width="8vw" style="padding-right: 1vh">
              <div class="row chuzhenyiyuan">
                <el-form-item label="病历号：" class="newItem" prop="userNum">
                  <el-autocomplete ref="autocompleteRef" style="width: 100%" size="small" v-model="ruleForm.userNum" :trigger-on-focus="false" :fetch-suggestions="querySearchAsync"  @select="handleSelect" @blur="handleBlur"></el-autocomplete>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="入组序号：" class="newItem" prop="enNum">
                  <el-input size="small" v-model="ruleForm.enNum"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="入组时间：" class="newItem" prop="enTime">
                  <el-date-picker
                      v-model="ruleForm.enTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="所在中心：" class="newItem" prop="hosName">
                  <el-input size="small" v-model="ruleForm.hosName"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="患者姓名：" class="newItem" prop="name">
                  <el-input size="small" v-model="ruleForm.name"></el-input>
                </el-form-item>
                <el-form-item label="国际疾病分类：" class="newItem" prop="IDE">
                  <el-select style="width: 150px;" size="small" clearable filterable v-model="ruleForm.ICD">
                    <el-option
                      v-for="item in allICDData"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="出生日期：" class="newItem" prop="birthTime">
                  <el-date-picker
                      v-model="ruleForm.birthTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="年龄：" class="newItem" prop="AGEy">
                  <el-input v-model="ruleForm.AGEy" style="width: 90%" size="small"></el-input>
                </el-form-item>
                <el-form-item label="性别：" class="newItem" prop="sex">
                  <el-select v-model="ruleForm.sex" style="width: 90%"  size="small">
                    <el-option label="男" value="1"></el-option>
                    <el-option label="女" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="出生地：" class="newItem" prop="natPla">
                  <el-cascader size="small" style="width: 100%" :options="options" v-model="ruleForm.natPla"
                      placeholder="请选择地区"
                  >
                  </el-cascader>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="确诊时间：" class="newItem" prop="firVisTime">
                  <el-date-picker
                      v-model="ruleForm.firVisTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>

                <el-form-item label="确诊年龄：" class="newItem" prop="firVisAge">
                  <el-input size="small" style="width: 90%" v-model="ruleForm.firVisAge"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="联系方式：" class="newItem" prop="contactsNum">
                  <el-input size="small" v-model="ruleForm.contactsNum"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="首次提交时间：" class="newItem" prop="oneTime">
                  <el-date-picker
                      v-model="ruleForm.oneTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      size="small"
                      placeholder="请选择日期"
                      style="width: 100%"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="孕周：" class="newItem" prop="gesWeek">
                  <el-input size="small" v-model="ruleForm.gesWeek"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="胎次：" class="newItem" prop="parity">
                  <el-input v-model="ruleForm.parity" style="width: 70%" size="small"></el-input>
                </el-form-item>
                <el-form-item label="产次：" class="newItem" prop="proNum">
                  <el-input v-model="ruleForm.proNum" style="width: 70%" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="出生体重：" class="newItem" prop="BWt">
                  <el-input v-model="ruleForm.BWt" style="width: 70%" size="small"></el-input>
                  kg
                </el-form-item>
                <el-form-item label="出生身长：" class="newItem" prop="BL">
                  <el-input v-model="ruleForm.BL" style="width: 70%" size="small"></el-input>
                  cm
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="孕期感染：" class="newItem" prop="preInf">
                  <el-select v-model="ruleForm.preInf"  size="small">
                    <el-option label="有" value="1"></el-option>
                    <el-option label="无" value="2"></el-option>
                    <el-option label="不详" value="3"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="分娩方式：" class="newItem" prop="cesaSec">
                  <el-select v-model="ruleForm.cesaSec"  size="small">
                    <el-option label="顺产" value="1"></el-option>
                    <el-option label="剖宫产" value="2"></el-option>
                    <el-option label="臀围产" value="3"></el-option>
                    <el-option label="足先露" value="4"></el-option>
                    <el-option label="其他" value="5"></el-option>
                    <el-option label="不详" value="6"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="窒息史：" class="newItem" prop="cesaAsphyxia">
                  <el-select v-model="ruleForm.cesaAsphyxia"  size="small">
                    <el-option label="有" value="1"></el-option>
                    <el-option label="无" value="2"></el-option>
                    <el-option label="不详" value="3"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="既往史：" class="newItem" prop="oldHis">
                  <el-input size="small" v-model="ruleForm.oldHis"></el-input>
                </el-form-item>
              </div>

              <div class="line"></div>
              <div class="row">
                <el-form-item label="病例编号：" class="newItem" prop="case_num">
                  <el-input
                      ref="huanzheid"
                      size="small"
                      class="yangben"
                      v-model="ruleForm.case_num"
                      placeholder="自动生成"
                      disabled
                  ></el-input>
                </el-form-item>
              </div>
            </el-form>
            <el-form v-else-if="$route.query.organ == '10000007' || $route.query.disClass == '10000007'" ref="ruleForm" :model="ruleForm" label-width="8vw" style="padding-right: 1vh">
              <div class="title">人口学信息</div>
              <div class="row">
                <el-form-item label="患者姓名：" class="newItem"  prop="name">
                  <el-input size="small" v-model="ruleForm.name"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="年龄：" class="newItem"  prop="age">
                  <el-input v-model="ruleForm.AGEy" style="width: 90%" size="small"></el-input>
                </el-form-item>
                <el-form-item label="性别：" class="newItem"  prop="sex">
                  <el-select v-model="ruleForm.sex" size="small">
                    <el-option label="男" value="1"></el-option>
                    <el-option label="女" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="民族：" class="newItem" prop="ethnic">
                    <el-select style="width: 150px;" size="small" clearable filterable v-model="ruleForm.ethnic">
                      <el-option
                        v-for="item in ethnicityData"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="出生日期：" class="newItem"  prop="birthTime">
                  <el-date-picker
                    v-model="ruleForm.birthTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    size="small"
                    placeholder="请选择日期"
                    style="width: 100%"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                  ></el-date-picker>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="籍贯：" class="newItem" prop="natPla">
                 <!--  <el-cascader size="small" style="width: 100%" :options="options" v-model="ruleForm.natPla"
                      placeholder="请选择地区"
                  >
                  </el-cascader> -->
                  <el-input v-model="ruleForm.natPla" style="width: 100%" size="small" @blur="validateFatherHeight"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="家庭住址：" class="newItem" prop="familyAdress">
                  <el-input v-model="ruleForm.familyAdress" size="small"></el-input>
                </el-form-item>
              </div>
              <div class="title">病史</div>
              <div class="row">
                <el-form-item label="现病史：" class="newItem" prop="category_describe">
                  <el-input v-model="ruleForm.category_describe" size="small"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="家族史：" class="newItem" prop="familyHis">
                  <el-input v-model="ruleForm.familyHis" size="small"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="出生身长：" class="newItem" prop="BL">
                  <el-input v-model="ruleForm.BL" style="width: 70%" size="small"></el-input>
                  cm
                </el-form-item>
                <el-form-item label="出生体重：" class="newItem" prop="BWt">
                  <el-input v-model="ruleForm.BWt" style="width: 70%" size="small"></el-input>
                  kg
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="出生胎龄：" class="newItem" prop="gesWeek">
                  <el-input v-model="ruleForm.gesWeek" size="small"></el-input>
                </el-form-item>
                <el-form-item label="出生胎次：" class="newItem" prop="parity">
                  <el-input v-model="ruleForm.parity" size="small"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="出生产次：" class="newItem" prop="proNum">
                  <el-input v-model="ruleForm.proNum" size="small"></el-input>
                </el-form-item>
              </div>
            </el-form>
            <el-form v-else  ref="ruleForm" :model="ruleForm"  label-width="8vw" style="padding-right: 1vh">
              <div class="row chuzhenyiyuan">
                <el-form-item label="病历号：" class="newItem"  prop="userNum">
                  <el-autocomplete ref="autocompleteRef" style="width: 100%" size="small" v-model="ruleForm.userNum"  :trigger-on-focus="false" :fetch-suggestions="querySearchAsync"  @select="handleSelect" @blur="handleBlur"></el-autocomplete>
                </el-form-item>
              </div>
              <div class="row jianchayiyuan">
                <el-form-item label="患者姓名：" class="newItem"  prop="name">
                  <el-input size="small" v-model="ruleForm.name"></el-input>
                </el-form-item>
                <el-form-item label="国际疾病分类：" class="newItem" prop="IDE">
                  <el-select style="width: 150px;" size="small" clearable filterable v-model="ruleForm.ICD">
                    <el-option
                      v-for="item in allICDData"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="致病基因:" class="newItem"  prop="sex">
                  <el-input v-model="genData" size="small"  :disabled="true"></el-input>
                </el-form-item>
              </div>
              <div class="row">
                <el-form-item label="性别：" class="newItem"  prop="sex">
                  <el-select v-model="ruleForm.sex" size="small">
                    <el-option label="男" value="1"></el-option>
                    <el-option label="女" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row jianchayiyuan">
                <el-form-item label="出生日期：" class="newItem"  prop="birthTime">
                  <el-date-picker
                    v-model="ruleForm.birthTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    size="small"
                    placeholder="请选择日期"
                    style="width: 100%"
                    value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="身份证号码：" class="newItem"  prop="card">
                  <el-input v-model="ruleForm.card" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="家庭地址：" class="newItem"  prop="familyAdress">
                  <el-input v-model="ruleForm.familyAdress" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="联系人姓名：" class="newItem"  prop="contactsName">
                  <el-input v-model="ruleForm.contactsName" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="与患者关系：" class="newItem"  prop="relation">
                  <el-input v-model="ruleForm.relation" size="small"></el-input>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="联系电话：" class="newItem"  prop="contactsNum">
                  <el-input v-model="ruleForm.contactsNum" size="small"></el-input>
                </el-form-item>
              </div>

               <div class="row">
                <el-form-item label="出生体重：" class="newItem"  prop="BWt">
                  <el-input v-model="ruleForm.BWt" style="width: 90%" size="small"></el-input>kg
                </el-form-item>
              </div>

               <div class="row">
                <el-form-item label="出生身长：" class="newItem"  prop="BL">
                  <el-input v-model="ruleForm.BL" style="width: 90%" size="small"></el-input>cm
                </el-form-item>
              </div>

               <div class="row">
                <el-form-item label="孕周：" class="newItem"  prop="gesWeek">
                  <el-input v-model="ruleForm.gesWeek" style="width: 90%" size="small"></el-input>周
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="分娩方式：" class="newItem"  prop="cesaSec">
                  <el-select v-model="ruleForm.cesaSec" size="small">
                    <el-option label="自然分娩" value="1"></el-option>
                    <el-option label="剖宫产" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="row">
                <el-form-item label="窒息抢救史：" class="newItem"  prop="cesaAsphyxia">
                  <el-select v-model="ruleForm.cesaAsphyxia" size="small">
                    <el-option label="无" value="1"></el-option>
                    <el-option label="轻度窒息" value="2"></el-option>
                    <el-option label="重度窒息" value="3"></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <div class="line"></div>
              <div class="row">
                <el-form-item label="病例编号：" class="newItem" prop="case_num">
                  <el-input
                    ref="huanzheid"
                    size="small"
                    class="yangben"
                    v-model="ruleForm.case_num"
                    placeholder="自动生成"
                    disabled
                  ></el-input>
                </el-form-item>
              </div>
            </el-form>
            <el-dialog title="选择病历号详情" :visible.sync="dialogTableVisible">
              <el-table  height="450" :data="patientArray">
                <el-table-column property="user_num" label="病历号" width="150"></el-table-column>
                <el-table-column property="name" label="姓名" width="200"></el-table-column>
                <el-table-column property="sex" label="性别">
                  <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.sex==='1'?'男':'女' }}</span>
                  </template>
                </el-table-column>
                <el-table-column property="birth_time" label="出生日期">
                  <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ formatDate( scope.row.birth_time)}}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  fixed="right"
                  label="操作"
                  width="100">
                  <template slot-scope="scope">
                    <el-button @click="handleClick(scope.row)" type="text" size="small">点击选择</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-dialog>
          </el-scrollbar>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="editor-right">
          <dsd ref="dsd"
               @resetFormLeft="resetFormLeft"
               :disClass="ruleForm.disClass"
               :queryId="queryId"
               :caseNum="ruleForm.case_num"
               :sex="ruleForm.sex"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000001' || $route.query.disClass == '10000001'"
          ></dsd>
          <fss ref="fss"
               @genDataHandle="handleGenData"
               @resetFormLeft="resetFormLeft"
               :disClass="ruleForm.disClass"
               :queryId="queryId"
               :caseNum="ruleForm.case_num"
               :sex="ruleForm.sex"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000002' || $route.query.disClass == '10000002'"
          ></fss>
          <cpp ref="cpp"
               @resetFormLeft="resetFormLeft"
               :disClass="ruleForm.disClass"
               :queryId="queryId"
               :sex="ruleForm.sex"
               :caseNum="ruleForm.case_num"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000003' || $route.query.disClass == '10000003'"
          ></cpp>
          <mas ref="mas"
               @resetFormLeft="resetFormLeft"
               :queryId="queryId"
               :sex="ruleForm.sex"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000004' || $route.query.disClass == '10000004'"
          ></mas>
          <sga ref="sga"
               @resetFormLeft="resetFormLeft"
               :disClass="ruleForm.disClass"
               :queryId="queryId"
               :caseNum="ruleForm.case_num"
               :sex="ruleForm.sex"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000005' || $route.query.disClass == '10000005'"
          ></sga>
          <sss ref="sss"
               @genDataHandle="handleGenData"
               @resetFormLeft="resetFormLeft"
               :disClass="ruleForm.disClass"
               :queryId="queryId"
               :caseNum="ruleForm.case_num"
               :sex="ruleForm.sex"
               :birthTime="ruleForm.birthTime"
               v-if="$route.query.organ == '10000006' || $route.query.disClass == '10000006'"
          ></sss>
          <eltm ref="eltm"
                :disClass="ruleForm.disClass"
                :queryId="queryId"
                :caseNum="ruleForm.case_num"
                :sex="ruleForm.sex"
                @resetFormLeft="resetFormLeft"
                :TGheight="TGheight"
                :TGweight="TGweight"
                :TGBMI="TGBMI"
                :birthTime="ruleForm.birthTime"
                v-if="$route.query.organ == '10000007' || $route.query.disClass == '10000007'"
          ></eltm>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import dsd from "./common/DSD"
  import fss from "./common/FSS"
  import cpp from "./common/CPP"
  import mas from "./common/MAS"
  import sga from "./common/SGA"
  import sss from "./common/SSS"
  import eltm from "./common/ELTM"
  import {validateHeight } from '../utils/heightValidator.js'
  import request from "../script/request";
  import { regionDataPlus } from "element-china-area-data";
  import {ICDDataArray}  from '../utils/ICDData';  
  import {ethnicityDataArray}  from '../utils/ethnicityData';  
  export default {
    name: 'Editor',
    components: {dsd, fss, cpp, mas,sga,sss,eltm},
    mounted() {
      this.allICDData=ICDDataArray
      this.ethnicityData=ethnicityDataArray
      // console.log(this.allICDData);
    },
    data() {
      return {
        genData:null,
        numberData:{},
        patientArray:[],
        dialogTableVisible:false,
        allICDData: '',//国际疾病数组
        ethnicityData:'',//民族
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
          ethnic:'',//民族
          category_describe:'',//现病史
          // birthAge:'',//出生年龄
          height:'',
          weight:'',
          Bmi:'',
        },
        TGheight:"",//体格检查年龄
        TGweight:"",//体格检查体重
        TGBMI:"",//体格检查BMI
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
        this.getPatientInfo()
      }
    },
    watch: {
      'ruleForm.queryId'() {
        this.getPatientInfo()
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
        this.$router.push({name: 'home'})
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
          this.TGheight=data.height;//体格检查年龄
          this.TGweight=data.weight;//体格检查体重
          this.TGBMI=data.bmi;//体格检查BMI
          console.log(this.TGheight,this.TGweight,this.TGBMI,"体格检查数据");
          this.ruleForm.userNum = data.user_num;
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
          this.ruleForm.ethnic = data.ethnic;
          this.ruleForm.FHt = data.FHt;
          this.ruleForm.MHt = data.MHt;
          this.ruleForm.familyHis = data.family_his;
          this.ruleForm.category_describe = data.category_describe;
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
        }else if (this.ruleForm.disClass === '10000007') {
          data2 = this.$refs.eltm.addData()
          console.log(data2,"data2")
          this.ruleForm.height=data2.nowHeight;//体格检查年龄
          this.ruleForm.weight=data2.nowWeight;//体格检查体重
          this.ruleForm.Bmi=data2.nowBMI;//体格检查BMI
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
      getPatientInfo(){
        let queryId = this.$route.query.queryId;
        request.getCaseDetail({queryId} ,data =>{
          // console.log(data.age,"data.age");
          this.TGheight=data.height;//体格检查年龄
          this.TGweight=data.weight;//体格检查体重
          this.TGBMI=data.bmi;//体格检查BMI
          console.log(this.TGheight,this.TGweight,this.TGBMI,"体格检查数据");
          this.ruleForm.ICD = data.ICD;
          this.ruleForm.disClass = data.dis_class;
          this.ruleForm.name = data.name;
          this.ruleForm.sex = data.sex;
          this.ruleForm.gonadalSex = data.gonadal_sex;
          if(data.fir_vis_time){
            this.ruleForm.firVisTime = data.fir_vis_time.substring(0,10);
          }
          // this.ruleForm.AGEy = data.AGEy;
          this.ruleForm.AGEy = data.age;
          this.ruleForm.AGEm = data.AGEm;
          if(data.birth_time){
            this.ruleForm.birthTime = data.birth_time.substring(0,10);
          }
          this.ruleForm.chiCom = data.chi_com;
          if (data.nat_pla) {
            let province = data.nat_pla.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
            this.ruleForm.natPla = province.split(',');
          }
          this.ruleForm.ethnic = data.ethnic;
          this.ruleForm.FHt = data.FHt;
          this.ruleForm.MHt = data.MHt;
          this.ruleForm.familyHis = data.family_his;
          this.ruleForm.category_describe = data.category_describe;
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
          this.ruleForm.userNum = data.user_num;
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
        })
      },
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
          this.ruleForm.age=`${this.ruleForm.AGEy}岁${this.ruleForm.AGEm}月`
        },
      resetForm() {
        if(this.$route.query.organ == '10000001' ){
          this.$refs.dsd.resetForm()
        }else if(this.$route.query.organ == '10000002'){
          this.$refs.fss.resetForm()
        }else if(this.$route.query.organ == '10000003'){
          this.$refs.cpp.resetForm() 
        }else if(this.$route.query.organ == '10000004'){
          this.$refs.mas.resetForm() 
        }else if(this.$route.query.organ == '10000005'){
          this.$refs.sga.resetForm()
        }else if(this.$route.query.organ == '10000006'){
          this.$refs.sss.resetForm()
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
      },
      //父亲身高提示
      validateFatherHeight() {
        validateHeight(19, this.ruleForm.FHt, 'father')
      },
      //母亲身高提示
      validateMotherHeight(){
        validateHeight(19, this.ruleForm.MHt, 'mother')
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
  .editor {
    .editor-head {
      min-height: 6vh;
      background: #ffffff;
    }

    .editor-title {
      font-size: 3vh;
      color: rgb(64, 158, 255);
    }

    .editor-left {
      background-color: rgb(236, 245, 255);

      .el-scrollbar {
        height: 85vh;
        width: 100%;
      }

      .newItem .el-form-item__label {
        font-size: 0.8vw;
      }

      .title{
        margin-top: 20px;
        margin-left: 20px;
        font-size: 20px;
      }
    }

    .editor-right {
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
<template>
  <div class="thyroid">
    <el-scrollbar class="scrollThy">
      <el-tabs :value="this.activeName">
        <el-tab-pane name="one" :style="{pointerEvents}">
          <span slot="label">临床资料</span>
          <p class="thyroid-title" v-show="isStatic">出生史：</p>
          <div class="div-box"  v-show="isStatic">
            <p class="thyroid-title" >胎龄周：{{ gesWeek }}周，出生体重：{{ BWt }}kg，出生身长：{{ BL }}cm</p>
            <p class="thyroid-title" >分娩方式：{{ cesaSec }}</p>
            <p class="thyroid-title" >窒息抢救史：{{ cesaAsphyxia }}</p>
          </div>
          <p class="thyroid-title">病史：</p>
          <div class="div-box">
            <div style="width: 100%;display: flex" v-show="!isStatic">
              <p class="thyroid-title" style="width: 50%;">
                初次就诊时间：
                <el-date-picker
                    size="small"
                    v-model="ruleForm.firVisTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="初次就诊时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
              </p>
              <p class="thyroid-title" style="width: 50%">
                初诊年龄：<input class="input-underLine" v-model="ruleForm.morbidAge"/>岁
              </p>
            </div>
            <p class="thyroid-title" v-show="!isStatic">主诉：</p>
            <el-input
                v-show="!isStatic"
                v-model="ruleForm.chiefCom"
                type="textarea"
                :autosize="{ minRows: 2}"
                resize="none"
                placeholder="请输入内容"
                style="width: 97%"
                maxlength="1500"
                show-word-limit
            ></el-input>
            <p class="thyroid-title">
              生长速率：
              <el-radio class="elRadio" v-model="ruleForm.growRate" label="1">不详</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.growRate" label="2">请选择</el-radio>
              <el-select v-if="ruleForm.growRate === '2'" size="small" v-model="ruleForm.rate">
                <el-option label="0.5厘米/年" value="0.5"></el-option>
                <el-option label="1厘米/年" value="1"></el-option>
                <el-option label="1.5厘米/年" value="1.5"></el-option>
                <el-option label="2厘米/年" value="2"></el-option>
                <el-option label="2.5厘米/年" value="2.5"></el-option>
                <el-option label="3厘米/年" value="3"></el-option>
                <el-option label="3.5厘米/年" value="3.5"></el-option>
                <el-option label="4厘米/年" value="4"></el-option>
                <el-option label="4.5厘米/年" value="4.5"></el-option>
                <el-option label="5厘米/年" value="5"></el-option>
                <el-option label="5.5厘米/年" value="5.5"></el-option>
                <el-option label="6厘米/年" value="6"></el-option>
                <el-option label="6.5厘米/年" value="6.5"></el-option>
                <el-option label="7厘米/年" value="7"></el-option>
                <el-option label="7.5厘米/年" value="7.5"></el-option>
                <el-option label="8厘米/年" value="8"></el-option>
                <el-option label="8.5厘米/年" value="8.5"></el-option>
                <el-option label="9厘米/年" value="9"></el-option>
                <el-option label="9.5厘米/年" value="9.5"></el-option>
                <el-option label="10厘米/年" value="10"></el-option>
                <el-option label="10.5厘米/年" value="10.5"></el-option>
                <el-option label="11厘米/年" value="11"></el-option>
                <el-option label="11.5厘米/年" value="11.5"></el-option>
                <el-option label="12厘米/年" value="12"></el-option>
                <el-option label="12.5厘米/年" value="12.5"></el-option>
                <el-option label="13厘米/年" value="13"></el-option>
                <el-option label="13.5厘米/年" value="13.5"></el-option>
                <el-option label="14厘米/年" value="14"></el-option>
                <el-option label="14.5厘米/年" value="14.5"></el-option>
                <el-option label="15厘米/年" value="15"></el-option>
                <el-option label="15.5厘米/年" value="15.5"></el-option>
                <el-option label="16厘米/年" value="16"></el-option>
                <el-option label="16.5厘米/年" value="16.5"></el-option>
                <el-option label="17厘米/年" value="17"></el-option>
                <el-option label="17.5厘米/年" value="17.5"></el-option>
                <el-option label="18厘米/年" value="18"></el-option>
                <el-option label="18.5厘米/年" value="18.5"></el-option>
                <el-option label="19厘米/年" value="19"></el-option>
                <el-option label="19.5厘米/年" value="19.5"></el-option>
                <el-option label="20厘米/年" value="20"></el-option>
                <el-option label="20.5厘米/年" value="20.5"></el-option>
                <el-option label="21厘米/年" value="21"></el-option>
                <el-option label="21.5厘米/年" value="21.5"></el-option>
                <el-option label="22厘米/年" value="22"></el-option>
                <el-option label="22.5厘米/年" value="22.5"></el-option>
                <el-option label="23厘米/年" value="23"></el-option>
                <el-option label="23.5厘米/年" value="23.5"></el-option>
                <el-option label="24厘米/年" value="24"></el-option>
                <el-option label="24.5厘米/年" value="24.5"></el-option>
                <el-option label="25厘米/年" value="25"></el-option>
                <el-option label="25.5厘米/年" value="25.5"></el-option>
                <el-option label="26厘米/年" value="26"></el-option>
                <el-option label="26.5厘米/年" value="26.5"></el-option>
                <el-option label="27厘米/年" value="27"></el-option>
                <el-option label="27.5厘米/年" value="27.5"></el-option>
                <el-option label="28厘米/年" value="28"></el-option>
                <el-option label="28.5厘米/年" value="28.5"></el-option>
                <el-option label="29厘米/年" value="29"></el-option>
                <el-option label="29.5厘米/年" value="29.5"></el-option>
                <el-option label="30厘米/年" value="30"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title" v-if="sex === '2'">
              月经初潮：
              <el-radio class="elRadio" v-model="ruleForm.menarchy" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.menarchy" label="2">有</el-radio>
              <el-date-picker
                  v-if="ruleForm.menarchy === '2'"
                  style="width: 8vw"
                  v-model="ruleForm.menarchyTime"
                  type="date"
                  size="small"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="初潮时间"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title" v-if="sex === '1'">
              初次遗精：
              <el-radio class="elRadio" v-model="ruleForm.menarchy" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.menarchy" label="2">有</el-radio>
              <el-date-picker
                  v-if="ruleForm.menarchy === '2'"
                  style="width: 8vw"
                  v-model="ruleForm.menarchyTime"
                  type="date"
                  size="small"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="初次遗精时间"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
          </div>
          <p class="thyroid-title">体格检查：</p>
          <div class="div-box">
            <p class="thyroid-title">
              身高：<input class="input-underLine" @blur="validateHeightFun" v-model="ruleForm.height"/>cm，
              体重：<input class="input-underLine" @change="getBMI" v-model="ruleForm.weight"/>kg，
              BMI：<input class="input-underLine" v-model="ruleForm.Bmi" readonly/>
            </p>
            <p class="thyroid-title" v-if="sex === '2'">
              <span> 
                  左侧乳腺发育分期（女孩）：
                  <el-select size="small" v-model="ruleForm.breastDev">
                    <el-option label="B1" value="1"></el-option>
                    <el-option label="B2" value="2"></el-option>
                    <el-option label="B3" value="3"></el-option>
                    <el-option label="B4" value="4"></el-option>
                    <el-option label="B5" value="5"></el-option>
                  </el-select>
              </span>
              <span> 
                  右侧乳腺发育分期（女孩）：
                  <el-select size="small" v-model="ruleForm.breastDevRight">
                    <el-option label="B1" value="1"></el-option>
                    <el-option label="B2" value="2"></el-option>
                    <el-option label="B3" value="3"></el-option>
                    <el-option label="B4" value="4"></el-option>
                    <el-option label="B5" value="5"></el-option>
                  </el-select>
              </span>
            </p>
            <p class="thyroid-title" v-if="sex === '1'">
              外生殖器分期：
              <el-select size="small" v-model="ruleForm.exGenitalia">
                <el-option label="G1" value="1"></el-option>
                <el-option label="G2" value="2"></el-option>
                <el-option label="G3" value="3"></el-option>
                <el-option label="G4" value="4"></el-option>
                <el-option label="G5" value="5"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title">
              阴毛分期：
              <el-select size="small" v-model="ruleForm.pubicHair">
                <el-option label="1" value="1"></el-option>
                <el-option label="2" value="2"></el-option>
                <el-option label="3" value="3"></el-option>
                <el-option label="4" value="4"></el-option>
                <el-option label="5" value="5"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title">
              臂长：<input class="input-underLine" v-model="ruleForm.armLength"/>cm
            </p>
            <p class="thyroid-title">特殊面容：
              <el-radio class="elRadio" v-model="ruleForm.specialFace" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.specialFace" label="2">有</el-radio>
              <input v-if="ruleForm.specialFace === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.specialFaceDesc"/>
            </p>
            <p class="thyroid-title">脊柱侧弯：
              <el-radio class="elRadio" v-model="ruleForm.scoliosis" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.scoliosis" label="2">有</el-radio>
              <el-select size="small" v-if="ruleForm.scoliosis === '2'" v-model="ruleForm.scoliosisDegree">
                <el-option label="轻度" value="1"></el-option>
                <el-option label="中度" value="2"></el-option>
                <el-option label="重度" value="3"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title">皮疹：
              <el-radio class="elRadio" v-model="ruleForm.rash" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.rash" label="2">有</el-radio>
              <input v-if="ruleForm.rash === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.rashDescribe"/>
            </p>
          </div>
          <p class="thyroid-title" >家族史：</p>
          <div class="div-box" >
            <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary"
                       size="mini" @click="addRow(familyData)"></el-button>
            <el-table
                border
                :data="familyData"
                ref="table"
            >
              <el-table-column label="与患者关系" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.relation" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="年龄" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.tAge" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="身高(cm)" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.height" @blur="validateHeights(scope.row.tAge,scope.row.height)" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="体重(kg)" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.weight" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="初潮/遗精年龄" width="130">
                <template slot-scope="scope">
                  <input v-model="scope.row.age" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="健康调查" min-width="160">
                <template slot-scope="scope">
                  <select v-model="scope.row.health">
                    <option value="1">健康</option>
                    <option value="2">基础疾病</option>
                  </select>
                  <span v-if="scope.row.health === '2'">
                             疾病名称： <input v-model="scope.row.disName" style="width: 10vw" class="input-underLine"/>
                         </span>
                </template>
              </el-table-column>
              <el-table-column label="操作"  fixed="right" width="120">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,familyData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                       size="mini" @click="addRow(familyData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <p class="thyroid-title">既往史：</p>
          <div class="div-box">
            <p>运动发育落后：
              <el-radio class="elRadio" v-model="ruleForm.motDevBack" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.motDevBack" label="2">有</el-radio>
              <input v-if="ruleForm.motDevBack === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.sport"/>
            </p>
            <p>语言发育落后：
              <el-radio class="elRadio" v-model="ruleForm.lanDevBack" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.lanDevBack" label="2">有</el-radio>
              <input v-if="ruleForm.lanDevBack === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.language"/>
            </p>
            <p>智力发育落后：
              <el-radio class="elRadio" v-model="ruleForm.intDevBack" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.intDevBack" label="2">有</el-radio>
              <input v-if="ruleForm.intDevBack === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.intelligence"/>
            </p>
            <p>听力异常：
              <el-radio class="elRadio" v-model="ruleForm.abnHear" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.abnHear" label="2">有</el-radio>
              <input v-if="ruleForm.abnHear === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.hear"/>
            </p>
            <p>反复感染史：
              <el-radio class="elRadio" v-model="ruleForm.recInfHis" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.recInfHis" label="2">有</el-radio>
              <input v-if="ruleForm.recInfHis === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.infection"/>
            </p>
            <p>抽搐史：
              <el-radio class="elRadio" v-model="ruleForm.conHis" label="1">无</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.conHis" label="2">有</el-radio>
            </p>
            <div class="thyroid-title" :style="{pointerEvents}">
              诊疗方案：
              <el-select v-model="ruleForm.diaPlan" :disabled="isStatic" @change="diaPlanChange">
                <el-option value="1" label="未治疗"></el-option>
                <el-option value="2" label="rhGH治疗"></el-option>
                <el-option value="7" label="GnRHa治疗"></el-option>
                <el-option value="3" label="GnRHa联合生长激素治疗"></el-option>
                <el-option value="8" label="芳香化酶抑制剂"></el-option>
                <el-option value="11" label="停止芳香化酶抑制剂"></el-option>
                <el-option value="10" label="芳香化酶联合生长激素治疗"></el-option>
                <el-option value="12" label="停止芳香化酶联合生长激素治疗"></el-option>
                <el-option value="4" label="停止GnRHa治疗"></el-option>
                <el-option value="5" label="停止GnRHa联合生长激素治疗"></el-option>
                <el-option value="6" label="停止生长激素治疗"></el-option>
              </el-select>
              <span v-if="ruleForm.diaPlan === '2'">
                <el-select v-model="ruleForm.rhGH" :disabled="isStatic">
                  <el-option value="1" label="短效rhGH(粉剂)"></el-option>
                  <el-option value="2" label="短效rhGH(水剂)"></el-option>
                  <el-option value="3" label="金培生长激素注射液"></el-option>
                  <el-option value="4" label="怡培生长激素注射液"></el-option>
                  <el-option value="5" label="帕西生长激素注射液"></el-option>
                </el-select>
                <span v-if="ruleForm.rhGH === '1' || ruleForm.rhGH === '2'">
                  <input v-model="ruleForm.rhGHdose" @input="calculate" class="input-underLine"/>IU/d，<input v-model="ruleForm.rhGHdoseKG" class="input-underLine"/>IU/kg.d，
                </span>
                <span v-if="ruleForm.rhGH === '3' || ruleForm.rhGH === '4' || ruleForm.rhGH === '5'">
                  <input v-model="ruleForm.PEGrhGHdose" @input="PEGcalculate" class="input-underLine"/>mg/w，<input v-model="ruleForm.PEGrhGHdoseKG" class="input-underLine"/>mg/kg.w，
                </span>
              </span>
              <span  v-if="ruleForm.diaPlan === '4'">
                <el-select v-model="ruleForm.rhGH" allow-create filterable default-first-option clearable
                            size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width: 15vw">
                  <el-option value="1" label="短效rhGH"></el-option>
                  <el-option value="2" label="长效生长激素 (PEG-rhGH)"></el-option>
                </el-select>
                <span v-if="ruleForm.rhGH === '1'">
                  停止治疗短效rhGH：<input v-model="ruleForm.rhCustomizationPrompt" @input="rhGHcalculate" class="input-underLine"/>IU/d，<input v-model="ruleForm.rhCustomizationPromptKG" class="input-underLine"/>mg/kg.w，
                </span>
                <span v-if="ruleForm.rhGH === '2'">
                  停止治疗长效生长激素（PEG-rhGH）:<input v-model="ruleForm.PEGrhCustomizationPrompt" @input="PEGrhGHcalculate" class="input-underLine"/>mg/kg.w，<input v-model="ruleForm.PEGrhCustomizationPromptKG" class="input-underLine"/>IU/kg.d，
                </span>
              </span>
              <span v-if="ruleForm.diaPlan === '7'">
                <el-select v-model="ruleForm.rhGH" allow-create filterable default-first-option clearable
                       size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width: 15vw">
                  <el-option value="1" label="达菲林针3.75mg，每28天1次"></el-option>
                  <el-option value="7" label="达菲林针3.75mg，每14天1次"></el-option>
                  <el-option value="8" label="达菲林针3.75mg，每21天1次"></el-option>
                  <el-option value="9" label="达菲林针3.75mg，每35天1次"></el-option>
                  <el-option value="10" label="达菲林针15mg，每84天1次"></el-option>
                  <el-option value="11" label="达必佳针3.75mg，每21天1次"></el-option>
                  <el-option value="2" label="达必佳针3.75mg，每28天1次"></el-option>
                  <el-option value="3" label="抑那通针3.75mg，每28天1次"></el-option>
                  <el-option value="4" label="抑那通针11.25mg，每12周1次"></el-option>
                  <el-option value="5" label="伯恩若康针3.75mg，每28天1次"></el-option>
                  <el-option value="6" label="贝依针3.75mg，每28天1次"></el-option>
                </el-select>
              </span>
              <!-- <span v-if="ruleForm.diaPlan === '3' ">
                <el-select v-model="ruleForm.GnRHa">
                  <el-option value="1" label="短效rhGH"></el-option>
                  <el-option value="2" label="长效生长激素（PEG-rhGH）"></el-option>
                </el-select>
                <span v-if="ruleForm.GnRHa === '1'">
                  <input v-model="ruleForm.GnRHadose" class="input-underLine"/>IU/kg.d
                </span>
                <span v-if="ruleForm.GnRHa === '2'">
                  <input v-model="ruleForm.GnRHadose" class="input-underLine"/>mg/kg.w，每周1次
                </span>
              </span> -->
              <div style="width: 38vw;" v-if="ruleForm.diaPlan === '3'">
                <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary" size="mini" @click="addGenRow(planData)"></el-button>
                <el-table
                    border
                    :data="planData"
                    ref="table"
                >
                  <el-table-column label="可自定义药物品种及剂型" width="240">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.rhGH" allow-create filterable default-first-option clearable
                          size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width:214px">
                        <el-option value="1" label="达菲林针3.75mg，每28天1次"></el-option>
                        <el-option value="7" label="达菲林针3.75mg，每14天1次"></el-option>
                        <el-option value="8" label="达菲林针3.75mg，每21天1次"></el-option>
                        <el-option value="9" label="达菲林针3.75mg，每35天1次"></el-option>
                        <el-option value="10" label="达菲林针15mg，每84天1次"></el-option>
                        <el-option value="11" label="达必佳针3.75mg，每21天1次"></el-option>
                        <el-option value="2" label="达必佳针3.75mg，每28天1次"></el-option>
                        <el-option value="3" label="抑那通针3.75mg，每28天1次"></el-option>
                        <el-option value="4" label="抑那通针11.25mg，每12周1次"></el-option>
                        <el-option value="5" label="伯恩若康针3.75mg，每28天1次"></el-option>
                        <el-option value="6" label="贝依针3.75mg，每28天1次"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="生长激素" width="210">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.rhUnitedCustomization" :disabled="isStatic">
                        <el-option value="1" label="短效rhGH(粉剂)"></el-option>
                        <el-option value="2" label="短效rhGH(水剂)"></el-option>
                        <el-option value="3" label="金培生长激素注射液"></el-option>
                        <el-option value="4" label="怡培生长激素注射液"></el-option>
                        <el-option value="5" label="帕西生长激素注射液"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="用量">
                    <template slot-scope="scope">
                        <span v-if="scope.row.rhUnitedCustomization === '1' || scope.row.rhUnitedCustomization === '2'">
                          <input v-model="scope.row.rhGHdose" style="width:60px" @input="tableRowCalculate(scope.row)" class="input-underLine"/>IU/kg.d，<input style="width:60px" v-model="scope.row.rhGHdoseKG" class="input-underLine"/>mg/kg.w，
                        </span>
                        <span v-if="scope.row.rhUnitedCustomization === '3' || scope.row.rhUnitedCustomization === '4' || scope.row.rhUnitedCustomization === '5'">
                          <input style="width:60px" v-model="scope.row.PEGrhGHdoseKG" @input="tableRowPEGcalculate(scope.row)" class="input-underLine"/>mg/w，<input v-model="scope.row.PEGrhGHdose" style="width:60px"   class="input-underLine"/>mg/kg.w，
                        </span>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作"  fixed="right" width="120">
                    <template slot-scope="scope">
                      <el-button @click.native.prevent="delPlanRow(scope.$index,planData)" class="el-icon-minus"
                                size="mini"></el-button>
                      <el-button class="el-icon-plus" type="primary"
                          size="mini" @click="addPlanRow(planData)"></el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <div style="width: 38vw;" v-if="ruleForm.diaPlan === '10'">
                <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary" size="mini" @click="addPlanRow(planData)"></el-button>
                <el-table
                    border
                    :data="planData"
                    ref="table"
                >
                  <el-table-column label="可自定义药物品种及剂型" width="240">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.rhGH" allow-create filterable default-first-option clearable
                          size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width:214px">
                          <el-option value="1" label="阿那曲唑0.5/片"></el-option>
                          <el-option value="2" label="阿那曲唑1/片"></el-option>
                          <el-option value="3" label="阿那曲唑1.5/片"></el-option>
                          <el-option value="4" label="阿那曲唑2/片"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="生长激素" width="210">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.rhUnitedCustomization" :disabled="isStatic">
                        <el-option value="1" label="短效rhGH(粉剂)"></el-option>
                        <el-option value="2" label="短效rhGH(水剂)"></el-option>
                        <el-option value="3" label="金培生长激素注射液"></el-option>
                        <el-option value="4" label="怡培生长激素注射液"></el-option>
                        <el-option value="5" label="帕西生长激素注射液"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="用量">
                    <template slot-scope="scope">
                        <span v-if="scope.row.rhUnitedCustomization === '1' || scope.row.rhUnitedCustomization === '2'">
                          <input v-model="scope.row.rhGHdose" style="width:60px" @change="tableRowCalculate(scope.row)" class="input-underLine"/>IU/kg.d，<input style="width:60px" v-model="scope.row.rhGHdoseKG" class="input-underLine"/>mg/kg.w，
                        </span>
                        <span v-if="scope.row.rhUnitedCustomization === '3'|| scope.row.rhUnitedCustomization === '4' ||scope.row.rhUnitedCustomization === '5'">
                          <input style="width:60px" v-model="scope.row.PEGrhGHdoseKG" @change="tableRowPEGcalculate(scope.row)" class="input-underLine"/>mg/w，<input v-model="scope.row.PEGrhGHdose" style="width:60px"   class="input-underLine"/>mg/kg.w，
                        </span>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作"  fixed="right" width="120">
                    <template slot-scope="scope">
                      <el-button @click.native.prevent="delPlanRow(scope.$index,planData)" class="el-icon-minus"
                                size="mini"></el-button>
                      <el-button class="el-icon-plus" type="primary"
                          size="mini" @click="addPlanRow(planData)"></el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <span v-if="ruleForm.diaPlan === '8' ">
                <el-select v-model="ruleForm.rhGH">
                  <el-option value="1" label="阿那曲唑0.5/片"></el-option>
                  <el-option value="2" label="阿那曲唑1/片"></el-option>
                  <el-option value="3" label="阿那曲唑1.5/片"></el-option>
                  <el-option value="4" label="阿那曲唑2/片"></el-option>
                </el-select>
              </span>
              其他药物：<input size="small" style="width: 260px" class="input-underLine" :style="{pointerEvents}" placeholder="请输入其他药物补充" v-model="ruleForm.otherMedicine"/>
            </div>
            <p>
              其他：<input class="input-underLine" style="width: 50%" v-model="ruleForm.pastOther"/>
            </p>
          </div>
        </el-tab-pane>

        <el-tab-pane name="two" :style="{pointerEvents}" >
          <span slot="label">检验检查</span>
          <p class="thyroid-title">实验室检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <div class="thyroid-lie">
              <p class="lie-first">LH：<input v-model="ruleForm.LH" class="input-underLine"/>mIU/mL，</p>
              <p class="lie-con">FSH：<input v-model="ruleForm.FSH" class="input-underLine"/>mIU/mL，</p>
              <p class="lie-last"> 检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.LHFSHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="LH以及FSH检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">E2：<input v-model="ruleForm.E2" class="input-underLine"/>pg/mL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.E2Time"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="E2检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> T：<input v-model="ruleForm.T" class="input-underLine"/>ng/dL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.TTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="T检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> PRL：<input v-model="ruleForm.PRL" class="input-underLine"/>ng/mL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.PRLTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="PRL检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">IGF-1：<input v-model="ruleForm.IGF" class="input-underLine"/>ng/mL，</p>
              <p class="lie-con">IGFBP-3：<input v-model="ruleForm.IGFBP3" class="input-underLine"/>ug/mL，</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.IGFBPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="IGF1以及IGFBP3检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                甲功：
                <el-radio class="elRadio" v-model="ruleForm.thyroid" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.thyroid" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="ruleForm.thyroid === '2'">
                异常说明：<input class="input-underLine" style="width: 50%" v-model="ruleForm.thyroidDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.thyroidTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="甲功检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">ACTH(8am)：<input v-model="ruleForm.ACTH" class="input-underLine"/>pg/mL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.ACTHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="ACTH检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">皮质醇（8am）：<input v-model="ruleForm.cortisol" class="input-underLine"/>ug/dL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.cortisolTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="皮质醇检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">DHEAs：<input v-model="ruleForm.DHEAS" class="input-underLine"/>ug/dL，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                    size="small"
                    v-model="ruleForm.DHEATime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="OHP检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> 17-OHP：<input v-model="ruleForm.OHP" class="input-underLine"/>nmol/L，</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.OHPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="17-OHP检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                血常规：
                <el-radio class="elRadio" v-model="ruleForm.blood" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.blood" label="2">异常</el-radio>
              </p>
              <p class="lie-con"  v-if="ruleForm.blood === '2'" >
                异常说明：<input class="input-underLine" style="width: 50%"   v-model="ruleForm.bloodDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.bloodTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="血常规检查时间"
                        value-format="yyyy-MM-dd"
                        @change="getAge"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                尿常规：
                <el-radio class="elRadio" v-model="ruleForm.urinalysis" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.urinalysis" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="ruleForm.urinalysis === '2'" > 
                异常说明：<input class="input-underLine" style="width: 50%" v-model="ruleForm.urinalysisDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.urinalysisTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="尿常规检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                肝肾脂糖电解质：
                <el-radio class="elRadio" v-model="ruleForm.LAKLGE" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.LAKLGE" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="ruleForm.LAKLGE === '2'">
                异常说明：<input  class="input-underLine" style="width: 50%" v-model="ruleForm.laklgeDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.LAKLGETime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="肝肾脂糖电解质检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px"> 
              乙肝三系：
                <el-radio class="elRadio" v-model="ruleForm.HBs" label="1">阴性</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.HBs" label="2">HBSAb阳性</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.HBs" label="3">小三阳</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.HBs" label="4">大三阳</el-radio>
              </p>
              <!--              <input v-if="ruleForm.HBs === '2'" class="input-underLine" style="width: 50%"-->
<!--                     v-model="ruleForm.HBsDescribe"/>-->
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.HBsTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="乙肝三系检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
                </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px"> Gh药物激发试验：Gh峰值<input v-model="ruleForm.gh" class="input-underLine"/>ng/ml</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.ghTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="Gh药物激发试验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px">空腹血糖:<input v-model="ruleForm.fasBloodGlu" class="input-underLine" />mmol/L</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.fasBloodGluTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹血糖检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px">空腹胰岛素:<input v-model="ruleForm.fasInsulin" class="input-underLine" />mIU/L</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.fasInsulinTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹胰岛素验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px">糖化血红蛋白:<input v-model="ruleForm.glyHem" class="input-underLine" />%</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.glyHemTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="糖化血红蛋白检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px">糖化血红蛋白A1c:<input v-model="ruleForm.glyHemA" class="input-underLine" />%</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="ruleForm.glyHemATime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="糖化血红蛋白A1c检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
          </div>
          <p class="thyroid-title">ACTH刺激实验：</p>
            <div class="div-box" :style="{pointerEvents}">
              ACTH(8am)：<input v-model="ruleForm.acth8am" class="input-underLine"/>pg/mL（0min），
              <!-- <input v-model="ruleForm.acth8amMax" class="input-underLine"/> -->
              ACTH(8am)：填（max）
              <input v-model="ruleForm.acthData" class="input-underLine"/>pg/mL
              （<input v-model="ruleForm.acthTime" class="input-underLine"/>min）
              <br><br>
              17α羟孕酮：<input v-model="ruleForm.hydroxy17a" class="input-underLine"/>nm/L（0min），
              17α羟孕酮：
              <!--  <input v-model="ruleForm.hydroxy17aMax" class="input-underLine"/> -->填（max）
              <input v-model="ruleForm.hydroxy17aData" class="input-underLine"/>nm/L
              （<input v-model="ruleForm.hydroxy17aTime" class="input-underLine"/>min）
              <br><br>
              DHEAs：<input v-model="ruleForm.DHEAs" class="input-underLine"/>（0min），
              <!-- <input v-model="ruleForm.DHEAsMax" class="input-underLine"/> -->
              DHEAs：填（max）
              <input v-model="ruleForm.DHEAsData" class="input-underLine"/>
              （<input v-model="ruleForm.DHEAsTime" class="input-underLine"/>min）
            </div>

          <p class="thyroid-title">影像放射检查：</p>
          <div class="div-box" >
            <p class="thyroid-title">心电图：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="心电图"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <br>
            <el-input
                v-model="ruleForm.electdiogram"
                :style="{pointerEvents}"
                type="textarea"
                :autosize="{ minRows: 3}"
                resize="none"
                placeholder="请输入内容"
                style="width: 97%"
                maxlength="1500"
                show-word-limit
            ></el-input>
            <br><br>
           <!--  检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.electdiogramTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="心电图检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker> -->
          <div class="checkBox">
            <!-- <p class="checkInp">
              项目名称：<input v-model="ruleForm.projectName" class="input-underLine" style="width: 200px"/>
            </p> -->
            <div v-for="(item,index) in ImageList" :key="index">
              <p class="thyroid-title">{{ item.title }}：</p>
              <div style="width: 100%;display: flex" :class="'divBox'+index">
                <div v-for="(url, iIndex) in item.imageUrl" :key="iIndex" style="display: flex; align-items: center; margin: 0 1vw 8px 0;flex-direction: column;">
                <p v-if="item.category === '其他' && url"> 项目名称：<input v-model="ruleForm.otherImageNames[`${item.category}_${iIndex}`]" class="input-underLine" style="width: 100px"/></p>
                <p v-else style="height: 21px"></p>
                <ImageUpload
                  style="margin-left: 1vw"
                  :key="iIndex"
                  :caseId="queryId"
                  organ="fss"
                  :category="item.category"
                  :fileName="url"
                  @update:fileName="v =>upBoneImageA(v,item.imageUrl,item.title,iIndex,ImageList)"
                  :editable="!isStatic"
                  :imageNum="iIndex"
                  @deleteImage="(name) => deleteImageListItem(name,item.category, iIndex,ImageList)"
                >
                </ImageUpload>
                </div>
              </div>
            </div>
          </div>
            <!--    <br><br>
             检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.CTImageTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="图片上传时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker> -->
            <p class="thyroid-title">性腺B超：</p>
             <!-- 检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.BUTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="性腺B超检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker> -->
            <p v-if="sex === '2'" :style="{pointerEvents}">
              子宫三径约：<input v-model="ruleForm.uterusOne" class="input-underLine"/>*<input v-model="ruleForm.uterusTwo" class="input-underLine"/>*<input v-model="ruleForm.uterusThr" class="input-underLine"/>cm，
              宫颈长约：<input v-model="ruleForm.cervixLong" class="input-underLine"/>cm，内膜厚度：<input v-model="ruleForm.intima" class="input-underLine"/>cm<br>
              <br>左侧卵巢大小约：<input v-model="ruleForm.ovaLeftOne" class="input-underLine"/>*<input v-model="ruleForm.ovaLeftTwo" class="input-underLine"/>*<input v-model="ruleForm.ovaLeftThr" class="input-underLine"/>cm<br>
              <br>右侧卵巢大小约：<input v-model="ruleForm.ovaRightOne" class="input-underLine"/>*<input v-model="ruleForm.ovaRightTwo" class="input-underLine"/>*<input v-model="ruleForm.ovaRightThr" class="input-underLine"/>cm<br>
              <br>最大滤泡直径大小：<input v-model="ruleForm.follDiameter" class="input-underLine"/>cm<br>
              <br>有无囊肿：<el-select size="small" v-model="ruleForm.isCyst" :disabled="isStatic">
                <el-option value="1" label="有"></el-option>
                <el-option value="2" label="无"></el-option>
              </el-select>
              <span v-if="ruleForm.isCyst === '1'">
                <input v-model="ruleForm.cyst" class="input-underLine"/>侧囊肿，
                大小：<input v-model="ruleForm.cystOne" class="input-underLine"/>*<input v-model="ruleForm.cystTwo" class="input-underLine"/>*<input v-model="ruleForm.cystThr" class="input-underLine"/>cm，
                <input  class="input-underLine" style="width: 8vw" v-model="ruleForm.cystDescribe"/>
              </span>
            </p>
            <p v-if="sex === '1'" :style="{pointerEvents}">
              睾丸大小：
              右侧<input v-model="ruleForm.testisLeftOne" class="input-underLine"/>cm×<input
                v-model="ruleForm.testisLeftTwo" class="input-underLine"/>cm×<input v-model="ruleForm.testisLeftThr"
                                                                                    class="input-underLine"/>cm
              ，长径<input v-model="ruleForm.testisLeftLon" class="input-underLine"/>cm；
              左侧<input v-model="ruleForm.testisRightOne" class="input-underLine"/>cm×<input
                v-model="ruleForm.testisRightTwo" class="input-underLine"/>cm×<input
                v-model="ruleForm.testisRightThr" class="input-underLine"/>cm ，长径<input
                v-model="ruleForm.testisRightLon" class="input-underLine"/>cm
            </p>
            <p class="thyroid-title" :style="{pointerEvents}">
              垂体MRI：
              <el-radio class="elRadio" v-model="ruleForm.MRI" label="1">正常</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.MRI" label="2">异常</el-radio>
              <input v-if="ruleForm.MRI === '2'" class="input-underLine" style="width: 50%"
                     v-model="ruleForm.mriDescribe"/>
              <!-- <br><br> -->
              <!--  检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.MRITime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="垂体MRI检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker> -->
            </p>
            <p class="thyroid-title">甲状腺b超：</p>
            <!-- <br><br> -->
             <!-- 检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.ThyroidLBTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="甲状腺b超检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker> -->
            <p class="thyroid-title" :style="{pointerEvents}">
              左侧甲状腺b超：
              <el-radio class="elRadio" v-model="ruleForm.ThyroidLB" label="1">正常</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.ThyroidLB" label="2">异常</el-radio>
              <span v-if="ruleForm.ThyroidLB === '2'">甲状腺结节:&nbsp;&nbsp;&nbsp;分级:
                  <el-select class="m-2" allow-create filterable default-first-option size="small" placeholder="分级" v-model="ruleForm.ThyroidLBGradation" style='width:100px'>
                    <el-option label="一级" value="一级"></el-option>
                    <el-option label="二级" value="二级"></el-option>
                    <el-option label="三级" value="三级"></el-option>
                    <el-option label="四级" value="四级"></el-option>
                    <el-option label="五级" value="五级"></el-option>
                  </el-select>
                  大小: <input class="input-underLine" v-model="ruleForm.ThyroidLBSize" style='width:100px'/>
                  弥漫性病变: <input class="input-underLine"  v-model="ruleForm.ThyroidLBLesions" style='width:100px'/>
                  其他:<input class="input-underLine"  v-model="ruleForm.ThyroidLBOther" style='width:100px'/>
            </span>
            </p>
            <p class="thyroid-title" :style="{pointerEvents}">
              右侧甲状腺b超：
              <el-radio class="elRadio" v-model="ruleForm.ThyroidRB" label="1">正常</el-radio>
              <el-radio class="elRadio" v-model="ruleForm.ThyroidRB" label="2">异常</el-radio>
              <span v-if="ruleForm.ThyroidRB === '2'">甲状腺结节:&nbsp;&nbsp;&nbsp;分级:
                  <el-select class="m-2" allow-create filterable default-first-option size="small" placeholder="分级" v-model="ruleForm.ThyroidRBGradation" style='width:100px'>
                    <el-option label="一级" value="一级"></el-option>
                    <el-option label="二级" value="二级"></el-option>
                    <el-option label="三级" value="三级"></el-option>
                    <el-option label="四级" value="四级"></el-option>
                    <el-option label="五级" value="五级"></el-option>
                  </el-select>
                  大小: <input class="input-underLine" v-model="ruleForm.ThyroidRBSize" style='width:100px'/>
                  弥漫性病变: <input class="input-underLine"  v-model="ruleForm.ThyroidRBLesions" style='width:100px'/>
                  其他:<input class="input-underLine"  v-model="ruleForm.ThyroidRBOther" style='width:100px'/>
            </span>
            </p>
          </div>
        </el-tab-pane>

        <el-tab-pane name="three" :style="{pointerEvents}">
          <span slot="label">遗传学检查</span>
          <div style="display: flex;width: 100%" >
            <p class="thyroid-title" style="margin: 0px">染色体核型：</p>
            <el-select allow-create filterable default-first-option
                       size="small" placeholder="（其他请自行输入）" v-model="ruleForm.speKar">
              <el-option label="46，XY" value="46，XY"></el-option>
              <el-option label="46，XX" value="46，XX"></el-option>
            </el-select>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh">
            <p class="thyroid-title" style="margin: 0px">生物样本库：</p>
            <el-select size="small" v-model="ruleForm.bioBank">
              <el-option label="有" value="2"></el-option>
              <el-option label="无" value="1"></el-option>
            </el-select>
          </div>
          <div class="div-box" v-show="this.ruleForm.bioBank === '2'">
            <el-table
                border
                :data="sampleBank"
                ref="table"
            >
              <el-table-column label="样本编号">
                <template slot-scope="scope">
                  <input v-model="scope.row.id" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="样本类型">
                <template slot-scope="scope">
                  <select v-model="scope.row.name">
                    <option value="1">DNA样本</option>
                    <option value="2">血清</option>
                    <option value="3">血浆</option>
                    <option value="4">尿液</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,sampleBank)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addBioRow(sampleBank)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh">
            <p class="thyroid-title" style="margin: 0px">父亲生物样本库：</p>
            <el-select size="small" v-model="ruleForm.bioBankFa">
              <el-option label="有" value="2"></el-option>
              <el-option label="无" value="1"></el-option>
            </el-select>
          </div>
          <div class="div-box" v-show="this.ruleForm.bioBankFa === '2'">
            <el-table
                border
                :data="sampleBankFa"
                ref="table"
            >
              <el-table-column label="样本编号">
                <template slot-scope="scope">
                  <input v-model="scope.row.id" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="样本类型">
                <template slot-scope="scope">
                  <select v-model="scope.row.name">
                    <option value="1">DNA样本</option>
                    <option value="2">血清</option>
                    <option value="3">血浆</option>
                    <option value="4">尿液</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,sampleBankFa)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addBioRow(sampleBankFa)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh">
            <p class="thyroid-title" style="margin: 0px">母亲生物样本库：</p>
            <el-select size="small" v-model="ruleForm.bioBankMo">
              <el-option label="有" value="2"></el-option>
              <el-option label="无" value="1"></el-option>
            </el-select>
          </div>
          <div class="div-box" v-show="this.ruleForm.bioBankMo === '2'">
            <el-table
                border
                :data="sampleBankMo"
                ref="table"
            >
              <el-table-column label="样本编号">
                <template slot-scope="scope">
                  <input v-model="scope.row.id" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="样本类型">
                <template slot-scope="scope">
                  <select v-model="scope.row.name">
                    <option value="1">DNA样本</option>
                    <option value="2">血清</option>
                    <option value="3">血浆</option>
                    <option value="4">尿液</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,sampleBankMo)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addBioRow(sampleBankMo)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh ">
            <p class="thyroid-title">上传染色体报告：</p>
            <div>
              <fileUpload ref="fileUpload"
                          v-if="this.chromImageUrl === ''"
                          :caseId="queryId"
                          organ="fss"
                          category="染色体报告"
                          @update:fileName="v =>upChromImage(v)"
              >
              </fileUpload>
              <p class="thyroid-title" v-else>
                <a style="text-decoration: none" href="#" @click="loadFile(chromImageUrl,'染色体报告')">下载报告</a>
                <a v-if="!this.isStatic" href="#" style="margin-left: 1vw" @click="resetImage(chromImageUrl,'染色体报告')">重新上传</a>
              </p>
            </div>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh ">
            <p class="thyroid-title">上传基因检测报告：</p>
            <div>
              <fileUpload ref="fileUpload"
                          v-if="this.genImageUrl === ''"
                          :caseId="queryId"
                          organ="fss"
                          category="基因检测报告"
                          @update:fileName="v =>upGenImage(v)"
              >
              </fileUpload>
              <p class="thyroid-title" v-else>
                <a style="text-decoration: none" href="#" @click="loadFile(genImageUrl,'基因检测报告')">下载报告</a>
                <a v-if="!this.isStatic" href="#" style="margin-left: 1vw" @click="resetImage(genImageUrl,'基因检测报告')">重新上传</a>
              </p>
            </div>
          </div>

          <div class="div-box" >
            <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary"
                       size="mini" @click="addGenRow(genData)"></el-button>
            <el-table
                border
                :data="genData"
                ref="table"
            >
              <el-table-column label="致病基因名称" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.genName" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="核酸变异" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.Rna" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="突变类型" width="120">
                <template slot-scope="scope">
                  <select v-model="scope.row.mutationType">
                    <option value="1">杂合突变</option>
                    <option value="2">纯合突变</option>
                    <option value="3">半合子突变</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="其他" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.other" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="致病等级" width="180">
                <template slot-scope="scope">
                  <select v-model="scope.row.infestansLevel">
                    <option value="1">P（致病）</option>
                    <option value="2">LP（可能致病）</option>
                    <option value="3">VUS（意义不明确）</option>
                    <option value="4">LB（可能良性）</option>
                    <option value="5">B（良性）</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="氨基酸变异" width="120">
                <template slot-scope="scope">
                  <input v-model="scope.row.amino" class="input-underLine"/>
                </template>
              </el-table-column>
              <!-- <el-table-column label="父亲" width="120">
                <template slot-scope="scope">
                  <select v-model="scope.row.father">
                    <option value="1">野生型</option>
                    <option value="2">杂合突变</option>
                    <option value="3">纯合突变</option>
                    <option value="4">半合子突变</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="母亲" width="120">
                <template slot-scope="scope">
                  <select v-model="scope.row.mother">
                    <option value="1">野生型</option>
                    <option value="2">杂合突变</option>
                    <option value="3">纯合突变</option>
                    <option value="4">半合子突变</option>
                  </select>
                </template>
              </el-table-column> -->
              <!-- 动态列 -->
              <el-table-column width="120"
                v-for="(item, index) in tiesData"
                :key="index"
                :prop="`ties${index + 1}`"
                :label="item"
              >
              <template slot-scope="scope">
                <select v-model="scope.row[`ties${index + 1}`]">
                  <option value="1">野生型</option>
                  <option value="2">杂合突变</option>
                  <option value="3">纯合突变</option>
                  <option value="4">半合子突变</option>
                </select>
              </template>
            </el-table-column>
              <el-table-column label="操作"  fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delGenRow(scope.$index,genData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                       size="mini" @click="addGenRow(genData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane name="four" :style="{pointerEvents}" >
          <span slot="label">诊断</span>
          <div class="div-box">
            <!-- <p class="thyroid-title">国际疾病分类：</p> -->
            <!-- <el-select style="width: 300px;" size="small" clearable filterable v-model="ruleForm.ICD">
              <el-option
                v-for="item in allICDData"
                :key="item.id"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select> -->
            <p class="thyroid-title">主要诊断：</p>
              <div v-if="showCascader">
              <el-cascader
                  style="width: 97%"
                  v-model="ruleForm.mainDia"
                  placeholder="（其他请自行输入）"
                  :options="options"
                  size="medium"
                  allow-create
                  multiple default-first-option
                  :disabled="isStatic"
                  @change="handleChange">
              </el-cascader>
              </div>
            <el-input v-if="ruleForm.mainDia=='特发性矮小,其他(手填或不填)'" placeholder="特发性矮小其他(手填或不填)" v-model="ruleForm.mainDiaIllustrate"></el-input>
            <el-input v-if="ruleForm.mainDia=='其他'"  placeholder="其他(手填或不填)" v-model="ruleForm.DiaIllustrate"></el-input>
            <p class="thyroid-title">次要诊断：</p>
            <el-input
                v-model="ruleForm.secDia"
                type="text"
                placeholder="请输入内容"
                style="width: 97%"
                show-word-limit
                :disabled="isStatic"
            ></el-input>
          </div>
        </el-tab-pane>

        <el-tab-pane name="five">
          <span slot="label">随访</span>
          <el-button type="primary" :style="{pointerEvents}" style="float: right; margin-right: 1vw" @click="openDialog">添加随访记录</el-button>
          <el-table
              :data="cases"
              border
              tooltip-effect="dark"
              reserve-selection="true"
              align="center"
              highlight-current-row
              :row-class-name="tableRowClassName"
              :header-cell-style="{background:'#f1f1f1', color:'#333','font-weight':'400','font-size':'14px', padding: '5px 0'}">
            <el-table-column
                v-if="this.cases.length > '0'"
                type="selection"
                width="60"
                align="center">
            </el-table-column>

            <el-table-column
                property="follwTime"
                label="随访日期"
                min-width="160">
            </el-table-column>

            <el-table-column
                property="upTime"
                label="添加日期"
                min-width="160">
            </el-table-column>

            <el-table-column
                property="height"
                label="身高"
                min-width="80">
            </el-table-column>

            <el-table-column
                property="weight"
                label="体重"
                min-width="60">
            </el-table-column>

            <el-table-column
                property="age"
                label="年龄"
                min-width="160">
            </el-table-column>
            <el-table-column
                  property="is_finalhei"
                  label="是否达终身高"
                  min-width="160">
                  <template slot-scope="scope">
                      <span v-show="scope.row.is_finalhei==='1'">是</span>
                      <span v-show="scope.row.is_finalhei==='2'">否</span>
                      <span v-show="scope.row.is_finalhei==='' || scope.row.is_finalhei===null"></span>
                  </template>
              </el-table-column>

            <el-table-column
                fixed="right"
                label="操作">
              <template slot-scope="scope">
                <div>
                  <el-tooltip class="item" effect="dark" content="查看" placement="top">
                    <i class="el-icon-view" @click="lookDetailClick(scope.row)" type="text" size="small"></i>
                  </el-tooltip>
                  <el-tooltip class="item" effect="dark" :style="{pointerEvents}" content="编辑" placement="top">
                    <i class="el-icon-edit" @click="upDateClick(scope.row)" type="text" size="small"></i>
                  </el-tooltip>
                  <el-tooltip class="item" effect="dark" :style="{pointerEvents}" content="删除" placement="top">
                    <i class="el-icon-delete" @click="del(scope.row)" type="text" size="small"></i>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
          </el-table>
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

          <div style="width: 100%" align="center">
            <div ref="chart" style="width:27vw;height:75vh"></div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-scrollbar>
    <el-dialog
        :visible.sync="dialogVisible"
        :modal="false"
        width="65%"
    >
      <ShortFollow
          :sex="sex"
          :birthTime="birthTime"
          :queryId="queryId"
          :queryPId="queryPId"
          :stuts="stuts"
          disClass="fss"
          ref="shortFollow">
      </ShortFollow>
      <span v-if="stuts === 'select'"  slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
      <span v-else  slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addFollow">确 定</el-button>
      </span>
    </el-dialog>
  </div>

</template>

<script>
  import ImageUpload from "../imageViewer/ImageUpload";
  import request from "../../script/request";
  import ShortFollow from "../common/shortFollow"
  // import image from "../../script/image"
  import otherImage from "../../script/otherImage"
  import fileUpload from "../imageViewer/fileUpload";
  // import {ICDDataArray}  from '../../utils/ICDData'; 
  import { validateHeightByBirthdate,validateHeight} from '../../utils/heightValidator.js' 

  const ImageString = JSON.stringify(otherImage)

  export default {
    name: "FSS",
    components: {ImageUpload, ShortFollow, fileUpload},
    props: {
      disClass: String,
      queryId: String,
      caseNum: String,
      sex: String,
      birthTime: String,
      isStatic: {
        type: Boolean,
        default: false
      },
      default: String,
      gesWeek: String,
      BWt: String,
      BL: String,
      cesaSec: String,
      cesaAsphyxia: String,
    },
    mounted() {
      if (this.isStatic) {
        this.pointerEvents = "none";
      }
      if(this.default === "follow"){
        this.activeName = "five"
      }
      if(this.$route.query.follow=== "follow"){
          this.activeName = "five"
        }
      // this.allICDData=ICDDataArray
      // console.log(this.allICDData);
    },
    data() {
      return {
        options: [{
          value: '生长激素缺乏症',
          label: '生长激素缺乏症',
          }, {
            value: '特发性矮小',
            label: '特发性矮小',
            children: [{
              value: '家族性矮小',
              label: '家族性矮小'
            }, {
              value: '小于胎龄儿生后持续身材矮小',
              label: '小于胎龄儿生后持续身材矮小'
            },{
              value: '其他(手填或不填)',
              label: '其他(手填或不填)'
            }]
        },
        {
          value: '其他',
          label: '其他',
        }
      ],
        // allICDData: '',//国际疾病数组
        queryPId:"",
        dialogVisible: false,
        stuts: "",
        unit: [],
        pointerEvents: "",
        boneAgeImageUrl: "",
        chromImageUrl: '',
        genImageUrl: '',
        cases: [],
        activeName: 'one',
        tiesData:[],
        planData:[{rhGH: '', rhUnitedCustomization: '',rhGHdose:'',rhGHdoseKG:'',PEGrhGHdose:'',PEGrhGHdoseKG:''}],
        familyData: [{relation: '父亲', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
          {relation: '母亲', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
          {relation: '爷爷', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
          {relation: '奶奶', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
          {relation: '外公', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
          {relation: '外婆', tAge:'', height: '', weight: '', age: '', health: '', disName: ''},
        ],
        sampleBank: [{id: '', name: ''},],
        sampleBankFa:[{id: '', name: ''},],
        sampleBankMo:[{id: '', name: ''},],
        genData:[
          {genName: '', Rna: '',mutationType:'',other:'',  infestansLevel:'', amino:''},//, father: '', mother: ''
        ],
        ruleForm: {
          // queryId:'',
          motDevBack: '1',
          sport: '',
          lanDevBack: '1',
          language: '',
          intDevBack: '1',
          intelligence: '',
          abnHear: '1',
          hear: '',
          recInfHis: '1',
          infection: '',
          conHis: '1',
          pastOther: '',

          firVisTime: '',
          morbidAge: '',
          chiefCom: '',
          growRate: '',
          rate: '',
          menarchy: '',
          menarchyTime: '',
          height: '',
          weight: '',
          Bmi: '',
          breastDev: '',
          breastDevRight:'',
          exGenitalia: '',
          pubicHair: '',
          armLength: '',
          specialFace: '',
          specialFaceDesc: '',
          scoliosis: '',
          scoliosisDegree: '',
          rash: '',
          rashDescribe: '',
          LH: '',
          FSH: '',
          LHFSHTime:'',
          E2: '',
          E2Time:'',
          T: '',
          TTime:'',
          PRL:'',
          PRLTime:'',
          IGF: '',
          IGFBP3: '',
          IGFBPTime:'',
          thyroid: '',
          thyroidDescribe: '',
          thyroidTime:'',
          ACTH: '',
          ACTHTime:'',
          cortisol: '',
          cortisolTime:'',
          DHEAS: '',
          DHEATime:'',
          OHP: '',
          OHPTime:'',
          blood: '',
          bloodTime: '',
          bloodDescribe: '',
          urinalysis: '',
          urinalysisDescribe: '',
          urinalysisTime:'',
          LAKLGE: '',
          LAKLGETime: '',
          laklgeDescribe: '',
          HBs: '', 
          HBsTime: '',
          HBsDescribe: '',
          gh: '',
          ghTime:'',
          fasBloodGlu:'',//空腹血糖
          fasBloodGluTime:'',//空腹血糖检查时间
          fasInsulin:'',//空腹胰岛素
          fasInsulinTime:'',//空腹胰岛素检查时间
          glyHem:'',//糖化血红蛋白
          glyHemTime:'',//糖化血红蛋白检查时间
          glyHemA:'',//糖化血红蛋白A1c
          glyHemATime:'',//糖化血红蛋白A1c检查时间
          acth8am:'',//ACTH(8am)
          // acth8amMax:'',
          acthData:'',
          acthTime:'',
          hydroxy17a:'',
          // hydroxy17aMax:'',
          hydroxy17aData:'',
          hydroxy17aTime:'',
          DHEAs:'',
          // DHEAsMax:'',
          DHEAsData:'',
          DHEAsTime:'',
          electdiogram: '',
          electdiogramTime:'',
          otherImageNames:{},//其他图片名称
          uterusOne:"",
          uterusTwo:"",
          uterusThr:"",
          cervixLong:"",
          intima:"",
          ovaLeftOne:"",
          ovaLeftTwo:"",
          ovaLeftThr:"",
          ovaRightOne:"",
          ovaRightTwo:"",
          ovaRightThr:"",
          follDiameter:"",
          isCyst:"",
          cyst:"",
          cystOne:"",
          cystTwo:"",
          cystThr:"",
          cystDescribe:"",
          testisLeftOne: '',
          testisLeftTwo: '',
          testisLeftThr: '',
          testisLeftLon: '',
          testisRightOne: '',
          testisRightTwo: '',
          testisRightThr: '',
          testisRightLon: '',
          MRI: '',
          ThyroidLB:'',//甲状腺B超左
          ThyroidLBGradation:'',//甲状腺B超左(分级)
          ThyroidLBSize:'',//甲状腺B超左(大小)
          ThyroidLBLesions:'',//甲状腺B超左(弥漫性病变)
          ThyroidLBOther:'',////甲状腺B超左(其他)
          ThyroidRB:'',//甲状腺B超右
          ThyroidRBGradation:'',//甲状腺B超右(分级)
          ThyroidRBSize:'',//甲状腺B超右(大小)
          ThyroidRBLesions:'',//甲状腺B超右(弥漫性病变)
          ThyroidRBOther:'',////甲状腺B超右(其他)
          mriDescribe: '',
          diaPlan: '',
          rhGH: '',
          rhGHdose: '',
          anstrozole:'',//阿那曲唑
          GnRHa:'',//GnRHa联合生长激素治疗-长短效
          GnRHadose:'',//GnRHa联合生长激素治疗-长短效_说明
          bioBank: '',
          sampleId: '',
          sampleClass: [],
          bioBankFa:'',
          sampleIdFa:'',
          sampleClassFa: [],
          bioBankMo:'',
          sampleIdMo:'',
          sampleClassMo: [],
          // ICD:'',//国际疾病分类
          mainDia: [],
          mainDiaIllustrate:'',//特发性矮小其他说明
          DiaIllustrate:'',//其他说明
          secDia: '',
          speKar: '',
          SRY: '',
          mutKind: '',
          sourMut: '',
          genMutName: '',
          baseMut: '',
          amiAciMut: '',
          famHis: [],
          genData:[],
          planData: [],
          rhGHdoseKG:'',//生长激素剂量（kg）
          PEGrhGHdose:'',//长效生长激素（PEG-rhGH）
          PEGrhGHdoseKG:'',//自动计算数值
          rhCustomizationDiaPlan:'',//重新定义GnRHal联合生长激素治疗选项
          rhCustomizationPrompt:'',//重新定义GnRHal联合生长激素治疗选项
          PEGrhCustomizationPrompt:"",//长效生长激素（PEG-rhGH）
          rhCustomizationPromptKG:"",//自动计算数值
          PEGrhCustomizationPromptKG:"",//自动计算数值
          otherMedicine:'',//其他药物补充
        },
        followForm: {},
        total:0,
        currPage: 1,
        pageSize: 10,
        filters:{currPage:1,limit:10,queryId:this.queryId},
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
          },
        },
        cleared: false,
        ImageList: JSON.parse(ImageString),
      }
    },
    computed:{
      showCascader() {
            // 当mainDia不包含"其他"或"其他(手填或不填)"时显示级联选择器
            return !(
              (this.ruleForm.mainDia && this.ruleForm.mainDia.includes('其他(手填或不填)')) || 
              (this.ruleForm.mainDia && this.ruleForm.mainDia.includes('其他') && !this.ruleForm.mainDia.includes('其他(手填或不填)'))
            )
          }
    },
    activated() {
      this.boneAgeImageUrl = '';
      this.chromImageUrl = '';
      this.genImageUrl = '';
      if (this.$route.query.queryId) {
        this.getCase();
      } else {
        for (let key in this.ruleForm) {
          this.ruleForm[key] = ''
        }
        this.ruleForm.motDevBack = '1';
        this.ruleForm.lanDevBack = '1';
        this.ruleForm.intDevBack = '1';
        this.ruleForm.abnHear = '1';
        this.ruleForm.recInfHis = '1';
        this.ruleForm.conHis = '1';
      }
      if (this.cleared) {
        this.getCase();
        this.cleared = false;
      }
    },
    watch: {
      'ruleForm.queryId'() {
        this.getCase()
      },
      familyData: {
        handler(val) {
              this.tiesData = val.map(item => {return item.relation}).filter(relation => relation && relation.trim() !== '');
              // console.log(this.tiesData);
              // 动态生成 genData 的属性
              const newGenData = this.genData.map(item => {
                const newItem = { ...item }; // 深拷贝
                val.forEach((relation, index) => {
                  const key = `ties${index + 1}`;
                  // newItem[`ties${index + 1}`] = ''; // 动态添加属性
                 // 如果关系不为空，才添加对应的 ties 属性
                  if (relation.relation && relation.relation.trim() !== '') {
                    if (!newItem[key]) {
                      newItem[key] = ''; // 动态添加属性
                    }
                  } else {
                   // 如果关系为空，删除对应的 ties 属性
                    if (newItem[key]) {
                      delete newItem[key];
                    }
                  }
                });
                return newItem;
              });
              this.genData = newGenData;
              /* this.tiesData = val.map(item => {return item.relation})
              // 动态生成 genData 的属性
              const newGenData = this.genData.map(item => {
                const newItem = { ...item }; // 深拷贝
                val.forEach((relation, index) => {
                  newItem[`ties${index + 1}`] = ''; // 动态添加属性
                });
                return newItem;
              });
              this.genData = newGenData; */
          },
        deep: true,
        immediate: true
      },
    },
    methods: {
      handleChange(value) {
        // console.log(value);
        // 当选择变化时，如果选择了其他选项，可以重置相关输入
        if (value && value.includes('其他')) {
          this.ruleForm.DiaIllustrate = ''
        }
        if (value && value.includes('其他(手填或不填)')) {
          this.ruleForm.mainDiaIllustrate = ''
        }
      },
      addPlanRow(tableData){
        tableData.push({rhGH: '', rhUnitedCustomization: '',rhGHdose:'',rhGHdoseKG:'',PEGrhGHdose:'',PEGrhGHdoseKG:''})
      },
      delPlanRow(index, rows){
        rows.splice(index, 1);
      },
      diaPlanChange(){
        // console.log(a,"发生变化");
        this.planData = [{
            rhGH: '', 
            rhUnitedCustomization: '',
            rhGHdose:'',
            rhGHdoseKG:'',
            PEGrhGHdose:'',
            PEGrhGHdoseKG:''
          }];
      },
      tableRowCalculate(row) {
        // 体重校验（和原有逻辑一致）
        // console.log(row,"row")
        const weight = Number(this.ruleForm.weight) || 0;
        if (!this.ruleForm.weight || this.ruleForm.weight === '0' || weight === 0) {
          this.$message({
            message: '体重未填写或为0，无法计算',
            type: 'warning'
          });
          row.rhGHdoseKG = ''; // 清空当前行的计算结果
          this.$set(row, 'rhGHdoseKG', '');
          return;
        }

        // 仅计算表格行的剂量
        const dose = Number(row.rhGHdose) || 0;
        const result = dose / weight;
        row.rhGHdoseKG = result.toFixed(4);
        // console.log(row.rhGHdoseKG,"row.rhGHdoseKG")
        this.$set(row, 'rhGHdoseKG',  row.rhGHdoseKG);
      },

      // 表格内 - 长效生长激素剂量计算（仅处理表格行数据）
      tableRowPEGcalculate(row) {
        // 体重校验（和原有逻辑一致）
        const weight = Number(this.ruleForm.weight) || 0;
        if (!this.ruleForm.weight || this.ruleForm.weight === '0' ||  Number(this.ruleForm.weight)  === 0) {
          this.$message({
            message: '体重未填写或为0，无法计算',
            type: 'warning'
          });
          row.PEGrhGHdose = ''; // 清空当前行的计算结果
          this.$set(row, 'PEGrhGHdose', '');
          return;
        }

        // 仅计算表格行的剂量
        const dose = Number(row.PEGrhGHdoseKG) || 0;
        const result = dose / weight;
        row.PEGrhGHdose = result.toFixed(4);
        this.$set(row, 'PEGrhGHdose', row.PEGrhGHdose);
      },
      PEGcalculate(){
        if (!this.ruleForm.weight || this.ruleForm.weight === '0' || Number(this.ruleForm.weight) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.ruleForm.PEGrhGHdoseKG = ''; // 清空计算结果
        this.$set(this.ruleForm, 'PEGrhGHdoseKG', '');
        return;
      }
        const kgData = Number(this.ruleForm.PEGrhGHdose) || 0;
        const wtData = Number(this.ruleForm.weight) || 0;
        if (wtData === 0) {
          this.ruleForm.PEGrhGHdoseKG = '';
          this.$set(this.ruleForm, 'PEGrhGHdoseKG', '');
          return;
        }
        const result = kgData / wtData;
        this.ruleForm.PEGrhGHdoseKG = result.toFixed(4);
        this.$set(this.ruleForm, 'PEGrhGHdoseKG', this.ruleForm.PEGrhGHdoseKG);
      },
      rhGHcalculate(){
        if (!this.ruleForm.weight || this.ruleForm.weight === '0' || Number(this.ruleForm.weight) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.ruleForm.rhCustomizationPromptKG = ''; // 清空计算结果
        this.$set(this.ruleForm, 'rhCustomizationPromptKG', '');
        return;
      }
        const kgData = Number(this.ruleForm.rhCustomizationPrompt) || 0;
        const wtData = Number(this.ruleForm.weight) || 0;
        if (wtData === 0) {
          this.ruleForm.rhCustomizationPromptKG = '';
          this.$set(this.ruleForm, 'rhCustomizationPromptKG', '');
          return;
        }
        const result = kgData / wtData;
        this.ruleForm.rhCustomizationPromptKG = result.toFixed(4);
        this.$set(this.ruleForm, 'rhCustomizationPromptKG',  this.ruleForm.rhCustomizationPromptKG);
      },
      PEGrhGHcalculate(){

        if (!this.ruleForm.weight || this.ruleForm.weight === '0' || Number(this.ruleForm.weight) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.ruleForm.PEGrhCustomizationPromptKG = ''; // 清空计算结果
        this.$set(this.ruleForm, 'PEGrhCustomizationPromptKG', '');
        return;
      }
        const kgData = Number(this.ruleForm.PEGrhCustomizationPrompt) || 0;
        const wtData = Number(this.ruleForm.weight) || 0;
        if (wtData === 0) {
          this.ruleForm.PEGrhCustomizationPromptKG = '';
          this.$set(this.ruleForm, 'PEGrhCustomizationPromptKG', '');
          return;
        }
        const result = kgData / wtData;
        this.ruleForm.PEGrhCustomizationPromptKG = result.toFixed(4);
        this.$set(this.ruleForm, 'PEGrhCustomizationPromptKG',  this.ruleForm.PEGrhCustomizationPromptKG);
      },
      addRow(tableData) {
        tableData.push({relation: '', tAge:'', height: '', weight: '', age: '', health: '', disName: ''})
      },
      delRow(index, rows) {
        rows.splice(index, 1);
      },
      addGenRow(tableData){
        tableData.push({genName: '', Rna: '',mutationType:'',other:'',  infestansLevel:'', amino:''})//, father: '', mother: ''
      },
      delGenRow(index, rows){
        rows.splice(index, 1);
      },
      addBioRow(tableData){
        tableData.push({id:'', name:''})
      },
      calculate(){
        if (!this.ruleForm.weight || this.ruleForm.weight === '0' || Number(this.ruleForm.weight) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.ruleForm.rhGHdoseKG = ''; // 清空计算结果
        this.$set(this.ruleForm, 'rhGHdoseKG', '');
        return;
      }
        const kgData = Number(this.ruleForm.rhGHdose) || 0;
        const wtData = Number(this.ruleForm.weight) || 0;
        if (wtData === 0) {
          this.ruleForm.rhGHdoseKG = '';
          this.$set(this.ruleForm, 'rhGHdoseKG', '');
          return;
        }
        const result = kgData / wtData;
        this.ruleForm.rhGHdoseKG = result.toFixed(4);
        this.$set(this.ruleForm, 'rhGHdoseKG',  this.ruleForm.rhGHdoseKG);
      },
      getAge() {
        let strDate1 = this.birthTime + ".0";
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
        this.ruleForm.morbidAge = (year + month/12).toFixed(1)
      },
      openDialog() {
        if (this.caseNum) {
          this.stuts = "add";
          this.dialogVisible = true;
          setTimeout(() => {
            this.$refs.shortFollow.clear();
          }, 100)
        } else {
          this.$message('请先保存患者信息，再添加随访记录！');
        }
      },
      addFollow() {
        this.$refs.shortFollow.followForm.genData=this.$refs.shortFollow.genData
        let follow = this.$refs.shortFollow.followForm;
        request.addFollow(follow, data => {
          this.$message('提交成功');
          this.dialogVisible = false;
          this.getFollow(this.filters);
          this.allFollow();
          console.log(data)
        }, error => {
          this.$message('提交失败');
          console.log(error)
        })
      },
      upBoneImage(v, arr, title, iIndex, imageArr) {
        imageArr.forEach((item, i) => {
          if (item.title == title) {
            if(v){
              item.imageUrl[iIndex] = v;
              if(!item.imageUrl[iIndex + 1]){
                item.imageUrl[iIndex + 1] = '';
              }
            }
          }
          this.$set(this.ImageList, i, item);
          this.$forceUpdate()
        });
      },
      upBoneImageA(v, arr, title, iIndex, imageArr) {
        this.getCase()
        imageArr.forEach((item, i) => {
          if (item.title == title) {
            if(v){
              item.imageUrl[iIndex] = v;
              // 处理“其他”图片的项目名称初始化
              if (item.category === '其他') {
                const key = `${item.category}_${iIndex}`;
                // 确保响应式设置（Vue2 需用 $set）
                if (!this.ruleForm.otherImageNames[key]) {
                  this.$set(this.ruleForm.otherImageNames, key, '');
                }
              }
              if(!item.imageUrl[iIndex + 1]){
                item.imageUrl[iIndex + 1] = '';
              }
            }
          }
          // console.log(this.ruleForm.otherImageNames)
          this.$set(this.ImageList, i, item);
          this.$forceUpdate()
        });
      },
      // 新增：通用删除ImageList图片的方法
      // deleteImageListItem(name,category, imageIndex, ImageList) {
      //   // console.log(category=='其他', imageIndex, ImageList,"name,category, imageIndex, ImageList","删除")
      //   if(category==='骨龄图片'){
      //     this.ImageList[0].imageUrl.splice(imageIndex, 1);
      //   }
      //   if(category==='脊柱全长片'){
      //     this.ImageList[1].imageUrl.splice(imageIndex, 1);
      //   }
      //   if(category==='其他'){
      //     this.ImageList[2].imageUrl.splice(imageIndex, 1);
      //     console.log(this.otherImageNames)
      //   }
      // //   // 找到要删除的图片路径
      // //   let deleteUrl = '';
      // //   this.ImageList.forEach(item => {
      // //     if (item.title === category) {
      // //       deleteUrl = item.imageUrl[imageIndex];
      // //     }
      // //   });
      // //   if (!deleteUrl) return;
        
      // //   // 调用删除接口并更新前端数据
      // //   this.resetImage(deleteUrl, category, imageIndex);
      // },
      deleteImageListItem(name,category, imageIndex, ImageList) {
        console.log(category=='其他', imageIndex, ImageList,"name,category, imageIndex, ImageList","删除")
        let targetImageUrl = [];
        // 1. 删除对应图片
        if(category==='骨龄图片'){
          targetImageUrl = this.ImageList[0].imageUrl;
          targetImageUrl.splice(imageIndex, 1);
        }
        if(category==='脊柱全长片'){
          targetImageUrl = this.ImageList[1].imageUrl;
          targetImageUrl.splice(imageIndex, 1);
        }
        if(category==='其他'){
          targetImageUrl = this.ImageList[2].imageUrl;
          targetImageUrl.splice(imageIndex, 1);
          
          // 2. 删除当前索引的项目名称
          const deleteKey = `${category}_${imageIndex}`;
          this.$delete(this.ruleForm.otherImageNames, deleteKey);

          // 3. 重新整理剩余图片的项目名称键名（避免索引断层）
          const newOtherImageNames = {};
          targetImageUrl.forEach((url, newIndex) => {
            const oldKey = `${category}_${newIndex + 1}`;
            if (this.ruleForm.otherImageNames[oldKey]) {
              newOtherImageNames[`${category}_${newIndex}`] = this.ruleForm.otherImageNames[oldKey];
              this.$delete(this.ruleForm.otherImageNames, oldKey);
            }
          });
          // 合并新的键值对（保持响应式）
          Object.keys(newOtherImageNames).forEach(key => {
            this.$set(this.ruleForm.otherImageNames, key, newOtherImageNames[key]);
          });
        }
        this.$forceUpdate(); // 强制更新视图
      },
      upChromImage(v) {
        this.chromImageUrl = v;
      },

      upGenImage(v) {
        this.genImageUrl = v;
      },

      loadFile(url,category){
        this.$message({
          message: '正在请求文件……',
          type: 'info'
        });
        const caseId = this.queryId;
        const organ = "fss";
        const path = category + "-" + url;
        request.loadFile({caseId,organ,path},url =>{
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

      resetImage(url,category){
        const data = {
          queryId: this.queryId,
          organ: "fss",
          path:  category +"-"+ url ,
        }
        request.deleteImage(data, () => {
          // console.log(data)
          if(category === "染色体报告"){
            this.chromImageUrl = '';
          }else if(category === "基因检测报告"){
            this.genImageUrl = ''
          }
        });
      },

      addData() {
        this.ruleForm.famHis = this.familyData;
        this.ruleForm.planData = this.planData;
        this.ruleForm.genData = this.genData;
        this.ruleForm.sampleClass = this.sampleBank;
        this.ruleForm.sampleClassFa = this.sampleBankFa;
        this.ruleForm.sampleClassMo = this.sampleBankMo;
        let data = null;
        data = this.ruleForm;
        return data;
      },
      tableRowClassName({row}) {
          const height = Number(row.height) || 0;
          const weight = Number(row.weight) || 0;
          if (row.is_finalhei === "1") {
            return 'success-row';
          }
          //体重超出范围的警示
          const isHeightAbnormal = height > 0 && (height > 190 || height < 70);
          const isWeightAbnormal = weight > 0 && (weight > 80 || weight < 10);
          if (isHeightAbnormal || isWeightAbnormal) {
            return 'warning-row'; // 警示行样式类
          }
          return ''
        },

      getCase() {
        let queryId = null
        if(this.$route.query.queryId){
          queryId = this.$route.query.queryId
        }else{
          queryId = this.queryId
        }
        request.getCase({queryId}, data => {
          this.queryPId = data.id;
          this.familyData = JSON.parse(data.fam_his.replace(/'/g, "\""));
          this.genData = data.gen_mut_name ? JSON.parse(data.gen_mut_name.replace(/'/g, "\"")) : [{genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:''}];//, father: '', mother: ''
          this.$emit('genDataHandle', this.genData || []);
          let temMot = data.mot_dev_back ? JSON.parse(data.mot_dev_back) : {};
          this.ruleForm.motDevBack = temMot['motDevBack'];
          this.ruleForm.sport = temMot['sport'];
          let temLan = data.lan_dev_back ? JSON.parse(data.lan_dev_back) : {};
          this.ruleForm.lanDevBack = temLan['lanDevBack'];
          this.ruleForm.language = temLan['language'];
          let temInt = data.int_dev_back ? JSON.parse(data.int_dev_back) : {};
          this.ruleForm.intDevBack = temInt['intDevBack'];
          this.ruleForm.intelligence = temInt['intelligence'];
          let temHear = data.abn_hear ? JSON.parse(data.abn_hear) : {};
          this.ruleForm.abnHear = temHear['abnHear'];
          this.ruleForm.hear = temHear['hear'];
          let temRec = data.rec_inf_his ? JSON.parse(data.rec_inf_his) : {};
          this.ruleForm.recInfHis = temRec['recInfHis'];
          this.ruleForm.infection = temRec['infection'];
          this.ruleForm.conHis = data.con_his;
          this.ruleForm.pastOther = data.past_other;
          // console.log(JSON.parse(data.other_ima_name))
          // this.ruleForm.otherImageNames = data.other_ima_name? JSON.parse(data.other_ima_name) : {};
          this.ruleForm.otherImageNames = data.other_ima_name 
            ? (() => {
                try {
                  // 替换所有单引号为双引号后再解析
                  return JSON.parse(data.other_ima_name.replace(/'/g, '"'));
                } catch (e) {
                  return {}; // 解析失败则赋值空对象
                }
              })()
            : {};
          let temBs = data.med_his ? JSON.parse(data.med_his) : {};
          this.ruleForm.firVisTime = temBs['firVisTime'];
          this.ruleForm.morbidAge = temBs['morbidAge'];
          this.ruleForm.chiefCom = temBs['chiefCom'];
          this.ruleForm.growRate = temBs['growRate'];
          this.ruleForm.rate = temBs['rate'];
          this.ruleForm.menarchy = temBs['menarchy'];
          this.ruleForm.menarchyTime = temBs['menarchyTime'];
          let tgjc = data.phy_exa ? JSON.parse(data.phy_exa) : {};
          this.ruleForm.height = tgjc['height'];
          this.ruleForm.weight = tgjc['weight'];
          this.ruleForm.Bmi = tgjc['Bmi'];
          this.ruleForm.breastDev = tgjc['breastDev'];
          this.ruleForm.breastDevRight = tgjc['breastDevRight'];
          this.ruleForm.exGenitalia = tgjc['exGenitalia'];
          this.ruleForm.pubicHair = tgjc['pubicHair'];
          this.ruleForm.armLength = tgjc['armLength'];
          this.ruleForm.specialFace = tgjc['specialFace'];
          this.ruleForm.specialFaceDesc = tgjc['specialFaceDesc'];
          this.ruleForm.scoliosis = tgjc['scoliosis'];
          this.ruleForm.scoliosisDegree = tgjc['scoliosisDegree'];
          this.ruleForm.rash = tgjc['rash'];
          this.ruleForm.rashDescribe = tgjc['rashDescribe'];
          let tempLab = data.lab_exa ? JSON.parse(data.lab_exa) : {};
          this.ruleForm.LH = tempLab['LH'];
          this.ruleForm.FSH = tempLab['FSH'];
          this.ruleForm.LHFSHTime = tempLab['LHFSHTime'] || '';
          this.ruleForm.E2 = tempLab['E2'];
          this.ruleForm.E2Time = tempLab['E2Time'] || '';
          this.ruleForm.T = tempLab['T'];
          this.ruleForm.TTime = tempLab['TTime'] || '';
          this.ruleForm.PRL = tempLab['PRL'];
          this.ruleForm.PRLTime = tempLab['PRLTime'] || '';
          this.ruleForm.IGF = tempLab['IGF'];
          this.ruleForm.IGFBP3 = tempLab['IGFBP3'];
          this.ruleForm.IGFBPTime = tempLab['IGFBPTime'] || '';
          this.ruleForm.thyroid = tempLab['thyroid'];
          this.ruleForm.thyroidDescribe = tempLab['thyroidDescribe'];
          this.ruleForm.thyroidTime = tempLab['thyroidTime'] || '';
          this.ruleForm.ACTH = tempLab['ACTH'];
          this.ruleForm.ACTHTime = tempLab['ACTHTime'] || '';
          this.ruleForm.cortisol = tempLab['cortisol'];
          this.ruleForm.cortisolTime = tempLab['cortisolTime'] || '';
          this.ruleForm.DHEAS = tempLab['DHEAS'];
          this.ruleForm.DHEATime = tempLab['DHEATime'] || '';
          this.ruleForm.OHP = tempLab['OHP'];
          this.ruleForm.OHPTime = tempLab['OHPTime'] || '';
          this.ruleForm.blood = tempLab['blood'];
          this.ruleForm.bloodDescribe = tempLab['bloodDescribe'];
          this.ruleForm.bloodTime = tempLab['bloodTime'] || '';
          this.ruleForm.urinalysis = tempLab['urinalysis'];
          this.ruleForm.urinalysisDescribe = tempLab['urinalysisDescribe'];
          this.ruleForm.urinalysisTime = tempLab['urinalysisTime']=='null'?'':tempLab['urinalysisTime'] || '';
          this.ruleForm.LAKLGE = tempLab['LAKLGE'];
          this.ruleForm.laklgeDescribe = tempLab['laklgeDescribe'];
          this.ruleForm.LAKLGETime = tempLab['LAKLGETime'] || '';
          this.ruleForm.HBs = tempLab['HBs'];
          this.ruleForm.HBsTime = tempLab['HBsTime'] || '';
          this.ruleForm.HBsDescribe = tempLab['HBsDescribe'];
          this.ruleForm.gh = tempLab['gh'];
          this.ruleForm.ghTime = tempLab['ghTime'] || '';
          this.ruleForm.fasBloodGlu = tempLab['fasBloodGlu'] || '';
          this.ruleForm.fasBloodGluTime = tempLab['fasBloodGluTime']=='null'?'':tempLab['fasBloodGluTime'] || '';
          this.ruleForm.fasInsulin = tempLab['fasInsulin'] || '';
          this.ruleForm.fasInsulinTime = tempLab['fasInsulinTime'] =='null'?'':tempLab['fasInsulinTime'] || '';
          this.ruleForm.glyHem = tempLab['glyHem'] || '';
          this.ruleForm.glyHemTime = tempLab['glyHemTime']=='null'?'':tempLab['glyHemTime'] || '';
          this.ruleForm.glyHemA = tempLab['glyHemA'] || '';
          this.ruleForm.glyHemATime = tempLab['glyHemATime'] =='null'?'':tempLab['glyHemATime'] || '';

          this.ruleForm.electdiogram = data.electr;
          this.ruleForm.electdiogramTime = tempLab['electdiogramTime'] || '';
          let xxbc = data.gon_B_ult ? JSON.parse(data.gon_B_ult) : {};
          this.ruleForm.uterusOne = xxbc['uterusOne'];
          this.ruleForm.uterusTwo = xxbc['uterusTwo'];
          this.ruleForm.uterusThr = xxbc['uterusThr'];
          this.ruleForm.cervixLong = xxbc['cervixLong'];
          this.ruleForm.intima = xxbc['intima'];
          this.ruleForm.ovaLeftOne = xxbc['ovaLeftOne'];
          this.ruleForm.ovaLeftTwo = xxbc['ovaLeftTwo'];
          this.ruleForm.ovaLeftThr = xxbc['ovaLeftThr'];
          this.ruleForm.ovaRightOne = xxbc['ovaRightOne'];
          this.ruleForm.ovaRightTwo = xxbc['ovaRightTwo'];
          this.ruleForm.ovaRightThr = xxbc['ovaRightThr'];
          this.ruleForm.follDiameter = xxbc['follDiameter'];
          this.ruleForm.isCyst = xxbc['isCyst'];
          this.ruleForm.cyst = xxbc['cyst'];
          this.ruleForm.cystOne = xxbc['cystOne'];
          this.ruleForm.cystTwo = xxbc['cystTwo'];
          this.ruleForm.cystThr = xxbc['cystThr'];
          this.ruleForm.cystDescribe = xxbc['cystDescribe'];
          this.ruleForm.testisLeftOne = xxbc['testisLeftOne'];
          this.ruleForm.testisLeftTwo = xxbc['testisLeftTwo'];
          this.ruleForm.testisLeftThr = xxbc['testisLeftThr'];
          this.ruleForm.testisLeftLon = xxbc['testisLeftLon'];
          this.ruleForm.testisRightOne = xxbc['testisRightOne'];
          this.ruleForm.testisRightTwo = xxbc['testisRightTwo'];
          this.ruleForm.testisRightThr = xxbc['testisRightThr'];
          this.ruleForm.testisRightLon = xxbc['testisRightLon'];
          this.ruleForm.MRI = xxbc['MRI'];
          this.ruleForm.mriDescribe = xxbc['mriDescribe'];
          this.ruleForm.ThyroidLB = xxbc['ThyroidLB'];// 左侧甲状腺b超
          this.ruleForm.ThyroidLBGradation = xxbc['ThyroidLBGradation'];// 左侧甲状腺b超(分级)
          this.ruleForm.ThyroidLBSize = xxbc['ThyroidLBSize'];// 左侧甲状腺b超(大小)
          this.ruleForm.ThyroidLBLesions = xxbc['ThyroidLBLesions'];// 左侧甲状腺b超(弥漫性病变)
          this.ruleForm.ThyroidLBOther = xxbc['ThyroidLBOther'];// 左侧甲状腺b超(其他)
          this.ruleForm.ThyroidRB = xxbc['ThyroidRB'];// 右侧甲状腺b超
          this.ruleForm.ThyroidRBGradation = xxbc['ThyroidRBGradation'];// 右侧甲状腺b超(分级)
          this.ruleForm.ThyroidRBSize = xxbc['ThyroidRBSize'];// 右侧甲状腺b超(大小)
          this.ruleForm.ThyroidRBLesions = xxbc['ThyroidRBLesions'];// 右侧甲状腺b超(弥漫性病变)
          this.ruleForm.ThyroidRBOther = xxbc['ThyroidRBOther'];// 右侧甲状腺b超(其他)
          let mainDias = data.main_dia ? JSON.parse(data.main_dia) : {};
          let mainDiaList=  mainDias['mainDia'].slice(1, -1);
          this.ruleForm.mainDia=mainDiaList.split(',').map(item => {  
                return item.trim().replace(/'/g, '');  
            });  
          this.ruleForm.mainDiaIllustrate=mainDias['mainDiaIllustrate'];
          this.ruleForm.DiaIllustrate=mainDias['DiaIllustrate'];
          // console.log(data.dia_trea_plan,"sdasdasdas")
          // let tempDia = data.dia_trea_plan ? JSON.parse(data.dia_trea_plan) : {};
          // let tempDia = data.dia_trea_plan 
          // ? (() => {
          //     try {
          //       // 先修复genData的错误引号，再解析
          //       const fixedStr = data.dia_trea_plan.replace(/"genData":"\[(.*?)\]"/g, '"genData":[$1]');
          //       return JSON.parse(fixedStr);
          //     } catch {
          //       return {}; // 解析失败返回空对象
          //     }
          //   })()
          // : {};
          let strData = data.dia_trea_plan.replace(/"otherMedicine":.*?(,|$)/g, '');
          let tempDia = {};
          try {
            // 1. 拿到原始字符串
            let str = strData;

            //2.修复 genData/planData 外层多余引号
            str = str
              .replace(/\\/g, '')             // 1. 去掉所有转义符 \
              .replace(/"{4}/g, '""')         // 2. 把 """" 变成合法的 ""
              .replace(/"null"/g, '""')       // 3. 把 "null" 变成空字符串
              .replace(/"otherMedicine":.*?(,|$)/g, '') // 4. 删掉 otherMedicine 字段
              .replace(/"\[/g, "[")   // 去掉数组前的 "
              .replace(/\]"/g, "]")  // 去掉数组后的 "
              .replace(/\\"/g, '"'); // 把转义引号 \" 变成正常 "

            // 3. 解析
            tempDia = JSON.parse(str);
          } catch (e) {
            console.error("解析失败", e);
            tempDia = { diaPlan: "" };
          }
          // 赋值
          // this.ruleForm.diaPlan = tempDia.diaPlan || "";
          this.ruleForm.diaPlan = tempDia['diaPlan'];
          this.ruleForm.rhGH = tempDia['rhGH'];
          this.ruleForm.rhGHdoseKG=tempDia['rhGHdoseKG'];
          this.ruleForm.PEGrhGHdose=tempDia['PEGrhGHdose'];
          this.ruleForm.PEGrhGHdoseKG=tempDia['PEGrhGHdoseKG'];
          // console.log(tempDia['planData'],"tempDia['planData']")
          this.planData = tempDia['planData'] ? tempDia['planData'] :[]
          this.ruleForm.rhCustomizationPrompt= tempDia['rhCustomizationPrompt'];
          this.ruleForm.rhCustomizationPromptKG= tempDia['rhCustomizationPromptKG'];
          this.ruleForm.PEGrhCustomizationPrompt=tempDia['PEGrhCustomizationPrompt'];
          this.ruleForm.PEGrhCustomizationPromptKG=tempDia['PEGrhCustomizationPromptKG'];
          this.ruleForm.otherMedicine =JSON.parse(`"${data.dia_trea_plan.match(/"otherMedicine":""(.*?)""/)?.[1]?.replace(/\\\\/g,'\\')||''}"`);
          this.otherMedicine = tempDia['otherMedicine'];
          this.ruleForm.rhGHdose = tempDia['rhGHdose'];
          this.ruleForm.GnRHa = tempDia['GnRHa'];
          this.ruleForm.GnRHadose = tempDia['GnRHadose'];
          this.ruleForm.anstrozole = tempDia['anstrozole'];
          let tempBio = data.bio_sam_bank ? JSON.parse(data.bio_sam_bank) : {};
          this.ruleForm.bioBank = tempBio['bioBank'];
          this.ruleForm.sampleId = tempBio['sampleId'];
          this.sampleBank = tempBio['sampleClass'] ? JSON.parse(tempBio['sampleClass'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          let tempBioFa = data.f_bio_sam_bank ? JSON.parse(data.f_bio_sam_bank) : {};
          this.ruleForm.bioBankFa = tempBioFa['bioBankFa'];
          this.ruleForm.sampleIdFa = tempBioFa['sampleIdFa'];
          this.sampleBankFa = tempBioFa['sampleClassFa'] ? JSON.parse(tempBioFa['sampleClassFa'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          let tempBioMo = data.m_bio_sam_bank ? JSON.parse(data.m_bio_sam_bank) : {};
          this.ruleForm.bioBankMo = tempBioMo['bioBankMo'];
          this.ruleForm.sampleIdMo = tempBioMo['sampleIdMo'];
          this.sampleBankMo = tempBioMo['sampleClassMo'] ? JSON.parse(tempBioMo['sampleClassMo'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          this.ruleForm.secDia = data.sec_dia;
          this.ruleForm.speKar = data.spe_kar;
          this.ruleForm.SRY = data.SRY;
          this.ruleForm.mutKind = data.mut_kind;
          this.ruleForm.sourMut = data.sour_mut;
          this.ruleForm.genMutName = data.gen_mut_name;
          this.ruleForm.baseMut = data.base_mut;
          this.ruleForm.amiAciMut = data.ami_aci_mut;
          let tempData = data.B_ult_image ? JSON.parse(data.B_ult_image) : {};
          // console.log(tempData,"tempData");
          this.applyImagePath(tempData);
          this.chromeImagePath(tempData['染色体报告']);
          this.genImagePath(tempData['基因检测报告']);
          this.getFollow(this.filters);
          this.allFollow();
          this.$emit('dataList',this.ruleForm.firVisTime,this.ruleForm.morbidAge,this.ruleForm.chiefCom,this.ruleForm.mainDia,this.ruleForm.secDia,this.ruleForm.diaPlan);
          // console.log(data)
        }, error => {
          this.$emit('genDataHandle', []);
          console.log(error)
        })
      },

      applyImagePath(imagePath) {
        this.ImageList.forEach((item, i) => {
          if (item.title in imagePath) {
            if (imagePath[item.title].length > 0) {
              item.imageUrl = imagePath[item.title];
              if (this.isStatic == false) {
                item.imageUrl = item.imageUrl.concat('')
              }
            }
          }
          this.$set(this.ImageList, i, item);
        })
      },

      chromeImagePath(imagePath) {
        if (imagePath && imagePath[0]) {
          this.chromImageUrl = imagePath[0];
        } else {
          this.chromImageUrl = '';
        }
      },
      genImagePath(imagePath) {
        if (imagePath && imagePath[0]) {
          this.genImageUrl = imagePath[0];
        } else {
          this.genImageUrl = '';
        }
      },

      getFollow(filters = null){
        filters.queryId = this.queryId;
        filters.currPage = this.currPage;
        filters.limit = this.pageSize;
        request.getFollow(filters, data => {
          let cases = []
          data['contacts'].forEach(item => {
            cases.push({
              follwTime: (item.foll_time != null ? (item.foll_time.substring(0,10)) : ""),
              upTime: item.up_time,
              height: item.Ht,
              weight: item.Wt,
              age: item.age,
              id: item.id,
              is_finalhei: item.is_finalhei
            })
          })
          this.cases = cases
          this.total = data['pagedata'].count
        })
      },

      allFollow(){
        const queryId = this.queryId;
        request.allFollow({queryId},data=>{
          this.getEchartData(data)
        });
      },

      getEchartData(data){
        const chart = this.$refs.chart;
        const myChart = this.$echarts.init(chart);
        let hData = [];
        let wData = [];
        let oneLine = [];
        let twoLine = [];
        hData.push([this.ruleForm.morbidAge,this.ruleForm.height]);
        wData.push([this.ruleForm.morbidAge,this.ruleForm.weight]);
        data.forEach(item =>{
          let strDate1 = this.birthTime + "   00:00:00.0";
          let strDate2 = item.foll_time.substring(0,10)+ "   00:00:00.0";
          strDate1 = strDate1.substring(0, strDate1.lastIndexOf(".")).replace(/-/g, "/ ");
          strDate2 = strDate2.substring(0, strDate2.lastIndexOf(".")).replace(/-/g, "/ ");
          //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
          let date1 = Date.parse(strDate1);
          let date2 = Date.parse(strDate2);
          let day = Math.ceil((date2 - date1) / (60 * 60 * 1000 * 24));
          let year = Math.floor(day / 365);
          let ageYear = day % 365;
          let month = Math.floor(ageYear / 30);
          let ageMonth = month / 12;
          let age = (year + ageMonth).toFixed(1);
          hData.push([age,item.Ht]);
          wData.push([age,item.Wt]);
          let diaPlan = item.dia_trea_plan ? JSON.parse(item.dia_trea_plan) : {};
          if(diaPlan['diaPlan'] === '2' || diaPlan['diaPlan'] === '3'){
            oneLine.push([age,87])
          }
          if(diaPlan['diaPlan'] === '3'){
            twoLine.push([age,82])
          }
        });

        if (chart) {
          let option  ={};
          if(this.sex === '1'){
            option = {
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                type: 'plain',
                data: ['身高', '体重','GH','GH+GnRHa']
              },
              xAxis: {
                boundaryGap: false,
                splitNumber: 15,
                min: 3,
                max: 18,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              },
              yAxis: [{
                name: '身高/cm',
                type: 'value',
                position: 'left',
                splitNumber: 20,
                min: 80,
                max: 190,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              }, {
                name: '体重/kg',
                type: 'value',
                min: 0,
                max: 110,
                splitNumber: 11,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              }],
              series: [
                {
                  name: '3rd',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 89.7], [4, 96.7], [5, 103.3], [6, 109.1], [7, 114.6], [8, 119.9], [9, 124.6], [10, 128.7], [11, 132.9], [12, 138.1], [13, 145.0], [14, 152.3], [15, 157.5], [16, 159.9], [17, 160.9], [18, 161.3]]
                },
                {
                  name: '10th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 91.9], [4, 99.1], [5, 105.8], [6, 111.8], [7, 117.6], [8, 123.1], [9, 128.0], [10, 132.3], [11, 136.8], [12, 142.5], [13, 149.6], [14, 156.7], [15, 161.4], [16, 163.6], [17, 164.5], [18, 164.9]]
                },
                {
                  name: '25th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 94.2], [4, 101.4], [5, 108.4], [6, 114.6], [7, 120.6], [8, 126.3], [9, 131.4], [10, 136.0], [11, 140.8], [12, 147.0], [13, 154.3], [14, 161.0], [15, 165.4], [16, 167.4], [17, 168.2], [18, 168.6]]
                },
                {
                  name: '50th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 96.8], [4, 104.1], [5, 111.3], [6, 117.7], [7, 124.0], [8, 130.0], [9, 135.4], [10, 140.2], [11, 145.3], [12, 151.9], [13, 159.5], [14, 165.9], [15, 169.8], [16, 171.6], [17, 172.3], [18, 172.7]]
                },
                {
                  name: '75th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 99.4], [4, 106.9], [5, 114.2], [6, 120.9], [7, 127.4], [8, 133.7], [9, 139.3], [10, 144.4], [11, 149.9], [12, 157.0], [13, 164.8], [14, 170.7], [15, 174.2], [16, 175.8], [17, 176.4], [18, 176.7]]
                },
                {
                  name: '90th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 101.8], [4, 109.3], [5, 116.9], [6, 123.7], [7, 130.5], [8, 137.1], [9, 142.9], [10, 148.2], [11, 154.0], [12, 161.5], [13, 169.5], [14, 175.1], [15, 178.2], [16, 179.5], [17, 180.1], [18, 180.4]]
                },
                {
                  name: '97th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 104.1], [4, 111.8], [5, 119.6], [6, 126.6], [7, 133.7], [8, 140.4], [9, 146.5], [10, 152.0], [11, 158.1], [12, 166.0], [13, 174.2], [14, 179.4], [15, 182.0], [16, 183.2], [17, 183.7], [18, 183.9]]
                },
                {
                  name: '身高',
                  data: hData,
                  type: 'line',
                  smooth: true,
                  yAxisIndex: 0,
                },
                {
                  name: '体重',
                  data: wData,
                  type: 'line',
                  yAxisIndex: 1,
                  smooth: true
                },
                {
                  name: 'GH',
                  type: 'line',
                  symbol: 'none',
                  itemStyle: {
                    normal: {
                      color: '#ff3e1c',
                      lineStyle: {
                        width: 5,
                        color: '#ff3e1c',
                      },
                    }
                  },
                  data: oneLine,
                },
                {
                  name: 'GH+GnRHa',
                  type: 'line',
                  symbol: 'none',
                  itemStyle: {
                    normal: {
                      color: '#6570ff',
                      lineStyle: {
                        width: 5,
                        color: '#6570ff',
                      },
                    }
                  },
                  data: twoLine,
                },
              ]
            }
          }else {
            option = {
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                type: 'plain',
                data: ['身高', '体重','GH','GH+GnRHa']
              },
              xAxis: {
                boundaryGap: false,
                splitNumber: 15,
                min: 3,
                max: 18,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              },
              yAxis: [{
                name: '身高/cm',
                type: 'value',
                position: 'left',
                splitNumber: 20,
                min: 80,
                max: 190,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              }, {
                name: '体重/kg',
                type: 'value',
                min: 0,
                max: 110,
                splitNumber: 11,
                offset: 0,
                axisLine: {
                  show: true,
                  symbol: ['none', 'arrow'],
                  symbolSize: [8, 8],
                  symbolOffset: [0, 8],
                  lineStyle: {
                    color: 'black',
                    width: 1,
                    type: 'solid'
                  }
                },
                axisTick: {
                  show: false,
                },
              }],
              series: [
                {
                  name: '3rd',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 88.6], [4, 95.8], [5, 102.3], [6, 108.1], [7, 113.3], [8, 118.5], [9, 123.3], [10, 128.3], [11, 134.2], [12, 140.2], [13, 145.0], [14, 147.9], [15, 149.5], [16, 149.8], [17, 150.1], [18, 150.4]]
                },
                {
                  name: '10th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 90.8], [4, 98.1], [5, 104.8], [6, 110.8], [7, 116.2], [8, 121.6], [9, 126.7], [10, 132.1], [11, 138.2], [12, 144.1], [13, 148.6], [14, 151.3], [15, 152.8], [16, 153.1], [17, 153.4], [18, 153.7]]
                },
                {
                  name: '25th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 93.1], [4, 100.4], [5, 107.3], [6, 113.5], [7, 119.2], [8, 124.9], [9, 130.2], [10, 135.9], [11, 142.2], [12, 148.0], [13, 152.2], [14, 154.8], [15, 156.1], [16, 156.4], [17, 156.7], [18, 157.0]]
                },
                {
                  name: '50th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 95.6], [4, 103.1], [5, 110.2], [6, 116.6], [7, 122.5], [8, 128.5], [9, 134.1], [10, 140.1], [11, 146.6], [12, 152.4], [13, 156.3], [14, 158.6], [15, 159.8], [16, 160.1], [17, 160.3], [18, 160.6]]
                },
                {
                  name: '75th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 98.2], [4, 105.7], [5, 113.1], [6, 119.7], [7, 125.9], [8, 132.1], [9, 138.0], [10, 144.4], [11, 151.1], [12, 156.7], [13, 160.3], [14, 162.4], [15, 163.5], [16, 163.8], [17, 164.0], [18, 164.2]]
                },
                {
                  name: '90th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 100.5], [4, 108.2], [5, 115.7], [6, 122.5], [7, 129.0], [8, 135.4], [9, 141.6], [10, 148.2], [11, 155.2], [12, 160.7], [13, 164.0], [14, 165.9], [15, 166.8], [16, 167.1], [17, 167.3], [18, 167.5]]
                },
                {
                  name: '97th',
                  type: 'line',
                  symbol: 'none',
                  data: [[3, 102.9], [4, 110.6], [5, 118.4], [6, 125.4], [7, 132.1], [8, 138.7], [9, 145.1], [10, 152.0], [11, 159.2], [12, 164.5], [13, 167.6], [14, 169.3], [15, 170.1], [16, 170.3], [17, 170.5], [18, 170.7]]
                },
                {
                  name: '身高',
                  data: hData,
                  type: 'line',
                  smooth: true,
                  yAxisIndex: 0,
                },
                {
                  name: '体重',
                  data: wData,
                  type: 'line',
                  yAxisIndex: 1,
                  smooth: true
                },
                {
                  name: 'GH',
                  type: 'line',
                  symbol: 'none',
                  itemStyle: {
                    normal: {
                      color: '#ff3e1c',
                      lineStyle: {
                        width: 5,
                        color: '#ff3e1c',
                      },
                    }
                  },
                  data: oneLine,
                },
                {
                  name: 'GH+GnRHa',
                  type: 'line',
                  symbol: 'none',
                  itemStyle: {
                    normal: {
                      color: '#6570ff',
                      lineStyle: {
                        width: 5,
                        color: '#6570ff',
                      },
                    }
                  },
                  data: twoLine,
                },
              ]
            }
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

      handleSizeChange(val) {
        this.pageSize = val;
        this.currPage = 1;
        this.getFollow(this.filters)
      },
      handleCurrentChange(val) {
        this.currPage = val;
        this.getFollow(this.filters)
      },
      getBMI() {
        if (this.ruleForm.height && this.ruleForm.weight) {
          const h = this.ruleForm.height / 100;
          this.ruleForm.Bmi = (this.ruleForm.weight / (h * h)).toFixed(1)
        } else {
          this.ruleForm.Bmi = "";
        }
      },

      lookDetailClick(row){
        this.stuts = "select";
        this.queryPId = row.id;
        this.dialogVisible = true;
        setTimeout(() => {
          this.$refs.shortFollow.getDetail();
        }, 100)
      },

      upDateClick(row){
        this.stuts = "update";
        this.queryPId = row.id;
        this.dialogVisible = true;
        setTimeout(() => {
          this.$refs.shortFollow.clear();
          this.$refs.shortFollow.getDetail();
        }, 100)
      },

      del(row){
        const queryPId = row.id;
        request.delFollow({queryPId},() =>{
          this.getFollow(this.filters);
          this.$message({
            message: '删除成功',
            type: 'success'
          });
        }, error => {
          this.$message('删除失败');
          console.log(error)
        });
      },
      resetForm() {
        this.$confirm('是否清空内容?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$emit('resetFormLeft');
          const user_num = this.ruleForm.user_num;
          const case_num = this.ruleForm.case_num;
          const queryUId = this.ruleForm.queryUId;
          this.boneAgeImageUrl = '';
          this.imageUrl = ['', '', ''];
          this.chromImageUrl = '';
          this.genImageUrl = '';
          this.patImageUrl = '';
          for (let key in this.ruleForm) {
            this.ruleForm[key] = ''
          }
          this.ruleForm.user_num = user_num;
          this.ruleForm.case_num = case_num;
          this.ruleForm.queryUId = queryUId;
          this.cleared = true;
          this.genData =[{genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:'', father: '', mother: ''}];
          this.planData=[{rhGH: '', rhUnitedCustomization: '',rhGHdose:'',rhGHdoseKG:'',PEGrhGHdose:'',PEGrhGHdoseKG:''}],
          this.biologBank =  [{id: '', name: ''}];
          this.ruleForm.case_num ='';
          this.ruleForm.Ht = '';
          this.ruleForm.HSDS = '';
          this.ruleForm.Wt = '';
          this.ruleForm.WSDS = '';
          this.ruleForm.penileLength = '';
          this.ruleForm.penileDia = '';
          this.ruleForm.tesVolume ='';
          this.ruleForm.prader = '';
          this.ruleForm.locaUreOri = '';
          this.ruleForm.rigTesPos ='';
          this.ruleForm.lefTesPos = '';
          let breast_dev={};
          this.ruleForm.breastDev = breast_dev['breastDev'];
          this.ruleForm.breastDevRight = breast_dev['breastDevRight'];
          this.ruleForm.exGenitalia ='';
          this.ruleForm.pubicHair ='';
          this.ruleForm.bodyOther = '';
          this.ruleForm.genitals = '';
          this.ruleForm.boneAge = '';
          this.ruleForm.LH ='';
          this.ruleForm.FSH = '';
          this.ruleForm.T = '';
          this.ruleForm.E2 ='';
          this.ruleForm.DHT ='' ;
          this.ruleForm.FT = '';
          this.ruleForm.SHBG = '';
          this.ruleForm.IGF1 = '';
          this.ruleForm.IGFBP3 = '';
          this.ruleForm.AMH = '';
          this.ruleForm.INHB = '';
          this.ruleForm.MRI = '';
          this.ruleForm.supOther ='';
          this.ruleForm.ACTH = '';
          this.ruleForm.Hyd = '';
          this.ruleForm.OHP = '';
          this.ruleForm.DHEA = '';
          this.ruleForm.AD = '';
          this.ruleForm.bscanExplain = '';
          this.ruleForm.uterusOne = '';
          this.ruleForm.uterusTwo = '';
          this.ruleForm.uterusThr = '';
          this.ruleForm.intima = '';
          let tempEx =  {ovaLeftOne:'',ovaLeftTwo:"",ovaLeftThr:'',ovaRightOne:'',ovaRightTwo:'',ovaRightThr:'',testisLeftOne:'',testisLeftTwo:'',testisLeftThr:'',testisRightOne:'',testisRightTwo:'',testisRightThr:''};
          this.ruleForm.ovaLeftOne = tempEx['ovaLeftOne'];
          this.ruleForm.ovaLeftTwo = tempEx['ovaLeftTwo'];
          this.ruleForm.ovaLeftThr = tempEx['ovaLeftThr'];
          this.ruleForm.ovaRightOne = tempEx['ovaRightOne'];
          this.ruleForm.ovaRightTwo = tempEx['ovaRightTwo'];
          this.ruleForm.ovaRightThr = tempEx['ovaRightThr'];
          this.ruleForm.testisLeftOne = tempEx['testisLeftOne'];
          this.ruleForm.testisLeftTwo = tempEx['testisLeftTwo'];
          this.ruleForm.testisLeftThr = tempEx['testisLeftThr'];
          this.ruleForm.testisRightOne = tempEx['testisRightOne'];
          this.ruleForm.testisRightTwo = tempEx['testisRightTwo'];
          this.ruleForm.testisRightThr = tempEx['testisRightThr'];
          this.ruleForm.HCG = '';
          this.ruleForm.HCGT = '';
          this.ruleForm.HCGDHT = '';
          this.ruleForm.HCGAD = '';
          this.ruleForm.HCGT_ext = '';
          this.ruleForm.HCGDHT_ext = '';
          this.ruleForm.HCGAD_ext ='';
          this.ruleForm.LHmax = '';
          this.ruleForm.FSHmax = '';
          this.ruleForm.speKar = '';
          this.ruleForm.SRY = '';
          this.ruleForm.mutKind = '';
          this.ruleForm.sourMut = '';
          this.ruleForm.genMutName = '';
          this.ruleForm.baseMut = '';
          this.ruleForm.amiAciMut = '';
          this.ruleForm.other ='';
          this.ruleForm.operation = '';
          this.ruleForm.patRes = '';
          this.ruleForm.hanOpi = '';
          this.ruleForm.biolog = '';
          // this.ruleForm.biologBank = data.biolog_bank;
          let tempData ={};
          this.applyImagePath(tempData);
          this.chromeImagePath(tempData['染色体报告']);
          this.genImagePath(tempData['基因检测报告']);
          this.getFollow(this.filters);
          this.allFollow();
          this.$emit('dataList',this.ruleForm.firVisTime,this.ruleForm.morbidAge,this.ruleForm.chiefCom,this.ruleForm.mainDia,this.ruleForm.secDia,this.ruleForm.diaPlan);
        }).catch(() => {

        });
      },
      validateHeightFun(){
        validateHeightByBirthdate(this.birthTime,this.ruleForm.height)
      },
      validateHeights(age,height){
        validateHeight(age,height)
      }
    }
  }
</script>

<style lang="less">
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
      .thyroid-lie{
       display: flex;
       .lie-first{
        width: 280px;
       }
       .lie-con{
        width: 340px;
       }
       .lie-last{
        width: 340px;
       }
      }
      // .checkBox{
      //     position: relative;
      //     .checkInp{
      //       position: absolute;
      //       top: 472px;
      //       left: 0;
      //       font-size: 1vw;
      //     }
      //     .divBox2{
      //       // background-color: red;
      //       padding-top: 100px;
      //     }
      //   }
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
    .el-table .success-row {
        background: #f0f9eb;
    }
    .el-table .warning-row{
        background-color: #FFFBEB;
      }
    .el-table .warning-row td {
      color: #FAAD14;
    }
  }
</style>
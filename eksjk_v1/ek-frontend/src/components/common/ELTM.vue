<template>
  <div class="thyroid">
    <el-scrollbar class="scrollThy">
      <el-tabs :value="this.activeName">
        <el-tab-pane name="one" :style="{pointerEvents}" >
          <span slot="label">检查</span>
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
            <p class="thyroid-title">血常规报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌血常规报告"
                    :fileName="ruleForm.CBCUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">尿常规报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌血尿常规报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">肝肾功能报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌肝肾功能报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">IGF-1报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌IGF-1报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">IGFBP-3报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌IGFBP-3报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">GH激发试验报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌GH激发试验报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">血脂报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌血脂报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">乙型肝炎病毒检测报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌乙型肝炎病毒检测报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">性腺功能报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌性腺功能报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">肾上腺皮质功能报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌肾上腺皮质功能报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
            <p class="thyroid-title">肿瘤标志物报告：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                    :caseId="queryId"
                    organ="eltm"
                    category="E路童萌肿瘤标志物报告"
                    :fileName="ruleForm.boneAgeUrl"
                    @update:fileName="v =>upBoneImage(v)"
                    :editable="!isStatic"
                >
                </ImageUpload>
            </div>
          </div>
          <p class="thyroid-title">体格检查：</p>
          <div class="div-box"  :style="{pointerEvents}">
            <div class="thyroid-lie">
              <p class="lie-first">当前身高：<input v-model="ruleForm.height" class="input-underLine"/>cm</p>
              <p class="lie-con">当前体重：<input v-model="ruleForm.weight" class="input-underLine"/>kg</p>
              <p class="lie-con">Tanner分期：
                <el-select class="m-2" allow-create filterable default-first-option size="small" placeholder="分级" v-model="ruleForm.Tanner" style='width:100px'>
                    <el-option label="Ⅰ期" value="Ⅰ期"></el-option>
                    <el-option label="Ⅱ期" value="Ⅱ期"></el-option>
                    <el-option label="Ⅲ期" value="Ⅲ期"></el-option>
                    <el-option label="Ⅳ期" value="Ⅳ期"></el-option>
                    <el-option label="Ⅴ期" value="Ⅴ期"></el-option>
                </el-select>
              </p>
              <p class="lie-con">BMI：<input v-model="ruleForm.Bmi" class="input-underLine"/></p>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="two" :style="{pointerEvents}">
          <span slot="label">不良事件</span>
          <div class="div-box" :style="{pointerEvents}">
            <div class="thyroid-lie">
              <div>
                发生时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.eventTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="发生时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </div>
              <div style="margin-left: 60px">
               结束时间：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.endTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="结束时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </div>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                是否为严重不良事件：
                <el-radio class="elRadio" v-model="ruleForm.isAdEvent" label="是">是</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isAdEvent" label="否">否</el-radio>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 1000px">
                与研究药物的关系(LA-rhGH)：
                <br/>
                <br/>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="肯定相关">肯定相关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="可能有关">可能有关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="可能无关">可能无关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="肯定无关">肯定无关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="无法判断">无法判断</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.larhGH" label="NA">NA</el-radio>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                是否调整剂量：
                <el-radio class="elRadio" v-model="ruleForm.isAdjust" label="是">是</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isAdjust" label="否">否</el-radio>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 1000px">
                与研究药物的关系(rhGH)：
                <br/>
                <br/>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="肯定相关">肯定相关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="可能有关">可能有关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="可能无关">可能无关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="肯定无关">肯定无关</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="无法判断">无法判断</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.isRhGH" label="NA">NA</el-radio>
              </p>
            </div>
            <div class="thyroid-lie">
              <p>
                不良事件的转归：
                <br/>
                <br/>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="痊愈">痊愈</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="痊愈有后遗症">痊愈有后遗症</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="好转">好转</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="未好转">未好转</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="死亡">死亡</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.outcome" label="不详">不详</el-radio>
              </p>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="three" :style="{pointerEvents}">
          <span slot="label">用药记录</span>
          <p class="thyroid-title">GH用药记录：</p>
          <div class="div-box" :style="{pointerEvents}">
            <div class="thyroid-lie">
              <p class="lie-first">药物名称：
                <el-radio class="elRadio" v-model="ruleForm.medicationName" label="金赛增">金赛增</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.medicationName" label="赛增">赛增</el-radio>
              </p>
              <p v-if="ruleForm.medicationName === '金赛增'" class="lie-con">
                单次剂量（mg/kg/w）
                <input class="input-underLine" style="width: 50%" v-model="ruleForm.dose"/>
              </p>
              <p v-if="ruleForm.medicationName === '赛增'" class="lie-con">
                单次剂量（IU/kg/d）
                <input class="input-underLine" style="width: 50%" v-model="ruleForm.dose"/>
              </p>
             
            </div>
            <div class="thyroid-lie">
              <p>
                用药天数（上个访视至今）
                <input class="input-underLine" style="width: 40%" v-model="ruleForm.days"/>天
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                是否停药
                <el-radio class="elRadio" v-model="ruleForm.stopMedication" label="是">是</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.stopMedication" label="否">否</el-radio>
              </p>
              <p v-if="ruleForm.stopMedication === '是'" class="lie-con">
                停药原因
                <input class="input-underLine" style="width: 50%" v-model="ruleForm.stopReason"/>
              </p>
            </div>
          </div>
          <p class="thyroid-title">合并用药记录：</p>
          <div class="div-box" :style="{pointerEvents}">
            <div class="thyroid-lie">
              <div>
                记录日期：
                <el-date-picker
                        size="small"
                        v-model="ruleForm.recordDate"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="记录日期"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </div>
            </div>
            <div class="thyroid-lie">
              <p>
                受试者有无既往用药史？
                <el-radio class="elRadio" v-model="ruleForm.hasHistory" label="无">无</el-radio>
                <el-radio class="elRadio" v-model="ruleForm.hasHistory" label="有">有</el-radio>
              </p>
            </div>
            <div class="thyroid-lie">
              <div style="width: 99%" v-show="this.ruleForm.hasHistory === '有'">
                <el-table
                border
                :data="sampleBankMed"
                ref="table"
                >
                  <el-table-column
                    type="index"
                    width="50">
                  </el-table-column>
                  <el-table-column label="药物名称（通用名）" width="130">
                    <template slot-scope="scope"  width="130">
                      <input v-model="scope.row.input1750239223456" class="input-underLineMed"/>
                    </template>
                  </el-table-column>
                  <el-table-column label="单次用量" width="120">
                    <template slot-scope="scope">
                      <input v-model="scope.row.input1750239238024" class="input-underLineMed"/>
                    </template>
                  </el-table-column>
                  <el-table-column label="频次" width="120">
                    <template slot-scope="scope">
                      <input v-model="scope.row.number1750239261135" class="input-underLineMed"/>
                    </template>
                  </el-table-column>
                  <el-table-column label="给药途径" width="120">
                    <template slot-scope="scope">
                      <input v-model="scope.row.input1750239267879" class="input-underLineMed"/>
                    </template>
                  </el-table-column>
                  <el-table-column label="开始日期" width="160">
                    <template slot-scope="scope">
                      <el-date-picker
                        style="width: 136px"
                        size="small"
                        v-model="scope.row.date1750239297875"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="开始日期"
                        value-format="yyyy-MM-dd"
                      ></el-date-picker>
                    </template>
                  </el-table-column>
                  <el-table-column label="结束日期（年/月/日）或仍在使用" width="140">
                    <template slot-scope="scope">
                      <el-select class="m-2" allow-create filterable default-first-option size="small" placeholder="结束日期（年/月/日）或仍在使用" v-model="scope.row.single1750239311504" style='width:100px'>
                          <el-option label="继续使用" value="继续使用"></el-option>
                          <el-option label="结束" value="结束"></el-option>
                          
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="结束日期" width="160">
                    <template slot-scope="scope">
                      <el-date-picker
                        style="width: 136px"
                        size="small"
                        v-model="scope.row.date1750239394709"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="结束日期"
                        value-format="yyyy-MM-dd"
                      ></el-date-picker>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                      <el-button @click.native.prevent="delRow(scope.$index,sampleBankMed)" class="el-icon-minus"
                                size="mini"></el-button>
                      <el-button class="el-icon-plus" type="primary"
                                size="mini" @click="addBioRow(sampleBankMed)"></el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="four" :style="{pointerEvents}">
          <span slot="label">结果</span>
          <p class="thyroid-title">基因结果：</p>
          <div class="div-box">
              <p class="thyroid-title">基因结果上传：</p>
              <div style="width: 100%;display: flex">
                  <ImageUpload
                      :caseId="queryId"
                      organ="eltm"
                      category="E路童萌基因结果上传"
                      :fileName="ruleForm.boneAgeUrl"
                      @update:fileName="v =>upBoneImage(v)"
                      :editable="!isStatic"
                  >
                  </ImageUpload>
              </div>
              <div class="thyroid-lie">
                <p class="lie-first">
                  基因检测方法:<input v-model="ruleForm.geneMethod" class="input-underLine" style="width: 50%" />
                </p>
                <p class="lie-con">
                  基因结果:
                  <el-radio class="elRadio" v-model="ruleForm.geneRes" label="阴性">阴性</el-radio>
                  <el-radio class="elRadio" v-model="ruleForm.geneRes" label="阳性">阳性</el-radio>
                </p>
              </div>
              <div class="thyroid-lie" v-show="ruleForm.geneRes === '阳性'">
                <p class="lie-first">
                  基因名称:<input v-model="ruleForm.geneName" class="input-underLine" style="width: 50%" />
                </p>
                <p class="lie-first">
                  突变位点:<input v-model="ruleForm.genePoint" class="input-underLine" style="width: 50%" />
                </p>
                <p class="lie-con">
                  突变类型:<input v-model="ruleForm.geneType" class="input-underLine" style="width: 50%" />
                </p>
                <p class="lie-last">
                  遗传模式:<input v-model="ruleForm.geneMode" class="input-underLine" style="width: 50%" />
                </p>
              </div>
          </div>
          <p class="thyroid-title">染色体结果：</p>
          <div class="div-box">
            <p class="thyroid-title">染色体检查报告：</p>
            <div style="width: 100%;display: flex">
                  <ImageUpload
                      :caseId="queryId"
                      organ="eltm"
                      category="E路童萌染色体检查报告"
                      :fileName="ruleForm.boneAgeUrl"
                      @update:fileName="v =>upBoneImage(v)"
                      :editable="!isStatic"
                  >
                  </ImageUpload>
            </div>
            <div class="thyroid-lie">
              <p>
                染色体核型:
                <br/>
                <br/>
                <el-checkbox-group v-model="chromosom">
                  <el-checkbox  label="14b0f9e2-b2e6-49ad-b97b-34d3f63ec1bf">正常核型</el-checkbox>
                  <el-checkbox  label="a3407b7f-ec6c-4531-81ca-5e5b45fc2890">21三体综合征</el-checkbox>
                  <el-checkbox  label="b5564f31-5508-4168-b287-b082d76a352c">特纳综合征</el-checkbox>
                  <el-checkbox  label="64398b35-4eae-4a33-b51a-8030dd984df5">克氏综合征</el-checkbox>
                  <el-checkbox  label="17328f52-5090-4dce-9aab-0dfac86547da">染色体平衡易位</el-checkbox>
                  <el-checkbox  label="e5cd20a8-93b5-436a-a16b-98fba2a52050">染色体嵌合体</el-checkbox>
                  <el-checkbox  label="0971136a-ee1d-4d0f-8dbb-cca3565b374f">其他异常核型</el-checkbox>
                </el-checkbox-group>
                </p>
                <input v-show="chromosom.includes('0971136a-ee1d-4d0f-8dbb-cca3565b374f')" v-model="ruleForm.chromosomOther" class="input-underLine" style="width: 200px;margin-left: 20px;" placeholder="请输入其他异常核型" />
            </div>
          </div>
          <p class="thyroid-title">影像学结果：</p>
          <div class="div-box">
            <p class="thyroid-title">骨龄X线诊断报告：</p>
            <p style="font-size: 14px;color: gray;">拍摄左手及腕部光片</p>
            <div style="width: 100%;display: flex">
                  <ImageUpload
                      :caseId="queryId"
                      organ="eltm"
                      category="E路童萌骨龄X线诊断报告"
                      :fileName="ruleForm.boneAgeUrl"
                      @update:fileName="v =>upBoneImage(v)"
                      :editable="!isStatic"
                  >
                  </ImageUpload>
            </div>
            <p class="thyroid-title">头颅（包含垂体、下丘脑）MRI/CT检查（GH治疗者必填）：</p>
            <div style="width: 100%;display: flex">
                  <ImageUpload
                      :caseId="queryId"
                      organ="eltm"
                      category="E路童萌头颅（包含垂体、下丘脑）MRI/CT检查（GH治疗者必填）"
                      :fileName="ruleForm.boneAgeUrl"
                      @update:fileName="v =>upBoneImage(v)"
                      :editable="!isStatic"
                  >
                  </ImageUpload>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="five" :style="{pointerEvents}">
          <span slot="label">基线临床症状和体征</span>
          <p class="thyroid-title">基线临床症状和体征：</p>
          <div class="div-box" :style="{pointerEvents}">
            <div class="thyroid-lie">
              <p class="lie-first">一般症状：<input v-model="ruleForm.generalSymptoms" class="input-underLine" style="width: 50%" plceholder="如头痛、疲劳、发力、注射部位反应等"/></p>
              <p class="lie-con">代谢相关症状：<input v-model="ruleForm.metabolicSymptoms" class="input-underLine" style="width: 50%" plceholder="一过性高血糖、体重变化、食欲改变等"/></p>
              <p class="lie-last">骨骼和肌肉症状：<input v-model="ruleForm.boneMuscleSymptoms" class="input-underLine"  style="width: 50%" plceholder="关节痛、肌肉痛、骨骼畸形等"/></p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">内分泌症状：<input v-model="ruleForm.endocrineSymptoms" class="input-underLine" style="width: 50%" plceholder="甲状腺功能减退、男子女性型乳房等"/></p>
              <p class="lie-con">其他症状：<input v-model="ruleForm.otherSymptoms" class="input-underLine" style="width: 50%" plceholder="水肿、皮肤痣数量增加、颅内高压等"/></p>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="six">
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
          disClass="eltm"
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
  // import fileUpload from "../imageViewer/fileUpload";
  // import {ICDDataArray}  from '../../utils/ICDData';  

  const ImageString = JSON.stringify(otherImage)

  export default {
    name: "FSS",
    //fileUpload
    components: {ImageUpload, ShortFollow, },
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
      TGheight:String,
      TGweight:String,
      TGBMI:String
    },
    mounted() {
      if (this.isStatic) {
        this.pointerEvents = "none";
      }
      if(this.default === "follow"){
        this.activeName = "six"
      }
      if(this.$route.query.follow=== "follow"){
          this.activeName = "six"
        }
      // this.ruleForm.height = this.TGheight
      // this.ruleForm.weight = this.TGweight
      // this.ruleForm.Bmi = this.TGBMI
      this.$set(this.ruleForm, 'height', this.TGheight)
      this.$set(this.ruleForm, 'weight', this.TGweight)
      this.$set(this.ruleForm, 'Bmi', this.TGBMI)
      // this.allICDData=ICDDataArray
      // console.log(this.allICDData);
    },
    data() {
      return {
      //   options: [{
      //     value: '生长激素缺乏症',
      //     label: '生长激素缺乏症',
      //     }, {
      //       value: '特发性矮小',
      //       label: '特发性矮小',
      //       children: [{
      //         value: '家族性矮小',
      //         label: '家族性矮小'
      //       }, {
      //         value: '小于胎龄儿生后持续身材矮小',
      //         label: '小于胎龄儿生后持续身材矮小'
      //       },{
      //         value: '其他(手填或不填)',
      //         label: '其他(手填或不填)'
      //       }]
      //   },
      //   {
      //     value: '其他',
      //     label: '其他',
      //   }
      // ],
        // allICDData: '',//国际疾病数组
        queryPId:"",
        dialogVisible: false,
        stuts: "",
        // unit: [],
        pointerEvents: "",
        // boneAgeImageUrl: "",
        // chromImageUrl: '',
        // genImageUrl: '',
        cases: [],
        activeName: 'one',
        // tiesData:[],
        // sampleBank: [{id: '', name: ''},],
        // sampleBankFa:[{id: '', name: ''},],
        sampleBankMed:[{ input1750239223456: '',input1750239238024:'',number1750239261135:'',input1750239267879:'',date1750239297875:'',single1750239311504:'',date1750239394709:''}],
        // sampleBankMo:[{id: '', name: ''},],
        // genData:[
        //   {genName: '', Rna: '',mutationType:'',other:'',  infestansLevel:'', amino:''},//, father: '', mother: ''
        // ],
        ruleForm: {
          // queryId:'',
          // motDevBack: '1',
          // sport: '',
          // lanDevBack: '1',
          // language: '',
          // intDevBack: '1',
          // intelligence: '',
          // abnHear: '1',
          // hear: '',
          // recInfHis: '1',
          // infection: '',
          // conHis: '1',
          // pastOther: '',

          // firVisTime: '',
          // morbidAge: '',
          // chiefCom: '',
          // growRate: '',
          // rate: '',
          // menarchy: '',
          // menarchyTime: '',
          height: '',
          weight: '',
          Bmi: '',
          // breastDev: '',
          // breastDevRight:'',
          // exGenitalia: '',
          // pubicHair: '',
          // armLength: '',
          // specialFace: '',
          // specialFaceDesc: '',
          // scoliosis: '',
          // scoliosisDegree: '',
          // rash: '',
          // rashDescribe: '',
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
          urinalysisTime: '',
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
          // projectName: '',//项目名称（其他图片上传）
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
          // height: '',//当前身高
          // weight: '',//当前体重
          // Bmi: '',//当前BMI
          Tanner: '',//Tanner分期
          // BMI: '',//BMI
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
          // genData:[],
          boneAgeUrl:'eltm',//骨年龄报告上传

          //不良事件
          eventTime:'',//发生时间
          endTime:'',//结束时间
          isAdEvent:"",//是否为严重不良事件
          larhGH:'',//与研究药物的关系(LA-rhGH)
          isAdjust:'',//是否调整剂量
          isRhGH:'',//与研究药物的关系(rhGH)
          outcome:'',//不良事件的转归
          //用药记录
          medicationName:'',//药物名称
          dose:'',//单次剂量
          days:'',//用药天数
          stopMedication:'',//是否停药
          stopReason:'',//停药原因
          //合并用药记录
          recordDate:'',//记录日期
          hasHistory:'',//受试者有无既往用药史
          sampleBankMed:[],
          //基因结果
          geneMethod:'',//基因检测方法
          geneRes:'',//基因结果
          geneName:'',//基因名称
          genePoint:'',//突变位点
          geneType:'',//突变类型
          geneMode:'',//遗传模式
          //染色体结果
          chromosom: [],//染色体核型
          chromosomOther:'',//其他异常核型
          //基线临床症状和体征
          generalSymptoms:'',//一般症状
          metabolicSymptoms:'',//代谢相关症状
          boneMuscleSymptoms:'',//骨骼和肌肉症状
          endocrineSymptoms:'',//内分泌症状
          otherSymptoms:'',//其他症状
        },
        chromosom: [],
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
          },
    formData() {
      return {
        height: this.TGheight,
        weight: this.TGweight,
        Bmi: this.TGBMI
      }
    }
    },
    activated() {
      // this.boneAgeImageUrl = '';
      // this.chromImageUrl = '';
      // this.genImageUrl = '';
      if (this.$route.query.queryId) {
        this.getCase();
      } else {
        for (let key in this.ruleForm) {
          this.ruleForm[key] = ''
        }
        // this.ruleForm.motDevBack = '1';
        // this.ruleForm.lanDevBack = '1';
        // this.ruleForm.intDevBack = '1';
        // this.ruleForm.abnHear = '1';
        // this.ruleForm.recInfHis = '1';
        // this.ruleForm.conHis = '1';
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
      formData: {
      deep: true,
      handler(val) {
        this.ruleForm.height = val.height || ''
        this.ruleForm.weight = val.weight || ''
        this.ruleForm.Bmi = val.Bmi || ''
      },
      immediate: true // 一进页面就执行
    }
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
      addRow(tableData) {
        tableData.push({relation: '', tAge:'', height: '', weight: '', age: '', health: '', disName: ''})
      },
      delRow(index, rows) {
        rows.splice(index, 1);
      },
      getData() {
        let data = null;
        this.ruleForm.chromosom = this.chromosom;
        return data;
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
        if (!this.ruleForm.Wt || this.ruleForm.Wt === '0' || Number(this.ruleForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.ruleForm.rhGHdoseKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.ruleForm.rhGHdose) || 0;
        const wtData = Number(this.ruleForm.Wt) || 0;
        if (wtData === 0) {
          this.ruleForm.rhGHdoseKG = '';
          return;
        }
        const result = kgData / wtData;
        this.ruleForm.rhGHdoseKG = result.toFixed(4);
      },
      // getAge() {
      //   let strDate1 = this.birthTime + ".0";
      //   // let strDate2 = this.ruleForm.firVisTime + "   00:00:00.0";
      //   strDate1 = strDate1.substring(0, strDate1.lastIndexOf(".")).replace(/-/g, "/ ");
      //   strDate2 = strDate2.substring(0, strDate2.lastIndexOf(".")).replace(/-/g, "/ ");
      //   //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
      //   let date1 = Date.parse(strDate1);
      //   let date2 = Date.parse(strDate2);
      //   let day = Math.ceil((date2 - date1) / (60 * 60 * 1000 * 24));
      //   let year = Math.floor(day / 365);
      //   let y = day % 365;
      //   let month = Math.floor(y / 30);
      //   this.ruleForm.morbidAge = (year + month/12).toFixed(1)
      // },
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
      // upChromImage(v) {
      //   this.chromImageUrl = v;
      // },

      // upGenImage(v) {
      //   this.genImageUrl = v;
      // },

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

      // resetImage(url,category){
      //   const data = {
      //     queryId: this.queryId,
      //     organ: "fss",
      //     path:  category +"-"+ url ,
      //   }
      //   request.deleteImage(data, () => {
      //     console.log(data)
      //     if(category === "染色体报告"){
      //       this.chromImageUrl = '';
      //     }else if(category === "基因检测报告"){
      //       this.genImageUrl = ''
      //     }
      //   });
      // },

      addData() {
      //   this.ruleForm.genData = this.genData;
      //   // this.ruleForm.sampleClass = this.sampleBank;
      //   // this.ruleForm.sampleClassFa = this.sampleBankFa;
        this.ruleForm.sampleBankMed = this.sampleBankMed;
        this.ruleForm.chromosom = this.chromosom;
      //   // this.ruleForm.sampleClassMo = this.sampleBankMo;
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
          console.log(data,"suoyoushuj")
          // this.familyData = JSON.parse(data.fam_his.replace(/'/g, "\""));
          // this.genData = data.gen_mut_name ? JSON.parse(data.gen_mut_name.replace(/'/g, "\"")) : [{genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:''}];//, father: '', mother: ''
          // this.$emit('genDataHandle', this.genData || []);
          // let temMot = data.mot_dev_back ? JSON.parse(data.mot_dev_back) : {};
          // this.ruleForm.motDevBack = temMot['motDevBack'];
          // this.ruleForm.sport = temMot['sport'];
          // let temLan = data.lan_dev_back ? JSON.parse(data.lan_dev_back) : {};
          // this.ruleForm.lanDevBack = temLan['lanDevBack'];
          // this.ruleForm.language = temLan['language'];
          // let temInt = data.int_dev_back ? JSON.parse(data.int_dev_back) : {};
          // this.ruleForm.intDevBack = temInt['intDevBack'];
          // this.ruleForm.intelligence = temInt['intelligence'];
          // let temHear = data.abn_hear ? JSON.parse(data.abn_hear) : {};
          // this.ruleForm.abnHear = temHear['abnHear'];
          // this.ruleForm.hear = temHear['hear'];
          // let temRec = data.rec_inf_his ? JSON.parse(data.rec_inf_his) : {};
          // this.ruleForm.recInfHis = temRec['recInfHis'];
          // this.ruleForm.infection = temRec['infection'];
          // this.ruleForm.conHis = data.con_his;
          // this.ruleForm.pastOther = data.past_other;
          // let temBs = data.med_his ? JSON.parse(data.med_his) : {};
          // this.ruleForm.firVisTime = temBs['firVisTime'];
          // this.ruleForm.morbidAge = temBs['morbidAge'];
          // this.ruleForm.chiefCom = temBs['chiefCom'];
          // this.ruleForm.growRate = temBs['growRate'];
          // this.ruleForm.rate = temBs['rate'];
          // this.ruleForm.menarchy = temBs['menarchy'];
          // this.ruleForm.menarchyTime = temBs['menarchyTime'];
          // let tgjc = data.phy_exa ? JSON.parse(data.phy_exa) : {};
      
          // this.ruleForm.height = tgjc['height'];
          // this.ruleForm.weight = tgjc['weight'];
          // this.ruleForm.Bmi = tgjc['Bmi'];
          this.ruleForm.Tanner = data.tanner;
          //不良事件
          this.ruleForm.eventTime = data.star_time;
          this.ruleForm.endTime = data.end_time;
          this.ruleForm.isAdEvent =data.is_adv_eve;
          this.ruleForm.larhGH = data.la_rhGH;
          this.ruleForm.isAdjust = data.is_adjust;
          this.ruleForm.isRhGH = data.rhGH;
          this.ruleForm.outcome = data.outcome;
          //用药记录
          this.ruleForm.medicationName = data.med_name;
          this.ruleForm.dose = data.dose;
          this.ruleForm.days = data.days;
          this.ruleForm.stopMedication = data.stop_med;
          this.ruleForm.stopReason = data.stop_rea;
          this.ruleForm.recordDate = data.rec_date;
          this.ruleForm.hasHistory = data.is_has_his;
          // this.sampleBankMed = data.has_his ? JSON.parse(data.has_his.replace(/'/g, "\"")) : [{name: '',dosage:'',frequency:'',path:'',startDate:'',isUse:'',endDate:''}];
          let hasHis = data.has_his;
          console.log(hasHis,"hasHis")
          if (hasHis) {
          // 1. 预处理：单引号转双引号 + 大写布尔值转小写
          let jsonStr = hasHis
            .replace(/'/g, '"')         // 单引号 → 双引号
            .replace(/False/g, 'false') // False → false（修复报错）
            .replace(/True/g, 'true');  // True → true（兼容）

          // 2. 解析 JSON
          let rawList = JSON.parse(jsonStr);
          
          // 3. 转换字段
          this.sampleBankMed = rawList.map(item => {
            // 处理继续/结束状态
            let isUseVal = item.single1750239311504?.value;
            let isUse = '';
            if (isUseVal === 'fb189df4-6126-49a2-af10-f8bfa5b58901') {
              isUse = '1'; // 继续使用
            } else if (isUseVal === '0c778528-cf23-45b9-bed8-621366bcd34c') {
              isUse = '2'; // 结束
            }
            return {
              name: item.input1750239223456 || '',
              dosage: item.input1750239238024 || '',
              frequency: item.number1750239261135 || '',
              path: item.input1750239267879 || '',
              startDate: item.date1750239297875 || '',
              endDate: item.date1750239394709 || '',
              isUse: isUse,
              
              // 保留原有字段，防止响应式丢失
              genName: '',
              Rna: '',
              mutationType:'',
              other:'',
              infestansLevel:'',
              amino:'',
              father: '',
              mother: ''
              };
            });
          } else {
            // 无数据时给空行
            this.sampleBankMed =[{ input1750239223456: '',input1750239238024:'',number1750239261135:'',input1750239267879:'',date1750239297875:'',single1750239311504:'',date1750239394709:''}];
          }
          //基因结果
          this.ruleForm.geneMethod=data.gene_method;
          this.ruleForm.geneRes=data.gene_res;
          this.ruleForm.geneName=data.gene_name;
          this.ruleForm.genePoint=data.gene_point;
          this.ruleForm.geneType=data.gene_type;
          this.ruleForm.geneMode=data.gene_mode;
          //染色体结果
          // this.ruleForm.chromosom = Array.isArray(data.chromosom) ? data.chrom : (data.chrom ? [data.chrom] : [])
          // this.chromosom = Array.isArray(data.chrom)? data.chrom: (data.chrom ? JSON.parse(data.chrom.replace(/'/g, '"')) : []);
          this.chromosom = JSON.parse(data.chrom ? data.chrom.replace(/'/g, '"') : '[]');
          this.ruleForm.chromosomOther=data.chrom_other;
          this.ruleForm.generalSymptoms=data.gen_sym;
          this.ruleForm.metabolicSymptoms=data.met_sym;
          this.ruleForm.boneMuscleSymptoms=data.bone_sym;
          this.ruleForm.endocrineSymptoms=data.endo_sym;
          this.ruleForm.otherSymptoms=data.other_sym;

          // this.ruleForm.breastDev = tgjc['breastDev'];
          // this.ruleForm.breastDevRight = tgjc['breastDevRight'];
          // this.ruleForm.exGenitalia = tgjc['exGenitalia'];
          // this.ruleForm.pubicHair = tgjc['pubicHair'];
          // this.ruleForm.armLength = tgjc['armLength'];
          // this.ruleForm.specialFace = tgjc['specialFace'];
          // this.ruleForm.specialFaceDesc = tgjc['specialFaceDesc'];
          // this.ruleForm.scoliosis = tgjc['scoliosis'];
          // this.ruleForm.scoliosisDegree = tgjc['scoliosisDegree'];
          // this.ruleForm.rash = tgjc['rash'];
          // this.ruleForm.rashDescribe = tgjc['rashDescribe'];
          let tempLab = data.lab_exa ? JSON.parse(data.lab_exa) : {};
          // this.ruleForm.LH = tempLab['LH'] || '';
          // this.ruleForm.FSH = tempLab['FSH'] || '';
          // this.ruleForm.LHFSHTime = tempLab['LHFSHTime'] || '';
          // this.ruleForm.E2 = tempLab['E2']|| '';
          // this.ruleForm.E2Time = tempLab['E2Time'] || '';
          // this.ruleForm.T = tempLab['T'] || '';
          // this.ruleForm.TTime = tempLab['TTime'] || '';
          // this.ruleForm.PRL = tempLab['PRL'] || '';
          // this.ruleForm.PRLTime = tempLab['PRLTime'] || '';
          this.ruleForm.IGF = tempLab['IGF'] || '';
          this.ruleForm.IGFBP3 = tempLab['IGFBP3'] || '';
          this.ruleForm.IGFBPTime = tempLab['IGFBPTime'] || '';
          this.ruleForm.thyroid = tempLab['thyroid'];
          this.ruleForm.thyroidDescribe = tempLab['thyroidDescribe'] || '';
          this.ruleForm.thyroidTime = tempLab['thyroidTime'] || '';
          this.ruleForm.ACTH = tempLab['ACTH'] || '';
          this.ruleForm.ACTHTime = tempLab['ACTHTime'] || '';
          this.ruleForm.cortisol = tempLab['cortisol'] || '';
          this.ruleForm.cortisolTime = tempLab['cortisolTime'] || '';
          this.ruleForm.DHEAS = tempLab['DHEAS'] || '';
          this.ruleForm.DHEATime = tempLab['DHEATime'] || '';
          this.ruleForm.OHP = tempLab['OHP'] || '';
          this.ruleForm.OHPTime = tempLab['OHPTime'] || '';
          this.ruleForm.blood = tempLab['blood'] || '';
          this.ruleForm.bloodDescribe = tempLab['bloodDescribe'] || '';
          this.ruleForm.bloodTime = tempLab['bloodTime'] || '';
          this.ruleForm.urinalysis = tempLab['urinalysis'] || '';
          this.ruleForm.urinalysisDescribe = tempLab['urinalysisDescribe'] || '';
          this.ruleForm.urinalysisTime = tempLab['urinalysisTime']=='null'?'':tempLab['urinalysisTime'] || '';
          this.ruleForm.LAKLGE = tempLab['LAKLGE'] || '';
          this.ruleForm.laklgeDescribe = tempLab['laklgeDescribe'] || '';
          this.ruleForm.LAKLGETime = tempLab['LAKLGETime'] || '';
          this.ruleForm.HBs = tempLab['HBs'] || '';
          this.ruleForm.HBsTime = tempLab['HBsTime'] || '';
          this.ruleForm.HBsDescribe = tempLab['HBsDescribe'] || '';
          this.ruleForm.gh = tempLab['gh'] || '';
          this.ruleForm.ghTime = tempLab['ghTime'] || '';
          this.ruleForm.glyHemA = tempLab['glyHemA'] || '';
          this.ruleForm.glyHemATime = tempLab['glyHemATime'] || '';
          this.ruleForm.electdiogramTime = tempLab['electdiogramTime'] || '';

          this.ruleForm.electdiogram = data.electr;
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
          // let mainDias = data.main_dia ? JSON.parse(data.main_dia) : {};
          // let mainDiaList=  mainDias['mainDia'].slice(1, -1);
          // this.ruleForm.mainDia=mainDiaList.split(',').map(item => {  
          //       return item.trim().replace(/'/g, '');  
          //   });  
          // this.ruleForm.mainDiaIllustrate=mainDias['mainDiaIllustrate'];
          // this.ruleForm.DiaIllustrate=mainDias['DiaIllustrate'];
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
          // this.ruleForm.diaPlan = tempDia['diaPlan'];
          // this.ruleForm.rhGH = tempDia['rhGH'];
          // this.ruleForm.rhGHdose = tempDia['rhGHdose'];
          // this.ruleForm.GnRHa = tempDia['GnRHa'];
          // this.ruleForm.GnRHadose = tempDia['GnRHadose'];
          // this.ruleForm.anstrozole = tempDia['anstrozole'];
          let tempBio = data.bio_sam_bank ? JSON.parse(data.bio_sam_bank) : {};
          this.ruleForm.bioBank = tempBio['bioBank'];
          this.ruleForm.sampleId = tempBio['sampleId'];
          // this.sampleBank = tempBio['sampleClass'] ? JSON.parse(tempBio['sampleClass'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          let tempBioFa = data.f_bio_sam_bank ? JSON.parse(data.f_bio_sam_bank) : {};
          this.ruleForm.bioBankFa = tempBioFa['bioBankFa'];
          this.ruleForm.sampleIdFa = tempBioFa['sampleIdFa'];
          // this.sampleBankFa = tempBioFa['sampleClassFa'] ? JSON.parse(tempBioFa['sampleClassFa'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          let tempBioMo = data.m_bio_sam_bank ? JSON.parse(data.m_bio_sam_bank) : {};
          this.ruleForm.bioBankMo = tempBioMo['bioBankMo'];
          this.ruleForm.sampleIdMo = tempBioMo['sampleIdMo'];
          // this.sampleBankMo = tempBioMo['sampleClassMo'] ? JSON.parse(tempBioMo['sampleClassMo'].replace(/'/g, "\"")) : [{id: '', name: ''}];
          this.ruleForm.secDia = data.sec_dia;
          this.ruleForm.speKar = data.spe_kar;
          this.ruleForm.SRY = data.SRY;
          this.ruleForm.mutKind = data.mut_kind;
          this.ruleForm.sourMut = data.sour_mut;
          this.ruleForm.genMutName = data.gen_mut_name;
          this.ruleForm.baseMut = data.base_mut;
          this.ruleForm.amiAciMut = data.ami_aci_mut;
          let tempData = data.B_ult_image ? JSON.parse(data.B_ult_image) : {};
          this.applyImagePath(tempData);
          // this.chromeImagePath(tempData['染色体报告']);
          // this.genImagePath(tempData['基因检测报告']);
          this.getFollow(this.filters);
          // this.allFollow();
          // this.$emit('dataList',this.ruleForm.firVisTime,this.ruleForm.morbidAge,this.ruleForm.chiefCom,this.ruleForm.mainDia,this.ruleForm.secDia,this.ruleForm.diaPlan);
          // console.log(data)
        }, error => {
          // this.$emit('genDataHandle', []);
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

      // chromeImagePath(imagePath) {
      //   if (imagePath && imagePath[0]) {
      //     this.chromImageUrl = imagePath[0];
      //   } else {
      //     this.chromImageUrl = '';
      //   }
      // },
      // genImagePath(imagePath) {
      //   if (imagePath && imagePath[0]) {
      //     this.genImageUrl = imagePath[0];
      //   } else {
      //     this.genImageUrl = '';
      //   }
      // },
      addFollow() {
        let follow = this.$refs.shortFollow.followForm;
        request.addFollow(follow, data => {
          this.$message('提交成功');
          this.dialogVisible = false;
          this.getFollow(this.filters);
          // this.allFollow();
          console.log(data)
        }, error => {
          this.$message('提交失败');
          console.log(error)
        })
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

      // allFollow(){
      //   const queryId = this.queryId;
      //   request.allFollow({queryId},data=>{
      //     // this.getEchartData(data)
      //   });
      // },

      // getEchartData(data){
      //   const chart = this.$refs.chart;
      //   const myChart = this.$echarts.init(chart);
      //   let hData = [];
      //   let wData = [];
      //   let oneLine = [];
      //   let twoLine = [];
      //   hData.push([this.ruleForm.morbidAge,this.ruleForm.height]);
      //   wData.push([this.ruleForm.morbidAge,this.ruleForm.weight]);
      //   data.forEach(item =>{
      //     let strDate1 = this.birthTime + "   00:00:00.0";
      //     let strDate2 = item.foll_time.substring(0,10)+ "   00:00:00.0";
      //     strDate1 = strDate1.substring(0, strDate1.lastIndexOf(".")).replace(/-/g, "/ ");
      //     strDate2 = strDate2.substring(0, strDate2.lastIndexOf(".")).replace(/-/g, "/ ");
      //     //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
      //     let date1 = Date.parse(strDate1);
      //     let date2 = Date.parse(strDate2);
      //     let day = Math.ceil((date2 - date1) / (60 * 60 * 1000 * 24));
      //     let year = Math.floor(day / 365);
      //     let ageYear = day % 365;
      //     let month = Math.floor(ageYear / 30);
      //     let ageMonth = month / 12;
      //     let age = (year + ageMonth).toFixed(1);
      //     hData.push([age,item.Ht]);
      //     wData.push([age,item.Wt]);
      //     let diaPlan = item.dia_trea_plan ? JSON.parse(item.dia_trea_plan) : {};
      //     if(diaPlan['diaPlan'] === '2' || diaPlan['diaPlan'] === '3'){
      //       oneLine.push([age,87])
      //     }
      //     if(diaPlan['diaPlan'] === '3'){
      //       twoLine.push([age,82])
      //     }
      //   });

      //   if (chart) {
      //     let option  ={};
      //     if(this.sex === '1'){
      //       option = {
      //         tooltip: {
      //           trigger: 'axis'
      //         },
      //         legend: {
      //           type: 'plain',
      //           data: ['身高', '体重','GH','GH+GnRHa']
      //         },
      //         xAxis: {
      //           boundaryGap: false,
      //           splitNumber: 15,
      //           min: 3,
      //           max: 18,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         },
      //         yAxis: [{
      //           name: '身高/cm',
      //           type: 'value',
      //           position: 'left',
      //           splitNumber: 20,
      //           min: 80,
      //           max: 190,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         }, {
      //           name: '体重/kg',
      //           type: 'value',
      //           min: 0,
      //           max: 110,
      //           splitNumber: 11,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         }],
      //         series: [
      //           {
      //             name: '3rd',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 89.7], [4, 96.7], [5, 103.3], [6, 109.1], [7, 114.6], [8, 119.9], [9, 124.6], [10, 128.7], [11, 132.9], [12, 138.1], [13, 145.0], [14, 152.3], [15, 157.5], [16, 159.9], [17, 160.9], [18, 161.3]]
      //           },
      //           {
      //             name: '10th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 91.9], [4, 99.1], [5, 105.8], [6, 111.8], [7, 117.6], [8, 123.1], [9, 128.0], [10, 132.3], [11, 136.8], [12, 142.5], [13, 149.6], [14, 156.7], [15, 161.4], [16, 163.6], [17, 164.5], [18, 164.9]]
      //           },
      //           {
      //             name: '25th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 94.2], [4, 101.4], [5, 108.4], [6, 114.6], [7, 120.6], [8, 126.3], [9, 131.4], [10, 136.0], [11, 140.8], [12, 147.0], [13, 154.3], [14, 161.0], [15, 165.4], [16, 167.4], [17, 168.2], [18, 168.6]]
      //           },
      //           {
      //             name: '50th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 96.8], [4, 104.1], [5, 111.3], [6, 117.7], [7, 124.0], [8, 130.0], [9, 135.4], [10, 140.2], [11, 145.3], [12, 151.9], [13, 159.5], [14, 165.9], [15, 169.8], [16, 171.6], [17, 172.3], [18, 172.7]]
      //           },
      //           {
      //             name: '75th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 99.4], [4, 106.9], [5, 114.2], [6, 120.9], [7, 127.4], [8, 133.7], [9, 139.3], [10, 144.4], [11, 149.9], [12, 157.0], [13, 164.8], [14, 170.7], [15, 174.2], [16, 175.8], [17, 176.4], [18, 176.7]]
      //           },
      //           {
      //             name: '90th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 101.8], [4, 109.3], [5, 116.9], [6, 123.7], [7, 130.5], [8, 137.1], [9, 142.9], [10, 148.2], [11, 154.0], [12, 161.5], [13, 169.5], [14, 175.1], [15, 178.2], [16, 179.5], [17, 180.1], [18, 180.4]]
      //           },
      //           {
      //             name: '97th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 104.1], [4, 111.8], [5, 119.6], [6, 126.6], [7, 133.7], [8, 140.4], [9, 146.5], [10, 152.0], [11, 158.1], [12, 166.0], [13, 174.2], [14, 179.4], [15, 182.0], [16, 183.2], [17, 183.7], [18, 183.9]]
      //           },
      //           {
      //             name: '身高',
      //             data: hData,
      //             type: 'line',
      //             smooth: true,
      //             yAxisIndex: 0,
      //           },
      //           {
      //             name: '体重',
      //             data: wData,
      //             type: 'line',
      //             yAxisIndex: 1,
      //             smooth: true
      //           },
      //           {
      //             name: 'GH',
      //             type: 'line',
      //             symbol: 'none',
      //             itemStyle: {
      //               normal: {
      //                 color: '#ff3e1c',
      //                 lineStyle: {
      //                   width: 5,
      //                   color: '#ff3e1c',
      //                 },
      //               }
      //             },
      //             data: oneLine,
      //           },
      //           {
      //             name: 'GH+GnRHa',
      //             type: 'line',
      //             symbol: 'none',
      //             itemStyle: {
      //               normal: {
      //                 color: '#6570ff',
      //                 lineStyle: {
      //                   width: 5,
      //                   color: '#6570ff',
      //                 },
      //               }
      //             },
      //             data: twoLine,
      //           },
      //         ]
      //       }
      //     }else {
      //       option = {
      //         tooltip: {
      //           trigger: 'axis'
      //         },
      //         legend: {
      //           type: 'plain',
      //           data: ['身高', '体重','GH','GH+GnRHa']
      //         },
      //         xAxis: {
      //           boundaryGap: false,
      //           splitNumber: 15,
      //           min: 3,
      //           max: 18,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         },
      //         yAxis: [{
      //           name: '身高/cm',
      //           type: 'value',
      //           position: 'left',
      //           splitNumber: 20,
      //           min: 80,
      //           max: 190,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         }, {
      //           name: '体重/kg',
      //           type: 'value',
      //           min: 0,
      //           max: 110,
      //           splitNumber: 11,
      //           offset: 0,
      //           axisLine: {
      //             show: true,
      //             symbol: ['none', 'arrow'],
      //             symbolSize: [8, 8],
      //             symbolOffset: [0, 8],
      //             lineStyle: {
      //               color: 'black',
      //               width: 1,
      //               type: 'solid'
      //             }
      //           },
      //           axisTick: {
      //             show: false,
      //           },
      //         }],
      //         series: [
      //           {
      //             name: '3rd',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 88.6], [4, 95.8], [5, 102.3], [6, 108.1], [7, 113.3], [8, 118.5], [9, 123.3], [10, 128.3], [11, 134.2], [12, 140.2], [13, 145.0], [14, 147.9], [15, 149.5], [16, 149.8], [17, 150.1], [18, 150.4]]
      //           },
      //           {
      //             name: '10th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 90.8], [4, 98.1], [5, 104.8], [6, 110.8], [7, 116.2], [8, 121.6], [9, 126.7], [10, 132.1], [11, 138.2], [12, 144.1], [13, 148.6], [14, 151.3], [15, 152.8], [16, 153.1], [17, 153.4], [18, 153.7]]
      //           },
      //           {
      //             name: '25th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 93.1], [4, 100.4], [5, 107.3], [6, 113.5], [7, 119.2], [8, 124.9], [9, 130.2], [10, 135.9], [11, 142.2], [12, 148.0], [13, 152.2], [14, 154.8], [15, 156.1], [16, 156.4], [17, 156.7], [18, 157.0]]
      //           },
      //           {
      //             name: '50th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 95.6], [4, 103.1], [5, 110.2], [6, 116.6], [7, 122.5], [8, 128.5], [9, 134.1], [10, 140.1], [11, 146.6], [12, 152.4], [13, 156.3], [14, 158.6], [15, 159.8], [16, 160.1], [17, 160.3], [18, 160.6]]
      //           },
      //           {
      //             name: '75th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 98.2], [4, 105.7], [5, 113.1], [6, 119.7], [7, 125.9], [8, 132.1], [9, 138.0], [10, 144.4], [11, 151.1], [12, 156.7], [13, 160.3], [14, 162.4], [15, 163.5], [16, 163.8], [17, 164.0], [18, 164.2]]
      //           },
      //           {
      //             name: '90th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 100.5], [4, 108.2], [5, 115.7], [6, 122.5], [7, 129.0], [8, 135.4], [9, 141.6], [10, 148.2], [11, 155.2], [12, 160.7], [13, 164.0], [14, 165.9], [15, 166.8], [16, 167.1], [17, 167.3], [18, 167.5]]
      //           },
      //           {
      //             name: '97th',
      //             type: 'line',
      //             symbol: 'none',
      //             data: [[3, 102.9], [4, 110.6], [5, 118.4], [6, 125.4], [7, 132.1], [8, 138.7], [9, 145.1], [10, 152.0], [11, 159.2], [12, 164.5], [13, 167.6], [14, 169.3], [15, 170.1], [16, 170.3], [17, 170.5], [18, 170.7]]
      //           },
      //           {
      //             name: '身高',
      //             data: hData,
      //             type: 'line',
      //             smooth: true,
      //             yAxisIndex: 0,
      //           },
      //           {
      //             name: '体重',
      //             data: wData,
      //             type: 'line',
      //             yAxisIndex: 1,
      //             smooth: true
      //           },
      //           {
      //             name: 'GH',
      //             type: 'line',
      //             symbol: 'none',
      //             itemStyle: {
      //               normal: {
      //                 color: '#ff3e1c',
      //                 lineStyle: {
      //                   width: 5,
      //                   color: '#ff3e1c',
      //                 },
      //               }
      //             },
      //             data: oneLine,
      //           },
      //           {
      //             name: 'GH+GnRHa',
      //             type: 'line',
      //             symbol: 'none',
      //             itemStyle: {
      //               normal: {
      //                 color: '#6570ff',
      //                 lineStyle: {
      //                   width: 5,
      //                   color: '#6570ff',
      //                 },
      //               }
      //             },
      //             data: twoLine,
      //           },
      //         ]
      //       }
      //     }
      //     myChart.setOption(option)
      //     myChart.resize()
      //     window.addEventListener("resize", function () {
      //       myChart.resize()
      //     })
      //   }
      //   this.$on('hook:destroyed', () => {
      //     window.removeEventListener("resize", function () {
      //       myChart.resize();
      //     });
      //   })
      // },

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
          // this.boneAgeImageUrl = '';
          this.imageUrl = ['', '', ''];
          // this.chromImageUrl = '';
          // this.genImageUrl = '';
          // this.patImageUrl = '';
          for (let key in this.ruleForm) {
            this.ruleForm[key] = ''
          }
          this.ruleForm.user_num = user_num;
          this.ruleForm.case_num = case_num;
          this.ruleForm.queryUId = queryUId;
          this.cleared = true;
          this.sampleBankMed =[{ input1750239223456: '',input1750239238024:'',number1750239261135:'',input1750239267879:'',date1750239297875:'',single1750239311504:'',date1750239394709:''}];
          this.biologBank =  [{id: '', name: ''}];
          this.chromosom=[];
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
          // let breast_dev={};
          // this.ruleForm.breastDev = breast_dev['breastDev'];
          // this.ruleForm.breastDevRight = breast_dev['breastDevRight'];
          // this.ruleForm.exGenitalia ='';
          // this.ruleForm.pubicHair ='';
          this.ruleForm.bodyOther = '';
          this.ruleForm.genitals = '';
          this.ruleForm.boneAge = '';
          // this.ruleForm.LH ='';
          // this.ruleForm.FSH = '';
          this.ruleForm.T = '';
          // this.ruleForm.E2 ='';
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
          // this.chromeImagePath(tempData['染色体报告']);
          // this.genImagePath(tempData['基因检测报告']);
          this.getFollow(this.filters);
          // this.allFollow();
          // this.$emit('dataList',this.ruleForm.firVisTime,this.ruleForm.morbidAge,this.ruleForm.chiefCom,this.ruleForm.mainDia,this.ruleForm.secDia,this.ruleForm.diaPlan);
        }).catch(() => {

        });
      },
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
    .input-underLineMed {
      width: 4.5vw;
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
<template>
  <div class="thyroid">
    <el-scrollbar class="scrollThy">
      <el-tabs :value="this.activeName">
        <el-tab-pane name="one">
          <span slot="label">家族史</span>
          <p class="thyroid-title">家族史：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              父亲身高：<input v-model="ruleForm.faHeight" class="input-underLine"  @blur="validateFatherHeight"/>CM,
              母亲身高：<input v-model="ruleForm.moHeight" class="input-underLine"/>CM
            </p>
            <p class="thyroid-title">
              父亲体重：<input v-model="ruleForm.faWeight" class="input-underLine" @blur="validateMotherHeight"/>KG,
              母亲体重：<input v-model="ruleForm.moWeight" class="input-underLine"/>KG
            </p>

            <div style="display: flex;width: 100%;margin-top: 1vw;margin-bottom: 1vw">
              <p class="thyroid-title" style="margin: 0px">糖尿病家族史：</p>
              <el-select size="small" v-model="ruleForm.isDiabetesFamily" :disabled="isStatic">
                <el-option label="有" value="有"></el-option>
                <el-option label="无" value="无"></el-option>
              </el-select>
              <span v-if="ruleForm.isDiabetesFamily === '有'" :style="{pointerEvents}">
              描述：<input v-model="ruleForm.DiabetesDescription" class="input-underLine" style="width: 70%"/>
            </span>
            </div>

            <div style="display: flex;width: 100%;margin-top: 1vw;margin-bottom: 1vw">
              <p class="thyroid-title" style="margin: 0px">甲状腺疾病家族史：</p>
              <el-select size="small" v-model="ruleForm.isThyroidFamily" :disabled="isStatic">
                <el-option label="有" value="有"></el-option>
                <el-option label="无" value="无"></el-option>
              </el-select>
              <span v-if="ruleForm.isThyroidFamily === '有'" :style="{pointerEvents}">
              描述：<input v-model="ruleForm.ThyroidDescription" class="input-underLine" style="width: 40%"/>
            </span>
            </div>

            <div style="display: flex;width: 100%;margin-top: 1vw;margin-bottom: 1vw">
              <p class="thyroid-title" style="margin: 0px">肿瘤家族史：</p>
              <el-select size="small" v-model="ruleForm.isTumorFamily" :disabled="isStatic">
                <el-option label="有" value="有"></el-option>
                <el-option label="无" value="无"></el-option>
              </el-select>
              <span v-if="ruleForm.isTumorFamily === '有'" :style="{pointerEvents}">
              描述：<input v-model="ruleForm.TumorDescription" class="input-underLine" style="width: 40%"/>
            </span>
            </div>

            <div style="display: flex;width: 100%;margin-top: 1vw;margin-bottom: 1vw">
              <p class="thyroid-title" style="margin: 0px">其他疾病家族史：</p>
              <el-select size="small" v-model="ruleForm.isOtherFamily" :disabled="isStatic">
                <el-option label="有" value="有"></el-option>
                <el-option label="无" value="无"></el-option>
              </el-select>
              <span v-if="ruleForm.isOtherFamily === '有'" :style="{pointerEvents}">
              描述：<input v-model="ruleForm.OtherDiseaseDescriptions" class="input-underLine" style="width: 40%"/>
            </span>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="two">
          <span slot="label">体格检查</span>
          <p class="thyroid-title">
            检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.checkTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
          </p>

          <p class="thyroid-title">一般情况：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              身高：<input v-model="ruleForm.height" class="input-underLine" @blur="validateHeightFun"/>cm，
              身高增长速度：<input v-model="ruleForm.heightRate" class="input-underLine"/>cm/year，
              体重：<input v-model="ruleForm.weight" class="input-underLine"/>kg
            </p>
            <p class="thyroid-title">
              收缩压：<input v-model="ruleForm.systolic" class="input-underLine"/>mmHg，
              舒张压：<input v-model="ruleForm.diastolic" class="input-underLine"/>mmHg
            </p>
            <p class="thyroid-title">心率：<input v-model="ruleForm.heartRate" class="input-underLine"/>次/分</p>
          </div>

          <div v-if="this.sex === '2'">
            <p class="thyroid-title">左侧乳腺发育分期（女孩）：
              <el-select size="small" v-model="ruleForm.leftBreastDev" :disabled="isStatic">
                <el-option label="B1期" value="1"></el-option>
                <el-option label="B2期" value="2"></el-option>
                <el-option label="B3期" value="3"></el-option>
                <el-option label="B4期" value="4"></el-option>
                <el-option label="B5期" value="5"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title">右侧乳腺发育分期（女孩）：
              <el-select size="small" v-model="ruleForm.rightBreastDev" :disabled="isStatic">
                <el-option label="B1期" value="1"></el-option>
                <el-option label="B2期" value="2"></el-option>
                <el-option label="B3期" value="3"></el-option>
                <el-option label="B4期" value="4"></el-option>
                <el-option label="B5期" value="5"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title">外阴阴毛分期：
              <el-select size="small" v-model="ruleForm.pubicHair" :disabled="isStatic">
                <el-option label="P1期" value="1"></el-option>
                <el-option label="P2期" value="2"></el-option>
                <el-option label="P3期" value="3"></el-option>
                <el-option label="P4期" value="4"></el-option>
                <el-option label="P5期" value="5"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title">女孩性征发育情况：</p>
            <div class="div-box" :style="{pointerEvents}">
              <p class="thyroid-title">
                乳房触痛：
                <el-select size="small" v-model="ruleForm.breastTend" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                阴蒂肥大：
                <el-select size="small" v-model="ruleForm.clitoralHypertrophy" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                阴唇着色：
                <el-select size="small" v-model="ruleForm.labialColoration" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
              </p>
            </div>
          </div>

          <div v-if="this.sex === '1'">
            <p class="thyroid-title">左侧睾丸发育分期：
              <el-select size="small" v-model="ruleForm.leftTesticleDev" :disabled="isStatic">
                <el-option label="G1期" value="1"></el-option>
                <el-option label="G2期" value="2"></el-option>
                <el-option label="G3期" value="3"></el-option>
                <el-option label="G4期" value="4"></el-option>
                <el-option label="G5期" value="5"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title">右侧睾丸发育分期：
              <el-select size="small" v-model="ruleForm.rightTesticleDev" :disabled="isStatic">
                <el-option label="G1期" value="1"></el-option>
                <el-option label="G2期" value="2"></el-option>
                <el-option label="G3期" value="3"></el-option>
                <el-option label="G4期" value="4"></el-option>
                <el-option label="G5期" value="5"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title">男孩性征发育情况：</p>
            <div class="div-box" :style="{pointerEvents}">
              <p class="thyroid-title">
                喉结凸起：
                <el-select size="small" v-model="ruleForm.appleProtrusion" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                乳房增大：
                <el-select size="small" v-model="ruleForm.breastEnlarg" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                阴茎增长：
                <el-select size="small" v-model="ruleForm.penileGrowth" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
              </p>
            </div>
          </div>
          <p class="thyroid-title">甲状腺肿大：
            <el-select size="small" v-model="ruleForm.goiter" :disabled="isStatic">
              <el-option label="无肿大" value="1"></el-option>
              <el-option label="肿大Ⅰ度" value="2"></el-option>
              <el-option label="肿大Ⅱ度" value="3"></el-option>
              <el-option label="肿大Ⅲ度" value="4"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">皮肤检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <el-checkbox-group v-model="skinExamination" @change="getSkinExam">
              <el-checkbox label="面部痤疮">面部痤疮</el-checkbox>
              <el-checkbox label="满月脸">满月脸</el-checkbox>
              <el-checkbox label="毛发增多">毛发增多</el-checkbox>
              <el-checkbox label="牛奶咖啡斑">牛奶咖啡斑<input v-if="isCafeMilk" v-model="ruleForm.cafeMilkPoint"
                                                     class="input-underLine"/></el-checkbox>
              <el-checkbox label="皮肤色素沉着">皮肤色素沉着</el-checkbox>
              <el-checkbox label="未做">未做</el-checkbox>
              <el-checkbox label="无异常">无异常</el-checkbox>
              <el-checkbox label="其他">其他</el-checkbox>
            </el-checkbox-group>
          </div>

          <p class="thyroid-title">骨骼检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <el-checkbox-group v-model="boneExamination" @change="getBoneExam">
              <el-checkbox label="骨膨胀或凸起">骨膨胀或凸起<input v-if="isBoneSwelling" v-model="ruleForm.boneSwelling"
                                                       class="input-underLine"/></el-checkbox>
              <el-checkbox label="头面部骨骼不对称">头面部骨骼不对称</el-checkbox>
              <el-checkbox label="胸骨压痛">胸骨压痛</el-checkbox>
              <el-checkbox label="鸡胸">鸡胸</el-checkbox>
              <el-checkbox label="肋串珠">肋串珠</el-checkbox>
              <el-checkbox label="脊柱侧弯">脊柱侧弯</el-checkbox>
              <el-checkbox label="脊柱压痛">脊柱压痛</el-checkbox>
              <el-checkbox label="骨盆压痛">骨盆压痛</el-checkbox>
              <el-checkbox label="双上肢长度不相等">双上肢长度不相等</el-checkbox>
              <el-checkbox label="双下肢长度不相等">双下肢长度不相等</el-checkbox>
              <el-checkbox label="关节畸形">关节畸形<input v-if="isJointDeformity" v-model="ruleForm.jointDeformity"
                                                   class="input-underLine"/></el-checkbox>
              <el-checkbox label="关节疼痛">关节疼痛<input v-if="isJointPain" v-model="ruleForm.jointPain"
                                                   class="input-underLine"/></el-checkbox>
              <el-checkbox label="骨痛">骨痛<input v-if="isBonePain" v-model="ruleForm.bonePain" class="input-underLine"/>
              </el-checkbox>
              <el-checkbox label="未做">未做</el-checkbox>
              <el-checkbox label="无异常">无异常</el-checkbox>
              <el-checkbox label="其他">其他</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-tab-pane>

        <el-tab-pane name="three">
          <span slot="label">影像学检查</span>
          <p class="thyroid-title">
            B超检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.bCheckTime"
                type="date"
                :clearable="false"
                :disabled="isStatic"
                :picker-options="pickerOptions"
                placeholder="B超检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
          </p>

          <div v-if="this.sex === '2'">
            <p class="thyroid-title">子宫超声情况：
              <el-select size="small" v-model="ruleForm.uterusUlt" :disabled="isStatic">
                <el-option label="有" value="1"></el-option>
                <el-option label="无" value="2"></el-option>
                <el-option label="未做" value="3"></el-option>
              </el-select>
            </p>

            <p class="thyroid-title" :style="{pointerEvents}">子宫情况具体描述：
              长度(mm)<input v-model="ruleForm.uterusLength" class="input-underLine"/>*
              宽度(mm)<input v-model="ruleForm.uterusWidth" class="input-underLine"/>*
              厚度(mm)<input v-model="ruleForm.uterineThickness" class="input-underLine"/>
            </p>

            <p class="thyroid-title">卵巢超声情况：</p>
            <div class="div-box" :style="{pointerEvents}">
              <p class="thyroid-title">
                左侧卵巢：
                <el-select size="small" v-model="ruleForm.leftOvary" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                <span v-if="ruleForm.leftOvary === '1'">
                长<input v-model="ruleForm.leftOvaryLength" class="input-underLine"/>*
                宽<input v-model="ruleForm.leftOvaryWidth" class="input-underLine"/>*
                厚<input v-model="ruleForm.leftOvaryThickness" class="input-underLine"/>(mm)
              </span>
              </p>
              <p class="thyroid-title">
                左侧卵巢囊肿：
                <el-select size="small" v-model="ruleForm.leftOvaryCyst" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                <span v-if="ruleForm.leftOvaryCyst === '1'" class="thyroid-title">
                长<input v-model="ruleForm.leftOvaryCystLength" class="input-underLine"/>*
                宽<input v-model="ruleForm.leftOvaryCystWidth" class="input-underLine"/>*
                厚<input v-model="ruleForm.leftOvaryCystThickness" class="input-underLine"/>(mm)
              </span>
              </p>
              <p class="thyroid-title">
                右侧卵巢：
                <el-select size="small" v-model="ruleForm.rightOvary" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                <span v-if="ruleForm.rightOvary === '1'" class="thyroid-title">
                长<input v-model="ruleForm.rightOvaryLength" class="input-underLine"/>*
                宽<input v-model="ruleForm.rightOvaryWidth" class="input-underLine"/>*
                厚<input v-model="ruleForm.rightOvaryThickness" class="input-underLine"/>(mm)
              </span>
              </p>
              <p class="thyroid-title">
                右侧卵巢囊肿：
                <el-select size="small" v-model="ruleForm.rightOvaryCyst" :disabled="isStatic">
                  <el-option label="有" value="1"></el-option>
                  <el-option label="无" value="2"></el-option>
                </el-select>
                <span v-if="ruleForm.rightOvaryCyst === '1'" class="thyroid-title">
                长<input v-model="ruleForm.rightOvaryCystLength" class="input-underLine"/>*
                宽<input v-model="ruleForm.rightOvaryCystWidth" class="input-underLine"/>*
                厚<input v-model="ruleForm.rightOvaryCystThickness" class="input-underLine"/>(mm)
              </span>
              </p>
            </div>
          </div>

          <p class="thyroid-title">甲状腺B超情况：
            <el-select size="small" v-model="ruleForm.thyroidUlt" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.thyroidUlt === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.thyroidUltAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">肾上腺B超情况：
            <el-select size="small" v-model="ruleForm.adrenalUlt" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.adrenalUlt === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.adrenalUltAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">肾脏B超情况：
            <el-select size="small" v-model="ruleForm.renalUlt" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.renalUlt === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.renalUltAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">病变骨骼X线片检查情况：
            <el-select size="small" v-model="ruleForm.boneX" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.boneX === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.boneXAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">MR：
            <el-select size="small" v-model="ruleForm.placeMR" :disabled="isStatic">
              <el-option label="头颅" value="1"></el-option>
              <el-option label="胸部" value="2"></el-option>
              <el-option label="腹部" value="3"></el-option>
              <el-option label="双上肢" value="4"></el-option>
              <el-option label="双下肢" value="5"></el-option>
            </el-select>
            <el-select size="small" v-model="ruleForm.typeMR" :disabled="isStatic">
              <el-option label="正常" value="6"></el-option>
              <el-option label="异常" value="7"></el-option>
              <el-option label="未做" value="8"></el-option>
            </el-select>
            <span v-if="ruleForm.typeMR === '7'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.MRdescription" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">CT:
            <el-select size="small" v-model="ruleForm.placeCT" :disabled="isStatic">
              <el-option label="头颅" value="1"></el-option>
              <el-option label="胸部" value="2"></el-option>
              <el-option label="腹部" value="3"></el-option>
              <el-option label="双上肢" value="4"></el-option>
              <el-option label="双下肢" value="5"></el-option>
            </el-select>
            <el-select size="small" v-model="ruleForm.typeCT" :disabled="isStatic">
              <el-option label="正常" value="6"></el-option>
              <el-option label="异常" value="7"></el-option>
              <el-option label="未做" value="8"></el-option>
            </el-select>
            <span v-if="ruleForm.typeCT === '7'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.CTdescription" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">全身骨扫描检查情况：
            <el-select size="small" v-model="ruleForm.bodyBoneScan" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.bodyBoneScan === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.bodyBoneScanAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>
        </el-tab-pane>

        <el-tab-pane name="four">
          <span slot="label">化验检查</span>
          <p class="thyroid-title">
            常规化验检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.commonCheckTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="常规化验检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
          </p>

          <p class="thyroid-title">原肝肾脂电解质：
            <el-select size="small" v-model="ruleForm.ProtoliverEle" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
            </el-select>
            <span v-if="ruleForm.ProtoliverEle === '2'">
              异常情况描述：<input v-model="ruleForm.ProtoliverEleAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>
          <p class="thyroid-title">血常规：</p>
          <div class="div-box" :style="{pointerEvents}">
            血常规检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.CBCTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="常规化验检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
            <br><br>
            <p class="thyroid-title">
              白细胞：<input v-model="ruleForm.leukocyte" class="input-underLine"/>10^9/L，
              血红蛋白：<input v-model="ruleForm.hemoglobin" class="input-underLine"/>g/L，
              血小板：<input v-model="ruleForm.platelet" class="input-underLine"/>10^9/L,
            </p>
              <p class="thyroid-title">
              中性粒细胞比例：<input v-model="ruleForm.Neutrophils" class="input-underLine"/>%,
              红细胞计数：<input v-model="ruleForm.erythrocyteNum" class="input-underLine"/>10^12/L
            </p>
           
          </div>

          <p class="thyroid-title">肝功能：</p>
          <div class="div-box" :style="{pointerEvents}">
            肝功能检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.hepaticTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="肝功能检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
            <br><br>
            <p class="thyroid-title">
              ALT：<input v-model="ruleForm.ALT" class="input-underLine"/>U/L，
              AST：<input v-model="ruleForm.AST" class="input-underLine"/>U/L，
              LDH：<input v-model="ruleForm.LDH" class="input-underLine"/>U/L,
              γ-GT：<input v-model="ruleForm.gamaGT" class="input-underLine"/>，
              <!-- 碱性磷酸酶：<input v-model="ruleForm.AKP" class="input-underLine"/>U/L -->
              碱性磷酸酶：<input v-model="ruleForm.ALP" class="input-underLine"/>U/L
            </p>
            <p class="thyroid-title">
              总胆汁酸：<input v-model="ruleForm.TBA" class="input-underLine"/>umol/L，
              总胆红素：<input v-model="ruleForm.totalBilirubin" class="input-underLine"/>umol/L，
              直接胆红素：<input v-model="ruleForm.directBilirubin" class="input-underLine"/>umol/L，
              间接胆红素：<input v-model="ruleForm.indirectBilirubin" class="input-underLine"/>umol/L
            </p>
          </div>

          <p class="thyroid-title">肾功能：</p>
          <div class="div-box" :style="{pointerEvents}">
            肾功能检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.renalTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="肾功能检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
            <br><br>
            <p class="thyroid-title">
              尿素：<input v-model="ruleForm.urea" class="input-underLine"/>mmol/L，
              肌酐：<input v-model="ruleForm.creatinine" class="input-underLine"/>umol/L，
              尿酸：<input v-model="ruleForm.uricAcid" class="input-underLine"/>umol/L
            </p>
          </div>

          <p class="thyroid-title">电解质：</p>
          <div class="div-box" :style="{pointerEvents}">
            电解质检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.electrolyteTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="电解质检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
            <br><br>
            <p class="thyroid-title">
              血钾：<input v-model="ruleForm.bloodK" class="input-underLine"/>mmol/L，
              血钠：<input v-model="ruleForm.bloodNa" class="input-underLine"/>mmol/L，
              血氯：<input v-model="ruleForm.bloodCl" class="input-underLine"/>mmol/L，
              总钙：<input v-model="ruleForm.Tca" class="input-underLine"/>mmol/L，
              无机磷：<input v-model="ruleForm.Pi" class="input-underLine"/>mmol/L
            </p>
          </div>

          <p class="thyroid-title">血脂：</p>
          <div class="div-box" :style="{pointerEvents}">
            血脂检查日期：
            <el-date-picker
                size="small"
                v-model="ruleForm.lipidTime"
                type="date"
                :clearable="false"
                :picker-options="pickerOptions"
                :disabled="isStatic"
                placeholder="血脂检查日期"
                value-format="yyyy-MM-dd"
            ></el-date-picker>
            <br><br>
            <p class="thyroid-title">
              TC：<input v-model="ruleForm.TC" class="input-underLine"/>mmol/L，
              TG：<input v-model="ruleForm.TG" class="input-underLine"/>mmol/L，
              HDL：<input v-model="ruleForm.HDL" class="input-underLine"/>mmol/L，
              LDL：<input v-model="ruleForm.LDL" class="input-underLine"/>mmol/L，
              甘油三酯：<input v-model="ruleForm.Trilaurin" class="input-underLine"/>mmol/L
            </p>
          </div>

          <p class="thyroid-title">骨代谢检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              骨代谢检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.boneCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="骨代谢检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              血钙：<input v-model="ruleForm.bloodCa" class="input-underLine"/>mmol/L，
              血磷：<input v-model="ruleForm.bloodP" class="input-underLine"/>mmol/L，
              β-CTX：<input v-model="ruleForm.CTX" class="input-underLine"/>pg/mL
            </p>
            <p class="thyroid-title">
              骨钙素：<input v-model="ruleForm.BGP" class="input-underLine"/>ng/mL，
              PINP：<input v-model="ruleForm.PINP" class="input-underLine"/>ug/L，
              PTH：<input v-model="ruleForm.PTH" class="input-underLine"/>pg/mL
            </p>
            <p class="thyroid-title">
              25羟维生素D：<input v-model="ruleForm.OHD25" class="input-underLine"/>nmol/L，
              <!-- 碱性磷酸酶：<input v-model="ruleForm.ALP" class="input-underLine"/>U/L， -->
              24h尿钙：<input v-model="ruleForm.urineCa" class="input-underLine"/>，
              24h尿磷：<input v-model="ruleForm.urineP" class="input-underLine"/>
            </p>
          </div>

          <p class="thyroid-title">性激素检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              性激素检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.sexHormoneCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="性激素检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              LH：<input v-model="ruleForm.LH" class="input-underLine"/>mIU/mL，
              FSH：<input v-model="ruleForm.FSH" class="input-underLine"/>mIU/mL，
              E2：<input v-model="ruleForm.E2" class="input-underLine"/>pg/mL
            </p>
            <p class="thyroid-title">
              T：<input v-model="ruleForm.T" class="input-underLine"/>ng/dL，
              PRL：<input v-model="ruleForm.PRL" class="input-underLine"/>ng/mL，
            </p>
          </div>

          <p class="thyroid-title">甲状腺功能及抗体检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              甲状腺功能及抗体检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.thyroidFunctionCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="甲状腺功能及抗体检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              TT4：<input v-model="ruleForm.TT4" class="input-underLine"/>nmol/L，
              TT3：<input v-model="ruleForm.TT3" class="input-underLine"/>nmol/L，
              TSH：<input v-model="ruleForm.TSH" class="input-underLine"/>mIU/L
            </p>
            <p class="thyroid-title">
              FT4：<input v-model="ruleForm.FT4" class="input-underLine"/>pmol/L，
              FT3：<input v-model="ruleForm.FT3" class="input-underLine"/>pmol/L，
              TPOAb：<input v-model="ruleForm.TPOAb" class="input-underLine"/>IU/mL，
              TGAb：<input v-model="ruleForm.TGAb" class="input-underLine"/>IU/mL
            </p>
            <p class="thyroid-title">


            </p>
          </div>

          <p class="thyroid-title">肾上腺功能检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              肾上腺功能检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.adrenalFunctionCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="肾上腺功能检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              ACTH：<input v-model="ruleForm.ACTH" class="input-underLine"/>pg/mL，
              ACTH 8am:<input v-model="ruleForm.ACTH8" class="input-underLine"/>,
              ACTH 4pm:<input v-model="ruleForm.ACTH4" class="input-underLine"/>,
            </p>
            <p class="thyroid-title">
              皮质醇8am：<input v-model="ruleForm.AM8" class="input-underLine"/>ug/dL，
              皮质醇4pm：<input v-model="ruleForm.PM4" class="input-underLine"/>ug/dL，
              24h尿游离皮质醇：<input v-model="ruleForm.UFC" class="input-underLine"/>ug/24h
            </p>
          </div>

          <p class="thyroid-title">生长激素分泌功能检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              生长激素分泌功能检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.somatotropinFunctionCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="生长激素分泌功能检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              GH：<input v-model="ruleForm.GH" class="input-underLine"/>ng/mL，
              IGF-1：<input v-model="ruleForm.IGF1" class="input-underLine"/>ng/mL，
              IGF-BP3：<input v-model="ruleForm.IGFBP3" class="input-underLine"/>ug/mL
            </p>
          </div>

          <p class="thyroid-title">糖代谢情况：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">
              糖代谢检查日期：
              <el-date-picker
                  size="small"
                  v-model="ruleForm.glycometabolismFunctionCheckTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="糖代谢检查日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
            </p>
            <p class="thyroid-title">
              空腹血糖：<input v-model="ruleForm.FBS" class="input-underLine"/>mmol/L，
              空腹胰岛素：<input v-model="ruleForm.FINS" class="input-underLine"/>mIU/L，
              空腹C肽：<input v-model="ruleForm.FCP" class="input-underLine"/>ng/mL，
              糖化血红蛋白：<input v-model="ruleForm.HbA1c" class="input-underLine"/>%
            </p>
          </div>

        </el-tab-pane>

        <el-tab-pane name="seven">
          <span slot="label">激发试验情况</span>

          <p class="thyroid-title">GnRH激发试验：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">是否GnRH激发试验：
              <el-select size="small" v-model="ruleForm.GnRH" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title" v-if="ruleForm.GnRH === '1'">
              药物名称： <input v-model="ruleForm.GnRHDrugName" class="input-underLine" style="width: 15%"/>，
              药物剂量： <input v-model="ruleForm.GnRHDrugDosage" class="input-underLine" style="width: 15%"/>，
              使用时间： <input v-model="ruleForm.GnRHUsageTime" class="input-underLine" style="width: 15%"/>
            </p>

            <p class="thyroid-title" v-if="ruleForm.GnRH === '1'">
              LF峰值： <input v-model="ruleForm.LFMax" class="input-underLine" style="width: 15%"/>，
              FSH峰值： <input v-model="ruleForm.FSHMax" class="input-underLine" style="width: 15%"/>，
              LH/FSH比值： <input v-model="ruleForm.LFRatio" class="input-underLine" style="width: 15%"/>
            </p>
          </div>

          <p class="thyroid-title">小剂量地塞米松抑制试验：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">是否行小剂量地塞米松抑制试验：
              <el-select size="small" v-model="ruleForm.LDDST" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title" v-if="ruleForm.LDDST === '1'">
              药物名称： <input v-model="ruleForm.LDDSTDrugName" class="input-underLine" style="width: 15%"/>，
              药物剂量： <input v-model="ruleForm.LDDSTDrugDosage" class="input-underLine" style="width: 15%"/>，
              使用时间： <input v-model="ruleForm.LDDSTUsageTime" class="input-underLine" style="width: 15%"/>
            </p>

            <p class="thyroid-title" v-if="ruleForm.LDDST === '1'">
              ACTH（试验前）：<input v-model="ruleForm.ACTHAfter" class="input-underLine" style="width: 15%"/>，
              ACTH（试验后）：<input v-model="ruleForm.ACTHBefore" class="input-underLine" style="width: 15%"/>
            </p>
            <p class="thyroid-title" v-if="ruleForm.LDDST === '1'">
              皮质醇（试验前）：<input v-model="ruleForm.cortisolAfter" class="input-underLine" style="width: 15%"/>，
              皮质醇（试验后）：<input v-model="ruleForm.cortisolBefore" class="input-underLine" style="width: 15%"/>
            </p>
            <p class="thyroid-title" v-if="ruleForm.LDDST === '1'">
              24h尿游离皮质醇（试验前）：<input v-model="ruleForm.UFFAfter" class="input-underLine" style="width: 15%"/>，
              24h尿游离皮质醇（试验后）：<input v-model="ruleForm.UFFBefore" class="input-underLine" style="width: 15%"/>
            </p>
          </div>

          <p class="thyroid-title">生长激素-葡萄糖抑制试验：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p class="thyroid-title">是否行生长激素-葡萄糖抑制试验：
              <el-select size="small" v-model="ruleForm.GHGIT" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
            </p>
            <p class="thyroid-title" v-if="ruleForm.GHGIT === '1'">
              药物名称： <input v-model="ruleForm.GHGITDrugName" class="input-underLine" style="width: 15%"/>，
              药物剂量： <input v-model="ruleForm.GHGITDrugDosage" class="input-underLine" style="width: 15%"/>，
              使用时间： <input v-model="ruleForm.GHGITUsageTime" class="input-underLine" style="width: 15%"/>
            </p>

            <p class="thyroid-title" v-if="ruleForm.GHGIT === '1'">
              GH（0min）：<input v-model="ruleForm.GH0" class="input-underLine"/>，
              GH（30min）：<input v-model="ruleForm.GH3" class="input-underLine"/>，
              GH（60min）：<input v-model="ruleForm.GH6" class="input-underLine"/>，
              GH（90min）：<input v-model="ruleForm.GH9" class="input-underLine"/>，
              GH（120min）：<input v-model="ruleForm.GH12" class="input-underLine"/>
            </p>

            <p class="thyroid-title" v-if="ruleForm.GHGIT === '1'">
              血糖值（0min）：<input v-model="ruleForm.XTZ0" class="input-underLine"/> mmol/l，
              血糖值（30min）：<input v-model="ruleForm.XTZ3" class="input-underLine"/> mmol/l，
              血糖值（60min）：<input v-model="ruleForm.XTZ6" class="input-underLine"/> mmol/l，
              血糖值（90min）：<input v-model="ruleForm.XTZ9" class="input-underLine"/> mmol/l，
              血糖值（120min）：<input v-model="ruleForm.XTZ12" class="input-underLine"/> mmol/l
            </p>
          </div>
        </el-tab-pane>

        <el-tab-pane name="five">
          <span slot="label">其他检查</span>

          <p class="thyroid-title">心电图检查：
            <el-select size="small" v-model="ruleForm.ecgExamination" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.ecgExamination === '2'" :style="{pointerEvents}">
              异常情况描述：<input v-model="ruleForm.ecgExaminationAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">X线骨龄检查：
            <el-select size="small" v-model="ruleForm.XboneAge" :disabled="isStatic">
              <el-option label="做" value="1"></el-option>
              <el-option label="未做" value="2"></el-option>
            </el-select>
            <span  v-if="ruleForm.XboneAge === '1'">
              骨龄<input v-model="ruleForm.XbAge" class="input-underLine" style="width: 10%"/>岁
            </span>
          </p>
          <p class="thyroid-title" v-if="ruleForm.XboneAge === '1'">骨龄测定图片上传：</p>
          <ImageUpload
                v-if="ruleForm.XboneAge === '1'"
                :caseId="queryId"
                organ="mas"
                category="骨龄测定图片"
                :fileName="boneAgeUrl"
                @update:fileName="v =>upBoneImage(v)"
                :editable="!isStatic"
            >
            </ImageUpload>

          <p class="thyroid-title">垂体MR检查：
            <el-select size="small" v-model="ruleForm.pituitaryMR" :disabled="isStatic">
              <el-option label="正常" value="1"></el-option>
              <el-option label="异常" value="2"></el-option>
              <el-option label="未做" value="3"></el-option>
            </el-select>
            <span v-if="ruleForm.pituitaryMR === '2'">
              异常情况描述：<input v-model="ruleForm.pituitaryMRAbnormal" class="input-underLine" style="width: 40%"/>
            </span>
          </p>

          <p class="thyroid-title">检查结果上传：</p>
          <ImageUpload
              :caseId="queryId"
              organ="mas"
              category="检查结果"
              :fileName="checkResultUrl"
              @update:fileName="v =>upCheckResul(v)"
              :editable="!isStatic"
          >
          </ImageUpload>
        </el-tab-pane>

        <el-tab-pane name="six">
          <span slot="label">遗传学检查及病理检查</span>

          <p class="thyroid-title">GNAS基因测定检查：
            <el-select size="small" v-model="ruleForm.GNAS" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
              <el-option label="不详" value="3"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title" v-if="ruleForm.GNAS !== '2'">标本采样类型或部位：
            <el-select size="small" v-model="ruleForm.GNASSampling" :disabled="isStatic">
              <el-option label="外周血" value="1"></el-option>
              <el-option label="病变组织" value="2"></el-option>
              <el-option label="囊肿穿刺液" value="3"></el-option>
              <el-option label="其他" value="4"></el-option>
            </el-select>
            <span v-if="ruleForm.GNASSampling && ruleForm.GNASSampling !== '1'" :style="{pointerEvents}">
              具体部位： <input v-model="ruleForm.gnasSamplingPosition" class="input-underLine" style="width: 15%"/>
            </span>
          </p>

          <p class="thyroid-title">病理活检检查：
            <el-select size="small" v-model="ruleForm.pathBiopsyExamination" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
              <el-option label="不详" value="3"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title" v-if="ruleForm.pathBiopsyExamination !== '2'">
            标本采样类型或部位：<input v-model="ruleForm.pathBiopsyPosition" class="input-underLine" :style="{pointerEvents}" style="width: 40%"/>
          </p>

        </el-tab-pane>

        

        <el-tab-pane name="eight">
          <span slot="label">随访及治疗</span>

          <p class="thyroid-title">是否达终身高：
            <el-select size="small" v-model="ruleForm.isFinalHeight" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
            <span v-if="ruleForm.isFinalHeight === '1'" :style="{pointerEvents}">
              具体身高：<input v-model="ruleForm.finalHeight" @blur="validateFinalHeight" class="input-underLine"/>（cm）
            </span>
          </p>

          <p class="thyroid-title">是否外周性性早熟：
            <el-select size="small" v-model="ruleForm.isPrecociousPuberty" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isPrecociousPuberty === '1'">
            <p class="thyroid-title">有无对外周性性早熟进行治疗：
              <el-select size="small" v-model="ruleForm.isPPP" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isPPP === '1'">
              治疗周期：
              <el-date-picker
                  class="timeCheck"
                  v-model="ruleForm.treatmentCyclePPP"
                  type="daterange"
                  :clearable="false"
                  unlink-panels
                  size="small"
                  range-separator="~"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
              </span>
            </p>

            <el-table
                border
                :data="precocityData"
                v-if="ruleForm.isPPP === '1'"
                ref="table"
            >
              <el-table-column label="随访日期" width="150">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 95%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="身高">
                <template slot-scope="scope">
                  <input v-model="scope.row.height" @blur="validateHeights(scope.row.height)" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="体重">
                <template slot-scope="scope">
                  <input v-model="scope.row.weight" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="BMI">
                <template slot-scope="scope">
                  <input v-model="scope.row.bmi" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="乳腺分期">
                <template slot-scope="scope">
                  <input v-model="scope.row.breast" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="睾丸分期">
                <template slot-scope="scope">
                  <input v-model="scope.row.testis" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="阴毛分期">
                <template slot-scope="scope">
                  <input v-model="scope.row.hair" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="LH">
                <template slot-scope="scope">
                  <input v-model="scope.row.LH" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="FSH">
                <template slot-scope="scope">
                  <input v-model="scope.row.FSH" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="E2">
                <template slot-scope="scope">
                  <input v-model="scope.row.E2" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="T">
                <template slot-scope="scope">
                  <input v-model="scope.row.T" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="子宫附件B超或睾丸B超">
                <template slot-scope="scope">
                  <input v-model="scope.row.ultra" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="骨龄情况">
                <template slot-scope="scope">
                  <input v-model="scope.row.boneage" class="input-underLine"/>
                </template>
              </el-table-column>

              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,precocityData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addRow(precocityData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否甲状腺功能亢进：
            <el-select size="small" v-model="ruleForm.isThyroidFunction" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isThyroidFunction === '1'">
            <p class="thyroid-title">有无对甲状腺功能亢进进行治疗：
              <el-select size="small" v-model="ruleForm.isHyperthyreosis" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isHyperthyreosis === '1'">
                治疗起始日期：
                <el-date-picker
                    class="timeCheck"
                    v-model="ruleForm.treatmentCycleHyper"
                    type="daterange"
                    :clearable="false"
                    unlink-panels
                    size="small"
                    range-separator="~"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                ></el-date-picker>
              </span>
            </p>

            <el-table
                border
                v-if="ruleForm.isHyperthyreosis === '1'"
                :data="hyperData"
                ref="table"
            >
              <el-table-column label="随访日期">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 80%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="治疗方法">
                <template slot-scope="scope">
                  <input v-model="scope.row.method" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="甲状腺功能">
                <template slot-scope="scope">
                  <input v-model="scope.row.TF" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="甲状腺B超">
                <template slot-scope="scope">
                  <input v-model="scope.row.thyroidUlt" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,hyperData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addHyperRow(hyperData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否生长激素分泌过多：
            <el-select size="small" v-model="ruleForm.isGrowthHormonePlethora" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isGrowthHormonePlethora === '1'">
            <p class="thyroid-title">有无对生长激素分泌过多进行治疗：
              <el-select size="small" v-model="ruleForm.isGrowth" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isGrowth === '1'">
                治疗起始日期：
                <el-date-picker
                    class="timeCheck"
                    v-model="ruleForm.treatmentCycleGrowth"
                    type="daterange"
                    :clearable="false"
                    unlink-panels
                    size="small"
                    range-separator="~"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                ></el-date-picker>
              </span>
            </p>
            <el-table
                border
                v-if="ruleForm.isGrowth === '1'"
                :data="growthData"
                ref="table"
            >
              <el-table-column label="随访日期" width="150">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 95%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="IGF-1">
                <template slot-scope="scope">
                  <input v-model="scope.row.IGF1" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="IGF-BP3">
                <template slot-scope="scope">
                  <input v-model="scope.row.IGFBP3" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="GH">
                <template slot-scope="scope">
                  <input v-model="scope.row.GH" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="垂体MR">
                <template slot-scope="scope">
                  <input v-model="scope.row.MR" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,growthData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addGrowRow(growthData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否高泌乳素血症：
            <el-select size="small" v-model="ruleForm.isHyperprolactinemia" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isHyperprolactinemia === '1'">
            <p class="thyroid-title">有无对高泌乳素血症进行治疗：
              <el-select size="small" v-model="ruleForm.isHPRL" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isHPRL === '1'">
                治疗起始日期：
                <el-date-picker
                    class="timeCheck"
                    v-model="ruleForm.treatmentCycleHPRL"
                    type="daterange"
                    :clearable="false"
                    unlink-panels
                    size="small"
                    range-separator="~"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                ></el-date-picker>
              </span>
            </p>
            <el-table
                border
                v-if="ruleForm.isHPRL === '1'"
                :data="HPRLData"
                ref="table"
            >
              <el-table-column label="随访日期">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 80%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="PRL">
                <template slot-scope="scope">
                  <input v-model="scope.row.PRL" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,HPRLData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addHPRLRow(HPRLData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否皮质醇增多症：
            <el-select size="small" v-model="ruleForm.isHypercortisolism" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isHypercortisolism === '1'">
            <p class="thyroid-title">有无对皮质醇增多症进行治疗：
              <el-select size="small" v-model="ruleForm.isCortisol" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isCortisol === '1'">
                治疗起始日期：
                <el-date-picker
                    class="timeCheck"
                    v-model="ruleForm.treatmentCycleCortisol"
                    type="daterange"
                    :clearable="false"
                    unlink-panels
                    size="small"
                    range-separator="~"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                ></el-date-picker>
              </span>
            </p>
            <el-table
                border
                v-if="ruleForm.isCortisol === '1'"
                :data="cortisolData"
                ref="table"
            >
              <el-table-column label="随访日期">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 95%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="ACTH">
                <template slot-scope="scope">
                  <input v-model="scope.row.ACTH" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="皮质醇8am">
                <template slot-scope="scope">
                  <input v-model="scope.row.cortisol8" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="皮质醇4pm">
                <template slot-scope="scope">
                  <input v-model="scope.row.cortisol4" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="24h尿游离皮质醇">
                <template slot-scope="scope">
                  <input v-model="scope.row.FC" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,cortisolData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addCortisoRow(cortisolData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">
            是否行颅内手术：
            <el-select size="small" v-model="ruleForm.isIntracranialSurgery" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            是否行双侧肾上腺切除术：
            <el-select size="small" v-model="ruleForm.isBilateralAdrenalectomy" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">是否骨痛：
            <el-select size="small" v-model="ruleForm.isOstealgia" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isOstealgia === '1'">
            <p class="thyroid-title">是否对骨痛进行治疗：
              <el-select size="small" v-model="ruleForm.isTreatBonePain" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
            </p>
            <el-table
                border
                v-if="ruleForm.isTreatBonePain === '1'"
                :data="bonePainData"
                ref="table"
            >
              <el-table-column label="随访日期">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 80%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="骨转化指标">
                <template slot-scope="scope">
                  <input v-model="scope.row.boneTurnover" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="骨密度">
                <template slot-scope="scope">
                  <input v-model="scope.row.BMD" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,bonePainData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addBoneRow(bonePainData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否低磷酸盐血症：
            <el-select size="small" v-model="ruleForm.isHaveHypophosphatemia" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}"  v-if="ruleForm.isHaveHypophosphatemia === '1'">
            <p class="thyroid-title">是否对低磷酸盐血症进行治疗（补充钙磷、骨化三醇治疗）：
              <el-select size="small" v-model="ruleForm.isHypophosphatemia" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
            </p>
            <el-table
                border
                v-if="ruleForm.isHypophosphatemia === '1'"
                :data="hypophosphatemiaData"
                ref="table"
            >
              <el-table-column label="随访日期" width="150">
                <template slot-scope="scope">
                  <el-date-picker
                      size="mini"
                      style="width: 95%"
                      v-model="scope.row.time"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="随访日期"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </template>
              </el-table-column>
              <el-table-column label="药物名称">
                <template slot-scope="scope">
                  <input v-model="scope.row.name" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="药物剂量">
                <template slot-scope="scope">
                  <input v-model="scope.row.dose" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="血钙">
                <template slot-scope="scope">
                  <input v-model="scope.row.bloodCa" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="尿钙">
                <template slot-scope="scope">
                  <input v-model="scope.row.urineCa" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="尿磷">
                <template slot-scope="scope">
                  <input v-model="scope.row.urineP" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="PTH">
                <template slot-scope="scope">
                  <input v-model="scope.row.PTH" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="肾功能">
                <template slot-scope="scope">
                  <input v-model="scope.row.renalFunction" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="肾脏B超">
                <template slot-scope="scope">
                  <input v-model="scope.row.renalUlt" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,hypophosphatemiaData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addHypopRow(hypophosphatemiaData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">是否骨骼外科手术：
            <el-select size="small" v-model="ruleForm.isHaveSkeletalSurgery" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
          <div class="div-box" :style="{pointerEvents}" v-if="ruleForm.isHaveSkeletalSurgery === '1'">
            <p class="thyroid-title">是否行骨骼外科手术：
              <el-select size="small" v-model="ruleForm.isSkeletalSurgery" :disabled="isStatic">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="2"></el-option>
              </el-select>
              <span v-if="ruleForm.isSkeletalSurgery === '1'">
                手术目的：<el-select size="small" v-model="ruleForm.surgicalPurpose" :disabled="isStatic">
                <el-option label="修复骨折" value="1"></el-option>
                <el-option label="矫正" value="2"></el-option>
                <el-option label="预防骨骼畸形" value="3"></el-option>
              </el-select>
              </span>
            </p>
          </div>

          <p class="thyroid-title">
            是否对牛奶咖啡斑进行激光治疗：
            <el-select size="small" v-model="ruleForm.isLaserTherapy" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            是否进行心理疏导：
            <el-select size="small" v-model="ruleForm.isPsychologicalCounseling" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            生存状态：
            <el-select size="small" v-model="ruleForm.isSurvivalState" :disabled="isStatic">
              <el-option label="生存" value="1"></el-option>
              <el-option label="死亡" value="2"></el-option>
            </el-select>
            <span v-if="ruleForm.isSurvivalState === '2'" :style="{pointerEvents}">
              死亡原因：<input style="width:30%" v-model="ruleForm.CauseOfDeath" class="input-underLine"/>
            </span>
          </p>
        </el-tab-pane>

        <el-tab-pane name="nine">
          <span slot="label">其他</span>
          <p class="thyroid-title">
            是否存在性早熟：
            <el-select size="small" v-model="ruleForm.isSexualPrecocity" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            是否存在甲状腺功能亢进：
            <el-select size="small" v-model="ruleForm.isHyperthyroidism" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            是否存在生长激素分泌过多：
            <el-select size="small" v-model="ruleForm.isGrowthHormone" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>

          <p class="thyroid-title">
            是否存在皮质醇增多症：
            <el-select size="small" v-model="ruleForm.isIncreasedCortisol" :disabled="isStatic">
              <el-option label="是" value="1"></el-option>
              <el-option label="否" value="2"></el-option>
            </el-select>
          </p>
        </el-tab-pane>
      </el-tabs>
    </el-scrollbar>

  </div>
</template>

<script>
  import request from "../../script/request";
  import ImageUpload from "../imageViewer/ImageUpload";
  import {validateHeight, validateHeightByBirthdate} from '../../utils/heightValidator.js'

  export default {
    name: "MAS",
    components: {ImageUpload},
    props: {
      disClass: String,
      birthTime: String,
      queryId: String,
      sex: String,
      caseNum: String,
      default: String,
      isStatic: {
        type: Boolean,
        default: false
      }
    },
    mounted() {
      if (this.isStatic) {
        this.pointerEvents = "none";
      }
      if(this.$route.query.follow=== "follow"){
          this.activeName = "eight"
        }
    },
    data() {
      return {
        pointerEvents: "",
        activeName: 'one',
        skinExamination: [],
        isCafeMilk: false,
        boneExamination: [],
        isBoneSwelling: false,
        isJointDeformity: false,
        isJointPain: false,
        isBonePain: false,

        precocityData: [{
          time: '', name: '', dose: '', height: '', weight: '', bmi: '', breast: '', testis: '',
          hair: '', LH: '', FSH: '', E2: '', T: '', ultra: '', boneage: ''
        }],
        hyperData: [{time: '', method: '', dose: '', TF: '', thyroidUlt: ''}],
        growthData: [{time: '', name: '', dose: '', IGF1: '', IGFBP3: '', GH: '', MR: ''}],
        HPRLData: [{time: '', name: '', dose: '', PRL: ''}],
        cortisolData: [{time: '', name: '', dose: '', ACTH: '', cortisol8: '', cortisol4: '', FC: ''}],
        bonePainData: [{time: '', name: '', dose: '', boneTurnover: '', BMD: ''}],
        hypophosphatemiaData: [{
          time: '', dose: '', bloodCa: '', urineCa: '', urineP: '', PTH: '',
          renalFunction: '', renalUlt: ''
        }],

        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
          },
        },
        
        boneAgeUrl: '',
        checkResultUrl: '',

        ruleForm: {
          faHeight: '',//父亲身高：
          moHeight: '',//母亲身高：
          faWeight:'',
          moWeight:'',
          isDiabetesFamily: '',
          isThyroidFamily: '',
          isTumorFamily: '',
          isOtherFamily: '',

          checkTime: '',
          height: '',
          heightRate: '',
          weight: '',
          systolic: '',
          diastolic: '',
          heartRate: '',
          leftBreastDev: '',
          rightBreastDev: '',
          pubicHair: '',
          breastTend: '',
          clitoralHypertrophy: '',
          labialColoration: '',
          leftTesticleDev: '',
          rightTesticleDev: '',
          appleProtrusion: '',
          breastEnlarg: '',
          penileGrowth: '',
          goiter: '',
          skinExamination: [],
          cafeMilkPoint: '',
          boneExamination: [],
          boneSwelling: '',
          jointDeformity: '',
          jointPain: '',
          bonePain: '',

          bCheckTime: '',
          uterusUlt: '',
          uterusLength: '',
          uterusWidth: '',
          uterineThickness: '',
          leftOvary: '',
          leftOvaryLength: '',
          leftOvaryWidth: '',
          leftOvaryThickness: '',
          leftOvaryCyst: '',
          leftOvaryCystLength: '',
          leftOvaryCystWidth: '',
          leftOvaryCystThickness: '',
          rightOvary: '',
          rightOvaryLength: '',
          rightOvaryWidth: '',
          rightOvaryThickness: '',
          rightOvaryCyst: '',
          rightOvaryCystLength: '',
          rightOvaryCystWidth: '',
          rightOvaryCystThickness: '',
          thyroidUlt: '',
          thyroidUltAbnormal: '',
          adrenalUlt: '',
          adrenalUltAbnormal: '',
          renalUlt: '',
          renalUltAbnormal: '',
          boneX: '',
          boneXAbnormal: '',
          // headMRCT: '',
          // headMRCTAbnormal: '',
          bodyBoneScan: '',
          bodyBoneScanAbnormal: '',

          DiabetesDescription:'',//糖尿病家族史描述
          ThyroidDescription:'',//甲状腺疾病家族史描述
          TumorDescription:'',//肿瘤描述
          OtherDiseaseDescriptions:'',//其他疾病描述

          placeMR:'',//MR 部位
          typeMR:'',//描述类型
          MRdescription:'',//MR描述
         
          placeCT:'',//CT 部位
          typeCT:'',//描述类型
          CTdescription:'',//CT描述

          Neutrophils:'',//中性粒细胞
          erythrocyteNum:'',//红细胞个数
          gamaGT:'',//γ-GT
          ACTH8:'',//ACTH8am
          ACTH4:'',//ACTH4pm

          XTZ0:'',//血糖值0分钟
          XTZ3:'',//血糖值30分钟
          XTZ6:'',//血糖值60分钟
          XTZ9:'',//血糖值90分钟
          XTZ12:'',//血糖值120分钟

          isPrecociousPuberty:'',//判断有是否外周性性早熟

          isThyroidFunction:'',//判断有甲状腺功能是否亢进

          isGrowthHormonePlethora:'',//判断有生长激素分泌过多

          isHyperprolactinemia:'',//判断有高泌乳素血症

          isHypercortisolism:'',//判断有皮质醇增多症

          isOstealgia:'',//判断有骨痛

          isHaveHypophosphatemia:'',//判断有低磷酸盐血症

          isHaveSkeletalSurgery:'',//判断有骨骼外科手术

          isSurvivalState:'',//生存状态

          CauseOfDeath:'',//死亡原因

          commonCheckTime: '',
          ProtoliverEle:'',//原肝肾脂电解质
          ProtoliverEleAbnormal:'',//原肝肾脂电解质异常描述
          // AKP:'',//肝功能-碱性磷酸酶
          TBA:'',//肝功能-总胆汁酸
          Tca:'',//电解质-总钙
          Pi:'',//电解质-无机磷
          Trilaurin:'',//血脂-甘油三酯
          leukocyte: '',
          hemoglobin: '',
          platelet: '',
          ALT: '',
          AST: '',
          LDH: '',
          totalBilirubin: '',
          directBilirubin: '',
          indirectBilirubin: '',
          urea: '',
          creatinine: '',
          uricAcid: '',
          bloodK: '',
          bloodNa: '',
          bloodCl: '',
          TC: '',
          TG: '',
          HDL: '',
          LDL: '',
          boneCheckTime: '',
          bloodCa: '',
          bloodP: '',
          CTX: '',
          BGP: '',
          PINP: '',
          PTH: '',
          OHD25: '',
          ALP: '',
          urineCa: '',
          urineP: '',
          sexHormoneCheckTime: '',
          LH: '',
          FSH: '',
          E2: '',
          T: '',
          PRL: '',
          thyroidFunctionCheckTime: '',
          TT4: '',
          TT3: '',
          TSH: '',
          FT4: '',
          FT3: '',
          TPOAb: '',
          TGAb: '',
          adrenalFunctionCheckTime: '',
          ACTH: '',
          AM8: '',
          PM4: '',
          UFC: '',
          somatotropinFunctionCheckTime: '',
          GH: '',
          IGF1: '',
          IGFBP3: '',
          glycometabolismFunctionCheckTime: '',
          FBS: '',
          FINS: '',
          FCP: '',
          HbA1c: '',

          ecgExamination: '',
          ecgExaminationAbnormal: '',
          XbAge:'',//新增骨龄岁数
          XboneAge: '',
          pituitaryMR: '',
          pituitaryMRAbnormal: '',

          GNAS: '',
          GNASSampling: '',
          gnasSamplingPosition: '',
          pathBiopsyExamination: '',
          pathBiopsyPosition: '',

          GnRH: '',
          GnRHDrugName: '',
          GnRHDrugDosage: '',
          GnRHUsageTime: '',
          LFMax: '',
          FSHMax: '',
          LFRatio: '',
          LDDST: '',
          LDDSTDrugName: '',
          LDDSTDrugDosage: '',
          LDDSTUsageTime: '',
          ACTHAfter: '',
          ACTHBefore: '',
          cortisolAfter: '',
          cortisolBefore: '',
          UFFAfter: '',
          UFFBefore: '',
          GHGIT: '',
          GHGITDrugName: '',
          GHGITDrugDosage: '',
          GHGITUsageTime: '',
          GH0: '',
          GH3: '',
          GH6: '',
          GH9: '',
          GH12: '',

          isFinalHeight: '',
          finalHeight: '',
          isPPP: '',
          treatmentCyclePPP: '',
          isHyperthyreosis: '',
          treatmentCycleHyper: '',
          isGrowth: '',
          treatmentCycleGrowth: '',
          isHPRL: '',
          treatmentCycleHPRL: '',
          isCortisol: '',
          treatmentCycleCortisol: '',
          isIntracranialSurgery: '',
          isBilateralAdrenalectomy: '',
          isTreatBonePain: '',
          isHypophosphatemia: '',
          isSkeletalSurgery: '',
          surgicalPurpose: '',
          isLaserTherapy: '',
          isPsychologicalCounseling: '',

          isSexualPrecocity: '',
          isHyperthyroidism: '',
          isGrowthHormone: '',
          isIncreasedCortisol: '',

          precocityData: [],
          hyperData: [],
          growthData: [],
          HPRLData: [],
          cortisolData: [],
          bonePainData: [],
          hypophosphatemiaData: [],
        },
      }
    },
    activated() {
      if (this.$route.query.queryId) {
        this.getCase();
      } else {
        for (let key in this.ruleForm) {
          this.ruleForm[key] = ''
        }
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
    },

    methods: {
      getSkinExam() {
        this.isCafeMilk = false
        this.skinExamination.forEach(item => {
          if (item === "牛奶咖啡斑") {
            this.isCafeMilk = true
          }
        })
      },
      getBoneExam() {
        this.isBoneSwelling = false;
        this.isJointDeformity = false;
        this.isJointPain = false;
        this.isBonePain = false;
        this.boneExamination.forEach(item => {
          if (item === "骨膨胀或凸起") {
            this.isBoneSwelling = true
          }
          if (item === "关节畸形") {
            this.isJointDeformity = true
          }
          if (item === "关节疼痛") {
            this.isJointPain = true
          }
          if (item === "骨痛") {
            this.isBonePain = true
          }
        })
      },

      addRow(tableData) {
        tableData.push({
          time: '', name: '', dose: '', height: '', weight: '', bmi: '', breast: '', testis: '',
          hair: '', LH: '', FSH: '', E2: '', T: '', ultra: '', boneage: ''
        })
      },
      addHyperRow(tableData){
        tableData.push({
          time: '', method: '', dose: '', TF: '', thyroidUlt: ''
        })
      },
      addGrowRow(tableData) {
        tableData.push({
          time: '', name: '', dose: '', IGF1: '', IGFBP3: '', GH: '', MR: ''
        })
      },
      addHPRLRow(tableData) {
        tableData.push({
          time: '', name: '', dose: '', PRL: ''
        })
      },

      addCortisoRow(tableData) {
        tableData.push({
          time: '', name: '', dose: '', ACTH: '', cortisol8: '', cortisol4: '', FC: ''
        })
      },

      addBoneRow(tableData){
        tableData.push({
          time: '', name: '', dose: '', boneTurnover: '', BMD: ''
        })
      },

      addHypopRow(tableData){
        tableData.push({
          time: '', dose: '', bloodCa: '', urineCa: '', urineP: '', PTH: '', renalFunction: '', renalUlt: ''
        })
      },

      delRow(index, rows) {
        rows.splice(index, 1);
      },

      getData() {
        let data = null;
        this.ruleForm.skinExamination = this.skinExamination;
        this.ruleForm.boneExamination = this.boneExamination;
        this.ruleForm.precocityData = this.precocityData;
        this.ruleForm.hyperData = this.hyperData;
        this.ruleForm.growthData = this.growthData;
        this.ruleForm.HPRLData = this.HPRLData;
        this.ruleForm.cortisolData = this.cortisolData;
        this.ruleForm.bonePainData = this.bonePainData;
        this.ruleForm.hypophosphatemiaData = this.hypophosphatemiaData;
        data = this.ruleForm;
        return data;
      },

      getCase() {
        let queryId = null
        if (this.$route.query.queryId) {
          queryId = this.$route.query.queryId
        } else {
          queryId = this.queryId
        }
        request.getCase({queryId}, data => {
          let temFamHis = data.fam_his ? JSON.parse(data.fam_his) : {};
          this.ruleForm.faHeight = temFamHis['faHeight'];//父亲身高：
          this.ruleForm.moHeight = temFamHis['moHeight'];//母亲身高：
          this.ruleForm.faWeight = temFamHis['faWeight'];
          this.ruleForm.moWeight = temFamHis['moWeight'];
          this.ruleForm.isDiabetesFamily = temFamHis['isDiabetesFamily'];
          this.ruleForm.isThyroidFamily = temFamHis['isThyroidFamily'];
          this.ruleForm.isTumorFamily = temFamHis['isTumorFamily'];
          this.ruleForm.isOtherFamily = temFamHis['isOtherFamily'];
          this.ruleForm.DiabetesDescription = temFamHis['DiabetesDescription'];//糖尿病家族史描述
          this.ruleForm.ThyroidDescription = temFamHis['ThyroidDescription'];//甲状腺疾病家族史描述
          this.ruleForm.TumorDescription = temFamHis['TumorDescription'];//肿瘤描述
          this.ruleForm.OtherDiseaseDescriptions = temFamHis['OtherDiseaseDescriptions'];//其他描述
          if(data.check_time){
            this.ruleForm.checkTime = data.check_time;
          }
          let temNormal = data.gen_sit ? JSON.parse(data.gen_sit) : {};
          this.ruleForm.height = temNormal['height'];
          this.ruleForm.heightRate = temNormal['heightRate'];
          this.ruleForm.weight = temNormal['weight'];
          this.ruleForm.systolic = temNormal['systolic'];
          this.ruleForm.diastolic = temNormal['diastolic'];
          this.ruleForm.heartRate = temNormal['heartRate'];
          let temGirl = data.girl_sta_dev ? JSON.parse(data.girl_sta_dev) : {};
          this.ruleForm.leftBreastDev = temGirl['leftBreastDev'];
          this.ruleForm.rightBreastDev = temGirl['rightBreastDev'];
          this.ruleForm.pubicHair = temGirl['pubicHair'];
          this.ruleForm.breastTend = temGirl['breastTend'];
          this.ruleForm.clitoralHypertrophy = temGirl['clitoralHypertrophy'];
          this.ruleForm.labialColoration = temGirl['labialColoration'];
          let temBoy = data.boy_sta_dev ? JSON.parse(data.boy_sta_dev) : {};
          this.ruleForm.leftTesticleDev = temBoy['leftTesticleDev'];
          this.ruleForm.rightTesticleDev = temBoy['rightTesticleDev'];
          this.ruleForm.appleProtrusion = temBoy['appleProtrusion'];
          this.ruleForm.breastEnlarg = temBoy['breastEnlarg'];
          this.ruleForm.penileGrowth = temBoy['penileGrowth'];
          this.ruleForm.goiter = data.goiter;
          let temSkin = data.skin_exam ? JSON.parse(data.skin_exam) : {};
          this.skinExamination = temSkin['skinExamination'];
          this.getSkinExam();
          this.ruleForm.cafeMilkPoint = temSkin['cafeMilkPoint'];
          let temBone = data.ske_sur ? JSON.parse(data.ske_sur) : {};
          this.boneExamination = temBone['boneExamination'];
          this.getBoneExam();
          this.ruleForm.boneSwelling = temBone['boneSwelling'];
          this.ruleForm.jointDeformity = temBone['jointDeformity'];
          this.ruleForm.jointPain = temBone['jointPain'];
          this.ruleForm.bonePain = temBone['bonePain'];
          this.ruleForm.bCheckTime = data.ult_exam_ova_date;
          this.ruleForm.uterusUlt = data.ute_ult_con;
          let temSpeDes = data.spe_des_ute_con ? JSON.parse(data.spe_des_ute_con) : {};
          this.ruleForm.uterusLength = temSpeDes['uterusLength'];
          this.ruleForm.uterusWidth = temSpeDes['uterusWidth'];
          this.ruleForm.uterineThickness = temSpeDes['uterineThickness'];
          let temOvaUlt = data.ova_ult_con ? JSON.parse(data.ova_ult_con) : {};
          this.ruleForm.leftOvary = temOvaUlt['zclc'];
          this.ruleForm.leftOvaryLength = temOvaUlt['zclcc'];
          this.ruleForm.leftOvaryWidth = temOvaUlt['zclck'];
          this.ruleForm.leftOvaryThickness = temOvaUlt['zclcg'];
          this.ruleForm.leftOvaryCyst = temOvaUlt['zcnz'];
          this.ruleForm.leftOvaryCystLength = temOvaUlt['zcnzc'];
          this.ruleForm.leftOvaryCystWidth = temOvaUlt['zcnzk'];
          this.ruleForm.leftOvaryCystThickness = temOvaUlt['zcnzg'];
          this.ruleForm.rightOvary = temOvaUlt['yclc'];
          this.ruleForm.rightOvaryLength = temOvaUlt['yclcc'];
          this.ruleForm.rightOvaryWidth = temOvaUlt['yclck'];
          this.ruleForm.rightOvaryThickness = temOvaUlt['yclcg'];
          this.ruleForm.rightOvaryCyst = temOvaUlt['ycnz'];
          this.ruleForm.rightOvaryCystLength = temOvaUlt['ycnzc'];
          this.ruleForm.rightOvaryCystWidth = temOvaUlt['ycnzk'];
          this.ruleForm.rightOvaryCystThickness = temOvaUlt['ycnzg'];
          let temThyUlt = data.thy_ult_con ? JSON.parse(data.thy_ult_con) : {};
          this.ruleForm.thyroidUlt = temThyUlt['thyroidUlt'];
          this.ruleForm.thyroidUltAbnormal = temThyUlt['thyroidUltAbnormal'];





          let temAdrUlt = data.adr_ult_con ? JSON.parse(data.adr_ult_con) : {};
          this.ruleForm.adrenalUlt = temAdrUlt['adrenalUlt'];
          this.ruleForm.adrenalUltAbnormal = temAdrUlt['adrenalUltAbnormal'];
          let temRenUlt = data.ren_ult_con ? JSON.parse(data.ren_ult_con) : {};
          this.ruleForm.renalUlt = temRenUlt['renalUlt'];
          this.ruleForm.renalUltAbnormal = temRenUlt['renalUltAbnormal'];
          let temX= data.X_exa_dis ? JSON.parse(data.X_exa_dis) : {};
          this.ruleForm.boneX = temX['boneX'];
          this.ruleForm.boneXAbnormal = temX['boneXAbnormal'];
          //MR：
          let tempMR = data.hea_mr_exa ? JSON.parse(data.hea_mr_exa) : {};
          this.ruleForm.placeMR = tempMR['placeMR'];
          this.ruleForm.typeMR = tempMR['typeMR'];
          this.ruleForm.MRdescription = tempMR['MRdescription'];
          //CT:
          let temtMR = data.hea_ct_exa ? JSON.parse(data.hea_ct_exa) : {};
          this.ruleForm.placeCT = temtMR['placeCT'];
          this.ruleForm.typeCT = temtMR['typeCT'];
          this.ruleForm.CTdescription = temtMR['CTdescription'];
          let temFullScan = data.foll_body_scan_exa ? JSON.parse(data.foll_body_scan_exa) : {};
          this.ruleForm.bodyBoneScan = temFullScan['bodyBoneScan'];
          this.ruleForm.bodyBoneScanAbnormal = temFullScan['bodyBoneScanAbnormal'];
          this.ruleForm.commonCheckTime = data.lab_exa;
          let temBloodRou = data.blo_rou ? JSON.parse(data.blo_rou) : {};
          this.ruleForm.leukocyte = temBloodRou['leukocyte'];
          this.ruleForm.hemoglobin = temBloodRou['hemoglobin'];
          this.ruleForm.platelet = temBloodRou['platelet'];
          this.ruleForm.Neutrophils = temBloodRou['Neutrophils'];//中性粒细胞比例
          this.ruleForm.erythrocyteNum = temBloodRou['erythrocyteNum'];//红细胞计数
          let temLivFunc = data.liv_fun ? JSON.parse(data.liv_fun) : {};
          this.ruleForm.ALT = temLivFunc['ALT'];
          this.ruleForm.AST = temLivFunc['AST'];
          this.ruleForm.LDH = temLivFunc['LDH'];
          this.ruleForm.gamaGT = temLivFunc['gamaGT'];//γ-GT
          this.ruleForm.totalBilirubin = temLivFunc['totalBilirubin'];
          this.ruleForm.directBilirubin = temLivFunc['directBilirubin'];
          this.ruleForm.indirectBilirubin = temLivFunc['indirectBilirubin'];
          let temRenFunc = data.ren_fun ? JSON.parse(data.ren_fun) : {};
          this.ruleForm.urea = temRenFunc['urea'];
          this.ruleForm.creatinine = temRenFunc['creatinine'];
          this.ruleForm.uricAcid = temRenFunc['uricAcid'];
          let temElectrolyte= data.electrolyte ? JSON.parse(data.electrolyte) : {};
          this.ruleForm.bloodK = temElectrolyte['bloodK'];
          this.ruleForm.bloodNa = temElectrolyte['bloodNa'];
          this.ruleForm.bloodCl = temElectrolyte['bloodCl'];
          let temBloodFat= data.blood_fat ? JSON.parse(data.blood_fat) : {};
          this.ruleForm.TC = temBloodFat['TC'];
          this.ruleForm.TG = temBloodFat['TG'];
          this.ruleForm.HDL = temBloodFat['HDL'];
          this.ruleForm.LDL = temBloodFat['LDL'];
          this.ruleForm.boneCheckTime = data.bone_met_exa_date;
          let temBoneMet= data.bone_met_exa ? JSON.parse(data.bone_met_exa) : {};
          this.ruleForm.bloodCa = temBoneMet['bloodCa'];
          this.ruleForm.bloodP = temBoneMet['bloodP'];
          this.ruleForm.CTX = temBoneMet['CTX'];
          this.ruleForm.BGP = temBoneMet['BGP'];
          this.ruleForm.PINP = temBoneMet['PINP'];
          this.ruleForm.PTH = temBoneMet['PTH'];
          this.ruleForm.OHD25 = temBoneMet['OHD25'];
          this.ruleForm.ALP = temBoneMet['ALP'];
          this.ruleForm.urineCa = temBoneMet['urineCa'];
          this.ruleForm.urineP = temBoneMet['urineP'];
          this.ruleForm.sexHormoneCheckTime = data.sex_hor_exa_date;
          let temSexHor= data.sex_hor_exa ? JSON.parse(data.sex_hor_exa) : {};
          this.ruleForm.LH = temSexHor['LH'];
          this.ruleForm.FSH = temSexHor['FSH'];
          this.ruleForm.E2 = temSexHor['E2'];
          this.ruleForm.T = temSexHor['T'];
          this.ruleForm.PRL = temSexHor['PRL'];
          this.ruleForm.thyroidFunctionCheckTime = data.thy_fun_ant_date;
          let temThyFunc= data.thy_fun_ant_exa ? JSON.parse(data.thy_fun_ant_exa) : {};
          this.ruleForm.TT4 = temThyFunc['TT4'];
          this.ruleForm.TT3 = temThyFunc['TT3'];
          this.ruleForm.TSH = temThyFunc['TSH'];
          this.ruleForm.FT4 = temThyFunc['FT4'];
          this.ruleForm.FT3 = temThyFunc['FT3'];
          this.ruleForm.TPOAb = temThyFunc['TPOAb'];
          this.ruleForm.TGAb = temThyFunc['TGAb'];
          this.ruleForm.adrenalFunctionCheckTime = data.adr_fun_exa_date;
          let temAdrFunc= data.adr_fun_exa ? JSON.parse(data.adr_fun_exa) : {};
          this.ruleForm.ACTH = temAdrFunc['ACTH'];
          this.ruleForm.ACTH8 = temAdrFunc['ACTH8'];//ACTH 8am
          this.ruleForm.ACTH4 = temAdrFunc['ACTH4'];//ACTH ACTH4
          this.ruleForm.AM8 = temAdrFunc['AM8'];
          this.ruleForm.PM4 = temAdrFunc['PM4'];
          this.ruleForm.UFC = temAdrFunc['UFC'];
          this.ruleForm.somatotropinFunctionCheckTime = data.gro_hor_exa;
          let temPhyExa= data.phy_exa ? JSON.parse(data.phy_exa) : {};
          this.ruleForm.GH = temPhyExa['GH'];
          this.ruleForm.IGF1 = temPhyExa['IGF1'];
          this.ruleForm.IGFBP3 = temPhyExa['IGFBP3'];
          this.ruleForm.glycometabolismFunctionCheckTime = data.glu_met_date;
          let temGluMet= data.glu_met ? JSON.parse(data.glu_met) : {};
          this.ruleForm.FBS = temGluMet['FBS'];
          this.ruleForm.FINS = temGluMet['FINS'];
          this.ruleForm.FCP = temGluMet['FCP'];
          this.ruleForm.HbA1c = temGluMet['HbA1c'];
          let temEcgExa= data.ecg_exa ? JSON.parse(data.ecg_exa) : {};
          this.ruleForm.ecgExamination = temEcgExa['ecgExamination'];
          this.ruleForm.ecgExaminationAbnormal = temEcgExa['ecgExaminationAbnormal'];
          this.ruleForm.XboneAge = data.x_bone_exa;
          let temPitExa= data.pit_exa ? JSON.parse(data.pit_exa) : {};
          this.ruleForm.pituitaryMR = temPitExa['pituitaryMR'];
          this.ruleForm.pituitaryMRAbnormal = temPitExa['pituitaryMRAbnormal'];
          this.ruleForm.GNAS = data.GNAS;
          let temGNAS= data.GNAS_sam_loc ? JSON.parse(data.GNAS_sam_loc) : {};
          this.ruleForm.GNASSampling = temGNAS['GNASSampling'];
          this.ruleForm.gnasSamplingPosition = temGNAS['gnasSamplingPosition'];
          this.ruleForm.pathBiopsyExamination = data.pat_exa;
          this.ruleForm.pathBiopsyPosition = data.pat_sam_loc;
          this.ruleForm.GnRH = data.GnRH;
          let temGnRH= data.GnRH_eva ? JSON.parse(data.GnRH_eva) : {};
          this.ruleForm.GnRHDrugName = temGnRH['GnRHDrugName'];
          this.ruleForm.GnRHDrugDosage = temGnRH['GnRHDrugDosage'];
          this.ruleForm.GnRHUsageTime = temGnRH['GnRHUsageTime'];
          this.ruleForm.LFMax = temGnRH['LFMax'];
          this.ruleForm.FSHMax = temGnRH['FSHMax'];
          this.ruleForm.LFRatio = temGnRH['LFRatio'];
          this.ruleForm.LDDST = data.low_dose;
          let temLowDose= data.low_dose_eva ? JSON.parse(data.low_dose_eva) : {};
          this.ruleForm.LDDSTDrugName = temLowDose['LDDSTDrugName'];
          this.ruleForm.LDDSTDrugDosage = temLowDose['LDDSTDrugDosage'];
          this.ruleForm.LDDSTUsageTime = temLowDose['LDDSTUsageTime'];
          this.ruleForm.ACTHAfter = temLowDose['ACTHAfter'];
          this.ruleForm.ACTHBefore = temLowDose['ACTHBefore'];
          this.ruleForm.cortisolAfter = temLowDose['cortisolAfter'];
          this.ruleForm.cortisolBefore = temLowDose['cortisolBefore'];
          this.ruleForm.UFFAfter = temLowDose['UFFAfter'];
          this.ruleForm.UFFBefore = temLowDose['UFFBefore'];
          this.ruleForm.GHGIT = data.gro_glu;
          let temGHG= data.gro_glu_eva ? JSON.parse(data.gro_glu_eva) : {};
          this.ruleForm.GHGITDrugName = temGHG['GHGITDrugName'];
          this.ruleForm.GHGITDrugDosage = temGHG['GHGITDrugDosage'];
          this.ruleForm.GHGITUsageTime = temGHG['GHGITUsageTime'];
          this.ruleForm.GH0 = temGHG['GH0'];
          this.ruleForm.GH3 = temGHG['GH3'];
          this.ruleForm.GH6 = temGHG['GH6'];
          this.ruleForm.GH9 = temGHG['GH9'];
          this.ruleForm.GH12 = temGHG['GH12'];
          this.ruleForm.XTZ0 = temGHG['XTZ0'];
          this.ruleForm.XTZ3 = temGHG['XTZ3'];
          this.ruleForm.XTZ6 = temGHG['XTZ6'];
          this.ruleForm.XTZ9 = temGHG['XTZ9'];
          this.ruleForm.XTZ12 = temGHG['XTZ12'];
          this.ruleForm.isSexualPrecocity = data.sex_pre;
          this.ruleForm.isHyperthyroidism = data.hyper;
          this.ruleForm.isGrowthHormone = data.is_gro_hor;
          this.ruleForm.isIncreasedCortisol = data.is_inc_cor;
          let tempData = data.glu_img_path ? JSON.parse(data.glu_img_path) : {};
          this.getBoneImagePath(tempData['骨龄测定图片']);
          this.getCheckImagePath(tempData['检查结果']);
          let queryMId = data.id;
          this.getMasFollow(queryMId)
        }, error => {
          console.log(error)
        })
      },

      getBoneImagePath(imagePath) {
        if (imagePath && imagePath[0]) {
          this.boneAgeUrl = imagePath[0];
        } else {
          this.boneAgeUrl = '';
        }
      },

      getCheckImagePath(imagePath){
        if (imagePath && imagePath[0]) {
          this.checkResultUrl = imagePath[0];
        } else {
          this.checkResultUrl = '';
        }
      },

      getMasFollow(queryMId){
        request.getMasFollow({queryMId}, data => {
          let temFinalHeight= data.is_finalhei ? JSON.parse(data.is_finalhei) : {};
          this.ruleForm.isFinalHeight = temFinalHeight['isFinalHeight'];
          this.ruleForm.finalHeight = temFinalHeight['finalHeight'];
          let temPPP= data.is_per_pre ? JSON.parse(data.is_per_pre) : {};
          this.ruleForm.isPPP = temPPP['isPPP'];
          this.ruleForm.isPrecociousPuberty = temPPP['isPrecociousPuberty'];
          this.ruleForm.treatmentCyclePPP = temPPP['treatmentCyclePPP'];
          this.precocityData = JSON.parse(data.per_pre_sf.replace(/'/g, "\""));
          let temHyper= data.is_hyper ? JSON.parse(data.is_hyper) : {};
          this.ruleForm.isThyroidFunction = temHyper['isThyroidFunction'];
          this.ruleForm.isHyperthyreosis = temHyper['isHyperthyreosis'];
          this.ruleForm.treatmentCycleHyper = temHyper['treatmentCycleHyper'];
          this.hyperData = JSON.parse(data.hyper_sf.replace(/'/g, "\""));
          let temGro= data.is_gro_hor ? JSON.parse(data.is_gro_hor) : {};
          this.ruleForm.isGrowthHormonePlethora = temGro['isGrowthHormonePlethora'];//判断有生长激素分泌过多
          this.ruleForm.isGrowth = temGro['isGrowth'];
          this.ruleForm.treatmentCycleGrowth = temGro['treatmentCycleGrowth'];
          this.growthData = JSON.parse(data.gro_hor_sf.replace(/'/g, "\""));
          let temTreHpy= data.is_tre_hpy ? JSON.parse(data.is_tre_hpy) : {};
          this.ruleForm.isHyperprolactinemia = temTreHpy['isHyperprolactinemia'];//判断有高泌乳素血症
          this.ruleForm.isHPRL = temTreHpy['isHPRL'];
          this.ruleForm.treatmentCycleHPRL = temTreHpy['treatmentCycleHPRL'];
          this.HPRLData = JSON.parse(data.tre_hpy_sf.replace(/'/g, "\""));
          let temIncCor= data.is_inc_cor ? JSON.parse(data.is_inc_cor) : {};
          this.ruleForm.isCortisol = temIncCor['isCortisol'];
          this.ruleForm.isHypercortisolism = temIncCor['isHypercortisolism'];//判断有皮质醇增多症
          this.ruleForm.treatmentCycleCortisol = temIncCor['treatmentCycleCortisol'];
          this.cortisolData = JSON.parse(data.inc_cor_sf.replace(/'/g, "\""));
          this.ruleForm.isIntracranialSurgery = data.is_int_sur;
          this.ruleForm.isBilateralAdrenalectomy = data.is_bil_adr;
          let temBonePain= data.is_bon_pai ? JSON.parse(data.is_bon_pai) : {};
          this.ruleForm.isTreatBonePain = temBonePain['isTreatBonePain'];
          this.ruleForm.isOstealgia =  temBonePain['isOstealgia'];
          this.bonePainData = JSON.parse(data.bon_pai_sf.replace(/'/g, "\""));
          let temBonehypop= data.hypop ? JSON.parse(data.hypop) : {};
          this.ruleForm.isHypophosphatemia =  temBonehypop['isHypophosphatemia'];
          this.ruleForm.isHaveHypophosphatemia =  temBonehypop['isHaveHypophosphatemia'];
          this.hypophosphatemiaData = JSON.parse(data.hypop_sf.replace(/'/g, "\""));
          let temSkeSur= data.is_ske_sur ? JSON.parse(data.is_ske_sur) : {};
          this.ruleForm.isHaveSkeletalSurgery = temSkeSur['isHaveSkeletalSurgery'];
          this.ruleForm.isSkeletalSurgery = temSkeSur['isSkeletalSurgery'];
          this.ruleForm.surgicalPurpose = temSkeSur['surgicalPurpose'];
          let temSurSta= data.sur_sta ? JSON.parse(data.sur_sta) : {};
          this.ruleForm.isSurvivalState = temSurSta['isSurvivalState'];
          this.ruleForm.CauseOfDeath = temSurSta['CauseOfDeath'];
          this.ruleForm.isLaserTherapy = data.is_cafe_spot;
          this.ruleForm.isPsychologicalCounseling = data.is_psy_cou;
        }, error => {
          console.log(error)
        })
      },

      upBoneImage(v) {
        this.boneAgeUrl = v;
      },

      upCheckResul(v){
        this.checkResultUrl = v;
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
          this.getBoneImagePath(tempData['骨龄测定图片']);
          this.getCheckImagePath(tempData['检查结果']);
        }).catch(() => {

        });
      },
    //父亲身高提示
      validateFatherHeight() {
        validateHeight(19, this.ruleForm.faHeight, 'father')
      },
      //母亲身高提示
      validateMotherHeight(){
        validateHeight(19, this.ruleForm.faWeight, 'mother')
      },
      // 现身高提示
      validateHeightFun(){
        validateHeightByBirthdate(this.birthTime,this.ruleForm.height)
      },
      validateFinalHeight() {
        validateHeightByBirthdate(this.birthTime,this.ruleForm.finalHeight)
      },
      validateHeights(height){
        validateHeightByBirthdate(this.birthTime,height)
      }
    },

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
</style>
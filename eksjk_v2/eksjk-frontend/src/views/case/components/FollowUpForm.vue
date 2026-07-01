<template>
  <el-drawer :model-value="visible" :title="isEdit ? '编辑随访记录' : '新增随访记录'" size="80%"
             @close="handleClose" @update:model-value="val => emit('update:visible', val)">
    <el-form ref="formRef" :model="formData" label-width="120px">
      <el-card shadow="never" class="form-section">
        <template #header><span>基础测量</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="随访日期" prop="follTime" :rules="[{ required: true, message: '请选择随访日期' }]">
              <el-date-picker v-model="formData.follTime" type="date" placeholder="选择日期"
                              value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="formData.age" placeholder="年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身高">
              <el-input v-model="formData.ht" placeholder="cm" @blur="calcBmi">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体重">
              <el-input v-model="formData.wt" placeholder="kg" @blur="calcBmi">
                <template #suffix>kg</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="BMI">
              <el-input v-model="formData.bmi" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体脂率">
              <el-input v-model="formData.bodyFat" placeholder="%">
                <template #suffix>%</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="腰围">
              <el-input v-model="formData.waistline" placeholder="cm">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="臀围">
              <el-input v-model="formData.hips" placeholder="cm">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>骨龄与发育</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="R系列骨龄">
              <el-input v-model="formData.rboneAge" placeholder="岁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="C系列骨龄">
              <el-input v-model="formData.cboneAge" placeholder="岁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生殖器分期">
              <el-input v-model="formData.genStag" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阴毛分期">
              <el-input v-model="formData.pubStag" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 性激素及相关 -->
      <el-card v-if="showBasicHormones" shadow="never" class="form-section">
        <template #header><span>性激素及相关</span></template>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="LH(mIU/mL)"><el-input v-model="formData.lh" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.lhCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="FSH(mIU/mL)"><el-input v-model="formData.fsh" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.fshCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="E2(pg/mL)"><el-input v-model="formData.e2" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.e2CheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="T(ng/dL)"><el-input v-model="formData.t" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.tCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="PRL(ng/mL)"><el-input v-model="formData.prl" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.prlCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row v-if="showDsdHormones" :gutter="12">
          <el-col :span="4"><el-form-item label="DHT(ng/dL)"><el-input v-model="formData.dht" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.dhtCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="FT(ng/dL)"><el-input v-model="formData.ft" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ftCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="SHBG(nmol/L)"><el-input v-model="formData.shbg" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.shbgCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row v-if="showDsdHormones" :gutter="12">
          <el-col :span="4"><el-form-item label="AMH(ng/mL)"><el-input v-model="formData.amh" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.amhCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="INHB(pg/mL)"><el-input v-model="formData.inhb" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.inhbCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-card>

      <!-- 生长因子与代谢 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>生长因子与代谢</span></template>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="IGF-1(ng/mL)"><el-input v-model="formData.igf1" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.igf1CheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="IGFBP-3(ug/mL)"><el-input v-model="formData.igfbp3" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.igfbp3CheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="空腹血糖(mmol/L)"><el-input v-model="formData.fasBloodGlu" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.fasBloodGluCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="空腹胰岛素(uIU/mL)"><el-input v-model="formData.fasInsulin" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.fasInsulinCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="糖化血红蛋白(%)"><el-input v-model="formData.glyHem" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.glyHemCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="HbA1c(%)"><el-input v-model="formData.glyHemA" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.glyHemACheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-card>

      <!-- 甲状腺功能 -->
      <el-card v-if="showThyroid" shadow="never" class="form-section">
        <template #header><span>甲状腺功能</span></template>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="TSH(uIU/mL)"><el-input v-model="formData.tsh" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.tshCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="FT3(pg/mL)"><el-input v-model="formData.ft3" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ft3CheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="FT4(ng/dL)"><el-input v-model="formData.ft4" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ft4CheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="TPOAb(IU/mL)"><el-input v-model="formData.tpoab" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.tpoabCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="TgAb(IU/mL)"><el-input v-model="formData.tgab" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.tgabCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="甲功评估"><el-input v-model="formData.thyroidFunction" size="small" placeholder="正常/异常" /></el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 肾上腺激素 -->
      <el-card v-if="showAdrenal" shadow="never" class="form-section">
        <template #header><span>肾上腺激素</span></template>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="ACTH(pg/mL)"><el-input v-model="formData.acth" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.acthCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="皮质醇(ug/dL)"><el-input v-model="formData.cortisol" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.cortisolCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="17-OHP(nmol/L)"><el-input v-model="formData.ohp" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ohpCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="DHEA-S(ug/dL)"><el-input v-model="formData.dheas" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.dheasCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="雄烯二酮(ng/mL)"><el-input v-model="formData.androstenedione" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.androstenedioneCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="4"><el-form-item label="AFP(ng/mL)"><el-input v-model="formData.afp" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.afpCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="CEA(ng/mL)"><el-input v-model="formData.cea" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ceaCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-card>

      <!-- 激发试验 -->
      <el-card v-if="showProvocation" shadow="never" class="form-section">
        <template #header><span>激发试验</span></template>
        <template v-if="showDsdHormones">
          <el-row :gutter="12">
            <el-col :span="4"><el-form-item label="HCG激发前T"><el-input v-model="formData.hcg" size="small" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.hcgCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="HCG激发后T"><el-input v-model="formData.hcgt" size="small" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.hcgtCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="HCG激发后DHT"><el-input v-model="formData.hcgdht" size="small" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.hcgdhtCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="4"><el-form-item label="HCG激发后AD"><el-input v-model="formData.hcgad" size="small" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.hcgadCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          </el-row>
        </template>
        <el-row v-if="showGnrhExcitation" :gutter="12">
          <el-col :span="4"><el-form-item label="GnRH激发LHmax"><el-input v-model="formData.lhMax" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.lhMaxCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="GnRH激发FSHmax"><el-input v-model="formData.fshMax" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.fshMaxCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row v-if="showGhExcitation" :gutter="12">
          <el-col :span="4"><el-form-item label="GH峰值(ng/mL)"><el-input v-model="formData.gh" size="small" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="日期"><el-date-picker v-model="formData.ghCheckDate" type="date" value-format="YYYY-MM-DD" size="small" style="width:100%" /></el-form-item></el-col>
        </el-row>
      </el-card>

      <!-- 影像检查 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>影像检查</span></template>
        <el-row :gutter="12">
          <el-col v-if="showGonBUlt" :span="12">
            <el-form-item label="性腺B超"><el-input v-model="formData.gonBUlt" type="textarea" :rows="2" size="small" /></el-form-item>
          </el-col>
          <el-col v-if="showPituitaryMri" :span="12">
            <el-form-item label="垂体MRI"><el-input v-model="formData.pituitaryMri" type="textarea" :rows="2" size="small" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col v-if="showThyroidUlt" :span="12">
            <el-form-item label="甲状腺B超"><el-input v-model="formData.thyroidUlt" type="textarea" :rows="2" size="small" /></el-form-item>
          </el-col>
          <el-col v-if="showBonMinDen" :span="12">
            <el-form-item label="骨密度"><el-input v-model="formData.bonMinDen" size="small" /></el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 常规实验室检查 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>常规实验室检查</span></template>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="血常规"><el-input v-model="formData.bloodRoutine" type="textarea" :rows="2" size="small" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尿常规"><el-input v-model="formData.urineRoutine" type="textarea" :rows="2" size="small" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="乙肝三系"><el-input v-model="formData.hepatitisB" size="small" /></el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>诊疗方案</span></template>
        <el-form-item label="诊疗方案">
          <el-input v-model="formData.diaTreaPlan" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="其他">
          <el-input v-model="formData.other" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="是否达终身高">
          <el-select v-model="formData.isFinalhei">
            <el-option label="是" value="是" />
            <el-option label="否" value="否" />
            <el-option label="无" value="无" />
          </el-select>
        </el-form-item>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>眼科检查</span></template>
        <el-form-item label="是否有做眼科检查">
          <el-radio-group v-model="eyeExam.hasExam" @change="syncEyeExam">
            <el-radio value="0">无</el-radio>
            <el-radio value="1">有</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="eyeExam.hasExam === '1'">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="检查时间">
                <el-date-picker v-model="eyeExam.examDate" type="date"
                  value-format="YYYY-MM-DD" placeholder="请选择日期" @change="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="裸眼视力 右">
                <el-input v-model="eyeExam.nakedVisionRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="裸眼视力 左">
                <el-input v-model="eyeExam.nakedVisionLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="矫正视力 右">
                <el-input v-model="eyeExam.correctedVisionRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="矫正视力 左">
                <el-input v-model="eyeExam.correctedVisionLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="眼轴 右 (cm)">
                <el-input v-model="eyeExam.axialLengthRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="眼轴 左 (cm)">
                <el-input v-model="eyeExam.axialLengthLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-card>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createFollowUp, updateFollowUp } from '@/api/followup'

const props = defineProps({
  visible: { type: Boolean, default: false },
  patientId: { type: String, required: true },
  diseaseType: { type: String, default: '' },
  editData: { type: Object, default: null }
})

const emit = defineEmits(['update:visible', 'saved'])

const isEdit = computed(() => !!props.editData?.id)
const formRef = ref(null)
const saving = ref(false)

// 疾病类型判断
const isDsd = computed(() => props.diseaseType === 'dsd')
const isCpp = computed(() => props.diseaseType === 'cpp')
const isFss = computed(() => props.diseaseType === 'fss')
const isSga = computed(() => props.diseaseType === 'sga')
const isSss = computed(() => props.diseaseType === 'sss')
const isMas = computed(() => props.diseaseType === 'mas')
const isEltm = computed(() => props.diseaseType === 'eltm')

// 区块条件显示（与基线辅助检查保持一致）
const showBasicHormones = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showDsdHormones = computed(() => isDsd.value)
const showAdrenal = computed(() => ['dsd', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showProvocation = computed(() => ['dsd', 'cpp', 'eltm'].includes(props.diseaseType))
const showGnrhExcitation = computed(() => ['dsd', 'cpp'].includes(props.diseaseType))
const showGhExcitation = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showThyroid = computed(() => ['mas', 'eltm'].includes(props.diseaseType))
const showGonBUlt = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showPituitaryMri = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showThyroidUlt = computed(() => ['cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showBonMinDen = computed(() => ['fss', 'sga', 'sss'].includes(props.diseaseType))

const formData = reactive({
  patientId: '',
  follTime: new Date().toISOString().slice(0, 19),
  age: '',
  ht: '',
  wt: '',
  bmi: '',
  bodyFat: '',
  waistline: '',
  hips: '',
  rboneAge: '',
  cboneAge: '',
  genStag: '',
  pubStag: '',
  // 性激素
  lh: '', fsh: '', e2: '', t: '', prl: '',
  dht: '', ft: '', shbg: '', amh: '', inhb: '',
  // 生长因子与代谢
  igf1: '', igfbp3: '', fasBloodGlu: '', fasInsulin: '', glyHem: '', glyHemA: '',
  // 甲状腺
  tsh: '', ft3: '', ft4: '', tpoab: '', tgab: '', thyroidFunction: '',
  // 肾上腺
  acth: '', cortisol: '', ohp: '', dheas: '', androstenedione: '', afp: '', cea: '',
  // 激发试验
  hcg: '', hcgt: '', hcgdht: '', hcgad: '', lhMax: '', fshMax: '', gh: '',
  // 常规实验室
  bloodRoutine: '', urineRoutine: '', hepatitisB: '',
  // 影像
  gonBUlt: '', pituitaryMri: '', thyroidUlt: '', gonBUltDetail: '', bonMinDen: '',
  // 检查日期 — 性激素
  lhCheckDate: '', fshCheckDate: '', e2CheckDate: '', tCheckDate: '', prlCheckDate: '',
  dhtCheckDate: '', ftCheckDate: '', shbgCheckDate: '', amhCheckDate: '', inhbCheckDate: '',
  // 检查日期 — 生长因子
  igf1CheckDate: '', igfbp3CheckDate: '', fasBloodGluCheckDate: '', fasInsulinCheckDate: '',
  glyHemCheckDate: '', glyHemACheckDate: '',
  // 检查日期 — 甲状腺
  tshCheckDate: '', ft3CheckDate: '', ft4CheckDate: '', tpoabCheckDate: '', tgabCheckDate: '',
  // 检查日期 — 肾上腺
  acthCheckDate: '', cortisolCheckDate: '', ohpCheckDate: '', dheasCheckDate: '',
  androstenedioneCheckDate: '', afpCheckDate: '', ceaCheckDate: '',
  // 检查日期 — 激发试验
  hcgCheckDate: '', hcgtCheckDate: '', hcgdhtCheckDate: '', hcgadCheckDate: '',
  lhMaxCheckDate: '', fshMaxCheckDate: '', ghCheckDate: '',
  // 诊疗
  diaTreaPlan: '', other: '', isFinalhei: '',
  eyeExam: ''
})

// 眼科检查
const eyeExam = reactive({
  hasExam: '0',
  examDate: '',
  nakedVisionRight: '',
  nakedVisionLeft: '',
  correctedVisionRight: '',
  correctedVisionLeft: '',
  axialLengthRight: '',
  axialLengthLeft: ''
})

watch(() => props.visible, (val) => {
  if (val) {
    formData.patientId = props.patientId
    if (props.editData) {
      Object.keys(formData).forEach(key => {
        if (props.editData[key] !== undefined) {
          formData[key] = props.editData[key]
        }
      })
      parseEyeExam(props.editData.eyeExam)
    }
  }
})

function calcBmi() {
  if (formData.ht && formData.wt) {
    try {
      const h = parseFloat(formData.ht) / 100
      const w = parseFloat(formData.wt)
      if (h > 0 && w > 0) {
        formData.bmi = (w / (h * h)).toFixed(1)
      }
    } catch (e) {
      formData.bmi = ''
    }
  }
}

function syncEyeExam() {
  formData.eyeExam = JSON.stringify({ ...eyeExam })
}

function parseEyeExam(json) {
  if (!json) return
  try {
    const obj = JSON.parse(json)
    Object.keys(eyeExam).forEach(k => {
      if (obj[k] !== undefined) eyeExam[k] = obj[k]
    })
  } catch { /* ignore parse errors */ }
}

async function handleSave() {
  try {
    await formRef.value?.validate()
  } catch {
    ElMessage.warning('请完善必填信息')
    return
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await updateFollowUp(props.editData.id, formData)
    } else {
      await createFollowUp(formData)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    emit('saved')
    handleClose()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

function handleClose() {
  emit('update:visible', false)
  // 重置表单
  Object.keys(formData).forEach(key => {
    if (key === 'follTime') {
      formData[key] = new Date().toISOString().slice(0, 19)
    } else {
      formData[key] = ''
    }
  })
  // 重置眼科检查
  eyeExam.hasExam = '0'
  eyeExam.examDate = ''
  eyeExam.nakedVisionRight = ''
  eyeExam.nakedVisionLeft = ''
  eyeExam.correctedVisionRight = ''
  eyeExam.correctedVisionLeft = ''
  eyeExam.axialLengthRight = ''
  eyeExam.axialLengthLeft = ''
}
</script>

<style scoped>
.form-section {
  margin-bottom: 12px;
}
.form-section :deep(.el-card__header) {
  padding: 8px 16px;
  background: #fafafa;
  font-weight: 600;
  font-size: 14px;
}
</style>

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
            <el-form-item label="随访年龄">
              <el-input :model-value="followUpAge" disabled placeholder="自动计算" />
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
          <el-col :span="12">
            <el-form-item label="生殖器分期">
              <el-input v-model="formData.genStag" placeholder="生殖器分期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阴毛分期">
              <el-input v-model="formData.pubStag" placeholder="阴毛分期" />
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

        <!-- 甲状腺B超（左右侧详细评估） -->
        <template v-if="showGonBUlt">
          <div class="sub-section-title">甲状腺B超</div>
          <el-row :gutter="16">
            <el-col :span="24"><div class="sub-item-title">左侧甲状腺B超</div></el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="左侧甲状腺">
                <el-radio-group v-model="thyroidUltDetail.leftResult" @change="syncThyroidUltDetail">
                  <el-radio value="1">正常没有变化</el-radio>
                  <el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <template v-if="thyroidUltDetail.leftResult === '2'">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="甲状腺结节">
                  <el-select v-model="thyroidUltDetail.leftNoduleGrade" placeholder="请选择分级" @change="syncThyroidUltDetail">
                    <el-option label="一级" value="1" /><el-option label="二级" value="2" />
                    <el-option label="三级" value="3" /><el-option label="四级" value="4" />
                    <el-option label="五级" value="5" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="大小"><el-input v-model="thyroidUltDetail.leftSize" placeholder="请输入大小" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="弥漫性病变"><el-input v-model="thyroidUltDetail.leftDiffuseLesion" placeholder="请输入弥漫性病变" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="其他"><el-input v-model="thyroidUltDetail.leftOther" placeholder="请输入其他" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
            </el-row>
          </template>
          <el-row :gutter="16">
            <el-col :span="24"><div class="sub-item-title">右侧甲状腺B超</div></el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="右侧甲状腺">
                <el-radio-group v-model="thyroidUltDetail.rightResult" @change="syncThyroidUltDetail">
                  <el-radio value="1">正常没有变化</el-radio>
                  <el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <template v-if="thyroidUltDetail.rightResult === '2'">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="甲状腺结节">
                  <el-select v-model="thyroidUltDetail.rightNoduleGrade" placeholder="请选择分级" @change="syncThyroidUltDetail">
                    <el-option label="一级" value="1" /><el-option label="二级" value="2" />
                    <el-option label="三级" value="3" /><el-option label="四级" value="4" />
                    <el-option label="五级" value="5" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="大小"><el-input v-model="thyroidUltDetail.rightSize" placeholder="请输入大小" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="弥漫性病变"><el-input v-model="thyroidUltDetail.rightDiffuseLesion" placeholder="请输入弥漫性病变" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="其他"><el-input v-model="thyroidUltDetail.rightOther" placeholder="请输入其他" @input="syncThyroidUltDetail" /></el-form-item>
              </el-col>
            </el-row>
          </template>
        </template>

        <!-- 性腺B超 -->
        <template v-if="showGonBUlt">
          <div class="sub-section-title">性腺B超</div>
          <template v-if="patientSex === '1'">
            <el-row :gutter="16">
              <el-col :span="24"><div class="sub-item-title">睾丸大小</div></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="6"><el-form-item label="右侧长(cm)"><el-input v-model="gonBUltDetail.rightTestisLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="右侧宽(cm)"><el-input v-model="gonBUltDetail.rightTestisWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="右侧高(cm)"><el-input v-model="gonBUltDetail.rightTestisHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="右侧长径(cm)"><el-input v-model="gonBUltDetail.rightTestisLongDiameter" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="6"><el-form-item label="左侧长(cm)"><el-input v-model="gonBUltDetail.leftTestisLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="左侧宽(cm)"><el-input v-model="gonBUltDetail.leftTestisWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="左侧高(cm)"><el-input v-model="gonBUltDetail.leftTestisHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="6"><el-form-item label="左侧长径(cm)"><el-input v-model="gonBUltDetail.leftTestisLongDiameter" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
          </template>
          <template v-if="patientSex === '2'">
            <el-row :gutter="16">
              <el-col :span="24"><div class="sub-item-title">子宫</div></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="子宫长(cm)"><el-input v-model="gonBUltDetail.uterusLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="子宫宽(cm)"><el-input v-model="gonBUltDetail.uterusWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="子宫高(cm)"><el-input v-model="gonBUltDetail.uterusHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="宫颈长(cm)"><el-input v-model="gonBUltDetail.cervixLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="内膜厚度(cm)"><el-input v-model="gonBUltDetail.endometriumThickness" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24"><div class="sub-item-title">卵巢</div></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="左卵巢长(cm)"><el-input v-model="gonBUltDetail.leftOvaryLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="左卵巢宽(cm)"><el-input v-model="gonBUltDetail.leftOvaryWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="左卵巢高(cm)"><el-input v-model="gonBUltDetail.leftOvaryHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="右卵巢长(cm)"><el-input v-model="gonBUltDetail.rightOvaryLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="右卵巢宽(cm)"><el-input v-model="gonBUltDetail.rightOvaryWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="右卵巢高(cm)"><el-input v-model="gonBUltDetail.rightOvaryHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="最大滤泡直径(cm)"><el-input v-model="gonBUltDetail.maxFollicleDiameter" @input="syncGonBUltDetail" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24">
                <el-form-item label="有无囊肿">
                  <el-radio-group v-model="gonBUltDetail.hasCyst" @change="syncGonBUltDetail">
                    <el-radio value="0">无</el-radio>
                    <el-radio value="1">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <template v-if="gonBUltDetail.hasCyst === '1'">
              <el-row :gutter="16">
                <el-col :span="8"><el-form-item label="囊肿侧"><el-input v-model="gonBUltDetail.cystSide" placeholder="左/右" @input="syncGonBUltDetail" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="囊肿长(cm)"><el-input v-model="gonBUltDetail.cystLength" @input="syncGonBUltDetail" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="囊肿宽(cm)"><el-input v-model="gonBUltDetail.cystWidth" @input="syncGonBUltDetail" /></el-form-item></el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8"><el-form-item label="囊肿高(cm)"><el-input v-model="gonBUltDetail.cystHeight" @input="syncGonBUltDetail" /></el-form-item></el-col>
              </el-row>
            </template>
          </template>
        </template>

        <!-- 卵巢囊肿 -->
        <el-row v-if="showOvarianCyst" :gutter="16">
          <el-col :span="24">
            <el-form-item label="卵巢囊肿">
              <el-radio-group v-model="imagingState.ovarianCyst.result" @change="syncImaging('ovarianCyst')">
                <el-radio value="0">未查</el-radio>
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 垂体MRI -->
        <template v-if="showPituitaryMri">
          <div class="sub-section-title">垂体MRI</div>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="垂体MRI">
                <el-radio-group v-model="imagingState.pituitaryMri.result" @change="syncImaging('pituitaryMri')">
                  <el-radio value="0">未查</el-radio>
                  <el-radio value="1">正常</el-radio>
                  <el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.pituitaryMri.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.pituitaryMri.description" type="textarea" :rows="3" placeholder="请输入垂体MRI异常描述" @input="syncImaging('pituitaryMri')" /></el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 骨密度 -->
        <el-row v-if="showBonMinDen" :gutter="16">
          <el-col :span="24">
            <el-form-item label="骨密度">
              <el-radio-group v-model="imagingState.bonMinDen.result" @change="syncImaging('bonMinDen')">
                <el-radio value="0">未查</el-radio>
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="imagingState.bonMinDen.result === '2'" :span="24">
            <el-form-item><el-input v-model="imagingState.bonMinDen.description" type="textarea" :rows="3" placeholder="请输入骨密度异常描述" @input="syncImaging('bonMinDen')" /></el-form-item>
          </el-col>
        </el-row>

        <!-- MAS 专项影像 -->
        <template v-if="isMas">
          <div class="section-sub-title">MAS 专项影像</div>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="肾上腺B超">
                <el-radio-group v-model="imagingState.adrenalUlt.result" @change="syncImaging('adrenalUlt')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.adrenalUlt.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.adrenalUlt.description" type="textarea" :rows="2" placeholder="请输入肾上腺B超异常描述" @input="syncImaging('adrenalUlt')" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="肾脏B超">
                <el-radio-group v-model="imagingState.renalUlt.result" @change="syncImaging('renalUlt')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.renalUlt.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.renalUlt.description" type="textarea" :rows="2" placeholder="请输入肾脏B超异常描述" @input="syncImaging('renalUlt')" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="骨骼X线">
                <el-radio-group v-model="imagingState.boneXRay.result" @change="syncImaging('boneXRay')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.boneXRay.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.boneXRay.description" type="textarea" :rows="2" placeholder="请输入骨骼X线异常描述" @input="syncImaging('boneXRay')" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="心脏B超">
                <el-radio-group v-model="imagingState.cardiacUlt.result" @change="syncImaging('cardiacUlt')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.cardiacUlt.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.cardiacUlt.description" type="textarea" :rows="2" placeholder="请输入心脏B超异常描述" @input="syncImaging('cardiacUlt')" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="MR部位">
                <el-select v-model="imagingState.mrPart.raw" placeholder="请选择" @change="syncImaging('mrPart')">
                  <el-option label="未查" value="" /><el-option label="头颅" value="头颅" />
                  <el-option label="脊柱" value="脊柱" /><el-option label="四肢" value="四肢" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="MR结果">
                <el-radio-group v-model="imagingState.mrResult.result" @change="syncImaging('mrResult')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.mrResult.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.mrResult.description" type="textarea" :rows="2" placeholder="请输入MR异常描述" @input="syncImaging('mrResult')" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="CT部位">
                <el-select v-model="imagingState.ctPart.raw" placeholder="请选择" @change="syncImaging('ctPart')">
                  <el-option label="未查" value="" /><el-option label="头颅" value="头颅" />
                  <el-option label="脊柱" value="脊柱" /><el-option label="四肢" value="四肢" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="CT结果">
                <el-radio-group v-model="imagingState.ctResult.result" @change="syncImaging('ctResult')">
                  <el-radio value="0">未查</el-radio><el-radio value="1">正常</el-radio><el-radio value="2">异常</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="imagingState.ctResult.result === '2'" :span="24">
              <el-form-item><el-input v-model="imagingState.ctResult.description" type="textarea" :rows="2" placeholder="请输入CT异常描述" @input="syncImaging('ctResult')" /></el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-card>

      <!-- 常规实验室检查 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>常规实验室检查</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="血常规">
              <el-radio-group v-model="bloodState.result" @change="syncRoutineState">
                <el-radio value="0">未查</el-radio>
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尿常规">
              <el-radio-group v-model="urineState.result" @change="syncRoutineState">
                <el-radio value="0">未查</el-radio>
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="bloodState.result === '2'" :gutter="16">
          <el-col :span="24">
            <el-form-item label="血常规异常"><el-input v-model="bloodState.description" type="textarea" :rows="2" placeholder="请输入血常规异常描述" @input="syncRoutineState" /></el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="urineState.result === '2'" :gutter="16">
          <el-col :span="24">
            <el-form-item label="尿常规异常"><el-input v-model="urineState.description" type="textarea" :rows="2" placeholder="请输入尿常规异常描述" @input="syncRoutineState" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="乙肝三系">
              <el-select v-model="formData.hepatitisB" placeholder="请选择">
                <el-option label="阴性" value="阴性" />
                <el-option label="HBSAb阳性" value="HBSAb阳性" />
                <el-option label="小三阳" value="小三阳" />
                <el-option label="大三阳" value="大三阳" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="肝肾糖电解质">
              <el-input v-model="formData.liverKidneyElectrolyte" type="textarea" :rows="3" placeholder="请输入肝肾糖电解质结果" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>诊疗方案</span></template>
        <TreatmentPlanForm v-model="treatmentPlanData" :disease-type="diseaseType"
                           :weight="Number(formData.wt) || 0" />
        <el-form-item label="其他" style="margin-top: 12px">
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
          <el-table :data="eyeRows" border stripe size="small" style="width:100%">
            <el-table-column label="项目" width="140">
              <template #default="{ row }">{{ row.label }}</template>
            </el-table-column>
            <el-table-column label="右眼">
              <template #default="{ row }">
                <template v-if="row.type === 'refraction'">
                  <el-radio-group v-model="eyeExam[row.rightHas]" size="small">
                    <el-radio value="0">无</el-radio>
                    <el-radio value="1">有</el-radio>
                  </el-radio-group>
                  <span v-if="eyeExam[row.rightHas] === '1'" style="margin-left:6px">
                    <el-input v-model="eyeExam[row.rightValue]" placeholder="度数" size="small" style="width:70px" /> D
                  </span>
                </template>
                <el-input v-else v-model="eyeExam[row.rightValue]" size="small" style="width:120px" />
              </template>
            </el-table-column>
            <el-table-column label="左眼">
              <template #default="{ row }">
                <template v-if="row.type === 'refraction'">
                  <el-radio-group v-model="eyeExam[row.leftHas]" size="small">
                    <el-radio value="0">无</el-radio>
                    <el-radio value="1">有</el-radio>
                  </el-radio-group>
                  <span v-if="eyeExam[row.leftHas] === '1'" style="margin-left:6px">
                    <el-input v-model="eyeExam[row.leftValue]" placeholder="度数" size="small" style="width:70px" /> D
                  </span>
                </template>
                <el-input v-else v-model="eyeExam[row.leftValue]" size="small" style="width:120px" />
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top:8px;color:#909399;font-size:12px">近视/远视/散光：选择「有」后填写度数(D)；眼轴长度单位为 mm</div>
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
import TreatmentPlanForm from '@/views/case/components/TreatmentPlanForm.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  patientId: { type: String, required: true },
  diseaseType: { type: String, default: '' },
  editData: { type: Object, default: null },
  birthTime: { type: String, default: '' },
  patientSex: { type: String, default: '' }
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
const showOvarianCyst = computed(() => ['cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))

// 编码/解码工具函数（与基线一致）
function parseField(raw) {
  if (!raw) return { result: '0', description: '' }
  const idx = raw.indexOf('|')
  if (idx === -1) return { result: '0', description: raw }
  return { result: raw.substring(0, idx) || '0', description: raw.substring(idx + 1) }
}
function encodeField(result, desc) {
  if (!result || result === '0') return '0|'
  if (result === '1') return '1|'
  return `2|${desc || ''}`
}

// 影像检查状态
function makeImagingState() {
  return {
    gonBUlt: { result: '0', description: '' },
    ovarianCyst: { result: '0', description: '' },
    pituitaryMri: { result: '0', description: '' },
    thyroidUlt: { result: '0', description: '' },
    bonMinDen: { result: '0', description: '' },
    adrenalUlt: { result: '0', description: '' },
    renalUlt: { result: '0', description: '' },
    boneXRay: { result: '0', description: '' },
    cardiacUlt: { result: '0', description: '' },
    mrPart: { raw: '' },
    mrResult: { result: '0', description: '' },
    ctPart: { raw: '' },
    ctResult: { result: '0', description: '' }
  }
}
const imagingState = reactive(makeImagingState())

// 性腺B超详情
const gonBUltDetail = reactive({
  uterusLength: '', uterusWidth: '', uterusHeight: '',
  cervixLength: '', endometriumThickness: '',
  leftOvaryLength: '', leftOvaryWidth: '', leftOvaryHeight: '',
  rightOvaryLength: '', rightOvaryWidth: '', rightOvaryHeight: '',
  maxFollicleDiameter: '',
  hasCyst: '0', cystSide: '', cystLength: '', cystWidth: '', cystHeight: '',
  rightTestisLength: '', rightTestisWidth: '', rightTestisHeight: '', rightTestisLongDiameter: '',
  leftTestisLength: '', leftTestisWidth: '', leftTestisHeight: '', leftTestisLongDiameter: ''
})

// 甲状腺B超详情
const thyroidUltDetail = reactive({
  leftResult: '1', leftNoduleGrade: '', leftSize: '', leftDiffuseLesion: '', leftOther: '',
  rightResult: '1', rightNoduleGrade: '', rightSize: '', rightDiffuseLesion: '', rightOther: ''
})

// 血常规/尿常规状态
const bloodState = reactive({ result: '0', description: '' })
const urineState = reactive({ result: '0', description: '' })

// sync 方法
function syncThyroidUltDetail() { formData.thyroidUlt = JSON.stringify({ ...thyroidUltDetail }) }
function syncGonBUltDetail() { formData.gonBUltDetail = JSON.stringify({ ...gonBUltDetail }) }
function syncImaging(key) {
  const s = imagingState[key]
  if (key === 'mrPart' || key === 'ctPart') {
    formData[key] = s.raw
  } else {
    formData[key] = encodeField(s.result, s.description)
  }
}
function syncRoutineState() {
  formData.bloodRoutine = encodeField(bloodState.result, bloodState.description)
  formData.urineRoutine = encodeField(urineState.result, urineState.description)
}

// 重置影像/实验室状态
function resetImagingLabState() {
  Object.assign(imagingState, makeImagingState())
  Object.assign(gonBUltDetail, {
    uterusLength: '', uterusWidth: '', uterusHeight: '',
    cervixLength: '', endometriumThickness: '',
    leftOvaryLength: '', leftOvaryWidth: '', leftOvaryHeight: '',
    rightOvaryLength: '', rightOvaryWidth: '', rightOvaryHeight: '',
    maxFollicleDiameter: '',
    hasCyst: '0', cystSide: '', cystLength: '', cystWidth: '', cystHeight: '',
    rightTestisLength: '', rightTestisWidth: '', rightTestisHeight: '', rightTestisLongDiameter: '',
    leftTestisLength: '', leftTestisWidth: '', leftTestisHeight: '', leftTestisLongDiameter: ''
  })
  Object.assign(thyroidUltDetail, {
    leftResult: '1', leftNoduleGrade: '', leftSize: '', leftDiffuseLesion: '', leftOther: '',
    rightResult: '1', rightNoduleGrade: '', rightSize: '', rightDiffuseLesion: '', rightOther: ''
  })
  Object.assign(bloodState, { result: '0', description: '' })
  Object.assign(urineState, { result: '0', description: '' })
}

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
  bloodRoutine: '', urineRoutine: '', hepatitisB: '', liverKidneyElectrolyte: '',
  // 影像
  gonBUlt: '', pituitaryMri: '', thyroidUlt: '', gonBUltDetail: '', bonMinDen: '', ovarianCyst: '',
  // MAS 专项影像
  adrenalUlt: '', renalUlt: '', boneXRay: '', cardiacUlt: '', mrPart: '', mrResult: '', ctPart: '', ctResult: '',
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
  myopiaR: '0', myopiaRD: '', myopiaL: '0', myopiaLD: '',
  hyperopiaR: '0', hyperopiaRD: '', hyperopiaL: '0', hyperopiaLD: '',
  astigmatismR: '0', astigmatismRD: '', astigmatismL: '0', astigmatismLD: '',
  nakedVisionR: '', nakedVisionL: '',
  correctedVisionR: '', correctedVisionL: '',
  axialLengthR: '', axialLengthL: ''
})

const eyeRows = computed(() => [
  { label: '近视', type: 'refraction', rightHas: 'myopiaR', rightValue: 'myopiaRD', leftHas: 'myopiaL', leftValue: 'myopiaLD' },
  { label: '远视', type: 'refraction', rightHas: 'hyperopiaR', rightValue: 'hyperopiaRD', leftHas: 'hyperopiaL', leftValue: 'hyperopiaLD' },
  { label: '散光', type: 'refraction', rightHas: 'astigmatismR', rightValue: 'astigmatismRD', leftHas: 'astigmatismL', leftValue: 'astigmatismLD' },
  { label: '裸眼视力', type: 'plain', rightValue: 'nakedVisionR', leftValue: 'nakedVisionL' },
  { label: '矫正视力', type: 'plain', rightValue: 'correctedVisionR', leftValue: 'correctedVisionL' },
  { label: '眼轴长度', type: 'plain', rightValue: 'axialLengthR', leftValue: 'axialLengthL' }
])

// 诊疗方案结构化数据
const treatmentPlanData = reactive({
  diaPlan: '1',
  rhGHType: '',
  rhGH: '',
  rhGHdose: '',
  rhGHdoseKG: '',
  PEGrhGHdose: '',
  PEGrhGHdoseKG: '',
  GnRHa: '',
  GnRHadose: '',
  planData: [],
  otherMedicine: '',
  rhCustomizationDiaPlan: '',
  rhCustomizationPrompt: '',
  PEGrhCustomizationPrompt: '',
  rhCustomizationPromptKG: '',
  PEGrhCustomizationPromptKG: '',
  laghOtherMedicine: '',
  laghOther: '',
  rhghOtherMedicine: '',
  rhghOther: ''
})

watch(treatmentPlanData, (val) => {
  formData.diaTreaPlan = JSON.stringify(val)
}, { deep: true })

// 随访年龄：根据随访日期和出生日期自动计算
const followUpAge = computed(() => {
  if (!formData.follTime || !props.birthTime) return ''
  const birth = new Date(props.birthTime)
  const visit = new Date(formData.follTime)
  if (isNaN(birth.getTime()) || isNaN(visit.getTime())) return ''
  let age = visit.getFullYear() - birth.getFullYear()
  const monthDiff = visit.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && visit.getDate() < birth.getDate())) age--
  return age >= 0 ? String(age) : ''
})

watch(followUpAge, (val) => { formData.age = val })

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
      // 解析诊疗方案 JSON
      if (props.editData.diaTreaPlan) {
        try {
          const parsed = JSON.parse(props.editData.diaTreaPlan)
          if (parsed && typeof parsed === 'object') Object.assign(treatmentPlanData, parsed)
        } catch { /* 旧纯文本格式，保持默认值 */ }
      }
      // 解析影像状态（编码格式 -> 结构化对象）
      const imagingKeys = ['gonBUlt', 'ovarianCyst', 'pituitaryMri', 'thyroidUlt', 'bonMinDen', 'adrenalUlt', 'renalUlt', 'boneXRay', 'cardiacUlt', 'mrResult', 'ctResult']
      imagingKeys.forEach(key => {
        if (props.editData[key] !== undefined && props.editData[key] !== null) {
          const parsed = parseField(props.editData[key])
          if (imagingState[key]) {
            imagingState[key].result = parsed.result
            imagingState[key].description = parsed.description
          }
        }
      })
      if (props.editData.mrPart !== undefined) imagingState.mrPart.raw = props.editData.mrPart || ''
      if (props.editData.ctPart !== undefined) imagingState.ctPart.raw = props.editData.ctPart || ''
      // 解析甲状腺B超详情
      if (props.editData.thyroidUlt) {
        try { Object.assign(thyroidUltDetail, JSON.parse(props.editData.thyroidUlt)) } catch {}
      }
      // 解析性腺B超详情
      if (props.editData.gonBUltDetail) {
        try { Object.assign(gonBUltDetail, JSON.parse(props.editData.gonBUltDetail)) } catch {}
      }
      // 解析血/尿常规
      if (props.editData.bloodRoutine) Object.assign(bloodState, parseField(props.editData.bloodRoutine))
      if (props.editData.urineRoutine) Object.assign(urineState, parseField(props.editData.urineRoutine))
      // livKidLip 映射到 liverKidneyElectrolyte
      formData.liverKidneyElectrolyte = props.editData.livKidLip || ''
    } else {
      resetImagingLabState()
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
    const payload = { ...formData }
    // 前端 liverKidneyElectrolyte 映射到后端 livKidLip
    delete payload.liverKidneyElectrolyte
    payload.livKidLip = formData.liverKidneyElectrolyte || ''
    if (isEdit.value) {
      await updateFollowUp(props.editData.id, payload)
    } else {
      await createFollowUp(payload)
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
  Object.keys(eyeExam).forEach(k => { eyeExam[k] = k === 'hasExam' ? '0' : '' })
  // 重置影像/实验室状态
  resetImagingLabState()
  // 重置诊疗方案
  Object.assign(treatmentPlanData, {
    diaPlan: '1', rhGHType: '', rhGH: '', rhGHdose: '', rhGHdoseKG: '',
    PEGrhGHdose: '', PEGrhGHdoseKG: '', GnRHa: '', GnRHadose: '',
    planData: [], otherMedicine: '',
    rhCustomizationDiaPlan: '', rhCustomizationPrompt: '',
    PEGrhCustomizationPrompt: '', rhCustomizationPromptKG: '', PEGrhCustomizationPromptKG: '',
    laghOtherMedicine: '', laghOther: '', rhghOtherMedicine: '', rhghOther: ''
  })
}
</script>

<style scoped>
.form-section {
  margin-bottom: 12px;
}

.sub-section-title {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  margin: 12px 0 8px 0;
  padding-left: 10px;
  border-left: 2px solid #67c23a;
}

.sub-item-title {
  font-size: 12px;
  font-weight: 500;
  color: #909399;
  margin: 8px 0 4px 0;
  padding-left: 6px;
}

.section-sub-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin: 16px 0 8px 0;
}
.form-section :deep(.el-card__header) {
  padding: 8px 16px;
  background: #fafafa;
  font-weight: 600;
  font-size: 14px;
}
</style>

<template>
  <div class="baseline-exam-form">
    <!-- 性激素及相关 -->
    <div class="section-title">性激素及相关</div>
    <el-row :gutter="16">
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="LH (mIU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.lh" :disabled="disabled" placeholder="LH" class="exam-val" />
            <el-date-picker v-model="localData.lhCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="FSH (mIU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.fsh" :disabled="disabled" placeholder="FSH" class="exam-val" />
            <el-date-picker v-model="localData.fshCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="E2 (pg/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.e2" :disabled="disabled" placeholder="E2" class="exam-val" />
            <el-date-picker v-model="localData.e2CheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="T (ng/dL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.t" :disabled="disabled" placeholder="T" class="exam-val" />
            <el-date-picker v-model="localData.tCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="PRL (ng/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.prl" :disabled="disabled" placeholder="PRL" class="exam-val" />
            <el-date-picker v-model="localData.prlCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="DHT (ng/dL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.dht" :disabled="disabled" placeholder="DHT" class="exam-val" />
            <el-date-picker v-model="localData.dhtCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="FT (ng/dL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.ft" :disabled="disabled" placeholder="FT" class="exam-val" />
            <el-date-picker v-model="localData.ftCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="SHBG (nmol/L)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.shbg" :disabled="disabled" placeholder="SHBG" class="exam-val" />
            <el-date-picker v-model="localData.shbgCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="AMH (ng/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.amh" :disabled="disabled" placeholder="AMH" class="exam-val" />
            <el-date-picker v-model="localData.amhCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="INHB (pg/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.inhb" :disabled="disabled" placeholder="INHB" class="exam-val" />
            <el-date-picker v-model="localData.inhbCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 生长因子与代谢 -->
    <div class="section-title">生长因子与代谢</div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-form-item label="IGF-1 (ng/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.igf1" :disabled="disabled" placeholder="IGF-1" class="exam-val" />
            <el-date-picker v-model="localData.igf1CheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="IGFBP-3 (ug/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.igfbp3" :disabled="disabled" placeholder="IGFBP-3" class="exam-val" />
            <el-date-picker v-model="localData.igfbp3CheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="空腹血糖 (mmol/L)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.fasBloodGlu" :disabled="disabled" placeholder="空腹血糖" class="exam-val" />
            <el-date-picker v-model="localData.fasBloodGluCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="空腹胰岛素 (uIU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.fasInsulin" :disabled="disabled" placeholder="空腹胰岛素" class="exam-val" />
            <el-date-picker v-model="localData.fasInsulinCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="糖化血红蛋白 (%)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.glyHem" :disabled="disabled" placeholder="糖化血红蛋白" class="exam-val" />
            <el-date-picker v-model="localData.glyHemCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="糖化血红蛋白A1c (%)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.glyHemA" :disabled="disabled" placeholder="糖化血红蛋白A1c" class="exam-val" />
            <el-date-picker v-model="localData.glyHemACheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 肾上腺激素 -->
    <div class="section-title" v-if="showAdrenal">肾上腺激素</div>
    <el-row v-if="showAdrenal" :gutter="16">
      <el-col :span="8">
        <el-form-item label="ACTH (8am) (pg/ml)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.acth" :disabled="disabled" placeholder="ACTH" class="exam-val" />
            <el-date-picker v-model="localData.acthCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="皮质醇 (8am) (ug/dl)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.cortisol" :disabled="disabled" placeholder="皮质醇" class="exam-val" />
            <el-date-picker v-model="localData.cortisolCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="isDsd || isEltm" :span="8">
        <el-form-item label="17-OHP (nmol/l)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.ohp" :disabled="disabled" placeholder="17-OHP" class="exam-val" />
            <el-date-picker v-model="localData.ohpCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="DHEA-S (ug/dL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.dheas" :disabled="disabled" placeholder="DHEA-S" class="exam-val" />
            <el-date-picker v-model="localData.dheasCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col v-if="isDsd" :span="8">
        <el-form-item label="雄烯二酮 (ng/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.androstenedione" :disabled="disabled" placeholder="雄烯二酮" class="exam-val" />
            <el-date-picker v-model="localData.androstenedioneCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <!-- 肿瘤标志物 -->
      <el-col :span="8">
        <el-form-item label="甲胎蛋白 (ng/ml)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.afp" :disabled="disabled" placeholder="甲胎蛋白" class="exam-val" />
            <el-date-picker v-model="localData.afpCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="癌胚抗原 (ng/ml)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.cea" :disabled="disabled" placeholder="癌胚抗原" class="exam-val" />
            <el-date-picker v-model="localData.ceaCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 激发试验 -->
    <div class="section-title" v-if="showProvocation">激发试验</div>
    <el-row v-if="showProvocation" :gutter="16">
      <!-- HCG 激发 (DSD) -->
      <template v-if="isDsd">
        <el-col :span="8">
          <el-form-item label="HCG 激发前 T" class="exam-with-date">
            <div class="exam-row">
              <el-input v-model="localData.hcg" :disabled="disabled" class="exam-val" />
              <el-date-picker v-model="localData.hcgCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 T" class="exam-with-date">
            <div class="exam-row">
              <el-input v-model="localData.hcgt" :disabled="disabled" class="exam-val" />
              <el-date-picker v-model="localData.hcgtCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 DHT" class="exam-with-date">
            <div class="exam-row">
              <el-input v-model="localData.hcgdht" :disabled="disabled" class="exam-val" />
              <el-date-picker v-model="localData.hcgdhtCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 AD" class="exam-with-date">
            <div class="exam-row">
              <el-input v-model="localData.hcgad" :disabled="disabled" class="exam-val" />
              <el-date-picker v-model="localData.hcgadCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
            </div>
          </el-form-item>
        </el-col>
      </template>
      <!-- GnRH 激发 (DSD, CPP) -->
      <el-col :span="8">
        <el-form-item label="GnRH 激发 LHmax" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.lhMax" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.lhMaxCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="GnRH 激发 FSHmax" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.fshMax" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.fshMaxCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <!-- GH 药物激发试验 -->
      <template v-if="showGhExcitation">
        <el-col :span="8">
          <el-form-item label="GH 药物激发试验 - GH 峰值 (ng/ml)" class="exam-with-date">
            <div class="exam-row">
              <el-input v-model="localData.gh" :disabled="disabled" class="exam-val" />
              <el-date-picker v-model="localData.ghCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
            </div>
          </el-form-item>
        </el-col>
      </template>
    </el-row>

    <!-- 甲状腺功能 -->
    <div class="section-title" v-if="showThyroid">甲状腺功能</div>
    <el-row v-if="showThyroid" :gutter="16">
      <el-col :span="8">
        <el-form-item label="TSH (uIU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.tsh" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.tshCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="FT3 (pg/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.ft3" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.ft3CheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="FT4 (ng/dL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.ft4" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.ft4CheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="TPOAb (IU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.tpoab" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.tpoabCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="TgAb (IU/mL)" class="exam-with-date">
          <div class="exam-row">
            <el-input v-model="localData.tgab" :disabled="disabled" class="exam-val" />
            <el-date-picker v-model="localData.tgabCheckDate" :disabled="disabled" type="date" value-format="YYYY-MM-DD" placeholder="日期" class="exam-dt" />
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 影像检查 -->
    <div class="section-title">影像检查</div>

    <!-- 性腺B超 -->
    <template v-if="showGonBUlt">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="性腺B超">
            <el-radio-group v-model="imagingState.gonBUlt.result" :disabled="disabled" @change="syncImagingToData('gonBUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="imagingState.gonBUlt.result === '2'" :gutter="16">
        <el-col :span="24">
          <el-form-item>
            <el-input v-model="imagingState.gonBUlt.description" type="textarea" :rows="3"
              :disabled="disabled" placeholder="请输入性腺B超异常描述" @input="syncImagingToData('gonBUlt')" />
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 卵巢囊肿 -->
    <el-row v-if="showOvarianCyst" :gutter="16">
      <el-col :span="24">
        <el-form-item label="卵巢囊肿">
          <el-radio-group v-model="imagingState.ovarianCyst.result" :disabled="disabled" @change="syncImagingToData('ovarianCyst')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 垂体MRI -->
    <el-row v-if="showPituitaryMri" :gutter="16">
      <el-col :span="24">
        <el-form-item label="垂体MRI">
          <el-radio-group v-model="imagingState.pituitaryMri.result" :disabled="disabled" @change="syncImagingToData('pituitaryMri')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.pituitaryMri.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.pituitaryMri.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入垂体MRI异常描述" @input="syncImagingToData('pituitaryMri')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 甲状腺B超 -->
    <template v-if="showThyroidUlt">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="甲状腺B超">
            <el-radio-group v-model="imagingState.thyroidUlt.result" :disabled="disabled" @change="syncImagingToData('thyroidUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="imagingState.thyroidUlt.result === '2'" :gutter="16">
        <el-col :span="24">
          <el-form-item>
            <el-input v-model="imagingState.thyroidUlt.description" type="textarea" :rows="3"
              :disabled="disabled" placeholder="请输入甲状腺B超异常描述" @input="syncImagingToData('thyroidUlt')" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- CPP 甲状腺B超左右侧细节（来自V1） -->
      <template v-if="isCpp && imagingState.thyroidUlt.result === '2'">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="左侧甲状腺">
              <el-radio-group v-model="thyroidUltDetail.leftResult" :disabled="disabled">
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="thyroidUltDetail.leftResult === '2'" :span="6">
            <el-form-item label="左侧结节分级">
              <el-select v-model="thyroidUltDetail.leftNoduleGrade" :disabled="disabled" placeholder="TI-RADS分级">
                <el-option label="TI-RADS 1" value="1" />
                <el-option label="TI-RADS 2" value="2" />
                <el-option label="TI-RADS 3" value="3" />
                <el-option label="TI-RADS 4a" value="4a" />
                <el-option label="TI-RADS 4b" value="4b" />
                <el-option label="TI-RADS 4c" value="4c" />
                <el-option label="TI-RADS 5" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="右侧甲状腺">
              <el-radio-group v-model="thyroidUltDetail.rightResult" :disabled="disabled">
                <el-radio value="1">正常</el-radio>
                <el-radio value="2">异常</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="thyroidUltDetail.rightResult === '2'" :span="6">
            <el-form-item label="右侧结节分级">
              <el-select v-model="thyroidUltDetail.rightNoduleGrade" :disabled="disabled" placeholder="TI-RADS分级">
                <el-option label="TI-RADS 1" value="1" />
                <el-option label="TI-RADS 2" value="2" />
                <el-option label="TI-RADS 3" value="3" />
                <el-option label="TI-RADS 4a" value="4a" />
                <el-option label="TI-RADS 4b" value="4b" />
                <el-option label="TI-RADS 4c" value="4c" />
                <el-option label="TI-RADS 5" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </template>

    <!-- 骨密度 -->
    <el-row v-if="showBonMinDen" :gutter="16">
      <el-col :span="24">
        <el-form-item label="骨密度">
          <el-radio-group v-model="imagingState.bonMinDen.result" :disabled="disabled" @change="syncImagingToData('bonMinDen')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.bonMinDen.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.bonMinDen.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入骨密度异常描述" @input="syncImagingToData('bonMinDen')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 性腺B超 -->
    <template v-if="showGonBUlt">
      <div class="section-title">性腺B超</div>

      <!-- 男性 -->
      <template v-if="patientSex === '1'">
        <el-row :gutter="16">
          <el-col :span="24"><div class="sub-section-title">睾丸大小</div></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="右侧长(cm)"><el-input v-model="gonBUltDetail.rightTestisLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="右侧宽(cm)"><el-input v-model="gonBUltDetail.rightTestisWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="右侧高(cm)"><el-input v-model="gonBUltDetail.rightTestisHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="右侧长径(cm)"><el-input v-model="gonBUltDetail.rightTestisLongDiameter" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="左侧长(cm)"><el-input v-model="gonBUltDetail.leftTestisLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="左侧宽(cm)"><el-input v-model="gonBUltDetail.leftTestisWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="左侧高(cm)"><el-input v-model="gonBUltDetail.leftTestisHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="左侧长径(cm)"><el-input v-model="gonBUltDetail.leftTestisLongDiameter" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>
      </template>

      <!-- 女性 -->
      <template v-if="patientSex === '2'">
        <el-row :gutter="16">
          <el-col :span="24"><div class="sub-section-title">子宫</div></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="子宫长(cm)"><el-input v-model="gonBUltDetail.uterusLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="子宫宽(cm)"><el-input v-model="gonBUltDetail.uterusWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="子宫高(cm)"><el-input v-model="gonBUltDetail.uterusHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="宫颈长(cm)"><el-input v-model="gonBUltDetail.cervixLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内膜厚度(cm)"><el-input v-model="gonBUltDetail.endometriumThickness" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="24"><div class="sub-section-title">卵巢</div></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="左卵巢长(cm)"><el-input v-model="gonBUltDetail.leftOvaryLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="左卵巢宽(cm)"><el-input v-model="gonBUltDetail.leftOvaryWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="左卵巢高(cm)"><el-input v-model="gonBUltDetail.leftOvaryHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="右卵巢长(cm)"><el-input v-model="gonBUltDetail.rightOvaryLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="右卵巢宽(cm)"><el-input v-model="gonBUltDetail.rightOvaryWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="右卵巢高(cm)"><el-input v-model="gonBUltDetail.rightOvaryHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="最大滤泡直径(cm)"><el-input v-model="gonBUltDetail.maxFollicleDiameter" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="有无囊肿">
              <el-radio-group v-model="gonBUltDetail.hasCyst" :disabled="disabled" @change="syncGonBUltDetail">
                <el-radio value="0">无</el-radio>
                <el-radio value="1">有</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="gonBUltDetail.hasCyst === '1'">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="囊肿侧"><el-input v-model="gonBUltDetail.cystSide" :disabled="disabled" placeholder="左/右" @input="syncGonBUltDetail" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="囊肿长(cm)"><el-input v-model="gonBUltDetail.cystLength" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="囊肿宽(cm)"><el-input v-model="gonBUltDetail.cystWidth" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="囊肿高(cm)"><el-input v-model="gonBUltDetail.cystHeight" :disabled="disabled" @input="syncGonBUltDetail" /></el-form-item>
            </el-col>
          </el-row>
        </template>
      </template>
    </template>

    <!-- 眼科检查 -->
    <div class="section-title">眼科检查</div>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="是否有做眼科检查">
          <el-radio-group v-model="eyeExam.hasExam" :disabled="disabled" @change="syncEyeExam">
            <el-radio value="0">无</el-radio>
            <el-radio value="1">有</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>
    <template v-if="eyeExam.hasExam === '1'">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="检查时间">
            <el-date-picker v-model="eyeExam.examDate" :disabled="disabled" type="date"
              value-format="YYYY-MM-DD" placeholder="请选择日期" @change="syncEyeExam" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="裸眼视力 右">
            <el-input v-model="eyeExam.nakedVisionRight" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="裸眼视力 左">
            <el-input v-model="eyeExam.nakedVisionLeft" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="矫正视力 右">
            <el-input v-model="eyeExam.correctedVisionRight" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="矫正视力 左">
            <el-input v-model="eyeExam.correctedVisionLeft" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="眼轴 右 (cm)">
            <el-input v-model="eyeExam.axialLengthRight" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="眼轴 左 (cm)">
            <el-input v-model="eyeExam.axialLengthLeft" :disabled="disabled" @input="syncEyeExam" />
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 常规实验室检查 -->
    <div class="section-title">常规实验室检查</div>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="血常规">
          <el-radio-group v-model="bloodState.result" :disabled="disabled" @change="syncRoutineState">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="尿常规">
          <el-radio-group v-model="urineState.result" :disabled="disabled" @change="syncRoutineState">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="bloodState.result === '2'" :gutter="16">
      <el-col :span="24">
        <el-form-item label="血常规异常">
          <el-input v-model="bloodState.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入血常规异常描述" @input="syncRoutineState" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="urineState.result === '2'" :gutter="16">
      <el-col :span="24">
        <el-form-item label="尿常规异常">
          <el-input v-model="urineState.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入尿常规异常描述" @input="syncRoutineState" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="乙肝三系">
          <el-select v-model="localData.hepatitisB" :disabled="disabled" placeholder="请选择">
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
          <el-input v-model="localData.liverKidneyElectrolyte" type="textarea" :rows="3" :disabled="disabled" placeholder="请输入肝肾糖电解质结果（支持OCR识别文本粘贴）" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 甲状腺功能评估 -->
    <div class="section-sub-title">甲状腺功能评估</div>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="甲状腺功能">
          <el-radio-group v-model="thyroidFunctionState.result" :disabled="disabled" @change="syncThyroidFunction">
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="thyroidFunctionState.result === '2'" :gutter="16">
      <el-col :span="24">
        <el-form-item label="异常描述">
          <el-input v-model="thyroidFunctionState.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入甲状腺功能异常具体情况" @input="syncThyroidFunction" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- ==================== MAS 专属影像 ==================== -->
    <template v-if="isMas">
      <div class="section-sub-title">MAS 专项影像</div>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="肾上腺B超">
            <el-radio-group v-model="imagingState.adrenalUlt.result" :disabled="disabled" @change="syncImagingToData('adrenalUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.adrenalUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.adrenalUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入肾上腺B超异常描述" @input="syncImagingToData('adrenalUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="肾脏B超">
            <el-radio-group v-model="imagingState.renalUlt.result" :disabled="disabled" @change="syncImagingToData('renalUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.renalUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.renalUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入肾脏B超异常描述" @input="syncImagingToData('renalUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="骨骼X线">
            <el-radio-group v-model="imagingState.boneXRay.result" :disabled="disabled" @change="syncImagingToData('boneXRay')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.boneXRay.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.boneXRay.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入骨骼X线异常描述" @input="syncImagingToData('boneXRay')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="心脏B超">
            <el-radio-group v-model="imagingState.cardiacUlt.result" :disabled="disabled" @change="syncImagingToData('cardiacUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.cardiacUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.cardiacUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入心脏B超异常描述" @input="syncImagingToData('cardiacUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="MR部位">
            <el-select v-model="imagingState.mrPart.raw" :disabled="disabled" placeholder="请选择" @change="syncImagingToData('mrPart')">
              <el-option label="未查" value="" />
              <el-option label="头颅" value="头颅" />
              <el-option label="脊柱" value="脊柱" />
              <el-option label="四肢" value="四肢" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="MR结果">
            <el-radio-group v-model="imagingState.mrResult.result" :disabled="disabled" @change="syncImagingToData('mrResult')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.mrResult.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.mrResult.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入MR异常描述" @input="syncImagingToData('mrResult')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="CT部位">
            <el-select v-model="imagingState.ctPart.raw" :disabled="disabled" placeholder="请选择" @change="syncImagingToData('ctPart')">
              <el-option label="未查" value="" />
              <el-option label="头颅" value="头颅" />
              <el-option label="脊柱" value="脊柱" />
              <el-option label="四肢" value="四肢" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="CT结果">
            <el-radio-group v-model="imagingState.ctResult.result" :disabled="disabled" @change="syncImagingToData('ctResult')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.ctResult.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.ctResult.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入CT异常描述" @input="syncImagingToData('ctResult')" /></el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 影像资料上传 -->
    <div class="section-title">影像资料上传</div>
    <FileUpload :patient-id="patientId" />
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import FileUpload from '@/views/case/components/FileUpload.vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  diseaseData: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false },
  diseaseType: { type: String, default: '' },
  patientId: { type: String, default: '' },
  patientSex: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'update:diseaseData'])

// 疾病类型判断
const isDsd = computed(() => props.diseaseType === 'dsd')
const isCpp = computed(() => props.diseaseType === 'cpp')
const isFss = computed(() => props.diseaseType === 'fss')
const isSga = computed(() => props.diseaseType === 'sga')
const isSss = computed(() => props.diseaseType === 'sss')
const isMas = computed(() => props.diseaseType === 'mas')
const isEltm = computed(() => props.diseaseType === 'eltm')

// 区块条件显示
const showBasicHormones = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showDsdHormones = computed(() => isDsd.value)
const showAdrenal = computed(() => ['dsd', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showProvocation = computed(() => ['dsd', 'cpp', 'eltm'].includes(props.diseaseType))
const showGhExcitation = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showThyroid = computed(() => ['mas', 'eltm'].includes(props.diseaseType))
const showGonBUlt = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showPituitaryMri = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showThyroidUlt = computed(() => ['cpp', 'fss', 'sga', 'sss', 'mas', 'eltm'].includes(props.diseaseType))
const showBonMinDen = computed(() => ['fss', 'sga', 'sss'].includes(props.diseaseType))
const showOvarianCyst = computed(() => ['cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))

// 编码/解码：合并存储 "状态|描述"
function parseField(raw) {
  if (!raw) return { result: '0', description: '' }
  const idx = raw.indexOf('|')
  if (idx === -1) return { result: '0', description: raw } // 兼容旧纯文本数据
  return {
    result: raw.substring(0, idx) || '0',
    description: raw.substring(idx + 1)
  }
}
function encodeField(result, desc) {
  if (!result || result === '0') return '0|'
  if (result === '1') return '1|'
  return `2|${desc || ''}`
}

// 影像检查状态（result + description 分离）
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

const gonBUltDetail = reactive({
  // 子宫（女性）
  uterusLength: '', uterusWidth: '', uterusHeight: '',
  cervixLength: '', endometriumThickness: '',
  // 卵巢（女性）
  leftOvaryLength: '', leftOvaryWidth: '', leftOvaryHeight: '',
  rightOvaryLength: '', rightOvaryWidth: '', rightOvaryHeight: '',
  maxFollicleDiameter: '',
  hasCyst: '0', cystSide: '', cystLength: '', cystWidth: '', cystHeight: '',
  // 睾丸（男性）
  rightTestisLength: '', rightTestisWidth: '', rightTestisHeight: '', rightTestisLongDiameter: '',
  leftTestisLength: '', leftTestisWidth: '', leftTestisHeight: '', leftTestisLongDiameter: ''
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

// 血常规/尿常规状态
const bloodState = reactive({ result: '0', description: '' })
const urineState = reactive({ result: '0', description: '' })

// 甲状腺功能评估状态
const thyroidFunctionState = reactive({ result: '1', description: '' })

function syncRoutineState() {
  localData.bloodRoutine = encodeField(bloodState.result, bloodState.description)
  localData.urineRoutine = encodeField(urineState.result, urineState.description)
}

function loadRoutineState() {
  const b = parseField(localData.bloodRoutine)
  bloodState.result = b.result
  bloodState.description = b.description
  const u = parseField(localData.urineRoutine)
  urineState.result = u.result
  urineState.description = u.description
}

function syncThyroidFunction() {
  localData.thyroidFunction = encodeField(thyroidFunctionState.result, thyroidFunctionState.description)
}

function loadThyroidFunctionState() {
  const t = parseField(localData.thyroidFunction)
  thyroidFunctionState.result = t.result || '1'
  thyroidFunctionState.description = t.description
}

// 甲状腺B超左右侧细节（CPP）
const thyroidUltDetail = reactive({
  leftResult: '1',
  leftNoduleGrade: '',
  rightResult: '1',
  rightNoduleGrade: ''
})

function syncGonBUltDetail() {
  localData.gonBUltDetail = JSON.stringify({ ...gonBUltDetail })
}

function parseGonBUltDetail(json) {
  if (!json) return
  try {
    const obj = JSON.parse(json)
    Object.keys(gonBUltDetail).forEach(k => {
      if (obj[k] !== undefined) gonBUltDetail[k] = obj[k]
    })
  } catch { /* ignore parse errors */ }
}

function syncEyeExam() {
  localData.eyeExam = JSON.stringify({ ...eyeExam })
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

// 从 localData（4个主表字段）加载到 imagingState
function loadImagingFromLocalData() {
  ;['gonBUlt', 'pituitaryMri', 'thyroidUlt', 'bonMinDen'].forEach(field => {
    const parsed = parseField(localData[field])
    imagingState[field].result = parsed.result
    imagingState[field].description = parsed.description
  })
}
// 从 diseaseData（MAS字段）加载到 imagingState
function loadImagingFromDiseaseData() {
  const masFields = ['adrenalUlt', 'renalUlt', 'boneXRay', 'cardiacUlt', 'mrResult', 'ctResult']
  masFields.forEach(field => {
    const raw = props.diseaseData[field]
    if (raw !== undefined && raw !== null) {
      const parsed = parseField(raw)
      imagingState[field].result = parsed.result
      imagingState[field].description = parsed.description
    }
  })
  // MR/CT 部位（plain text, no encoding）
  if (props.diseaseData.mrPart) imagingState.mrPart.raw = props.diseaseData.mrPart
  if (props.diseaseData.ctPart) imagingState.ctPart.raw = props.diseaseData.ctPart
}

// 同步影像状态到对应存储
function syncImagingToData(field) {
  // 4个主表字段 → localData → modelValue
  if (['gonBUlt', 'pituitaryMri', 'thyroidUlt', 'bonMinDen'].includes(field)) {
    localData[field] = encodeField(imagingState[field].result, imagingState[field].description)
    return
  }
  // MR/CT 部位
  if (field === 'mrPart' || field === 'ctPart') {
    emit('update:diseaseData', { ...props.diseaseData, [field]: imagingState[field].raw })
    return
  }
  // MAS 影像字段 → diseaseData
  const encoded = encodeField(imagingState[field].result, imagingState[field].description)
  emit('update:diseaseData', { ...props.diseaseData, [field]: encoded })
}

const localData = reactive({
  lh: '', lhCheckDate: '',
  fsh: '', fshCheckDate: '',
  e2: '', e2CheckDate: '',
  t: '', tCheckDate: '',
  prl: '', prlCheckDate: '',
  dht: '', dhtCheckDate: '',
  ft: '', ftCheckDate: '',
  shbg: '', shbgCheckDate: '',
  amh: '', amhCheckDate: '',
  inhb: '', inhbCheckDate: '',
  igf1: '', igf1CheckDate: '',
  igfbp3: '', igfbp3CheckDate: '',
  fasBloodGlu: '', fasBloodGluCheckDate: '',
  fasInsulin: '', fasInsulinCheckDate: '',
  glyHem: '', glyHemCheckDate: '', glyHemA: '', glyHemACheckDate: '',
  acth: '', acthCheckDate: '',
  cortisol: '', cortisolCheckDate: '',
  ohp: '', ohpCheckDate: '',
  dheas: '', dheasCheckDate: '',
  androstenedione: '', androstenedioneCheckDate: '',
  afp: '', afpCheckDate: '', cea: '', ceaCheckDate: '',
  hcg: '', hcgCheckDate: '',
  hcgt: '', hcgtCheckDate: '',
  hcgdht: '', hcgdhtCheckDate: '',
  hcgad: '', hcgadCheckDate: '',
  lhMax: '', lhMaxCheckDate: '',
  fshMax: '', fshMaxCheckDate: '',
  gh: '', ghCheckDate: '',
  tsh: '', tshCheckDate: '',
  ft3: '', ft3CheckDate: '',
  ft4: '', ft4CheckDate: '',
  tpoab: '', tpoabCheckDate: '',
  tgab: '', tgabCheckDate: '',
  gonBUlt: '', pituitaryMri: '', thyroidUlt: '', bonMinDen: '',
  gonBUltDetail: '',
  eyeExam: '',
  thyroidFunction: '',
  bloodRoutine: '0|', urineRoutine: '0|', hepatitisB: '', liverKidneyElectrolyte: ''
})

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.keys(localData).forEach(key => {
      if (val[key] !== undefined && val[key] !== null) {
        localData[key] = val[key]
      }
    })
    loadImagingFromLocalData()
    loadRoutineState()
    loadThyroidFunctionState()
    parseGonBUltDetail(val.gonBUltDetail)
    parseEyeExam(val.eyeExam)
  }
}, { immediate: true, deep: true })

// 监听 diseaseData 变化（MAS影像字段加载）
watch(() => props.diseaseData, (val) => {
  if (val && typeof val === 'object') {
    loadImagingFromDiseaseData()
  }
}, { immediate: true, deep: true })

watch(localData, () => {
  emit('update:modelValue', { ...localData })
}, { deep: true })
</script>

<style scoped>
.baseline-exam-form {
  padding: 0;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 12px 0;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.sub-section-title {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  margin: 12px 0 8px 0;
  padding-left: 10px;
  border-left: 2px solid #67c23a;
}

.exam-row {
  display: flex;
  gap: 6px;
  width: 100%;
}

.exam-val {
  flex: 1;
  min-width: 0;
}

.exam-dt {
  flex-shrink: 0;
  width: 140px;
}
</style>

<template>
  <div class="thyroid">
    <el-scrollbar class="scrollThy">
      <el-tabs :value="this.activeName">
        <el-tab-pane name="one" :style="{pointerEvents}">
          <span slot="label">临床资料</span>
          <p class="thyroid-title">体格检查：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p>
              现身高：<input v-model="ruleForm.Ht"  @change="getSDS" @blur="validateHeightFun" class="input-underLine"/>cm，
              现身高标准差：<input v-model="ruleForm.HSDS" class="input-underLine"/>SDS
            </p>
            <p>
              现体重：<input v-model="ruleForm.Wt" @change="getBMI" class="input-underLine"/>kg，
              BMI：<input v-model="ruleForm.WSDS" class="input-underLine"/>kg/m^2
            </p>
            <p v-if="sex === '2'">
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
            <p v-if="sex === '1'">
              外生殖器分期：
              <el-select size="small" v-model="ruleForm.exGenitalia" :disabled="isStatic">
                <el-option label="G1" value="1"></el-option>
                <el-option label="G2" value="2"></el-option>
                <el-option label="G3" value="3"></el-option>
                <el-option label="G4" value="4"></el-option>
                <el-option label="G5" value="5"></el-option>
              </el-select>
            </p>
            <p>
              阴毛分期：
              <el-select size="small" v-model="ruleForm.pubicHair" :disabled="isStatic">
                <el-option label="1" value="1"></el-option>
                <el-option label="2" value="2"></el-option>
                <el-option label="3" value="3"></el-option>
                <el-option label="4" value="4"></el-option>
                <el-option label="5" value="5"></el-option>
              </el-select>
            </p>
            <p>
              其他：
              <input v-model="ruleForm.bodyOther" class="input-underLine" style="width: 50%" align="left"/>
            </p>
          </div>
          <p class="thyroid-title">生殖器信息：</p>
          <div class="div-box">
            阴茎长：<input v-model="ruleForm.penileLength" class="input-underLine"/>cm，
            阴茎直径：<input v-model="ruleForm.penileDia" class="input-underLine"/>cm，
            睾丸容量：<input v-model="ruleForm.tesVolume" class="input-underLine"/>ml，
            Prader分期：<input v-model="ruleForm.prader" class="input-underLine"/><br>
            <br>尿道口位置：<select v-model="ruleForm.locaUreOri" class="edit-select">
            <option value="0">正常</option>
            <option value="1">冠状沟型</option>
            <option value="2">阴茎型</option>
            <option value="3">阴茎阴囊型</option>
            <option value="4">会阴型</option>
          </select>
            右睾丸位置：<select v-model="ruleForm.rigTesPos" class="edit-select">
            <option value="1">在阴唇</option>
            <option value="2">在腹股沟</option>
            <option value="3">在腹部</option>
            <option value="4">睾丸缺如</option>
            <option value="5">在阴囊</option>
          </select>
            左睾丸位置：<select v-model="ruleForm.lefTesPos" class="edit-select">
            <option value="1">在阴唇</option>
            <option value="2">在腹股沟</option>
            <option value="3">在腹部</option>
            <option value="4">睾丸缺如</option>
            <option value="5">在阴囊</option>
          </select>
          </div>

          <p class="thyroid-title">生殖器评估：</p>
          <div class="div-box">

            生殖器评估：<select v-model="ruleForm.genitals" class="edit-select" style="width: 25vw;">
            <option value="0">1级：正常男性化</option>
            <option value="1">2级：男性化轻度缺陷的男性表型，如孤立性尿道下裂</option>
            <option value="2">3级：男性化重度缺陷的男性表型，如小阴茎、会阴阴蒂尿道下裂、阴囊裂和/或隐睾</option>
            <option value="3">4级：严重生殖器模糊阴蒂样阴茎、阴唇阴蒂皱褶，单会阴口</option>
            <option value="4">5级：女性表型，后唇融合，阴蒂肥大</option>
            <option value="5">6/7级：女性表型（成年期有阴毛者为6级，成年期无阴毛者为7级）</option>
          </select><br>
            <img src="../../assets/img/genitals.png"/>

            <br>
            <p style="font-size: 1vw">女性外阴伴可触及性腺：</p>
            <div style="display: flex">
              <img src="../../assets/img/gen1.png" style="width: 50%"/>
              <img src="../../assets/img/gen2.png" style="width: 50%"/>
            </div>
            <br>
            <p style="font-size: 1vw">阴唇后融合（尿生殖窦）：</p>
            <div style="display: flex">
              <img src="../../assets/img/gen3.png"/>
            </div>
            <br>
            <p style="font-size: 1vw">阴蒂肥大：</p>
            <div style="display: flex">
              <img src="../../assets/img/gen4.png"/>
            </div>
            <br>
            <p style="font-size: 1vw">阴囊或会阴型尿道下裂：</p>
            <div style="display: flex">
              <img src="../../assets/img/gen5.png"/>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="two">
          <span slot="label">检验检查</span>
          <p class="thyroid-title">骨龄信息：</p>
          <div class="div-box" :style="{pointerEvents}">
            骨龄：<input v-model="ruleForm.boneAge" class="input-underLine"/>
            <br><br>
             检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.boneTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="骨龄上传时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
          </div>
          <div v-for="(item,index) in ImageList" :key="index">
            <p class="thyroid-title">{{ item.title }}：</p>
            <div style="width: 100%;display: flex">
                <ImageUpload
                  style="margin-left: 1vw"
                  v-for="(url, iIndex) in item.imageUrl"
                  :key="iIndex"
                  :caseId="queryId"
                  organ="bone"
                  :category="item.category"
                  :fileName="url"
                  @update:fileName="v =>upImage(v,item.imageUrl,item.title,iIndex,ImageList)"
                  :editable="!isStatic"
                  :imageNum="iIndex"
                >
                </ImageUpload>
              </div>

<!--            <ImageUpload-->
<!--                :caseId="queryId"-->
<!--                organ="bone"-->
<!--                :category="item.category"-->
<!--                :fileName="item.imageUrl"-->
<!--                @update:fileName="v =>upImage(v,item.imageUrl,item.title, ImageList)"-->
<!--                :editable="!isStatic"-->
<!--            >-->
<!--            </ImageUpload>-->
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
          <p class="thyroid-title">B超图像：</p>
          <div class="div-box" :style="{pointerEvents}">
            <p>图像说明：</p>
            <span v-if="sex === '2'">
              <p>子宫大小：<input v-model="ruleForm.uterusOne" class="input-underLine"/>*<input v-model="ruleForm.uterusTwo" class="input-underLine"/>*<input v-model="ruleForm.uterusThr" class="input-underLine"/>cm，内膜厚度：<input v-model="ruleForm.intima" class="input-underLine"/>cm</p>
              <p>左侧卵巢大小约：<input v-model="ruleForm.ovaLeftOne" class="input-underLine"/>*<input v-model="ruleForm.ovaLeftTwo" class="input-underLine"/>*<input v-model="ruleForm.ovaLeftThr" class="input-underLine"/>cm</p>
              <p>右侧卵巢大小约：<input v-model="ruleForm.ovaRightOne" class="input-underLine"/>*<input v-model="ruleForm.ovaRightTwo" class="input-underLine"/>*<input v-model="ruleForm.ovaRightThr" class="input-underLine"/>cm</p>
              <p>最大滤泡直径大小：<input v-model="ruleForm.follDiameter" class="input-underLine"/>cm</p>
            </span>
            <span v-if="sex === '1'">
              <p>右侧睾丸大小：<input v-model="ruleForm.testisLeftOne" class="input-underLine"/>cm×<input v-model="ruleForm.testisLeftTwo" class="input-underLine"/>cm×<input v-model="ruleForm.testisLeftThr" class="input-underLine"/>cm</p>
              <p>左侧睾丸大小：<input v-model="ruleForm.testisRightOne" class="input-underLine"/>cm×<input v-model="ruleForm.testisRightTwo" class="input-underLine"/>cm×<input v-model="ruleForm.testisRightThr" class="input-underLine"/>cm。</p>
            </span>

            <div style="display: inline-flex;margin-bottom: 1vh">
              <ImageUpload
                  style="margin-left: 1vh"
                  v-for="(url ,iIndex) in imageUrl"
                  :key="iIndex"
                  :caseId="queryId"
                  organ="bone"
                  category="B超图片"
                  :fileName="url"
                  @update:fileName="v =>upBscanImage(v,iIndex)"
                  :editable="!isStatic"
              >
              </ImageUpload>
            </div>
            <br><br>
            
             检查时间：
              <el-date-picker
                    size="small"
                    v-model="ruleForm.BUImageTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="B超图像上传时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
          </div>
          <p class="thyroid-title">生殖激素：</p>
          <div class="div-box" :style="{pointerEvents}">
            LH：<input v-model="ruleForm.LH" class="input-underLine"/>mIU/mL，
            FSH：<input v-model="ruleForm.FSH" class="input-underLine"/>mIU/mL，
             检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.LHFSHTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="LH以及FSH检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>，
            睾酮T：<input v-model="ruleForm.T" class="input-underLine"/>ng/dL, 
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.TTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="睾酮T检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker><br>
            <br>
            雌二醇E2：<input v-model="ruleForm.E2" class="input-underLine"/>pg/mL，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.E2Time"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="雌二醇E2检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>，
            DHT：<input v-model="ruleForm.DHT" class="input-underLine"/>ng/mL，
             检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.DHTTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="DHT检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker><br><br>
            游离睾酮：<input v-model="ruleForm.FT" class="input-underLine"/>ng/mL，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.FTTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="游离睾酮检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>，
            SHBG：<input v-model="ruleForm.SHBG" class="input-underLine"/>nmol/L，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.SHBGTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="SHBG检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
                <br><br>
            IGF-1：<input v-model="ruleForm.IGF1" class="input-underLine"/>ng/mL，
            IGFBP-3：<input v-model="ruleForm.IGFBP3" class="input-underLine"/>μg/mL
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
            <br>
          </div>
          <p class="thyroid-title">抗缪勒管激素（AMH）：</p>
          <el-input
              :style="{pointerEvents}"
              v-model="ruleForm.AMH"
              type="textarea"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :readonly="this.pointerEvents === 'none' ? true : false"
              maxlength="1500"
              show-word-limit
          ></el-input>
          <br><br>
          检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.AMHTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="抗缪勒管激素（AMH）检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
          <p class="thyroid-title">抑制素B（INHB）：</p>
          <el-input
              type="textarea"
              :style="{pointerEvents}"
              v-model="ruleForm.INHB"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :readonly="this.pointerEvents === 'none' ? true : false"
              maxlength="1500"
              show-word-limit
          ></el-input>

          <br><br>
          检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.INHBTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="抑制素B（INHB）检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
          <p class="thyroid-title">磁共振：</p>
          <el-input
              type="textarea"
              :style="{pointerEvents}"
              v-model="ruleForm.MRI"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :readonly="this.pointerEvents === 'none' ? true : false"
              maxlength="1500"
              show-word-limit
          ></el-input>

          <br><br>
          检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.MRITime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="磁共振检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
          <p class="thyroid-title">其他：</p>
          <el-input
              type="textarea"
              :style="{pointerEvents}"
              v-model="ruleForm.supOther"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :readonly="this.pointerEvents === 'none' ? true : false"
              maxlength="1500"
              show-word-limit
          ></el-input>

          <p class="thyroid-title">生化指标：</p>
          <div class="div-box" :style="{pointerEvents}">
            促肾上腺皮质激素（ACTH）：<input v-model="ruleForm.ACTH" class="input-underLine"/>pg/ml，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.ACTHTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="促肾上腺皮质激素（ACTH）检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>，
            皮质醇：<input v-model="ruleForm.Hyd" class="input-underLine"/>ug/dl，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.HydTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="皮质醇检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
                <br><br>
            17-OHP：<input v-model="ruleForm.OHP" class="input-underLine"/>nmol/l，
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
                ></el-date-picker>，
            硫酸脱氢表雄酮：<input v-model="ruleForm.DHEA" class="input-underLine"/>ug/dl，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.DHEATime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="硫酸脱氢表雄酮检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker><br><br>
            雄烯二酮：<input v-model="ruleForm.AD" class="input-underLine"/>ng/ml，
            检查时间：
            <el-date-picker
                    size="small"
                    v-model="ruleForm.ADTime"
                    type="date"
                    :clearable="false"
                    :picker-options="pickerOptions"
                    placeholder="雄烯二酮检查时间"
                    value-format="yyyy-MM-dd"
                    @change="getAge"
                ></el-date-picker>
            <br><img src="../../assets/img/danweihuansuan.png" style="width: 50%;margin-top: 1vh"/>
          </div>
        </el-tab-pane>

        <el-tab-pane name="three">
          <span slot="label">HCG激发试验</span>
          <div class="div-box">
            <p class="thyroid-title">HCG激发试验：</p>
            <div :style="{pointerEvents}"
                 style="width: 97%; margin: 1vh; padding: 1vh;border: 1px solid #ccc; border-radius: 0.2rem;">
              <el-radio class="elRadio" v-model="ruleForm.HCG" label="1">
                无
              </el-radio>
              <el-radio class="elRadio" v-model="ruleForm.HCG" label="2">
                标准HCG激发
              </el-radio>
              <el-radio class="elRadio" v-model="ruleForm.HCG" label="3">
                延长HCG激发
              </el-radio>
              <div v-show="this.ruleForm.HCG === '2'" style="padding-top: 1vh">
                标准HCG激发T：<input v-model="ruleForm.HCGT" class="input-underLine"/>ng/dL,
                标准HCG激发激发DHT：<input v-model="ruleForm.HCGDHT" class="input-underLine"/>ng/ml，
                标准HCG激发激发AD：<input v-model="ruleForm.HCGAD" class="input-underLine"/>ng/ml
              </div>
              <div v-show="this.ruleForm.HCG === '3'" style="padding-top: 1vh">
                延长HCG激发T：<input v-model="ruleForm.HCGT_ext" class="input-underLine"/>ng/dL，
                延长HCG激发激发DHT：<input v-model="ruleForm.HCGDHT_ext" class="input-underLine"/>ng/ml，
                延长HCG激发激发AD：<input v-model="ruleForm.HCGAD_ext" class="input-underLine"/>ng/ml
              </div>
            </div>
          </div>
          <div class="div-box">
            <p class="thyroid-title">LHRH激发试验：</p>
            <div :style="{pointerEvents}" style="width: 97%; margin: 1vh; padding: 1vh;border: 1px solid #ccc; border-radius: 0.2rem;">
              LH峰值：<input class="input-underLine" v-model="ruleForm.LHmax"/>mIU/ml
              FSH峰值：<input class="input-underLine" v-model="ruleForm.FSHmax" />mIU/ml
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="four">
          <span slot="label">遗传学检查</span>
          <div style="display: flex;width: 100%" >
            <p class="thyroid-title" style="margin: 0px">染色体核型：</p>
            <el-select allow-create filterable default-first-option :disabled="isStatic"
                       size="small" placeholder="（其他请自行输入）" v-model="ruleForm.speKar">
              <el-option label="46，XY" value="46，XY"></el-option>
              <el-option label="46，XX" value="46，XX"></el-option>
            </el-select>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh ">
            <p class="thyroid-title">上传染色体报告：</p>
            <div>
              <fileUpload ref="fileUpload"
                          v-if="this.chromImageUrl === ''"
                          :caseId="this.queryId"
                          organ="bone"
                          category="染色体报告"
                          @update:fileName="v =>upChromImage(v)"
              >
              </fileUpload>
              <p class="thyroid-title" v-else>
                <a style="text-decoration: none" href="#" @click="loadFile(chromImageUrl,'染色体报告')">下载报告</a>
                <a v-if="!this.isStatic" href="#" style="margin-left: 1vw" @click="resetImage(chromImageUrl,'染色体报告')">重新上传</a>
              </p>
            </div><br>
          </div>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh ">
            <p class="thyroid-title">上传基因检测报告：</p>
            <div>
              <fileUpload ref="fileUpload"
                          v-if="this.genImageUrl === ''"
                          :caseId="this.queryId"
                          organ="bone"
                          category="基因检测报告"
                          @update:fileName="v =>upGenImage(v)"
              >
              </fileUpload>
              <p class="thyroid-title" v-else>
                <a style="text-decoration: none" href="#" @click="loadFile(genImageUrl,'基因检测报告')">下载报告</a>
                <a v-if="!this.isStatic" href="#" style="margin-left: 1vw" @click="resetImage(genImageUrl,'基因检测报告')">重新上传</a>
              </p>
            </div><br>
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
              <el-table-column label="父亲" width="120">
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

          <div style="display: flex;width: 100%;margin-top: 1vw;margin-bottom: 1vw">
            <p class="thyroid-title" style="margin: 0px">生物样本库：</p>
            <el-select size="small" v-model="ruleForm.biolog" :disabled="isStatic">
              <el-option label="有" value="有"></el-option>
              <el-option label="无" value="无"></el-option>
            </el-select>
          </div>
          <div class="div-box" v-show="this.ruleForm.biolog === '有'">
            <el-table
                border
                :data="biologBank"
                ref="table"
            >
              <el-table-column label="样本编号" >
                <template slot-scope="scope">
                  <input v-model="scope.row.id" class="input-underLine"/>
                </template>
              </el-table-column>
              <el-table-column label="样本类型" >
                <template slot-scope="scope">
                  <select v-model="scope.row.name">
                    <option value="1">DNA样本</option>
                    <option value="2">血清</option>
                    <option value="3">血浆</option>
                    <option value="4">尿液</option>
                  </select>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" >
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delRow(scope.$index,biologBank)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                             size="mini" @click="addRow(biologBank)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <p class="thyroid-title">手术情况：</p>
          <el-input
              type="textarea"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :style="{pointerEvents}"
              maxlength="1500"
              show-word-limit
              v-model="ruleForm.operation"
          ></el-input>
          <p class="thyroid-title">病理结果：</p>
          <el-input
              type="textarea"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :style="{pointerEvents}"
              maxlength="1500"
              show-word-limit
              v-model="ruleForm.patRes"
          ></el-input>

          <div style="display: flex;width: 100%;margin-top: 2vh;margin-bottom: 2vh ">
            <p class="thyroid-title">上传病理报告：</p>
            <div>
              <fileUpload ref="fileUpload"
                          v-if="this.patImageUrl === ''"
                          :caseId="this.queryId"
                          organ="bone"
                          category="病理报告"
                          @update:fileName="v =>upPatImage(v)"
              >
              </fileUpload>
              <p class="thyroid-title" v-else>
                <a style="text-decoration: none" href="#" @click="loadFile(patImageUrl,'病理报告')">下载报告</a>
                <a v-if="!this.isStatic" href="#" style="margin-left: 1vw" @click="resetImage(patImageUrl,'病理报告')">重新上传</a>
              </p>
            </div>
          </div>

          <p class="thyroid-title">处理意见：</p>
          <el-input
              type="textarea"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%"
              :style="{pointerEvents}"
              maxlength="1500"
              show-word-limit
              v-model="ruleForm.hanOpi"
          ></el-input>
          <p class="thyroid-title">其他：</p>
          <el-input
              type="textarea"
              :autosize="{ minRows: 3}"
              resize="none"
              placeholder="请输入内容"
              style="width: 97%;margin-bottom: 1vh"
              :style="{pointerEvents}"
              maxlength="1500"
              show-word-limit
              v-model="ruleForm.other"
          ></el-input>
        </el-tab-pane>

        <el-tab-pane name="five">
          <span slot="label">诊断</span>
          <p class="thyroid-title">诊断：</p>
          <el-cascader
              :disabled="isStatic"
              style="width: 50%"
              v-model="ruleForm.diagnosis"
              :options="options"
              @change="handleChange">
          </el-cascader>
        </el-tab-pane>

        <el-tab-pane name="seven">
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
        width="50%"
    >
      <ShortFollow
          :sex="sex"
          :birthTime="birthTime"
          :queryId="queryId"
          :queryPId="queryPId"
          :stuts="stuts"
          disClass="dsd"
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
  import request from "../../script/request";
  import {validateHeightByBirthdate } from '../../utils/heightValidator.js'
  import ImageUpload from "../imageViewer/ImageUpload";
  import ShortFollow from "../common/shortFollow"
  import image from "../../script/otherImage"
  import fileUpload from "../imageViewer/fileUpload";

  const ImageString = JSON.stringify(image)

  export default {
    name: "DSD",
    components: {ImageUpload,ShortFollow,fileUpload},
    props: {
      disClass: String,
      queryId: String,
      sex: String,
      caseNum: String,
      birthTime: String,
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
      if(this.default === "follow"){
        this.activeName = "seven"
      }
      if(this.$route.query.follow=== "follow"){
          this.activeName = "seven"
        }
    },
    data() {
      return {
        ImageList: JSON.parse(ImageString),
        unit: [],
        pointerEvents: "",
        boneAgeImageUrl: '',
        imageUrl: ['', '', ''],
        chromImageUrl: '',
        genImageUrl: '',
        patImageUrl: '',
        activeName: 'one',
        dia: [],
        options: [{
          value: 'A',
          label: '性染色体异常',
          children: [{
            value: 'A01',
            label: '45，X(turner综合征及其变体)',
          }, {
            value: 'A02',
            label: '47，XXY(Klinefelter综合征及其变体)',
          }, {
            value: 'A03',
            label: '45，X/46，XY[混合性性腺发育不良(MGD),卵睾DSD]',
          }, {
            value: 'A04',
            label: '46，XX/46，XY(嵌合体,卵睾DSD)',
          },]
        }, {
          value: 'B',
          label: '46，XY',
          children: [{
            value: 'B01',
            label: '性腺（睾丸）发育不良',
            children: [{
              value: 'B01A',
              label: '完全性腺发育不良（swyer综合征）',
            }, {
              value: 'B01B',
              label: '部分性腺发育不良睾丸',
            }, {
              value: 'B01C',
              label: '退化综合征',
            }, {
              value: 'B01D',
              label: '卵睾DSD',
            }]
          }, {
            value: 'B02',
            label: '雄激素合成或作用障碍',
            children: [{
              value: 'B02A',
              label: '雄激素合成障碍（5a-还原酶缺乏，17-羟基类固醇脱氢酶缺乏）',
            }, {
              value: 'B02B',
              label: '雄激素作用障碍（完全性雄激素不敏感综合征，部分性雄激素不敏感综合征）',
            }, {
              value: 'B02C',
              label: 'LH受体缺乏（间质细胞萎缩）',
            }, {
              value: 'B02D',
              label: 'AMH的缺乏及AMH受体障碍（持续性副中肾管综合征）',
            }]
          }, {
            value: 'B03',
            label: '其他',
            children: [{
              value: 'B03A',
              label: '严重的尿道下裂',
            }, {
              value: 'B03B',
              label: '泄殖腔外翻',
            }]
          },]
        }, {
          value: 'C',
          label: '46，XX',
          children: [{
            value: 'C01',
            label: '性腺（卵巢）发育不良',
            children: [{
              value: 'C01A',
              label: '性腺发育不良',
            }, {
              value: 'C01B',
              label: '卵睾DSD',
            }, {
              value: 'C01C',
              label: '睾丸性DSD',
            }]
          }, {
            value: 'C02',
            label: '雄激素过多',
            children: [{
              value: 'C02A',
              label: '胎儿源性（21-羟化酶缺乏，11-羟化酶缺乏）',
            }, {
              value: 'C02B',
              label: '胎盘源性（芳香化酶缺乏）',
            }, {
              value: 'C02C',
              label: '母体源（黄体瘤，孕期服用雄激素）',
            }]
          }, {
            value: 'C03',
            label: '其他',
            children: [{
              value: 'C03A',
              label: '阴道闭锁',
            }, {
              value: 'C03B',
              label: '泄殖腔外翻',
            }, {
              value: 'C03C',
              label: 'MURCS等',
            }]
          },]
        }],
        familyData: [{relation: '父亲', tAge:'', height: '', weight: '', age: '', health: '', disName: ''}],
        genData:[
          {genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:'', father: '', mother: ''},
        ],
        biologBank: [
          {id:'', name:''}
        ],

        ruleForm: {
          Ht: '',
          HSDS: '',
          Wt: "",
          WSDS: '',
          penileLength: '',
          penileDia: '',
          tesVolume: '',
          prader: '',
          locaUreOri: '',
          rigTesPos: '',
          lefTesPos: '',
          diagnosis: [],
          breastDev: '',
          breastDevRight:'',
          exGenitalia:'',
          pubicHair:'',
          bodyOther:'',
          boneAge: '',
          LH: '',
          FSH: '',
          T: '',
          E2: '',
          DHT: '',
          FT: '',
          SHBG: '',
          IGF1: '',
          IGFBP3: '',
          AMH: '',
          INHB: '',
          MRI:'',
          supOther:'',
          HCG: '',
          HCGT: '',
          HCGDHT: '',
          HCGAD: '',
          HCGT_ext: '',
          HCGDHT_ext: '',
          HCGAD_ext: '',
          LHmax:'',
          FSHmax:'',
          speKar: '',
          SRY: '',
          mutKind: '',
          sourMut: '',
          genMutName: '',
          baseMut: '',
          amiAciMut: '',
          ACTH: '',
          Hyd: '',
          OHP: '',
          DHEA: '',
          AD: '',
          uterusOne:"",
          uterusTwo:"",
          uterusThr:"",
          intima:"",
          ovaLeftOne:"",
          ovaLeftTwo:"",
          ovaLeftThr:"",
          ovaRightOne:"",
          ovaRightTwo:"",
          ovaRightThr:"",
          testisLeftOne:"",
          testisLeftTwo:"",
          testisLeftThr:"",
          testisRightOne:"",
          testisRightTwo:"",
          testisRightThr:"",
          bscanExplain: '',
          genitals: '',
          other: '',
          operation: '',
          patRes: '',
          hanOpi: '',
          biolog: '',
          biologBank: [],
          genData:[],
        },
        cleared: false,
        queryPId:"",
        dialogVisible: false,
        stuts: "",
        cases: [],
        total:0,
        currPage: 1,
        pageSize: 10,
        filters:{currPage:1,limit:10,queryId:this.queryId},
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
          },
        },
      }
    },
    activated() {
      this.getUserInfo();
      this.boneAgeImageUrl = '';
      this.imageUrl = ['', '', ''];
      this.chromImageUrl = '';
      this.genImageUrl = '';
      this.patImageUrl = '';
      if (this.$route.query.queryId) {
        // this.queryId = this.$route.query.queryId;
        this.ruleForm.queryId = this.$route.query.queryId;
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
      backHistory() {
        this.$router.push({name: 'home'})
      },

      handleChange(value) {
        this.ruleForm.diagnosis = value
        // console.log(this.ruleForm.diagnosis);
      },

      addGenRow(tableData){
        tableData.push({genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:'', father: '', mother: ''})
      },
      delGenRow(index, rows){
        rows.splice(index, 1);
      },

      addRow(tableData){
        tableData.push({id: '', name: ''})
      },
      delRow(index, rows){
        rows.splice(index, 1);
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
      getFollow(filters = null){
        filters.currPage = this.currPage;
        filters.limit = this.pageSize;
        filters.queryId = this.queryId;
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
                data: ['身高', '体重']
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
                }
              ]
            }
          }else {
            option = {
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                type: 'plain',
                data: ['身高', '体重']
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
                }
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
          this.$refs.shortFollow.getDetail();
        }, 100)
      },

      del(row){
        const queryPId = row.id;
        request.delFollow({queryPId},() =>{
          this.getFollow(this.filters);
          this.allFollow();
          this.$message({
            message: '删除成功',
            type: 'success'
          });
        }, error => {
          this.$message('删除失败');
          console.log(error)
        });
      },

      getBMI() {
        if (this.ruleForm.Ht && this.ruleForm.Wt) {
          const h = this.ruleForm.Ht / 100;
          this.ruleForm.WSDS = (this.ruleForm.Wt / (h * h)).toFixed(1)
        } else {
          this.ruleForm.WSDS = "";
        }
      },

      getSDS(){
        const sex = this.sex;
        const height = this.ruleForm.Ht;
        // const bTime = new Date(this.birthTime.replace(/-/g, "/"));
        let tempData = new Date();
        let y = tempData.getFullYear();
        let m = tempData.getMonth() + 1;
        let d = tempData.getDate();
        let strDate1 = this.birthTime + "   00:00:00.0";
        let strDate2 = y+"-"+ m + "-" + d + "-" + "   00:00:00.0";
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
        const from = {sex,height,age}
        request.getSDS(from,data=>{
          this.ruleForm.HSDS = data['SD'].toFixed(2)
        })
      },

      upImage(v, arr, title, iIndex, imageArr) {
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

      resetImage(url,category){
        const data = {
          queryId: this.queryId,
          organ: "bone",
          path:  category +"-"+ url ,
        }
        request.deleteImage(data, () => {
          // console.log(data)
          if(category === "染色体报告"){
            this.chromImageUrl = '';
          }else if(category === "基因检测报告"){
            this.genImageUrl = ''
          }else if(category === "病理报告"){
            this.patImageUrl = ''
          }
        });
      },

      upBscanImage(v, iIndex) {
        this.imageUrl[iIndex] = v
      },

      upChromImage(v) {
        this.chromImageUrl = v;
      },

      upGenImage(v) {
        this.genImageUrl = v;
      },

      upPatImage(v) {
        this.patImageUrl = v;
      },

      getUserInfo() {
        const queryUId = this.$store.state.user.id
        request.userInfo({queryUId}, data => {
          this.ruleForm.jianchayiyuan = data.unitName
        }, error => {
          console.log(error);
        })
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
        const queryId = this.ruleForm.queryId;
        // this.queryId = queryId;
        request.getCase({queryId}, data => {
          // this.queryPId = data.id;
          this.genData = data.gen_mut_name ? JSON.parse(data.gen_mut_name.replace(/'/g, "\"")) : [{genName: '', Rna: '',mutationType:'',other:'', infestansLevel:'', amino:'', father: '', mother: ''}];
          this.biologBank = data.biolog_bank ? JSON.parse(data.biolog_bank.replace(/'/g, "\"")) : [{id: '', name: ''}];
          this.ruleForm.case_num = data.case_num;
          this.ruleForm.Ht = data.Ht;
          this.ruleForm.HSDS = data.HSDS;
          this.ruleForm.Wt = data.Wt;
          this.ruleForm.WSDS = data.WSDS;
          this.ruleForm.penileLength = data.penile_length;
          this.ruleForm.penileDia = data.penile_dia;
          this.ruleForm.tesVolume = data.tes_volume;
          this.ruleForm.prader = data.prader;
          this.ruleForm.locaUreOri = data.loca_ure_ori;
          this.ruleForm.rigTesPos = data.rig_tes_pos;
          this.ruleForm.lefTesPos = data.lef_tes_pos;
          if(data.diagnosis){
            let diagnosis = data.diagnosis.replace("[", "").replace("]", "").replaceAll(/'/g, "").replace(/ /g, '');
            this.ruleForm.diagnosis = diagnosis.split(',');
          }
          let breast_dev=data.breast_dev ? JSON.parse(data.breast_dev) : {};
          // console.log(breast_dev);
          this.ruleForm.breastDev = breast_dev['breastDev'];
          this.ruleForm.breastDevRight = breast_dev['breastDevRight'];
          this.ruleForm.exGenitalia = data.ex_genitalia;
          this.ruleForm.pubicHair = data.pubic_hair;
          this.ruleForm.bodyOther = data.body_other;
          this.ruleForm.genitals = data.genitals;
          this.ruleForm.boneAge = data.bone_age;
          this.ruleForm.LH = data.LH;
          this.ruleForm.FSH = data.FSH;
          this.ruleForm.T = data.T;
          this.ruleForm.E2 = data.E2;
          this.ruleForm.DHT = data.DHT;
          this.ruleForm.FT = data.FT;
          this.ruleForm.SHBG = data.SHBG;
          this.ruleForm.IGF1 = data.IGF1;
          this.ruleForm.IGFBP3 = data.IGFBP3;
          this.ruleForm.AMH = data.AMH;
          this.ruleForm.INHB = data.INHB;
          this.ruleForm.MRI = data.MRI;
          this.ruleForm.supOther = data.sup_other;
          this.ruleForm.ACTH = data.ACTH;
          this.ruleForm.Hyd = data.Hyd;
          this.ruleForm.OHP = data.OHP;
          this.ruleForm.DHEA = data.DHEAS;
          this.ruleForm.AD = data.AD;
          this.ruleForm.bscanExplain = data.bscanExplain;
          this.ruleForm.uterusOne = data.uterusOne;
          this.ruleForm.uterusTwo = data.uterusTwo;
          this.ruleForm.uterusThr = data.uterusThr;
          this.ruleForm.intima = data.intima;
          let tempEx = data.bscanExplain ? JSON.parse(data.bscanExplain) : {};
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
          this.ruleForm.HCG = data.HCG;
          this.ruleForm.HCGT = data.HCGT;
          this.ruleForm.HCGDHT = data.HCGDHT;
          this.ruleForm.HCGAD = data.HCGAD;
          this.ruleForm.HCGT_ext = data.HCGT_ext;
          this.ruleForm.HCGDHT_ext = data.HCGDHT_ext;
          this.ruleForm.HCGAD_ext = data.HCGAD_ext;
          this.ruleForm.LHmax = data.LHmax;
          this.ruleForm.FSHmax = data.FSHmax;
          this.ruleForm.speKar = data.spe_kar;
          this.ruleForm.SRY = data.SRY;
          this.ruleForm.mutKind = data.mut_kind;
          this.ruleForm.sourMut = data.sour_mut;
          this.ruleForm.genMutName = data.gen_mut_name;
          this.ruleForm.baseMut = data.base_mut;
          this.ruleForm.amiAciMut = data.ami_aci_mut;
          this.ruleForm.other = data.other;
          this.ruleForm.operation = data.operation;
          this.ruleForm.patRes = data.pat_res;
          this.ruleForm.hanOpi = data.han_opi;
          this.ruleForm.biolog = data.biolog;
          // this.ruleForm.biologBank = data.biolog_bank;
          let tempData = data.B_ult_image ? JSON.parse(data.B_ult_image) : {};
          this.boneAgeImagePath(tempData);
          this.ImagePath(tempData['B超图片']);
          this.chromeImagePath(tempData['染色体报告']);
          this.genImagePath(tempData['基因检测报告']);
          this.patImagePath(tempData['病理报告']);
          this.getFollow(this.filters);
          this.allFollow();
        }, error => {
          console.log(error)
        })
      },

      getData() {
        let data = null;
        this.ruleForm.genData = this.genData;
        this.ruleForm.biologBank = this.biologBank;
        data = this.ruleForm;
        return data;
      },

      boneAgeImagePath(imagePath) {
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
      ImagePath(imagePath) {
        if (imagePath) {
          imagePath.forEach((item, i) => {
            this.imageUrl[i] = item
          })
        }
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
      patImagePath(imagePath) {
        if (imagePath && imagePath[0]) {
          this.patImageUrl = imagePath[0];
        } else {
          this.patImageUrl = '';
        }
      },

      loadFile(url,category){
        this.$message({
          message: '正在请求文件……',
          type: 'info'
        });
        const caseId =this.queryId;
        const organ = "bone";
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
          this.boneAgeImagePath(tempData);
          this.ImagePath(tempData['B超图片']);
          this.chromeImagePath(tempData['染色体报告']);
          this.genImagePath(tempData['基因检测报告']);
          this.patImagePath(tempData['病理报告']);
          this.getFollow(this.filters);
          this.allFollow();
        }).catch(() => {

        });
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
        },
      // 现身高提示
      validateHeightFun(){
        validateHeightByBirthdate(this.birthTime,this.ruleForm.Ht)
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
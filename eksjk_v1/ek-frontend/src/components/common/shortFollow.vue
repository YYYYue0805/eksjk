<template>
  <div>
    <div style="width: 100%" align="center">
      <p v-if="stuts === 'add'">添加随访记录</p>
      <p v-if="stuts === 'select'">查看随访记录</p>
      <p v-if="stuts === 'update'">修改随访记录</p>
    </div>
    <el-form ref="followForm" :model="followForm" label-width="10vw" v-if="disClass !== 'eltm'" >
      <el-form-item label="随访日期：" prop="comCount">
        <el-date-picker
            :style="{pointerEvents}"
            v-model="followForm.followTime"
            type="date"
            :clearable="false"
            :picker-options="pickerOptions"
            size="small"
            @change="getAge"
            placeholder="请选择日期"
            value-format="yyyy-MM-dd"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="年龄：" prop="age">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" v-model="followForm.age" readonly></el-input>
      </el-form-item>
      <el-form-item label="身高：" prop="Ht">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}"  @blur="validateHeightFun" placeholder="请输入身高" v-model="followForm.Ht"></el-input>cm
      </el-form-item>
      <el-form-item label="体重：" prop="Wt">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" @blur="validateWeightFun" placeholder="请输入体重" v-model="followForm.Wt"></el-input>kg
      </el-form-item>
      <el-form-item label="是否行为发育评估:" prop="isFYassess"  v-if="disClass === 'sga'" >
        <el-select size="small" v-model="followForm.isFYassess" :disabled="isStatic">
          <el-option label="否" value="0"></el-option>
          <el-option label="是" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item style="width: 380px;" label="Peabody运动发育评估:" prop="Peabody" v-if="disClass === 'sga'">
        <input v-model="followForm.Peabody" class="input-underLine"/>（百分位表示）
      </el-form-item>
      <el-form-item label=" Griffiths心理发育评估:" v-if="disClass === 'sga'">
        A 粗大运动<input v-model="followForm.GriffithsA" class="input-underLine"/>（百分位表示）,<br>
        B 个人社会<input v-model="followForm.GriffithsB" class="input-underLine"/>（百分位表示）,<br>
        C 听力语言<input v-model="followForm.GriffithsC" class="input-underLine"/>（百分位表示）,<br>
        D 手眼协调<input v-model="followForm.GriffithsD" class="input-underLine"/>（百分位表示）,<br>
        E 视觉表现<input v-model="followForm.GriffithsE" class="input-underLine"/>（百分位表示）,<br>
        F 实际推理<input v-model="followForm.GriffithsF" class="input-underLine"/>（百分位表示）
      </el-form-item>
      <el-form-item label=" 韦氏智力量表:" prop="wszlb" v-if="disClass === 'sga'">
        <input v-model="followForm.wszlb" class="input-underLine"/>（百分位表示）
      </el-form-item>
      <el-form-item label="双乳发育分期：" prop="genStag" v-if="sex === '2'">
        <el-select size="small" v-model="followForm.genStag" :disabled="isStatic">
          <el-option label="B1" value="1"></el-option>
          <el-option label="B2" value="2"></el-option>
          <el-option label="B3" value="3"></el-option>
          <el-option label="B4" value="4"></el-option>
          <el-option label="B5" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="外生殖器分期：" prop="genStag" v-if="sex === '1'">
        <el-select size="small" v-model="followForm.genStag" :disabled="isStatic">
          <el-option label="G1" value="1"></el-option>
          <el-option label="G2" value="2"></el-option>
          <el-option label="G3" value="3"></el-option>
          <el-option label="G4" value="4"></el-option>
          <el-option label="G5" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="阴毛分期：" prop="pubStag">
        <el-select size="small" v-model="followForm.pubStag" :disabled="isStatic">
          <el-option label="1" value="1"></el-option>
          <el-option label="2" value="2"></el-option>
          <el-option label="3" value="3"></el-option>
          <el-option label="4" value="4"></el-option>
          <el-option label="5" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="IGF-1：" v-if="disClass === 'sga'" prop="IGF1">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}"  placeholder="请输入" v-model="followForm.IGF1"></el-input>ng/ml
      </el-form-item>
      <el-form-item label="IGFBP3：" v-if="disClass === 'sga'" prop="IGFBP3">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}"  placeholder="请输入" v-model="followForm.IGFBP3"></el-input>μg/ml
      </el-form-item>
      <el-form-item label="甲功：" v-if="disClass === 'sga'"  prop="thyroid">
        <div style="width: 100%;display: flex">
          <el-select size="small" v-model="followForm.Jiagong" :disabled="isStatic">
            <el-option label="正常" value="1"></el-option>
            <el-option label="异常" value="2"></el-option>
          </el-select>
          <el-form-item v-if="followForm.Jiagong === '2'" prop="thyroidDes">
            <el-input size="small" style="width: 220px" placeholder="请输入" v-model="followForm.JiagongDes"></el-input>
          </el-form-item>
        </div>
      </el-form-item>
<!--       <el-form-item label="空腹血糖 ：" v-if="disClass === 'fss' || disClass === 'sga'" prop="fasBloodGlu">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" placeholder="请输入" v-model="followForm.fasBloodGlu"></el-input>mmol/L，
          检查时间：
          <el-date-picker
                  size="small"
                  v-model="followForm.fasBloodGluTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="空腹血糖检查时间"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
      </el-form-item>
      <el-form-item label="空腹胰岛素 ："   prop="fasInsulin">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" placeholder="请输入" v-model="followForm.fasInsulin"></el-input>IU/，
        检查时间：
          <el-date-picker
                  size="small"
                  v-model="followForm.fasInsulinTime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="空腹胰岛素检查时间"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
      </el-form-item>
      <el-form-item label="肝肾脂电解质：" v-if="disClass === 'sga'"  prop="livKidLip">
        <div style="width: 100%;display: flex">
          <el-select size="small" v-model="followForm.livKidLip" :disabled="isStatic">
            <el-option label="正常" value="1"></el-option>
            <el-option label="异常" value="2"></el-option>
          </el-select>
          <el-form-item v-if="followForm.livKidLip === '2'" prop="age">
            <el-input size="small" style="width: 220px" placeholder="请输入" v-model="followForm.LAKLEdes"></el-input>
          </el-form-item>
        </div>
      </el-form-item>
      <el-form-item label="糖化血红蛋白 ：" v-if="disClass === 'fss' || disClass === 'sga'" prop="glyHem">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" placeholder="请输入" v-model="followForm.glyHem"></el-input>
      </el-form-item>
      <el-form-item label="糖化血红蛋白A1c ：" prop="glyHem">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" placeholder="请输入" v-model="followForm.glyHemA"></el-input>%，
        检查时间：
          <el-date-picker
                  size="small"
                  v-model="followForm.glyHemATime"
                  type="date"
                  :clearable="false"
                  :picker-options="pickerOptions"
                  placeholder="糖化血红蛋白A1c检查时间"
                  value-format="yyyy-MM-dd"
              ></el-date-picker>
      </el-form-item> -->
      <el-form-item label="注射后1小时"  v-if="disClass === 'cpp'" >
        LH值：<input v-model="followForm.LH" class="input-underLine"/>mIU/ml，
        FSH值：<input v-model="followForm.FSH" class="input-underLine"/>mIU/ml<br>
        E2：<input v-model="followForm.E2" class="input-underLine"/>pg/ml
        T：<input v-model="followForm.T" class="input-underLine"/>ng/dl
      </el-form-item>
      <el-form-item label="生殖激素 ："  v-if="disClass === 'dsd'" >
        LH：<input v-model="followForm.LH" class="input-underLine"/>mIU/ml，
        FSH：<input v-model="followForm.FSH" class="input-underLine"/>mIU/ml，
        睾酮T：<input v-model="followForm.T" class="input-underLine"/>ng/dL<br>
        <br>
        雌二醇E2：<input v-model="followForm.E2" class="input-underLine"/>pg/ml，
        DHT：<input v-model="followForm.DHT" class="input-underLine"/>ng/ml，
        游离睾酮：<input v-model="followForm.FT" class="input-underLine"/>ng/ml<br>
        <br>
        SHBG：<input v-model="followForm.SHBG" class="input-underLine"/>L，
        IGF-1：<input v-model="followForm.IGF1" class="input-underLine"/>ng/ml，
        IGFBP-3：<input v-model="followForm.IGFBP3" class="input-underLine"/>μg/ml<br>
      </el-form-item>
      <el-form-item label="图片 ：" prop="boneAge">
        <div class="checkBox">
        <div v-for="(item,index) in ImageList" :key="index">
          <p class="thyroid-title">{{ item.title }}：</p>
          <div style="width: 100%;display: flex"  :class="'divBox'+index">
            <div v-for="(url, iIndex) in item.imageUrl" :key="iIndex" style="display: flex; align-items: center; margin: 0 1vw 8px 0;flex-direction: column;">
              <p v-if="item.category === '其他' && url"> 项目名称：<input v-model="followForm.otherImageNames[`${item.category}_${iIndex}`]" class="input-underLine" style="width: 100px"/></p>
              <p v-else style="height: 21px"></p>
              <ImageUpload
                  style="margin-left: 1vw"
                  :key="`img-${index}-${iIndex}`"
                  :caseId="queryPId"
                  organ="follow"
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
      </el-form-item>
      <el-form-item label="性腺B超 ：" prop="BScan" :style="{pointerEvents}">
        <span v-if="sex === '2'">
          子宫三径约：<input v-model="followForm.uterusOne" class="input-underLine"/>*<input v-model="followForm.uterusTwo" class="input-underLine"/>*<input v-model="followForm.uterusThr" class="input-underLine"/>cm<br>
          <br>宫颈长约：<input v-model="followForm.cervixLong" class="input-underLine"/>cm，内膜厚度：<input v-model="followForm.intima" class="input-underLine"/>cm<br>
          <br>左侧卵巢大小约：<input v-model="followForm.ovaLeftOne" class="input-underLine"/>*<input v-model="followForm.ovaLeftTwo" class="input-underLine"/>*<input v-model="followForm.ovaLeftThr" class="input-underLine"/>cm<br>
          <br>右侧卵巢大小约：<input v-model="followForm.ovaRightOne" class="input-underLine"/>*<input v-model="followForm.ovaRightTwo" class="input-underLine"/>*<input v-model="followForm.ovaRightThr" class="input-underLine"/>cm<br>
          <br>最大滤泡直径大小：<input v-model="followForm.follDiameter" class="input-underLine"/>cm<br>
          <br>有无囊肿：<el-select size="small" v-model="followForm.isCyst" :disabled="isStatic">
          <el-option value="1" label="有"></el-option>
          <el-option value="2" label="无"></el-option>
          </el-select><br>
          <span v-if="followForm.isCyst === '1'">
            <input v-model="followForm.cyst" class="input-underLine"/>侧囊肿，
            大小：<input v-model="followForm.cystOne" class="input-underLine"/>*<input v-model="followForm.cystTwo" class="input-underLine"/>*<input v-model="followForm.cystThr" class="input-underLine"/>cm，
            <input  class="input-underLine" style="width: 8vw" v-model="followForm.cystDescribe"/>
          </span>
        </span>
        <span v-if="sex === '1'" >
          睾丸大小：
          右侧<input v-model="followForm.testisLeftOne" class="input-underLine"/>cm×
          <input v-model="followForm.testisLeftTwo" class="input-underLine"/>cm×
          <input v-model="followForm.testisLeftThr" class="input-underLine"/>cm ，
          长径<input v-model="followForm.testisLeftLon" class="input-underLine"/>cm；<br>
          左侧<input v-model="followForm.testisRightOne" class="input-underLine"/>cm×
          <input v-model="followForm.testisRightTwo" class="input-underLine"/>cm×
          <input v-model="followForm.testisRightThr" class="input-underLine"/>cm ，
          长径<input v-model="followForm.testisRightLon" class="input-underLine"/>cm
        </span>
      </el-form-item>
      <el-form-item label="实验室检查 ：" v-if="disClass === 'fss' || disClass === 'sss' || disClass === 'cpp'" prop="other" :style="{pointerEvents}">
        <div class="fss-box">
            <div class="thyroid-lie">
              <p class="lie-first">LH：<input v-model="followForm.LH" class="input-underLine"/>mIU/mL</p>
              <p class="lie-con">FSH：<input v-model="followForm.FSH" class="input-underLine"/>mIU/mL</p>
              <p class="lie-last"> 检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.LHFSHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="LH以及FSH检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">E2：<input v-model="followForm.E2" class="input-underLine"/>pg/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.E2Time"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="E2检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> T：<input v-model="followForm.T" class="input-underLine"/>ng/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.TTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="T检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> PRL：<input v-model="followForm.PRL" class="input-underLine"/>ng/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.PRLTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="PRL检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">IGF-1：<input v-model="followForm.IGF1" class="input-underLine"/>ng/mL</p>
              <p class="lie-con">IGFBP-3：<input v-model="followForm.IGFBP3" class="input-underLine"/>ug/mL</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.IGFBPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="IGF1以及IGFBP3检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                甲功：
                <el-radio class="elRadio" v-model="followForm.Jiagong" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.Jiagong" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.Jiagong === '2'">
                异常说明：<input class="input-underLine" style="width: 50%" v-model="followForm.JiagongDes"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.thyroidTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="甲功检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">ACTH(8am)：<input v-model="followForm.ACTH" class="input-underLine"/>pg/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.ACTHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="ACTH检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">皮质醇（8am）：<input v-model="followForm.cortisol" class="input-underLine"/>ug/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.cortisolTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="皮质醇检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">DHEAs：<input v-model="followForm.DHEAS" class="input-underLine"/>ug/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.DHEATime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="DHEAs检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> 17-OHP：<input v-model="followForm.OHP" class="input-underLine"/>nmol/</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.OHPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="17-OHP检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                血常规：
                <el-radio class="elRadio" v-model="followForm.blood" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.blood" label="2">异常</el-radio>
              </p>
              <p class="lie-con"  v-if="followForm.blood === '2'" >
                异常说明：<input class="input-underLine" style="width: 50%"   v-model="followForm.bloodDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.bloodTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="血常规检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                尿常规：
                <el-radio class="elRadio" v-model="followForm.urinalysis" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.urinalysis" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.urinalysis === '2'" > 
                异常说明：<input class="input-underLine" style="width: 50%" v-model="followForm.urinalysisDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.urinalysisTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="尿常规检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                肝肾脂糖电解质：
                <el-radio class="elRadio" v-model="followForm.livKidLip" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.livKidLip" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.livKidLip === '2'">
                异常说明：<input  class="input-underLine" style="width: 50%" v-model="followForm.LAKLEdes"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.LAKLGETime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="肝肾脂糖电解质检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px"> 
              乙肝三系：
                <el-radio class="elRadio" v-model="followForm.HBs" label="1">阴性</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="2">HBSAb阳性</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="3">小三阳</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="4">大三阳</el-radio>
              </p>
              <!--              <input v-if="followForm.HBs === '2'" class="input-underLine" style="width: 50%"-->
<!--                     v-model="followForm.HBsDescribe"/>-->
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.HBsTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="乙肝三系检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 300px"> Gh药物激发试验：Gh峰值<input v-model="followForm.gh" class="input-underLine"/>ng/ml</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.ghTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="Gh药物激发试验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'" >
              <p style="width: 300px">空腹血糖 ：<input v-model="followForm.fasBloodGlu" class="input-underLine"/>mmol/L</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasBloodGluTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="Gh药物激发试验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'">
              <p style="width: 300px">空腹胰岛素 ：<input v-model="followForm.fasInsulin" class="input-underLine"/>mIU/L</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasInsulinTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹胰岛素检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <!-- <div class="thyroid-lie" v-if="disClass === 'sga'">
              <p style="width: 300px">肝肾脂电解质：
                  <el-select size="small" v-model="followForm.livKidLip" :disabled="isStatic">
                    <el-option label="正常" value="1"></el-option>
                    <el-option label="异常" value="2"></el-option>
                  </el-select>
                    <input v-if="followForm.livKidLip === '2'" style="width: 220px" v-model="followForm.LAKLEdes"/>
              </p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasInsulinTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹胰岛素检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div> -->
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'">
              <p style="width: 300px">糖化血红蛋白 ：<input v-model="followForm.glyHem" class="input-underLine"/>%</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.glyHemTime"
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
              <p style="width: 300px">糖化血红蛋白A1c ：<input v-model="followForm.glyHemA" class="input-underLine"/>%</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.glyHemATime"
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
      </el-form-item>
      <el-form-item label="是否达终身高：" prop="isFinalHeight">
        <el-select size="small" v-model="followForm.isFinalHeight" :disabled="isStatic">
          <el-option label="是" value="1"></el-option>
          <el-option label="否" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="诊疗方案 ：" v-if="disClass === 'dsd'"  prop="diaPlan">
        <el-select v-model="followForm.diaPlan" :disabled="isStatic" @change="diaPlanChange">
          <el-option value="1" label="雄激素替代治疗（药名，剂量，用法）"></el-option>
          <el-option value="2" label="雌激素替代治疗（药名，剂量，用法）"></el-option>
        </el-select>
        <span v-if="followForm.diaPlan === '1'">
          <input v-model="followForm.rhGH" style="width: 10vw" class="input-underLine"/>
        </span>
        <span v-if="followForm.diaPlan === '2'">
          <input v-model="followForm.rhGH" style="width: 10vw"  class="input-underLine"/>
        </span>
      </el-form-item>
      <el-form-item v-else label="诊疗方案 ：" prop="diaPlan">
        <el-select v-model="followForm.diaPlan" :disabled="isStatic" @change="diaPlanChange">
          <el-option value="1" label="未治疗"></el-option>
          <el-option value="2" label="rhGH治疗"></el-option>
          <el-option v-if="disClass === 'cpp' || disClass === 'fss' || disClass === 'sga'" value="7" label="GnRHa治疗"></el-option>
          <el-option value="3" label="GnRHa联合生长激素治疗"></el-option>
          <el-option value="8" label="芳香化酶抑制剂治疗"></el-option>
          <!-- <el-option value="4" label="停止治疗"></el-option> -->
          <el-option value="11" label="停止芳香化酶抑制剂"></el-option>
          <el-option value="10" label="芳香化酶联合生长激素治疗"></el-option>
          <el-option value="12" label="停止芳香化酶联合生长激素治疗"></el-option>
          <el-option value="4" label="停止GnRHa治疗"></el-option>
          <el-option value="5" label="停止GnRHa联合生长激素治疗"></el-option>
          <el-option value="6" label="停止生长激素治疗"></el-option>
        </el-select>
        <!--  || followForm.diaPlan === '10' -->
        <span  v-if="followForm.diaPlan === '2'" >
          <el-select v-model="followForm.rhGH" :disabled="isStatic">
            <el-option value="1" label="短效rhGH(粉剂)"></el-option>
            <el-option value="2" label="短效rhGH(水剂)"></el-option>
            <el-option value="3" label="金培生长激素注射液"></el-option>
            <el-option value="4" label="怡培生长激素注射液"></el-option>
            <el-option value="5" label="帕西生长激素注射液"></el-option>
          </el-select>
          <span v-if="followForm.rhGH === '1' || followForm.rhGH === '2'">
            <input v-model="followForm.rhGHdose" @change="calculate" class="input-underLine"/>IU/d，<input v-model="followForm.rhGHdoseKG" class="input-underLine"/>IU/kg.d，
          </span>
          <span v-if="followForm.rhGH === '3' || followForm.rhGH === '4' || followForm.rhGH === '5'">
            <input v-model="followForm.PEGrhGHdose"  @change="PEGcalculate" class="input-underLine"/>mg/w，<input v-model="followForm.PEGrhGHdoseKG" class="input-underLine"/>mg/kg.w，
          </span>
        </span>
       
        <span  v-if="followForm.diaPlan === '4'">
          <el-select v-model="followForm.rhCustomizationDiaPlan" allow-create filterable default-first-option clearable
                       size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width: 15vw">
            <el-option value="1" label="短效rhGH"></el-option>
            <el-option value="2" label="长效生长激素 (PEG-rhGH)"></el-option>
          </el-select>
          <span v-if="followForm.rhCustomizationDiaPlan === '1'">
            停止治疗短效rhGH：<input v-model="followForm.rhCustomizationPrompt" @change="rhGHcalculate" class="input-underLine"/>IU/d，<input v-model="followForm.rhCustomizationPromptKG" class="input-underLine"/>mg/kg.w，
          </span>
          <span v-if="followForm.rhCustomizationDiaPlan === '2'">
            停止治疗长效生长激素（PEG-rhGH）:<input v-model="followForm.PEGrhCustomizationPrompt" @change="PEGrhGHcalculate" class="input-underLine"/>mg/kg.w，<input v-model="followForm.PEGrhCustomizationPromptKG" class="input-underLine"/>IU/kg.d，
          </span>
        </span>
      <!-- || followForm.diaPlan === '3' -->
        <span v-if="followForm.diaPlan === '7' ">
          <el-select v-model="followForm.rhGH" allow-create filterable default-first-option clearable
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
        <!-- 新增GnRHal联合生长激素治疗代码 -->
        <!-- <span v-if="followForm.diaPlan === '3'">
          <el-select v-model="followForm.rhUnitedCustomization" allow-create filterable default-first-option clearable
                       size="small" placeholder="（可自定义药物品种及剂型）" :disabled="isStatic" style="width: 15vw">
            <el-option value="1" label="短效rhGH"></el-option>
            <el-option value="2" label="长效生长激素 (PEG-rhGH)"></el-option>
          </el-select> -->
          <!-- 新增短效rhGH效果 -->
          <!-- <span v-if="followForm.rhUnitedCustomization === '1'">
            联合用药治疗短效rhGH：<input v-model="followForm.rhUnitedDose"  @change="dosecalculate" class="input-underLine"/>IU/d，<input v-model="followForm.rhUnitedDoseKG" class="input-underLine"/>mg/kg.w，
          </span>
          <span v-if="followForm.rhUnitedCustomization === '2'">
            联合用药治疗长效生长激素（PEG-rhGH）:<input v-model="followForm.PEGrhUnitedDose" @change="PEGdosecalculate" class="input-underLine"/>mg/kg.w，<input v-model="followForm.PEGdosecalculateKG" class="input-underLine"/>IU/kg.d，
          </span>
        </span> -->
        <div style="width: 38vw;" v-if="followForm.diaPlan === '3'">
            <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary" size="mini" @click="addGenRow(genData)"></el-button>
            <el-table
                border
                :data="genData"
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
                      <input v-model="scope.row.rhGHdose" style="width:60px" @change="tableRowCalculate(scope.row)" class="input-underLine"/>IU/kg.d，<input style="width:60px" v-model="scope.row.rhGHdoseKG" class="input-underLine"/>mg/kg.w，
                    </span>
                    <span v-if="scope.row.rhUnitedCustomization === '3' || scope.row.rhUnitedCustomization === '4' || scope.row.rhUnitedCustomization === '5'">
                      <input style="width:60px" v-model="scope.row.PEGrhGHdoseKG" @change="tableRowPEGcalculate(scope.row)" class="input-underLine"/>mg/w，<input v-model="scope.row.PEGrhGHdose" style="width:60px"   class="input-underLine"/>mg/kg.w，
                    </span>
                </template>
              </el-table-column>
              <el-table-column label="操作"  fixed="right" width="120">
                <template slot-scope="scope">
                  <el-button @click.native.prevent="delGenRow(scope.$index,genData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                       size="mini" @click="addGenRow(genData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div style="width: 38vw;" v-if="followForm.diaPlan === '10'">
            <el-button style="float: right;margin-right: 2vh;margin-top: -5vh" class="el-icon-plus" type="primary" size="mini" @click="addGenRow(genData)"></el-button>
            <el-table
                border
                :data="genData"
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
                  <el-button @click.native.prevent="delGenRow(scope.$index,genData)" class="el-icon-minus"
                             size="mini"></el-button>
                  <el-button class="el-icon-plus" type="primary"
                       size="mini" @click="addGenRow(genData)"></el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        <span v-if="followForm.diaPlan === '8' ">
         <!--  anstrozole -->
          <el-select v-model="followForm.rhGH">
            <el-option value="1" label="阿那曲唑0.5/片"></el-option>
            <el-option value="2" label="阿那曲唑1/片"></el-option>
            <el-option value="3" label="阿那曲唑1.5/片"></el-option>
            <el-option value="4" label="阿那曲唑2/片"></el-option>
          </el-select>
        </span>
          <!--  -->
          其他药物：<input size="small" style="width: 260px" class="input-underLine" :style="{pointerEvents}" placeholder="请输入其他药物补充" v-model="followForm.otherMedicine"/>
      </el-form-item>
      <el-form-item label="其他 ：" prop="other" :style="{pointerEvents}">
        <el-input type="textarea" :rows="2" placeholder="请输入内容" v-model="followForm.other"></el-input>
      </el-form-item>
    </el-form>
    <el-form ref="followForm" :model="followForm" label-width="10vw" v-else>
      <el-form-item label="随访日期：" prop="comCount">
        <el-date-picker
            :style="{pointerEvents}"
            v-model="followForm.followTime"
            type="date"
            :clearable="false"
            :picker-options="pickerOptions"
            size="small"
            @change="getAge"
            placeholder="请选择日期"
            value-format="yyyy-MM-dd"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="年龄：" prop="age">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" v-model="followForm.age" readonly></el-input>
      </el-form-item>
      <el-form-item label="身高：" prop="Ht">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}"  @blur="validateHeightFun" placeholder="请输入身高" v-model="followForm.Ht"></el-input>cm
      </el-form-item>
      <el-form-item label="体重：" prop="Wt">
        <el-input size="small" style="width: 220px" :style="{pointerEvents}" @blur="validateWeightFun" placeholder="请输入体重" v-model="followForm.Wt"></el-input>kg
      </el-form-item>
      <el-form-item label="实验室检查 ：" v-if="disClass === 'eltm'" prop="other" :style="{pointerEvents}">
        <div class="fss-box">
            <div class="thyroid-lie">
              <p class="lie-first">LH：<input v-model="followForm.LH" class="input-underLine"/>mIU/mL</p>
              <p class="lie-con">FSH：<input v-model="followForm.FSH" class="input-underLine"/>mIU/mL</p>
              <p class="lie-last"> 检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.LHFSHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="LH以及FSH检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">E2：<input v-model="followForm.E2" class="input-underLine"/>pg/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.E2Time"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="E2检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> T：<input v-model="followForm.T" class="input-underLine"/>ng/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.TTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="T检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> PRL：<input v-model="followForm.PRL" class="input-underLine"/>ng/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.PRLTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="PRL检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">IGF-1：<input v-model="followForm.IGF1" class="input-underLine"/>ng/mL</p>
              <p class="lie-con">IGFBP-3：<input v-model="followForm.IGFBP3" class="input-underLine"/>ug/mL</p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.IGFBPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="IGF1以及IGFBP3检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                甲功：
                <el-radio class="elRadio" v-model="followForm.Jiagong" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.Jiagong" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.Jiagong === '2'">
                异常说明：<input class="input-underLine" style="width: 50%" v-model="followForm.JiagongDes"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.thyroidTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="甲功检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">ACTH(8am)：<input v-model="followForm.ACTH" class="input-underLine"/>pg/mL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.ACTHTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="ACTH检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">皮质醇（8am）：<input v-model="followForm.cortisol" class="input-underLine"/>ug/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.cortisolTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="皮质醇检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">DHEAs：<input v-model="followForm.DHEAS" class="input-underLine"/>ug/dL</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.DHEATime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="DHEAs检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first"> 17-OHP：<input v-model="followForm.OHP" class="input-underLine"/>nmol/</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.OHPTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="17-OHP检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                血常规：
                <el-radio class="elRadio" v-model="followForm.blood" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.blood" label="2">异常</el-radio>
              </p>
              <p class="lie-con"  v-if="followForm.blood === '2'" >
                异常说明：<input class="input-underLine" style="width: 50%"   v-model="followForm.bloodDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                        size="small"
                        v-model="followForm.bloodTime"
                        type="date"
                        :clearable="false"
                        :picker-options="pickerOptions"
                        placeholder="血常规检查时间"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                尿常规：
                <el-radio class="elRadio" v-model="followForm.urinalysis" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.urinalysis" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.urinalysis === '2'" > 
                异常说明：<input class="input-underLine" style="width: 50%" v-model="followForm.urinalysisDescribe"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.urinalysisTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="尿常规检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p class="lie-first">
                肝肾脂糖电解质：
                <el-radio class="elRadio" v-model="followForm.livKidLip" label="1">正常</el-radio>
                <el-radio class="elRadio" v-model="followForm.livKidLip" label="2">异常</el-radio>
              </p>
              <p class="lie-con" v-if="followForm.livKidLip === '2'">
                异常说明：<input  class="input-underLine" style="width: 50%" v-model="followForm.LAKLEdes"/>
              </p>
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.LAKLGETime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="肝肾脂糖电解质检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 620px"> 
              乙肝三系：
                <el-radio class="elRadio" v-model="followForm.HBs" label="1">阴性</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="2">HBSAb阳性</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="3">小三阳</el-radio>
                <el-radio class="elRadio" v-model="followForm.HBs" label="4">大三阳</el-radio>
              </p>
              <!--              <input v-if="followForm.HBs === '2'" class="input-underLine" style="width: 50%"-->
<!--                     v-model="followForm.HBsDescribe"/>-->
              <p class="lie-last">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.HBsTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="乙肝三系检查时间"
                      value-format="yyyy-MM-dd"
                  ></el-date-picker>
                </p>
            </div>
            <div class="thyroid-lie">
              <p style="width: 300px"> Gh药物激发试验：Gh峰值<input v-model="followForm.gh" class="input-underLine"/>ng/ml</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.ghTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="Gh药物激发试验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'" >
              <p style="width: 300px">空腹血糖 ：<input v-model="followForm.fasBloodGlu" class="input-underLine"/>mmol/L</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasBloodGluTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="Gh药物激发试验检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'">
              <p style="width: 300px">空腹胰岛素 ：<input v-model="followForm.fasInsulin" class="input-underLine"/>mIU/L</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasInsulinTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹胰岛素检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div>
            <!-- <div class="thyroid-lie" v-if="disClass === 'sga'">
              <p style="width: 300px">肝肾脂电解质：
                  <el-select size="small" v-model="followForm.livKidLip" :disabled="isStatic">
                    <el-option label="正常" value="1"></el-option>
                    <el-option label="异常" value="2"></el-option>
                  </el-select>
                    <input v-if="followForm.livKidLip === '2'" style="width: 220px" v-model="followForm.LAKLEdes"/>
              </p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.fasInsulinTime"
                      type="date"
                      :clearable="false"
                      :picker-options="pickerOptions"
                      placeholder="空腹胰岛素检查时间"
                      value-format="yyyy-MM-dd"
                      @change="getAge"
                  ></el-date-picker>
              </p>
            </div> -->
            <div class="thyroid-lie" v-if="disClass === 'fss' || disClass === 'sga'">
              <p style="width: 300px">糖化血红蛋白 ：<input v-model="followForm.glyHem" class="input-underLine"/>%</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.glyHemTime"
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
              <p style="width: 300px">糖化血红蛋白A1c ：<input v-model="followForm.glyHemA" class="input-underLine"/>%</p>
              <p class="lie-con">
                检查时间：
                <el-date-picker
                      size="small"
                      v-model="followForm.glyHemATime"
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
      </el-form-item>
      <el-form-item label="实验室检查" v-if="disClass === 'eltm'">
        <p class="thyroid-title" style="margin-top: 20px">血常规报告上传：</p>
        <ImageUpload
            :caseId="queryId"
            organ="eltm"
            category="E路童萌血常规"
            :fileName="followForm.boneAgeUrl"
            @update:fileName="v =>upBoneImage(v)"
            :editable="!isStatic"
        >
        </ImageUpload>
        <p class="thyroid-title">尿常规报告上传：</p>
        <ImageUpload
            :caseId="queryId"
            organ="eltm"
            category="E路童萌尿常规报告"
            :fileName="followForm.boneAgeUrl"
            @update:fileName="v =>upBoneImage(v)"
            :editable="!isStatic"
        >
        </ImageUpload>
        <p class="thyroid-title">肝肾功能报告上传：</p>
        <ImageUpload
            :caseId="queryId"
            organ="eltm"
            category="E路童萌肝肾功能报告"
            :fileName="followForm.boneAgeUrl"
            @update:fileName="v =>upBoneImage(v)"
            :editable="!isStatic"
        >
        </ImageUpload>
        <p class="thyroid-title">IGF-1报告上传：</p>
        <ImageUpload
            :caseId="queryId"
            organ="eltm"
            category="E路童萌IGF-1报告"
            :fileName="followForm.boneAgeUrl"
            @update:fileName="v =>upBoneImage(v)"
            :editable="!isStatic"
        >
        </ImageUpload>
        <p class="thyroid-title">IGFBP-3报告上传：</p>
        <ImageUpload
            :caseId="queryId"
            organ="eltm"
            category="E路童萌IGFBP-3报告"
            :fileName="followForm.boneAgeUrl"
            @update:fileName="v =>upBoneImage(v)"
            :editable="!isStatic"
        >
        </ImageUpload>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import ImageUpload from "../imageViewer/ImageUpload";
  import request from "../../script/request";
  import image from "../../script/otherImage"
  import {validateHeightByBirthdate } from '../../utils/heightValidator.js'

  const ImageString = JSON.stringify(image)

  export default {
    name: "shortFollow",
    components: {ImageUpload},
    props: {
      queryId: String,
      sex: String,
      birthTime: String,
      queryPId: String,
      stuts: String,
      disClass:String,
    },
    mounted() {
      this.followForm.queryId = this.queryId;
      this.followForm.queryPId = this.queryPId;
      this.ImageList = JSON.parse(ImageString);
      if (this.stuts === 'select') {
        this.isStatic = true;
        this.pointerEvents = "none";
      } else {
        this.isStatic = false;
        this.pointerEvents = "";
      }
    },
    watch: {
      'stuts'() {
        if (this.stuts === 'select') {
          this.isStatic = true;
          this.pointerEvents = "none";
        }else {
          this.isStatic = false;
          this.pointerEvents = "";
        }
        for (let key in this.followForm) {
          this.followForm[key] = ''
        }
        this.followForm.queryId = this.queryId;
        this.followForm.queryPId = this.queryPId;
        this.ImageList = JSON.parse(ImageString);
      },
        // 新增：监听诊疗方案变化，清空genData
      // 'followForm.diaPlan'(newVal, oldVal) {
      //   // 仅当值真的发生变化时执行（避免初始化时重复触发）
      //   if (newVal !== oldVal && newVal !== undefined && newVal !== '') {
      //     // 重置genData为初始的空行结构
      //     this.genData = [{
      //       rhGH: '', 
      //       rhUnitedCustomization: '',
      //       rhGHdose:'',
      //       rhGHdoseKG:'',
      //       PEGrhGHdose:'',
      //       PEGrhGHdoseKG:''
      //     }];
      //     // 可选：强制更新视图（确保表格刷新）
      //     this.$forceUpdate();
      //   }
      // }
    },
    data() {
      return {
        isStatic: false,
        pointerEvents:"",
        genData:[{rhGH: '', rhUnitedCustomization: '',rhGHdose:'',rhGHdoseKG:'',PEGrhGHdose:'',PEGrhGHdoseKG:''}],
        followForm: {
          queryId: '',
          queryPId: '',
          followTime:"",
          age:"",
          Ht:"",
          Wt:"",
          genStag:"",
          isFYassess:'',//是否发育评估
          Peabody:'',//Peabody运动发育评估
          GriffithsA:'',//Griffiths心理发育评估
          GriffithsB:'',
          GriffithsC:'',
          GriffithsD:'',
          GriffithsE:'',
          GriffithsF:'',
          wszlb:'',//韦氏智力量表
          // outStag:"",
          pubStag:"",
          // IGF1:"",
          // IGFBP3:"",
          Jiagong:"",
          JiagongDes:"",
          fasBloodGlu:"",
          fasBloodGluTime:'',//空腹血糖检查时间
          fasInsulin:"",
          fasInsulinTime:'',//空腹胰岛素检查时间
          livKidLip:"",
          LAKLEdes:"",
          glyHem:"",
          glyHemTime:"",//糖化血红蛋白检查时间
          glyHemA:'',//糖化血红蛋白A1c
          glyHemATime:'',//糖化血红蛋白A1c检查时间
          otherMedicine:'',//其他药物
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
          testisLeftOne:"",
          testisLeftTwo:"",
          testisLeftThr:"",
          testisLeftLon:"",
          testisRightOne:"",
          testisRightTwo:"",
          testisRightThr:"",
          testisRightLon:"",
          diaPlan:"",
          other:"",
          rhGH:"",
          // anstrozole:'',//阿那曲唑
          rhUnitedCustomization:'',//重新定义GnRHal联合生长激素治疗选项
          rhUnitedDose:'',//重新定义GnRHal联合生长激素治疗选项
          PEGrhUnitedDose:"",//联合用药治疗长效生长激素（PEG-rhGH）
          rhUnitedDoseKG:"",//自动计算数值
          PEGdosecalculateKG:"",//自动计算数值
          rhCustomizationDiaPlan:'',//重新定义GnRHal联合生长激素治疗选项
          rhCustomizationPrompt:'',//重新定义GnRHal联合生长激素治疗选项
          PEGrhCustomizationPrompt:"",//长效生长激素（PEG-rhGH）
          rhCustomizationPromptKG:"",//自动计算数值
          PEGrhCustomizationPromptKG:"",//自动计算数值


          rhGHdose:"",
          PEGrhGHdose:'',//长效生长激素（PEG-rhGH）
          rhGHdoseKG:"",//自动计算数值
          PEGrhGHdoseKG:"",//自动计算数值

          LH:"",
          FSH:"",
          LHFSHTime:'',
          E2:"",
          E2Time:'',
          T:"",
          TTime:'',
          PRL:'',
          PRLTime:'',
          DHT: '',
          FT: '',
          SHBG: '',
          // IGF1: '',
          IGF1: '',
          IGFBPTime: '',
          IGFBP3: '',
          // Jiagong:'',
          // thyroidDescribe:'',
          thyroidTime: '',
          ACTH:'',
          ACTHTime:'',
          cortisol: '',
          cortisolTime:'',
          DHEAS:'',
          DHEATime:'',
          OHP:'',
          OHPTime:'',
          blood:'',
          bloodDescribe:'',
          bloodTime:'',
          urinalysis:'',
          urinalysisDescribe:'',
          urinalysisTime:'',
          LAKLGETime:'',
          HBs:'',
          HBsTime:'',
          gh:'',
          ghTime:'',
          isFinalHeight: '',//是否达终身高
          boneAgeUrl:'',//图片
          genData: [],
        },
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
          },
        },
        ImageList: JSON.parse(ImageString),
      }
    },
    methods: {
      // upBoneImage(v, arr, title, iIndex, imageArr) {
      //   imageArr.forEach((item, i) => {
      //     if (item.title == title) {
      //       item.imageUrl[iIndex] = v
      //     }
      //     this.$set(this.ImageList, i, item);
      //     this.$forceUpdate()
      //   });
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
          this.$delete(this.followForm.otherImageNames, deleteKey);

          // 3. 重新整理剩余图片的项目名称键名（避免索引断层）
          const newOtherImageNames = {};
          targetImageUrl.forEach((url, newIndex) => {
            const oldKey = `${category}_${newIndex + 1}`;
            if (this.followForm.otherImageNames[oldKey]) {
              newOtherImageNames[`${category}_${newIndex}`] = this.followForm.otherImageNames[oldKey];
              this.$delete(this.followForm.otherImageNames, oldKey);
            }
          });
          // 合并新的键值对（保持响应式）
          Object.keys(newOtherImageNames).forEach(key => {
            this.$set(this.followForm.otherImageNames, key, newOtherImageNames[key]);
          });
        }
        this.$forceUpdate(); // 强制更新视图
      },
      addGenRow(tableData){
        tableData.push({rhGH: '', rhUnitedCustomization: '',rhGHdose:'',rhGHdoseKG:'',PEGrhGHdose:'',PEGrhGHdoseKG:''})
      },
      delGenRow(index, rows){
        rows.splice(index, 1);
      },
      // 表格内 - 短效rhGH剂量计算（仅处理表格行数据）
      tableRowCalculate(row) {
        // 体重校验（和原有逻辑一致）
        const weight = Number(this.followForm.Wt) || 0;
        if (!this.followForm.Wt || this.followForm.Wt === '0' || weight === 0) {
          this.$message({
            message: '体重未填写或为0，无法计算',
            type: 'warning'
          });
          row.rhGHdoseKG = ''; // 清空当前行的计算结果
          return;
        }

        // 仅计算表格行的剂量
        const dose = Number(row.rhGHdose) || 0;
        const result = dose / weight;
        row.rhGHdoseKG = result.toFixed(4);
      },

      // 表格内 - 长效生长激素剂量计算（仅处理表格行数据）
      tableRowPEGcalculate(row) {
        // 体重校验（和原有逻辑一致）
        const weight = Number(this.followForm.Wt) || 0;
        if (!this.followForm.Wt || this.followForm.Wt === '0' || weight === 0) {
          this.$message({
            message: '体重未填写或为0，无法计算',
            type: 'warning'
          });
          row.PEGrhGHdose = ''; // 清空当前行的计算结果
          return;
        }

        // 仅计算表格行的剂量
        const dose = Number(row.PEGrhGHdoseKG) || 0;
        const result = dose / weight;
        row.PEGrhGHdose = result.toFixed(4);
      },
      // addData() {
        // this.followForm.familyData = this.familyData;
        // this.followForm.genData = this.genData;
        // console.log(this.followForm.genData)
        // this.followForm.sampleClass = this.sampleBank;
        // let data = null;
        // data = this.followForm;
        // return data;
      // },
      //上传多张图片
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
        this.getDetail()
        imageArr.forEach((item, i) => {
          if (item.title == title) {
            if(v){
              item.imageUrl[iIndex] = v;
              // 处理“其他”图片的项目名称初始化
              if (item.category === '其他') {
                const key = `${item.category}_${iIndex}`;
                // 确保响应式设置（Vue2 需用 $set）
                if (!this.followForm.otherImageNames[key]) {
                  this.$set(this.followForm.otherImageNames, key, '');
                }
              }
            }
          }
          // console.log(this.followForm.otherImageNames)
          this.$set(this.ImageList, i, item);
          this.$forceUpdate()
        });
      },
      calculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.rhGHdoseKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.rhGHdose) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.rhGHdoseKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.rhGHdoseKG = result.toFixed(4);
      },
      PEGcalculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.PEGrhGHdoseKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.PEGrhGHdose) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.PEGrhGHdoseKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.PEGrhGHdoseKG = result.toFixed(4);
      },
      rhGHcalculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.rhCustomizationPromptKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.rhCustomizationPrompt) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.rhCustomizationPromptKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.rhCustomizationPromptKG = result.toFixed(4);
      },
      PEGrhGHcalculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.PEGrhCustomizationPromptKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.PEGrhCustomizationPrompt) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.PEGrhCustomizationPromptKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.PEGrhCustomizationPromptKG = result.toFixed(4);
        // console.log(kgData, 'this.followForm.PEGrhCustomizationPromptKG')
      },
      dosecalculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.rhUnitedDoseKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.rhUnitedDose) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.rhUnitedDoseKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.rhUnitedDoseKG = result.toFixed(4);
      },
      PEGdosecalculate(){
        if (!this.followForm.Wt || this.followForm.Wt === '0' || Number(this.followForm.Wt) === 0) {
        this.$message({
          message: '体重未填写或为0，无法计算',
          type: 'warning'
        });
        this.followForm.PEGdosecalculateKG = ''; // 清空计算结果
        return;
      }
        const kgData = Number(this.followForm.PEGrhUnitedDose) || 0;
        const wtData = Number(this.followForm.Wt) || 0;
        if (wtData === 0) {
          this.followForm.PEGdosecalculateKG = '';
          return;
        }
        const result = kgData / wtData;
        this.followForm.PEGdosecalculateKG = result.toFixed(4);
      },
      diaPlanChange(){
        // console.log(a,"发生变化");
        this.genData = [{
            rhGH: '', 
            rhUnitedCustomization: '',
            rhGHdose:'',
            rhGHdoseKG:'',
            PEGrhGHdose:'',
            PEGrhGHdoseKG:''
          }];
      },
      getAge() {
        // let strDate1 = this.birthTime + ".0";
        // let strDate2 = this.followForm.followTime + "   00:00:00.0";
        // strDate1 = strDate1.substring(0, strDate1.lastIndexOf(".")).replace(/-/g, "/ ");
        // strDate2 = strDate2.substring(0, strDate2.lastIndexOf(".")).replace(/-/g, "/ ");
        // //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
        // let date1 = Date.parse(strDate1);
        // let date2 = Date.parse(strDate2);
        // let day = Math.ceil((date2 - date1) / (60 * 60 * 1000 * 24));
        // let year = Math.floor(day / 365);
        // let y = day % 365;
        // let month = Math.floor(y / 30);
        // this.followForm.age = year + "岁" + month + "个月"
        // 解析出生日期（处理无时间部分的情况）
        const birthDate = new Date(this.birthTime);
        // 解析随访日期（直接使用完整时间字符串）
        const followDate = new Date(this.followForm.followTime);
        
        // 验证日期有效性
        if (isNaN(birthDate.getTime()) || isNaN(followDate.getTime())) {
          console.error("Invalid date format");
          this.followForm.age = "日期格式错误";
          return;
        }

        // 计算精确的毫秒差
        const timeDiff = followDate - birthDate;
        
        // 转换为天数（四舍五入）
        const days = Math.floor(timeDiff / (24 * 60 * 60 * 1000));
        
        // 计算年、月、日
        const years = Math.floor(days / 365);
        const remainingDays = days % 365;
        const months = Math.floor(remainingDays / 30);
        
        this.followForm.age = `${years}岁${months}个月`;
      },
      getDetail(){
        this.followForm.queryPId = this.queryPId;
        const queryPId = this.queryPId;
        request.followDetail({queryPId}, data => {
          this.followForm.followTime = data.foll_time;
          this.followForm.age = data.age;
          this.followForm.Ht = data.Ht;
          this.followForm.Wt = data.Wt;
          this.followForm.isFYassess=data.beh_dev_ass;//是否行为发育评估
          this.followForm.Peabody=data.ped_mot_dev_ass;//Peabody运动发育评估
          this.followForm.GriffithsA=data.gro_mot;//粗大运动
          this.followForm.GriffithsB=data.ind_soc;//个人社会
          this.followForm.GriffithsC=data.lis_lan;//听力语言
          this.followForm.GriffithsD=data.han_eye_coo;//手眼协调
          this.followForm.GriffithsE=data.vis_rep;//视觉表现
          this.followForm.GriffithsF=data.pra_rea;//实际推理
          this.followForm.wszlb=data.wec_sca;//韦氏智力量表
          this.followForm.genStag = data.gen_stag;
          // this.followForm.outStag = data.gen_stag;
          this.followForm.pubStag = data.pub_stag;
          // this.followForm.IGF1 = data.IGF1;
          // this.followForm.IGFBP3 = data.IGFBP3;
          let temJiagong = data.Jiagong ? JSON.parse(data.Jiagong) : {};
          this.followForm.Jiagong = temJiagong['Jiagong'];
          this.followForm.JiagongDes = temJiagong['JiagongDes'];
          // this.followForm.fasBloodGlu = data.fas_blood_glu;
          this.followForm.fasInsulin = data.fas_insulin;
          let temJiag = data.Jiagong ? JSON.parse(data.Jiagong) : {};
          this.followForm.Jiagong = temJiag['Jiagong'];
          this.followForm.JiagongDes = temJiag['JiagongDes'];
          this.followForm.fasBloodGlu = data.fas_blood_glu;
          this.followForm.fasInsulin = data.fas_insulin;
          let temGz = data.liv_kid_lip ? JSON.parse(data.liv_kid_lip) : {};
          this.followForm.livKidLip = temGz['livKidLip'];
          this.followForm.LAKLEdes = temGz['LAKLEdes'];
          this.followForm.glyHem = data.gly_hem;
          let temBc = data.gon_B_ult ? JSON.parse(data.gon_B_ult) : {};
          this.followForm.uterusOne = temBc['uterusOne'];
          this.followForm.uterusTwo = temBc['uterusTwo'];
          this.followForm.uterusThr = temBc['uterusThr'];
          this.followForm.cervixLong = temBc['cervixLong'];
          this.followForm.intima = temBc['intima'];
          this.followForm.ovaLeftOne = temBc['ovaLeftOne'];
          this.followForm.ovaLeftTwo = temBc['ovaLeftTwo'];
          this.followForm.ovaLeftThr = temBc['ovaLeftThr'];
          this.followForm.ovaRightOne = temBc['ovaRightOne'];
          this.followForm.ovaRightTwo = temBc['ovaRightTwo'];
          this.followForm.ovaRightThr = temBc['ovaRightThr'];
          this.followForm.follDiameter = temBc['follDiameter'];
          this.followForm.isCyst = temBc['isCyst'];
          this.followForm.cyst = temBc['cyst'];
          this.followForm.cystOne = temBc['cystOne'];
          this.followForm.cystTwo = temBc['cystTwo'];
          this.followForm.cystThr = temBc['cystThr'];
          this.followForm.cystDescribe = temBc['cystDescribe'];
          this.followForm.testisLeftOne = temBc['testisLeftOne'];
          this.followForm.testisLeftTwo = temBc['testisLeftTwo'];
          this.followForm.testisLeftThr = temBc['testisLeftThr'];
          this.followForm.testisLeftLon = temBc['testisLeftLon'];
          this.followForm.testisRightOne = temBc['testisRightOne'];
          this.followForm.testisRightTwo = temBc['testisRightTwo'];
          this.followForm.testisRightThr = temBc['testisRightThr'];
          this.followForm.testisRightLon = temBc['testisRightLon'];
          this.followForm.otherImageNames = data.other_ima_name 
            ? (() => {
                try {
                  // 替换所有单引号为双引号后再解析
                  return JSON.parse(data.other_ima_name.replace(/'/g, '"'));
                } catch (e) {
                  return {}; // 解析失败则赋值空对象
                }
              })()
            : {};
          // let str = data.dia_trea_plan.replace(/"otherMedicine":.*?(,|$)/g, '');
          // const fixedStr = str.replace(
          //   /("genData":")(\[.*?\])(")/g,  // 匹配genData的完整值
          //   (match, prefix, content, suffix) => {
          //     // 转义content里的双引号：" → \"
          //     const escapedContent = content.replace(/"/g, '\\"');
          //     return prefix + escapedContent + suffix;
          //   }
          // );
          // console.log(fixedStr);
          // let temDia = fixedStr ? JSON.parse(fixedStr) : {};
          let strData = data.dia_trea_plan.replace(/"otherMedicine":.*?(,|$)/g, '');
          // console.log(strData);
          let temDia = {};
          try {
            // 1. 拿到原始字符串
            let str = strData;

            // 修复 genData/planData 外层多余引号
            str = str
            .replace(/\\/g, '')             // 1. 去掉所有转义符 \
            .replace(/"{4}/g, '""')         // 2. 把 """" 变成合法的 ""
            .replace(/"null"/g, '""')       // 3. 把 "null" 变成空字符串
            .replace(/"otherMedicine":.*?(,|$)/g, '') // 4. 删掉 otherMedicine 字段
            .replace(/"\[/g, "[")   // 去掉数组前的 "
            .replace(/\]"/g, "]")  // 去掉数组后的 "
            .replace(/\\"/g, '"') // 把转义引号 \" 变成正常 "
            .replace(/"genData":""""""/g, '"genData":""')
            .replace(/"planData":"null"/g, '"planData":""');

            // 3. 解析
            temDia = JSON.parse(str);
          } catch (e) {
            console.error("解析失败", e);
            temDia = { diaPlan: "" };
          }
          this.followForm.diaPlan = temDia['diaPlan'];
          this.followForm.rhGH = temDia['rhGH'];
          this.followForm.rhGHdose = temDia['rhGHdose'];
          // this.genData = JSON.parse(temDia['genData'] || '[]');
          this.genData = temDia['genData'] ? temDia['genData'] :[]
          this.followForm.rhGHdoseKG=temDia['rhGHdoseKG'];
          this.followForm.PEGrhGHdose=temDia['PEGrhGHdose'];
          this.followForm.PEGrhGHdoseKG=temDia['PEGrhGHdoseKG'];
          this.followForm.rhCustomizationPromptKG=temDia['rhCustomizationPromptKG'];
          this.followForm.PEGrhCustomizationPromptKG=temDia['PEGrhCustomizationPromptKG'];
          this.followForm.PEGrhCustomizationPrompt=temDia['PEGrhCustomizationPrompt'];
          this.followForm.PEGrhGHcalculate=temDia['PEGrhGHcalculate'];
          // console.log( this.genData ,"genData");
          // this.followForm.anstrozole = temDia['anstrozole'];
          this.followForm.rhUnitedCustomization = temDia['rhUnitedCustomization'];
          this.followForm.rhUnitedDose = temDia['rhUnitedDose'];
          this.followForm.rhCustomizationDiaPlan = temDia['rhCustomizationDiaPlan'];
          this.followForm.rhCustomizationPrompt = temDia['rhCustomizationPrompt'];
          this.followForm.other = data.other;
          // console.log(JSON.parse(`"${data.dia_trea_plan.match(/"otherMedicine":""(.*?)""/)?.[1]?.replace(/\\\\/g,'\\')||''}"`));
          this.followForm.otherMedicine =JSON.parse(`"${data.dia_trea_plan.match(/"otherMedicine":""(.*?)""/)?.[1]?.replace(/\\\\/g,'\\')||''}"`);
          this.followForm.LH = data.LH;
          this.followForm.FSH = data.FSH;
          let labEOther = data.lab_exa_other ? JSON.parse(data.lab_exa_other) : {};
          this.followForm.glyHemA = labEOther['glyHemA'] || '';
          this.followForm.fasBloodGluTime = labEOther['fasBloodGluTime'] || '';
          this.followForm.fasInsulinTime = labEOther['fasInsulinTime'] || '';
          this.followForm.glyHemTime = labEOther['glyHemTime'] || '';
          this.followForm.glyHemATime = labEOther['glyHemATime'] || '';
          // this.followForm.otherMedicine = labEOther['otherMedicine'] || '';
          this.followForm.LHFSHTime =  labEOther['LHFSHTime'] || '';
          this.followForm.E2 = data.E2;
          this.followForm.E2Time =  labEOther['E2Time'] || '';
          this.followForm.T = data.T;
          this.followForm.TTime =  labEOther['TTime'] || '';
          this.followForm.PRL =  labEOther['PRL'] === 'null' || labEOther['PRL'] == null ? '' : labEOther['PRL'];
          this.followForm.PRLTime =  labEOther['PRLTime'] || '';
          this.followForm.DHT = data.DHT;
          this.followForm.FT = data.FT;
          this.followForm.SHBG = data.SHBG;
          this.followForm.IGF1 = data.IGF1;
          this.followForm.IGFBP3 = data.IGFBP3;
          this.followForm.IGFBPTime =  labEOther['IGFBPTime'] || '';
          this.followForm.thyroidTime =  labEOther['thyroidTime'] || '';
          this.followForm.ACTH =  labEOther['ACTH'] === 'null' || labEOther['ACTH'] == null ? '' : labEOther['ACTH'];
          this.followForm.ACTHTime = labEOther['ACTHTime'] || '';
          this.followForm.cortisol = labEOther['cortisol'] === 'null' || labEOther['cortisol'] == null ? '' : labEOther['cortisol'];
          this.followForm.cortisolTime =  labEOther['cortisolTime'] || '';
          this.followForm.DHEAS = labEOther['DHEAS'] === 'null' || labEOther['DHEAS'] == null ? '' : labEOther['cortisol'];
          this.followForm.DHEATime =  labEOther['DHEATime'] || '';
          this.followForm.OHP = labEOther['OHP'] === 'null' || labEOther['OHP'] == null ? '' : labEOther['OHP'];
          this.followForm.OHPTime =  labEOther['OHPTime'] || '';
          this.followForm.blood = labEOther['blood'];
          this.followForm.bloodDescribe = labEOther['bloodDescribe'] === 'null' || labEOther['bloodDescribe'] == null ? '' : labEOther['bloodDescribe'];
          this.followForm.bloodTime =  labEOther['bloodTime'] || '';
          this.followForm.urinalysis = labEOther['urinalysis'];
          this.followForm.urinalysisDescribe = labEOther['urinalysisDescribe'] === 'null' || labEOther['urinalysisDescribe'] == null ? '' : labEOther['urinalysisDescribe'];
          this.followForm.urinalysisTime =  labEOther['urinalysisTime'] || '';
          this.followForm.LAKLGETime =  labEOther['LAKLGETime'] || '';
          this.followForm.HBs = labEOther['HBs'];
          this.followForm.HBsTime =  labEOther['HBsTime'] || '';
          this.followForm.gh = labEOther['gh'] === 'null' || labEOther['gh'] == null ? '' : labEOther['gh'];
          this.followForm.isFinalHeight =  data.is_finalhei ? data.is_finalhei : "";
          this.followForm.ghTime =  labEOther['ghTime'] || '';
          let tempData = data.image ? JSON.parse(data.image) : {};
          this.applyImagePath(tempData);
          this.getAge()
        }, error => {
          console.log(error)
        })
      },

      // applyImagePath(imagePath) {
      //   this.ImageList.forEach((item, i) => {
      //     if (item.title in imagePath) {
      //       if (imagePath[item.title].length > 0) {
      //         item.imageUrl = imagePath[item.title];
      //         if (this.isStatic == false) {
      //           if ((item.title ==='腰椎正侧位片' || item.title ==='胸椎正侧位片')  && item.imageUrl.length === 1) {
      //             item.imageUrl = item.imageUrl.concat('')
      //           }
      //         }
      //       }
      //     }
      //     this.$set(this.ImageList, i, item);
      //   })
      // },
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

      clear(){
        const t = this.followForm.queryId;
        for (let key in this.followForm) {
          this.followForm[key] = ''
        }
        this.followForm.queryId = t;
        this.ImageList = JSON.parse(ImageString);
        this.$forceUpdate()
      },
      validateHeightFun(){
        validateHeightByBirthdate(this.birthTime,this.followForm.Ht)
      },
      validateWeightFun(){
        const weight = Number(this.followForm.Wt) || 0;
        if(weight>80 || weight < 10){
          this.$message({
              message: '检查体重是否有误',
              type: 'warning'
            });
        }
      }
    },
  }
</script>

<style  lang="less" scoped>

  .input-underLine{
        width: 4vw;
        border: 0;
        border-bottom: 1px blueviolet solid;
        outline: none;
        text-align: center
      }
      .fss-box{
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
      }

</style>
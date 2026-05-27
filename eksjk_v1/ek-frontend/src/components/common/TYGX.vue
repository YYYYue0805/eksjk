<template>
    <div class="TYGXBox">
        <el-scrollbar class="scrollThy">
            <el-tabs :value="this.activeName">
                <el-tab-pane name="one" :style="{ pointerEvents }">
                    <span slot="label">基本信息填写</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">填表人和孩子的关系:
                                    <el-select size="small" v-model="ruleForm.ties" :disabled="isStatic">
                                        <el-option label="本人" value="1"></el-option>
                                        <el-option label="父母" value="2"></el-option>
                                        <el-option label="其他" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">编号:
                                    <input v-model="ruleForm.number" :disabled="isStatic" class="input-underLine" style="width: 320px" />
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">班级:
                                    <input v-model="ruleForm.class" :disabled="isStatic" class="input-underLine" style="width: 320px" />
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">姓名:
                                    <input v-model="ruleForm.name" :disabled="isStatic" class="input-underLine" style="width: 320px" />
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">性别:
                                    <el-select size="small" v-model="ruleForm.sex" :disabled="isStatic"
                                        style="width: 350px">
                                        <el-option label="男" value="1"></el-option>
                                        <el-option label="女" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">出生日期:
                                    <el-date-picker style="width: 290px" v-model="ruleForm.birthDate" type="date"  :disabled="isStatic"
                                        size="small" :clearable="false" :picker-options="pickerOptions" placeholder="出生日期"
                                        value-format="yyyy-MM-dd"></el-date-picker>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">当前身高:
                                    <input v-model="ruleForm.height" class="input-underLine" style="width: 260px" />CM
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">当前体重:
                                    <input v-model="ruleForm.weight" class="input-underLine" style="width: 260px" />KG
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">家庭情况：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">母亲受教育程度:
                                    <el-select size="small" v-model="ruleForm.motherEducation" :disabled="isStatic"
                                        style="width: 240px">
                                        <el-option label="小学及以下" value="1"></el-option>
                                        <el-option label="初中" value="2"></el-option>
                                        <el-option label="高中" value="3"></el-option>
                                        <el-option label="专科" value="4"></el-option>
                                        <el-option label="本科" value="5"></el-option>
                                        <el-option label="研究生" value="6"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">父亲受教育程度:
                                    <el-select size="small" v-model="ruleForm.fatherEducation" :disabled="isStatic"
                                        style="width: 240px">
                                        <el-option label="小学及以下" value="1"></el-option>
                                        <el-option label="初中" value="2"></el-option>
                                        <el-option label="高中" value="3"></el-option>
                                        <el-option label="专科" value="4"></el-option>
                                        <el-option label="本科" value="5"></el-option>
                                        <el-option label="研究生" value="6"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">家庭年收入:
                                    <input v-model="ruleForm.familyIncome" class="input-underLine" style="width: 260px" />
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">孩子的主要照护人:
                                    <input v-model="ruleForm.mainCaregiver" class="input-underLine" style="width: 210px" />
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">主要照护人受教育程度:
                                    <el-select size="small" v-model="ruleForm.mainCEducation" :disabled="isStatic"
                                        style="width: 180px">
                                        <el-option label="小学及以下" value="1"></el-option>
                                        <el-option label="初中" value="2"></el-option>
                                        <el-option label="高中" value="3"></el-option>
                                        <el-option label="专科" value="4"></el-option>
                                        <el-option label="本科" value="5"></el-option>
                                        <el-option label="研究生" value="6"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">是否有兄弟姐妹:
                                    <el-select size="small" v-model="ruleForm.hasSiblings" :disabled="isStatic"
                                        style="width: 240px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>

                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">孕期情况：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">医生诊断为妊娠期糖尿病:
                                    <el-select size="small" v-model="ruleForm.gdm" :disabled="isStatic"
                                        style="width: 160px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">医生诊断为妊娠期高血压:
                                    <el-select size="small" v-model="ruleForm.ghp" :disabled="isStatic"
                                        style="width: 160px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="9" :offset="1">
                                <p class="TYGXBox-title">精神压力大或者情绪问题且需要专业人员帮助:
                                    <el-select size="small" v-model="ruleForm.mental" :disabled="isStatic"
                                        style="width: 260px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">医生诊断为营养不良:
                                    <el-select size="small" v-model="ruleForm.nutrition" :disabled="isStatic"
                                        style="width: 200px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">出生史：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">分娩方式:
                                    <el-select size="small" v-model="ruleForm.deliveryMode" :disabled="isStatic"
                                        style="width: 300px">
                                        <el-option label="顺产" value="1"></el-option>
                                        <el-option label="剖宫产" value="2"></el-option>
                                        <el-option label="其他" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">出生体重:
                                    <input v-model="ruleForm.birthWeight" :disabled="isStatic" class="input-underLine" style="width: 260px" />g
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">出生孕周:
                                    <input v-model="ruleForm.birthGAge" :disabled="isStatic" class="input-underLine" style="width: 260px" />周
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">出生时是否发生窒息或抢救:
                                    <el-select size="small" v-model="ruleForm.asRescue" :disabled="isStatic"
                                        style="width: 160px">
                                        <el-option label="是" value="1"></el-option>
                                        <el-option label="否" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">喂养史：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="6">
                                <p class="TYGXBox-title">出生后喂养方式:
                                    <el-select size="small" v-model="ruleForm.birthFMethod" :disabled="isStatic"
                                        style="width: 240px">
                                        <el-option label="母乳" value="1"></el-option>
                                        <el-option label="人工" value="2"></el-option>
                                        <el-option label="混合喂养" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">断母乳时间:
                                    <el-date-picker style="width: 280px" v-model="ruleForm.weanBTime" type="date" :disabled="isStatic"
                                        size="small" :clearable="false" :picker-options="pickerOptions" placeholder="出生日期"
                                        value-format="yyyy-MM-dd"></el-date-picker>
                                </p>
                            </el-col>
                            <el-col :span="6">
                                <p class="TYGXBox-title">添加辅食时间:
                                    <el-date-picker style="width: 260px" v-model="ruleForm.addCFTime" type="date" :disabled="isStatic"
                                        size="small" :clearable="false" :picker-options="pickerOptions" placeholder="出生日期"
                                        value-format="yyyy-MM-dd"></el-date-picker>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="two" :style="{ pointerEvents }">
                    <span slot="label">身体活动问卷</span>
                    <div class="div-box">
                        <p class="TYGXBox-title">
                            最近7天，您孩子参加的“中强度身体活动”的频率（每周几天）中高强度身体活动指需要适度的体力消耗，如快走、骑自行车、跑步、打球、游泳、跳舞等，在此过程中儿童身上发热，呼吸急促:
                            <input v-model="ruleForm.physicalAFreq" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            在参加中高强度身体活动的那几天，您孩子通常每天花多少时间来做中高强度身体活动？每次至少持续10分钟以上:
                            <input v-model="ruleForm.physicalATime" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            最近7天，您孩子参加的“低强度身体活动”的频率（每周几天）低强度身体活动时间，如缓慢步行、洗澡或其它不会导致儿童身上发热或呼吸急促的偶然活动：
                            <input v-model="ruleForm.lowPAFreq" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            在参加低强度身体活动的那几天，您孩子通常每天花多少时间来做低强度身体活动？每次至少持续10分钟以上：
                            <input v-model="ruleForm.lowPATime" class="input-underLine" style="width: 320px" />小时
                        </p>
                        <p class="TYGXBox-title">
                            最近7天，您孩子静坐的频率（每周几天）静坐时间，如束缚在汽车安全座椅、高脚椅、手推车、婴儿车、婴儿背带或看护者背上的时间，也包括静坐听故事的时间：
                            <input v-model="ruleForm.sittingFreq" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            每天静坐的时间：
                            <input v-model="ruleForm.sittingTime" class="input-underLine" style="width: 320px" />小时
                        </p>
                        <p class="TYGXBox-title">
                            最近7天，您孩子非看屏幕的静坐频率（每周几天）如躺在垫子上，坐在高脚椅上、婴儿车或手推车中而几乎不动，坐着看书或坐着玩游戏：
                            <input v-model="ruleForm.nonSFreq" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            每天非看屏幕的静坐时间如躺在垫子上，坐在高脚椅上、婴儿车或手推车中而几乎不动，坐着看书或坐着玩游戏：
                            <input v-model="ruleForm.nonSSTime" class="input-underLine" style="width: 320px" />小时
                        </p>
                        <p class="TYGXBox-title">
                            最近7天，您孩子屏幕前静坐的频率（每周几天）如被动地观看屏幕娱乐节目（电视、计算机、移动设备）的时间,不包括需要进行身体活动或运动的积极屏幕游戏：
                            <input v-model="ruleForm.screenSFreq" class="input-underLine" style="width: 320px" />天
                        </p>
                        <p class="TYGXBox-title">
                            每天屏幕前静坐的时间如被动地观看屏幕娱乐节目（电视、计算机、移动设备）的时间,不包括需要进行身体活动或运动的积极屏幕游戏：
                            <input v-model="ruleForm.screenSTime" class="input-underLine" style="width: 320px" />小时
                        </p>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="three" :style="{ pointerEvents }">
                    <span slot="label">长处和困难问卷</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">能体谅到别人的感受：
                                    <el-select size="small" v-model="ruleForm.isPardon" :disabled="isStatic"
                                        style="width: 600px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不安定、过分活跃、不能长久安静：
                                    <el-select size="small" v-model="ruleForm.isActive" :disabled="isStatic"
                                        style="width: 500px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>

                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常抱怨头痛、肚子痛或身体不舒服：
                                    <el-select size="small" v-model="ruleForm.isHeadache" :disabled="isStatic"
                                        style="width: 464px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">很乐意与别的小孩分享东西，比如糖果、玩具、铅笔等等：
                                    <el-select size="small" v-model="ruleForm.isShare" :disabled="isStatic"
                                        style="width: 310px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常发脾气或大吵大闹：
                                    <el-select size="small" v-model="ruleForm.isAngry" :disabled="isStatic"
                                        style="width: 580px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">比较孤独，喜欢自己一个人玩：
                                    <el-select size="small" v-model="ruleForm.isLonely" :disabled="isStatic"
                                        style="width: 540px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">一般来说，比较顺从，通常是大人要求做的都肯做：
                                    <el-select size="small" v-model="ruleForm.isConform" :disabled="isStatic"
                                        style="width: 350px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">有很多担忧，经常表现出忧虑：
                                    <el-select size="small" v-model="ruleForm.isWorry" :disabled="isStatic"
                                        style="width: 540px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">如果有人受伤，不舒服或是生病，都很乐意提供帮助：
                                    <el-select size="small" v-model="ruleForm.isHelp" :disabled="isStatic"
                                        style="width: 330px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常的坐立不安或躁动：
                                    <el-select size="small" v-model="ruleForm.isAnxious" :disabled="isStatic"
                                        style="width: 600px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">有一个或一个以上的好朋友：
                                    <el-select size="small" v-model="ruleForm.isFriend" :disabled="isStatic"
                                        style="width: 540px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常与别的小孩吵架或欺负其他小孩子：
                                    <el-select size="small" v-model="ruleForm.isArrest" :disabled="isStatic"
                                        style="width: 470px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常不高兴、情绪低落或哭泣：
                                    <el-select size="small" v-model="ruleForm.isSad" :disabled="isStatic"
                                        style="width: 520px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">一般来说，受别的小孩所喜欢：
                                    <el-select size="small" v-model="ruleForm.isLike" :disabled="isStatic"
                                        style="width: 550px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">容易分心，注意力不集中：
                                    <el-select size="small" v-model="ruleForm.isDistracted" :disabled="isStatic"
                                        style="width: 560px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在新环境下，会紧张或粘住大人，容易失去信心：
                                    <el-select size="small" v-model="ruleForm.isTight" :disabled="isStatic"
                                        style="width: 400px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">爱对年纪小的儿童和善：
                                    <el-select size="small" v-model="ruleForm.isYoung" :disabled="isStatic"
                                        style="width: 580px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常撒谎或欺骗：
                                    <el-select size="small" v-model="ruleForm.isLie" :disabled="isStatic"
                                        style="width: 670px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">受别的小孩捉弄或欺负：
                                    <el-select size="small" v-model="ruleForm.isBother" :disabled="isStatic"
                                        style="width: 580px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常自愿的帮助别人（父母、老师或其他小孩）：
                                    <el-select size="small" v-model="ruleForm.isHelpful" :disabled="isStatic"
                                        style="width: 410px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">做事前会想清楚：
                                    <el-select size="small" v-model="ruleForm.isThink" :disabled="isStatic"
                                        style="width: 638px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">会从事家里、学校或其他地方偷东西：
                                    <el-select size="small" v-model="ruleForm.isSteal" :disabled="isStatic"
                                        style="width: 500px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">跟大人相处比跟小孩子相处融洽：
                                    <el-select size="small" v-model="ruleForm.isCongenial" :disabled="isStatic"
                                        style="width: 500px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对很多事情容易感到害怕，容易受惊吓：
                                    <el-select size="small" v-model="ruleForm.isScary" :disabled="isStatic"
                                        style="width: 480px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">做事情能做到底，注意力持久：
                                    <el-select size="small" v-model="ruleForm.isObedient" :disabled="isStatic"
                                        style="width: 520px">
                                        <el-option label="不符合" value="1"></el-option>
                                        <el-option label="有点符合" value="2"></el-option>
                                        <el-option label="完全符合" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="four" :style="{ pointerEvents }">
                    <span slot="label">儿童气质问卷（CBQ-SF）</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">似乎总是匆匆忙忙地从一个地方到另一个地方
                                    <el-select size="small" v-model="ruleForm.isQuick" :disabled="isStatic"
                                        style="width: 470px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">当被阻止做想做的事时，会变得非常失落
                                    <el-select size="small" v-model="ruleForm.isLost" :disabled="isStatic"
                                        style="width: 480px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在书上画图或涂色时表现得非常专注
                                    <el-select size="small" v-model="ruleForm.isFocus" :disabled="isStatic"
                                        style="width: 544px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">喜欢滑高的滑梯或其它冒险性活动
                                    <el-select size="small" v-model="ruleForm.isAdventure" :disabled="isStatic"
                                        style="width: 540px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">会因为很小的切伤或擦伤而非常不安
                                    <el-select size="small" v-model="ruleForm.isScratched" :disabled="isStatic"
                                        style="width: 544px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">能为旅行或外出准备他/她需要的东西
                                    <el-select size="small" v-model="ruleForm.isPrepare" :disabled="isStatic"
                                        style="width: 514px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">经常贸然进入新情境(比如，鲁莽地到没去过的地方去玩儿，或者仓促地参加新的游戏)
                                    <el-select size="small" v-model="ruleForm.isEnter" :disabled="isStatic"
                                        style="width: 130px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">如果家庭计划没有兑现（比如，外出旅行没有按计划实施）
                                    <el-select size="small" v-model="ruleForm.isPlan" :disabled="isStatic"
                                        style="width: 330px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">喜欢别人对他唱歌
                                    <el-select size="small" v-model="ruleForm.isSing" :disabled="isStatic"
                                        style="width: 700px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">好像对任何人都不拘束
                                    <el-select size="small" v-model="ruleForm.isBind" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">害怕夜贼或者“大灰狼”
                                    <el-select size="small" v-model="ruleForm.isFear" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">当父母穿了新衣服时，能注意到
                                    <el-select size="small" v-model="ruleForm.isNotice" :disabled="isStatic"
                                        style="width: 564px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">相对于活跃性游戏，更喜欢安静的活动
                                    <el-select size="small" v-model="ruleForm.isQuiet" :disabled="isStatic"
                                        style="width: 524px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对某事生气时，往往要持续十分钟或更长时间
                                    <el-select size="small" v-model="ruleForm.isConstantA" :disabled="isStatic"
                                        style="width: 450px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">当搭建或者拼凑某些东西时，能够非常投入且坚持很长时间
                                    <el-select size="small" v-model="ruleForm.isBuild" :disabled="isStatic"
                                        style="width: 350px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在荡秋千时喜欢又高又快
                                    <el-select size="small" v-model="ruleForm.isSwing" :disabled="isStatic"
                                        style="width: 624px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在不能完成某些任务时似乎很沮丧
                                    <el-select size="small" v-model="ruleForm.isFrustrated" :disabled="isStatic"
                                        style="width: 560px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">善于按照要求行动
                                    <el-select size="small" v-model="ruleForm.isFollow" :disabled="isStatic"
                                        style="width: 684px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">需要花很长的时间适应新的环境
                                    <el-select size="small" v-model="ruleForm.isAdapt" :disabled="isStatic"
                                        style="width: 580px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在感冒时很少抱怨
                                    <el-select size="small" v-model="ruleForm.isComplain" :disabled="isStatic"
                                        style="width: 684px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">喜欢歌曲，比如童谣
                                    <el-select size="small" v-model="ruleForm.isListen" :disabled="isStatic"
                                        style="width: 674px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">即使在认识了很长时间的人面前，有时也会害羞
                                    <el-select size="small" v-model="ruleForm.isShy" :disabled="isStatic"
                                        style="width: 434px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在烦躁时，很容易被安抚下来
                                    <el-select size="small" v-model="ruleForm.isRelax" :disabled="isStatic"
                                        style="width: 598px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">能很快注意到客厅里的新东西
                                    <el-select size="small" v-model="ruleForm.isNoticeNew" :disabled="isStatic"
                                        style="width: 590px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">即使在晚上，也精力充沛
                                    <el-select size="small" v-model="ruleForm.isEnergetic" :disabled="isStatic"
                                        style="width: 636px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不害怕黑夜
                                    <el-select size="small" v-model="ruleForm.isFearNight" :disabled="isStatic"
                                        style="width: 744px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">有时会专注于图画书很长时间
                                    <el-select size="small" v-model="ruleForm.isFocusDate" :disabled="isStatic"
                                        style="width: 596px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不喜欢粗野的游戏
                                    <el-select size="small" v-model="ruleForm.isAvoidWild" :disabled="isStatic"
                                        style="width: 688px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对轻微切伤或擦伤并不十分心烦
                                    <el-select size="small" v-model="ruleForm.isAvoidDamage" :disabled="isStatic"
                                        style="width: 578px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">到听说有危险的地方时会小心翼翼
                                    <el-select size="small" v-model="ruleForm.isAvoidDanger" :disabled="isStatic"
                                        style="width: 556px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">会缓慢而不匆忙地决定接下来要做的事
                                    <el-select size="small" v-model="ruleForm.isSlowlyDecide" :disabled="isStatic"
                                        style="width: 520px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">当不能找到他/她想玩的东西时会生气
                                    <el-select size="small" v-model="ruleForm.isTAngry" :disabled="isStatic"
                                        style="width: 530px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">喜欢柔和有节拍的活动，比如摇摆
                                    <el-select size="small" v-model="ruleForm.isSoftBeat" :disabled="isStatic"
                                        style="width: 560px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">有时会对新认识的人害羞地转过脸去
                                    <el-select size="small" v-model="ruleForm.isNewShy" :disabled="isStatic"
                                        style="width: 540px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">
                                    当喜欢的亲戚或朋友在来访后准备离开时，变得烦躁
                                    <el-select size="small" v-model="ruleForm.isLikeAngry" :disabled="isStatic"
                                        style="width: 404px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">会对父母外表的变化做评价
                                    <el-select size="small" v-model="ruleForm.isEvaluateParent" :disabled="isStatic"
                                        style="width: 618px">
                                        <el-option label="非常恰当" value="1"></el-option>
                                        <el-option label="很不恰当" value="2"></el-option>
                                        <el-option label="有点不恰当" value="3"></el-option>
                                        <el-option label="不确定" value="4"></el-option>
                                        <el-option label="有点恰当" value="5"></el-option>
                                        <el-option label="很恰当" value="6"></el-option>
                                        <el-option label="非常恰当" value="7"></el-option>
                                        <el-option label="不适用" value="8"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="five" :style="{ pointerEvents }">
                    <span slot="label">母亲照养方式问卷</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">只要孩子不高兴，犯了错误，也不批评。
                                    <el-select size="small" v-model="ruleForm.isNotCriticize" :disabled="isStatic"
                                        style="width: 500px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子要什么就给什么。
                                    <el-select size="small" v-model="ruleForm.isGiveWhat" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子是否服从自己无所谓。
                                    <el-select size="small" v-model="ruleForm.isSubordinate" :disabled="isStatic"
                                        style="width: 616px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">只要孩子高兴，可以不惜一切。
                                    <el-select size="small" v-model="ruleForm.isGivingAll" :disabled="isStatic"
                                        style="width: 564px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子不服从家长时打骂孩子。
                                    <el-select size="small" v-model="ruleForm.isJugdeChild" :disabled="isStatic"
                                        style="width: 596px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子犯了错误并不在乎。
                                    <el-select size="small" v-model="ruleForm.isNotCare" :disabled="isStatic"
                                        style="width: 602px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子一点点异常过分着急。
                                    <el-select size="small" v-model="ruleForm.isTooMuch" :disabled="isStatic"
                                        style="width: 596px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">鼓励孩子做他会做的事。
                                    <el-select size="small" v-model="ruleForm.isEncourage" :disabled="isStatic"
                                        style="width: 622px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子做不好的事情替他做。
                                    <el-select size="small" v-model="ruleForm.isReplace" :disabled="isStatic"
                                        style="width: 612px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">根据孩子本人的兴趣培养他的特长。
                                    <el-select size="small" v-model="ruleForm.isInterest" :disabled="isStatic"
                                        style="width: 526px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子的哭闹，有时查问清楚，有时拒绝。
                                    <el-select size="small" v-model="ruleForm.isCrying" :disabled="isStatic"
                                        style="width: 478px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在和孩子谈话时允许孩子插话提问。
                                    <el-select size="small" v-model="ruleForm.isAllow" :disabled="isStatic"
                                        style="width: 526px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">要求孩子做什么事都必须报告家长。
                                    <el-select size="small" v-model="ruleForm.isReport" :disabled="isStatic"
                                        style="width: 534px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">当孩子做错时问明原因再批评。
                                    <el-select size="small" v-model="ruleForm.isReason" :disabled="isStatic"
                                        style="width: 564px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子缠着问这问那不耐烦。
                                    <el-select size="small" v-model="ruleForm.isPity" :disabled="isStatic"
                                        style="width: 610px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不向孩子做任何承诺。
                                    <el-select size="small" v-model="ruleForm.isPromise" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">吩咐孩子做事时让孩子明白为什么或怎么做。
                                    <el-select size="small" v-model="ruleForm.isExplain" :disabled="isStatic"
                                        style="width: 456px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子的学习、生活有时关心，有时不关心。
                                    <el-select size="small" v-model="ruleForm.isCare" :disabled="isStatic"
                                        style="width: 450px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不注意孩子在做什么或怎么做。
                                    <el-select size="small" v-model="ruleForm.isCNotice" :disabled="isStatic"
                                        style="width: 570px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">自己忙的时候不理睬孩子的提问。
                                    <el-select size="small" v-model="ruleForm.isBusy" :disabled="isStatic"
                                        style="width: 546px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子想怎么样就怎么样。
                                    <el-select size="small" v-model="ruleForm.isWish" :disabled="isStatic"
                                        style="width: 626px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不切实际地表扬孩子。
                                    <el-select size="small" v-model="ruleForm.isPraise" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不了解孩子不和父母在一起时具体做什么。
                                    <el-select size="small" v-model="ruleForm.isDo" :disabled="isStatic"
                                        style="width: 470px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">以适当的方式表扬或奖励孩子。
                                    <el-select size="small" v-model="ruleForm.isReward" :disabled="isStatic"
                                        style="width: 564px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子无理要求，有时满足，有时拒绝。
                                    <el-select size="small" v-model="ruleForm.isReq" :disabled="isStatic"
                                        style="width: 486px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子没有惩罚或奖励
                                    <el-select size="small" v-model="ruleForm.isNoReward" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在孩子学习或做其他事遇到困难时帮助他解决。
                                    <el-select size="small" v-model="ruleForm.isCHelp" :disabled="isStatic"
                                        style="width: 430px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">看着孩子做事情并随时指点。
                                    <el-select size="small" v-model="ruleForm.isPoint" :disabled="isStatic"
                                        style="width: 584px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子提出的问题予以认真解答。
                                    <el-select size="small" v-model="ruleForm.isAnswer" :disabled="isStatic"
                                        style="width: 546px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">同样一件事情，有时允许，有时拒绝。
                                    <el-select size="small" v-model="ruleForm.isSame" :disabled="isStatic"
                                        style="width: 508px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">对孩子不讲是非。
                                    <el-select size="small" v-model="ruleForm.isNoJudge" :disabled="isStatic"
                                        style="width: 680px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子和谁在一起经过家长同意。
                                    <el-select size="small" v-model="ruleForm.isAgree" :disabled="isStatic"
                                        style="width: 564px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">同孩子一起消遣、游戏。
                                    <el-select size="small" v-model="ruleForm.isGame" :disabled="isStatic"
                                        style="width: 620px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">要求孩子做什么事必讲明原因或怎么做。
                                    <el-select size="small" v-model="ruleForm.isCDo" :disabled="isStatic"
                                        style="width: 490px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子做了错事，有时批评，有时无所谓。
                                    <el-select size="small" v-model="ruleForm.isCritic" :disabled="isStatic"
                                        style="width: 486px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">不关心孩子的生活小事。
                                    <el-select size="small" v-model="ruleForm.isNoCare" :disabled="isStatic"
                                        style="width: 626px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">通过说理使孩子服从。
                                    <el-select size="small" v-model="ruleForm.isCSub" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">有时说服孩子，有时强制孩子。
                                    <el-select size="small" v-model="ruleForm.isCForce" :disabled="isStatic"
                                        style="width: 570px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子在家里随便做自己的事情，家长没有具体要求。
                                    <el-select size="small" v-model="ruleForm.isDoReq" :disabled="isStatic"
                                        style="width: 390px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">培养孩子哪方面特长由家长决定。
                                    <el-select size="small" v-model="ruleForm.isSpecial" :disabled="isStatic"
                                        style="width: 552px">
                                        <el-option label="从不" value="1"></el-option>
                                        <el-option label="很少" value="2"></el-option>
                                        <el-option label="有时" value="3"></el-option>
                                        <el-option label="经常" value="4"></el-option>
                                        <el-option label="总是" value="5"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="six" :style="{ pointerEvents }">
                    <span slot="label">亲子活动问卷</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">与孩子一起阅读、看图画书
                                    <el-select size="small" v-model="ruleForm.isRead" :disabled="isStatic"
                                        style="width: 600px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">在生活中教孩子数的概念
                                    <el-select size="small" v-model="ruleForm.isCount" :disabled="isStatic"
                                        style="width: 640px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">涂涂画画
                                    <el-select size="small" v-model="ruleForm.isDraw" :disabled="isStatic"
                                        style="width: 754px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">跟孩子一起玩开发智力的游戏(如搭积木、图形配对、过家家等)
                                    <el-select size="small" v-model="ruleForm.isInGame" :disabled="isStatic"
                                        style="width: 320px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">结合日常生活与孩子一起识字
                                    <el-select size="small" v-model="ruleForm.isDailyR" :disabled="isStatic"
                                        style="width: 578px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">一起听唱歌曲、诗歌、童谣
                                    <el-select size="small" v-model="ruleForm.isTogetherL" :disabled="isStatic"
                                        style="width: 620px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">讲故事（家长讲，孩子听）
                                    <el-select size="small" v-model="ruleForm.isStory" :disabled="isStatic"
                                        style="width: 598px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">做手工
                                    <el-select size="small" v-model="ruleForm.isHand" :disabled="isStatic"
                                        style="width: 794px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">做运动
                                    <el-select size="small" v-model="ruleForm.isSport" :disabled="isStatic"
                                        style="width: 770px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">教孩子生活自理技能，如吃饭、穿衣等
                                    <el-select size="small" v-model="ruleForm.isSelf" :disabled="isStatic"
                                        style="width: 524px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">与孩子谈论周围发生的一些事
                                    <el-select size="small" v-model="ruleForm.isTalk" :disabled="isStatic"
                                        style="width: 576px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">与孩子一起认识大自然的动植物
                                    <el-select size="small" v-model="ruleForm.isNature" :disabled="isStatic"
                                        style="width: 582px">
                                        <el-option label="完全没有" value="1"></el-option>
                                        <el-option label="每周少于一次" value="2"></el-option>
                                        <el-option label="每周1次" value="3"></el-option>
                                        <el-option label="每周2次" value="4"></el-option>
                                        <el-option label="每周3次" value="5"></el-option>
                                        <el-option label="每周4-5次" value="6"></el-option>
                                        <el-option label="每周6次或以上" value="7"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="seven" :style="{ pointerEvents }">
                    <span slot="label">屏幕暴露问卷</span>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">第一次接触电子屏幕的月龄
                                    <input v-model="ruleForm.firstTouch" class="input-underLine" style="width: 380px" />
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">平均每天接触电子屏幕时间
                                    <input v-model="ruleForm.screenTime" class="input-underLine" style="width: 320px" />小时
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">矩阵填写</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">益智类或早教类视频节目时间
                                    <input v-model="ruleForm.puzzleTime" class="input-underLine" style="width: 320px" />小时
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">娱乐卡通类视频节目时间
                                    <input v-model="ruleForm.cartoonTime" class="input-underLine" style="width: 340px" />小时
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">非儿童类视频节目时间
                                    <input v-model="ruleForm.NVideoTime" class="input-underLine" style="width: 380px" />小时
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">电子游戏时间
                                    <input v-model="ruleForm.gameTime" class="input-underLine" style="width: 440px" />小时
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">上网时间（如浏览网页微信、微博等）
                                    <input v-model="ruleForm.webTime" class="input-underLine" style="width: 246px" />小时
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <div class="div-box" style="margin-top: 40px;">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">您的孩子观看电视时，您或者其他照样人陪同观看的时间
                                    <el-select size="small" v-model="ruleForm.talkTime" :disabled="isStatic"
                                        style="width: 276px">
                                        <el-option label="一半或者不到一半时间" value="1"></el-option>
                                        <el-option label="绝大多数时间" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">您的孩子观看电视时，您或者其他照样人与其交流电视内容的时间
                                    <el-select size="small" v-model="ruleForm.talkContent" :disabled="isStatic"
                                        style="width: 282px">
                                        <el-option label="一半或者不到一半时间" value="1"></el-option>
                                        <el-option label="绝大多数时间" value="2"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
                <el-tab-pane name="eight" :style="{ pointerEvents }">
                    <span slot="label">儿童睡眠习惯问卷</span>
                    <p class="box-title">就寝习惯:</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上就寝/上床时间：平时（周一至周五）：
                                    <el-time-picker size="small" v-model="ruleForm.sleepTime" format="HH:mm"
                                        placeholder="平时就寝时间" style="width: 430px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上就寝/上床时间：周末：
                                    <el-time-picker size="small" v-model="ruleForm.wakeUpTime" format="HH:mm"
                                        placeholder="周末就寝时间" style="width: 524px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上睡着时间（通常晚于就寝时间）：平时（周一至周五）：
                                    <el-time-picker size="small" v-model="ruleForm.sleepTimeWeek" format="HH:mm"
                                        placeholder="平时晚于就寝时间" style="width: 290px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上睡着时间（通常晚于就寝时间）：周末：
                                    <el-time-picker size="small" v-model="ruleForm.sleepTimeWeekend" format="HH:mm"
                                        placeholder="周末晚于就寝时间" style="width: 390px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上在固定时间上床睡觉
                                    <el-select size="small" v-model="ruleForm.sleepTimeNum" :disabled="isStatic"
                                        style="width: 578px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子上床后在20分钟内入睡
                                    <el-select size="small" v-model="ruleForm.fallTime" :disabled="isStatic"
                                        style="width: 570px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子在自己床上独自入睡
                                    <el-select size="small" v-model="ruleForm.SleepAlone" :disabled="isStatic"
                                        style="width: 618px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子在他人（父母或兄弟姐妹）床上入睡
                                    <el-select size="small" v-model="ruleForm.OthSleep" :disabled="isStatic"
                                        style="width: 460px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子入睡时出现摇摆或节律性动作
                                    <el-select size="small" v-model="ruleForm.swingSway" :disabled="isStatic"
                                        style="width: 542px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子需要特定物品入睡（如玩偶、特定的毛毯等）
                                    <el-select size="small" v-model="ruleForm.sleepItem" :disabled="isStatic"
                                        style="width: 384px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子需要家长在房间陪伴才能入睡
                                    <el-select size="small" v-model="ruleForm.parentSleep" :disabled="isStatic"
                                        style="width: 542px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">到了就寝时间，孩子会准备好去睡觉
                                    <el-select size="small" v-model="ruleForm.bedtime" :disabled="isStatic"
                                        style="width: 504px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">到了就寝时间，孩子会抗拒去睡觉
                                    <el-select size="small" v-model="ruleForm.resistSleep" :disabled="isStatic"
                                        style="width: 542px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">到了就寝时间，孩子会挣扎（如哭闹、拒绝待在床上等）
                                    <el-select size="small" v-model="ruleForm.struggle" :disabled="isStatic"
                                        style="width: 334px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子害怕在黑暗中睡觉
                                    <el-select size="small" v-model="ruleForm.darkSleep" :disabled="isStatic"
                                        style="width: 638px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子害怕独自一个人睡觉
                                    <el-select size="small" v-model="ruleForm.singleSleep" :disabled="isStatic"
                                        style="width: 604px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">就寝睡眠时间：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">通常孩子每天的睡眠：（包括夜间睡眠和日间小睡时间）
                                    <el-time-picker size="small" v-model="ruleForm.sleepDay" format="HH:mm"
                                        placeholder="周末就寝时间" style="width: 354px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡得太少
                                    <el-select size="small" v-model="ruleForm.sleepLess" :disabled="isStatic"
                                        style="width: 704px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡得太多
                                    <el-select size="small" v-model="ruleForm.sleepMore" :disabled="isStatic"
                                        style="width: 710px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子的睡眠适量
                                    <el-select size="small" v-model="ruleForm.SleepNormal" :disabled="isStatic"
                                        style="width: 684px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子每天的睡眠量都一样
                                    <el-select size="small" v-model="ruleForm.sleepSame" :disabled="isStatic"
                                        style="width: 610px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子晚上会尿床
                                    <el-select size="small" v-model="ruleForm.sink" :disabled="isStatic"
                                        style="width: 684px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中会说梦话
                                    <el-select size="small" v-model="ruleForm.sleepDream" :disabled="isStatic"
                                        style="width: 650px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中不安稳，常动来动去
                                    <el-select size="small" v-model="ruleForm.sleepUnstable" :disabled="isStatic"
                                        style="width: 554px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子夜间会梦游（睡眠过程中行走）
                                    <el-select size="small" v-model="ruleForm.sleepwalking" :disabled="isStatic"
                                        style="width: 516px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子夜间会移动到他人（如父母、兄弟姐妹等）的床上
                                    <el-select size="small" v-model="ruleForm.sleepMove" :disabled="isStatic"
                                        style="width: 364px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子反映睡眠中身体疼痛。如果有，说明哪里痛？
                                    <el-select size="small" v-model="ruleForm.sleepPain" :disabled="isStatic"
                                        style="width: 400px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中有磨牙现象（牙医可能告诉过您）
                                    <el-select size="small" v-model="ruleForm.sleepMouth" :disabled="isStatic"
                                        style="width: 440px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中打鼾/呼噜很响
                                    <el-select size="small" v-model="ruleForm.sleepNoise" :disabled="isStatic"
                                        style="width: 604px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中出现呼吸暂停
                                    <el-select size="small" v-model="ruleForm.sleepPause" :disabled="isStatic"
                                        style="width: 614px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子睡眠中鼻息重或气急
                                    <el-select size="small" v-model="ruleForm.breath" :disabled="isStatic"
                                        style="width: 608px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子不在家（如到亲戚家或去旅行）睡觉时有问题
                                    <el-select size="small" v-model="ruleForm.sleepUnfamiliar" :disabled="isStatic"
                                        style="width: 404px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子抱怨睡眠问题
                                    <el-select size="small" v-model="ruleForm.sleepProblem" :disabled="isStatic"
                                        style="width: 666px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子夜间醒来尖叫、出汗且无法安抚
                                    <el-select size="small" v-model="ruleForm.sleepScream" :disabled="isStatic"
                                        style="width: 520px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子被噩梦惊醒
                                    <el-select size="small" v-model="ruleForm.nightmare" :disabled="isStatic"
                                        style="width: 684px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">夜醒问题：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">夜间醒来一般总共持续：平时（周一至周五）
                                    <el-time-picker size="small" v-model="ruleForm.nightWakingUs" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 434px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">夜间醒来一般总共持续：周末：
                                    <el-time-picker size="small" v-model="ruleForm.nightWakingWe" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 554px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子夜间会醒来一次
                                    <el-select size="small" v-model="ruleForm.nightWakingOnce" :disabled="isStatic"
                                        style="width: 644px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子夜间会醒来一次以上
                                    <el-select size="small" v-model="ruleForm.nightWakingMore" :disabled="isStatic"
                                        style="width: 612px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <p class="TYGXBox-title">孩子夜间醒来后没有帮助，能自主重新入睡
                                    <el-select size="small" v-model="ruleForm.nightWakingHelp" :disabled="isStatic"
                                        style="width: 452px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                        </el-row>
                    </div>
                    <p class="box-title">晨起习惯：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨醒来的时间：平时
                                    <el-time-picker size="small" v-model="ruleForm.morningWakingUs" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 586px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨醒来的时间：周末
                                    <el-time-picker size="small" v-model="ruleForm.morningWakingWe" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 590px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨起床的时间（一般晚于醒来的时间）：平时
                                    <el-time-picker size="small" v-model="ruleForm.morningWakingUpUs" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 386px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨起床的时间（一般晚于醒来的时间）：周末
                                    <el-time-picker size="small" v-model="ruleForm.morningWakingUpWe" format="HH:mm"
                                        placeholder="夜间醒来时间" style="width: 390px">
                                    </el-time-picker>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨自己醒来
                                    <el-select size="small" v-model="ruleForm.morningWakingOnce" :disabled="isStatic"
                                        style="width: 664px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨由闹钟叫醒
                                    <el-select size="small" v-model="ruleForm.morningWakingAlarm" :disabled="isStatic"
                                        style="width: 650px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子醒来后情绪不佳
                                    <el-select size="small" v-model="ruleForm.morningWakingEmotion" :disabled="isStatic"
                                        style="width: 644px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨由他人（如家长或兄弟姐妹）叫醒
                                    <el-select size="small" v-model="ruleForm.morningWakingOther" :disabled="isStatic"
                                        style="width: 460px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨起床困难
                                    <el-select size="small" v-model="ruleForm.morningWakingDifficulty" :disabled="isStatic"
                                        style="width: 662px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨需要很长时间才能清醒
                                    <el-select size="small" v-model="ruleForm.morningWakingLongTime" :disabled="isStatic"
                                        style="width: 556px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨醒来很早
                                    <el-select size="small" v-model="ruleForm.morningWakingEarly" :disabled="isStatic"
                                        style="width: 662px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子早晨胃口很好
                                    <el-select size="small" v-model="ruleForm.morningWakingGood" :disabled="isStatic"
                                        style="width: 672px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">白天嗜睡程度：</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子日间会小睡
                                    <el-select size="small" v-model="ruleForm.daySleep" :disabled="isStatic"
                                        style="width: 674px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子在兴奋活动中突然睡着了
                                    <el-select size="small" v-model="ruleForm.daySleepAlarm" :disabled="isStatic"
                                        style="width: 576px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">孩子看起来很疲倦
                                    <el-select size="small" v-model="ruleForm.daySleepTired" :disabled="isStatic"
                                        style="width: 654px">
                                        <el-option label="通常（5~7次/周）" value="1"></el-option>
                                        <el-option label="有时（2~4次/周）" value="2"></el-option>
                                        <el-option label="偶尔（0~1次/周）" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <p class="box-title">在过去的一周中，孩子在如下情形时是否非常瞌睡或入睡?</p>
                    <div class="div-box">
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">独自玩耍
                                    <el-select size="small" v-model="ruleForm.playAlone" :disabled="isStatic"
                                        style="width: 734px">
                                        <el-option label="不瞌睡" value="1"></el-option>
                                        <el-option label="非常瞌睡" value="2"></el-option>
                                        <el-option label="入睡" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">看电视
                                    <el-select size="small" v-model="ruleForm.tv" :disabled="isStatic"
                                        style="width: 776px">
                                        <el-option label="不瞌睡" value="1"></el-option>
                                        <el-option label="非常瞌睡" value="2"></el-option>
                                        <el-option label="入睡" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <p class="TYGXBox-title">坐车
                                    <el-select size="small" v-model="ruleForm.drive" :disabled="isStatic"
                                        style="width: 772px">
                                        <el-option label="不瞌睡" value="1"></el-option>
                                        <el-option label="非常瞌睡" value="2"></el-option>
                                        <el-option label="入睡" value="3"></el-option>
                                    </el-select>                                    
                                </p>
                            </el-col>
                            <el-col :span="12">
                                <p class="TYGXBox-title">吃饭
                                    <el-select size="small" v-model="ruleForm.eat" :disabled="isStatic"
                                        style="width: 794px">
                                        <el-option label="不瞌睡" value="1"></el-option>
                                        <el-option label="非常瞌睡" value="2"></el-option>
                                        <el-option label="入睡" value="3"></el-option>
                                    </el-select>
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-scrollbar>
    </div>
</template>
<script>
import request from "../../script/request";
export default {
    name: "TYGX",
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
        if (this.default === "follow") {
            this.activeName = "five"
        }
        if (this.$route.query.follow === "follow") {
            this.activeName = "five"
        }
    },
    data() {
        return {
            activeName: 'one',
            pointerEvents: "none",
            pickerOptions: {
            disabledDate(time) {
                return time.getTime() > new Date(new Date().toLocaleDateString()).getTime();
                },
            },
            ruleForm: {
                ties: '',//填表人和孩子的关系
                number: '',//编号
                class: '',//班级
                name: '',//姓名
                sex: '',//性别
                birthDate: '',//出生日期
                height: '',//当前身高
                weight: '',//当前体重
                motherEducation: '',//母亲受教育程度
                fatherEducation: '',//父亲受教育程度
                familyIncome: '',//家庭年收入
                mainCaregiver: '',//孩子的主要照护人
                mainCEducation: '',//主要照护人受教育程度
                hasSiblings: '',//是否有兄弟姐妹
                gdm: '',//医生诊断为妊娠期糖尿病
                ghp: '',//医生诊断为妊娠期高血压
                mental: '',//精神压力大或者情绪问题且需要专业人员帮助
                nutrition: '',//医生诊断为营养不良
                deliveryMode: '',//分娩方式
                birthWeight: '',//出生体重
                birthGAge: '',//出生孕周
                asRescue: '',//出生时是否发生窒息或抢救
                birthFMethod: '',//出生后喂养方式
                weanBTime: '',//断母乳时间
                addCFTime: '',//添加辅食时间
                physicalAFreq: '',//中高强度身体活动频率
                physicalATime: '',//中高强度身体活动时间
                lowPAFreq: '',//低强度身体活动频率
                lowPATime: '',//低强度身体活动时间
                sittingFreq: '',//静坐频率
                sittingTime: '',//静坐时间
                nonSFreq: '',//非看屏幕的静坐频率
                nonSSTime: '',//非看屏幕的静坐时间
                screenSFreq: '',//屏幕前静坐的频率
                screenSTime: '',//屏幕前静坐的时间
                isPardon: '',//能体谅到别人的感受
                isActive: '',//不安定、过分活跃、不能长久安静
                isHeadache: '',//经常抱怨头痛、肚子痛或身体不舒服
                isShare: '',//很乐意与别的小孩分享东西，比如糖果、玩具、铅笔等等
                isAngry: '',//经常发脾气或大吵大闹
                isLonely: '',//比较孤独，喜欢自己一个人玩
                isConform: '',//一般来说，比较顺从，通常是大人要求做的都肯做
                isWorry: '',//有很多担忧，经常表现出忧虑
                isHelp: '',//如果有人受伤，不舒服或是生病，都很乐意提供帮助
                isAnxious: '',//经常的坐立不安或躁动
                isFriend: '',//有一个或一个以上的好朋友
                isArrest: '',//经常与别的小孩吵架或欺负其他小孩子
                isSad: '',//经常不高兴、情绪低落或哭泣
                isLike: '',//一般来说，受别的小孩所喜欢
                isDistracted: '',//容易分心，注意力不集中
                isTight: '',//在新环境下，会紧张或粘住大人，容易失去信心
                isYoung: '',//爱对年纪小的儿童和善
                isLie: '',//经常撒谎或欺骗
                isBother: '',//受别的小孩捉弄或欺负
                isHelpful: '',//经常自愿的帮助别人（父母、老师或其他小孩）
                isThink: '',//做事前会想清楚
                isSteal: '',//会从事家里、学校或其他地方偷东西
                isCongenial: '',//跟大人相处比跟小孩子相处融洽
                isScary: '',//对很多事情容易感到害怕，容易受惊吓
                isObedient: '',//做事情能做到底，注意力持久
                isQuick: '',//似乎总是匆匆忙忙地从一个地方到另一个地方
                isLost: '',//在陌生的环境下会迷失方向
                isFocus: '',//有时会专注于图画书很长时间
                isAdventure: '',//喜欢冒险的游戏和活动
                isScratched: '',//对轻微切伤或擦伤并不十分心烦
                isPrepare: '',//能为旅行或外出准备他/她需要的东西
                isEnter: '',//经常贸然进入新情境
                isPlan: '',//如果家庭计划没有兑现
                isSing: '',//喜欢别人对他唱歌
                isBind: '',//好像对任何人都不拘束
                isFear: '',//害怕夜贼或者“大灰狼”
                isNotice: '',//当父母穿了新衣服时，能注意到
                isQuiet: '',//相对于活跃性游戏，更喜欢安静的活动
                isConstantA: '',//对某事生气时，往往要持续十分钟或更长时间
                isBuild: '',//当搭建或者拼凑某些东西时，能够非常投入且坚持很长时间
                isSwing: '',//在荡秋千时喜欢又高又快
                isFrustrated: '',//在不能完成某些任务时似乎很沮丧
                isFollow: '',//善于按照要求行动
                isAdapt: '',//需要花很长的时间适应新的环境
                isComplain: '',//在感冒时很少抱怨
                isListen: '',//喜欢歌曲，比如童谣
                isShy: '',//即使在认识了很长时间的人面前，有时也会害羞
                isRelax: '',//在烦躁时，很容易被安抚下来
                isNoticeNew: '',//能很快注意到客厅里的新东西
                isEnergetic: '',//即使在晚上，也精力充沛
                isFearNight: '',//不害怕黑夜
                isFocusDate: '',//有时会专注于图画书很长时间
                isAvoidWild: '',//不喜欢粗野的游戏
                isAvoidDamage: '',//对轻微切伤或擦伤并不十分心烦
                isAvoidDanger: '',//到听说有危险的地方时会小心翼翼
                isSlowlyDecide: '',//会缓慢而不匆忙地决定接下来要做的事
                isTAngry: '',//当不能找到他/她想玩的东西时会生气
                isSoftBeat: '',//喜欢柔和有节拍的活动，比如摇摆
                isNewShy: '',//有时会对新认识的人害羞地转过脸去
                isLikeAngry: '',//当喜欢的亲戚或朋友在来访后准备离开时，变得烦躁
                isEvaluateParent: '',//会对父母外表的变化做评价
                isNotCriticize: '',//只要孩子不高兴，犯了错误，也不批评。
                isGiveWhat: '',//孩子想要什么就给什么。
                isSubordinate: '',//孩子是否服从自己无所谓。
                isGivingAll: '',//只要孩子高兴，可以不惜一切。
                isJugdeChild: '',//孩子不服从家长时打骂孩子。
                isNotCare: '',//对孩子犯了错误并不在乎。
                isTooMuch: '',//对孩子一点点异常过分着急。
                isEncourage: '',//鼓励孩子做他会做的事。
                isReplace: '',//孩子做不好的事情替他做。
                isInterest: '',//根据孩子本人的兴趣培养他的特长。
                isCrying: '',//对孩子的哭闹，有时查问清楚，有时拒绝。
                isAllow: '',//在和孩子谈话时允许孩子插话提问。
                isReport: '',//要求孩子做什么事都必须报告家长。
                isReason: '',//当孩子做错时问明原因再批评。
                isPity: '',//孩子缠着问这问那不耐烦。
                isPromise: '',//不向孩子做任何承诺。
                isExplain: '',//吩咐孩子做事时让孩子明白为什么或怎么做。
                isCare: '',//对孩子的学习、生活有时关心，有时不关心。
                isCNotice: '',//不注意孩子在做什么或怎么做。
                isBusy: '',//自己忙的时候不理睬孩子的提问。
                isWish: '',//孩子想怎么样就怎么样。
                isPraise: '',//不切实际地表扬孩子。
                isDo: '',//不了解孩子不和父母在一起时具体做什么。
                isReward: '',//以适当的方式表扬或奖励孩子。
                isReq: '',//要求孩子做什么事都必须报告家长。
                isNoReward: '',//对孩子没有惩罚或奖励
                isCHelp: '',//在孩子学习或做其他事遇到困难时帮助他解决。
                isPoint: '',//看着孩子做事情并随时指点。
                isAnswer: '',//对孩子提出的问题予以认真解答。
                isSame: '',//同样一件事情，有时允许，有时拒绝。
                isNoJudge: '',//对孩子不讲是非。
                isAgree: '',//孩子和谁在一起经过家长同意。
                isGame: '',//同孩子一起消遣、游戏。
                isCDo: '',//要求孩子做什么事必讲明原因或怎么做。
                isCritic: '',//孩子做了错事，有时批评，有时无所谓。
                isNoCare: '',//不关心孩子的生活小事。
                isCSub: '',//通过说理使孩子服从。
                isCForce: '',//有时说服孩子，有时强制孩子。
                isDoReq: '',//孩子在家里随便做自己的事情，家长没有具体要求。
                isSpecial: '',//培养孩子哪方面特长由家长决定。
                isRead: '',//与孩子一起阅读、看图画书
                isCount: '',//在生活中教孩子数的概念
                isDraw: '',//涂涂画画
                isInGame: '',//跟孩子一起玩开发智力的游戏(如搭积木、图形配对、过家家等)
                isDailyR: '',//结合日常生活与孩子一起识字
                isTogetherL: '',//一起听唱歌曲、诗歌、童谣
                isStory: '',//讲故事（家长讲，孩子听）
                isHand: '',//做手工
                isSport: '',//做运动
                isSelf: '',//教孩子生活自理技能，如吃饭、穿衣等
                isTalk: '',//与孩子谈论周围发生的一些事
                isNature: '',//与孩子一起认识大自然的动植物
                firstTouch: '',//第一次接触电子屏幕的月龄
                screenTime: '',//平均每天接触电子屏幕时间
                puzzleTime: '',//益智类或早教类视频节目时间
                cartoonTime: '',//娱乐卡通类视频节目时间
                NVideoTime: '',//非儿童类视频节目时间
                gameTime: '',//电子游戏时间
                webTime: '',//上网时间（如浏览网页微信、微博等）
                talkTime: '',//您的孩子观看电视时，您或者其他照样人陪同观看的时间
                talkContent: '',//您的孩子观看电视时，您或者其他照样人与其交流电视内容的时间
                sleepTime: '',//孩子晚上就寝/上床时间：平时（周一至周五）
                wakeUpTime: '',//孩子晚上就寝/上床时间：周末
                sleepTimeWeek: '',//孩子晚上睡着时间（通常晚于就寝时间）：平时（周一至周五）
                sleepTimeWeekend: '',//孩子晚上睡着时间（通常晚于就寝时间）：周末
                sleepTimeNum: '',//孩子晚上在固定时间上床睡觉
                fallTime: '',//孩子上床后在20分钟内入睡
                SleepAlone: '',//孩子在自己床上独自入睡
                OthSleep: '',//孩子在他人（父母或兄弟姐妹）床上入睡
                swingSway: '',//孩子入睡时出现摇摆或节律性动作
                sleepItem: '',//孩子需要特定物品入睡（如玩偶、特定的毛毯等）
                parentSleep: '',//孩子需要家长在房间陪伴才能入睡
                bedtime: '',//到了就寝时间，孩子会准备好去睡觉
                resistSleep: '',//到了就寝时间，孩子会抗拒去睡觉
                struggle: '',//到了就寝时间，孩子会挣扎（如哭闹、拒绝待在床上等）
                darkSleep: '',//孩子害怕在黑暗中睡觉
                singleSleep: '',//孩子害怕独自一个人睡觉
                sleepDay:'',//通常孩子每天的睡眠：（包括夜间睡眠和日间小睡时间）
                sleepLess: '',//孩子睡得太少
                sleepMore: '',//孩子睡得太多
                sleepNormal: '',//孩子的睡眠适量
                sleepSame: '',//孩子每天的睡眠量都一样
                sink: '',//孩子晚上会尿床
                sleepDream: '',//孩子睡眠中会说梦话
                sleepUnstable: '',//孩子睡眠中不安稳，常动来动去
                sleepwalking: '',//孩子夜间会梦游（睡眠过程中行走）
                sleepMove: '',//孩子夜间会移动到他人（如父母、兄弟姐妹等）的床上
                sleepPain: '',//孩子反映睡眠中身体疼痛。如果有，说明哪里痛？
                sleepMouth: '',//孩子睡眠中有磨牙现象（牙医可能告诉过您）
                sleepNoise: '',//孩子睡眠中打鼾/呼噜很响
                sleepPause: '',//孩子睡眠中出现呼吸暂停
                breath: '',//孩子睡眠中鼻息重或气急
                sleepUnfamiliar: '',//孩子不在家（如到亲戚家或去旅行）睡觉时有问题
                sleepProblem: '',//孩子抱怨睡眠问题
                sleepScream: '',//孩子夜间醒来尖叫、出汗且无法安抚
                nightmare: '',//孩子被噩梦惊醒
                nightWakingUs: '',//夜间醒来一般总共持续：平时（周一至周五）
                nightWakingWe: '',//夜间醒来一般总共持续：周末
                nightWakingOnce:'',//孩子夜间会醒来一次
                nightWakingMore:'',//孩子夜间会醒来一次以上
                nightWakingHelp:'',//孩子夜间醒来后没有帮助，能自主重新入睡
                morningWakingUs:'',//孩子早晨醒来的时间：平时
                morningWakingWe:'',//孩子早晨醒来的时间：周末
                morningWakingUpUs:'',//孩子早晨起床的时间（一般晚于醒来的时间）：平时
                morningWakingUpWe:'',//孩子早晨起床的时间（一般晚于醒来的时间）：周末
                morningWakingOnce:'',//孩子早晨自己醒来
                morningWakingAlarm:'',//孩子早晨由闹钟叫醒
                morningWakingEmotion:'',//孩子醒来后情绪不佳
                morningWakingOther:'',//孩子早晨由他人（如家长或兄弟姐妹）叫醒
                morningWakingDifficulty:'',//孩子早晨起床困难
                morningWakingLongTime:'',//孩子早晨需要很长时间才能清醒
                morningWakingEarly:'',//孩子早晨醒来很早
                morningWakingGood:'',//孩子早晨胃口很好
                daySleep:'',//孩子日间会小睡
                daySleepAlarm:'',//孩子在兴奋活动中突然睡着了
                daySleepTired:'',//孩子看起来很疲倦
                playAlone:'',//独自玩耍
                tv:'',//看电视
                drive:'',//坐车
                eat:'',//吃饭
            }
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
        getCase() {
            let queryId = null
            if(this.$route.query.queryId){
                queryId = this.$route.query.queryId
            }else{
                queryId = this.queryId
            }
            request.getStudent({queryId}, data => {
                this.queryPId = data.id;
                //基本信息填写
                let STU = data.student ? data.student : {};
                this.ruleForm.ties = STU['hhzgx'];//填表人和孩子的关系
                this.ruleForm.number = STU['num'];//编号
                this.ruleForm.class = STU['sclass'];//班级
                this.ruleForm.name = STU['name'];//姓名
                this.ruleForm.sex = STU['sex'];//性别
                this.ruleForm.birthDate = STU['birth_time'];//出生日期
                this.ruleForm.height = STU['height'];//身高
                this.ruleForm.weight = STU['weight'];//体重
                this.ruleForm.motherEducation = STU['mqjycd'];//母亲受教育程度
                this.ruleForm.fatherEducation = STU['fqjycd'];//父亲受教育程度
                this.ruleForm.familyIncome = STU['jtnsr'];//家庭收入
                this.ruleForm.mainCaregiver = STU['zyjhr'];//孩子的主要照护人
                this.ruleForm.mainCEducation = STU['zhrjycd'];//主要照护人受教育程度
                this.ruleForm.hasSiblings = STU['isxdjm'];//是否有兄弟姐妹
                this.ruleForm.gdm = STU['yszdrstlb'];//医生诊断为妊娠期糖尿病
                this.ruleForm.ghp = STU['yszdrsgxy'];//医生诊断为妊娠期高血压
                this.ruleForm.mental = STU['yldisbz'];//精神压力大或者情绪问题且需要专业人员帮助
                this.ruleForm.nutrition = STU['yszdyybl'];//医生诊断为营养不良
                this.ruleForm.deliveryMode = STU['fmfs'];//分娩方式
                this.ruleForm.birthWeight = STU['bweight'];//出生体重
                this.ruleForm.birthGAge = STU['bweek'];//出生孕周
                this.ruleForm.asRescue = STU['csiszxqj'];//出生时是否发生窒息或抢救
                this.ruleForm.birthFMethod = STU['cswyfs'];//出生后喂养方式
                this.ruleForm.weanBTime = STU['dmrsj'];//断母乳时间
                this.ruleForm.addCFTime = STU['jfssj'];//添加辅食时间
                //身体活动问卷
                let STHD = data.sthd ? data.sthd : {};
                this.ruleForm.physicalAFreq = STHD['cjzqdpl'];//最近7天，您孩子参加的“中强度身体活动”的频率（每周几天）中高强度身体活动指需要适度的体力消耗，如快走、骑自行车、跑步、打球、游泳、跳舞等，在此过程中儿童身上发热，呼吸急促
                this.ruleForm.physicalATime = STHD['dssjzqd'];//在参加中高强度身体活动的那几天，您孩子通常每天花多少时间来做中高强度身体活动？每次至少持续10分钟以上
                this.ruleForm.lowPAFreq = STHD['cjdqdpl'];//最近7天，您孩子参加的“低强度身体活动”的频率（每周几天）低强度身体活动时间，如缓慢步行、洗澡或其它不会导致儿童身上发热或呼吸急促的偶然活动
                this.ruleForm.lowPATime = STHD['dssjdqd'];// 在参加低强度身体活动的那几天，您孩子通常每天花多少时间来做低强度身体活动？每次至少持续10分钟以上
                this.ruleForm.sittingFreq = STHD['jzpl'];// 最近7天，您孩子静坐的频率（每周几天）静坐时间，如束缚在汽车安全座椅、高脚椅、手推车、婴儿车、婴儿背带或看护者背上的时间，也包括静坐听故事的时间
                this.ruleForm.sittingTime = STHD['jzsj'];//每天静坐的时间
                this.ruleForm.nonSFreq = STHD['fkpmjspv'];// 最近7天，您孩子非看屏幕的静坐频率（每周几天）如躺在垫子上，坐在高脚椅上、婴儿车或手推车中而几乎不动，坐着看书或坐着玩游戏
                this.ruleForm.nonSSTime = STHD['fkpmjzsj'];// 每天非看屏幕的静坐时间如躺在垫子上，坐在高脚椅上、婴儿车或手推车中而几乎不动，坐着看书或坐着玩游戏
                this.ruleForm.screenSFreq = STHD['pmjzpl'];//  最近7天，您孩子屏幕前静坐的频率（每周几天）如被动地观看屏幕娱乐节目（电视、计算机、移动设备）的时间,不包括需要进行身体活动或运动的积极屏幕游戏
                this.ruleForm.screenSTime = STHD['pmjzsj'];//每天屏幕前静坐的时间如被动地观看屏幕娱乐节目（电视、计算机、移动设备）的时间,不包括需要进行身体活动或运动的积极屏幕游戏
                //长处和困难问卷
                let CCHKN = data.cchkn ? data.cchkn : {};
                this.ruleForm.isPardon = CCHKN['ntlbrgs'];//能体谅到别人的感受
                this.ruleForm.isActive = CCHKN['bad'];//不安定、过分活跃、不能长久安静
                this.ruleForm.isHeadache = CCHKN['jcdzt'];//经常抱怨头痛、肚子痛或身体不舒服
                this.ruleForm.isShare = CCHKN['lyfx'];//很乐意与别的小孩分享东西，比如糖果、玩具、铅笔等等
                this.ruleForm.isAngry = CCHKN['jcfpq'];//经常发脾气或大吵大闹
                this.ruleForm.isLonely = CCHKN['bjgd'];//比较孤独，喜欢自己一个人玩
                this.ruleForm.isConform = CCHKN['bjsc'];//一般来说，比较顺从，通常是大人要求做的都肯做
                this.ruleForm.isWorry = CCHKN['hdyy'];//有很多担忧，经常表现出忧虑
                this.ruleForm.isHelp = CCHKN['lybz'];//如果有人受伤，不舒服或是生病，都很乐意提供帮助
                this.ruleForm.isAnxious = CCHKN['jczlba'];//经常的坐立不安或躁动
                this.ruleForm.isFriend = CCHKN['yhpy'];//有一个或一个以上的好朋友
                this.ruleForm.isArrest = CCHKN['cjqfbr'];//经常与别的小孩吵架或欺负其他小孩子
                this.ruleForm.isSad = CCHKN['jcbgx'];//经常不高兴、情绪低落或哭泣
                this.ruleForm.isLike = CCHKN['sxpyxh'];//一般来说，受别的小孩所喜欢
                this.ruleForm.isDistracted = CCHKN['ryfx'];//容易分心，注意力不集中
                this.ruleForm.isTight = CCHKN['xhjjz'];//在新环境下，会紧张或粘住大人，容易失去信心
                this.ruleForm.isYoung = CCHKN['dljxyh'];//爱对年纪小的儿童和善
                this.ruleForm.isLie = CCHKN['jcshqp'];//经常撒谎或欺骗
                this.ruleForm.isBother = CCHKN['sbrzn'];//受别的小孩捉弄或欺负
                this.ruleForm.isHelpful = CCHKN['zybzbr'];//经常自愿的帮助别人（父母、老师或其他小孩）
                this.ruleForm.isThink = CCHKN['zsqxqc'];//做事前会想清楚
                this.ruleForm.isSteal = CCHKN['htdx'];//会从事家里、学校或其他地方偷东西
                this.ruleForm.isCongenial = CCHKN['hdrrq'];//跟大人相处比跟小孩子相处融洽
                this.ruleForm.isScary = CCHKN['ryjx'];//对很多事情容易感到害怕，容易受惊吓
                this.ruleForm.isObedient = CCHKN['zylcj'];//做事情能做到底，注意力持久
                //儿童气质问卷（CBQ-SF）
                let CBQ =  data.cbq ? data.cbq : {};
                this.ruleForm.isQuick = CBQ['zscm'];//似乎总是匆匆忙忙地从一个地方到另一个地方
                this.ruleForm.isLost = CBQ['bzzsl'];//当被阻止做想做的事时，会变得非常失落
                this.ruleForm.isFocus = CBQ['htzz'];//在书上画图或涂色时表现得非常专注
                this.ruleForm.isAdventure = CBQ['xhmxhd'];//喜欢滑高的滑梯或其它冒险性活动
                this.ruleForm.isScratched = CBQ['xcsba'];//会因为很小的切伤或擦伤而非常不安
                this.ruleForm.isPrepare = CBQ['nwczb'];//能为旅行或外出准备他/她需要的东西
                this.ruleForm.isEnter = CBQ['mrjrxdf'];//经常贸然进入新情境(比如，鲁莽地到没去过的地方去玩儿，或者仓促地参加新的游戏)
                this.ruleForm.isPlan = CBQ['jtjhmdx'];//如果家庭计划没有兑现（比如，外出旅行没有按计划实施)
                this.ruleForm.isSing = CBQ['xhdtrcg'];//喜欢别人对他唱歌
                this.ruleForm.isBind = CBQ['drhrbjs'];//好像对任何人都不拘束
                this.ruleForm.isFear = CBQ['hpyz'];//害怕夜贼或者“大灰狼”
                this.ruleForm.isNotice = CBQ['nzyfmxyf'];//当父母穿了新衣服时，能注意到
                this.ruleForm.isQuiet = CBQ['gxhaj'];//相对于活跃性游戏，更喜欢安静的活动
                this.ruleForm.isConstantA = CBQ['sqgcsj'];//对某事生气时，往往要持续十分钟或更长时间
                this.ruleForm.isBuild = CBQ['trjccsj'];//当搭建或者拼凑某些东西时，能够非常投入且坚持很长时间
                this.ruleForm.isSwing = CBQ['dqqgk'];//在荡秋千时喜欢又高又快
                this.ruleForm.isFrustrated = CBQ['bwcjs'];//在不能完成某些任务时似乎很沮丧
                this.ruleForm.isFollow = CBQ['syayqxd'];//善于按照要求行动
                this.ruleForm.isAdapt = CBQ['hcsjsyxhj'];//需要花很长的时间适应新的环境
                this.ruleForm.isComplain = CBQ['gmssby'];//在感冒时很少抱怨
                this.ruleForm.isListen = CBQ['xhgq'];//喜欢歌曲，比如童谣
                this.ruleForm.isShy = CBQ['hhx'];//即使在认识了很长时间的人面前，有时也会害羞
                this.ruleForm.isRelax = CBQ['ryaf'];//在烦躁时，很容易被安抚下来
                this.ruleForm.isNoticeNew = CBQ['zyktxsw'];//能很快注意到客厅里的新东西
                this.ruleForm.isEnergetic = CBQ['wsjlcp'];//即使在晚上，也精力充沛
                this.ruleForm.isFearNight = CBQ['bhphy'];//不害怕黑夜
                this.ruleForm.isFocusDate = CBQ['zztscsj'];//有时会专注于图画书很长时间
                this.ruleForm.isAvoidWild = CBQ['bxhcyyx'];//不喜欢粗野的游戏
                this.ruleForm.isAvoidDamage = CBQ['qwcsbxf'];//对轻微切伤或擦伤并不十分心烦
                this.ruleForm.isAvoidDanger = CBQ['zywx'];//到听说有危险的地方时会小心翼翼
                this.ruleForm.isSlowlyDecide = CBQ['hmzs'];//会缓慢而不匆忙地决定接下来要做的事
                this.ruleForm.isTAngry = CBQ['bnzdhsc'];//当不能找到他/她想玩的东西时会生气
                this.ruleForm.isSoftBeat = CBQ['xhrhhd'];//喜欢柔和有节拍的活动，比如摇摆
                this.ruleForm.isNewShy = CBQ['xrshx'];//有时会对新认识的人害羞地转过脸去
                this.ruleForm.isLikeAngry = CBQ['pyzhfz'];//当喜欢的亲戚或朋友在来访后准备离开时，变得烦躁
                this.ruleForm.isEvaluateParent = CBQ['dfmwbpj'];//会对父母外表的变化做评价
                //母亲照养方式问卷
                let MQZYFS = data.mqzyfs ? data.mqzyfs : {};
                this.ruleForm.isNotCriticize = MQZYFS['hzbgxbpp'];//只要孩子不高兴，犯了错误，也不批评。
                this.ruleForm.isGiveWhat = MQZYFS['hzysmgsm'];//孩子要什么就给什么。
                this.ruleForm.isSubordinate = MQZYFS['hzsffcwsw'];//孩子是否服从自己无所谓。
                this.ruleForm.isGivingAll = MQZYFS['hzgxbxyq'];//只要孩子高兴，可以不惜一切。
                this.ruleForm.isJugdeChild = MQZYFS['hzbfcdm'];//孩子不服从家长时打骂孩子。
                this.ruleForm.isNotCare = MQZYFS['hzfcbzh'];//对孩子犯了错误并不在乎。
                this.ruleForm.isTooMuch = MQZYFS['hzyddycgfzj'];//对孩子一点点异常过分着急。
                this.ruleForm.isEncourage = MQZYFS['glhzzs'];//鼓励孩子做他会做的事。
                this.ruleForm.isReplace = MQZYFS['thzzs'];//孩子做不好的事情替他做。
                this.ruleForm.isInterest = MQZYFS['gjhzxqpytc'];//根据孩子本人的兴趣培养他的特长。
                this.ruleForm.isCrying = MQZYFS['dhzknysdc'];//对孩子的哭闹，有时查问清楚，有时拒绝。
                this.ruleForm.isAllow = MQZYFS['yxchtw'];//在和孩子谈话时允许孩子插话提问。
                this.ruleForm.isReport = MQZYFS['bxbgjz'];//要求孩子做什么事都必须报告家长。
                this.ruleForm.isReason = MQZYFS['wmyyzpp'];//当孩子做错时问明原因再批评。
                this.ruleForm.isPity = MQZYFS['bnfwzwn'];//孩子缠着问这问那不耐烦。
                this.ruleForm.isPromise = MQZYFS['bxhzzcn'];//不向孩子做任何承诺。
                this.ruleForm.isExplain = MQZYFS['rhzmbwsm'];//吩咐孩子做事时让孩子明白为什么或怎么做。
                this.ruleForm.isCare = MQZYFS['dhzshysgx'];//对孩子的学习、生活有时关心，有时不关心。
                this.ruleForm.isCNotice = MQZYFS['bzyhzzsm'];//不注意孩子在做什么或怎么做。
                this.ruleForm.isBusy = MQZYFS['zjmblchz'];//自己忙的时候不理睬孩子的提问。
                this.ruleForm.isWish = MQZYFS['hzxzmyjzy'];//孩子想怎么样就怎么样。
                this.ruleForm.isPraise = MQZYFS['bqsjbyhz'];//不切实际地表扬孩子。
                this.ruleForm.isDo = MQZYFS['bljhzhfuzsm'];//不了解孩子不和父母在一起时具体做什么。
                this.ruleForm.isReward = MQZYFS['ysdfsbyhz'];//以适当的方式表扬或奖励孩子。
                this.ruleForm.isReq = MQZYFS['wlyqysmz'];//对孩子无理要求，有时满足，有时拒绝。
                this.ruleForm.isNoReward = MQZYFS['dhzmyjc'];//对孩子没有惩罚或奖励。
                this.ruleForm.isCHelp = MQZYFS['bhzjjkn'];//在孩子学习或做其他事遇到困难时帮助他解决。
                this.ruleForm.isPoint = MQZYFS['khzzs'];//看着孩子做事情并随时指点。
                this.ruleForm.isAnswer = MQZYFS['dhzrzhd'];//对孩子提出的问题予以认真解答。
                this.ruleForm.isSame = MQZYFS['tyjsysyx'];//同样一件事情，有时允许，有时拒绝。
                this.ruleForm.isNoJudge = MQZYFS['dhzbjsf'];//对孩子不讲是非。
                this.ruleForm.isAgree = MQZYFS['hsyqyty'];//孩子和谁在一起经过家长同意。
                this.ruleForm.isGame = MQZYFS['thzyqyx'];//同孩子一起消遣、游戏。
                this.ruleForm.isCDo = MQZYFS['yqhzjyy'];//要求孩子做什么事必讲明原因或怎么做。
                this.ruleForm.isCritic = MQZYFS['hzzcyspp'];//孩子做了错事，有时批评，有时无所谓。
                this.ruleForm.isNoCare = MQZYFS['bgxxs'];//不关心孩子的生活小事。
                this.ruleForm.isCSub = MQZYFS['slshzsc'];//通过说理使孩子服从。
                this.ruleForm.isCForce = MQZYFS['ysqz'];//有时说服孩子，有时强制孩子。
                this.ruleForm.isDoReq = MQZYFS['zjmjtyq'];//孩子在家里随便做自己的事情，家长没有具体要求。
                this.ruleForm.isSpecial = MQZYFS['pyhzjzjd'];//培养孩子哪方面特长由家长决定。
                //亲子活动问卷
                let QZHD =data.qzhd ? data.qzhd : {};
                this.ruleForm.isRead = QZHD['yhzyqks'];//与孩子一起阅读、看图画书
                this.ruleForm.isCount = QZHD['jhzsdgl'];//在生活中教孩子数的概念
                this.ruleForm.isDraw = QZHD['tthh'];//涂涂画画
                this.ruleForm.isInGame = QZHD['yhzyqyx'];//跟孩子一起玩开发智力的游戏(如搭积木、图形配对、过家家等)
                this.ruleForm.isDailyR = QZHD['yqsz'];//结合日常生活与孩子一起识字
                this.ruleForm.isTogetherL = QZHD['yqcg'];//一起听唱歌曲、诗歌、童谣
                this.ruleForm.isStory = QZHD['jgs'];//讲故事(家长讲，孩子听）
                this.ruleForm.isHand = QZHD['zsg'];//做手工
                this.ruleForm.isSport = QZHD['zyd'];//做运动
                this.ruleForm.isSelf = QZHD['jhzzl'];//教孩子生活自理技能，如吃饭、穿衣等
                this.ruleForm.isTalk = QZHD['yhztlzw'];//与孩子谈论周围发生的一些事
                this.ruleForm.isNature = QZHD['yhzrsdzr'];//与孩子一起认识大自然的动植物
                //屏幕暴露问卷
                let PMML =data.pmbl ? data.pmbl : {};
                this.ruleForm.firstTouch = PMML['dycyn'];//第一次接触电子屏幕的月龄
                this.ruleForm.screenTime = PMML['mtjcsj'];//平均每天接触电子屏幕时间
                //屏幕暴露问卷-矩阵填写
                let JZTX = PMML['jztx'] ? JSON.parse(PMML['jztx']) : {};
                this.ruleForm.puzzleTime = JZTX.puzzleTime;//益智类或早教类视频节目时间
                this.ruleForm.cartoonTime = JZTX.cartoonTime;//娱乐卡通类视频节目时间
                this.ruleForm.NVideoTime = JZTX.NVideoTime;//非儿童类视频节目时间
                this.ruleForm.gameTime = JZTX.gameTime;//电子游戏时间
                this.ruleForm.webTime = JZTX.webTime;//上网时间（如浏览网页微信、微博等）
                this.ruleForm.talkTime = PMML['ptgksj'];//您的孩子观看电视时，您或者其他照样人陪同观看的时间
                this.ruleForm.talkContent = PMML['ptgkjlsj'];//您的孩子观看电视时，您或者其他照样人与其交流电视内容的时间
                //儿童睡眠习惯问卷
                let ETXG =data.smxg ? data.smxg : {};
                this.ruleForm.sleepTime = ETXG['psscsj'];//孩子晚上就寝/上床时间：平时（周一至周五）：
                this.ruleForm.wakeUpTime = ETXG['zmscsj'];//孩子晚上就寝/上床时间：周末：
                this.ruleForm.sleepTimeWeek = ETXG['psszsj'];//孩子晚上睡着时间（通常晚于就寝时间）：平时（周一至周五）：
                this.ruleForm.sleepTimeWeekend = ETXG['zmszsj'];//孩子晚上睡着时间（通常晚于就寝时间）：周末：
                this.ruleForm.sleepTimeNum = ETXG['gdsjscsj'];//孩子晚上在固定时间上床睡觉
                this.ruleForm.fallTime = ETXG['esfzrs'];//孩子上床后在20分钟内入睡
                this.ruleForm.SleepAlone = ETXG['dzrs'];//孩子晚上在固定时间上床睡觉
                this.ruleForm.OthSleep = ETXG['ztrcsrs'];//孩子在他人（父母或兄弟姐妹）床上入睡
                this.ruleForm.swingSway = ETXG['rsyb'];//孩子入睡时出现摇摆或节律性动作
                this.ruleForm.sleepItem = ETXG['tdwrs'];//孩子需要特定物品入睡（如玩偶、特定的毛毯等）
                this.ruleForm.parentSleep = ETXG['xypbrs'];//孩子需要家长在房间陪伴才能入睡
                this.ruleForm.bedtime = ETXG['hzbrs'];//到了就寝时间，孩子会准备好去睡觉
                this.ruleForm.resistSleep = ETXG['hkjrs'];//到了就寝时间，孩子会抗拒去睡觉
                this.ruleForm.struggle = ETXG['hzhzz'];//到了就寝时间，孩子会挣扎（如哭闹、拒绝待在床上等）
                this.ruleForm.darkSleep = ETXG['hzhpha'];//孩子害怕在黑暗中睡觉
                this.ruleForm.singleSleep = ETXG['hzhpyr'];//孩子害怕独自一个人睡觉
                this.ruleForm.sleepDay = ETXG['hzmtsm'];//通常孩子每天的睡眠：（包括夜间睡眠和日间小睡时间）
                this.ruleForm.sleepLess = ETXG['sdts'];//孩子睡得太少
                this.ruleForm.sleepMore = ETXG['sdtd'];//孩子睡得太多
                this.ruleForm.SleepNormal = ETXG['smsl'];//孩子的睡眠适量
                this.ruleForm.sleepSame = ETXG['mtsmlyy'];//孩子每天的睡眠量都一样
                this.ruleForm.sink = ETXG['wsnc'];//孩子晚上会尿床
                this.ruleForm.sleepDream = ETXG['smsmh'];//孩子睡眠中会说梦话
                this.ruleForm.sleepUnstable = ETXG['smbaw'];//孩子睡眠中不安稳，常动来动去
                this.ruleForm.sleepwalking = ETXG['hmy'];//孩子夜间会梦游（睡眠过程中行走）
                this.ruleForm.sleepMove = ETXG['hdtrcs'];//孩子夜间会移动到他人（如父母、兄弟姐妹等）的床上
                this.ruleForm.sleepPain = ETXG['fytt'];//孩子反映睡眠中身体疼痛。如果有，说明哪里痛？
                this.ruleForm.sleepMouth = ETXG['myxx'];//孩子睡眠中有磨牙现象（牙医可能告诉过您）
                this.ruleForm.sleepNoise = ETXG['dhl'];//孩子睡眠中打鼾/呼噜很响
                this.ruleForm.sleepPause = ETXG['hxzt'];//孩子睡眠中出现呼吸暂停
                this.ruleForm.breath = ETXG['bxz'];//孩子睡眠中鼻息重或气急
                this.ruleForm.sleepUnfamiliar = ETXG['jzbzjywt'];//孩子不在家（如到亲戚家或去旅行）睡觉时有问题
                this.ruleForm.sleepProblem = ETXG['bysmwt'];//孩子抱怨睡眠问题
                this.ruleForm.sleepScream = ETXG['yjxljj'];//孩子夜间醒来尖叫、出汗且无法安抚
                this.ruleForm.nightmare = ETXG['emjx'];//孩子被噩梦惊醒
                this.ruleForm.nightWakingUs = ETXG['psxlsj'];//夜间醒来一般总共持续：平时（周一至周五）
                this.ruleForm.nightWakingWe = ETXG['zmxlsj'];//夜间醒来一般总共持续：周末：
                this.ruleForm.nightWakingOnce = ETXG['xlyc'];//孩子夜间会醒来一次
                this.ruleForm.nightWakingMore = ETXG['xlycys'];//孩子夜间会醒来一次以上
                this.ruleForm.nightWakingHelp = ETXG['mybzcxrs'];//孩子夜间醒来后没有帮助，能自主重新入睡
                this.ruleForm.morningWakingUs = ETXG['pszcxlsj'];//孩子早晨醒来的时间：平时
                this.ruleForm.morningWakingWe = ETXG['zmzcxlsj'];//孩子早晨醒来的时间：周末
                this.ruleForm.morningWakingUpUs = ETXG['psqcsj'];//孩子早晨起床的时间（一般晚于醒来的时间）：平时
                this.ruleForm.morningWakingUpWe = ETXG['zmqcsj'];//孩子早晨起床的时间（一般晚于醒来的时间）：周末
                this.ruleForm.morningWakingOnce = ETXG['zjxl'];//孩子早晨自己醒来
                this.ruleForm.morningWakingAlarm = ETXG['nzjx'];//孩子早晨由闹钟叫醒
                this.ruleForm.morningWakingEmotion = ETXG['xlqxbj'];//孩子醒来后情绪不佳
                this.ruleForm.morningWakingOther = ETXG['yjrjx'];//孩子早晨由他人（如家长或兄弟姐妹）叫醒
                this.ruleForm.morningWakingDifficulty = ETXG['qckn'];//孩子早晨起床困难
                this.ruleForm.morningWakingLongTime = ETXG['csjcnqx'];//孩子早晨需要很长时间才能清醒
                this.ruleForm.morningWakingEarly = ETXG['xlhz'];//孩子早晨醒来很早
                this.ruleForm.morningWakingGood = ETXG['wkhh'];//孩子早晨胃口很好
                this.ruleForm.daySleep = ETXG['rjhxs'];//孩子日间会小睡
                this.ruleForm.daySleepAlarm = ETXG['xfhdzsz'];//孩子在兴奋活动中突然睡着了
                this.ruleForm.daySleepTired = ETXG['kqlhpj'];//孩子看起来很疲倦
                this.ruleForm.playAlone = ETXG['dzws'];//独自玩耍
                this.ruleForm.tv = ETXG['kds'];//看电视
                this.ruleForm.drive = ETXG['zc'];//坐车
                this.ruleForm.eat = ETXG['cf'];//吃饭
            }, error => {
                console.log(error)
            })
        }
    }
}
</script>
  
<style lang="less">
.TYGXBox {
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

    .TYGXBox-title {
        font-size: 1vw;
    }

    .div-box {
        width: 96%;
        margin: 0 auto;
        align-items: center;
        padding: 1rem 0;
        border: 1px solid #ccc;
        border-radius: 0.2rem;
        padding-left: 3vh;
    }

    .input-underLine {
        width: 4vw;
        border: 0;
        border-bottom: 1px blueviolet solid;
        outline: none;
        text-align: center
    }

    .box-title {
        font-size: 1vw;
        font-weight: bold;
    }
}
</style>
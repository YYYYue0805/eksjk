"""将 AI赋能医疗.md 转换为格式化的 Word 文档"""
from docx import Document
from docx.shared import Pt, Inches, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
import re

doc = Document()

# -- 全局样式设置 --
style = doc.styles['Normal']
font = style.font
font.name = '微软雅黑'
font.size = Pt(11)
style.element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
style.paragraph_format.space_after = Pt(6)
style.paragraph_format.line_spacing = 1.5

# 页边距
for section in doc.sections:
    section.top_margin = Cm(2.5)
    section.bottom_margin = Cm(2.5)
    section.left_margin = Cm(2.5)
    section.right_margin = Cm(2.5)


def add_title(text):
    """文档主标题"""
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run(text)
    run.font.size = Pt(22)
    run.bold = True
    run.font.color.rgb = RGBColor(0x1a, 0x56, 0xdb)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_after = Pt(4)


def add_subtitle(text):
    """副标题"""
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run(text)
    run.font.size = Pt(11)
    run.font.color.rgb = RGBColor(0x66, 0x66, 0x66)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_after = Pt(20)


def add_h1(text):
    """一级标题"""
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.size = Pt(16)
    run.bold = True
    run.font.color.rgb = RGBColor(0x1a, 0x56, 0xdb)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_before = Pt(24)
    p.paragraph_format.space_after = Pt(12)


def add_h2(text):
    """二级标题"""
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.size = Pt(13)
    run.bold = True
    run.font.color.rgb = RGBColor(0x2c, 0x3e, 0x50)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_before = Pt(16)
    p.paragraph_format.space_after = Pt(8)


def add_h3(text):
    """三级标题"""
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.size = Pt(11.5)
    run.bold = True
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_before = Pt(12)
    p.paragraph_format.space_after = Pt(6)


def add_para(text, bold_prefix=None):
    """普通段落，支持句首加粗"""
    p = doc.add_paragraph()
    if bold_prefix:
        run_b = p.add_run(bold_prefix)
        run_b.bold = True
        run_b.font.name = '微软雅黑'
        run_b._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    run = p.add_run(text)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')


def add_quote(text):
    """引用块（用缩进+灰色模拟）"""
    p = doc.add_paragraph()
    p.paragraph_format.left_indent = Cm(1)
    run = p.add_run(text)
    run.font.size = Pt(10.5)
    run.italic = True
    run.font.color.rgb = RGBColor(0x55, 0x55, 0x55)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    p.paragraph_format.space_before = Pt(4)
    p.paragraph_format.space_after = Pt(4)


def add_code_block(text):
    """代码/指令块（灰底等宽）"""
    p = doc.add_paragraph()
    p.paragraph_format.left_indent = Cm(0.8)
    run = p.add_run(text)
    run.font.size = Pt(9.5)
    run.font.name = 'Consolas'
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)


def add_table(headers, rows):
    """添加表格"""
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Light Grid Accent 1'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    # 表头
    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = ''
        run = cell.paragraphs[0].add_run(h)
        run.bold = True
        run.font.size = Pt(10)
        run.font.name = '微软雅黑'
        run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    # 数据行
    for r, row in enumerate(rows):
        for c, val in enumerate(row):
            cell = table.rows[r + 1].cells[c]
            cell.text = ''
            run = cell.paragraphs[0].add_run(val)
            run.font.size = Pt(10)
            run.font.name = '微软雅黑'
            run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    doc.add_paragraph()  # 表后间距


def add_bullet(text, bold_prefix=None):
    """无序列表"""
    p = doc.add_paragraph(style='List Bullet')
    if bold_prefix:
        run_b = p.add_run(bold_prefix)
        run_b.bold = True
        run_b.font.name = '微软雅黑'
        run_b._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
    run = p.add_run(text)
    run.font.name = '微软雅黑'
    run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')


def add_divider():
    """分割线"""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(8)
    run = p.add_run('─' * 40)
    run.font.color.rgb = RGBColor(0xcc, 0xcc, 0xcc)
    run.font.size = Pt(8)


# ==================== 开始写内容 ====================

# -- 封面 --
add_title('AI 赋能医疗')
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('—— 医护人员如何用好人工智能')
run.font.size = Pt(14)
run.font.color.rgb = RGBColor(0x1a, 0x56, 0xdb)
run.font.name = '微软雅黑'
run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')
p.paragraph_format.space_after = Pt(6)

add_subtitle('分享嘉宾：yyyyy　｜　适用对象：医院医护人员、行政管理人员　｜　2026年7月')

add_divider()

# -- 一 --
add_h1('一、开场：AI 离我们并不远')

add_para('各位老师好，今天想和大家聊聊一个很"热"的话题——人工智能（AI）。')
add_para('可能有人觉得 AI 是程序员的事，离我们医护人员很远。但其实——')
add_quote('您手机里的语音助手、网购时的智能推荐、拍照时的美颜功能……这些全是 AI。')
add_para('而现在，AI 已经能帮我们写病历、看报告、做PPT、整理文献，甚至参与开发一套完整的医院管理系统。')
add_para('今天我所在的这个项目——EKSJK 儿科数据管理系统——就是全程用 AI 辅助开发出来的。')
add_para('接下来，我会用"说人话"的方式，带大家认识 AI、学会用它，并看看它在我们医疗工作中能发挥什么作用。')

# -- 二 --
add_h1('二、AI 到底是什么？—— 一句话讲清楚')

add_table(
    ['通俗理解', '说明'],
    [
        ['AI 像一个超级实习生', '它读过海量的书和文章，能听懂你的话，帮你干活'],
        ['但它不是神', '它也会犯错，需要你来判断对错、做最终决定'],
        ['你是指挥官，它是执行者', '你说得越清楚，它干得越漂亮'],
    ]
)

add_para('', bold_prefix='核心公式：AI = 海量知识 + 理解能力 + 执行能力')
add_para('它就像你科室里那个最勤奋的进修生——什么都能搭把手，但最终签字的还得是你。')

# -- 三 --
add_h1('三、医护日常，AI 能帮你做什么？')

add_h2('3.1 写文书、写报告')
add_table(
    ['场景', '具体用法'],
    [
        ['📝 写科普文章', '"帮我写一篇关于儿童性早熟的科普短文，通俗易懂，800字"'],
        ['📋 整理会议纪要', '把语音转文字的会议记录丢给 AI："帮我整理成规范的会议纪要"'],
        ['📄 润色论文摘要', '"这段摘要帮我润色一下，更学术化一些"'],
        ['📧 写工作邮件', '"帮我写一封给科室的邮件，通知下周三大查房"'],
    ]
)

add_h2('3.2 辅助诊断与学习')
add_table(
    ['场景', '具体用法'],
    [
        ['🔍 辅助读片', '上传影像图片，请 AI 帮你标注可疑区域（AI 读片准确率已经很高）'],
        ['📚 查文献', '"帮我查一下 2024-2025 年关于 McCune-Albright 综合征的最新诊疗指南"'],
        ['🧪 解读报告', '"这份激素六项报告数值偏高的可能原因有哪些？"'],
        ['📖 快速学习', '"我是儿科护士，用通俗的话给我讲讲什么是 Tanner 分期"'],
        ['🧬 遗传咨询辅助', '"这个基因突变位点有什么临床意义？"'],
    ]
)

add_h2('3.3 患者沟通')
add_table(
    ['场景', '具体用法'],
    [
        ['👨‍👩‍👧 写患者须知', '"帮我写一份儿童生长激素治疗的家庭护理注意事项"'],
        ['📱 回复模板', '"帮我拟一个患者微信群常见问题回复模板，关于骨龄检查的"'],
        ['🎨 做宣教材料', '"帮我设计一份儿童矮小症筛查的宣传折页大纲"'],
    ]
)

add_h2('3.4 办公效率')
add_table(
    ['场景', '具体用法'],
    [
        ['📊 数据处理', '"帮我把这个 Excel 里的异常值标出来"'],
        ['📈 做 PPT', '"给我一个大查房 PPT 大纲，主题是中枢性性早熟的鉴别诊断"'],
        ['📅 排班建议', '"根据这些约束条件，帮我排一个科室 12 月值班表"'],
    ]
)

# -- 四 --
add_h1('四、实战案例：AI 如何"做"出了一套医院管理系统？')

add_para('大家正在用的 EKSJK 儿科数据管理系统，它的开发过程可以让大家直观感受 AI 的能力。')

add_h2('4.1 这个系统是干什么的？')
add_para('简单说，是一套儿童内分泌疾病的电子病历和随访管理系统。覆盖：')
add_bullet('性发育异常（DSD）、遗传性骨病（FSS）等 7 大疾病类型')
add_bullet('患者管理 + 随访跟踪 + 生长曲线自动绘制')
add_bullet('审核发放工作流 + 数据统计分析 + 微信小程序')

add_h2('4.2 AI 参与了开发的全过程')

add_para('', bold_prefix='💡 需求讨论 → ')
add_para('你说"我要一个管理儿童患者的系统"，AI 和你反复对话，帮你把模糊的想法变成清晰的需求清单。')

add_para('', bold_prefix='🏗️ 架构设计 → ')
add_para('AI 给出技术方案：用什么框架、数据库怎么设计、前后端怎么分。')

add_para('', bold_prefix='💻 写代码 → ')
add_para('AI 生成了后端的 158 个 Java 文件（16,595 行代码）、前端的 36 个界面组件（10,701 行代码），以及 30 次数据库迭代迁移脚本。')

add_para('', bold_prefix='🧪 测试调试 → ')
add_para('系统出了 Bug，把报错信息丢给 AI，它能帮你定位问题并给出修复方案。')

add_para('', bold_prefix='🚀 部署上线 → ')
add_para('AI 辅助编写部署脚本，一键推送到阿里云 K8s 集群，系统就上线了。')

add_h2('4.3 一组真实数字')
add_table(
    ['指标', '数量'],
    [
        ['后端 Java 代码', '16,595 行'],
        ['前端界面代码', '10,701 行'],
        ['数据库迁移脚本', '30 个'],
        ['代码文件总数', '211 个'],
        ['从零到上线', '约 4 轮迭代'],
    ]
)
add_quote('如果完全由人工编写，保守估计需要一个 5 人团队工作 6-8 个月。借助 AI，核心开发一个人 + AI 即可高效推进。')

add_h2('4.4 系统亮点功能一览')
add_bullet('7 种疾病各有一套专属表单，AI 自动判断疾病分类（ELTM 智能诊断）')
add_bullet('生长曲线自动绘制，异常数据自动标记')
add_bullet('基线数据 → 审核 → 驳回/通过 → 发放，完整工作流闭环')
add_bullet('一键 Excel 导出，数据秒级生成')

# -- 五 --
add_h1('五、用好 AI 的三个关键技巧')

add_h2('技巧一：把话说清楚')
add_para('', bold_prefix='坏例子：')
add_code_block('"帮我写个东西"')
add_para('', bold_prefix='好例子：')
add_code_block('"帮我写一篇面向家长的科普文章，主题是如何判断孩子是否发育过早，800字左右，语言通俗，分3个小标题，要有具体自查方法"')
add_para('记住：你给 AI 的指令越具体，它产出越靠谱。')

add_h2('技巧二：多轮对话，逐步细化')
add_para('AI 的第一版回答往往不够完美，没关系——像带学生一样，多调教几轮：')
add_code_block('你：帮我写一段生长发育门诊的患者宣教文字')
add_code_block('AI：[生成初版]')
add_code_block('你：太学术了，改成小学文化能看懂的程度')
add_code_block('AI：[修改版]')
add_code_block('你：再加一段关于复查时间的提醒')
add_code_block('AI：[完善版]')

add_h2('技巧三：你是审核者，不是搬运工')
add_para('AI 产出的内容，一定要经过你的专业审核。尤其是：')
add_bullet('医疗建议——必须核实指南和教科书，不能直接用 AI 给的结论', bold_prefix='⚠️ ')
add_bullet('药物剂量——绝对不能直接抄 AI 给的数字，哪怕它说得很笃定', bold_prefix='⚠️ ')
add_bullet('患者隐私——不要把真实姓名、身份证号、病历号等喂给 AI', bold_prefix='⚠️ ')

# -- 六 --
add_h1('六、AI 在医学领域的更多应用')

add_para('除了帮我们写东西，AI 在医学上还有很多成熟应用：')

add_table(
    ['领域', '实际应用', '成熟度'],
    [
        ['🩻 影像诊断', '肺结节筛查、骨折检测、眼底病变识别', '⭐⭐⭐⭐⭐ 非常成熟'],
        ['🧬 基因组学', '基因变异致病性预测、肿瘤靶向药匹配', '⭐⭐⭐⭐ 较成熟'],
        ['📋 病历质控', '自动检查病历书写完整性、逻辑一致性', '⭐⭐⭐⭐ 较成熟'],
        ['💊 药物研发', 'AI 筛选候选分子、预测药物相互作用', '⭐⭐⭐⭐ 较成熟'],
        ['🏥 智能导诊', '根据症状推荐挂号科室、预问诊', '⭐⭐⭐ 推广中'],
        ['🤖 手术机器人', '辅助定位、规划手术路径', '⭐⭐⭐ 发展中'],
    ]
)

# -- 七 --
add_h1('七、医护人员最关心的几个问题')

add_h2('Q1：AI 会不会取代医生/护士？')
add_para('不会。AI 是工具，不是替代者。')
add_para('医疗的核心是人与人之间的信任、判断和责任——这些是 AI 不具备的。AI 可以帮我们干重复劳动，但最终诊断、治疗方案、护理操作，永远需要持证的专业人员来完成。')

add_h2('Q2：用 AI 安全吗？病人隐私怎么办？')
add_bullet('不要上传患者真实姓名、身份证号、病历号等可识别信息')
add_bullet('提问时用脱敏后的数据：如"10岁男童，身高130cm"而非"张三，病历号xxxx"')
add_bullet('医院未来可以部署本地化的私有大模型，数据不出院，彻底解决隐私顾虑')

add_h2('Q3：我完全不懂技术，能学会吗？')
add_para('能。使用 AI 不需要任何技术背景。')
add_para('你只需要会用微信打字，就能用好 AI。它理解的是"人话"，不是代码。这就像你不需要懂发动机原理，照样能开车。')

# -- 八 --
add_h1('八、现在就试试（动手环节）')

add_para('打开你手机上的 AI 应用（如豆包、Kimi、通义千问等，全部免费），试试这些指令：')

add_code_block('1. "帮我写一份儿童生长激素激发试验的护理配合要点"')
add_code_block('2. "把这段病历摘要翻译成英文"')
add_code_block('3. "给我一份儿科门诊冬季常见病预防的家长告知书"')
add_code_block('4. "我是一名儿科护士，需要准备一个5分钟的晨会分享，')
add_code_block('    主题是生长曲线图怎么看，帮我写个发言稿"')
add_code_block('5. "整理以下就诊记录，生成规范的病程记录模板：[粘贴内容]"')

# -- 九 --
add_h1('九、总结')

add_table(
    ['记住这三点就够了'],
    [
        ['🔑 AI 是工具，你是指挥官 —— 你提需求，它来执行'],
        ['🔑 指令越具体，结果越靠谱 —— 别让它猜，直接说清楚'],
        ['🔑 你是最终把关人 —— AI 的建议要经过你的专业判断'],
    ]
)

doc.add_paragraph()
add_quote('未来，不会用 AI 的医护人员，就像现在不会用电脑的医护人员一样。')
add_quote('但会用 AI 的医护人员，永远不会被 AI 取代。')

# -- 附录 --
add_h1('附录：推荐工具')

add_table(
    ['工具', '用途', '费用'],
    [
        ['豆包（字节跳动）', '通用对话、写作', '免费'],
        ['Kimi（月之暗面）', '长文档分析、论文阅读', '免费'],
        ['通义千问（阿里）', '通用对话、多轮对话', '免费'],
        ['Claude（Anthropic）', '专业写作、代码开发', '有免费额度'],
        ['讯飞听见', '语音转文字（查房记录）', '有免费额度'],
    ]
)

# -- 尾注 --
add_divider()
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('本文档由 yyyyy 于 2026 年 7 月在医院交流会上分享。AI 发展日新月异，请以实际使用体验为准。')
run.font.size = Pt(9)
run.font.color.rgb = RGBColor(0x99, 0x99, 0x99)
run.font.name = '微软雅黑'
run._element.rPr.rFonts.set(qn('w:eastAsia'), '微软雅黑')

# -- 保存 --
output_path = 'C:/Users/Administrator/Desktop/ekdms-develop/AI赋能医疗-医护人员AI使用指南.docx'
doc.save(output_path)
print(f'Word doc generated: {output_path}')

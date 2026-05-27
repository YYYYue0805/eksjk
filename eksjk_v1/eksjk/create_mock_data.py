"""
批量创建模拟用户数据脚本
通过 Django shell 执行: python manage.py shell < create_mock_data.py
"""
import os
import sys
import django
import random
import hashlib

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'wjwsjk.settings_k8s')
django.setup()

from login.models import User
from django.contrib.auth.hashers import make_password

# ========== 中文姓名生成 ==========
SURNAMES = [
    '王', '李', '张', '刘', '陈', '杨', '赵', '黄', '周', '吴',
    '徐', '孙', '胡', '朱', '高', '林', '何', '郭', '马', '罗',
    '梁', '宋', '郑', '谢', '韩', '唐', '冯', '于', '董', '萧',
    '程', '曹', '袁', '邓', '许', '傅', '沈', '曾', '彭', '吕',
    '苏', '卢', '蒋', '蔡', '贾', '丁', '魏', '薛', '叶', '阎',
]

MALE_NAMES = [
    '伟', '强', '磊', '洋', '勇', '军', '杰', '涛', '超', '明',
    '刚', '平', '辉', '鑫', '波', '斌', '宇', '浩', '凯', '健',
    '俊', '飞', '毅', '峰', '帅', '雷', '鹏', '龙', '威', '彬',
    '昊', '晨', '博', '翔', '睿', '航', '志', '文', '建', '国',
    '华', '亮', '成', '东', '旭', '阳', '松', '林', '海', '天',
]

FEMALE_NAMES = [
    '芳', '娜', '敏', '静', '丽', '强', '磊', '洋', '艳', '霞',
    '秀', '娟', '英', '华', '慧', '巧', '美', '婷', '玉', '萍',
    '红', '玲', '芬', '莉', '桂', '凤', '洁', '梅', '琳', '素',
    '雪', '云', '燕', '蕾', '瑶', '欣', '颖', '露', '瑞', '佳',
    '倩', '珊', '莹', '翠', '雅', '晶', '妍', '茜', '秋', '珍',
]

DEPARTMENTS = [
    '内分泌科', '儿科', '骨科', '神经内科', '心血管内科',
    '消化内科', '呼吸内科', '泌尿外科', '普外科', '妇产科',
    '眼科', '耳鼻喉科', '皮肤科', '口腔科', '康复科',
    '急诊科', '重症医学科', '肿瘤科', '血液科', '风湿免疫科',
]

UNITS = ['1', '2', '3', '4', '5']

PROFESSIONAL_CHOICES = ['10040001', '10040002', '10040003', '10040004', '10040005']

def gen_name(sex):
    """生成随机中文姓名"""
    surname = random.choice(SURNAMES)
    if sex == '男':
        given = random.choice(MALE_NAMES)
    else:
        given = random.choice(FEMALE_NAMES)
    # 50%概率双字名
    if random.random() > 0.5:
        if sex == '男':
            given += random.choice(MALE_NAMES)
        else:
            given += random.choice(FEMALE_NAMES)
    return surname + given


# ========== 创建1000个模拟普通用户 ==========
print("开始创建1000个模拟用户...")

# 预先生成密码哈希（所有用户统一密码 user123）
hashed_password = make_password('user123')

batch_size = 200
user_count = 0

for batch_start in range(0, 1000, batch_size):
    users = []
    batch_end = min(batch_start + batch_size, 1000)
    for i in range(batch_start, batch_end):
        idx = i + 1
        sex = random.choice(['男', '女'])
        name = gen_name(sex)
        user = User(
            username=f'user{idx:04d}',
            password=hashed_password,
            name=name,
            sex=sex,
            unit=random.choice(UNITS),
            level=0,  # 普通用户
            professional=random.choice(PROFESSIONAL_CHOICES),
            department=random.choice(DEPARTMENTS),
            is_staff=False,
            is_superuser=False,
            is_active=True,
            email=f'user{idx:04d}@example.com',
        )
        users.append(user)

    User.objects.bulk_create(users, ignore_conflicts=True)
    user_count += len(users)
    print(f"  已创建 {user_count}/1000 个用户")

print(f"✅ 1000个模拟用户创建完成（用户名: user0001 ~ user1000，密码: user123）")


# ========== 创建10个医生数据 ==========
print("\n开始创建10个医生数据...")

DOCTOR_NAMES = [
    ('张明远', '男'), ('李秀华', '女'), ('王建国', '男'), ('陈丽萍', '女'), ('刘志强', '男'),
    ('赵雅琴', '女'), ('黄海涛', '男'), ('周婷婷', '女'), ('吴德胜', '男'), ('孙晓燕', '女'),
]

# 医生职称分布：2个主任医师、3个副主任医师、3个主治医师、2个医师
DOCTOR_PROFESSIONALS = [
    '10040005', '10040005',  # 主任医师
    '10040004', '10040004', '10040004',  # 副主任医师
    '10040003', '10040003', '10040003',  # 主治医师
    '10040002', '10040002',  # 医师
]

DOCTOR_DEPARTMENTS = [
    '内分泌科', '儿科', '骨科', '神经内科', '心血管内科',
    '消化内科', '呼吸内科', '泌尿外科', '普外科', '妇产科',
]

doctors = []
for i, (name, sex) in enumerate(DOCTOR_NAMES):
    idx = i + 1
    doctor = User(
        username=f'doctor{idx:02d}',
        password=make_password('doctor123'),
        name=name,
        sex=sex,
        unit='1',
        level=1,  # 管理员级别（医生有管理权限）
        professional=DOCTOR_PROFESSIONALS[i],
        department=DOCTOR_DEPARTMENTS[i],
        is_staff=True,
        is_superuser=False,
        is_active=True,
        email=f'doctor{idx:02d}@hospital.com',
    )
    doctors.append(doctor)

User.objects.bulk_create(doctors, ignore_conflicts=True)
print(f"✅ 10个医生创建完成（用户名: doctor01 ~ doctor10，密码: doctor123）")


# ========== 汇总 ==========
total = User.objects.count()
print(f"\n========== 数据创建完成 ==========")
print(f"用户总数: {total}")
print(f"  - 管理员: admin / admin123")
print(f"  - 普通用户: user0001 ~ user1000 / user123")
print(f"  - 医生: doctor01 ~ doctor10 / doctor123")
print(f"====================================")

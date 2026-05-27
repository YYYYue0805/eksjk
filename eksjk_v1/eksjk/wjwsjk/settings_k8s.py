"""
Django K8s 环境配置文件
继承自原始 settings.py，覆盖需要在 K8s 环境中修改的配置项
"""
import os
from .settings import *  # noqa: F401,F403

# ============ 安全配置 ============
DEBUG = os.environ.get('DJANGO_DEBUG', 'False').lower() == 'true'
SECRET_KEY = os.environ.get('DJANGO_SECRET_KEY', SECRET_KEY)
ALLOWED_HOSTS = os.environ.get('DJANGO_ALLOWED_HOSTS', '*').split(',')

# ============ 数据库配置（从环境变量读取） ============
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': os.environ.get('DB_NAME', 'eksjk'),
        'USER': os.environ.get('DB_USER', 'root'),
        'PASSWORD': os.environ.get('DB_PASSWORD', '123456'),
        'HOST': os.environ.get('DB_HOST', 'eksjk-mysql'),
        'PORT': os.environ.get('DB_PORT', '3306'),
        'CONN_MAX_AGE': 9,
        'OPTIONS': {
            'charset': 'utf8mb4',
            'init_command': "SET innodb_strict_mode=0",
        }
    }
}

# ============ RSA 密钥路径 ============
PRIVATE_KEY_PATH = os.environ.get('PRIVATE_KEY_PATH', '/app/wjwsjk/keys/rsa_1024_priv.pem')
PUBLIC_KEY_PATH = os.environ.get('PUBLIC_KEY_PATH', '/app/wjwsjk/keys/rsa_1024_pub.pem')

# ============ 文件存储路径（使用 PV 挂载点） ============
STORAGE_ROOT = os.environ.get('STORAGE_ROOT', '/data/storage')

# 图片储存路径
IMG_PATH = os.path.join(STORAGE_ROOT, 'images/')
# mask 文件储存路径
MASK_PATH = os.path.join(STORAGE_ROOT, 'masks/')
# 压缩包储存路径
ZIP_PATH = os.path.join(STORAGE_ROOT, 'zips/')
# 统计报表导出路径
STA_PATH = os.path.join(STORAGE_ROOT, 'reports/')

# 媒体文件根目录
MEDIA_ROOT = os.path.join(STORAGE_ROOT, 'media')
MEDIA_URL = '/media/'

# 自定义存储图片的目录
BABY_IMAGES_DIR = os.path.join(MEDIA_ROOT, 'baby_images')

# 确保存储目录存在
for _dir in [IMG_PATH, MASK_PATH, ZIP_PATH, STA_PATH, MEDIA_ROOT, BABY_IMAGES_DIR]:
    os.makedirs(_dir, exist_ok=True)

# ============ 验证码字体路径 ============
CAPTCHA_FONT_PATH = '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'

# ============ Collector 配置 ============
COLLECTOR_HOST = os.environ.get('COLLECTOR_HOST', 'tcp://127.0.0.1:18736')

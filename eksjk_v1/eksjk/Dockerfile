FROM python:3.8-slim

# 设置环境变量
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV DJANGO_SETTINGS_MODULE=wjwsjk.settings_k8s

WORKDIR /app

# 安装系统依赖（mysqlclient 需要）
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    default-libmysqlclient-dev \
    pkg-config \
    libffi-dev \
    libjpeg-dev \
    zlib1g-dev \
    libfreetype6-dev \
    fonts-dejavu-core \
    openssl \
    && rm -rf /var/lib/apt/lists/*

# 先复制依赖文件，利用 Docker 缓存层
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple \
    && pip install --no-cache-dir gunicorn -i https://pypi.tuna.tsinghua.edu.cn/simple

# 复制项目代码
COPY . .

# 生成 RSA 密钥对（如果不存在）
RUN mkdir -p /app/wjwsjk/keys && \
    if [ ! -f /app/wjwsjk/keys/rsa_1024_priv.pem ]; then \
        openssl genrsa -out /app/wjwsjk/keys/rsa_1024_priv.pem 1024 && \
        openssl rsa -pubout -in /app/wjwsjk/keys/rsa_1024_priv.pem -out /app/wjwsjk/keys/rsa_1024_pub.pem; \
    fi

# 创建存储目录
RUN mkdir -p /data/storage

# 暴露端口
EXPOSE 8000

# 启动命令
CMD ["gunicorn", "--config", "gunicorn.conf.py", "wjwsjk.wsgi:application"]

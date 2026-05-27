# gunicorn 配置文件

# 绑定地址和端口
bind = "0.0.0.0:8000"

# 工作进程数（建议 CPU 核心数 * 2 + 1）
workers = 3

# 工作模式
worker_class = "sync"

# 超时时间（秒）
timeout = 300

# 最大请求数，超过后 worker 自动重启（防止内存泄漏）
max_requests = 1000
max_requests_jitter = 50

# 日志配置
accesslog = "-"
errorlog = "-"
loglevel = "info"

# 优雅重启超时
graceful_timeout = 30

# 预加载应用
preload_app = True

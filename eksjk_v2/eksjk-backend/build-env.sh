#!/bin/bash
# 设置 JAVA_HOME 为 JDK 17（如果可用）
# 如果系统默认 JDK 版本不兼容，请手动设置 JAVA_HOME
# export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 注意：本项目要求 JDK 17+，推荐使用 JDK 17 或 JDK 21
# 当前 JDK 24 存在 Lombok 兼容性问题，建议使用以下命令编译：
# JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn clean package

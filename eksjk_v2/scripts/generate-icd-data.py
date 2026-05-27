"""
ICD-10 数据转换脚本
从 ICD10(3).xlsx 生成前端 JS 数据模块 icdData.js

用法: python3 generate-icd-data.py
输出: ../eksjk-frontend/src/data/icdData.js
"""
import pandas as pd
import re
import os

INPUT_FILE = os.path.join(os.path.dirname(__file__), '..', 'ICD10(3).xlsx')
OUTPUT_FILE = os.path.join(os.path.dirname(__file__), '..', 'eksjk-frontend', 'src', 'data', 'icdData.js')


def generate():
    df = pd.read_excel(INPUT_FILE)
    df.columns = ['main_code', 'sub_code', 'description']

    # 过滤空编码行
    df = df[df['main_code'].notna()].copy()

    # 清理编码：去除末尾 † 符号
    df['clean_code'] = df['main_code'].astype(str).str.replace(r'[†‡]', '', regex=True).str.strip()

    # 按 clean_code + description 去重
    df = df.drop_duplicates(subset=['clean_code', 'description'])

    # 按编码排序
    df = df.sort_values('clean_code')

    print(f'处理完成: 原始 {len(df)} 条有效记录')

    # 生成 icdOptions 数组
    options_lines = ['export const icdOptions = [']
    count = 0
    for _, row in df.iterrows():
        code = row['clean_code']
        desc = row['description']
        # 对 JS 字符串转义单引号和反斜杠
        code_esc = code.replace('\\', '\\\\').replace("'", "\\'")
        desc_esc = str(desc).replace('\\', '\\\\').replace("'", "\\'")
        label = f'{code} {desc_esc}'
        options_lines.append(f"  {{ value: '{code_esc}', label: '{label}' }},")
        count += 1
    options_lines.append(']')

    # 生成 icdLabelMap 对象
    map_lines = ['export const icdLabelMap = {']
    for _, row in df.iterrows():
        code = row['clean_code']
        desc = str(row['description']).replace('\\', '\\\\').replace("'", "\\'")
        code_esc = code.replace('\\', '\\\\').replace("'", "\\'")
        map_lines.append(f"  '{code_esc}': '{desc}',")
    map_lines.append('}')

    output = '\n'.join(options_lines) + '\n\n' + '\n'.join(map_lines) + '\n'

    # 确保输出目录存在
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)

    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(output)

    file_size_kb = os.path.getsize(OUTPUT_FILE) / 1024
    print(f'已生成: {OUTPUT_FILE}')
    print(f'文件大小: {file_size_kb:.1f} KB')
    print(f'总条目: {count}')


if __name__ == '__main__':
    generate()

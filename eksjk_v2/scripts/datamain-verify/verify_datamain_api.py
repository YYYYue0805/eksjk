#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
datamain 接口抽样验证脚本

用途：
  1. 调用 /datamain/cxList，缓存原始响应，统计 66 列的类型、空值、异常与重复主键；
  2. 解析列 54-65 的 JSON 明细，输出内部字段名（回答方案文档中的"待确认"项）；
  3. 抽样调用 /datamain/gettpbyid 与 /datamain/getzqbyid，验证 16/25 列结构；
  4. 可选：连接本地 MySQL，对比外部患者 id 与本地 eltm_id / 身份证 / 手机号的重合度；
  5. 输出 Markdown 报告 + JSON 摘要，默认对姓名/手机号/身份证做脱敏。

用法：
  # 首次运行（会下载约 52MB，写入 tmp/datamain-verify/cxlist.json）
  python verify_datamain_api.py --refresh

  # 复用已下载的响应，不访问外部接口
  python verify_datamain_api.py --cxlist tmp/datamain-verify/cxlist.json --skip-probe

  # 同时对比本地数据库
  python verify_datamain_api.py --mysql --mysql-user eksjk --mysql-password eksjk123

  # 只验证单个患者
  python verify_datamain_api.py --probe-id 12345

说明：
  - 仅使用 Python 标准库（urllib/json），无需 pip 安装；
  - 52MB JSON 反序列化约占 300-600MB 内存；
  - 外部接口为明文 HTTP 且无鉴权，请勿在公网环境运行或转发原始响应。
"""

import argparse
import json
import os
import re
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from collections import Counter, OrderedDict
from datetime import datetime

# 让 Windows 控制台按 UTF-8 输出
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")
    sys.stderr.reconfigure(encoding="utf-8")

DEFAULT_BASE_URL = "http://36.26.56.94:8026"
DEFAULT_OUT_DIR = os.path.join("tmp", "datamain-verify")

CX_COLUMNS = {
    0: "id", 1: "name", 2: "phone", 3: "id_card", 4: "gender_code", 5: "gender",
    6: "birthday", 7: "age", 8: "register_source", 9: "id_type", 10: "created_at",
    11: "doctor_name", 12: "doctor_id", 13: "hospital_name", 14: "hospital_id",
    15: "first_doctor_names", 16: "first_doctor_ids", 17: "first_hospital_names",
    18: "first_hospital_ids", 19: "assign_type", 20: "project_count", 21: "project_name",
    22: "待确认", 23: "待确认", 24: "patient_type", 25: "data_scope", 26: "待确认",
    27: "form_count", 28: "待确认", 29: "latest_form_time", 30: "latest_form_update",
    31: "latest_form_name", 32: "pic_count", 33: "latest_pic_time", 34: "latest_pic_update",
    35: "physical_sign_count", 36: "待确认", 37: "latest_sign_time", 38: "latest_sign_update",
    39: "report_count", 40: "待确认", 41: "latest_report_time", 42: "latest_report_update",
    43: "exercise_flag", 44: "待确认", 45: "latest_exercise_time", 46: "latest_exercise_update",
    47: "sleep_flag", 48: "待确认", 49: "latest_sleep_time", 50: "latest_sleep_update",
    51: "consent_count", 52: "latest_consent_time", 53: "latest_consent_update",
    54: "extend_data_json", 55: "hospital_bind_json", 56: "project_json", 57: "form_json",
    58: "pic_json", 59: "physical_json", 60: "report_json", 61: "exercise_json",
    62: "exercise_summary_json", 63: "sleep_json", 64: "sleep_summary_json", 65: "consent_json",
}

PIC_COLUMNS = {
    0: "pic_id", 1: "patient_id", 2: "待确认(long_id)", 3: "record_date", 4: "record_name",
    5: "image_url", 6: "create_time", 7: "update_time", 8: "待确认(doctor_id?)",
    9: "待确认(doctor_id?)", 10: "pic_source_type", 11: "source", 12: "待确认(0/1/3)",
    13: "待确认(related_id?)", 14: "image_type", 15: "remark?",
}

CONSENT_COLUMNS = {
    0: "sign_id", 1: "project_id", 2: "group_id", 3: "informed_consent_id", 4: "patient_id",
    5: "create_time", 6: "update_time", 7: "待确认(doctor_id?)", 8: "待确认(doctor_id?)",
    9: "content", 10: "sign_doctor_id?", 11: "patient_sign_image", 12: "patient_sign_time",
    13: "guardian_sign_image", 14: "guardian_sign_time", 15: "doctor_sign_image",
    16: "doctor_sign_time", 17: "sign_status", 18: "source", 19: "pdf_path",
    20: "pdf_file_name", 21: "pdf_error_msg", 22: "pdf_status", 23: "pdf_retry_count",
    24: "pdf_generate_time",
}

JSON_DETAIL_COLUMNS = {
    54: "extend_data_json", 55: "hospital_bind_json", 56: "project_json", 57: "form_json",
    58: "pic_json", 59: "physical_json", 60: "report_json", 61: "exercise_json",
    62: "exercise_summary_json", 63: "sleep_json", 64: "sleep_summary_json", 65: "consent_json",
}

PII_COLUMNS = {1, 2, 3}  # name / phone / id_card


# ---------------------------------------------------------------- 通用工具

def type_name(value):
    if value is None:
        return "null"
    if isinstance(value, bool):
        return "bool"
    if isinstance(value, int):
        return "int"
    if isinstance(value, float):
        return "float"
    if isinstance(value, str):
        return "str"
    if isinstance(value, list):
        return "list"
    if isinstance(value, dict):
        return "dict"
    return type(value).__name__


def mask_phone(value):
    s = str(value)
    if len(s) <= 7:
        return s[:2] + "***"
    return s[:3] + "****" + s[-4:]


def mask_id_card(value):
    s = str(value)
    if len(s) <= 8:
        return s[:2] + "***"
    return s[:6] + "********" + s[-4:]


def mask_name(value):
    s = str(value).strip()
    if not s:
        return s
    if len(s) <= 1:
        return s
    return s[0] + "*" * (len(s) - 1)


def mask_for_column(col_index, value):
    if value is None:
        return None
    if col_index == 1:
        return mask_name(value)
    if col_index == 2:
        return mask_phone(value)
    if col_index == 3:
        return mask_id_card(value)
    if isinstance(value, (dict, list)):
        return "<%s>" % type_name(value)
    s = str(value)
    return s if len(s) <= 40 else s[:40] + "..."


def mask_json_sample(obj, max_depth=3):
    """递归脱敏 JSON 样本，避免把 PII/签名图片写入报告。"""
    if max_depth < 0:
        return "..."
    if isinstance(obj, dict):
        out = OrderedDict()
        for k, v in list(obj.items())[:30]:
            kl = str(k).lower()
            if any(x in kl for x in ("phone", "mobile", "tel", "id_card", "idcard", "id_card_no")):
                out[k] = mask_phone(v) if v is not None else None
            elif "name" in kl and isinstance(v, str):
                out[k] = mask_name(v)
            elif "image" in kl and isinstance(v, str) and v.startswith("data:"):
                out[k] = "<base64 len=%d>" % len(v)
            elif "content" in kl and isinstance(v, str):
                out[k] = v[:60] + ("..." if len(v) > 60 else "")
            else:
                out[k] = mask_json_sample(v, max_depth - 1)
        return out
    if isinstance(obj, list):
        # 扁平数组（如接口返回的一行 16/25 列）直接展开，不做截断提示
        if all(not isinstance(v, (dict, list)) for v in obj):
            out = [mask_json_sample(v, max_depth - 1) for v in obj[:20]]
            if len(obj) > 20:
                out.append("<共 %d 项>" % len(obj))
            return out
        out = [mask_json_sample(v, max_depth - 1) for v in obj[:3]]
        if len(obj) > 3:
            out.append("<共 %d 项>" % len(obj))
        return out
    if isinstance(obj, str) and len(obj) > 80:
        return obj[:80] + "..."
    return obj


def parse_json_value(value):
    """把可能是 JSON 字符串 / 已解析对象的值统一成对象。"""
    if value is None:
        return None, None
    if isinstance(value, (list, dict)):
        return value, None
    if isinstance(value, str):
        s = value.strip()
        if not s:
            return None, None
        if s[0] in "[{":
            try:
                return json.loads(s), None
            except Exception as exc:  # noqa: BLE001
                return None, str(exc)
    return None, "not_json"


def parse_datetime(value):
    if value is None or value == "":
        return None
    s = str(value).strip()
    for fmt in ("%Y-%m-%d %H:%M:%S", "%Y-%m-%d", "%Y/%m/%d %H:%M:%S", "%Y/%m/%d",
                "%Y-%m-%dT%H:%M:%S", "%Y-%m-%dT%H:%M:%S.%f"):
        try:
            return datetime.strptime(s, fmt)
        except ValueError:
            continue
    return None


def to_int(value):
    try:
        return int(float(str(value)))
    except (TypeError, ValueError):
        return 0


# ---------------------------------------------------------------- HTTP

def http_download(url, out_path, timeout, retries):
    """流式下载到文件，返回字节数与耗时。"""
    last_error = None
    for attempt in range(1, retries + 1):
        started = time.time()
        try:
            req = urllib.request.Request(
                url,
                headers={"User-Agent": "eksjk-datamain-verify/1.0", "Accept": "application/json"},
            )
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                total = int(resp.headers.get("Content-Length") or 0)
                written = 0
                with open(out_path, "wb") as fh:
                    while True:
                        chunk = resp.read(1024 * 1024)
                        if not chunk:
                            break
                        fh.write(chunk)
                        written += len(chunk)
                        if total:
                            pct = written * 100 // total
                            print("\r  下载中 %d%%（%.1f MB / %.1f MB）" % (
                                pct, written / 1048576, total / 1048576), end="", flush=True)
                print("\r  下载完成：%.1f MB，耗时 %.1f 秒" % (written / 1048576, time.time() - started))
                return written, time.time() - started
        except (urllib.error.URLError, TimeoutError, OSError) as exc:
            last_error = exc
            wait = [5, 30, 120][min(attempt - 1, 2)]
            print("  第 %d 次请求失败：%s，%d 秒后重试" % (attempt, exc, wait))
            if attempt < retries:
                time.sleep(wait)
    raise RuntimeError("请求失败（已重试 %d 次）：%s" % (retries, last_error))


def load_payload(path):
    with open(path, "r", encoding="utf-8") as fh:
        payload = json.load(fh)
    if not isinstance(payload, dict):
        raise ValueError("响应顶层不是对象: %s" % type(payload).__name__)
    code = payload.get("code")
    data = payload.get("data")
    rows = None
    if isinstance(data, dict):
        rows = data.get("data")
    elif isinstance(data, list):
        rows = data
    if rows is None:
        raise ValueError("响应中未找到 data.data 数组，code=%r" % code)
    return code, rows


# ---------------------------------------------------------------- 分析

def column_stats(rows, col_count, col_names):
    stats = []
    for col in range(col_count):
        values = [row[col] for row in rows if col < len(row)]
        non_empty = [v for v in values if v is not None and v != ""]
        type_counter = Counter(type_name(v) for v in values)
        unique_counter = Counter()
        for v in values:
            if v is None or v == "":
                continue
            unique_counter[mask_for_column(col, v)] += 1
        max_len = max((len(str(v)) for v in non_empty), default=0)
        stats.append({
            "col": col,
            "name": col_names.get(col, "?"),
            "non_null": len(non_empty),
            "null": len(values) - len(non_empty),
            "types": dict(type_counter),
            "max_len": max_len,
            "top_values": unique_counter.most_common(5),
        })
    return stats


def detail_key_analysis(rows, col, max_samples=3):
    """解析 JSON 明细列，统计元素数量与内部键名。"""
    element_counter = Counter()
    key_counter = Counter()
    key_examples = {}
    invalid = 0
    non_empty = 0
    samples = []

    for row in rows:
        if col >= len(row):
            continue
        obj, err = parse_json_value(row[col])
        if err:
            invalid += 1
            continue
        if obj is None or obj == [] or obj == {}:
            continue
        non_empty += 1
        items = obj if isinstance(obj, list) else [obj]
        element_counter[len(items)] += 1
        for item in items:
            if isinstance(item, dict):
                for key, val in item.items():
                    key_counter[key] += 1
                    if key not in key_examples and val not in (None, "", [], {}):
                        key_examples[key] = mask_json_sample(val, max_depth=1)
                    if isinstance(val, dict):
                        for sub_key in val:
                            key_counter["%s.%s" % (key, sub_key)] += 1
                    elif isinstance(val, list) and val and isinstance(val[0], dict):
                        for sub_key in val[0]:
                            key_counter["%s[].%s" % (key, sub_key)] += 1
        if len(samples) < max_samples:
            samples.append(mask_json_sample(items[:1]))

    return {
        "col": col,
        "name": JSON_DETAIL_COLUMNS.get(col, str(col)),
        "non_empty_rows": non_empty,
        "invalid_json": invalid,
        "element_count_distribution": dict(element_counter.most_common(20)),
        "keys": dict(key_counter.most_common(80)),
        "key_examples": key_examples,
        "samples": samples,
    }


def analyze_age(values):
    counter = Counter()
    for value in values:
        if value is None or value == "":
            counter["空值"] += 1
        elif isinstance(value, (int, float)):
            counter["数字 ≤150" if float(value) <= 150 else "数字 >150"] += 1
        else:
            s = str(value).strip()
            if re.fullmatch(r"\d+(\.\d+)?", s):
                counter["纯数字串"] += 1
            elif re.search(r"[岁年月天]", s):
                counter["中文年龄串"] += 1
            else:
                counter["其他"] += 1
    return counter


def analyze_birthday(values):
    counter = Counter()
    invalid_examples = []
    for value in values:
        if value is None or value == "":
            counter["空值"] += 1
        elif parse_datetime(value):
            counter["合法日期"] += 1
        else:
            counter["非法日期"] += 1
            if len(invalid_examples) < 5:
                invalid_examples.append(str(value))
    return counter, invalid_examples


def detail_field_distribution(rows, col, field):
    counter = Counter()
    for row in rows:
        if col >= len(row):
            continue
        obj, err = parse_json_value(row[col])
        if err or obj is None:
            continue
        items = obj if isinstance(obj, list) else [obj]
        for item in items:
            if isinstance(item, dict) and field in item:
                counter[str(item[field])] += 1
    return counter


def find_duplicates(values):
    counter = Counter(str(v) for v in values if v is not None and v != "")
    return {k: v for k, v in counter.items() if v > 1}


# ---------------------------------------------------------------- 单患者抽样

def mask_probe_value(name, value):
    """按字段语义脱敏单患者接口的返回值。"""
    if value is None:
        return None
    lname = name.lower()
    if "image" in lname and isinstance(value, str) and value.startswith("data:"):
        return "<base64 len=%d>" % len(value)
    if name in ("content",) and isinstance(value, str):
        return value[:80] + ("..." if len(value) > 80 else "")
    if name in ("record_name", "pdf_file_name", "medical_record_name") and isinstance(value, str):
        return value[:6] + ("..." if len(value) > 6 else "")
    if isinstance(value, (dict, list)):
        return mask_json_sample(value)
    s = str(value)
    return s if len(s) <= 80 else s[:80] + "..."


def probe_patient(base_url, patient_id, endpoint, expected_cols, col_names, out_dir, timeout, retries):
    url = "%s%s?queryId=%s" % (base_url, endpoint, urllib.parse.quote(str(patient_id)))
    safe_id = re.sub(r"[^0-9A-Za-z_-]", "_", str(patient_id))
    file_path = os.path.join(out_dir, "%s_%s.json" % (endpoint.strip("/").replace("/", "_"), safe_id))
    print("  抽样请求 %s" % url)
    size, elapsed = http_download(url, file_path, timeout, retries)
    code, rows = load_payload(file_path)
    rows = rows or []
    row_lengths = Counter(len(r) for r in rows)

    samples = []
    for row in rows[:3]:
        samples.append({
            col_names.get(idx, "col%d" % idx): mask_probe_value(col_names.get(idx, "col%d" % idx), value)
            for idx, value in enumerate(row)
        })

    # 枚举类字段的取值分布（去重值不超过 10 个的列）
    distributions = {}
    for idx in range(expected_cols):
        field_name = col_names.get(idx, "col%d" % idx)
        lname = field_name.lower()
        # 跳过可能包含 PII 或大文本的字段
        if any(x in lname for x in ("name", "content", "image", "path", "remark", "error")):
            continue
        values = [str(row[idx]) for row in rows if idx < len(row) and row[idx] not in (None, "")]
        # 跳过 URL、base64、长文本，避免报告体积膨胀和敏感内容泄露
        if any(v.startswith("data:") or v.startswith("http") or len(v) > 80 for v in values):
            continue
        uniq = Counter(values)
        if 1 <= len(uniq) <= 10:
            distributions[field_name] = dict(uniq.most_common(10))

    return {
        "patient_id": str(patient_id),
        "endpoint": endpoint,
        "url": url,
        "code": code,
        "file": file_path,
        "size_bytes": size,
        "elapsed_seconds": round(elapsed, 2),
        "row_count": len(rows),
        "row_length_distribution": dict(row_lengths),
        "distributions": distributions,
        "column_stats": column_stats(rows, expected_cols, col_names) if rows else [],
        "samples": samples,
    }


def pick_probe_ids(rows, count):
    """优先选有图片/知情同意数据的患者。"""
    pic_ids, consent_ids, other_ids = [], [], []
    for row in rows:
        if not row:
            continue
        pid = row[0]
        if pid is None:
            continue
        if len(row) > 32 and to_int(row[32]) > 0:
            pic_ids.append(pid)
        if len(row) > 51 and to_int(row[51]) > 0:
            consent_ids.append(pid)
        other_ids.append(pid)
    ordered = []
    for group in (pic_ids, consent_ids, other_ids):
        for pid in group:
            if pid not in ordered:
                ordered.append(pid)
            if len(ordered) >= count:
                return ordered
    return ordered


# ---------------------------------------------------------------- 本地库对比

def mysql_query(sql, user, password, database):
    env = os.environ.copy()
    env["MYSQL_PWD"] = password
    cmd = ["mysql", "-u", user, "-N", "-B", "--default-character-set=utf8mb4", database, "-e", sql]
    output = subprocess.check_output(cmd, env=env, stderr=subprocess.STDOUT)
    text = output.decode("utf-8", errors="replace")
    return [line.split("\t") for line in text.splitlines() if line.strip()]


def compare_with_database(rows, args):
    sql = ("SELECT COALESCE(eltm_id,''), COALESCE(idcard,''), COALESCE(self_tel,'') "
           "FROM datamain_patient WHERE is_deleted = 0")
    try:
        local_rows = mysql_query(sql, args.mysql_user, args.mysql_password, args.mysql_db)
    except FileNotFoundError:
        return {"error": "未找到 mysql 命令，请把 MySQL bin 目录加入 PATH"}
    except subprocess.CalledProcessError as exc:
        return {"error": "查询本地库失败：%s" % exc.output.decode("utf-8", errors="replace")}

    local_eltm = {r[0] for r in local_rows if len(r) > 0 and r[0]}
    local_idcard = {r[1] for r in local_rows if len(r) > 1 and r[1]}
    local_phone = {r[2] for r in local_rows if len(r) > 2 and r[2]}

    ext_ids, ext_idcards, ext_phones = set(), set(), set()
    for row in rows:
        if len(row) > 0 and row[0] is not None:
            ext_ids.add(str(row[0]))
        if len(row) > 3 and row[3]:
            ext_idcards.add(str(row[3]).strip())
        if len(row) > 2 and row[2]:
            ext_phones.add(str(row[2]).strip())

    eltm_hits = sorted(local_eltm & ext_ids)
    idcard_hits = sorted(local_idcard & ext_idcards)
    phone_hits = sorted(local_phone & ext_phones)

    return {
        "local_patient_rows": len(local_rows),
        "local_eltm_id_count": len(local_eltm),
        "external_id_count": len(ext_ids),
        "id_match_via_eltm_id": len(eltm_hits),
        "id_match_via_idcard": len(idcard_hits),
        "id_match_via_phone": len(phone_hits),
        "eltm_id_examples": eltm_hits[:10],
        "idcard_masked_examples": [mask_id_card(v) for v in idcard_hits[:10]],
        "phone_masked_examples": [mask_phone(v) for v in phone_hits[:10]],
    }


# ---------------------------------------------------------------- 报告

def write_report(report, out_dir):
    path = os.path.join(out_dir, "report.md")
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(report)
    return path


def build_cxlist_report(summary, detail_analysis, args):
    lines = []
    lines.append("# datamain 接口抽样验证报告\n")
    lines.append("- 生成时间：%s" % datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
    lines.append("- 接口地址：%s/datamain/cxList" % summary["base_url"])
    lines.append("- 响应文件：`%s`" % summary["file"])
    lines.append("- 响应大小：%.2f MB，下载耗时 %.1f 秒" % (
        summary["size_bytes"] / 1048576, summary["download_seconds"]))
    lines.append("- 返回 code：%r" % summary["code"])
    lines.append("- 行数：%d，期望列数：%d" % (summary["row_count"], summary["expected_cols"]))
    lines.append("- 实际列数分布：`%s`" % json.dumps(summary["row_length_distribution"], ensure_ascii=False))
    lines.append("")

    if summary["row_length_anomalies"]:
        lines.append("## 列数异常行（最多展示 20 条）\n")
        lines.append("| 行号 | 实际列数 | 患者 id |")
        lines.append("|------|----------|---------|")
        for item in summary["row_length_anomalies"][:20]:
            lines.append("| %d | %d | %s |" % (item["row"], item["length"], item["id"]))
        lines.append("")

    duplicates = summary["duplicate_ids"]
    lines.append("## 主键检查\n")
    if duplicates:
        lines.append("发现 **%d 个重复患者 id**：`%s`\n" % (
            len(duplicates), json.dumps(dict(list(duplicates.items())[:10]), ensure_ascii=False)))
    else:
        lines.append("患者 id（列 0）无重复。\n")

    lines.append("## 特殊字段分析\n")
    lines.append("### 年龄列（列 7）口径")
    lines.append("| 类型 | 数量 |")
    lines.append("|------|------|")
    for key, count in summary["age_categories"].items():
        lines.append("| %s | %d |" % (key, count))
    lines.append("")

    lines.append("### 出生日期（列 6）")
    lines.append("| 类型 | 数量 |")
    lines.append("|------|------|")
    for key, count in summary["birthday_categories"].items():
        lines.append("| %s | %d |" % (key, count))
    if summary["birthday_invalid_examples"]:
        lines.append("\n非法日期示例：`%s`\n" % "`, `".join(summary["birthday_invalid_examples"]))
    lines.append("")

    lines.append("### 性别编码")
    lines.append("- gender_code（列 4）：`%s`" % json.dumps(summary["gender_code_dist"], ensure_ascii=False))
    lines.append("- gender（列 5）：`%s`\n" % json.dumps(summary["gender_text_dist"], ensure_ascii=False))

    lines.append("## 列统计（66 列）\n")
    lines.append("| 列 | 字段 | 非空 | 空值 | 类型 | 示例（已脱敏） |")
    lines.append("|----|------|------|------|------|----------------|")
    for stat in summary["column_stats"]:
        examples = "；".join("%s(%d)" % (v, c) for v, c in stat["top_values"][:3])
        lines.append("| %d | %s | %d | %d | %s | %s |" % (
            stat["col"], stat["name"], stat["non_null"], stat["null"],
            json.dumps(stat["types"], ensure_ascii=False), examples.replace("|", "\\|")))
    lines.append("")

    lines.append("## JSON 明细列内部结构\n")
    for item in detail_analysis:
        lines.append("### 列 %d：%s\n" % (item["col"], item["name"]))
        lines.append("- 非空行数：%d，JSON 解析失败：%d" % (item["non_empty_rows"], item["invalid_json"]))
        lines.append("- 每行元素数分布（前 10）：`%s`" % json.dumps(
            dict(list(item["element_count_distribution"].items())[:10]), ensure_ascii=False))
        what = "元素内部" if item["col"] != 54 else "内部"
        lines.append("- %s字段（按出现次数排序，前 30）：\n" % what)
        lines.append("| 字段 | 出现次数 | 示例（已脱敏） |")
        lines.append("|------|----------|----------------|")
        for key, count in list(item["keys"].items())[:30]:
            example = json.dumps(item["key_examples"].get(key), ensure_ascii=False)
            if len(example) > 80:
                example = example[:80] + "..."
            lines.append("| %s | %d | %s |" % (key, count, example.replace("|", "\\|")))
        lines.append("")
        if item["samples"]:
            lines.append("样本（已脱敏）：\n```json\n%s\n```\n" % json.dumps(
                item["samples"][0], ensure_ascii=False, indent=2)[:1500])

    return "\n".join(lines)


def append_probe_report(lines, probe_results):
    lines.append("## 单患者接口抽样\n")
    if not probe_results:
        lines.append("本次未执行单患者抽样。\n")
        return lines
    for result in probe_results:
        lines.append("### %s（patient_id=%s）\n" % (result["endpoint"], result["patient_id"]))
        lines.append("- 行数：%d，列数分布：`%s`" % (
            result["row_count"], json.dumps(result["row_length_distribution"], ensure_ascii=False)))
        if result.get("distributions"):
            lines.append("- 枚举分布：`%s`" % json.dumps(result["distributions"], ensure_ascii=False))
        if result["samples"]:
            lines.append("- 样本（已脱敏）：\n```json\n%s\n```" % json.dumps(
                result["samples"][0], ensure_ascii=False, indent=2)[:1200])
        lines.append("")
    return lines


def append_db_report(lines, db_result):
    lines.append("## 与本地数据库对比\n")
    if not db_result:
        lines.append("本次未连接本地数据库。\n")
        return lines
    if "error" in db_result:
        lines.append("对比失败：%s\n" % db_result["error"])
        return lines
    lines.append("| 项目 | 数量 |")
    lines.append("|------|------|")
    lines.append("| 本地患者行数 | %d |" % db_result["local_patient_rows"])
    lines.append("| 本地已有 eltm_id 数 | %d |" % db_result["local_eltm_id_count"])
    lines.append("| 外部患者 id 数 | %d |" % db_result["external_id_count"])
    lines.append("| 外部 id 与本地 eltm_id 重合 | %d |" % db_result["id_match_via_eltm_id"])
    lines.append("| 外部身份证与本地 idcard 重合 | %d |" % db_result["id_match_via_idcard"])
    lines.append("| 外部手机号与本地 self_tel 重合 | %d |" % db_result["id_match_via_phone"])
    lines.append("")
    if db_result["eltm_id_examples"]:
        lines.append("重合示例（eltm_id）：`%s`\n" % "`, `".join(db_result["eltm_id_examples"]))
    return lines


# ---------------------------------------------------------------- 主流程

def parse_args(argv=None):
    parser = argparse.ArgumentParser(description="datamain 接口抽样验证")
    parser.add_argument("--base-url", default=DEFAULT_BASE_URL)
    parser.add_argument("--out-dir", default=DEFAULT_OUT_DIR)
    parser.add_argument("--cxlist", help="复用已下载的 cxList JSON 文件，不发起下载")
    parser.add_argument("--refresh", action="store_true", help="忽略缓存，重新下载 cxList")
    parser.add_argument("--skip-probe", action="store_true", help="不调用单患者接口")
    parser.add_argument("--probe-count", type=int, default=2, help="抽样患者数量，默认 2")
    parser.add_argument("--probe-id", action="append", default=[], help="指定患者 id，可重复")
    parser.add_argument("--timeout", type=int, default=180, help="单次请求超时秒数，默认 180")
    parser.add_argument("--retries", type=int, default=3, help="请求重试次数，默认 3")
    parser.add_argument("--mysql", action="store_true", help="对比本地 MySQL")
    parser.add_argument("--mysql-db", default="eksjk_db")
    parser.add_argument("--mysql-user", default="eksjk")
    parser.add_argument("--mysql-password", default="eksjk123")
    return parser.parse_args(argv)


def main(argv=None):
    args = parse_args(argv)
    out_dir = args.out_dir
    os.makedirs(out_dir, exist_ok=True)

    cache_path = os.path.join(out_dir, "cxlist.json")
    if args.cxlist:
        cxlist_path = args.cxlist
        print("使用已有响应文件：%s" % cxlist_path)
        size_bytes, download_seconds = os.path.getsize(cxlist_path), 0.0
    else:
        if os.path.exists(cache_path) and not args.refresh:
            cxlist_path = cache_path
            size_bytes, download_seconds = os.path.getsize(cache_path), 0.0
            print("使用缓存响应：%s（--refresh 可重新下载）" % cache_path)
        else:
            cxlist_path = cache_path
            print("开始下载 cxList（约 52MB）...")
            size_bytes, download_seconds = http_download(
                args.base_url + "/datamain/cxList", cxlist_path, args.timeout, args.retries)

    print("加载响应 ...")
    code, rows = load_payload(cxlist_path)
    rows = rows or []
    print("解析完成：%d 行" % len(rows))

    expected_cols = 66
    row_lengths = Counter(len(r) for r in rows)
    anomalies = []
    for index, row in enumerate(rows):
        if len(row) != expected_cols:
            anomalies.append({
                "row": index,
                "length": len(row),
                "id": mask_for_column(0, row[0]) if row else None,
            })

    ids = [row[0] for row in rows if row]
    birthday_analysis, birthday_invalid_examples = analyze_birthday(
        [row[6] if len(row) > 6 else None for row in rows])
    summary = {
        "base_url": args.base_url,
        "file": cxlist_path,
        "size_bytes": size_bytes,
        "download_seconds": round(download_seconds, 2),
        "code": code,
        "expected_cols": expected_cols,
        "row_count": len(rows),
        "row_length_distribution": dict(row_lengths),
        "row_length_anomalies": anomalies,
        "duplicate_ids": find_duplicates(ids),
        "column_stats": column_stats(rows, expected_cols, CX_COLUMNS),
        "age_categories": dict(analyze_age([row[7] if len(row) > 7 else None for row in rows])),
        "birthday_categories": dict(birthday_analysis),
        "birthday_invalid_examples": birthday_invalid_examples,
        "gender_code_dist": dict(Counter(
            str(row[4]) for row in rows if len(row) > 4 and row[4] not in (None, ""))),
        "gender_text_dist": dict(Counter(
            str(row[5]) for row in rows if len(row) > 5 and row[5] not in (None, ""))),
    }

    print("分析 JSON 明细列 ...")
    detail_analysis = [detail_key_analysis(rows, col) for col in JSON_DETAIL_COLUMNS]

    # 明细里的枚举分布
    summary["sign_status_dist"] = dict(detail_field_distribution(rows, 65, "sign_status"))
    summary["pdf_status_dist"] = dict(detail_field_distribution(rows, 65, "pdf_status"))
    summary["image_type_dist"] = dict(detail_field_distribution(rows, 58, "image_type"))
    summary["pic_source_type_dist"] = dict(detail_field_distribution(rows, 58, "pic_source_type"))

    probe_results = []
    if not args.skip_probe:
        probe_ids = args.probe_id or pick_probe_ids(rows, args.probe_count)
        print("抽样患者 id：%s" % ", ".join(str(x) for x in probe_ids))
        for pid in probe_ids:
            probe_results.append(probe_patient(
                args.base_url, pid, "/datamain/gettpbyid", 16, PIC_COLUMNS,
                out_dir, args.timeout, args.retries))
            probe_results.append(probe_patient(
                args.base_url, pid, "/datamain/getzqbyid", 25, CONSENT_COLUMNS,
                out_dir, args.timeout, args.retries))

    db_result = compare_with_database(rows, args) if args.mysql else None

    # 写 report.md
    lines = build_cxlist_report(summary, detail_analysis, args).splitlines()
    lines.append("## 明细枚举分布\n")
    lines.append("- sign_status（列 65）：`%s`" % json.dumps(summary["sign_status_dist"], ensure_ascii=False))
    lines.append("- pdf_status（列 65）：`%s`" % json.dumps(summary["pdf_status_dist"], ensure_ascii=False))
    lines.append("- image_type（列 58）：`%s`" % json.dumps(summary["image_type_dist"], ensure_ascii=False))
    lines.append("- pic_source_type（列 58）：`%s`" % json.dumps(
        summary["pic_source_type_dist"], ensure_ascii=False))
    lines.append("")
    append_probe_report(lines, probe_results)
    append_db_report(lines, db_result)
    report_path = write_report("\n".join(lines), out_dir)

    # 写 summary.json（不含明细样本，便于机器读取）
    summary_path = os.path.join(out_dir, "summary.json")
    with open(summary_path, "w", encoding="utf-8") as fh:
        json.dump({
            "summary": summary,
            "detail_analysis": detail_analysis,
            "probe_results": probe_results,
            "db_compare": db_result,
        }, fh, ensure_ascii=False, indent=2, default=str)

    print("")
    print("=" * 60)
    print("验证完成")
    print("  报告：%s" % report_path)
    print("  摘要：%s" % summary_path)
    print("  行数：%d，列数分布：%s" % (len(rows), dict(row_lengths)))
    print("  重复 id：%d，列数异常行：%d" % (len(summary["duplicate_ids"]), len(anomalies)))
    print("  年龄口径：%s" % json.dumps(summary["age_categories"], ensure_ascii=False))
    print("  出生日期：%s" % json.dumps(summary["birthday_categories"], ensure_ascii=False))
    print("  sign_status：%s" % json.dumps(summary["sign_status_dist"], ensure_ascii=False))
    print("  pdf_status：%s" % json.dumps(summary["pdf_status_dist"], ensure_ascii=False))
    if db_result and "error" not in db_result:
        print("  本地库重合：eltm_id=%d，身份证=%d，手机号=%d" % (
            db_result["id_match_via_eltm_id"], db_result["id_match_via_idcard"],
            db_result["id_match_via_phone"]))
    print("=" * 60)
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\n已取消")
        sys.exit(130)
    except Exception as exc:  # noqa: BLE001
        print("执行失败：%s" % exc, file=sys.stderr)
        sys.exit(1)

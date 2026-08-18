package com.eksjk.service;

import java.util.Map;

/**
 * 知情同意书外部同步服务
 *
 * @author eksjk
 */
public interface InformedConsentSyncService {

    /**
     * 从 E路童萌 同步知情同意书记录
     * 当前为占位实现（mock 数据），对接真实接口后替换
     */
    Map<String, Object> syncFromExternal();
}

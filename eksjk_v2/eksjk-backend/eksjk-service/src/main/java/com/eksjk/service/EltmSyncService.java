package com.eksjk.service;

import java.util.Map;

/**
 * ELTM 数据同步服务接口
 *
 * @author eksjk
 */
public interface EltmSyncService {

    /**
     * 从外部 V1 ELTM 系统同步数据
     * @return 同步结果 { totalRecords, newCount, updatedCount, failedCount }
     */
    Map<String, Object> syncFromExternal();

    /**
     * 获取同步状态
     * @return { lastSyncTime, syncedCount, totalEltmCount }
     */
    Map<String, Object> getSyncStatus();

}

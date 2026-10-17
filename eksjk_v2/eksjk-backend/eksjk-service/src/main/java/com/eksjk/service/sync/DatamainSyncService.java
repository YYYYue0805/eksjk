package com.eksjk.service.sync;

import com.eksjk.model.entity.EltmSyncLog;

import java.util.List;
import java.util.Map;

/**
 * datamain 数据同步服务
 *
 * @author eksjk
 */
public interface DatamainSyncService {

    /**
     * 从外部 cxList 同步全部患者
     *
     * @param triggerType MANUAL / SCHEDULED
     */
    Map<String, Object> syncFromExternal(String triggerType);

    /** 同步状态 */
    Map<String, Object> getSyncStatus();

    /** 最近同步日志 */
    List<EltmSyncLog> listRecentLogs(int limit);
}

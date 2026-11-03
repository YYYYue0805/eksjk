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

    /**
     * 从外部 cxList 同步患者
     *
     * @param force true 时忽略行指纹强制重处理
     * @param limit >0 时只处理前 N 行（用于小批验证，仍会下载全量响应）
     */
    Map<String, Object> syncFromExternal(String triggerType, boolean force, int limit);

    /**
     * 后台异步触发同步，立即返回
     */
    Map<String, Object> startAsync(String triggerType, boolean force, int limit);

    /** 同步状态 */
    Map<String, Object> getSyncStatus();

    /** 最近同步日志 */
    List<EltmSyncLog> listRecentLogs(int limit);
}

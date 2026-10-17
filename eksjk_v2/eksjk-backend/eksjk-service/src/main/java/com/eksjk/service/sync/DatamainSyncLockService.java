package com.eksjk.service.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 同步锁（MySQL GET_LOCK）
 *
 * <p>GET_LOCK 是连接级的，必须使用同一条连接获取和释放；
 * 因此单独占用一个连接，而不是走 MyBatis 连接池。</p>
 *
 * @author eksjk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatamainSyncLockService {

    private final DataSource dataSource;
    private Connection connection;

    public synchronized boolean acquire() {
        try {
            connection = dataSource.getConnection();
            try (PreparedStatement statement = connection.prepareStatement("SELECT GET_LOCK('eltm_sync', 0)");
                 ResultSet resultSet = statement.executeQuery()) {
                boolean acquired = resultSet.next() && resultSet.getInt(1) == 1;
                if (!acquired) {
                    closeConnection();
                }
                return acquired;
            }
        } catch (Exception e) {
            log.error("获取同步锁失败", e);
            closeConnection();
            return false;
        }
    }

    public synchronized void release() {
        if (connection == null) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement("SELECT RELEASE_LOCK('eltm_sync')")) {
            statement.executeQuery();
        } catch (Exception e) {
            log.warn("释放同步锁失败: {}", e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
                // 忽略关闭异常
            }
            connection = null;
        }
    }
}

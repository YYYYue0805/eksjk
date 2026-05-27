package com.eksjk.common.util;

import org.hashids.Hashids;

/**
 * Hashids 编解码工具类
 * <p>
 * 用于将数据库自增 ID 编码为短字符串，对外暴露时隐藏真实 ID。
 * 保持与 V1 版本相同的编码规则（salt、minLength 等参数一致），确保兼容性。
 * </p>
 *
 * @author eksjk
 */
public class HashidsUtil {

    /**
     * 与 V1 版本一致的 salt（Django SECRET_KEY）
     */
    private static final String SALT = "p@m&_v$ijv7hsd0w_4h4i#j9ra6j75-b*l)f$_27j+$#ix1i8y";

    /**
     * 与 V1 版本一致的最小编码长度
     */
    private static final int MIN_LENGTH = 5;

    /**
     * Hashids 实例（线程安全）
     */
    private static final Hashids HASHIDS = new Hashids(SALT, MIN_LENGTH);

    private HashidsUtil() {
        // 工具类禁止实例化
    }

    /**
     * 将数据库 ID 编码为 Hashid 字符串
     *
     * @param id 数据库自增 ID
     * @return 编码后的字符串
     */
    public static String encode(long id) {
        return HASHIDS.encode(id);
    }

    /**
     * 将 Hashid 字符串解码为数据库 ID
     *
     * @param hashid 编码后的字符串
     * @return 数据库自增 ID
     * @throws IllegalArgumentException 如果解码失败
     */
    public static long decode(String hashid) {
        long[] decoded = HASHIDS.decode(hashid);
        if (decoded == null || decoded.length == 0) {
            throw new IllegalArgumentException("无效的 Hashid: " + hashid);
        }
        return decoded[0];
    }

    /**
     * 安全解码，解码失败返回 null
     *
     * @param hashid 编码后的字符串
     * @return 数据库自增 ID，解码失败返回 null
     */
    public static Long decodeSafe(String hashid) {
        try {
            long[] decoded = HASHIDS.decode(hashid);
            if (decoded != null && decoded.length > 0) {
                return decoded[0];
            }
        } catch (Exception ignored) {
            // 解码失败，返回 null
        }
        return null;
    }
}

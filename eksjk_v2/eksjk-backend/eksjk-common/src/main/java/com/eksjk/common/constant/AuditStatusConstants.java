package com.eksjk.common.constant;

/**
 * 审核发放状态常量
 *
 * @author eksjk
 */
public final class AuditStatusConstants {

    private AuditStatusConstants() {
    }

    /** 待审核 */
    public static final String PENDING_REVIEW = "pending_review";

    /** 待发放（审核已通过） */
    public static final String PENDING_RELEASE = "pending_release";

    /** 已发放 */
    public static final String RELEASED = "released";

    /** 已驳回 */
    public static final String REJECTED = "rejected";
}

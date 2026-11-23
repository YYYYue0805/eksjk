package com.eksjk.common.constant;

/**
 * 审核发放状态常量
 *
 * @author eksjk
 */
public final class AuditStatusConstants {

    private AuditStatusConstants() {
    }

    /** 草稿（必填信息未完善，不参与审核发放） */
    public static final String DRAFT = "draft";

    /** 未上传（外部同步数据，需在病例管理手动上传后进入审核发放流程） */
    public static final String PENDING_UPLOAD = "pending_upload";

    /** 待审核 */
    public static final String PENDING_REVIEW = "pending_review";

    /** 待发放（审核已通过） */
    public static final String PENDING_RELEASE = "pending_release";

    /** 已发放 */
    public static final String RELEASED = "released";

    /** 已驳回 */
    public static final String REJECTED = "rejected";
}

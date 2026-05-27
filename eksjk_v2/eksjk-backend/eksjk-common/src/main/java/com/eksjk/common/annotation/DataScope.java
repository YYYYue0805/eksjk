package com.eksjk.common.annotation;

import java.lang.annotation.*;

/**
 * 数据范围过滤注解
 * <p>
 * 标注在 Service 方法上，AOP 切面会根据当前用户角色自动注入数据范围条件。
 * </p>
 *
 * @author eksjk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 医院 ID 字段别名（SQL 中的列名或别名）
     * 默认为 "hospital_id"
     */
    String hospitalAlias() default "hospital_id";

    /**
     * 创建人 ID 字段别名（SQL 中的列名或别名）
     * 默认为 "creator_id"
     */
    String creatorAlias() default "creator_id";
}

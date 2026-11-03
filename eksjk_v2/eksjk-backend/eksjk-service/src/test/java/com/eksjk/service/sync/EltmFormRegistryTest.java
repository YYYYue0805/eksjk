package com.eksjk.service.sync;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ELTM 表单注册表单元测试
 *
 * @author eksjk
 */
class EltmFormRegistryTest {

    @Test
    void shouldTrimFormNameAndMatchKnownForm() {
        assertThat(EltmFormRegistry.isHandledForm(" 人口学信息 ")).isTrue();
        assertThat(EltmFormRegistry.isHandledForm("儿童基础信息 ")).isTrue();
        assertThat(EltmFormRegistry.isHandledForm("未知表单")).isFalse();
        assertThat(EltmFormRegistry.isHandledForm(null)).isFalse();
    }
}

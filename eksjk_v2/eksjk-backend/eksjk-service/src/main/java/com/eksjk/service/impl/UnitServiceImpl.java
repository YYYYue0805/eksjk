package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.ErrorCode;
import com.eksjk.common.result.PageResult;
import com.eksjk.mapper.UnitMapper;
import com.eksjk.mapper.UserMapper;
import com.eksjk.model.dto.UnitDTO;
import com.eksjk.model.dto.UnitQueryDTO;
import com.eksjk.model.entity.Unit;
import com.eksjk.model.entity.User;
import com.eksjk.service.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 医疗机构服务实现类
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitMapper unitMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Unit> queryPage(UnitQueryDTO queryDTO) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();

        // V1 兼容：del_flg = '1' 表示有效数据
        wrapper.eq(Unit::getDelFlg, "1");

        // 关键词搜索（名称/编码）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                    .like(Unit::getUnitName, queryDTO.getKeyword())
                    .or()
                    .like(Unit::getUnitCode, queryDTO.getKeyword())
            );
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Unit::getStatus, queryDTO.getStatus());
        }

        // 机构级别筛选
        if (queryDTO.getUnitLevel() != null && !queryDTO.getUnitLevel().isEmpty()) {
            wrapper.eq(Unit::getUnitLevel, queryDTO.getUnitLevel());
        }

        // 机构类型筛选
        if (queryDTO.getUnitType() != null && !queryDTO.getUnitType().isEmpty()) {
            wrapper.eq(Unit::getUnitType, queryDTO.getUnitType());
        }

        wrapper.orderByDesc(Unit::getId);

        Page<Unit> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Unit> result = unitMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(),
                result.getCurrent(), result.getSize());
    }

    @Override
    public Unit getById(Long id) {
        Unit unit = unitMapper.selectById(id);
        if (unit == null || "0".equals(unit.getDelFlg())) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "机构不存在");
        }
        return unit;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(UnitDTO unitDTO) {
        // 校验名称唯一性
        checkNameUnique(unitDTO.getUnitName(), null);

        // 校验编码唯一性
        if (unitDTO.getUnitCode() != null && !unitDTO.getUnitCode().isEmpty()) {
            checkCodeUnique(unitDTO.getUnitCode(), null);
        }

        Unit unit = new Unit();
        copyDtoToEntity(unitDTO, unit);
        unit.setDelFlg("1");
        unit.setStatus(1);
        unit.setCreatedAt(LocalDateTime.now());
        unit.setUpdatedAt(LocalDateTime.now());

        unitMapper.insert(unit);
        log.info("新增机构: id={}, name={}", unit.getId(), unit.getUnitName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UnitDTO unitDTO) {
        Unit unit = getById(id);

        // 校验名称唯一性（排除自身）
        checkNameUnique(unitDTO.getUnitName(), id);

        // 校验编码唯一性（排除自身）
        if (unitDTO.getUnitCode() != null && !unitDTO.getUnitCode().isEmpty()) {
            checkCodeUnique(unitDTO.getUnitCode(), id);
        }

        copyDtoToEntity(unitDTO, unit);
        unit.setUpdatedAt(LocalDateTime.now());

        unitMapper.updateById(unit);
        log.info("编辑机构: id={}, name={}", id, unit.getUnitName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Unit unit = getById(id);
        unit.setStatus(status);
        unit.setUpdatedAt(LocalDateTime.now());
        unitMapper.updateById(unit);

        // 禁用机构时联动禁用该机构下所有用户
        if (status == 0) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUnit, String.valueOf(id));
            wrapper.eq(User::getIsActive, true);
            User updateUser = new User();
            updateUser.setIsActive(false);
            userMapper.update(updateUser, wrapper);
            log.info("禁用机构 {} 并联动禁用其下所有用户", id);
        }

        log.info("更新机构状态: id={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Unit unit = getById(id);

        // 检查关联数据
        String unitIdStr = String.valueOf(id);
        long userCount = unitMapper.countUsersByUnitId(unitIdStr);
        if (userCount > 0) {
            throw new BusinessException(ErrorCode.DATA_REFERENCED,
                    "该机构下有 " + userCount + " 个用户，无法删除");
        }

        // 逻辑删除（V1 兼容：del_flg = '0'）
        unit.setDelFlg("0");
        unit.setUpdatedAt(LocalDateTime.now());
        unitMapper.updateById(unit);
        log.info("删除机构: id={}, name={}", id, unit.getUnitName());
    }

    @Override
    public List<Map<String, Object>> getOptions(String keyword) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Unit::getDelFlg, "1");
        // 仅返回启用状态的机构
        wrapper.eq(Unit::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Unit::getUnitName, keyword);
        }
        wrapper.select(Unit::getId, Unit::getUnitName);
        wrapper.orderByAsc(Unit::getUnitName);

        List<Unit> units = unitMapper.selectList(wrapper);
        List<Map<String, Object>> options = new ArrayList<>();
        for (Unit unit : units) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", unit.getId());
            option.put("name", unit.getUnitName());
            options.add(option);
        }
        return options;
    }

    @Override
    public Map<String, Long> getStatistics(Long id) {
        String unitIdStr = String.valueOf(id);
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", unitMapper.countUsersByUnitId(unitIdStr));
        try {
            stats.put("patientCount", unitMapper.countPatientsByUnitId(unitIdStr));
        } catch (Exception e) {
            // 病例表可能还不存在
            stats.put("patientCount", 0L);
        }
        return stats;
    }

    // ==================== 私有方法 ====================

    private void checkNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Unit::getUnitName, name);
        wrapper.eq(Unit::getDelFlg, "1");
        if (excludeId != null) {
            wrapper.ne(Unit::getId, excludeId);
        }
        if (unitMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_EXIST, "机构名称已存在");
        }
    }

    private void checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Unit::getUnitCode, code);
        wrapper.eq(Unit::getDelFlg, "1");
        if (excludeId != null) {
            wrapper.ne(Unit::getId, excludeId);
        }
        if (unitMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_EXIST, "机构编码已存在");
        }
    }

    private void copyDtoToEntity(UnitDTO dto, Unit entity) {
        entity.setUnitName(dto.getUnitName());
        entity.setUnitShortName(dto.getUnitShortName());
        entity.setUnitCode(dto.getUnitCode());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setContactAddress(dto.getContactAddress());
        entity.setZipCode(dto.getZipCode());
        entity.setUnitLevel(dto.getUnitLevel());
        entity.setUnitType(dto.getUnitType());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setRemark(dto.getRemark());
    }
}

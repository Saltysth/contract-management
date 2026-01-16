package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contract.management.infrastructure.entity.ContractTaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同任务Mapper
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ContractTaskMapper extends BaseMapper<ContractTaskEntity> {
}
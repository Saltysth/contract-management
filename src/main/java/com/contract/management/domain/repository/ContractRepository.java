package com.contract.management.domain.repository;

import com.contract.common.constant.ContractStatus;
import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.model.valueobject.ContractName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 合同仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ContractRepository {
    
    // ==================== 基础CRUD ====================
    
    /**
     * 保存合同
     *
     * @param contract 合同
     * @return 保存后的合同
     */
    Contract save(Contract contract);

    /**
     * 更新合同
     *
     * @param contract 合同
     * @return 更新后的合同
     */
    Contract update(Contract contract);
    
    /**
     * 根据ID查找合同
     *
     * @param id 合同ID
     * @return 合同
     */
    Optional<Contract> findById(ContractId id);
    
    /**
     * 删除合同（软删除）
     *
     * @param id 合同ID
     */
    void delete(ContractId id);
    
    /**
     * 检查合同是否存在
     *
     * @param id 合同ID
     * @return 是否存在
     */
    boolean existsById(ContractId id);
    
    // ==================== 查询方法 ====================
    
    /**
     * 根据过滤条件分页查询合同
     *
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Contract> findByFilters(ContractFilters filters, Pageable pageable);
    
    /**
     * 根据过滤条件查询合同列表
     *
     * @param filters 过滤条件
     * @return 合同列表
     */
    List<Contract> findByFilters(ContractFilters filters);
    
    /**
     * 根据类型查找合同
     *
     * @param type 合同类型
     * @return 合同列表
     */
    List<Contract> findByType(ContractType type);
    
    /**
     * 根据创建人查找合同
     *
     * @param userId 用户ID
     * @return 合同列表
     */
    List<Contract> findByCreatedBy(Long userId);
    
    /**
     * 查找即将到期的合同
     *
     * @param beforeDate 截止日期
     * @return 合同列表
     */
    List<Contract> findExpiringContracts(LocalDate beforeDate);
    
    /**
     * 根据附件UUID查找合同
     *
     * @param attachmentUuid 附件UUID
     * @return 合同
     */
    Optional<Contract> findByAttachmentUuid(String attachmentUuid);
    
    // ==================== 统计方法 ====================
    
    /**
     * 根据类型统计合同数量
     *
     * @param type 合同类型
     * @return 数量
     */
    long countByType(ContractType type);
    
    /**
     * 根据创建人统计合同数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    long countByCreatedBy(Long userId);
    
    /**
     * 获取类型统计
     *
     * @return 类型统计Map
     */
    Map<ContractType, Long> getTypeStatistics();

    // ==================== 业务查询 ====================
    
    /**
     * 检查合同名称是否已存在（同一创建人）
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @return 是否存在
     */
    boolean existsByNameAndCreatedBy(String contractName, Long createdBy);

    /**
     * 检查合同名称是否已存在（同一创建人，排除指定合同）
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @param excludeContractId 排除的合同ID
     * @return 是否存在
     */
    boolean existsByNameAndCreatedByAndIdNot(String contractName, Long createdBy, ContractId excludeContractId);
    
    /**
     * 查找需要关注的合同（即将到期等）
     *
     * @return 合同列表
     */
    List<Contract> findContractsNeedingAttention();

    // ==================== 批量操作 ====================
    
    /**
     * 批量保存合同
     *
     * @param contracts 合同列表
     * @return 保存后的合同列表
     */
    List<Contract> saveAll(List<Contract> contracts);
    
    /**
     * 批量删除合同
     *
     * @param contractIds 合同ID列表
     */
    void deleteAll(List<ContractId> contractIds);
    
    // ============== 逻辑删除专用操作 ==============
    /**
     * 标记逻辑删除（仅更新 is_deleted 及必要审计字段）
     *
     * @param contractName
     * @param id           合同ID
     * @param operatorId   操作者ID（用于审计）
     * @return
     */
    boolean markDeleted(ContractName contractName, ContractId id, Long operatorId);
    
    /**
     * 恢复逻辑删除（仅更新 is_deleted 及必要审计字段）
     * @param id 合同ID
     * @param operatorId 操作者ID（用于审计）
     */
    void restoreDeleted(ContractId id, Long operatorId);
}
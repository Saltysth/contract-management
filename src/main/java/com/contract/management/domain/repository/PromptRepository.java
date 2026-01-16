package com.contract.management.domain.repository;

import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.PromptId;
import com.contract.management.domain.model.valueobject.PromptName;
import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 提示词模板仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface PromptRepository {

    // ==================== 基础CRUD ====================

    /**
     * 保存提示词模板
     *
     * @param prompt 提示词模板
     * @return 保存后的提示词模板
     */
    Prompt save(Prompt prompt);

    /**
     * 更新提示词模板
     *
     * @param prompt 提示词模板
     * @return 更新后的提示词模板
     */
    Prompt update(Prompt prompt);

    /**
     * 根据ID查找提示词模板
     *
     * @param id 提示词模板ID
     * @return 提示词模板
     */
    Optional<Prompt> findById(PromptId id);

    /**
     * 删除提示词模板
     *
     * @param id 提示词模板ID
     */
    void delete(PromptId id);

    /**
     * 检查提示词模板是否存在
     *
     * @param id 提示词模板ID
     * @return 是否存在
     */
    boolean existsById(PromptId id);

    // ==================== 查询方法 ====================


    Prompt findOneByFilters(PromptFilters filters);

    /**
     * 根据过滤条件分页查询提示词模板
     *
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Prompt> findByFilters(PromptFilters filters, Pageable pageable);

    /**
     * 根据过滤条件查询提示词模板列表
     *
     * @param filters 过滤条件
     * @return 提示词模板列表
     */
    List<Prompt> findByFilters(PromptFilters filters);

    /**
     * 根据类型查找提示词模板
     *
     * @param type 提示词模板类型
     * @return 提示词模板列表
     */
    List<Prompt> findByType(PromptType type);

    /**
     * 根据类型查找启用的提示词模板
     *
     * @param type 提示词模板类型
     * @return 提示词模板列表
     */
    List<Prompt> findEnabledByType(PromptType type);

    /**
     * 根据角色查找提示词模板
     *
     * @param role 提示词角色
     * @return 提示词模板列表
     */
    List<Prompt> findByRole(PromptRole role);

    /**
     * 根据创建人查找提示词模板
     *
     * @param userId 用户ID
     * @return 提示词模板列表
     */
    List<Prompt> findByCreatedBy(Long userId);

    /**
     * 查找所有启用的提示词模板
     *
     * @return 提示词模板列表
     */
    List<Prompt> findEnabledPrompts();

    /**
     * 查找所有禁用的提示词模板
     *
     * @return 提示词模板列表
     */
    List<Prompt> findDisabledPrompts();

    // ==================== 统计方法 ====================

    /**
     * 根据类型统计提示词模板数量
     *
     * @param type 提示词模板类型
     * @return 数量
     */
    long countByType(PromptType type);

    /**
     * 根据角色统计提示词模板数量
     *
     * @param role 提示词角色
     * @return 数量
     */
    long countByRole(PromptRole role);

    /**
     * 根据创建人统计提示词模板数量
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
    Map<PromptType, Long> getTypeStatistics();

    /**
     * 获取角色统计
     *
     * @return 角色统计Map
     */
    Map<PromptRole, Long> getRoleStatistics();

    // ==================== 业务查询 ====================

    /**
     * 检查提示词模板名称是否已存在（同一类型下启用的模板）
     *
     * @param promptName 提示词模板名称
     * @param type       提示词模板类型
     * @return 是否存在
     */
    boolean existsByNameAndType(String promptName, PromptType type);

    /**
     * 检查提示词模板名称是否已存在（排除指定ID）
     *
     * @param promptName 提示词模板名称
     * @param type       提示词模板类型
     * @param excludeId  排除的模板ID
     * @return 是否存在
     */
    boolean existsByNameAndTypeExcludeId(String promptName, PromptType type, PromptId excludeId);

    // ==================== 批量操作 ====================

    /**
     * 批量保存提示词模板
     *
     * @param prompts 提示词模板列表
     * @return 保存后的提示词模板列表
     */
    List<Prompt> saveAll(List<Prompt> prompts);

    /**
     * 批量删除提示词模板
     *
     * @param promptIds 提示词模板ID列表
     */
    void deleteAll(List<PromptId> promptIds);

    /**
     * 批量启用提示词模板
     *
     * @param promptIds 提示词模板ID列表
     */
    void enableAll(List<PromptId> promptIds);

    /**
     * 批量禁用提示词模板
     *
     * @param promptIds 提示词模板ID列表
     */
    void disableAll(List<PromptId> promptIds);
}
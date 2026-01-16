package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.infrastructure.entity.PromptEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 提示词模板数据访问接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface PromptMapper extends BaseMapper<PromptEntity> {

    /**
     * 通过id查找提示词模板
     *
     * @param id 主键
     * @return 提示词模板实体
     */
    @Select("SELECT * FROM prompt WHERE id = #{id}")
    PromptEntity findById(@Param("id") Long id);

    /**
     * 根据类型查找提示词模板
     *
     * @param promptType 提示词模板类型
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE prompt_type = #{promptType}")
    List<PromptEntity> findByType(@Param("promptType") String promptType);

    /**
     * 根据类型查找启用的提示词模板
     *
     * @param promptType 提示词模板类型
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE prompt_type = #{promptType} AND enabled = true")
    List<PromptEntity> findEnabledByType(@Param("promptType") String promptType);

    /**
     * 根据角色查找提示词模板
     *
     * @param promptRole 提示词角色
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE prompt_role = #{promptRole}")
    List<PromptEntity> findByRole(@Param("promptRole") String promptRole);

    /**
     * 根据创建人查找提示词模板
     *
     * @param createdBy 创建人ID
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE created_by = #{createdBy}")
    List<PromptEntity> findByCreatedBy(@Param("createdBy") Long createdBy);

    /**
     * 查找所有启用的提示词模板
     *
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE enabled = true")
    List<PromptEntity> findEnabledPrompts();

    /**
     * 查找所有禁用的提示词模板
     *
     * @return 提示词模板列表
     */
    @Select("SELECT * FROM prompt WHERE enabled = false")
    List<PromptEntity> findDisabledPrompts();

    /**
     * 检查提示词模板名称是否已存在（同一类型下启用的模板）
     *
     * @param promptName 提示词模板名称
     * @param promptType 提示词模板类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM prompt WHERE prompt_name = #{promptName} AND prompt_type = #{promptType} AND enabled = true")
    long countByNameAndType(@Param("promptName") String promptName, @Param("promptType") String promptType);

    /**
     * 检查提示词模板名称是否已存在（排除指定ID）
     *
     * @param promptName 提示词模板名称
     * @param promptType 提示词模板类型
     * @param excludeId  排除的模板ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM prompt WHERE prompt_name = #{promptName} AND prompt_type = #{promptType} AND enabled = true AND id != #{excludeId}")
    long countByNameAndTypeExcludeId(@Param("promptName") String promptName, @Param("promptType") String promptType, @Param("excludeId") Long excludeId);

    /**
     * 根据类型统计提示词模板数量
     *
     * @param promptType 提示词模板类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM prompt WHERE prompt_type = #{promptType}")
    long countByType(@Param("promptType") String promptType);

    /**
     * 根据角色统计提示词模板数量
     *
     * @param promptRole 提示词角色
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM prompt WHERE prompt_role = #{promptRole}")
    long countByRole(@Param("promptRole") String promptRole);

    /**
     * 根据创建人统计提示词模板数量
     *
     * @param createdBy 创建人ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM prompt WHERE created_by = #{createdBy}")
    long countByCreatedBy(@Param("createdBy") Long createdBy);

    /**
     * 获取类型统计
     *
     * @return 统计结果
     */
    @Select("SELECT prompt_type as type, COUNT(*) as count FROM prompt GROUP BY prompt_type")
    List<Map<String, Object>> getTypeStatistics();

    /**
     * 获取角色统计
     *
     * @return 统计结果
     */
    @Select("SELECT prompt_role as role, COUNT(*) as count FROM prompt GROUP BY prompt_role")
    List<Map<String, Object>> getRoleStatistics();

    /**
     * 复杂条件分页查询
     *
     * @param page            分页参数
     * @param promptType      提示词模板类型
     * @param promptRole      提示词角色
     * @param enabled         是否启用
     * @param responseFormat  响应格式
     * @param createdBy       创建人
     * @param keyword         关键词（搜索名称和备注）
     * @param startTimeStart  创建时间开始范围
     * @param startTimeEnd    创建时间结束范围
     * @param updateTimeStart 更新时间开始范围
     * @param updateTimeEnd   更新时间结束范围
     * @return 分页结果
     */
    IPage<PromptEntity> selectByFilters(
        Page<PromptEntity> page,
        @Param("promptType") String promptType,
        @Param("promptRole") String promptRole,
        @Param("enabled") Boolean enabled,
        @Param("responseFormat") String responseFormat,
        @Param("createdBy") Long createdBy,
        @Param("keyword") String keyword,
        @Param("startTimeStart") LocalDateTime startTimeStart,
        @Param("startTimeEnd") LocalDateTime startTimeEnd,
        @Param("updateTimeStart") LocalDateTime updateTimeStart,
        @Param("updateTimeEnd") LocalDateTime updateTimeEnd
    );

    /**
     * 批量启用提示词模板
     *
     * @param ids 提示词模板ID列表
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Update("<script>" +
            "UPDATE prompt SET enabled = true, updated_time = #{updateTime} WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchEnable(@Param("ids") List<Long> ids, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 批量禁用提示词模板
     *
     * @param ids 提示词模板ID列表
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Update("<script>" +
            "UPDATE prompt SET enabled = false, updated_time = #{updateTime} WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDisable(@Param("ids") List<Long> ids, @Param("updateTime") LocalDateTime updateTime);
}
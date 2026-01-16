package com.contract.management.interfaces.rest.api.v1.convertor;

import com.contract.management.application.dto.*;
import com.contract.management.interfaces.rest.api.v1.dto.request.*;
import com.contract.management.interfaces.rest.api.v1.dto.response.ReviewRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 审查规则REST转换器
 * 用于REST DTO和应用DTO之间的转换
 * 命名以Mapping结尾避免与MyBatis的Mapper冲突
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ReviewRuleRestConvertor {

    /**
     * 创建请求转应用DTO
     */
    CreateReviewRuleCommand toApplicationDTO(CreateReviewRuleRequest request);

    /**
     * 更新请求转应用DTO
     */
    UpdateReviewRuleCommand toApplicationDTO(UpdateReviewRuleRequest request);

    /**
     * 搜索请求转查询请求
     */
    ReviewRuleQueryRequest toQueryRequest(ReviewRuleSearchRequest searchRequest);

    /**
     * 应用DTO转响应DTO
     */
    ReviewRuleResponse toResponse(ReviewRuleDTO dto);
}
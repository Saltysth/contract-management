package com.contract.management.interfaces.rest.api.v1.convertor;

import com.contract.management.application.dto.CreatePromptRequest;
import com.contract.management.application.dto.PromptDTO;
import com.contract.management.application.dto.UpdatePromptRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.*;
import com.contract.management.interfaces.rest.api.v1.dto.response.PromptResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 提示词模板REST接口转换器 - MapStruct实现
 * 负责REST DTO与应用DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface PromptRestConvertor {

    PromptRestConvertor INSTANCE = Mappers.getMapper(PromptRestConvertor.class);

    /**
     * 应用DTO转换为响应DTO
     *
     * @param promptDTO 应用DTO
     * @return 响应DTO
     */
    PromptResponse toResponse(PromptDTO promptDTO);

    /**
     * 应用DTO列表转换为响应DTO列表
     *
     * @param promptDTOs 应用DTO列表
     * @return 响应DTO列表
     */
    List<PromptResponse> toResponseList(List<PromptDTO> promptDTOs);

    /**
     * REST创建请求转换为应用DTO
     *
     * @param request REST创建请求
     * @return 应用DTO
     */
    com.contract.management.application.dto.CreatePromptRequest toApplicationDTO(CreatePromptRequest request);

    /**
     * REST更新请求转换为应用DTO
     *
     * @param request REST更新请求
     * @return 应用DTO
     */
    com.contract.management.application.dto.UpdatePromptRequest toApplicationDTO(UpdatePromptRequest request);
}
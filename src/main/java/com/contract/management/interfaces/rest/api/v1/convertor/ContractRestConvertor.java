package com.contract.management.interfaces.rest.api.v1.convertor;

import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.interfaces.rest.api.v1.dto.request.ContractQueryRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateContractRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdateContractRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 合同REST接口转换器 - MapStruct实现
 * 负责REST DTO与应用DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ContractRestConvertor {
    
    ContractRestConvertor INSTANCE = Mappers.getMapper(ContractRestConvertor.class);
    
    /**
     * 创建请求转换为应用DTO
     *
     * @param request 创建请求
     * @return 应用DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "objectVersionNumber", ignore = true)
    ContractDTO toApplicationDTO(CreateContractRequest request);
    
    /**
     * 更新请求转换为应用DTO
     *
     * @param request 更新请求
     * @return 应用DTO
     */
    @Mapping(target = "contractType", ignore = true) // 合同类型创建后不可修改
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    ContractDTO toApplicationDTO(UpdateContractRequest request);
    
    /**
     * 应用DTO转换为响应DTO
     *
     * @param dto 应用DTO
     * @return 响应DTO
     */
    ContractResponse toResponse(ContractDTO dto);
    
    /**
     * 查询请求转换为应用查询DTO
     *
     * @param request 查询请求
     * @return 应用查询DTO
     */
    ContractQueryDTO toApplicationQueryDTO(ContractQueryRequest request);
    
    /**
     * PartyInfo请求转换为应用DTO
     */
    ContractDTO.PartyInfoDTO toApplicationPartyDTO(CreateContractRequest.PartyInfoRequest request);
    
    /**
     * PartyInfo应用DTO转换为响应DTO
     */
    ContractResponse.PartyInfoResponse toResponsePartyDTO(ContractDTO.PartyInfoDTO dto);
    
    /**
     * 批量转换 - 应用DTO转响应DTO
     */
    List<ContractResponse> toResponseList(List<ContractDTO> dtos);
}
package com.contract.management.interfaces.feign.convertor;

import com.contract.common.feign.dto.ContractFeignDTO;
import com.contract.common.feign.dto.ContractPageResultFeignDTO;
import com.contract.common.feign.dto.ContractQueryFeignDTO;
import com.contract.common.feign.dto.UpdateContractFeignRequest;
import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateContractRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangcong@SaltyFish
 * @description
 * @since 2025/10/15 19:52
 */
@Slf4j
public class ContractFeignConvertor {

    /**
     * 应用层DTO转Feign DTO
     *
     * @param contractDTO 应用层DTO
     * @return Feign DTO
     */
    public static ContractFeignDTO convertToFeignDTO(ContractDTO contractDTO) {
        if (contractDTO == null) {
            return null;
        }

        ContractFeignDTO feignDTO = new ContractFeignDTO();

        // 基本信息
        feignDTO.setId(contractDTO.getId());
        feignDTO.setContractName(contractDTO.getContractName());
        feignDTO.setContractType(contractDTO.getContractType());

        // 当事方信息
        if (contractDTO.getPartyA() != null) {
            ContractFeignDTO.PartyInfoFeignDTO partyA = new ContractFeignDTO.PartyInfoFeignDTO();
            partyA.setCompanyName(contractDTO.getPartyA().getCompanyName());
            partyA.setContactInfo(contractDTO.getPartyA().getContactInfo());
            feignDTO.setPartyA(partyA);
        }

        if (contractDTO.getPartyB() != null) {
            ContractFeignDTO.PartyInfoFeignDTO partyB = new ContractFeignDTO.PartyInfoFeignDTO();
            partyB.setCompanyName(contractDTO.getPartyB().getCompanyName());
            partyB.setContactInfo(contractDTO.getPartyB().getContactInfo());
            feignDTO.setPartyB(partyB);
        }

        // 金额信息
        feignDTO.setContractAmount(contractDTO.getContractAmount());

        // 期限信息
        feignDTO.setSignDate(contractDTO.getSignDate());
        feignDTO.setEffectiveDate(contractDTO.getEffectiveDate());
        feignDTO.setExpiryDate(contractDTO.getExpiryDate());

        // 其他信息
        feignDTO.setAttachmentUuid(contractDTO.getAttachmentUuid());
        feignDTO.setCreatedBy(contractDTO.getCreatedBy());
        feignDTO.setCreatedTime(contractDTO.getCreatedTime());
        feignDTO.setUpdatedTime(contractDTO.getUpdatedTime());

        return feignDTO;
    }

    /**
     * Feign查询DTO转应用查询DTO
     *
     * @param feignQuery Feign查询DTO
     * @return 应用查询DTO
     */
    public static ContractQueryDTO convertToApplicationQuery(ContractQueryFeignDTO feignQuery) {
        if (feignQuery == null) {
            return new ContractQueryDTO();
        }

        ContractQueryDTO query = new ContractQueryDTO();
        query.setContractName(feignQuery.getContractName());
        query.setContractTypes(feignQuery.getContractTypes());
        query.setPartyAName(feignQuery.getPartyAName());
        query.setPartyBName(feignQuery.getPartyBName());
        query.setMinAmount(feignQuery.getMinAmount());
        query.setMaxAmount(feignQuery.getMaxAmount());
        query.setSignDateStart(feignQuery.getSignDateStart());
        query.setSignDateEnd(feignQuery.getSignDateEnd());
        query.setEffectiveDateStart(feignQuery.getEffectiveDateStart());
        query.setEffectiveDateEnd(feignQuery.getEffectiveDateEnd());
        query.setExpiryDateStart(feignQuery.getExpiryDateStart());
        query.setExpiryDateEnd(feignQuery.getExpiryDateEnd());

        // Feign版本支持多个创建人ID，取第一个
        if (feignQuery.getCreatedByList() != null && !feignQuery.getCreatedByList().isEmpty()) {
            query.setCreatedBy(feignQuery.getCreatedByList().get(0));
        }

        query.setIncludeDeleted(feignQuery.getIncludeDeleted());

        return query;
    }

    /**
     * UpdateContractFeignRequest转CreateContractRequest
     *
     * @param updateRequest 更新请求
     * @return 创建请求
     */
    public static CreateContractRequest convertToUpdateRequest(UpdateContractFeignRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }

        CreateContractRequest createRequest = new CreateContractRequest();

        // 基本信息
        createRequest.setContractName(updateRequest.getContractName());

        // 甲乙方信息转换
        if (updateRequest.getPartyA() != null) {
            CreateContractRequest.PartyInfoRequest partyA = new CreateContractRequest.PartyInfoRequest();
            partyA.setCompanyName(updateRequest.getPartyA().getCompanyName());
            partyA.setContactInfo(updateRequest.getPartyA().getContactInfo());
            partyA.setAddress(updateRequest.getPartyA().getAddress());
            createRequest.setPartyA(partyA);
        }

        if (updateRequest.getPartyB() != null) {
            CreateContractRequest.PartyInfoRequest partyB = new CreateContractRequest.PartyInfoRequest();
            partyB.setCompanyName(updateRequest.getPartyB().getCompanyName());
            partyB.setContactInfo(updateRequest.getPartyB().getContactInfo());
            partyB.setAddress(updateRequest.getPartyB().getAddress());
            createRequest.setPartyB(partyB);
        }

        // 金额信息
        createRequest.setContractAmount(updateRequest.getContractAmount());

        // 期限信息
        createRequest.setSignDate(updateRequest.getSignDate());
        createRequest.setEffectiveDate(updateRequest.getEffectiveDate());
        createRequest.setExpiryDate(updateRequest.getExpiryDate());

        // 其他信息
        createRequest.setDescription(updateRequest.getDescription());
        createRequest.setAttachmentUuid(updateRequest.getAttachmentUuid());
        createRequest.setTags(updateRequest.getTags());

        return createRequest;
    }

    /**
     * 应用分页结果转Feign分页结果
     *
     * @param pageResult Spring Data Page对象
     * @return Feign分页结果
     */
    public static ContractPageResultFeignDTO convertToFeignPageResult(Page<ContractDTO> pageResult) {
        if (pageResult == null) {
            return ContractPageResultFeignDTO.empty();
        }

        log.debug("转换分页结果，总记录数: {}, 当前页: {}, 每页大小: {}",
            pageResult.getTotalElements(), pageResult.getNumber() + 1, pageResult.getSize());

        // 转换数据列表
        List<ContractFeignDTO> records = new ArrayList<>();
        pageResult.getContent().forEach(contractDTO -> {
            ContractFeignDTO feignDTO = convertToFeignDTO(contractDTO);
            if (feignDTO != null) {
                records.add(feignDTO);
            }
        });

        // 构建分页结果
        return ContractPageResultFeignDTO.of(
            records,
            pageResult.getTotalElements(),
            pageResult.getNumber() + 1, // Spring Data页码从0开始，Feign从1开始
            pageResult.getSize()
        );
    }
}

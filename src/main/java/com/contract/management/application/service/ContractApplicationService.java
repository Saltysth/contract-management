package com.contract.management.application.service;

import com.contract.management.application.convertor.ContractApplicationConvertor;
import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.repository.ContractFilters;
import com.contract.management.domain.service.ContractDomainService;
import com.contract.management.domain.service.CosService;
import com.contract.management.infrastructure.util.FileDownloadUtil;
import com.contractreview.fileapi.client.FileClient;
import com.contractreview.fileapi.dto.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 合同应用服务
 * 负责合同的业务流程编排和DTO转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractApplicationService {
    private final FileClient fileClient;
    private final ContractDomainService contractDomainService;
    private final ContractApplicationConvertor contractApplicationConvertor;
    private final FileDownloadUtil fileDownloadUtil;
    private final CosService cosService;
    @Value("${file.download.type}")
    private String uploadType;

    /**
     * 保存或更新合同（根据ID是否存在判断）
     *
     * @param contractDTO 合同DTO
     * @return 保存或更新后的合同DTO
     */
    @Transactional
    public ContractDTO saveOrUpdateContract(ContractDTO contractDTO) {
        Long dtoId = contractDTO.getId();
        log.info("保存或更新合同: {}", dtoId);

        FileInfoResponse fileInfoResponse = fileClient.queryByUuid(contractDTO.getAttachmentUuid());
        contractDTO.setContractName(fileInfoResponse.getFileName());
        String fileName = fileInfoResponse.getUuid() + "_" + fileInfoResponse.getFileName();

        if (dtoId == null) {
            try {
                // 使用文件UUID作为本地文件名，确保唯一性
                if ("local".equals(uploadType)) {
                    // 使用fileClient下载文件内容并保存到本地
                    downloadFileFromClient(fileInfoResponse.getUuid(), fileName);
                } else if ("cos".equals(uploadType)) {
                    InputStream inputStream = fileClient.downloadByUuid(fileInfoResponse.getUuid());
                    cosService.uploadSingleFile(inputStream, fileName, fileInfoResponse.getFileSize());
                }
                log.info("任务中合同附件上传成功: uuid={} -> fileName={}", fileInfoResponse.getUuid(), fileName);
            } catch (Exception e) {
                log.error("任务中保存合同附件失败: uuid={}", fileInfoResponse.getUuid(), e);
                // 下载失败不影响合同创建，但记录错误日志
            }
        }

        // 在应用层判断ID是否存在，仅用于编排，不包含业务规则
        boolean exists = false;
        ContractId cid = null;
        if (dtoId != null) {
            cid = ContractId.of(dtoId);
            exists = contractDomainService.findById(cid).isPresent();
        }
        
        // DTO -> 领域模型（纯映射，无创建/更新分支判断）
        Contract contract = contractApplicationConvertor.toDomain(contractDTO);
        
        // 仅做编排，具体校验与持久化由领域服务负责
        Contract persisted = exists
            ? contractDomainService.updateContract(contract)
            : contractDomainService.createContract(contract);
        
        // 领域模型 -> DTO
        ContractDTO result = contractApplicationConvertor.toDTO(persisted);
        log.info("合同保存/更新成功，ID: {}", result.getId());
        return result;
    }
    
    /**
     * 更新合同
     *
     * @param contractDTO 合同DTO
     * @return 更新后的合同DTO
     */
    @Transactional
    public ContractDTO updateContract(ContractDTO contractDTO) {
        log.info("更新合同: {}", contractDTO.getId());
        
        // 验证合同是否存在
        ContractId contractId = ContractId.of(contractDTO.getId());
        Optional<Contract> existingContract = contractDomainService.findById(contractId);
        if (existingContract.isEmpty()) {
            throw new IllegalArgumentException("合同不存在: " + contractDTO.getId());
        }
        
        // DTO转换为领域模型（纯映射）
        Contract contract = contractApplicationConvertor.toDomain(contractDTO);
        
        // 交给领域服务执行业务校验与持久化更新
        Contract updatedContract = contractDomainService.updateContract(contract);
        
        // 领域模型转换为DTO返回
        ContractDTO result = contractApplicationConvertor.toDTO(updatedContract);
        
        log.info("合同更新成功，ID: {}", result.getId());
        return result;
    }
    
    /**
     * 根据ID获取合同
     *
     * @param contractId 合同ID
     * @return 合同DTO，如果不存在则返回null
     */
    @Transactional(readOnly = true)
    public ContractDTO getContractById(Long contractId) {
        log.debug("获取合同详情: {}", contractId);
        
        ContractId id = ContractId.of(contractId);
        Optional<Contract> contract = contractDomainService.findById(id);
        
        return contract.map(contractApplicationConvertor::toDTO).orElse(null);
    }
    
    /**
     * 分页查询合同
     *
     * @param queryDTO 查询条件
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<ContractDTO> findContracts(ContractQueryDTO queryDTO, int pageNum, int pageSize) {
        log.debug("分页查询合同，条件: {}, 页码: {}, 每页: {}", queryDTO, pageNum, pageSize);
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(
            pageNum - 1, // Spring Data的页码从0开始
            pageSize,
            Sort.by(Sort.Direction.DESC, "createdTime")
        );
        
        // 转换查询条件
        ContractFilters filters = convertToFilters(queryDTO);
        
        // 调用仓储层查询
        Page<Contract> contractPage = contractDomainService.findByFilters(filters, pageable);
        
        // 转换为DTO
        return contractPage.map(contractApplicationConvertor::toDTO);
    }
    
    /**
     * 删除合同（逻辑删除）
     *
     * @param contractId 合同ID
     */
    @Transactional
    public Boolean deleteContract(Long contractId) {
        log.info("删除合同: {}", contractId);
        
        ContractId id = ContractId.of(contractId);
        boolean res = contractDomainService.deleteContract(id);
        if (res) {
            log.info("合同删除成功: {}", contractId);
        }
        return res;
    }
    
    /**
     * 批量删除合同
     *
     * @param contractIds 合同ID列表
     */
    @Transactional
    public void deleteContracts(List<Long> contractIds) {
        log.info("批量删除合同: {}", contractIds);
        
        for (Long contractId : contractIds) {
            deleteContract(contractId);
        }
        
        log.info("批量删除合同成功，数量: {}", contractIds.size());
    }
    
    /**
     * 统计合同数量
     *
     * @param queryDTO 查询条件
     * @return 符合条件的合同数量
     */
    @Transactional(readOnly = true)
    public long countContracts(ContractQueryDTO queryDTO) {
        log.debug("统计合同数量，条件: {}", queryDTO);
        
        // 转换查询条件
        ContractFilters filters = convertToFilters(queryDTO);
        
        // 创建一个大页面来获取总数（这里可以优化为专门的count查询）
        Pageable pageable = PageRequest.of(0, 1);
        Page<Contract> page = contractDomainService.findByFilters(filters, pageable);
        
        return page.getTotalElements();
    }
    
    /**
     * 根据查询条件查询合同列表
     *
     * @param queryDTO 查询条件
     * @return 合同列表
     */
    @Transactional(readOnly = true)
    public List<ContractDTO> queryContracts(ContractQueryDTO queryDTO) {
        log.debug("查询合同列表，条件: {}", queryDTO);
        
        ContractFilters filters = convertToFilters(queryDTO);
        List<Contract> contracts = contractDomainService.findByFilters(filters);
        return contractApplicationConvertor.toDTOList(contracts);
    }
    
    /**
     * 根据名称查询合同
     *
     * @param contractName 合同名称
     * @return 合同列表
     */
    @Transactional(readOnly = true)
    public List<ContractDTO> findContractsByName(String contractName) {
        log.debug("根据名称查询合同: {}", contractName);
        
        ContractFilters filters = ContractFilters.builder()
            .contractName(contractName)
            .build();
        
        List<Contract> contracts = contractDomainService.findByFilters(filters);
        return contractApplicationConvertor.toDTOList(contracts);
    }
    
    /**
     * 将查询DTO转换为领域过滤器
     *
     * @param queryDTO 查询DTO
     * @return 领域过滤器
     */
    private ContractFilters convertToFilters(ContractQueryDTO queryDTO) {
        ContractFilters.ContractFiltersBuilder filtersBuilder = ContractFilters.builder();
        
        if (queryDTO.getContractName() != null) {
            filtersBuilder.contractName(queryDTO.getContractName());
        }
        if (queryDTO.getContractTypes() != null && !queryDTO.getContractTypes().isEmpty()) {
            filtersBuilder.contractTypes(queryDTO.getContractTypes());
        }
                if (queryDTO.getPartyAName() != null) {
            filtersBuilder.partyAName(queryDTO.getPartyAName());
        }
        if (queryDTO.getPartyBName() != null) {
            filtersBuilder.partyBName(queryDTO.getPartyBName());
        }
        if (queryDTO.getMinAmount() != null) {
            filtersBuilder.minAmount(queryDTO.getMinAmount());
        }
        if (queryDTO.getMaxAmount() != null) {
            filtersBuilder.maxAmount(queryDTO.getMaxAmount());
        }
        if (queryDTO.getSignDateStart() != null) {
            filtersBuilder.signDateStart(queryDTO.getSignDateStart());
        }
        if (queryDTO.getSignDateEnd() != null) {
            filtersBuilder.signDateEnd(queryDTO.getSignDateEnd());
        }
        if (queryDTO.getEffectiveDateStart() != null) {
            filtersBuilder.effectiveDateStart(queryDTO.getEffectiveDateStart());
        }
        if (queryDTO.getEffectiveDateEnd() != null) {
            filtersBuilder.effectiveDateEnd(queryDTO.getEffectiveDateEnd());
        }
        if (queryDTO.getExpiryDateStart() != null) {
            filtersBuilder.expiryDateStart(queryDTO.getExpiryDateStart());
        }
        if (queryDTO.getExpiryDateEnd() != null) {
            filtersBuilder.expiryDateEnd(queryDTO.getExpiryDateEnd());
        }
                if (queryDTO.getCreatedBy() != null) {
            filtersBuilder.createdBy(queryDTO.getCreatedBy());
        }
        if (queryDTO.getIncludeDeleted() != null) {
            filtersBuilder.includeDeleted(queryDTO.getIncludeDeleted());
        }
        
        return filtersBuilder.build();
    }

    /**
     * 使用fileClient下载文件并保存到本地
     *
     * @param fileUuid 文件UUID
     * @param localFileName 本地文件名
     * @throws IOException 文件下载失败
     */
    private void downloadFileFromClient(String fileUuid, String localFileName) throws IOException {
        log.debug("开始下载文件: uuid={}, localFileName={}", fileUuid, localFileName);

        // 确保临时存储目录存在
        String tmpDir = fileDownloadUtil.getTmpStorageDir();
        java.io.File targetFile = new java.io.File(tmpDir, localFileName);

        // 使用fileClient下载文件
        try (InputStream inputStream = fileClient.downloadByUuid(fileUuid);
             FileOutputStream outputStream = new FileOutputStream(targetFile)) {

            if (inputStream == null) {
                throw new IOException("文件下载失败，返回的InputStream为null: " + fileUuid);
            }

            // 使用缓冲复制文件内容
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            log.info("文件下载成功: uuid={}, localFileName={}, size={} bytes",
                    fileUuid, localFileName, totalBytes);

        } catch (Exception e) {
            // 如果下载失败，删除可能创建的不完整文件
            if (targetFile.exists()) {
                targetFile.delete();
            }
            throw new IOException("下载文件失败: " + fileUuid, e);
        }
    }
}
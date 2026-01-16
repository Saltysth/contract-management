package com.contract.management.interfaces.rest.api.v1.dto.response;

import com.contract.common.constant.ContractType;
import com.contract.management.application.dto.ContractClassificationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 合同分类响应DTO
 * 用于REST接口返回给客户端的数据结构
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "合同分类响应")
public class ContractClassificationResponse {

    /**
     * 合同ID
     */
    @Schema(description = "合同ID", example = "1")
    private Long contractId;

    /**
     * 合同类型枚举值
     */
    @Schema(description = "合同类型", example = "SALES")
    private ContractType contractType;

    /**
     * 合同类型显示名称
     */
    @Schema(description = "合同类型显示名称", example = "销售合同")
    private String contractTypeDisplayName;

    /**
     * 合同类型置信度
     */
    @Schema(description = "分类置信度(0-1)", example = "0.85")
    private Double confidence;

    /**
     * 分类方法
     */
    @Schema(description = "分类方法", example = "TEXT_ANALYSIS")
    private String classificationMethod;

    /**
     * 分析的文本内容
     */
    @Schema(description = "分析的文本内容(前2000字)")
    private String analyzedContent;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型", example = "document")
    private String fileType;

    /**
     * 分类时间
     */
    @Schema(description = "分类时间")
    private LocalDateTime classificationTime;

    /**
     * 是否成功分类
     */
    @Schema(description = "是否成功分类", example = "true")
    private Boolean success;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 文件处理状态
     */
    @Schema(description = "文件处理状态", example = "COMPLETED")
    private FileProcessingStatus fileProcessingStatus;

    /**
     * 扩展属性
     */
    @Schema(description = "扩展属性")
    private Map<String, Object> extendedProperties;

    /**
     * 文件处理状态枚举
     */
    @Schema(description = "文件处理状态")
    public enum FileProcessingStatus {
        PENDING("待处理"),
        EXTRACTING("文本提取中"),
        ANALYZING("AI分析中"),
        COMPLETED("处理完成"),
        FAILED("处理失败");

        private final String displayName;

        FileProcessingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 从应用层DTO转换为响应DTO
     */
    public static ContractClassificationResponse from(ContractClassificationDTO dto) {
        if (dto == null) {
            return null;
        }

        FileProcessingStatus status = dto.getFileProcessingStatus() != null ?
            FileProcessingStatus.valueOf(dto.getFileProcessingStatus().name()) :
            FileProcessingStatus.FAILED;

        return ContractClassificationResponse.builder()
            .contractId(dto.getContractId())
            .contractType(dto.getContractType())
            .contractTypeDisplayName(dto.getContractType() != null ?
                dto.getContractType().getDisplayName() : null)
            .confidence(dto.getConfidence())
            .classificationMethod(dto.getClassificationMethod())
            .fileType(dto.getFileType())
            .classificationTime(dto.getClassificationTime())
            .success(dto.getSuccess())
            .errorMessage(dto.getErrorMessage())
            .fileProcessingStatus(status)
            .build();
    }
}
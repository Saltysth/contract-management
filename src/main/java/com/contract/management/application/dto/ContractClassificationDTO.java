package com.contract.management.application.dto;

import com.contract.common.constant.ContractType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 合同分类DTO
 * 用于合同分类查询接口的返回数据
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractClassificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同类型枚举值（必填）
     */
    private ContractType contractType;

    /**
     * 合同类型置信度（0-1）
     */
    @Builder.Default
    private Double confidence = 0.0;

    /**
     * 分类方法
     */
    @Builder.Default
    private String classificationMethod = "UNKNOWN";

    /**
     * 文件类型
     */
    @Builder.Default
    private String fileType = "UNKNOWN";

    /**
     * 分类时间
     */
    @Builder.Default
    private LocalDateTime classificationTime = LocalDateTime.now();

    /**
     * 是否成功分类
     */
    @Builder.Default
    private Boolean success = false;

    /**
     * 错误信息（如果分类失败）
     */
    private String errorMessage;

    /**
     * 文件处理状态
     */
    @Builder.Default
    private FileProcessingStatus fileProcessingStatus = FileProcessingStatus.PENDING;

    /**
     * 文件处理状态枚举
     */
    public enum FileProcessingStatus {
        /**
         * 待处理
         */
        PENDING("待处理"),

        /**
         * 文本提取中
         */
        EXTRACTING("文本提取中"),

        /**
         * AI分析中
         */
        ANALYZING("AI分析中"),

        /**
         * 处理完成
         */
        COMPLETED("处理完成"),

        /**
         * 处理失败
         */
        FAILED("处理失败");

        private final String displayName;

        FileProcessingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
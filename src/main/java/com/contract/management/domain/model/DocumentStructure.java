package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档结构领域模型
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStructure {

    /**
     * 主键ID（由数据库生成）
     */
    private Long id;

    /**
     * 合同ID
     */
    private DocumentId documentId;

    /**
     * 关联的条款抽取任务ID（冗余字段）
     */
    private Long extractionTaskId;

    /**
     * 审计信息
     */
    private AuditInfo auditInfo;

    /**
     * 创建文档结构
     */
    public DocumentStructure(DocumentId documentId, Long createdBy) {
        this.documentId = documentId;
        this.auditInfo = AuditInfo.create(createdBy);
    }

    /**
     * 设置抽取任务ID
     */
    public void setExtractionTaskId(Long extractionTaskId) {
        this.extractionTaskId = extractionTaskId;
    }

    /**
     * 更新审计信息
     */
    public void updateAudit() {
        if (this.auditInfo != null) {
            this.auditInfo = this.auditInfo.update();
        }
    }
}
package com.contract.management.infrastructure.repository;

import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.ClassificationMethod;
import com.contract.management.domain.model.ClassificationResult;
import com.contract.management.domain.model.ClassificationResultId;
import com.contract.management.domain.model.ContractId;
import com.contract.management.infrastructure.entity.ClassificationResultEntity;
import com.contract.management.infrastructure.mapper.ClassificationResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 合同分类结果仓储测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("合同分类结果仓储测试")
class ClassificationRepositoryImplTest {

    @Mock
    private ClassificationResultMapper classificationResultMapper;

    @InjectMocks
    private ClassificationRepositoryImpl classificationRepository;

    private ContractId contractId;
    private ClassificationResult classificationResult;

    @BeforeEach
    void setUp() {
        contractId = ContractId.of(1L);

        classificationResult = new ClassificationResult(
                contractId,
                ContractType.SALES,
                ClassificationMethod.AI,
                BigDecimal.valueOf(0.95),
                "v1.0",
                null,
                "AI自动分类",
                null,
                1L
        );
    }

    @Test
    @DisplayName("保存分类结果 - 新增")
    void save_newClassification_success() {
        // Given
        when(classificationResultMapper.insert(any(ClassificationResultEntity.class))).thenReturn(1);

        // When
        ClassificationResult savedResult = classificationRepository.save(classificationResult);

        // Then
        assertThat(savedResult).isNotNull();
        verify(classificationResultMapper).insert(any(ClassificationResultEntity.class));
    }

    @Test
    @DisplayName("根据ID查找分类结果")
    void findById_existingClassification_returnsOptional() {
        // Given
        ClassificationResultEntity entity = new ClassificationResultEntity();
        entity.setId(1L);
        entity.setContractId(1L);
        entity.setContractType("SALES");
        entity.setClassificationMethod("AI");
        entity.setConfidenceScore(BigDecimal.valueOf(0.95));
        entity.setIsActive(true);
        entity.setCreatedBy(1L);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedBy(1L);
        entity.setUpdatedTime(LocalDateTime.now());
        entity.setObjectVersionNumber(0L);

        when(classificationResultMapper.selectById(1L)).thenReturn(entity);

        // When
        Optional<ClassificationResult> result = classificationRepository.findById(ClassificationResultId.of(1L));

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getContractId().getValue()).isEqualTo(1L);
        assertThat(result.get().getContractType()).isEqualTo(ContractType.SALES);
        assertThat(result.get().getClassificationMethod()).isEqualTo(ClassificationMethod.AI);
    }

    @Test
    @DisplayName("根据合同ID查找活跃分类结果")
    void findActiveByContractId_existingClassification_returnsOptional() {
        // Given
        ClassificationResultEntity entity = new ClassificationResultEntity();
        entity.setId(1L);
        entity.setContractId(1L);
        entity.setContractType("SALES");
        entity.setClassificationMethod("AI");
        entity.setConfidenceScore(BigDecimal.valueOf(0.95));
        entity.setIsActive(true);
        entity.setCreatedBy(1L);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedBy(1L);
        entity.setUpdatedTime(LocalDateTime.now());
        entity.setObjectVersionNumber(0L);

        when(classificationResultMapper.findActiveByContractId(1L)).thenReturn(entity);

        // When
        Optional<ClassificationResult> result = classificationRepository.findActiveByContractId(contractId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getContractId()).isEqualTo(contractId);
        assertThat(result.get().getContractType()).isEqualTo(ContractType.SALES);
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("停用合同的活跃分类结果")
    void deactivateByContractId_existingActiveClassification_success() {
        // When
        classificationRepository.deactivateByContractId(contractId, 2L);

        // Then
        verify(classificationResultMapper).deactivateByContractId(eq(1L), eq(2L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("检查分类结果是否存在")
    void existsById_existingClassification_returnsTrue() {
        // Given
        ClassificationResultEntity entity = new ClassificationResultEntity();
        entity.setId(1L);

        when(classificationResultMapper.selectById(1L)).thenReturn(entity);

        // When
        boolean exists = classificationRepository.existsById(ClassificationResultId.of(1L));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("检查分类结果是否存在 - 不存在")
    void existsById_nonExistingClassification_returnsFalse() {
        // Given
        when(classificationResultMapper.selectById(1L)).thenReturn(null);

        // When
        boolean exists = classificationRepository.existsById(ClassificationResultId.of(1L));

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("查找合同的所有分类结果")
    void findAllByContractId_existingClassifications_returnsList() {
        // Given
        ClassificationResultEntity entity1 = new ClassificationResultEntity();
        entity1.setId(1L);
        entity1.setContractId(1L);
        entity1.setContractType("SALES");
        entity1.setClassificationMethod("AI");
        entity1.setIsActive(false);
        entity1.setCreatedBy(1L);
        entity1.setCreatedTime(LocalDateTime.now());
        entity1.setUpdatedBy(1L);
        entity1.setUpdatedTime(LocalDateTime.now());
        entity1.setObjectVersionNumber(0L);

        ClassificationResultEntity entity2 = new ClassificationResultEntity();
        entity2.setId(2L);
        entity2.setContractId(1L);
        entity2.setContractType("PURCHASE");
        entity2.setClassificationMethod("MANUAL");
        entity2.setIsActive(true);
        entity2.setCreatedBy(1L);
        entity2.setCreatedTime(LocalDateTime.now());
        entity2.setUpdatedBy(1L);
        entity2.setUpdatedTime(LocalDateTime.now());
        entity2.setObjectVersionNumber(0L);

        List<ClassificationResultEntity> entities = List.of(entity1, entity2);
        when(classificationResultMapper.findAllByContractId(1L)).thenReturn(entities);

        // When
        List<ClassificationResult> results = classificationRepository.findAllByContractId(contractId);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getContractId()).isEqualTo(contractId);
        assertThat(results.get(1).getContractId()).isEqualTo(contractId);
    }
}
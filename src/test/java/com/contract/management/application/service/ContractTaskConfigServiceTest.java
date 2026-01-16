package com.contract.management.application.service;

import com.contract.management.infrastructure.entity.ContractTaskEntity;
import com.contract.management.infrastructure.entity.PromptEntity;
import com.contract.management.infrastructure.entity.ReviewRuleEntity;
import com.contract.management.infrastructure.mapper.ContractTaskMapper;
import com.contract.management.infrastructure.mapper.PromptMapper;
import com.contract.management.infrastructure.mapper.ReviewRuleMapper;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractTaskConfigResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 合同任务配置服务测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("合同任务配置服务测试")
class ContractTaskConfigServiceTest {

    @Mock
    private ContractTaskMapper contractTaskMapper;

    @Mock
    private PromptMapper promptMapper;

    @Mock
    private ReviewRuleMapper reviewRuleMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContractTaskConfigService contractTaskConfigService;

    private ContractTaskEntity mockContractTask;

    @BeforeEach
    void setUp() {
        mockContractTask = new ContractTaskEntity();
        mockContractTask.setId(1L);
        mockContractTask.setTaskId(1L);
        mockContractTask.setContractId(1L);
        mockContractTask.setFileUuid("test-uuid");
        mockContractTask.setBusinessTags("采购,电子设备");
        mockContractTask.setReviewType("COMPREHENSIVE");
        mockContractTask.setCustomSelectedReviewTypes("[\"付款条款\",\"违约责任\"]");
        mockContractTask.setIndustry("制造业");
        mockContractTask.setCurrency("CNY");
        mockContractTask.setContractType("采购合同");
        mockContractTask.setTypeConfidence(new BigDecimal("0.95"));
        mockContractTask.setReviewRules("严格审查");
        mockContractTask.setPromptTemplate("分类合同提示词-文字");
        mockContractTask.setEnableTerminology(true);
        mockContractTask.setCreatedBy(1L);
        mockContractTask.setCreatedTime(LocalDateTime.now());
        mockContractTask.setUpdatedBy(1L);
        mockContractTask.setUpdatedTime(LocalDateTime.now());
        mockContractTask.setObjectVersionNumber(1L);
    }

    @Test
    @DisplayName("测试获取合同任务配置信息 - 成功")
    void testGetContractTaskConfig_Success() throws JsonProcessingException {
        // Given
        Long contractTaskId = 1L;

        // Mock contract task
        when(contractTaskMapper.selectById(contractTaskId)).thenReturn(mockContractTask);

        // Mock prompt template
        PromptEntity mockPrompt = new PromptEntity();
        mockPrompt.setPromptName("分类合同提示词-文字");
        mockPrompt.setPromptType("INNER");
        mockPrompt.setPromptRole("USER");
        mockPrompt.setPromptContent("你是一个合同审查专家...");
        mockPrompt.setResponseFormat("json");
        mockPrompt.setEnabled(true);

        when(promptMapper.selectOne(any())).thenReturn(mockPrompt);

        // Mock review rules
        ReviewRuleEntity mockRule = new ReviewRuleEntity();
        mockRule.setId(1L);
        mockRule.setRuleName("采购合同质量条款审查");
        mockRule.setEnabled(true);

        when(reviewRuleMapper.selectList(any())).thenReturn(Arrays.asList(mockRule));

        // Mock JSON parsing
        when(objectMapper.readValue(anyString(), any(Class.class)))
            .thenReturn(Arrays.asList("付款条款", "违约责任"));

        // When
        ContractTaskConfigResponse response = contractTaskConfigService.getContractTaskConfig(contractTaskId);

        // Then
        assertNotNull(response);
        assertEquals(contractTaskId, response.getContractTaskId());
        assertEquals("采购,电子设备", response.getBusinessTags());
        assertEquals("COMPREHENSIVE", response.getReviewType());
        assertEquals("制造业", response.getIndustry());
        assertEquals("CNY", response.getCurrency());
        assertEquals("采购合同", response.getContractType());
        assertEquals("0.95", response.getTypeConfidence());
        assertEquals("严格审查", response.getReviewRules());
        assertTrue(response.getEnableTerminology());

        // Verify prompt template info
        assertNotNull(response.getPromptTemplate());
        assertEquals("分类合同提示词-文字", response.getPromptTemplate().getPromptName());
        assertEquals(3, response.getPromptTemplate().getTimeConsuming());

        // Verify terminology info
        assertNotNull(response.getTerminology());
        assertFalse(response.getTerminology().getHasConfig());
        assertEquals(2, response.getTerminology().getTimeConsuming());

        // Verify rule info
        assertNotNull(response.getRule());
        assertEquals(1, response.getRule().getRuleCount());
        assertEquals(1, response.getRule().getTimeConsuming());

        // Verify total time
        assertEquals(6, response.getTotalTimeConsuming()); // 3 + 2 + 1 = 6

        // Verify interactions
        verify(contractTaskMapper, times(1)).selectById(contractTaskId);
        verify(promptMapper, times(1)).selectOne(any());
        verify(reviewRuleMapper, times(1)).selectList(any());
    }

    @Test
    @DisplayName("测试获取合同任务配置信息 - 任务不存在")
    void testGetContractTaskConfig_TaskNotFound() {
        // Given
        Long contractTaskId = 999L;
        when(contractTaskMapper.selectById(contractTaskId)).thenReturn(null);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            contractTaskConfigService.getContractTaskConfig(contractTaskId);
        });

        assertTrue(exception.getMessage().contains("合同任务不存在"));
        verify(contractTaskMapper, times(1)).selectById(contractTaskId);
    }

    @Test
    @DisplayName("测试获取合同任务配置信息 - 无提示词模板")
    void testGetContractTaskConfig_NoPromptTemplate() throws JsonProcessingException {
        // Given
        mockContractTask.setPromptTemplate(null);
        when(contractTaskMapper.selectById(1L)).thenReturn(mockContractTask);
        when(promptMapper.selectOne(any())).thenReturn(null);
        when(reviewRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(objectMapper.readValue(anyString(), any(Class.class)))
            .thenReturn(Collections.emptyList());

        // When
        ContractTaskConfigResponse response = contractTaskConfigService.getContractTaskConfig(1L);

        // Then
        assertNotNull(response);
        assertNull(response.getPromptTemplate());
        assertEquals(2, response.getTotalTimeConsuming()); // Only terminology time
    }

    @Test
    @DisplayName("测试获取合同任务配置信息 - 术语库未启用")
    void testGetContractTaskConfig_TerminologyDisabled() throws JsonProcessingException {
        // Given
        mockContractTask.setEnableTerminology(false);
        when(contractTaskMapper.selectById(1L)).thenReturn(mockContractTask);
        when(promptMapper.selectOne(any())).thenReturn(null);
        when(reviewRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(objectMapper.readValue(anyString(), any(Class.class)))
            .thenReturn(Collections.emptyList());

        // When
        ContractTaskConfigResponse response = contractTaskConfigService.getContractTaskConfig(1L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getTerminology());
        assertFalse(response.getTerminology().getHasConfig());
        assertEquals("术语库未启用", response.getTerminology().getConfigContent());
        assertEquals(0, response.getTerminology().getTimeConsuming());
        assertEquals(0, response.getTotalTimeConsuming()); // No time consumption
    }
}
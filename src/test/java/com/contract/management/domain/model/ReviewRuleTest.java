package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 审查规则领域模型测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
class ReviewRuleTest {

    @Test
    @DisplayName("应该成功创建专属规则")
    void shouldCreateSpecificRule() {
        // Given
        RuleName ruleName = RuleName.of("采购合同质量检查");
        RuleType ruleType = RuleType.SPECIFIC;
        ApplicableContractTypes contractTypes = ApplicableContractTypes.of("采购合同");
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("质量条款", "验收条款");
        RuleContent ruleContent = RuleContent.of("检查合同中质量条款是否完整");
        PromptMode promptMode = PromptMode.STANDARD;
        String remark = "重要规则";

        // When
        ReviewRule rule = new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, remark);

        // Then
        assertNotNull(rule);
        assertEquals("采购合同质量检查", rule.getRuleName().getValue());
        assertEquals(RuleType.SPECIFIC, rule.getRuleType());
        assertTrue(rule.getApplicableContractTypes().appliesTo("采购合同"));
        assertFalse(rule.getApplicableContractTypes().appliesTo("销售合同"));
        assertTrue(rule.getApplicableClauseTypes().appliesTo("质量条款"));
        assertTrue(rule.getEnabled());
        assertEquals(PromptMode.STANDARD, rule.getPromptMode());
        assertTrue(rule.isApplicableTo("采购合同", "质量条款"));
        assertFalse(rule.isApplicableTo("销售合同", "质量条款"));
        assertTrue(rule.isEffectiveInMode(PromptMode.STANDARD));
        assertTrue(rule.isEffectiveInMode(PromptMode.STRICT));
        assertFalse(rule.isEffectiveInMode(PromptMode.EFFICIENCY));
    }

    @Test
    @DisplayName("应该成功创建兜底规则")
    void shouldCreateFallbackRule() {
        // Given
        RuleName ruleName = RuleName.of("通用合同检查");
        RuleType ruleType = RuleType.FALLBACK;
        ApplicableContractTypes contractTypes = ApplicableContractTypes.fallback();
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("基础条款");
        RuleContent ruleContent = RuleContent.of("基础合同检查规则");
        PromptMode promptMode = PromptMode.EFFICIENCY;

        // When
        ReviewRule rule = new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, null);

        // Then
        assertNotNull(rule);
        assertEquals(RuleType.FALLBACK, rule.getRuleType());
        assertTrue(rule.getApplicableContractTypes().isFallback());
        assertTrue(rule.getApplicableContractTypes().appliesTo("任何合同类型"));
        assertTrue(rule.isFallbackRule());
        assertFalse(rule.isSpecificRule());
        assertFalse(rule.isExtendedRule());
    }

    @Test
    @DisplayName("应该成功创建扩展规则")
    void shouldCreateExtendedRule() {
        // Given
        RuleName ruleName = RuleName.of("严格模式合同审查");
        RuleType ruleType = RuleType.EXTENDED;
        ApplicableContractTypes contractTypes = ApplicableContractTypes.of("重要合同");
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("风险条款");
        RuleContent ruleContent = RuleContent.of("严格模式下的额外检查规则");
        PromptMode promptMode = PromptMode.STRICT;

        // When
        ReviewRule rule = new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, null);

        // Then
        assertNotNull(rule);
        assertEquals(RuleType.EXTENDED, rule.getRuleType());
        assertTrue(rule.isExtendedRule());
        assertTrue(rule.isEffectiveInMode(PromptMode.STRICT));
        assertFalse(rule.isEffectiveInMode(PromptMode.STANDARD));
        assertFalse(rule.isEffectiveInMode(PromptMode.EFFICIENCY));
    }

    @Test
    @DisplayName("创建扩展规则时如果模式不是严格模式应该抛出异常")
    void shouldThrowExceptionWhenCreatingExtendedRuleWithNonStrictMode() {
        // Given
        RuleName ruleName = RuleName.of("无效扩展规则");
        RuleType ruleType = RuleType.EXTENDED;
        ApplicableContractTypes contractTypes = ApplicableContractTypes.of("合同类型");
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("条款类型");
        RuleContent ruleContent = RuleContent.of("规则内容");
        PromptMode promptMode = PromptMode.STANDARD; // 非严格模式

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, null);
        });
    }

    @Test
    @DisplayName("创建兜底规则时如果规则类型不是FALLBACK应该抛出异常")
    void shouldThrowExceptionWhenCreatingFallbackRuleWithNonFallbackType() {
        // Given
        RuleName ruleName = RuleName.of("无效兜底规则");
        RuleType ruleType = RuleType.SPECIFIC; // 非FALLBACK类型
        ApplicableContractTypes contractTypes = ApplicableContractTypes.fallback(); // 兜底合同类型
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("条款类型");
        RuleContent ruleContent = RuleContent.of("规则内容");
        PromptMode promptMode = PromptMode.STANDARD;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, null);
        });
    }

    @Test
    @DisplayName("应该能够启用和禁用规则")
    void shouldEnableAndDisableRule() {
        // Given
        ReviewRule rule = createTestRule();
        LocalDateTime beforeUpdateTime = rule.getUpdateTime();

        // When
        rule.disable();

        // Then
        assertFalse(rule.getEnabled());
        assertTrue(rule.getUpdateTime().isAfter(beforeUpdateTime));

        // When
        beforeUpdateTime = rule.getUpdateTime();
        rule.enable();

        // Then
        assertTrue(rule.getEnabled());
        assertTrue(rule.getUpdateTime().isAfter(beforeUpdateTime));
    }

    @Test
    @DisplayName("应该能够更新规则信息")
    void shouldUpdateRule() {
        // Given
        ReviewRule rule = createTestRule();
        RuleType newRuleType = RuleType.SPECIFIC;
        RuleName newRuleName = RuleName.of("更新后的规则名称");
        ApplicableContractTypes newContractTypes = ApplicableContractTypes.of("新合同类型");
        ApplicableClauseTypes newClauseTypes = ApplicableClauseTypes.of("新条款类型");
        RuleContent newRuleContent = RuleContent.of("更新后的规则内容");
        PromptMode newPromptMode = PromptMode.STRICT;
        String newRemark = "更新后的备注";

        // When
        rule.updateRule(newRuleName, newRuleType, newContractTypes, newClauseTypes, newRuleContent, newPromptMode, newRemark);

        // Then
        assertEquals("更新后的规则名称", rule.getRuleName().getValue());
        assertEquals(newContractTypes, rule.getApplicableContractTypes());
        assertEquals(newClauseTypes, rule.getApplicableClauseTypes());
        assertEquals("更新后的规则内容", rule.getRuleContent().getValue());
        assertEquals(PromptMode.STRICT, rule.getPromptMode());
        assertEquals("更新后的备注", rule.getRemark());
    }

    @Test
    @DisplayName("应该能够标记规则为已修改")
    void shouldMarkRuleAsModified() {
        // Given
        ReviewRule rule = createTestRule();
        LocalDateTime beforeUpdateTime = rule.getUpdateTime();

        // When
        rule.markAsModified();

        // Then
        assertTrue(rule.getUpdateTime().isAfter(beforeUpdateTime));
    }

    private ReviewRule createTestRule() {
        RuleName ruleName = RuleName.of("测试规则");
        RuleType ruleType = RuleType.SPECIFIC;
        ApplicableContractTypes contractTypes = ApplicableContractTypes.of("测试合同");
        ApplicableClauseTypes clauseTypes = ApplicableClauseTypes.of("测试条款");
        RuleContent ruleContent = RuleContent.of("测试规则内容");
        PromptMode promptMode = PromptMode.STANDARD;

        return new ReviewRule(ruleName, ruleType, contractTypes, clauseTypes, ruleContent, promptMode, "测试备注");
    }
}
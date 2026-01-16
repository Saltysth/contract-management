package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 风险因子值对象
 * 用于描述条款相关的风险因素信息
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskFactors {

    /**
     * 风险类型列表
     */
    private final List<String> riskTypes;

    /**
     * 风险描述
     */
    private final String description;

    /**
     * 风险等级（1-5，5为最高风险）
     */
    private final Integer severity;

    /**
     * 建议措施
     */
    private final List<String> recommendations;

    /**
     * 风险标识时间
     */
    private final LocalDateTime identifiedAt;

    @JsonCreator
    public RiskFactors(@JsonProperty("riskTypes") List<String> riskTypes,
                      @JsonProperty("description") String description,
                      @JsonProperty("severity") Integer severity,
                      @JsonProperty("recommendations") List<String> recommendations,
                      @JsonProperty("identifiedAt") LocalDateTime identifiedAt) {
        if (riskTypes == null || riskTypes.isEmpty()) {
            throw new IllegalArgumentException("风险类型不能为空");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("风险描述不能为空");
        }
        if (severity == null || severity < 1 || severity > 5) {
            throw new IllegalArgumentException("风险等级必须在1-5之间");
        }
        if (identifiedAt == null) {
            throw new IllegalArgumentException("风险识别时间不能为空");
        }

        this.riskTypes = new ArrayList<>(riskTypes);
        this.description = description.trim();
        this.severity = severity;
        this.recommendations = recommendations != null ? new ArrayList<>(recommendations) : new ArrayList<>();
        this.identifiedAt = identifiedAt;
    }

    /**
     * 创建风险因子
     *
     * @param riskTypes       风险类型列表
     * @param description     风险描述
     * @param severity        风险等级
     * @param recommendations 建议措施列表
     * @param identifiedAt    风险识别时间
     * @return RiskFactors实例
     */
    public static RiskFactors of(List<String> riskTypes, String description, Integer severity,
                                List<String> recommendations, LocalDateTime identifiedAt) {
        return new RiskFactors(riskTypes, description, severity, recommendations, identifiedAt);
    }

    /**
     * 创建简单风险因子（无建议措施）
     */
    public static RiskFactors simple(List<String> riskTypes, String description, Integer severity) {
        return new RiskFactors(riskTypes, description, severity, null, LocalDateTime.now());
    }

    /**
     * 添加建议措施
     *
     * @param recommendation 建议措施
     * @return 新的RiskFactors实例
     */
    public RiskFactors addRecommendation(String recommendation) {
        if (recommendation != null && !recommendation.trim().isEmpty()) {
            List<String> newRecommendations = new ArrayList<>(this.recommendations);
            newRecommendations.add(recommendation.trim());
            return new RiskFactors(this.riskTypes, this.description, this.severity,
                                newRecommendations, this.identifiedAt);
        }
        return this;
    }

    /**
     * 是否为高风险
     *
     * @return true if severity >= 4
     */
    public boolean isHighRisk() {
        return this.severity >= 4;
    }

    /**
     * 是否为极高风险
     *
     * @return true if severity == 5
     */
    public boolean isCriticalRisk() {
        return this.severity == 5;
    }

    @Override
    public String toString() {
        return String.format("RiskFactors{severity=%d, types=%s, description='%s'}",
            severity, riskTypes, description);
    }
}
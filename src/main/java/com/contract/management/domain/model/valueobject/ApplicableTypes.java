package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 适用类型值对象（包含合同类型和条款类型）
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ApplicableTypes {
    private final List<String> contractTypes;
    private final List<String> clauseTypes;

    public ApplicableTypes(List<String> contractTypes, List<String> clauseTypes) {
        if (contractTypes == null || contractTypes.isEmpty()) {
            throw new IllegalArgumentException("Contract types cannot be null or empty");
        }
        if (clauseTypes == null || clauseTypes.isEmpty()) {
            throw new IllegalArgumentException("Clause types cannot be null or empty");
        }
        this.contractTypes = List.copyOf(contractTypes);
        this.clauseTypes = List.copyOf(clauseTypes);
    }

    /**
     * 创建适用类型
     */
    public static ApplicableTypes of(List<String> contractTypes, List<String> clauseTypes) {
        return new ApplicableTypes(contractTypes, clauseTypes);
    }

    /**
     * 创建适用类型（数组参数）
     */
    public static ApplicableTypes of(String[] contractTypes, String[] clauseTypes) {
        List<String> contractTypeList = contractTypes != null ? Arrays.asList(contractTypes) : List.of();
        List<String> clauseTypeList = clauseTypes != null ? Arrays.asList(clauseTypes) : List.of();
        return new ApplicableTypes(contractTypeList, clauseTypeList);
    }

    /**
     * 是否为兜底规则（适用于所有合同）
     */
    public boolean isFallbackRule() {
        return contractTypes.size() == 1 && "all".equalsIgnoreCase(contractTypes.get(0));
    }

    /**
     * 检查是否适用于指定合同类型
     */
    public boolean appliesToContract(String contractType) {
        if (contractType == null) {
            return false;
        }
        return isFallbackRule() || contractTypes.contains(contractType);
    }

    /**
     * 检查是否适用于指定条款类型
     */
    public boolean appliesToClause(String clauseType) {
        if (clauseType == null) {
            return false;
        }
        return clauseTypes.contains(clauseType);
    }

    @Override
    public String toString() {
        return String.format("ApplicableTypes{contracts=%s, clauses=%s}", contractTypes, clauseTypes);
    }
}
package com.contract.management.domain.model.valueobject;

import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public enum OperationType {

    /**
     * 合同相关操作
     */
    CONTRACT_CREATE("合同创建"),
    CONTRACT_UPDATE("合同更新"),
    CONTRACT_DELETE("合同删除"),
    CONTRACT_QUERY("合同查询"),

    /**
     * 条款抽取相关操作
     */
    CLAUSE_EXTRACTION_START("条款抽取开始"),
    CLAUSE_EXTRACTION_PROCESS("条款抽取处理中"),
    CLAUSE_EXTRACTION_SUCCESS("条款抽取成功"),
    CLAUSE_EXTRACTION_FAILED("条款抽取失败"),
    CLAUSE_EXTRACTION_CANCEL("条款抽取取消"),

    /**
     * 文件相关操作
     */
    FILE_UPLOAD("文件上传"),
    FILE_DOWNLOAD("文件下载"),
    FILE_DELETE("文件删除"),

    /**
     * 系统相关操作
     */
    USER_LOGIN("用户登录"),
    USER_LOGOUT("用户登出"),
    SYSTEM_ERROR("系统错误");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    /**
     * 根据描述获取操作类型
     */
    public static OperationType fromDescription(String description) {
        for (OperationType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        return null;
    }
}
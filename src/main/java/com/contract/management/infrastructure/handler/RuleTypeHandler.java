package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.RuleType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;

/**
 * RuleType枚举的PostgreSQL枚举类型处理器
 * 适配PostgreSQL枚举类型与Java RuleType枚举的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(RuleType.class)
// 注意：PostgreSQL枚举的JDBC类型实际是OTHER（而非VARCHAR），需修正映射
@MappedJdbcTypes(JdbcType.OTHER)
public class RuleTypeHandler extends BaseTypeHandler<RuleType> {

    // 数据库枚举类型名（必须与PostgreSQL中定义的rule_type_enum完全一致）
    private static final String PG_ENUM_TYPE_NAME = "rule_type_enum";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RuleType parameter, JdbcType jdbcType) throws SQLException {
        // 关键：PostgreSQL枚举需要通过PGobject显式指定枚举类型，而非直接传字符串
        PGobject pgEnum = new PGobject();
        pgEnum.setType(PG_ENUM_TYPE_NAME); // 绑定数据库枚举类型名
        pgEnum.setValue(parameter.getCode()); // 传入枚举的code值（需与数据库枚举值一致）
        ps.setObject(i, pgEnum); // 用setObject而非setString，触发PostgreSQL枚举类型识别
    }

    @Override
    public RuleType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToRuleType(value);
    }

    @Override
    public RuleType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToRuleType(value);
    }

    @Override
    public RuleType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToRuleType(value);
    }

    /**
     * 通用转换方法：数据库枚举字符串 → Java RuleType枚举
     */
    private RuleType convertToRuleType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return RuleType.fromCode(value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("数据库枚举值[" + value + "]未匹配到RuleType枚举", e);
        }
    }
}
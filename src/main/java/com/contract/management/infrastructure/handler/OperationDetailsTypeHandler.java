package com.contract.management.infrastructure.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.contract.management.domain.model.valueobject.OperationDetails;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB类型处理器
 * 用于处理OperationDetails对象与PostgreSQL JSONB类型之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(OperationDetails.class)
public class OperationDetailsTypeHandler extends BaseTypeHandler<OperationDetails> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, OperationDetails parameter, JdbcType jdbcType) throws SQLException {
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(parameter);
            // 使用 PGobject 确保正确的 JSONB 类型处理
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(jsonString);
            ps.setObject(i, pgObject);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize OperationDetails to JSON", e);
        }
    }

    @Override
    public OperationDetails getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public OperationDetails getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public OperationDetails getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    /**
     * 解析JSON字符串为OperationDetails对象
     */
    OperationDetails parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(json, OperationDetails.class);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize JSON to OperationDetails: " + json, e);
        }
    }
}
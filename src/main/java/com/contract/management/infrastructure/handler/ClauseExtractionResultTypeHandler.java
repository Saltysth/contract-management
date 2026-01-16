package com.contract.management.infrastructure.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB类型处理器（适配ClauseExtractionResult）
 */
@MappedTypes(ClauseExtractionResult.class) // 指定处理的Java类型
public class ClauseExtractionResultTypeHandler extends BaseTypeHandler<ClauseExtractionResult> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JSONB_TYPE = "jsonb"; // PostgreSQL jsonb类型标识

    static {
        // 配置ObjectMapper支持Java 8时间类型（如LocalDateTime），避免序列化失败
        OBJECT_MAPPER.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        OBJECT_MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ClauseExtractionResult parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(JSONB_TYPE); // 显式指定为jsonb类型
        try {
            // 将对象序列化为JSON字符串
            String jsonString = OBJECT_MAPPER.writeValueAsString(parameter);
            pgObject.setValue(jsonString);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize ClauseExtractionResult to JSONB", e);
        }
        // 传递PGobject对象（PostgreSQL会识别为jsonb类型）
        ps.setObject(i, pgObject);
    }

    @Override
    public ClauseExtractionResult getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public ClauseExtractionResult getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public ClauseExtractionResult getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private ClauseExtractionResult parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, ClauseExtractionResult.class);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize JSON to ClauseExtractionResult: " + json, e);
        }
    }
}
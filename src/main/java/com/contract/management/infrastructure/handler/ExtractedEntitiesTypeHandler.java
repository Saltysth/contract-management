package com.contract.management.infrastructure.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * PostgreSQL JSONB类型处理器（适配Map<String, String>）
 * 用于处理提取的实体数据（实体名称 -> 实体值）
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(Map.class)
public class ExtractedEntitiesTypeHandler extends BaseTypeHandler<Map<String, String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JSONB_TYPE = "jsonb";
    private static final TypeReference<Map<String, String>> TYPE_REFERENCE = new TypeReference<Map<String, String>>() {};

    static {
        // 配置ObjectMapper支持Java 8时间类型
        OBJECT_MAPPER.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        OBJECT_MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(JSONB_TYPE);
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(parameter);
            pgObject.setValue(jsonString);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize ExtractedEntities to JSONB", e);
        }
        ps.setObject(i, pgObject);
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private Map<String, String> parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, TYPE_REFERENCE);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize JSON to ExtractedEntities: " + json, e);
        }
    }
}
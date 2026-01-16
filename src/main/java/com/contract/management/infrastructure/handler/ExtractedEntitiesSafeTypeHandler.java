package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ExtractedEntities;
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
 * PostgreSQL JSONB类型处理器（适配ExtractedEntities）
 * 用于安全地处理提取的实体数据，避免与Map类型冲突
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ExtractedEntities.class)
public class ExtractedEntitiesSafeTypeHandler extends BaseTypeHandler<ExtractedEntities> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JSONB_TYPE = "jsonb";
    private static final TypeReference<Map<String, String>> TYPE_REFERENCE = new TypeReference<Map<String, String>>() {};

    static {
        // 配置ObjectMapper支持Java 8时间类型
        OBJECT_MAPPER.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        OBJECT_MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExtractedEntities parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(JSONB_TYPE);
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(parameter.toMap());
            pgObject.setValue(jsonString);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize ExtractedEntities to JSONB", e);
        }
        ps.setObject(i, pgObject);
    }

    @Override
    public ExtractedEntities getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public ExtractedEntities getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public ExtractedEntities getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    /**
     * 解析JSON为ExtractedEntities
     */
    private ExtractedEntities parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            Map<String, String> map = OBJECT_MAPPER.readValue(json, TYPE_REFERENCE);
            return ExtractedEntities.of(map);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize JSON to ExtractedEntities: " + json, e);
        }
    }
}
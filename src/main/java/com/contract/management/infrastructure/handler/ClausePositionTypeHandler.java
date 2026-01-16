package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ClausePosition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB类型处理器（适配ClausePosition）
 * 用于处理ClausePosition对象与PostgreSQL JSONB类型之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ClausePosition.class)
public class ClausePositionTypeHandler extends BaseTypeHandler<ClausePosition> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JSONB_TYPE = "jsonb";

    static {
        // 配置ObjectMapper支持Java 8时间类型
        OBJECT_MAPPER.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        OBJECT_MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ClausePosition parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType(JSONB_TYPE);
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(parameter);
            pgObject.setValue(jsonString);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize ClausePosition to JSONB", e);
        }
        ps.setObject(i, pgObject);
    }

    @Override
    public ClausePosition getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public ClausePosition getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public ClausePosition getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private ClausePosition parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, ClausePosition.class);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize JSON to ClausePosition: " + json, e);
        }
    }
}
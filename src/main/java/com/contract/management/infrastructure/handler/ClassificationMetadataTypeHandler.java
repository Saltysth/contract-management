package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ClassificationMetadata;
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
 * 分类元数据JSONB类型处理器
 * 用于处理ClassificationMetadata与PostgreSQL JSONB类型之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ClassificationMetadata.class)
public class ClassificationMetadataTypeHandler extends BaseTypeHandler<ClassificationMetadata> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ClassificationMetadata parameter, JdbcType jdbcType) throws SQLException {
        try {
            PGobject pgObject = parameter.toPGobject();
            ps.setObject(i, pgObject);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to serialize ClassificationMetadata to JSON", e);
        } catch (RuntimeException e) {
            // 捕获由toPGobject抛出的包装异常
            if (e.getCause() instanceof java.sql.SQLException) {
                throw (java.sql.SQLException) e.getCause();
            }
            throw new SQLException("Failed to create PGobject", e);
        }
    }

    @Override
    public ClassificationMetadata getNullableResult(ResultSet rs, String columnName) throws SQLException {
        PGobject pgObject = (PGobject) rs.getObject(columnName);
        if (pgObject == null) {
            return null;
        }
        return ClassificationMetadata.fromPGobject(pgObject);
    }

    @Override
    public ClassificationMetadata getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        PGobject pgObject = (PGobject) rs.getObject(columnIndex);
        if (pgObject == null) {
            return null;
        }
        return ClassificationMetadata.fromPGobject(pgObject);
    }

    @Override
    public ClassificationMetadata getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        PGobject pgObject = (PGobject) cs.getObject(columnIndex);
        if (pgObject == null) {
            return null;
        }
        return ClassificationMetadata.fromPGobject(pgObject);
    }
}
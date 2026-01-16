package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ApplicableClauseTypes;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostgreSQL 数组类型处理器（适配ApplicableClauseTypes）
 * 用于安全地处理适用条款类型数组字段
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ApplicableClauseTypes.class)
public class ApplicableClauseTypesHandler extends BaseTypeHandler<ApplicableClauseTypes> {

    private static final String ARRAY_TYPE = "text";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ApplicableClauseTypes parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.ARRAY);
            return;
        }

        Connection connection = ps.getConnection();
        Array array = connection.createArrayOf(ARRAY_TYPE, parameter.getValues().toArray());
        ps.setArray(i, array);
    }

    @Override
    public ApplicableClauseTypes getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        return parseArray(array);
    }

    @Override
    public ApplicableClauseTypes getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        return parseArray(array);
    }

    @Override
    public ApplicableClauseTypes getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        return parseArray(array);
    }

    /**
     * 解析数组为ApplicableClauseTypes
     */
    private ApplicableClauseTypes parseArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }

        Object[] arrayData = (Object[]) array.getArray();
        if (arrayData == null) {
            return null;
        }

        List<String> stringList = Arrays.stream(arrayData)
                .map(String::valueOf)
                .collect(Collectors.toList());

        return ApplicableClauseTypes.of(stringList);
    }
}
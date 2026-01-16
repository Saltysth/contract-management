package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ApplicableContractTypes;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostgreSQL 数组类型处理器（适配ApplicableContractTypes）
 * 用于安全地处理适用合同类型数组字段
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ApplicableContractTypes.class)
public class ApplicableContractTypesHandler extends BaseTypeHandler<ApplicableContractTypes> {

    private static final String ARRAY_TYPE = "text";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ApplicableContractTypes parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.ARRAY);
            return;
        }

        Connection connection = ps.getConnection();
        Array array = connection.createArrayOf(ARRAY_TYPE, parameter.getValues().toArray());
        ps.setArray(i, array);
    }

    @Override
    public ApplicableContractTypes getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        return parseArray(array);
    }

    @Override
    public ApplicableContractTypes getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        return parseArray(array);
    }

    @Override
    public ApplicableContractTypes getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        return parseArray(array);
    }

    /**
     * 解析数组为ApplicableContractTypes
     */
    private ApplicableContractTypes parseArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }

        Object[] arrayData = (Object[]) array.getArray();
        if (arrayData == null || arrayData.length == 0) {
            return ApplicableContractTypes.empty();
        }

        List<String> stringList = Arrays.stream(arrayData)
                .map(String::valueOf)
                .filter(str -> str != null && !str.trim().isEmpty()) // 过滤掉空字符串
                .collect(Collectors.toList());

        // 如果过滤后为空，返回一个空的ApplicableContractTypes
        if (stringList.isEmpty()) {
            return ApplicableContractTypes.empty();
        }

        return ApplicableContractTypes.of(stringList);
    }
}
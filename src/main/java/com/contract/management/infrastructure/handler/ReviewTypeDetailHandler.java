package com.contract.management.infrastructure.handler;

import com.contract.common.enums.ReviewTypeDetail;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReviewTypeDetail枚举的VARCHAR类型处理器
 * 用于安全地处理审查规则分类字段
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@MappedTypes(ReviewTypeDetail.class)
public class ReviewTypeDetailHandler extends BaseTypeHandler<ReviewTypeDetail> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReviewTypeDetail parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public ReviewTypeDetail getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToReviewTypeDetail(value);
    }

    @Override
    public ReviewTypeDetail getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToReviewTypeDetail(value);
    }

    @Override
    public ReviewTypeDetail getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToReviewTypeDetail(value);
    }

    /**
     * 通用转换方法：数据库字符串 → ReviewTypeDetail枚举
     */
    private ReviewTypeDetail convertToReviewTypeDetail(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ReviewTypeDetail.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("数据库值[" + value + "]未匹配到ReviewTypeDetail枚举", e);
        }
    }
}
package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ApplicableContractTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApplicableContractTypesHandler测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ApplicableContractTypesHandlerTest {

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CallableStatement callableStatement;

    @Mock
    private Connection connection;

    @Mock
    private Array array;

    private ApplicableContractTypesHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApplicableContractTypesHandler();
    }

    @Test
    @DisplayName("应该能够设置非空参数")
    void shouldSetNonNullParameter() throws SQLException {
        // Given
        ApplicableContractTypes contractTypes = ApplicableContractTypes.of("采购合同", "销售合同");
        String[] expectedArray = {"采购合同", "销售合同"};

        when(preparedStatement.getConnection()).thenReturn(connection);
        when(connection.createArrayOf("text", expectedArray)).thenReturn(array);

        // When
        handler.setNonNullParameter(preparedStatement, 1, contractTypes, null);

        // Then
        verify(connection).createArrayOf("text", expectedArray);
        verify(preparedStatement).setArray(1, array);
    }

    @Test
    @DisplayName("应该能够设置null参数")
    void shouldSetNullParameter() throws SQLException {
        // When
        handler.setNonNullParameter(preparedStatement, 1, null, null);

        // Then
        verify(preparedStatement).setNull(1, Types.ARRAY);
    }

    @Test
    @DisplayName("应该能够从ResultSet获取结果")
    void shouldGetNullableResultFromResultSet() throws SQLException {
        // Given
        String[] testArray = {"采购合同", "销售合同"};
        when(resultSet.getArray("column_name")).thenReturn(array);
        when(array.getArray()).thenReturn(testArray);

        // When
        ApplicableContractTypes result = handler.getNullableResult(resultSet, "column_name");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.appliesTo("采购合同"));
        assertTrue(result.appliesTo("销售合同"));
        assertFalse(result.appliesTo("服务合同"));
    }

    @Test
    @DisplayName("应该能够处理null的ResultSet结果")
    void shouldHandleNullResultSetResult() throws SQLException {
        // Given
        when(resultSet.getArray("column_name")).thenReturn(null);

        // When
        ApplicableContractTypes result = handler.getNullableResult(resultSet, "column_name");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("应该能够从CallableStatement获取结果")
    void shouldGetNullableResultFromCallableStatement() throws SQLException {
        // Given
        String[] testArray = {"服务合同"};
        when(callableStatement.getArray(1)).thenReturn(array);
        when(array.getArray()).thenReturn(testArray);

        // When
        ApplicableContractTypes result = handler.getNullableResult(callableStatement, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.appliesTo("服务合同"));
    }

    @Test
    @DisplayName("应该能够处理数组索引的结果")
    void shouldGetNullableResultByColumnIndex() throws SQLException {
        // Given
        String[] testArray = {"租赁合同", " employment合同"};
        when(resultSet.getArray(2)).thenReturn(array);
        when(array.getArray()).thenReturn(testArray);

        // When
        ApplicableContractTypes result = handler.getNullableResult(resultSet, 2);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("应该能够处理空数组")
    void shouldHandleEmptyArray() throws SQLException {
        // Given
        String[] emptyArray = {};
        when(resultSet.getArray("column_name")).thenReturn(array);
        when(array.getArray()).thenReturn(emptyArray);

        // When
        ApplicableContractTypes result = handler.getNullableResult(resultSet, "column_name");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // empty()方法返回包含一个"empty"字符串的列表
        assertEquals("empty", result.getFirst());
    }
}
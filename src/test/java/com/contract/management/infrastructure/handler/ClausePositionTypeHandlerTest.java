package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ClausePosition;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ClausePositionTypeHandler测试类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
class ClausePositionTypeHandlerTest {

    private final ClausePositionTypeHandler handler = new ClausePositionTypeHandler();

    @Test
    void testSetNonNullParameter() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ClausePosition position = ClausePosition.of(1, 10.0, 20.0, 100.0, 150.0);

        handler.setNonNullParameter(ps, 1, position, null);

        verify(ps).setObject(anyInt(), any());
    }

    @Test
    void testGetNullableResultFromResultSetByName() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        String json = "{\"page\":1,\"startX\":10.0,\"startY\":20.0,\"endX\":100.0,\"endY\":150.0}";
        when(rs.getString(anyString())).thenReturn(json);

        ClausePosition result = handler.getNullableResult(rs, "clause_position");

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(10.0, result.getStartX());
        assertEquals(20.0, result.getStartY());
        assertEquals(100.0, result.getEndX());
        assertEquals(150.0, result.getEndY());
    }

    @Test
    void testGetNullableResultFromResultSetByIndex() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        String json = "{\"page\":2,\"startX\":5.5,\"startY\":15.5,\"endX\":95.5,\"endY\":145.5}";
        when(rs.getString(anyInt())).thenReturn(json);

        ClausePosition result = handler.getNullableResult(rs, 1);

        assertNotNull(result);
        assertEquals(2, result.getPage());
        assertEquals(5.5, result.getStartX());
        assertEquals(15.5, result.getStartY());
        assertEquals(95.5, result.getEndX());
        assertEquals(145.5, result.getEndY());
    }

    @Test
    void testGetNullableResultFromCallableStatement() throws SQLException {
        CallableStatement cs = mock(CallableStatement.class);
        String json = "{\"page\":3,\"startX\":0.0,\"startY\":0.0,\"endX\":50.0,\"endY\":75.0}";
        when(cs.getString(anyInt())).thenReturn(json);

        ClausePosition result = handler.getNullableResult(cs, 1);

        assertNotNull(result);
        assertEquals(3, result.getPage());
        assertEquals(0.0, result.getStartX());
        assertEquals(0.0, result.getStartY());
        assertEquals(50.0, result.getEndX());
        assertEquals(75.0, result.getEndY());
    }

    @Test
    void testGetNullableResultWithNull() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(anyString())).thenReturn(null);

        ClausePosition result = handler.getNullableResult(rs, "clause_position");

        assertNull(result);
    }

    @Test
    void testGetNullableResultWithEmptyString() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(anyString())).thenReturn("");

        ClausePosition result = handler.getNullableResult(rs, "clause_position");

        assertNull(result);
    }

    @Test
    void testGetNullableResultWithInvalidJson() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(anyString())).thenReturn("invalid json");

        assertThrows(SQLException.class, () -> {
            handler.getNullableResult(rs, "clause_position");
        });
    }
}
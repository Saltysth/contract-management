package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.OperationDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OperationDetailsTypeHandlerTest {

    private OperationDetailsTypeHandler typeHandler;

    @BeforeEach
    void setUp() {
        typeHandler = new OperationDetailsTypeHandler();
    }

    @Test
    void testShouldHandleNullValue() throws SQLException {
        OperationDetails result = typeHandler.parseJson(null);
        assertNull(result);
    }

    @Test
    void testShouldHandleEmptyString() throws SQLException {
        OperationDetails result = typeHandler.parseJson("   ");
        assertNull(result);
    }

    @Test
    void testShouldParseValidJson() throws SQLException {
        String json = "{\"description\":\"Test operation\",\"result\":\"SUCCESS\"}";

        OperationDetails result = typeHandler.parseJson(json);

        assertNotNull(result);
        assertEquals("Test operation", result.getDescription());
        assertEquals("SUCCESS", result.getResult());
    }

    @Test
    void testShouldHandleComplexJson() throws SQLException {
        Map<String, Object> beforeState = new HashMap<>();
        beforeState.put("status", "DRAFT");
        beforeState.put("version", 1);

        Map<String, Object> afterState = new HashMap<>();
        afterState.put("status", "ACTIVE");
        afterState.put("version", 2);

        String json = String.format(
            "{\"description\":\"Update contract\",\"result\":\"SUCCESS\"," +
            "\"beforeState\":%s,\"afterState\":%s,\"executionTimeMs\":1500}",
            "{\"status\":\"DRAFT\",\"version\":1}",
            "{\"status\":\"ACTIVE\",\"version\":2}"
        );

        OperationDetails result = typeHandler.parseJson(json);

        assertNotNull(result);
        assertEquals("Update contract", result.getDescription());
        assertEquals("SUCCESS", result.getResult());
        assertNotNull(result.getBeforeState());
        assertNotNull(result.getAfterState());
        assertEquals("DRAFT", result.getBeforeState().get("status"));
        assertEquals("ACTIVE", result.getAfterState().get("status"));
        assertEquals(Long.valueOf(1500), result.getExecutionTimeMs());
    }

    @Test
    void testShouldThrowExceptionForInvalidJson() {
        String invalidJson = "{invalid json}";

        SQLException exception = assertThrows(SQLException.class, () -> {
            typeHandler.parseJson(invalidJson);
        });

        assertTrue(exception.getMessage().contains("Failed to deserialize JSON to OperationDetails"));
    }
}
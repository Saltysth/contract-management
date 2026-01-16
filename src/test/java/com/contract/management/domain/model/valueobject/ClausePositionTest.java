package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 条款位置值对象测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
class ClausePositionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserializeJsonWithUnknownProperties() throws Exception {
        // JSON containing unknown properties (area, width, height)
        String jsonWithUnknownProperties = """
            {
                "area": 5000.0,
                "endX": 100.0,
                "endY": 50.0,
                "page": 1,
                "width": 100.0,
                "height": 50.0,
                "startX": 0.0,
                "startY": 0.0
            }
            """;

        // This should not throw an exception despite unknown properties
        ClausePosition position = objectMapper.readValue(jsonWithUnknownProperties, ClausePosition.class);

        assertNotNull(position);
        assertEquals(1, position.getPage());
        assertEquals(0.0, position.getStartX());
        assertEquals(0.0, position.getStartY());
        assertEquals(100.0, position.getEndX());
        assertEquals(50.0, position.getEndY());

        // Verify calculated properties match the expected values
        assertEquals(100.0, position.getWidth());
        assertEquals(50.0, position.getHeight());
        assertEquals(5000.0, position.getArea());
    }

    @Test
    void testDeserializeJsonWithoutUnknownProperties() throws Exception {
        // JSON without unknown properties
        String jsonWithoutUnknownProperties = """
            {
                "page": 2,
                "startX": 10.5,
                "startY": 20.5,
                "endX": 110.5,
                "endY": 70.5
            }
            """;

        ClausePosition position = objectMapper.readValue(jsonWithoutUnknownProperties, ClausePosition.class);

        assertNotNull(position);
        assertEquals(2, position.getPage());
        assertEquals(10.5, position.getStartX());
        assertEquals(20.5, position.getStartY());
        assertEquals(110.5, position.getEndX());
        assertEquals(70.5, position.getEndY());

        // Verify calculated properties
        assertEquals(100.0, position.getWidth());
        assertEquals(50.0, position.getHeight());
        assertEquals(5000.0, position.getArea());
    }

    @Test
    void testSerializeToJson() throws Exception {
        ClausePosition position = ClausePosition.of(1, 0.0, 0.0, 100.0, 50.0);

        String json = objectMapper.writeValueAsString(position);

        assertNotNull(json);
        assertTrue(json.contains("\"page\":1"));
        assertTrue(json.contains("\"startX\":0.0"));
        assertTrue(json.contains("\"startY\":0.0"));
        assertTrue(json.contains("\"endX\":100.0"));
        assertTrue(json.contains("\"endY\":50.0"));
    }
}
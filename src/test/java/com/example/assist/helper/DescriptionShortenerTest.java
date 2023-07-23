package com.example.assist.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DescriptionShortenerTest {

    final String EXAMPLE_DESCRIPTION = "ABC Responsibilities EFG Requirements HIJ";
    final String SHORT_DESCRIPTION = "Responsibilities EFG Requirements HIJ";

    @Test
    void test_shortenDescription_nullDescription_success() {
        final String result1 = DescriptionShortener.shortenDescription(null);
        assertNull(result1);
        final String result2 = DescriptionShortener.shortenDescription("");
        assertEquals(result2.length(), 0);
    }

    @Test
    void test_shortenDescription_exampleDescription_success() {
        final String result = DescriptionShortener.shortenDescription(EXAMPLE_DESCRIPTION);
        assertEquals(result, SHORT_DESCRIPTION);
    }
}

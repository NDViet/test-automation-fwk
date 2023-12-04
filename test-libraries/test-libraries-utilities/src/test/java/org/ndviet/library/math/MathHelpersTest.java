package org.ndviet.library.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.ndviet.library.configuration.ConfigurationHelpers;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathHelpersTest {

    @ParameterizedTest
    @MethodSource("provideParametersForNumberDecimalFormat")
    public void numberDecimalFormat(String input, String decimal, String roundingMode, String expected) {
        String result = MathHelpers.numberDecimalFormat(input, decimal, roundingMode);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideParametersForNumberDecimalFormat() {
        return Stream.of(
                Arguments.of("1234.5678", "#.##", "HALF_UP", "1234.57"),
                Arguments.of("invalid", "#.##", "HALF_UP", "invalid"),
                Arguments.of("1234.5678", "#.##", null, "1234.57"),
                Arguments.of("1234.5678", null, "HALF_UP", "1234.5678")
        );
    }

    @ParameterizedTest
    @CsvSource({
            "1234.5678, true",
            "0, true",
            "-1234, true",
            "1.23E3, true",
            "invalid, false",
            ", false"
    })
    public void isCreatable(String input, boolean expected) {
        assertEquals(expected, MathHelpers.isCreatable(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'1234,5678', true",
            "'-0,75', true",
    })
    public void isCreatable_different_locate(String input, boolean expected) {
        try (MockedStatic<ConfigurationHelpers> mockHelpers = Mockito.mockStatic(ConfigurationHelpers.class)) {
            mockHelpers.when(ConfigurationHelpers::getSystemLocale).thenReturn(Locale.FRANCE);
            assertEquals(expected, MathHelpers.isCreatable(input));
        }
    }
}
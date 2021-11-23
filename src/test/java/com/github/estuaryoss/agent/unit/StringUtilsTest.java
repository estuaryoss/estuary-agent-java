package com.github.estuaryoss.agent.unit;

import com.github.estuaryoss.agent.utils.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "a;2;a",
                    "aa;3;aa"
            }
    )
    public void whenMaxSizeIsGreaterThanInputLenght_ThenOutIsTheSame(String inOutPairs) {
        String[] inOut = inOutPairs.split(";");
        String out = StringUtils.trimString(inOut[0], Integer.parseInt(inOut[1]));

        assertThat(out).isEqualTo(inOut[2]);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "abcd;3;bcd",
                    "123456;2;56",
                    "123456;1;6"
            }
    )
    public void whenMaxSizeIsLessThanInputLenght_ThenOutTruncatedToTheLast(String inOutPairs) {
        String[] inOut = inOutPairs.split(";");
        String out = StringUtils.trimString(inOut[0], Integer.parseInt(inOut[1]));

        assertThat(out).isEqualTo(inOut[2]);
    }
}

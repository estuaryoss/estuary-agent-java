package com.github.estuaryoss.agent.unit;

import com.github.estuaryoss.agent.utils.TemplateGluer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateGluerTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "{FOO1}/A;FOO1;B;B/A",
                    "A;A;A;A",
                    "A/{FOO1};FOO1;B;A/B",
            }
    )
    public void whenSwapping_ThenProfit(String templateInVarNameInVarValueExpectedOut) {
        String[] pieces = templateInVarNameInVarValueExpectedOut.split(";");
        String template = pieces[0];
        String swapName = pieces[1];
        String swapValue = pieces[2];
        String expectedOut = pieces[3];

        String out = TemplateGluer.glue(template, new HashMap<>() {{
            put(swapName, swapValue);
        }});

        assertThat(out).isEqualTo(expectedOut);
    }
}

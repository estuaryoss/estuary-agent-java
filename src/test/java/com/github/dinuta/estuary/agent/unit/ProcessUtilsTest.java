package com.github.dinuta.estuary.agent.unit;

import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUtilsTest {
    @Test
    public void whenGettingTheSystemProcessesThenTheListIsGreaterThanZero() {
        List<Map<String, Object>> processUtils = ProcessUtils.getProcesses();

        assertThat(processUtils.size()).isGreaterThan(0);
        assertThat(processUtils.get(0).size()).isEqualTo(4);
    }
}

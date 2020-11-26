package com.github.dinuta.estuary.agent.unit;

import com.github.dinuta.estuary.agent.model.ProcessInfo;
import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUtilsTest {
    @Test
    public void whenGettingTheSystemProcessesThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcesses();

        assertThat(processUtils.size()).isGreaterThan(0);
    }
}

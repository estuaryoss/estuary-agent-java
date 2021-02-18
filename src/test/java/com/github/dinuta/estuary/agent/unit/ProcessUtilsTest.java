package com.github.dinuta.estuary.agent.unit;

import com.github.dinuta.estuary.agent.model.ProcessInfo;
import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUtilsTest {
    @Test
    public void whenGettingTheSystemProcesses_ThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcesses();

        assertThat(processUtils.size()).isGreaterThan(0);
    }


    @Test
    public void whenGettingTheSystemProcessesForPid_ThenTheListIsOne() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcessInfoForPid(
                ProcessUtils.getProcesses().get(0).getPid());

        assertThat(processUtils.size()).isEqualTo(1);
    }

    @Test
    public void whenGettingTheSystemProcessesForInvalidPid_ThenTheListIsOne() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcessInfoForPid(-999L);

        assertThat(processUtils.size()).isEqualTo(0);
    }

    @Test
    public void whenGettingTheSystemProcessesForExec_ThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcessInfoForExec("java");

        assertThat(processUtils.size()).isGreaterThan(0);
    }

    @Test
    public void whenGettingTheSystemProcessesForInvalidExec_ThenTheListIsZero() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcessInfoForExec("some_invalid_exec_name");

        assertThat(processUtils.size()).isEqualTo(0);
    }
}

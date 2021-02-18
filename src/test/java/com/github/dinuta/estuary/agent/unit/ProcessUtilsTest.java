package com.github.dinuta.estuary.agent.unit;

import com.github.dinuta.estuary.agent.model.ProcessInfo;
import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUtilsTest {
    @Test
    public void whenGettingTheSystemProcesses_ThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcesses();

        assertThat(processInfoList.size()).isGreaterThan(0);
    }


    @Test
    public void whenGettingTheSystemProcessesForPid_ThenTheListIsOne() {
        List<ProcessInfo> processList = ProcessUtils.getProcesses();
        assertThat(processList.size()).isGreaterThan(0);

        List<ProcessInfo> processesPid = ProcessUtils.getProcessInfoForPid(processList.get(0).getPid());

        assertThat(processesPid.size()).isEqualTo(1);
    }

    @Test
    public void whenGettingTheSystemProcessesForInvalidPid_ThenTheListIsOne() {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcessInfoForPid(-999L);

        assertThat(processInfoList.size()).isEqualTo(0);
    }

    @Test
    public void whenGettingTheSystemProcessesForExec_ThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcessInfoForExec("java");

        assertThat(processInfoList.size()).isGreaterThan(0);
    }

    @Test
    public void whenGettingTheSystemProcessesForInvalidExec_ThenTheListIsZero() {
        List<ProcessInfo> processUtils = ProcessUtils.getProcessInfoForExec("some_invalid_exec_name");

        assertThat(processUtils.size()).isEqualTo(0);
    }
}

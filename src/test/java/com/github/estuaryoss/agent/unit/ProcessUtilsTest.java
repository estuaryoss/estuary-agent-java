package com.github.estuaryoss.agent.unit;

import com.github.estuaryoss.agent.model.ProcessInfo;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUtilsTest {
    @Test
    public void whenGettingTheSystemProcesses_ThenTheListIsGreaterThanZero() {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcesses(true);

        assertThat(processInfoList.size()).isGreaterThan(0);
    }


    @Test
    public void whenGettingTheSystemProcessesForPid_ThenTheListHasOneEntry() {
        List<ProcessInfo> processList = ProcessUtils.getProcesses(true);
        assertThat(processList.size()).isGreaterThan(0);

        List<ProcessInfo> processesPid = ProcessUtils.getProcessInfoForPid(processList.get(0).getPid(), true);

        assertThat(processesPid.size()).isEqualTo(1);
    }

    @Test
    public void whenGettingTheSystemProcessesForInvalidPid_ThenTheListIsOne() {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcessInfoForPid(-999L, true);

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

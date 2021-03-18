package com.github.estuaryoss.agent.component;

import com.github.estuaryoss.agent.model.ProcessState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessHolder {
    private Map<ProcessState, String> inMemoryCmdProcessState = new HashMap<>();
    private final int SIZE = 50;

    public void put(String[] command, ProcessState processState) {
        if (inMemoryCmdProcessState.size() <= SIZE)
            inMemoryCmdProcessState.put(processState, joinCommand(command));
    }

    public void clearAll() {
        inMemoryCmdProcessState.clear();
    }

    public Map<ProcessState, String> getAll() {
        return inMemoryCmdProcessState;
    }

    public void remove(String[] command) {
        inMemoryCmdProcessState.remove(joinCommand(command));
    }

    public Map<String, String> dumpAll() {
        Map<String, String> dumpCmdProcessState = new HashMap<>();
        inMemoryCmdProcessState.forEach((pState, cmd) -> {
            dumpCmdProcessState.put(pState.toString(), cmd);
        });
        return dumpCmdProcessState;
    }

    private String joinCommand(String[] command) {
        return String.join(" ", command);
    }
}

package com.github.dinuta.estuary.agent.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessUtils {

    public static List<Map<String, Object>> getProcesses() {
        List customProcessInfoList = new ArrayList();
        ProcessHandle.allProcesses().forEach(p -> {
            Map<String, Object> processInfo = new LinkedHashMap<>();
            processInfo.put("status", "NA");
            processInfo.put("name", p.info().command().orElse(""));
            processInfo.put("pid", p.pid());
            processInfo.put("username", p.info().user().orElse(""));
            customProcessInfoList.add(processInfo);
        });

        return customProcessInfoList;
    }
}

package com.github.dinuta.estuary.agent.utils;

import com.github.dinuta.estuary.agent.model.ProcessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProcessUtils {
    private static final Logger log = LoggerFactory.getLogger(ProcessUtils.class);

    private static String EXEC = "start.py";

    public static List<ProcessInfo> getProcesses() {
        List customProcessInfoList = new ArrayList();
        ProcessHandle.allProcesses().forEach(p -> {
            long parent = -1L;
            List<String> arguments = new ArrayList<>();
            try {
                parent = p.parent().get().pid();
            } catch (Exception e) {
                //try at best
            }
            try {
                arguments = Arrays.asList(p.info().arguments().get());
            } catch (Exception e) {
                //try at best
            }

            ProcessInfo processInfo = new ProcessInfo()
                    .status("NA")
                    .name(p.info().command().orElse(""))
                    .pid(p.pid())
                    .username(p.info().user().orElse(""))
                    .parent(parent)
                    .arguments(arguments);

            customProcessInfoList.add(processInfo);
        });

        return customProcessInfoList;
    }

    public static List<ProcessInfo> getProcessesWithChildren() {
        List customProcessInfoList = new ArrayList();
        ProcessHandle.allProcesses().forEach(p -> {
            long parent = -1L;
            List<String> arguments = new ArrayList<>();
            try {
                parent = p.parent().get().pid();
            } catch (Exception e) {
                //try at best
            }
            try {
                arguments = Arrays.asList(p.info().arguments().get());
            } catch (Exception e) {
                //try at best
            }

            ProcessInfo processInfo = new ProcessInfo()
                    .status("NA")
                    .name(p.info().command().orElse(""))
                    .pid(p.pid())
                    .username(p.info().user().orElse(""))
                    .parent(parent)
                    .arguments(arguments)
                    .children(p.children().collect(Collectors.toList()));

            customProcessInfoList.add(processInfo);
        });

        return customProcessInfoList;
    }


    public static ProcessInfo getParentProcessForDetachedCmd(String commandId) {
        long processId = 100000L;
        ProcessInfo processInfo = new ProcessInfo();
        List<ProcessInfo> filteredProcessInfoList = getProcessesWithChildren().stream().filter(elem ->
                elem.getName().contains(EXEC))
                .collect(Collectors.toList());

        for (int i = 0; i < filteredProcessInfoList.size(); i++) {
            if (filteredProcessInfoList.get(i).getPid() < processId) {
                processId = filteredProcessInfoList.get(i).getPid();
                processInfo = filteredProcessInfoList.get(i);
            }
        }
        return processInfo;
    }

    public static void killChildrenProcesses(List<ProcessHandle> childrenProcesses) throws IOException, InterruptedException, TimeoutException {
        for (int i = 0; i < childrenProcesses.size(); i++) {
            if (childrenProcesses.get(i).children().collect(Collectors.toList()).size() > 0)
                killChildrenProcesses(childrenProcesses.get(i).children().collect(Collectors.toList()));
            log.debug("Killing child PID " + (int) childrenProcesses.get(i).pid() + " is alive: " + childrenProcesses.get(i).isAlive());
            killProcess(childrenProcesses.get(i));
        }
    }

    public static void killProcess(ProcessInfo processInfo) throws IOException, InterruptedException, TimeoutException {
        PidProcess process = Processes.newPidProcess((int) processInfo.getPid());

        log.debug("Process PID " + (int) processInfo.getPid() + " is alive: " + process.isAlive());
        if (process.isAlive() && (int) processInfo.getPid() != 0)
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
        log.debug("Process PID " + (int) processInfo.getPid() + " is alive: " + process.isAlive());
    }

    public static void killProcess(ProcessHandle processHandle) throws IOException, InterruptedException, TimeoutException {
        PidProcess process = Processes.newPidProcess((int) processHandle.pid());

        log.debug("Process PID " + (int) processHandle.pid() + " is alive: " + process.isAlive());
        if (process.isAlive() && (int) processHandle.pid() != 0)
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
        log.debug("Process PID " + (int) processHandle.pid() + " is alive: " + process.isAlive());
    }
}

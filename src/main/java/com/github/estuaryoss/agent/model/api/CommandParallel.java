package com.github.estuaryoss.agent.model.api;

import com.github.estuaryoss.agent.entity.Command;
import com.github.estuaryoss.agent.model.ProcessState;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandParallel {
    @Setter
    @Getter
    private int threadId;
    @Setter
    @Getter
    private Command command;
    @Setter
    @Getter
    private CommandDescription commandDescription;
    @Setter
    @Getter
    private ArrayList<CommandStatus> commandStatuses;
    @Setter
    @Getter
    private LinkedHashMap<String, CommandStatus> commandsStatus;
    @Setter
    @Getter
    private ProcessState processState;
}

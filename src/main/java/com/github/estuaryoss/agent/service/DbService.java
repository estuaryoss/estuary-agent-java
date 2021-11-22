package com.github.estuaryoss.agent.service;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import com.github.estuaryoss.agent.entity.FinishedCommand;
import com.github.estuaryoss.agent.model.ProcessState;
import com.github.estuaryoss.agent.model.api.CommandStatus;
import com.github.estuaryoss.agent.repository.ActiveCommandRepository;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.COMMAND_MAX_SIZE;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FIELD_MAX_SIZE;
import static com.github.estuaryoss.agent.utils.StringUtils.trimString;

@Service
public class DbService {
    @Autowired
    private ActiveCommandRepository activeCommandRepository;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    private Comparator<FinishedCommand> finishedCommandComparator = Comparator.comparing(finishedCommand -> finishedCommand.getFinishedAt());

    public void saveActiveCommand(String command, String commandId, ProcessState processState) {
        ActiveCommand activeCommand = ActiveCommand.builder()
                .commandId(commandId)
                .command(command)
                .startedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .pid(processState.getProcess().pid())
                .build();

        activeCommandRepository.saveAndFlush(activeCommand);
    }

    public void saveFinishedCommand(String command, String commandId, CommandStatus commandStatus) {
        FinishedCommand finishedCommand = FinishedCommand.builder()
                .command(trimString(command, COMMAND_MAX_SIZE))
                .commandId(commandId)
                .code(commandStatus.getDetails().getCode())
                .out(trimString(commandStatus.getDetails().getOut(), FIELD_MAX_SIZE))
                .err(trimString(commandStatus.getDetails().getErr(), FIELD_MAX_SIZE))
                .startedAt(commandStatus.getStartedat())
                .finishedAt(commandStatus.getFinishedat())
                .duration(commandStatus.getDuration())
                .pid(commandStatus.getDetails().getPid())
                .build();

        finishedCommandRepository.saveAndFlush(finishedCommand);
    }

    public void clearAllActiveCommands() {
        activeCommandRepository.deleteAll();
    }

    public List<FinishedCommand> getFinishedCommands(long limit) {
        return finishedCommandRepository
                .findAll(Sort.by(Sort.Order.desc("id")))
                .stream().limit(limit).collect(Collectors.toList());
    }

    public List<ActiveCommand> getAllActiveCommands() {
        return activeCommandRepository.findAll(Sort.by(Sort.Order.desc("id")))
                .stream().collect(Collectors.toList());
    }

    public void removeActiveCommand(long pid) {
        ActiveCommand activeCommand = activeCommandRepository.findActiveCommandByPid(pid);
        if (activeCommand != null) activeCommandRepository.delete(activeCommand);
    }

    public List<ActiveCommand> getAllActiveCommandsByCommandId(String commandId) {
        return activeCommandRepository.findActiveCommandByCommandId(commandId);
    }

    public List<FinishedCommand> getAllFinishedCommandsByCommandId(String commandId) {
        List<FinishedCommand> finishedCommandsList = finishedCommandRepository.findFinishedCommandByCommandId(commandId);
        finishedCommandsList.sort(finishedCommandComparator);

        return finishedCommandsList;
    }

    private String joinCommand(String[] command) {
        return String.join(" ", command);
    }
}

package com.github.estuaryoss.agent.service;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import com.github.estuaryoss.agent.entity.FinishedCommand;
import com.github.estuaryoss.agent.model.ProcessState;
import com.github.estuaryoss.agent.repository.ActiveCommandRepository;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbService {
    @Autowired
    private ActiveCommandRepository activeCommandRepository;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    public void save(String[] command, ProcessState processState) {
        ActiveCommand activeCommand = ActiveCommand.builder()
                .command(joinCommand(command))
                .startedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .pid(processState.getProcess().pid())
                .build();

        activeCommandRepository.saveAndFlush(activeCommand);
    }

    public void clearAll() {
        activeCommandRepository.deleteAll();
    }

    public List<ActiveCommand> getAllActiveCommands() {
        return activeCommandRepository.findAll(Sort.by(Sort.Order.desc("id")))
                .stream().collect(Collectors.toList());
    }

    public List<FinishedCommand> getAllFinishedCommands(long limit) {
        return finishedCommandRepository
                .findAll(Sort.by(Sort.Order.desc("id")))
                .stream().limit(limit).collect(Collectors.toList());
    }

    public void remove(long pid) {
        ActiveCommand activeCommand = activeCommandRepository.findActiveCommandByPid(pid);

        activeCommandRepository.delete(activeCommand);
    }

    private String joinCommand(String[] command) {
        return String.join(" ", command);
    }
}

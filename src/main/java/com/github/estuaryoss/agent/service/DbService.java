package com.github.estuaryoss.agent.service;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import com.github.estuaryoss.agent.entity.FileTransfer;
import com.github.estuaryoss.agent.entity.FinishedCommand;
import com.github.estuaryoss.agent.model.ProcessState;
import com.github.estuaryoss.agent.model.api.CommandStatus;
import com.github.estuaryoss.agent.repository.ActiveCommandRepository;
import com.github.estuaryoss.agent.repository.FileTransferRepository;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.*;
import static com.github.estuaryoss.agent.utils.StringUtils.trimString;

@Service
public class DbService {
    @Autowired
    private ActiveCommandRepository activeCommandRepository;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    @Autowired
    private FileTransferRepository fileTransferRepository;

    public void saveFileTransfer(FileTransfer fileTransfer) {
        fileTransferRepository.saveAndFlush(fileTransfer);
    }

    public List<FileTransfer> getFileTransfers(long limit) {
        return fileTransferRepository.findAll(Sort.by(Sort.Order.desc("id")))
                .stream().limit(limit).collect(Collectors.toList());
    }

    public void saveActiveCommand(String command, ProcessState processState) {
        ActiveCommand activeCommand = ActiveCommand.builder()
                .command(trimString(command, COMMAND_MAX_SIZE))
                .startedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .pid(processState.getProcess().pid())
                .status("running")
                .build();

        activeCommandRepository.saveAndFlush(activeCommand);
    }

    public void saveFinishedCommand(String command, CommandStatus commandStatus) {
        FinishedCommand finishedCommand = FinishedCommand.builder()
                .command(trimString(command, COMMAND_MAX_SIZE))
                .code(commandStatus.getDetails().getCode())
                .out(trimString(commandStatus.getDetails().getOut(), COMMAND_STDOUT_MAX_SIZE))
                .err(trimString(commandStatus.getDetails().getErr(), COMMAND_STDERR_MAX_SIZE))
                .startedAt(commandStatus.getStartedat())
                .finishedAt(commandStatus.getFinishedat())
                .duration(commandStatus.getDuration())
                .pid(commandStatus.getDetails().getPid())
                .status("finished")
                .build();

        finishedCommandRepository.saveAndFlush(finishedCommand);
    }

    public void clearAllActiveCommands() {
        activeCommandRepository.deleteAll();
    }
    public void clearActiveCommandByPid(long pid) {
        activeCommandRepository.deleteByPid(pid);
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
}

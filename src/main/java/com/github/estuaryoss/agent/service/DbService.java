package com.github.estuaryoss.agent.service;

import com.github.estuaryoss.agent.entity.Command;
import com.github.estuaryoss.agent.entity.FileTransfer;
import com.github.estuaryoss.agent.repository.CommandRepository;
import com.github.estuaryoss.agent.repository.FileTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbService {
    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private FileTransferRepository fileTransferRepository;

    public void saveFileTransfer(FileTransfer fileTransfer) {
        fileTransferRepository.saveAndFlush(fileTransfer);
    }

    public List<FileTransfer> getFileTransfers(long limit) {
        return fileTransferRepository.findAll(Sort.by(Sort.Order.desc("id")))
                .stream().limit(limit).collect(Collectors.toList());
    }

    public void saveCommand(Command command) {
        commandRepository.save(command);
    }

    public void saveAndFlushCommand(Command command) {
        commandRepository.saveAndFlush(command);
    }

    public void clearActiveCommandByPid(long pid) {
        commandRepository.deleteByPid(pid);
    }

    public List<Command> getCommands(String status, long limit) {
        List<Command> commandList = commandRepository.findCommandByStatus(status)
                .stream().limit(limit).collect(Collectors.toList());

        Collections.sort(commandList, Comparator.comparing(Command::getId).reversed());

        return commandList;
    }

    public List<Command> getCommands(long limit) {
        List<Command> runningAndFinishedCommands = commandRepository.findAll(Sort.by(Sort.Order.desc("id")))
                .stream().limit(limit).collect(Collectors.toList());

        Collections.sort(runningAndFinishedCommands, Comparator.comparing(Command::getStatus).reversed());

        return runningAndFinishedCommands;
    }
}

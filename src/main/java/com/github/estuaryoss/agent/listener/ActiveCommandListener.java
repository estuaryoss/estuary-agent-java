package com.github.estuaryoss.agent.listener;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import com.github.estuaryoss.agent.service.DbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.PostRemove;

@Component
@Slf4j
public class ActiveCommandListener {
    private final DbService dbService;

    @Autowired
    public ActiveCommandListener(@Lazy DbService dbService) {
        this.dbService = dbService;
    }

    @PostRemove
    public void saveAsFinishedCommand(ActiveCommand activeCommand) {
        dbService.saveAsFinishedCommand(activeCommand);
    }
}

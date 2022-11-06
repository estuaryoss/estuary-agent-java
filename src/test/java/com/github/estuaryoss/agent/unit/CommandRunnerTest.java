package com.github.estuaryoss.agent.unit;

import com.github.estuaryoss.agent.component.AppEnvironment;
import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRunnerTest {

    @Test
    public void whenRunningSimpleCommandWithNullDbService_ThenSuccess() throws IOException {
        String command = "echo 1";
        CommandRunner commandRunner = new CommandRunner(null, new AppEnvironment());

        CommandDescription commandDescription = commandRunner.runCommands(new String[]{command});

        assertThat(commandDescription.getCommands().get(command).getDetails().getCode()).isEqualTo(0);
        assertThat(commandDescription.getCommands().get(command).getDetails().getOut().trim()).isEqualTo("1");
        assertThat(commandDescription.getCommands().get(command).getDetails().getErr()).isBlank();
    }
}

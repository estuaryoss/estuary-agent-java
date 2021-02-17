package com.github.dinuta.estuary.agent;

import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import com.github.dinuta.estuary.agent.constants.FluentdServiceConstants;
import com.github.dinuta.estuary.agent.service.FluentdService;
import com.github.dinuta.estuary.agent.utils.MessageDumper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.io.File;

import static com.github.dinuta.estuary.agent.constants.DefaultConstants.*;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.github.dinuta.estuary.agent",
        "com.github.dinuta.estuary.agent.api",
        "com.github.dinuta.estuary.agent.configuration",
        "com.github.dinuta.estuary.agent.component",
        "com.github.dinuta.estuary.agent.handler"
})
public class TestAgentSpringBoot implements CommandLineRunner {
    @Autowired
    private FluentdService fluentdService;

    @Autowired
    private VirtualEnvironment environment;

    public static void main(String[] args) {
        SpringApplication.run(TestAgentSpringBoot.class, args);
    }

    @Value("${app.folder.streams}")
    private String backgroundStreamsFolderName;

    @Value("${app.folder.commands}")
    private String backgroundCommandsFolderName;

    private void createFolders() {
        File commandsFolder = new File(BACKGROUND_COMMANDS_FOLDER);
        File streamsFolder = new File(BACKGROUND_COMMANDS_STREAMS_FOLDER);
        if (!commandsFolder.exists()) commandsFolder.mkdirs();
        if (!streamsFolder.exists()) streamsFolder.mkdirs();
    }

    private void initFolderConstants() {
        BACKGROUND_COMMANDS_FOLDER = backgroundCommandsFolderName;
        BACKGROUND_COMMANDS_STREAMS_FOLDER = backgroundStreamsFolderName;
    }

    @PostConstruct
    public void postConstruct() {
        initFolderConstants();
        createFolders();
    }

    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new ExitException();
        }

        fluentdService.emit(FluentdServiceConstants.STARTUP,
                MessageDumper.dumpMessage(environment.getEnvAndVirtualEnv().toString()));
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return PROCESS_EXCEPTION_GENERAL;
        }
    }
}

package com.github.estuaryoss.agent;

import com.github.estuaryoss.agent.component.VirtualEnvironment;
import com.github.estuaryoss.agent.constants.DefaultConstants;
import com.github.estuaryoss.agent.constants.FluentdServiceConstants;
import com.github.estuaryoss.agent.service.Fluentd;
import com.github.estuaryoss.agent.utils.MessageDumper;
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

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.github.estuaryoss.agent",
        "com.github.estuaryoss.agent.api",
        "com.github.estuaryoss.agent.configuration",
        "com.github.estuaryoss.agent.component",
        "com.github.estuaryoss.agent.handler"
})
public class EstuaryAgent implements CommandLineRunner {
    @Autowired
    private Fluentd fluentd;

    @Autowired
    private VirtualEnvironment environment;

    public static void main(String[] args) {
        SpringApplication.run(EstuaryAgent.class, args);
    }

    @Value("${app.folder.streams}")
    private String backgroundStreamsFolderName;

    @Value("${app.folder.commands}")
    private String backgroundCommandsFolderName;

    @Value("${app.folder.uploads}")
    private String uploadsFolderName;

    private void createFolders() {
        File commandsFolder = new File(DefaultConstants.BACKGROUND_COMMANDS_FOLDER);
        File streamsFolder = new File(DefaultConstants.BACKGROUND_COMMANDS_STREAMS_FOLDER);
        File uploadsFolder = new File(DefaultConstants.UPLOADS_FOLDER);
        if (!commandsFolder.exists()) commandsFolder.mkdirs();
        if (!streamsFolder.exists()) streamsFolder.mkdirs();
        if (!uploadsFolder.exists()) uploadsFolder.mkdirs();
    }

    private void initFolderConstants() {
        DefaultConstants.BACKGROUND_COMMANDS_FOLDER = backgroundCommandsFolderName;
        DefaultConstants.BACKGROUND_COMMANDS_STREAMS_FOLDER = backgroundStreamsFolderName;
        DefaultConstants.UPLOADS_FOLDER = uploadsFolderName;
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

        fluentd.emit(FluentdServiceConstants.STARTUP,
                MessageDumper.dumpMessage(environment.getEnvAndVirtualEnv().toString()));
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return DefaultConstants.PROCESS_EXCEPTION_GENERAL;
        }
    }
}

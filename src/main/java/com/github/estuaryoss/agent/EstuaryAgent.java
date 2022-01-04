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

    @Value("${app.folder.uploads}")
    private String uploadsFolderName;

    @Value("${app.folder.downloads}")
    private String downloadsFolderName;

    private void createFolders() {
        File uploadsFolder = new File(DefaultConstants.UPLOADS_FOLDER);
        File downloadsFolder = new File(DefaultConstants.DOWNLOADS_FOLDER);
        if (!uploadsFolder.exists()) uploadsFolder.mkdirs();
        if (!downloadsFolder.exists()) downloadsFolder.mkdirs();
    }

    private void initFolderConstants() {
        DefaultConstants.UPLOADS_FOLDER = uploadsFolderName;
        DefaultConstants.DOWNLOADS_FOLDER = downloadsFolderName;
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

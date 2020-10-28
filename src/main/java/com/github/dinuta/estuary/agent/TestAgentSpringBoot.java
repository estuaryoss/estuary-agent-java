package com.github.dinuta.estuary.agent;

import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import com.github.dinuta.estuary.agent.constants.DefaultConstants;
import com.github.dinuta.estuary.agent.constants.FluentdServiceConstants;
import com.github.dinuta.estuary.agent.service.FluentdService;
import com.github.dinuta.estuary.agent.utils.MessageDumper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.github.dinuta.estuary.agent",
        "com.github.dinuta.estuary.agent.api",
        "com.github.dinuta.estuary.agent.config",
        "com.github.dinuta.estuary.agent.configuration",
        "com.github.dinuta.estuary.agent.component"
})
public class TestAgentSpringBoot implements CommandLineRunner {
    @Autowired
    private FluentdService fluentdService;

    @Autowired
    private VirtualEnvironment environment;

    public static void main(String[] args) {
        new SpringApplication(TestAgentSpringBoot.class).run(args);
    }

    @Override
    public void run(String... arg0) {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
        File file_cmds = new File(DefaultConstants.CMD_DETACHED_FOLDER);
        File file_streams = new File(DefaultConstants.STREAMS_DETACHED_FOLDER);
        if (!file_cmds.exists()) file_cmds.mkdirs();
        if (!file_streams.exists()) file_streams.mkdirs();

        fluentdService.emit(FluentdServiceConstants.STARTUP, MessageDumper.dumpMessage(environment.getEnvAndVirtualEnv().toString()));
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}

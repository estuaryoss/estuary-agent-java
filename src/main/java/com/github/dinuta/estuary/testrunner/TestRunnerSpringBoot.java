package com.github.dinuta.estuary.testrunner;

import com.github.dinuta.estuary.testrunner.constants.FluentdServiceConstants;
import com.github.dinuta.estuary.testrunner.service.FluentdService;
import com.github.dinuta.estuary.testrunner.utils.MessageDumper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableSwagger2
@ComponentScan(basePackages = {"com.github.dinuta.estuary.testrunner", "com.github.dinuta.estuary.testrunner.api", "com.github.dinuta.estuary.testrunner.configuration"})
public class TestRunnerSpringBoot implements CommandLineRunner {
    @Autowired
    FluentdService fluentdService;

    public static void main(String[] args) {
        new SpringApplication(TestRunnerSpringBoot.class).run(args);
    }

    @Override
    public void run(String... arg0) {
        fluentdService.emit(FluentdServiceConstants.STARTUP, MessageDumper.dumpMessage(System.getenv().toString()));
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}

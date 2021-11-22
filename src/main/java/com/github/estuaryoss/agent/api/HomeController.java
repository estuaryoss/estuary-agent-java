package com.github.estuaryoss.agent.api;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home redirection to swagger api documentation
 */
@Controller
@Api(tags = {"estuary-agent"}, description = "index page")
@Slf4j
public class HomeController {
    @GetMapping(value = "/")
    public String index() {
        log.info("redirect:swagger-ui/");
        return "redirect:swagger-ui/";
    }
}

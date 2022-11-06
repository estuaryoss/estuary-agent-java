package com.github.estuaryoss.agent.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home redirection to swagger api documentation
 */
@Controller
@Tag(name = "estuary-agent", description = "index page")
@Slf4j
public class HomeController {
    @GetMapping(value = "/")
    public String index() {
        log.info("redirect:/swagger-ui/index.html");
        return "redirect:/swagger-ui/index.html";
    }
}

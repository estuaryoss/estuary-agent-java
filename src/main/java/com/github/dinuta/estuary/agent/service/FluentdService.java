package com.github.dinuta.estuary.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.EnvConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.logging.EnrichedMessage;
import com.github.dinuta.estuary.agent.model.logging.ParentMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fluentd.logger.FluentLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Component
public class FluentdService {
    @Autowired
    private Environment environment;

    private FluentLogger fluentLogger;
    private EnrichedMessage enrichedMsgCopy;


    public FluentdService() {
        if (System.getenv(EnvConstants.FLUENTD_IP_PORT) != null)
            this.fluentLogger = FluentLogger.getLogger(About.getAppName(),
                    System.getenv(EnvConstants.FLUENTD_IP_PORT).split(":")[0],
                    Integer.parseInt(System.getenv(EnvConstants.FLUENTD_IP_PORT).split(":")[1]));
    }

    /**
     * Sends the log to the FluentD service. If FluentD is not enabled then it will only print the message back to the user.
     *
     * @param loggingLevel On which level to log the message
     * @param msg          The message to be sent into FluentD
     * @return A response which can be printed in console to be sure that it was sent to the FluentD service.
     * For success, the return value should be: 'emit: true'.
     */
    public LinkedHashMap emit(String loggingLevel, ParentMessage msg) {
        LinkedHashMap map = new LinkedHashMap();
        EnrichedMessage message = this.enrichLog("DEBUG", msg);

        map.put(FinalConsoleMessage.EMIT.getField(), this.emit(loggingLevel, message));
        map.put(FinalConsoleMessage.MESSAGE.getField(), enrichedMsgCopy);

        try {
            System.out.println(new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

    private EnrichedMessage enrichLog(String levelCode, ParentMessage parrentMessage) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        EnrichedMessage enrichedMessage = new EnrichedMessage();
        enrichedMessage.setName(About.getAppName());
        enrichedMessage.setPort(environment.getProperty("local.server.port"));
        enrichedMessage.setVersion(About.getVersion());
        enrichedMessage.setUname(new String[]{System.getProperty("os.name")});
        enrichedMessage.setJava(System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));
        enrichedMessage.setPid(ProcessHandle.current().pid());
        enrichedMessage.setLevelCode(levelCode);
        enrichedMessage.setMsg(parrentMessage);
        enrichedMessage.setTimestamp(LocalDateTime.now().format(customFormatter));


        return enrichedMessage;
    }

    private String emit(String loggingLevel, EnrichedMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();
        enrichedMsgCopy = new EnrichedMessage();
        try {
            enrichedMsgCopy = objectMapper
                    .readValue(objectMapper.writeValueAsString(message), EnrichedMessage.class);

            //not breaking elasticsearch. description has different shapes -> all will be string
            if (message.getMsg().getBody() instanceof ApiResponse) {
                ((LinkedHashMap) enrichedMsgCopy.getMsg().getBody()).put("description",
                        String.valueOf(((LinkedHashMap) enrichedMsgCopy.getMsg().getBody()).get("description")));
            }

        } catch (JsonProcessingException e) {
            enrichedMsgCopy = message;
            HashMap<String, String> exception = new HashMap<>();
            ParentMessage parentMessage = new ParentMessage();
            exception.put("message", ExceptionUtils.getStackTrace(e));
            parentMessage.setBody(exception);
            enrichedMsgCopy.setMsg(parentMessage);
        }

        if (System.getenv(EnvConstants.FLUENTD_IP_PORT) == null) {
            return String.format("Fluentd logging not enabled",
                    EnvConstants.FLUENTD_IP_PORT);
        }

        return String.valueOf(this.fluentLogger.log(loggingLevel, objectMapper.convertValue(enrichedMsgCopy, LinkedHashMap.class)));
    }

    private enum FinalConsoleMessage {
        EMIT("emit"),
        MESSAGE("message");

        private final String field;

        FinalConsoleMessage(String field) {
            this.field = field;
        }

        public String getField() {
            return this.field;
        }
    }
}
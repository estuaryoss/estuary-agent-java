package com.estuary.service;

import com.estuary.constants.About;
import org.fluentd.logger.FluentLogger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static com.estuary.constants.EnvConstants.FLUENTD_IP_PORT;

@Component
public class FluentdService {
    private FluentLogger fluentLogger;

    public FluentdService() {
        if (System.getenv(FLUENTD_IP_PORT) != null) {
            this.fluentLogger = FluentLogger.getLogger(About.getAppName(),
                    System.getenv(FLUENTD_IP_PORT).split(":")[0],
                    Integer.parseInt(System.getenv(FLUENTD_IP_PORT).split(":")[1]));
        }
    }

    private LinkedHashMap<String, Object> enrichLog(String levelCode, Object object) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LinkedHashMap<String, Object> enrichedLog = new LinkedHashMap<>();
        enrichedLog.put("name", About.getAppName());
        enrichedLog.put("version", About.getVersion());
        enrichedLog.put("uname", System.getProperty("os.name"));
        enrichedLog.put("java", System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));
        enrichedLog.put("pid", ProcessHandle.current().pid());
        enrichedLog.put("level_code", levelCode);
        enrichedLog.put("msg", object.toString());
        enrichedLog.put("timestamp", LocalDateTime.now().format(customFormatter));

        return enrichedLog;
    }

    private String emit(String loggingLevel, LinkedHashMap msg) {
        if (System.getenv(FLUENTD_IP_PORT) != null) {
            return String.format("Fluentd logging not enabled",
                    FLUENTD_IP_PORT);
        }
        return String.valueOf(this.fluentLogger.log(loggingLevel, msg));
    }

    public LinkedHashMap emit(String loggingLevel, Object msg) {
        LinkedHashMap map = new LinkedHashMap();
        LinkedHashMap<String, Object> message = this.enrichLog("DEBUG", msg);

        map.put(FinalConsoleMessage.EMIT.getField(), this.emit(loggingLevel, message));
        map.put(FinalConsoleMessage.MESSAGE.getField(), message);

        System.out.println(map);
        return map;
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

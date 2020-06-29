package com.github.dinuta.estuary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class CommandDescription {
    @JsonProperty("finished")
    private boolean finished;

    @JsonProperty("started")
    private boolean started;

    @JsonProperty("startedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime startedat = LocalDateTime.now();

    @JsonProperty("finishedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime finishedat = LocalDateTime.now();

    @JsonProperty("duration")
    private long duration = 0;

    @JsonProperty("pid")
    private long pid = 0;

    @JsonProperty("id")
    private String id = "none";

    @JsonProperty("commands")
    private LinkedHashMap<String, CommandStatus> commands = new LinkedHashMap<>();

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean getStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public LocalDateTime getFinishedat() {
        return finishedat;
    }

    public void setFinishedat(LocalDateTime finishedat) {
        this.finishedat = finishedat;
    }

    public LocalDateTime getStartedat() {
        return startedat;
    }

    public void setStartedat(LocalDateTime startedat) {
        this.startedat = startedat;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public LinkedHashMap<String, CommandStatus> getCommands() {
        return commands;
    }

    public void setCommands(LinkedHashMap<String, CommandStatus> commands) {
        this.commands = commands;
    }


    public CommandDescription finished(boolean finished) {
        this.finished = finished;
        return this;
    }

    public CommandDescription started(boolean started) {
        this.started = started;
        return this;
    }

    public CommandDescription finishedat(LocalDateTime finishedat) {
        this.finishedat = finishedat;
        return this;
    }

    public CommandDescription startedat(LocalDateTime startedat) {
        this.startedat = startedat;
        return this;
    }

    public CommandDescription duration(long duration) {
        this.duration = duration;
        return this;
    }

    public CommandDescription pid(long pid) {
        this.pid = pid;
        return this;
    }

    public CommandDescription id(String id) {
        this.id = id;
        return this;
    }

    public CommandDescription commands(LinkedHashMap<String, CommandStatus> commands) {
        this.commands = commands;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    finished: ").append(toIndentedString(finished));
        sb.append("    started: ").append(toIndentedString(started));
        sb.append("    startedat: ").append(toIndentedString(startedat));
        sb.append("    finishedat: ").append(toIndentedString(finishedat));
        sb.append("    duration: ").append(toIndentedString(duration));
        sb.append("    pid: ").append(toIndentedString(pid));
        sb.append("    commands: ").append(toIndentedString(commands));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

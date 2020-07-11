package com.github.dinuta.estuary.testrunner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CommandStatus {
    @JsonProperty("status")
    private String status = null;

    @JsonProperty("details")
    private CommandDetails details = null;

    @JsonProperty("startedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime startedat = null;

    @JsonProperty("finishedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime finishedat = null;

    @JsonProperty("duration")
    private float duration = 0;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public CommandDetails getDetails() {
        return details;
    }

    public void setDetails(CommandDetails details) {
        this.details = details;
    }

    public CommandStatus status(String status) {
        this.status = status;
        return this;
    }

    public CommandStatus details(CommandDetails details) {
        this.details = details;
        return this;
    }

    public CommandStatus finishedat(LocalDateTime finishedat) {
        this.finishedat = finishedat;
        return this;
    }

    public CommandStatus startedat(LocalDateTime startedat) {
        this.startedat = startedat;
        return this;
    }

    public CommandStatus duration(float duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    status: ").append(toIndentedString(status));
        sb.append("    details: ").append(toIndentedString(details));
        sb.append("    startedat: ").append(toIndentedString(startedat));
        sb.append("    finishedat: ").append(toIndentedString(finishedat));
        sb.append("    duration: ").append(toIndentedString(duration));
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

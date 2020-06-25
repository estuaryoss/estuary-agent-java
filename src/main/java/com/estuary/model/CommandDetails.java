package com.estuary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommandDetails {
    @JsonProperty("out")
    private String out = null;

    @JsonProperty("err")
    private String err = null;

    @JsonProperty("code")
    private long code = 0;

    @JsonProperty("pid")
    private long pid = 0;

    @JsonProperty("args")
    private String[] args = null;

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public long getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public CommandDetails out(String out) {
        this.out = out;
        return this;
    }

    public CommandDetails err(String err) {
        this.err = err;
        return this;
    }

    public CommandDetails code(int code) {
        this.code = code;
        return this;
    }

    public CommandDetails pid(long pid) {
        this.pid = pid;
        return this;
    }

    public CommandDetails args(String[] args) {
        this.args = args;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    out: ").append(toIndentedString(out)).append("\n");
        sb.append("    err: ").append(toIndentedString(err)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    pid: ").append(toIndentedString(pid)).append("\n");
        sb.append("    args: ").append(toIndentedString(String.join(" ", args))).append("\n");
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

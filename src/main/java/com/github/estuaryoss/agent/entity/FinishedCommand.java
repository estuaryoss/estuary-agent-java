package com.github.estuaryoss.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinishedCommand {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "COMMAND")
    private String command;
    @Column(name = "CODE")
    private Long code;
    @Column(name = "OUT")
    private String out;
    @Column(name = "ERR")
    private String err;
    @Column(name = "STARTED_AT")
    private String startedAt;
    @Column(name = "FINISHED_AT")
    private String finishedAt;
    @Column(name = "DURATION")
    private Float duration;
    @Column(name = "PID")
    private Long pid;
}

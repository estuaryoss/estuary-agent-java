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
public class ActiveCommand {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "COMMAND")
    private String command;
    @Column(name = "STARTED_AT")
    private String startedAt;
    @Column(name = "PID")
    private Long pid;
}

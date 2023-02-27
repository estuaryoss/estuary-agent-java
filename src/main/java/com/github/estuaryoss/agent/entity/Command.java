package com.github.estuaryoss.agent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Command {
    @Id
    @GeneratedValue
    @Column(name = "ID") //DB
    private Long id;

    @Column(name = "COMMAND", length = COMMAND_MAX_SIZE)
    private String command;

    @Column(name = "CODE")
    private Long code;

    @Column(name = "OUT", length = COMMAND_STDOUT_MAX_SIZE)
    private String out;

    @Column(name = "ERR", length = COMMAND_STDERR_MAX_SIZE)
    private String err;

    @Column(name = "STARTED_AT")
    private String startedAt;

    @Column(name = "FINISHED_AT")
    private String finishedAt;

    @Column(name = "DURATION")
    private Float duration;

    @Column(name = "PID")
    private Long pid;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ARGS")
    @Length(max = COMMAND_MAX_SIZE)
    private String args;
}

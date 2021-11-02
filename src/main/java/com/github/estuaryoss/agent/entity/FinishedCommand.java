package com.github.estuaryoss.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.COMMAND_MAX_SIZE;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FIELD_MAX_SIZE;

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
    @Length(max = COMMAND_MAX_SIZE)
    private String command;

    @Column(name = "CODE")
    private Long code;

    @Column(name = "OUT")
    @Length(max = FIELD_MAX_SIZE)
    private String out;

    @Column(name = "ERR")
    @Length(max = FIELD_MAX_SIZE)
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

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
public class ActiveCommand {
    @Id
    @GeneratedValue
    @Column(name = "ID") //DB
    private Long id;

    @Column(name = "CID")
    @Length(max = FIELD_MAX_SIZE)
    private String commandId = "none";

    @Column(name = "COMMAND")
    @Length(max = COMMAND_MAX_SIZE)
    private String command;

    @Column(name = "STARTED_AT")
    private String startedAt;

    @Column(name = "PID")
    private Long pid;
}

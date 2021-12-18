package com.github.estuaryoss.agent.repository;

import com.github.estuaryoss.agent.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findCommandById(Long id);

    List<Command> findCommandByStatus(String status);

    void deleteByPid(long pid);
}

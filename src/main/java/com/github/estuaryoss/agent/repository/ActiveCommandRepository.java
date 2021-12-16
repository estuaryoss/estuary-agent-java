package com.github.estuaryoss.agent.repository;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveCommandRepository extends JpaRepository<ActiveCommand, Long> {
    ActiveCommand findActiveCommandByPid(Long pid);

    List<ActiveCommand> findActiveCommandById(Long id);

    void deleteByPid(Long pid);
}

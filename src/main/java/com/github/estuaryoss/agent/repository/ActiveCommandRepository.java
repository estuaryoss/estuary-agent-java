package com.github.estuaryoss.agent.repository;

import com.github.estuaryoss.agent.entity.ActiveCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveCommandRepository extends JpaRepository<ActiveCommand, Long> {
    ActiveCommand findActiveCommandByPid(Long pid);
}

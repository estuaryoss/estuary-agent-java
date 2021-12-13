package com.github.estuaryoss.agent.repository;

import com.github.estuaryoss.agent.entity.FinishedCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinishedCommandRepository extends JpaRepository<FinishedCommand, Long> {
    List<FinishedCommand> findFinishedCommandById(Long id);
}

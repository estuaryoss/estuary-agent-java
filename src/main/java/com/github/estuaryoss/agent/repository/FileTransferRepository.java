package com.github.estuaryoss.agent.repository;

import com.github.estuaryoss.agent.entity.FileTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTransferRepository extends JpaRepository<FileTransfer, Long> {
    FileTransfer findFileTransferById(Long pid);
}

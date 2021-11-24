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

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FILE_NAME_MAX_SIZE;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FILE_PATH_MAX_SIZE;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileTransfer {
    @Id
    @GeneratedValue
    @Column(name = "ID") //DB
    private Long id;

    @Column(name = "SOURCE_FILENAME")
    @Length(max = FILE_NAME_MAX_SIZE)
    private String sourceFileName;

    @Column(name = "TARGET_FILENAME")
    @Length(max = FILE_NAME_MAX_SIZE)
    private String targetFileName;

    @Column(name = "TARGET_FILEPATH")
    @Length(max = FILE_PATH_MAX_SIZE)
    private String targetFilePath;

    @Column(name = "TARGET_FOLDER")
    @Length(max = FILE_PATH_MAX_SIZE)
    private String targetFolder;

    @Column(name = "FILE_SIZE")
    private Long fileSize;
}

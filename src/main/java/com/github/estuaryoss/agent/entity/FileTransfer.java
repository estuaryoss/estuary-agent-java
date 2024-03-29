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

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SOURCE_FILENAME")
    @Length(max = FILE_NAME_MAX_SIZE)
    private String sourceFileName;

    @Column(name = "SOURCE_FILEPATH")
    @Length(max = FILE_PATH_MAX_SIZE)
    private String sourceFilePath;

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

    @Column(name = "DATE_TIME", columnDefinition = "TIMESTAMP")
    private String dateTime;
}

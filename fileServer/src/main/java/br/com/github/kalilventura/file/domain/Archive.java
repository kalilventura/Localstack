package br.com.github.kalilventura.file.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
public class Archive {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String extension;
    private String version;
    private long size;
    private long numberOfDownloads;
    private String eTag;

    @Column(name = "createdAt", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        setCreatedAt(LocalDate.now());
    }
}

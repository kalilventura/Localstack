package br.com.github.kalilventura.eventos.domain;

import lombok.Data;

@Data
public class File {
    private String id;
    private String name;
    private String extension;
    private String version;
    private String size;
    private String numberOfDownloads;
}

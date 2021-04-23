package br.com.github.kalilventura.file.repository;

import br.com.github.kalilventura.file.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Archive findArchiveByName(String name);
}
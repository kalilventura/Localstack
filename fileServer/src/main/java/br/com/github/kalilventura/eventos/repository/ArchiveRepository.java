package br.com.github.kalilventura.eventos.repository;

import br.com.github.kalilventura.eventos.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Archive findArchiveByName(String name);
}
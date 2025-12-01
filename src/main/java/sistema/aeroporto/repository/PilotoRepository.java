package sistema.aeroporto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.model.Piloto;

public interface PilotoRepository extends JpaRepository <Piloto, Long> {

}

package sistema.aeroporto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.model.CompanhiaAerea;

public interface CompanhiaAereaRepository extends JpaRepository<CompanhiaAerea, Long> {
    Optional<CompanhiaAerea> findByNome(String nome);

    boolean existsByNome(String nome);

    Optional<CompanhiaAerea> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}

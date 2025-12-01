package sistema.aeroporto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.model.Voo;

public interface VooRepository extends JpaRepository <Voo, Long> {
    
}

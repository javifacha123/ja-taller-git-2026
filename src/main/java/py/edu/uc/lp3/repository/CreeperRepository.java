package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Creeper;

/**
 * Acceso directo a los Creepers (misma tabla que la familia, filtrada por el
 * discriminador). Lo usan la simulacion y el endpoint de explosivos.
 */
public interface CreeperRepository extends JpaRepository<Creeper, Long> {
}

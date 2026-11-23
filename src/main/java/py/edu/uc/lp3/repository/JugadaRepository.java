package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Jugada;

import java.util.List;

/**
 * POO-09: jugadas del escenario que los dos compañeros aceptaron.
 * Solo se usa desde {@code EscenarioServiceImpl}.
 */
public interface JugadaRepository extends JpaRepository<Jugada, Long> {

    /** Historial de jugadas, la más reciente primero. */
    List<Jugada> findAllByOrderByMomentoDesc();
}

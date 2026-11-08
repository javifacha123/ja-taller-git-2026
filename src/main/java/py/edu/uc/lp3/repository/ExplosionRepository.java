package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Explosion;

import java.util.List;

public interface ExplosionRepository extends JpaRepository<Explosion, Long> {

    /** Historial de explosiones, la mas reciente primero. */
    List<Explosion> findAllByOrderByMomentoDesc();
}

package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.BloqueTnt;

import java.util.List;

public interface BloqueTntRepository extends JpaRepository<BloqueTnt, Long> {

    /** TNT que todavía no explotó. */
    List<BloqueTnt> findByDetonadoFalse();
}

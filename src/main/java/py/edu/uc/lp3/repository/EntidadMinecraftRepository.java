package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.EntidadMinecraft;

import java.util.List;

/**
 * Repositorio de la clase base de la familia. Es una interfaz: declaramos los
 * mensajes y Spring Data genera la implementación a partir del nombre del método.
 * Como trabaja con EntidadMinecraft, devuelve Creepers, Aldeanos y Endermans.
 */
public interface EntidadMinecraftRepository extends JpaRepository<EntidadMinecraft, Long> {

    /** Entidades con vida mayor a la indicada (con 0: las que siguen vivas). */
    List<EntidadMinecraft> findByVidaGreaterThan(int vida);

    /** Búsqueda por parte del nombre, sin importar mayúsculas. */
    List<EntidadMinecraft> findByNombreContainingIgnoreCase(String nombre);
}

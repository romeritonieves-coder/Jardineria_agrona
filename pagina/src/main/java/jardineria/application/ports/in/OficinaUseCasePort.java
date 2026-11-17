package jardineria.application.ports.in;

import jardineria.domain.model.Oficina;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Oficinas.
 */
public interface OficinaUseCasePort {
    Oficina registrarOficina(Oficina oficina);
    Oficina actualizarOficina(Oficina oficina);
    Optional<Oficina> obtenerOficinaPorId(String codigoOficina);
    List<Oficina> listarOficinas();
    void eliminarOficina(String codigoOficina);
}


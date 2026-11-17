package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.OficinaUseCasePort;
import jardineria.domain.model.Oficina;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.domain.ports.out.OficinaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para la gestión de Oficinas.
 */
@Slf4j
@RequiredArgsConstructor
public class OficinaService implements OficinaUseCasePort {

    private final OficinaRepositoryPort oficinaRepository;
    private final EmpleadoRepositoryPort empleadoRepository;

    @Override
    public Oficina registrarOficina(Oficina oficina) {
        log.info("Iniciando registro de oficina con código: {}", oficina.getCodigoOficina());

        if (oficina.getCodigoOficina() == null || oficina.getCodigoOficina().trim().isEmpty()) {
            throw new BusinessRuleException("El código de la oficina es obligatorio.");
        }

        if (oficinaRepository.findById(oficina.getCodigoOficina()).isPresent()) {
            throw new BusinessRuleException("Ya existe una oficina registrada con el código: " + oficina.getCodigoOficina());
        }

        validarDatosOficina(oficina);

        Oficina guardada = oficinaRepository.save(oficina);
        log.info("Oficina guardada exitosamente: {}", guardada.getCodigoOficina());
        return guardada;
    }

    @Override
    public Oficina actualizarOficina(Oficina oficina) {
        log.info("Iniciando actualización de oficina: {}", oficina.getCodigoOficina());

        if (oficina.getCodigoOficina() == null || oficina.getCodigoOficina().trim().isEmpty()) {
            throw new BusinessRuleException("El código de la oficina es necesario para actualizar.");
        }

        oficinaRepository.findById(oficina.getCodigoOficina())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la oficina con código: " + oficina.getCodigoOficina()));

        validarDatosOficina(oficina);

        Oficina actualizada = oficinaRepository.update(oficina);
        log.info("Oficina actualizada exitosamente: {}", actualizada.getCodigoOficina());
        return actualizada;
    }

    @Override
    public Optional<Oficina> obtenerOficinaPorId(String codigoOficina) {
        return oficinaRepository.findById(codigoOficina);
    }

    @Override
    public List<Oficina> listarOficinas() {
        return oficinaRepository.findAll();
    }

    @Override
    public void eliminarOficina(String codigoOficina) {
        log.info("Solicitud para eliminar la oficina con código: {}", codigoOficina);

        oficinaRepository.findById(codigoOficina)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Oficina no encontrada: " + codigoOficina));

        boolean tieneEmpleados = empleadoRepository.findAll().stream()
                .anyMatch(e -> e.getOficina() != null && codigoOficina.equalsIgnoreCase(e.getOficina().getCodigoOficina()));

        if (tieneEmpleados) {
            throw new BusinessRuleException("No se puede eliminar la oficina '" + codigoOficina + "' porque tiene empleados asociados.");
        }

        oficinaRepository.deleteById(codigoOficina);
        log.info("Oficina con código {} eliminada exitosamente.", codigoOficina);
    }

    private void validarDatosOficina(Oficina oficina) {
        if (oficina.getCiudad() == null || oficina.getCiudad().trim().isEmpty()) {
            throw new BusinessRuleException("La ciudad de la oficina es obligatoria.");
        }
        if (oficina.getPais() == null || oficina.getPais().trim().isEmpty()) {
            throw new BusinessRuleException("El país de la oficina es obligatorio.");
        }
        if (oficina.getTelefono() == null || oficina.getTelefono().trim().isEmpty()) {
            throw new BusinessRuleException("El teléfono de la oficina es obligatorio.");
        }
        if (oficina.getLineaDireccion1() == null || oficina.getLineaDireccion1().trim().isEmpty()) {
            throw new BusinessRuleException("La línea de dirección 1 es obligatoria.");
        }
    }
}


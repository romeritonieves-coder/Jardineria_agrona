package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.GamaProductoUseCasePort;
import jardineria.domain.model.GamaProducto;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para las Gamas de Producto.
 */
@Slf4j
@RequiredArgsConstructor
public class GamaProductoService implements GamaProductoUseCasePort {

    private final GamaProductoRepositoryPort gamaProductoRepository;
    private final ProductoRepositoryPort productoRepository;

    @Override
    public GamaProducto registrarGama(GamaProducto gama) {
        log.info("Iniciando registro de gama de producto: {}", gama.getGama());

        if (gama.getGama() == null || gama.getGama().trim().isEmpty()) {
            throw new BusinessRuleException("El identificador de la gama es obligatorio.");
        }

        if (gamaProductoRepository.findById(gama.getGama()).isPresent()) {
            throw new BusinessRuleException("Ya existe una gama de producto con el nombre: " + gama.getGama());
        }

        GamaProducto guardada = gamaProductoRepository.save(gama);
        log.info("Gama de producto registrada con éxito: {}", guardada.getGama());
        return guardada;
    }

    @Override
    public GamaProducto actualizarGama(GamaProducto gama) {
        log.info("Iniciando actualización de gama de producto: {}", gama.getGama());

        if (gama.getGama() == null || gama.getGama().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre de la gama es obligatorio para actualizar.");
        }

        gamaProductoRepository.findById(gama.getGama())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la gama: " + gama.getGama()));

        GamaProducto actualizada = gamaProductoRepository.update(gama);
        log.info("Gama de producto actualizada: {}", actualizada.getGama());
        return actualizada;
    }

    @Override
    public Optional<GamaProducto> obtenerGamaPorId(String gama) {
        return gamaProductoRepository.findById(gama);
    }

    @Override
    public List<GamaProducto> listarGamas() {
        return gamaProductoRepository.findAll();
    }

    @Override
    public void eliminarGama(String gama) {
        log.info("Solicitud para eliminar la gama: {}", gama);

        gamaProductoRepository.findById(gama)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Gama no encontrada: " + gama));

        boolean tieneProductos = productoRepository.findAll().stream()
                .anyMatch(p -> p.getGama() != null && gama.equalsIgnoreCase(p.getGama().getGama()));

        if (tieneProductos) {
            throw new BusinessRuleException("No se puede eliminar la gama '" + gama + "' porque tiene productos asociados.");
        }

        gamaProductoRepository.deleteById(gama);
        log.info("Gama de producto '{}' eliminada con éxito.", gama);
    }
}


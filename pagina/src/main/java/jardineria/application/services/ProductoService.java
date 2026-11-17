package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para la gestión de Productos.
 */
@Slf4j
@RequiredArgsConstructor
public class ProductoService implements ProductoUseCasePort {

    private final ProductoRepositoryPort productoRepository;
    private final GamaProductoRepositoryPort gamaProductoRepository;
    private final DetallePedidoRepositoryPort detallePedidoRepository;

    @Override
    public Producto registrarProducto(Producto producto) {
        log.info("Iniciando registro de producto con código: {}", producto.getCodigoProducto());

        if (producto.getCodigoProducto() == null || producto.getCodigoProducto().trim().isEmpty()) {
            throw new BusinessRuleException("El código del producto es obligatorio.");
        }

        if (productoRepository.findById(producto.getCodigoProducto()).isPresent()) {
            throw new BusinessRuleException("Ya existe un producto con el código: " + producto.getCodigoProducto());
        }

        validarDatosProducto(producto);
        validarGama(producto);

        Producto guardado = productoRepository.save(producto);
        log.info("Producto registrado exitosamente: {}", guardado.getCodigoProducto());
        return guardado;
    }

    @Override
    public Producto actualizarProducto(Producto producto) {
        log.info("Iniciando actualización para el producto: {}", producto.getCodigoProducto());

        if (producto.getCodigoProducto() == null || producto.getCodigoProducto().trim().isEmpty()) {
            throw new BusinessRuleException("El código del producto es obligatorio para actualizar.");
        }

        productoRepository.findById(producto.getCodigoProducto())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con código: " + producto.getCodigoProducto()));

        validarDatosProducto(producto);
        validarGama(producto);

        Producto actualizado = productoRepository.update(producto);
        log.info("Producto actualizado exitosamente: {}", actualizado.getCodigoProducto());
        return actualizado;
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(String codigoProducto) {
        return productoRepository.findById(codigoProducto);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> listarProductosPorGama(String gama) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getGama() != null && gama.equalsIgnoreCase(p.getGama().getGama()))
                .toList();
    }

    @Override
    public Producto actualizarStock(String codigoProducto, int nuevoStock) {
        log.info("Actualizando stock para producto '{}' a {} unidades", codigoProducto, nuevoStock);

        if (nuevoStock < 0) {
            throw new BusinessRuleException("El stock no puede ser un valor negativo.");
        }

        Producto producto = productoRepository.findById(codigoProducto)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con código: " + codigoProducto));

        producto.setCantidadEnStock(nuevoStock);
        return productoRepository.update(producto);
    }

    @Override
    public void eliminarProducto(String codigoProducto) {
        log.info("Solicitud para eliminar el producto con código: {}", codigoProducto);

        productoRepository.findById(codigoProducto)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Producto no encontrado: " + codigoProducto));

        boolean tienePedidos = detallePedidoRepository.findAll().stream()
                .anyMatch(d -> d.getProducto() != null && codigoProducto.equalsIgnoreCase(d.getProducto().getCodigoProducto()));

        if (tienePedidos) {
            throw new BusinessRuleException("No se puede eliminar el producto '" + codigoProducto + "' porque figura en pedidos existentes.");
        }

        productoRepository.deleteById(codigoProducto);
        log.info("Producto con código '{}' eliminado con éxito.", codigoProducto);
    }

    private void validarDatosProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre del producto es obligatorio.");
        }
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El precio de venta debe ser mayor a cero.");
        }
        if (producto.getPrecioProveedor() != null && producto.getPrecioProveedor().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("El precio del proveedor no puede ser negativo.");
        }
        if (producto.getCantidadEnStock() != null && producto.getCantidadEnStock() < 0) {
            throw new BusinessRuleException("La cantidad en stock no puede ser negativa.");
        }
    }

    private void validarGama(Producto producto) {
        if (producto.getGama() == null || producto.getGama().getGama() == null) {
            throw new BusinessRuleException("El producto debe tener una gama asignada.");
        }
        String codGama = producto.getGama().getGama();
        gamaProductoRepository.findById(codGama)
                .orElseThrow(() -> new ResourceNotFoundException("La gama '" + codGama + "' no existe en el catálogo."));
    }
}


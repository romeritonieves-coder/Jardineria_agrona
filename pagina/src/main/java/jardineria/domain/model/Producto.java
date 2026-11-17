package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad de dominio que representa un Producto del catálogo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    private String codigoProducto;
    private String nombre;
    private GamaProducto gama;
    private String dimensiones;
    private String proveedor;
    private String descripcion;
    private Integer cantidadEnStock;
    private BigDecimal precioVenta;
    private BigDecimal precioProveedor;
}


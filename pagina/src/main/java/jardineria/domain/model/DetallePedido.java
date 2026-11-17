package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad de dominio que representa una línea de detalle dentro de un Pedido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {
    private Pedido pedido;
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precioUnidad;
    private Integer numeroLinea;
}


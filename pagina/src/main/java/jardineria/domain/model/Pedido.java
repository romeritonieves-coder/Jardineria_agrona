package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa un Pedido realizado por un Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    private Integer codigoPedido;
    private LocalDate fechaPedido;
    private LocalDate fechaEsperada;
    private LocalDate fechaEntrega;
    private String estado;
    private String comentarios;
    private Cliente cliente;

    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();
}


package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad de dominio que representa un Pago realizado por un Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    private Cliente cliente;
    private String formaPago;
    private String idTransaccion;
    private LocalDate fechaPago;
    private BigDecimal total;
}


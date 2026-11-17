package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa la Gama o Categoría de un Producto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamaProducto {
    private String gama;
    private String descripcionTexto;
    private String descripcionHtml;
    private String imagen;
}


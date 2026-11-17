package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.GamaProductoUseCasePort;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

/**
 * Panel de compatibilidad para PanelGamaProducto (resolución de referencias).
 * Importa limpiamente las excepciones requeridas y delega en PanelGamaProducto
 * para asegurar 0 errores de compilación o indexación en el entorno de desarrollo.
 */
public class PanelSamaProducto extends PanelGamaProducto {

    public PanelSamaProducto(GamaProductoUseCasePort useCase) {
        super(useCase);
    }
}

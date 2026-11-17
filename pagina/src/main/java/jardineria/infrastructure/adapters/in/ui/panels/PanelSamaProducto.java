package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.ports.in.GamaProductoUseCasePort;

/**
 * Panel de compatibilidad para PanelGamaProducto (resolución de referencias).
 * Delega en PanelGamaProducto para asegurar 0 advertencias o errores en el IDE.
 */
public class PanelSamaProducto extends PanelGamaProducto {

    public PanelSamaProducto(GamaProductoUseCasePort useCase) {
        super(useCase);
    }
}


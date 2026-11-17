package jardineria;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.application.services.ClienteService;
import jardineria.application.services.ProductoService;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.in.ui.panels.PanelCliente;
import jardineria.infrastructure.adapters.in.ui.panels.PanelProducto;
import jardineria.infrastructure.adapters.out.persistence.ClienteRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.DetallePedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.EmpleadoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.GamaProductoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.ProductoRepositoryJdbc;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Jardinería Agrona.
 * Actúa como Composition Root para la Inyección de Dependencias.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar Look & Feel FlatDarkLaf
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // 1. Adaptadores de salida (Infraestructura / Persistencia JDBC)
            ClienteRepositoryPort clienteRepo = new ClienteRepositoryJdbc();
            EmpleadoRepositoryPort empleadoRepo = new EmpleadoRepositoryJdbc();
            ProductoRepositoryPort productoRepo = new ProductoRepositoryJdbc();
            GamaProductoRepositoryPort gamaRepo = new GamaProductoRepositoryJdbc();
            DetallePedidoRepositoryPort detallePedidoRepo = new DetallePedidoRepositoryJdbc();

            // 2. Capa de Aplicación (Servicios que implementan Puertos de Entrada)
            ClienteUseCasePort clienteService = new ClienteService(clienteRepo, empleadoRepo);
            ProductoUseCasePort productoService = new ProductoService(productoRepo, gamaRepo, detallePedidoRepo);

            // 3. Adaptadores de entrada visual (Paneles Swing)
            PanelCliente panelCliente = new PanelCliente(clienteService);
            PanelProducto panelProducto = new PanelProducto(productoService);

            // 4. Ventana Principal (Dashboard) y registro de pantallas en CardLayout
            DashboardFrame frame = new DashboardFrame();
            frame.registrarPantalla(panelCliente, DashboardFrame.CARD_CLIENTES);
            frame.registrarPantalla(panelProducto, DashboardFrame.CARD_PRODUCTOS);

            frame.setVisible(true);
        });
    }
}

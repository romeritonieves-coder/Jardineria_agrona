package jardineria;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.application.services.ClienteService;
import jardineria.application.services.PedidoService;
import jardineria.application.services.ProductoService;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import jardineria.domain.ports.out.PedidoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.in.ui.panels.PanelCliente;
import jardineria.infrastructure.adapters.in.ui.panels.PanelPedido;
import jardineria.infrastructure.adapters.in.ui.panels.PanelProducto;
import jardineria.infrastructure.adapters.out.persistence.ClienteRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.DetallePedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.EmpleadoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.GamaProductoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.PedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.ProductoRepositoryJdbc;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Jardinería Agrona.
 * Actúa como Composition Root para la Inyección de Dependencias.
 */
public class Main {
    public static void main(String[] args) {
        // Desactivar aceleración por hardware defectuosa en Windows (elimina 100% el ghosting en DirectX/D3D)
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");

        // Configurar Look & Feel FlatDarkLaf
        FlatDarkLaf.setup();

        // Configuración global para evitar desgarro visual (ghosting) en popups y menús
        UIManager.put("PopupMenu.dropShadowPainted", false);
        UIManager.put("PopupMenu.opaque", true);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("Menu.opaque", true);
        UIManager.put("MenuBar.opaque", true);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        SwingUtilities.invokeLater(() -> {
            // 1. Adaptadores de salida (Infraestructura / Persistencia JDBC)
            ClienteRepositoryPort clienteRepo = new ClienteRepositoryJdbc();
            EmpleadoRepositoryPort empleadoRepo = new EmpleadoRepositoryJdbc();
            ProductoRepositoryPort productoRepo = new ProductoRepositoryJdbc();
            GamaProductoRepositoryPort gamaRepo = new GamaProductoRepositoryJdbc();
            DetallePedidoRepositoryPort detallePedidoRepo = new DetallePedidoRepositoryJdbc();
            PedidoRepositoryPort pedidoRepo = new PedidoRepositoryJdbc();

            // 2. Capa de Aplicación (Servicios que implementan Puertos de Entrada)
            ClienteUseCasePort clienteService = new ClienteService(clienteRepo, empleadoRepo);
            ProductoUseCasePort productoService = new ProductoService(productoRepo, gamaRepo, detallePedidoRepo);
            PedidoUseCasePort pedidoService = new PedidoService(pedidoRepo, detallePedidoRepo, clienteRepo, productoRepo);

            // 3. Adaptadores de entrada visual (Paneles Swing)
            PanelCliente panelCliente = new PanelCliente(clienteService);
            PanelProducto panelProducto = new PanelProducto(productoService);
            PanelPedido panelPedido = new PanelPedido(pedidoService);

            // 4. Ventana Principal (Dashboard) y registro de pantallas en CardLayout
            DashboardFrame frame = new DashboardFrame(clienteService, productoService, pedidoService);
            frame.registrarPantalla(panelCliente, DashboardFrame.CARD_CLIENTES);
            frame.registrarPantalla(panelProducto, DashboardFrame.CARD_PRODUCTOS);
            frame.registrarPantalla(panelPedido, DashboardFrame.CARD_PEDIDOS);

            frame.setVisible(true);
        });
    }
}

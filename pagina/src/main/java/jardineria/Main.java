package jardineria;

import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.EmpleadoUseCasePort;
import jardineria.application.ports.in.OficinaUseCasePort;
import jardineria.application.ports.in.PagoUseCasePort;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.application.ports.in.ReporteUseCasePort;
import jardineria.application.services.ClienteService;
import jardineria.application.services.EmpleadoService;
import jardineria.application.services.OficinaService;
import jardineria.application.services.PagoService;
import jardineria.application.services.PedidoService;
import jardineria.application.services.ProductoService;
import jardineria.application.services.ReporteService;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import jardineria.domain.ports.out.OficinaRepositoryPort;
import jardineria.domain.ports.out.PagoRepositoryPort;
import jardineria.domain.ports.out.PedidoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import jardineria.domain.ports.out.ReporteExportPort;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.in.ui.panels.PanelCliente;
import jardineria.infrastructure.adapters.in.ui.panels.PanelEmpleado;
import jardineria.infrastructure.adapters.in.ui.panels.PanelOficina;
import jardineria.infrastructure.adapters.in.ui.panels.PanelPago;
import jardineria.infrastructure.adapters.in.ui.panels.PanelPedido;
import jardineria.infrastructure.adapters.in.ui.panels.PanelProducto;
import jardineria.infrastructure.adapters.out.export.ReporteExcelAdapter;
import jardineria.infrastructure.adapters.out.persistence.ClienteRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.DetallePedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.EmpleadoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.GamaProductoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.OficinaRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.PagoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.PedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.ProductoRepositoryJdbc;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Jardinería Agrona.
 * Actúa como Composition Root para la Inyección de Dependencias bajo Arquitectura Hexagonal.
 */
public class Main {
    public static void main(String[] args) {
        // Desactivar aceleración por hardware defectuosa en Windows (elimina 100% el ghosting en DirectX/D3D)
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");

        // Configurar tema e identidad visual "AGRONA Jardinería" (Dark Emerald & Lime)
        DashboardFrame.configurarTemaAgrona();

        SwingUtilities.invokeLater(() -> {
            // 1. Adaptadores de salida (Infraestructura / Persistencia JDBC & Exportación)
            ClienteRepositoryPort clienteRepo = new ClienteRepositoryJdbc();
            EmpleadoRepositoryPort empleadoRepo = new EmpleadoRepositoryJdbc();
            OficinaRepositoryPort oficinaRepo = new OficinaRepositoryJdbc();
            PagoRepositoryPort pagoRepo = new PagoRepositoryJdbc();
            ProductoRepositoryPort productoRepo = new ProductoRepositoryJdbc();
            GamaProductoRepositoryPort gamaRepo = new GamaProductoRepositoryJdbc();
            DetallePedidoRepositoryPort detallePedidoRepo = new DetallePedidoRepositoryJdbc();
            PedidoRepositoryPort pedidoRepo = new PedidoRepositoryJdbc();
            ReporteExportPort reporteExportAdapter = new ReporteExcelAdapter();

            // 2. Capa de Aplicación (Servicios que implementan Puertos de Entrada)
            ClienteUseCasePort clienteService = new ClienteService(clienteRepo, empleadoRepo);
            OficinaUseCasePort oficinaService = new OficinaService(oficinaRepo, empleadoRepo);
            EmpleadoUseCasePort empleadoService = new EmpleadoService(empleadoRepo, oficinaRepo, clienteRepo);
            PagoUseCasePort pagoService = new PagoService(pagoRepo, clienteRepo);
            ProductoUseCasePort productoService = new ProductoService(productoRepo, gamaRepo, detallePedidoRepo);
            PedidoUseCasePort pedidoService = new PedidoService(pedidoRepo, detallePedidoRepo, clienteRepo, productoRepo);
            ReporteUseCasePort reporteService = new ReporteService(clienteRepo, productoRepo, pedidoRepo, reporteExportAdapter);

            // 3. Adaptadores de entrada visual (Paneles Swing con inyección de servicios)
            PanelCliente panelCliente = new PanelCliente(clienteService, reporteService);
            PanelProducto panelProducto = new PanelProducto(productoService, reporteService);
            PanelPedido panelPedido = new PanelPedido(pedidoService, reporteService);
            PanelOficina panelOficina = new PanelOficina(oficinaService);
            PanelEmpleado panelEmpleado = new PanelEmpleado(empleadoService, oficinaService);
            PanelPago panelPago = new PanelPago(pagoService, clienteService);

            // 4. Ventana Principal (Dashboard) y registro de pantallas en CardLayout
            DashboardFrame frame = new DashboardFrame(
                    clienteService,
                    productoService,
                    pedidoService,
                    oficinaService,
                    empleadoService,
                    pagoService
            );

            // Registrar todos los módulos en el sistema de navegación por tarjetas
            frame.registrarPantalla(panelCliente, DashboardFrame.CARD_CLIENTES);
            frame.registrarPantalla(panelProducto, DashboardFrame.CARD_PRODUCTOS);
            frame.registrarPantalla(panelPedido, DashboardFrame.CARD_PEDIDOS);
            frame.registrarPantalla(panelPago, DashboardFrame.CARD_PAGOS);
            frame.registrarPantalla(panelEmpleado, DashboardFrame.CARD_EMPLEADOS);
            frame.registrarPantalla(panelOficina, DashboardFrame.CARD_OFICINAS);

            frame.setVisible(true);
        });
    }
}

package jardineria;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.services.ClienteService;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.in.ui.panels.PanelCliente;
import jardineria.infrastructure.adapters.out.persistence.ClienteRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.EmpleadoRepositoryJdbc;

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

            // 2. Capa de Aplicación (Servicios que implementan Puertos de Entrada)
            ClienteUseCasePort clienteService = new ClienteService(clienteRepo, empleadoRepo);

            // 3. Adaptadores de entrada visual (Paneles Swing)
            PanelCliente panelCliente = new PanelCliente(clienteService);

            // 4. Ventana Principal (Dashboard)
            DashboardFrame frame = new DashboardFrame();
            frame.registrarPantalla(panelCliente, DashboardFrame.CARD_CLIENTES);

            frame.setVisible(true);
        });
    }
}

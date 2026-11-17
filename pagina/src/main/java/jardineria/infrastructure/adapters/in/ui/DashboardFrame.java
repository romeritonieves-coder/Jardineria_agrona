package jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Ventana principal del sistema (Dashboard) para Jardinería Agrona.
 * Adaptador de entrada primario en la Arquitectura Hexagonal.
 */
public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JLabel statusLabel;
    private final java.util.Map<String, Component> registeredCards = new java.util.HashMap<>();

    private ClienteUseCasePort clienteUseCase;
    private ProductoUseCasePort productoUseCase;
    private PedidoUseCasePort pedidoUseCase;

    private JLabel lblTotalClientes;
    private JLabel lblTotalProductos;
    private JLabel lblTotalPedidos;

    public static final String CARD_HOME = "CARD_HOME";
    public static final String CARD_CLIENTES = "CARD_CLIENTES";
    public static final String CARD_PEDIDOS = "CARD_PEDIDOS";
    public static final String CARD_PRODUCTOS = "CARD_PRODUCTOS";
    public static final String CARD_EMPLEADOS = "CARD_EMPLEADOS";
    public static final String CARD_OFICINAS = "CARD_OFICINAS";
    public static final String CARD_GAMAS = "CARD_GAMAS";
    public static final String CARD_PAGOS = "CARD_PAGOS";

    public DashboardFrame() {
        this(null, null, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.productoUseCase = productoUseCase;
        this.pedidoUseCase = pedidoUseCase;

        // Forzar opacidad en popups y menús de Swing para evitar desgarro visual (ghosting)
        UIManager.put("PopupMenu.dropShadowPainted", false);
        UIManager.put("PopupMenu.opaque", true);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("Menu.opaque", true);
        UIManager.put("MenuBar.opaque", true);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        setTitle("Jardinería Agrona - Sistema de Gestión Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Inicializar MenuBar
        setJMenuBar(createMenuBar());

        // Layout principal
        setLayout(new BorderLayout());
        this.getRootPane().setOpaque(true);
        this.getRootPane().setBackground(UIManager.getColor("Panel.background"));
        ((JComponent) this.getContentPane()).setOpaque(true);
        ((JComponent) this.getContentPane()).setBackground(UIManager.getColor("Panel.background"));

        // Barra de herramientas superior / navegación rápida
        add(createToolBar(), BorderLayout.NORTH);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(UIManager.getColor("Panel.background"));

        // Añadir tarjeta de bienvenida inicial con Dashboard de métricas
        registrarPantalla(createHomePanel(), CARD_HOME);

        // Registrar paneles de marcador de posición (Placeholders) para módulos pendientes
        registrarPantalla(createPlaceholderPanel("Gestión de Pagos"), CARD_PAGOS);
        registrarPantalla(createPlaceholderPanel("Gestión de Empleados"), CARD_EMPLEADOS);
        registrarPantalla(createPlaceholderPanel("Gestión de Oficinas"), CARD_OFICINAS);
        registrarPantalla(createPlaceholderPanel("Gamas de Productos"), CARD_GAMAS);

        add(contentPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        statusLabel = new JLabel(" Sistema Listo | Conexión: MySQL Docker | Jardinería Agrona v1.0");
        statusLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
        add(statusLabel, BorderLayout.SOUTH);

        // Asegurar opacidad recursiva de todos los componentes
        hacerComponentesOpacos(this);

        // Mostrar pantalla de inicio y cargar KPIs
        mostrarPantalla(CARD_HOME);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        Color panelBg = UIManager.getColor("Panel.background");
        menuBar.setBackground(panelBg);

        // Menú Sistema / Archivo
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemInicio = new JMenuItem("Inicio / Dashboard");
        itemInicio.addActionListener(e -> mostrarPantalla(CARD_HOME));
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuSistema.add(itemInicio);
        menuSistema.addSeparator();
        menuSistema.add(itemSalir);

        // Menú Gestión Comercial
        JMenu menuComercial = new JMenu("Comercial");
        JMenuItem itemClientes = new JMenuItem("Gestión de Clientes");
        itemClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));
        JMenuItem itemPedidos = new JMenuItem("Gestión de Pedidos");
        itemPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));
        JMenuItem itemPagos = new JMenuItem("Gestión de Pagos");
        itemPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));
        menuComercial.add(itemClientes);
        menuComercial.add(itemPedidos);
        menuComercial.add(itemPagos);

        // Menú Catálogo e Inventario
        JMenu menuInventario = new JMenu("Catálogo");
        JMenuItem itemProductos = new JMenuItem("Productos");
        itemProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));
        JMenuItem itemGamas = new JMenuItem("Gamas de Producto");
        itemGamas.addActionListener(e -> mostrarPantalla(CARD_GAMAS));
        menuInventario.add(itemProductos);
        menuInventario.add(itemGamas);

        // Menú Organización
        JMenu menuOrganizacion = new JMenu("Organización");
        JMenuItem itemEmpleados = new JMenuItem("Empleados");
        itemEmpleados.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));
        JMenuItem itemOficinas = new JMenuItem("Oficinas");
        itemOficinas.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));
        menuOrganizacion.add(itemEmpleados);
        menuOrganizacion.add(itemOficinas);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Jardinería Agrona\nArquitectura Hexagonal & SOLID\nJava 17 + Swing FlatLaf + MySQL",
                "Acerca de Jardinería Agrona",
                JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuSistema);
        menuBar.add(menuComercial);
        menuBar.add(menuInventario);
        menuBar.add(menuOrganizacion);
        menuBar.add(menuAyuda);

        // Asegurar opacidad sólida en cada menú, submenú y popup desplegable
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                configurarMenuOpaco(menu, panelBg);
            }
        }

        return menuBar;
    }

    private void configurarMenuOpaco(JMenu menu, Color bg) {
        menu.setOpaque(true);
        menu.setBackground(bg);
        if (menu.getPopupMenu() != null) {
            menu.getPopupMenu().setLightWeightPopupEnabled(false);
            menu.getPopupMenu().setOpaque(true);
            menu.getPopupMenu().setBackground(bg);
        }
        for (Component c : menu.getMenuComponents()) {
            if (c instanceof JMenuItem item) {
                item.setOpaque(true);
                item.setBackground(bg);
                if (item instanceof JMenu subMenu) {
                    configurarMenuOpaco(subMenu, bg);
                }
            } else if (c instanceof JComponent jcomp) {
                jcomp.setOpaque(true);
                jcomp.setBackground(bg);
            }
        }
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton btnHome = new JButton(" Inicio");
        btnHome.addActionListener(e -> mostrarPantalla(CARD_HOME));

        JButton btnClientes = new JButton(" Clientes");
        btnClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnPedidos = new JButton(" Pedidos");
        btnPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        JButton btnProductos = new JButton(" Productos");
        btnProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnPagos = new JButton(" Pagos");
        btnPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));

        JButton btnEmpleados = new JButton(" Empleados");
        btnEmpleados.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));

        JButton btnOficinas = new JButton(" Oficinas");
        btnOficinas.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));

        toolBar.add(btnHome);
        toolBar.addSeparator();
        toolBar.add(btnClientes);
        toolBar.add(btnPedidos);
        toolBar.add(btnProductos);
        toolBar.add(btnPagos);
        toolBar.addSeparator();
        toolBar.add(btnEmpleados);
        toolBar.add(btnOficinas);

        return toolBar;
    }

    public void setServicios(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.productoUseCase = productoUseCase;
        this.pedidoUseCase = pedidoUseCase;
        actualizarEstadisticasDashboard();
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setOpaque(true);
        panel.setBackground(UIManager.getColor("Panel.background"));
        panel.setBorder(new EmptyBorder(35, 45, 35, 45));

        // 1. Cabecera superior de bienvenida
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Panel de Control & Indicadores Clave (KPIs)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Métricas del negocio en tiempo real consultadas desde MySQL mediante Arquitectura Hexagonal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // 2. Panel central con las 3 tarjetas de KPIs
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 25, 25));
        kpiGrid.setOpaque(false);

        lblTotalClientes = new JLabel("0", SwingConstants.CENTER);
        lblTotalProductos = new JLabel("0", SwingConstants.CENTER);
        lblTotalPedidos = new JLabel("0", SwingConstants.CENTER);

        JPanel cardClientes = crearTarjetaKpi("TOTAL DE CLIENTES", lblTotalClientes, "Cartera activa registrada en el sistema", new Color(76, 175, 80));
        JPanel cardProductos = crearTarjetaKpi("TOTAL DE PRODUCTOS", lblTotalProductos, "Variedad de artículos en catálogo/inventario", new Color(66, 165, 245));
        JPanel cardPedidos = crearTarjetaKpi("TOTAL DE PEDIDOS", lblTotalPedidos, "Órdenes de compra y despachos registrados", new Color(255, 138, 101));

        kpiGrid.add(cardClientes);
        kpiGrid.add(cardProductos);
        kpiGrid.add(cardPedidos);

        // Contenedor medio para centrar la grilla y las acciones
        JPanel centerContainer = new JPanel(new BorderLayout(20, 20));
        centerContainer.setOpaque(false);
        centerContainer.add(kpiGrid, BorderLayout.NORTH);

        // 3. Barra de Acciones y Botón Actualizar Métricas
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setOpaque(false);

        JButton btnRefrescar = new JButton("Actualizar Métricas");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefrescar.addActionListener(e -> actualizarEstadisticasDashboard());

        JButton btnIrClientes = new JButton("Gestionar Clientes");
        btnIrClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnIrProductos = new JButton("Ver Productos");
        btnIrProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnIrPedidos = new JButton("Nuevo Pedido");
        btnIrPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        bottomBar.add(btnIrClientes);
        bottomBar.add(btnIrProductos);
        bottomBar.add(btnIrPedidos);
        bottomBar.add(btnRefrescar);

        centerContainer.add(bottomBar, BorderLayout.CENTER);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel valorLabel, String pie, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(true);
        Color cardBg = UIManager.getColor("Panel.background");
        if (cardBg != null) {
            // Fondo con contraste sutil sobre el tema oscuro de FlatLaf
            card.setBackground(new Color(
                    Math.min(255, cardBg.getRed() + 10),
                    Math.min(255, cardBg.getGreen() + 10),
                    Math.min(255, cardBg.getBlue() + 10)));
        }
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(65, 70, 75), 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        // Título del indicador con acento de color
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(colorAcento);

        // Valor cuantitativo principal
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));

        // Subtexto descriptivo al pie
        JLabel lblPie = new JLabel(pie, SwingConstants.CENTER);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPie.setForeground(new Color(175, 180, 185));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        card.add(lblPie, BorderLayout.SOUTH);

        return card;
    }

    public void actualizarEstadisticasDashboard() {
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int clientes = 0;
                int productos = 0;
                int pedidos = 0;
                try {
                    if (clienteUseCase != null) {
                        clientes = clienteUseCase.obtenerTodosLosClientes().size();
                    }
                    if (productoUseCase != null) {
                        productos = productoUseCase.obtenerTodosLosProductos().size();
                    }
                    if (pedidoUseCase != null) {
                        pedidos = pedidoUseCase.obtenerTodosLosPedidos().size();
                    }
                } catch (Exception ex) {
                    System.err.println("Advertencia al calcular estadísticas del dashboard: " + ex.getMessage());
                }
                return new int[]{clientes, productos, pedidos};
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    if (lblTotalClientes != null) lblTotalClientes.setText(String.valueOf(stats[0]));
                    if (lblTotalProductos != null) lblTotalProductos.setText(String.valueOf(stats[1]));
                    if (lblTotalPedidos != null) lblTotalPedidos.setText(String.valueOf(stats[2]));
                    actualizarBarraEstado("Métricas sincronizadas: " + stats[0] + " clientes | " + stats[1] + " productos | " + stats[2] + " pedidos");
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private JPanel createPlaceholderPanel(String tituloModulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(UIManager.getColor("Panel.background"));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel(tituloModulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Módulo en construcción");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.GRAY);
        panel.add(subtitleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel infoLabel = new JLabel("<html><center>Esta sección estará disponible en la próxima versión.<br>Actualmente los módulos funcionales son <b>Clientes</b>, <b>Productos</b> y <b>Pedidos</b>.</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        panel.add(infoLabel, gbc);

        return panel;
    }

    /**
     * Registra o actualiza una vista dentro del CardLayout del contenedor principal.
     */
    public void registrarPantalla(JPanel panel, String cardName) {
        hacerComponentesOpacos(panel);
        Component old = registeredCards.get(cardName);
        if (old != null) {
            contentPanel.remove(old);
        }
        registeredCards.put(cardName, panel);
        contentPanel.add(panel, cardName);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Método estático y recursivo que aplica opacidad y color de fondo sólido
     * a todos los contenedores anidados (JPanel, JScrollPane, JViewport, JSplitPane, JTabbedPane)
     * para eliminar de raíz el desgarro de pantalla o 'ghosting' en Swing.
     */
    public static void hacerComponentesOpacos(Container parent) {
        if (parent == null) return;

        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg == null) {
            panelBg = new Color(30, 31, 34);
        }

        if (parent instanceof JPanel || parent instanceof JScrollPane || parent instanceof JViewport ||
            parent instanceof JSplitPane || parent instanceof JTabbedPane) {
            if (parent instanceof JComponent jcomp) {
                jcomp.setOpaque(true);
            }
            parent.setBackground(panelBg);
        }

        if (parent instanceof JScrollPane scrollPane) {
            if (scrollPane.getViewport() != null) {
                scrollPane.getViewport().setOpaque(true);
                scrollPane.getViewport().setBackground(panelBg);
            }
        }

        for (Component child : parent.getComponents()) {
            if (child instanceof JPanel || child instanceof JScrollPane || child instanceof JViewport ||
                child instanceof JSplitPane || child instanceof JTabbedPane) {
                if (child instanceof JComponent jcomp) {
                    jcomp.setOpaque(true);
                }
                child.setBackground(panelBg);
            }
            if (child instanceof JScrollPane scrollPane) {
                if (scrollPane.getViewport() != null) {
                    scrollPane.getViewport().setOpaque(true);
                    scrollPane.getViewport().setBackground(panelBg);
                }
            }
            if (child instanceof Container container) {
                hacerComponentesOpacos(container);
            }
        }
    }

    /**
     * Muestra la pantalla correspondiente al identificador de tarjeta.
     */
    public void mostrarPantalla(String cardName) {
        if (CARD_HOME.equals(cardName)) {
            actualizarEstadisticasDashboard();
        }
        cardLayout.show(contentPanel, cardName);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
        java.awt.Toolkit.getDefaultToolkit().sync(); // Fuerza la sincronización del búfer gráfico del SO
        actualizarBarraEstado("Módulo activo: " + cardName.replace("CARD_", ""));
    }

    public void actualizarBarraEstado(String mensaje) {
        if (statusLabel != null) {
            statusLabel.setText(" " + mensaje + " | Base de datos: Conectada");
        }
    }

    /**
     * Método de inicio para lanzar el Dashboard con FlatDarkLaf.
     */
    public static void iniciar() {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame();
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        iniciar();
    }
}


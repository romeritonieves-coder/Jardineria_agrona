package jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.EmpleadoUseCasePort;
import jardineria.application.ports.in.OficinaUseCasePort;
import jardineria.application.ports.in.PagoUseCasePort;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Ventana principal del sistema (Dashboard) para Jardinería Agrona.
 * Adaptador de entrada primario en la Arquitectura Hexagonal.
 * Diseñado con la identidad visual de "AGRONA Jardinería" (Tema oscuro con acentos verde esmeralda y verde lima).
 */
public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JLabel statusLabel;
    private final java.util.Map<String, Component> registeredCards = new java.util.HashMap<>();

    private ClienteUseCasePort clienteUseCase;
    private ProductoUseCasePort productoUseCase;
    private PedidoUseCasePort pedidoUseCase;
    private OficinaUseCasePort oficinaUseCase;
    private EmpleadoUseCasePort empleadoUseCase;
    private PagoUseCasePort pagoUseCase;

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
        this(null, null, null, null, null, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase) {
        this(clienteUseCase, productoUseCase, pedidoUseCase, null, null, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase,
                          ProductoUseCasePort productoUseCase,
                          PedidoUseCasePort pedidoUseCase,
                          OficinaUseCasePort oficinaUseCase,
                          EmpleadoUseCasePort empleadoUseCase,
                          PagoUseCasePort pagoUseCase) {
        configurarTemaAgrona();

        this.clienteUseCase = clienteUseCase;
        this.productoUseCase = productoUseCase;
        this.pedidoUseCase = pedidoUseCase;
        this.oficinaUseCase = oficinaUseCase;
        this.empleadoUseCase = empleadoUseCase;
        this.pagoUseCase = pagoUseCase;

        setTitle("AGRONA Jardinería - Sistema de Gestión Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 740);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);

        // Inicializar MenuBar
        setJMenuBar(createMenuBar());

        // Layout principal
        setLayout(new BorderLayout());
        Color fondoGeneral = UIManager.getColor("Panel.background");
        if (fondoGeneral == null) fondoGeneral = new Color(0x12, 0x16, 0x14);

        this.getRootPane().setOpaque(true);
        this.getRootPane().setBackground(fondoGeneral);
        ((JComponent) this.getContentPane()).setOpaque(true);
        ((JComponent) this.getContentPane()).setBackground(fondoGeneral);

        // Barra de herramientas superior / navegación rápida
        add(createToolBar(), BorderLayout.NORTH);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(fondoGeneral);

        // Añadir tarjeta de bienvenida inicial con Dashboard de métricas
        registrarPantalla(createHomePanel(), CARD_HOME);

        // Registrar paneles de marcador de posición (Placeholders) para módulos pendientes
        registrarPantalla(createPlaceholderPanel("Gestión de Pagos"), CARD_PAGOS);
        registrarPantalla(createPlaceholderPanel("Gestión de Empleados"), CARD_EMPLEADOS);
        registrarPantalla(createPlaceholderPanel("Gestión de Oficinas"), CARD_OFICINAS);
        registrarPantalla(createPlaceholderPanel("Gamas de Productos"), CARD_GAMAS);

        add(contentPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        statusLabel = new JLabel(" Sistema Listo | Base de datos: Conectada (MySQL Docker) | AGRONA Jardinería v1.0");
        statusLabel.setBorder(new EmptyBorder(8, 14, 8, 14));
        statusLabel.setForeground(new Color(0x94, 0xA3, 0x98));
        add(statusLabel, BorderLayout.SOUTH);

        // Asegurar opacidad recursiva de todos los componentes
        hacerComponentesOpacos(this);

        // Mostrar pantalla de inicio y cargar KPIs
        mostrarPantalla(CARD_HOME);
    }

    /**
     * Aplica la personalización global de FlatLaf para la identidad de marca "AGRONA Jardinería":
     * Fondos oscuros profundos botánicos (#121614, #1E2621), acentos en verde esmeralda (#2ECC71, #27AE60)
     * y verde lima (#A3E635), bordes redondeados y efectos hover modernos.
     */
    public static void configurarTemaAgrona() {
        FlatDarkLaf.setup();

        Color fondoGeneral = new Color(0x12, 0x16, 0x14);   // #121614
        Color fondoPaneles = new Color(0x1E, 0x26, 0x21);   // #1E2621
        Color fondoElevado = new Color(0x25, 0x32, 0x2A);   // #25322A
        Color bordeSuave = new Color(0x2D, 0x3D, 0x33);     // #2D3D33
        Color verdeEsmeralda = new Color(0x2E, 0xCC, 0x71); // #2ECC71 (Acento primario)
        Color verdeLima = new Color(0xA3, 0xE6, 0x35);      // #A3E635 (Acento secundario)
        Color textoPrincipal = new Color(0xF1, 0xF5, 0xF2);
        Color textoSecundario = new Color(0x94, 0xA3, 0x98);

        // 1. Acentos y estilos redondeados modernos
        UIManager.put("Component.accentColor", verdeEsmeralda);
        UIManager.put("Component.focusColor", new Color(0x2E, 0xCC, 0x71, 100));
        UIManager.put("Component.focusedBorderColor", verdeEsmeralda);
        UIManager.put("Component.borderColor", bordeSuave);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ProgressBar.arc", 10);

        // 2. Superficies y fondos
        UIManager.put("Panel.background", fondoGeneral);
        UIManager.put("control", fondoPaneles);
        UIManager.put("window", fondoGeneral);
        UIManager.put("Separator.foreground", bordeSuave);

        // 3. Botones y efectos Hover
        UIManager.put("Button.background", fondoElevado);
        UIManager.put("Button.hoverBackground", new Color(0x31, 0x43, 0x38));
        UIManager.put("Button.default.background", new Color(0x27, 0xAE, 0x60));
        UIManager.put("Button.default.hoverBackground", verdeEsmeralda);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.foreground", textoPrincipal);

        // 4. Tablas de datos
        UIManager.put("Table.background", fondoPaneles);
        UIManager.put("Table.alternateRowColor", new Color(0x17, 0x20, 0x1A));
        UIManager.put("Table.selectionBackground", new Color(0x27, 0xAE, 0x60));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", bordeSuave);
        UIManager.put("TableHeader.background", fondoElevado);
        UIManager.put("TableHeader.foreground", verdeEsmeralda);
        UIManager.put("TableHeader.separatorColor", bordeSuave);

        // 5. Menús y Barras
        UIManager.put("MenuBar.background", fondoGeneral);
        UIManager.put("MenuBar.borderColor", bordeSuave);
        UIManager.put("ToolBar.background", fondoPaneles);
        UIManager.put("ToolBar.borderColor", bordeSuave);
        UIManager.put("PopupMenu.background", fondoPaneles);
        UIManager.put("PopupMenu.borderColor", verdeEsmeralda);
        UIManager.put("MenuItem.selectionBackground", new Color(0x27, 0xAE, 0x60));
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        // 6. Inputs
        UIManager.put("TextField.background", new Color(0x16, 0x1D, 0x18));
        UIManager.put("TextField.foreground", textoPrincipal);
        UIManager.put("TextField.caretColor", verdeLima);
        UIManager.put("TextArea.background", new Color(0x16, 0x1D, 0x18));
        UIManager.put("TextArea.foreground", textoPrincipal);

        // 7. Prevención de ghosting
        UIManager.put("PopupMenu.dropShadowPainted", false);
        UIManager.put("PopupMenu.opaque", true);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("Menu.opaque", true);
        UIManager.put("MenuBar.opaque", true);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
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
        JMenuItem itemProductos = new JMenuItem("Productos e Inventario");
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
                "AGRONA Jardinería - Sistema de Gestión Empresarial\n" +
                "Arquitectura Hexagonal & SOLID | Java 17 + FlatLaf + MySQL\n" +
                "Identidad Visual: Dark Emerald & Lime",
                "Acerca de AGRONA Jardinería",
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
        toolBar.setBorder(new EmptyBorder(6, 12, 6, 12));

        JButton btnHome = new JButton("📊 Inicio");
        btnHome.addActionListener(e -> mostrarPantalla(CARD_HOME));

        JButton btnClientes = new JButton("👥 Clientes");
        btnClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnPedidos = new JButton("📦 Pedidos");
        btnPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        JButton btnProductos = new JButton("🌿 Productos");
        btnProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnPagos = new JButton("💳 Pagos");
        btnPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));

        JButton btnEmpleados = new JButton("👔 Empleados");
        btnEmpleados.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));

        JButton btnOficinas = new JButton("🏢 Oficinas");
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

    public void setServicios(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase,
                             OficinaUseCasePort oficinaUseCase, EmpleadoUseCasePort empleadoUseCase, PagoUseCasePort pagoUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.productoUseCase = productoUseCase;
        this.pedidoUseCase = pedidoUseCase;
        this.oficinaUseCase = oficinaUseCase;
        this.empleadoUseCase = empleadoUseCase;
        this.pagoUseCase = pagoUseCase;
        actualizarEstadisticasDashboard();
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setOpaque(true);
        panel.setBackground(new Color(0x12, 0x16, 0x14));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Cabecera superior con logotipo e identidad AGRONA
        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setOpaque(false);

        JPanel headerText = new JPanel(new GridLayout(2, 1, 4, 4));
        headerText.setOpaque(false);

        JLabel titleLabel = new JLabel("AGRONA Jardinería");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0x2E, 0xCC, 0x71)); // Verde Esmeralda

        JLabel subtitleLabel = new JLabel("Sistema Empresarial de Gestión | Panel de Control & Métricas en Tiempo Real");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(0x94, 0xA3, 0x98));

        headerText.add(titleLabel);
        headerText.add(subtitleLabel);
        headerPanel.add(headerText, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // 2. Contenedor central: Tarjetas KPI + Barra de Acciones + Banner Informativo
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        // Grilla de las 3 KPI Cards con bordes redondeados y línea lateral en verde brillante
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 24, 24));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 175));

        lblTotalClientes = new JLabel("0", SwingConstants.LEFT);
        lblTotalProductos = new JLabel("0", SwingConstants.LEFT);
        lblTotalPedidos = new JLabel("0", SwingConstants.LEFT);

        JPanel cardClientes = crearTarjetaKpi("TOTAL DE CLIENTES", lblTotalClientes, "Cartera activa de clientes registrados", new Color(0x2E, 0xCC, 0x71), "👥");
        JPanel cardProductos = crearTarjetaKpi("CATÁLOGO DE PRODUCTOS", lblTotalProductos, "Variedad de artículos y plantas en catálogo", new Color(0xA3, 0xE6, 0x35), "🌿");
        JPanel cardPedidos = crearTarjetaKpi("TOTAL DE PEDIDOS", lblTotalPedidos, "Órdenes de compra y despachos registrados", new Color(0x34, 0xD3, 0x99), "📦");

        kpiGrid.add(cardClientes);
        kpiGrid.add(cardProductos);
        kpiGrid.add(cardPedidos);

        centerContainer.add(kpiGrid);
        centerContainer.add(Box.createVerticalStrut(25));

        // Barra de Acciones y Refresco
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomBar.setOpaque(false);

        JButton btnRefrescar = new JButton("🔄 Actualizar Métricas");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefrescar.setBackground(new Color(0x27, 0xAE, 0x60));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.addActionListener(e -> actualizarEstadisticasDashboard());

        JButton btnIrClientes = new JButton("👥 Clientes");
        btnIrClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnIrProductos = new JButton("🌿 Catálogo");
        btnIrProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnIrPedidos = new JButton("📦 Pedidos");
        btnIrPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        JButton btnIrPagos = new JButton("💳 Pagos");
        btnIrPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));

        JButton btnIrEmpleados = new JButton("👔 Empleados");
        btnIrEmpleados.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));

        JButton btnIrOficinas = new JButton("🏢 Oficinas");
        btnIrOficinas.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));

        bottomBar.add(btnIrClientes);
        bottomBar.add(btnIrProductos);
        bottomBar.add(btnIrPedidos);
        bottomBar.add(btnIrPagos);
        bottomBar.add(btnIrEmpleados);
        bottomBar.add(btnIrOficinas);
        bottomBar.add(btnRefrescar);

        centerContainer.add(bottomBar);
        centerContainer.add(Box.createVerticalStrut(25));

        // Banner Informativo Institucional AGRONA
        JPanel bannerInfo = new JPanel(new BorderLayout(15, 10));
        bannerInfo.setOpaque(true);
        bannerInfo.setBackground(new Color(0x1A, 0x22, 0x1D));
        bannerInfo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x2D, 0x3D, 0x33), 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel lblBannerTitle = new JLabel("🌿 Arquitectura Hexagonal & Rendimiento Optimizado");
        lblBannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBannerTitle.setForeground(new Color(0x2E, 0xCC, 0x71));

        JLabel lblBannerText = new JLabel("<html>El sistema opera con persistencia pura MySQL JDBC, desacoplamiento absoluto por casos de uso y renderizado acelerado con FlatLaf Dark Theme.<br>Para generar reportes analíticos, utiliza la opción <b>Exportar a Excel (.xlsx)</b> presente en cada módulo.</html>");
        lblBannerText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBannerText.setForeground(new Color(0x94, 0xA3, 0x98));

        bannerInfo.add(lblBannerTitle, BorderLayout.NORTH);
        bannerInfo.add(lblBannerText, BorderLayout.CENTER);

        centerContainer.add(bannerInfo);

        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Construye una tarjeta KPI con estética AGRONA: fondo oscuro (#1E2621),
     * línea lateral izquierda de 6px en verde brillante, borde redondeado y tipografía nítida en blanco.
     */
    private JPanel crearTarjetaKpi(String titulo, JLabel valorLabel, String pie, Color colorAcento, String emoji) {
        JPanel card = new JPanel(new BorderLayout(12, 10));
        card.setOpaque(true);
        card.setBackground(new Color(0x1E, 0x26, 0x21));

        // Borde moderno con línea de acento izquierda y contorno redondeado suave
        Border lineaLateral = BorderFactory.createMatteBorder(0, 6, 0, 0, colorAcento);
        Border contorno = new LineBorder(new Color(0x2D, 0x3D, 0x33), 1, true);
        Border padding = new EmptyBorder(20, 22, 20, 22);

        card.setBorder(BorderFactory.createCompoundBorder(
                contorno,
                BorderFactory.createCompoundBorder(lineaLateral, padding)
        ));

        // 1. Título e Icono superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topPanel.setOpaque(false);
        JLabel lblTitulo = new JLabel(emoji + " " + titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(colorAcento);
        topPanel.add(lblTitulo);

        // 2. Valor cuantitativo grande, nítido y blanco
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 54));
        valorLabel.setForeground(new Color(0xF1, 0xF5, 0xF2));

        // 3. Subtexto explicativo al pie
        JLabel lblPie = new JLabel(pie, SwingConstants.LEFT);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPie.setForeground(new Color(0x94, 0xA3, 0x98));

        card.add(topPanel, BorderLayout.NORTH);
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
        titleLabel.setForeground(new Color(0x2E, 0xCC, 0x71));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Módulo en construcción");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(0x94, 0xA3, 0x98));
        panel.add(subtitleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel infoLabel = new JLabel("<html><center>Esta sección estará disponible en la próxima versión.<br>Actualmente los módulos funcionales son <b>Clientes</b>, <b>Productos</b> y <b>Pedidos</b>.</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setForeground(new Color(0x94, 0xA3, 0x98));
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
     * a todos los contenedores anidados para eliminar el ghosting en Swing.
     */
    public static void hacerComponentesOpacos(Container parent) {
        if (parent == null) return;

        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg == null) {
            panelBg = new Color(0x12, 0x16, 0x14);
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
        java.awt.Toolkit.getDefaultToolkit().sync();
        actualizarBarraEstado("Módulo activo: " + cardName.replace("CARD_", ""));
    }

    public void actualizarBarraEstado(String mensaje) {
        if (statusLabel != null) {
            statusLabel.setText(" " + mensaje + " | Base de datos: Conectada (MySQL Docker) | AGRONA Jardinería v1.0");
        }
    }

    /**
     * Método de inicio para lanzar el Dashboard con la identidad visual AGRONA.
     */
    public static void iniciar() {
        configurarTemaAgrona();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame();
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        iniciar();
    }
}

package jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.EmpleadoUseCasePort;
import jardineria.application.ports.in.GamaProductoUseCasePort;
import jardineria.application.ports.in.OficinaUseCasePort;
import jardineria.application.ports.in.PagoUseCasePort;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.infrastructure.adapters.in.ui.components.AgronaIcons;
import jardineria.infrastructure.adapters.in.ui.components.GraficoBarrasAgrona;
import jardineria.infrastructure.adapters.in.ui.components.HeroAgronaPanel;
import jardineria.infrastructure.adapters.in.ui.panels.PanelCliente;
import jardineria.infrastructure.adapters.in.ui.panels.PanelEmpleado;
import jardineria.infrastructure.adapters.in.ui.panels.PanelGamaProducto;
import jardineria.infrastructure.adapters.in.ui.panels.PanelOficina;
import jardineria.infrastructure.adapters.in.ui.panels.PanelPago;
import jardineria.infrastructure.adapters.in.ui.panels.PanelPedido;
import jardineria.infrastructure.adapters.in.ui.panels.PanelProducto;
import jardineria.application.services.ClienteService;
import jardineria.application.services.EmpleadoService;
import jardineria.application.services.GamaProductoService;
import jardineria.application.services.OficinaService;
import jardineria.application.services.PagoService;
import jardineria.application.services.PedidoService;
import jardineria.application.services.ProductoService;
import jardineria.domain.ports.out.*;
import jardineria.infrastructure.adapters.out.persistence.ClienteRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.DetallePedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.EmpleadoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.GamaProductoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.OficinaRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.PagoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.PedidoRepositoryJdbc;
import jardineria.infrastructure.adapters.out.persistence.ProductoRepositoryJdbc;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del sistema (Dashboard Ejecutivo) para Jardinería Agrona.
 * Adaptador de entrada primario en la Arquitectura Hexagonal.
 * Diseñado con la identidad visual oficial de "AGRONA Jardinería":
 * Fondos oscuros profundos (#121614, #1E2621), acentos en verde esmeralda (#2ECC71) y verde lima (#A3E635),
 * gráficas dinámicas personalizadas en Swing y animaciones fluidas en contadores.
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
    private GamaProductoUseCasePort gamaUseCase;

    // Etiquetas de métricas (KPIs)
    private JLabel lblTotalClientes;
    private JLabel lblTotalProductos;
    private JLabel lblTotalPedidos;
    private JLabel lblTotalPagos;
    private JLabel lblTotalEmpleados;
    private JLabel lblTotalOficinas;

    // Componente gráfico personalizado
    private GraficoBarrasAgrona graficoBarras;

    public static final String CARD_HOME = "CARD_HOME";
    public static final String CARD_CLIENTES = "CARD_CLIENTES";
    public static final String CARD_PEDIDOS = "CARD_PEDIDOS";
    public static final String CARD_PRODUCTOS = "CARD_PRODUCTOS";
    public static final String CARD_EMPLEADOS = "CARD_EMPLEADOS";
    public static final String CARD_OFICINAS = "CARD_OFICINAS";
    public static final String CARD_GAMAS = "CARD_GAMAS";
    public static final String CARD_PAGOS = "CARD_PAGOS";

    public DashboardFrame() {
        this(null, null, null, null, null, null, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase) {
        this(clienteUseCase, productoUseCase, pedidoUseCase, null, null, null, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase,
                          ProductoUseCasePort productoUseCase,
                          PedidoUseCasePort pedidoUseCase,
                          OficinaUseCasePort oficinaUseCase,
                          EmpleadoUseCasePort empleadoUseCase,
                          PagoUseCasePort pagoUseCase) {
        this(clienteUseCase, productoUseCase, pedidoUseCase, oficinaUseCase, empleadoUseCase, pagoUseCase, null);
    }

    public DashboardFrame(ClienteUseCasePort clienteUseCase,
                          ProductoUseCasePort productoUseCase,
                          PedidoUseCasePort pedidoUseCase,
                          OficinaUseCasePort oficinaUseCase,
                          EmpleadoUseCasePort empleadoUseCase,
                          PagoUseCasePort pagoUseCase,
                          GamaProductoUseCasePort gamaUseCase) {
        configurarTemaAgrona();

        // Inicializar repositorios y servicios por defecto si no son inyectados externamente
        ClienteRepositoryPort clienteRepo = new ClienteRepositoryJdbc();
        EmpleadoRepositoryPort empleadoRepo = new EmpleadoRepositoryJdbc();
        OficinaRepositoryPort oficinaRepo = new OficinaRepositoryJdbc();
        PagoRepositoryPort pagoRepo = new PagoRepositoryJdbc();
        GamaProductoRepositoryPort gamaRepo = new GamaProductoRepositoryJdbc();
        DetallePedidoRepositoryPort detalleRepo = new DetallePedidoRepositoryJdbc();
        ProductoRepositoryPort productoRepo = new ProductoRepositoryJdbc();
        PedidoRepositoryPort pedidoRepo = new PedidoRepositoryJdbc();

        this.clienteUseCase = clienteUseCase != null ? clienteUseCase : new ClienteService(clienteRepo, empleadoRepo);
        this.productoUseCase = productoUseCase != null ? productoUseCase : new ProductoService(productoRepo, gamaRepo, detalleRepo);
        this.pedidoUseCase = pedidoUseCase != null ? pedidoUseCase : new PedidoService(pedidoRepo, detalleRepo, clienteRepo, productoRepo);
        this.oficinaUseCase = oficinaUseCase != null ? oficinaUseCase : new OficinaService(oficinaRepo, empleadoRepo);
        this.empleadoUseCase = empleadoUseCase != null ? empleadoUseCase : new EmpleadoService(empleadoRepo, oficinaRepo, clienteRepo);
        this.pagoUseCase = pagoUseCase != null ? pagoUseCase : new PagoService(pagoRepo, clienteRepo);
        this.gamaUseCase = gamaUseCase != null ? gamaUseCase : new GamaProductoService(gamaRepo, productoRepo);

        setTitle("AGRONA Jardinería — Sistema de Gestión Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);

        // Incorporar el logotipo oficial como icono de la ventana de la aplicación
        ImageIcon iconoVentana = obtenerLogoIcon(64, 64);
        if (iconoVentana != null) {
            setIconImage(iconoVentana.getImage());
        }

        // Inicializar MenuBar
        setJMenuBar(createMenuBar());

        // Layout principal
        setLayout(new BorderLayout());
        Color fondoGeneral = new Color(0x12, 0x16, 0x14);

        this.getRootPane().setOpaque(true);
        this.getRootPane().setBackground(fondoGeneral);
        ((JComponent) this.getContentPane()).setOpaque(true);
        ((JComponent) this.getContentPane()).setBackground(fondoGeneral);

        // Barra de herramientas superior con logotipo integrado
        add(createToolBar(), BorderLayout.NORTH);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(fondoGeneral);

        // Añadir tarjeta de inicio con Dashboard ejecutivo y gráficos
        registrarPantalla(createHomePanel(), CARD_HOME);

        // Registrar todos los módulos funcionales conectando con los casos de uso
        registrarPantalla(new PanelCliente(this.clienteUseCase), CARD_CLIENTES);
        registrarPantalla(new PanelProducto(this.productoUseCase), CARD_PRODUCTOS);
        registrarPantalla(new PanelGamaProducto(this.gamaUseCase), CARD_GAMAS);
        registrarPantalla(new PanelPedido(this.pedidoUseCase), CARD_PEDIDOS);
        registrarPantalla(new PanelPago(this.pagoUseCase, this.clienteUseCase), CARD_PAGOS);
        registrarPantalla(new PanelEmpleado(this.empleadoUseCase, this.oficinaUseCase), CARD_EMPLEADOS);
        registrarPantalla(new PanelOficina(this.oficinaUseCase), CARD_OFICINAS);

        add(contentPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        statusLabel = new JLabel(" Sistema Listo | Base de datos: Conectada (MySQL Docker) | AGRONA Jardinería v2.0");
        statusLabel.setBorder(new EmptyBorder(8, 14, 8, 14));
        statusLabel.setForeground(new Color(0x94, 0xA3, 0x98));
        add(statusLabel, BorderLayout.SOUTH);

        // Asegurar opacidad recursiva de todos los componentes
        hacerComponentesOpacos(this);

        // Mostrar pantalla de inicio y sincronizar KPIs
        mostrarPantalla(CARD_HOME);
    }

    /**
     * Carga el logotipo oficial de AGRONA Jardinería empaquetado en los recursos del proyecto.
     */
    public static ImageIcon obtenerLogoIcon(int ancho, int alto) {
        try {
            URL url = DashboardFrame.class.getResource("/agrona_logo_icon.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Aplica la personalización global de FlatLaf para la identidad de marca "AGRONA Jardinería":
     * Fondos oscuros profundos botánicos (#121614, #1E2621), acentos en verde esmeralda (#2ECC71, #27AE60)
     * y verde lima (#A3E635), bordes redondeados y efectos hover modernos.
     */
    public static void configurarTemaAgrona() {
        FlatDarkLaf.setup();

        Color fondoGeneral = new Color(0x16, 0x1F, 0x1A);   // #161F1A (Fondo suave forestal)
        Color fondoPaneles = new Color(0x23, 0x30, 0x2B);   // #23302B (Paneles grisáceos verdosos sobrios)
        Color fondoElevado = new Color(0x2C, 0x3B, 0x35);   // #2C3B35 (Superficies elevadas e inputs)
        Color bordeSuave = new Color(0x38, 0x4D, 0x44);     // #384D44 (Bordes sutiles)
        Color verdeEsmeralda = new Color(0x22, 0xC5, 0x5E); // #22C55E (Acento primario vibrante)
        Color verdeLima = new Color(0xBE, 0xF2, 0x64);      // #BEF264 (Acento secundario lima fresco)
        Color textoPrincipal = new Color(0xF1, 0xF5, 0xF2);

        // 1. Acentos y estilos redondeados modernos
        UIManager.put("Component.accentColor", verdeEsmeralda);
        UIManager.put("Component.focusColor", new Color(0x22, 0xC5, 0x5E, 100));
        UIManager.put("Component.focusedBorderColor", verdeEsmeralda);
        UIManager.put("Component.borderColor", bordeSuave);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ProgressBar.arc", 12);

        // 2. Superficies y fondos
        UIManager.put("Panel.background", fondoGeneral);
        UIManager.put("control", fondoPaneles);
        UIManager.put("window", fondoGeneral);
        UIManager.put("Separator.foreground", bordeSuave);

        // 3. Botones y efectos Hover
        UIManager.put("Button.background", fondoElevado);
        UIManager.put("Button.hoverBackground", new Color(0x36, 0x47, 0x40));
        UIManager.put("Button.default.background", verdeEsmeralda);
        UIManager.put("Button.default.hoverBackground", new Color(0x16, 0xA3, 0x4A));
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.foreground", textoPrincipal);

        // 4. Tablas de datos
        UIManager.put("Table.background", fondoPaneles);
        UIManager.put("Table.alternateRowColor", new Color(0x1C, 0x27, 0x22));
        UIManager.put("Table.selectionBackground", verdeEsmeralda);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", bordeSuave);
        UIManager.put("TableHeader.background", fondoElevado);
        UIManager.put("TableHeader.foreground", verdeLima);
        UIManager.put("TableHeader.separatorColor", bordeSuave);

        // 5. Menús y Barras
        UIManager.put("MenuBar.background", fondoGeneral);
        UIManager.put("MenuBar.borderColor", bordeSuave);
        UIManager.put("ToolBar.background", fondoPaneles);
        UIManager.put("ToolBar.borderColor", bordeSuave);
        UIManager.put("PopupMenu.background", fondoPaneles);
        UIManager.put("PopupMenu.borderColor", verdeEsmeralda);
        UIManager.put("MenuItem.selectionBackground", verdeEsmeralda);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        // 6. Inputs
        UIManager.put("TextField.background", new Color(0x1B, 0x24, 0x20));
        UIManager.put("TextField.foreground", textoPrincipal);
        UIManager.put("TextField.caretColor", verdeLima);
        UIManager.put("TextArea.background", new Color(0x1B, 0x24, 0x20));
        UIManager.put("TextArea.foreground", textoPrincipal);

        // 7. Prevención de ghosting en Swing
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
        Color panelBg = new Color(0x12, 0x16, 0x14);
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
        JMenuItem itemOficinas = new JMenuItem("Oficinas y Sedes");
        itemOficinas.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));
        menuOrganizacion.add(itemEmpleados);
        menuOrganizacion.add(itemOficinas);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "AGRONA Jardinería - Sistema Empresarial de Gestión v2.0\n" +
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
        toolBar.setBorder(new EmptyBorder(6, 14, 6, 14));

        // Incorporar el logotipo oficial miniatura en la barra de herramientas
        ImageIcon logoMini = obtenerLogoIcon(24, 24);
        if (logoMini != null) {
            JLabel lblLogoBar = new JLabel(" AGRONA", logoMini, SwingConstants.LEFT);
            lblLogoBar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblLogoBar.setForeground(new Color(0x2E, 0xCC, 0x71));
            lblLogoBar.setBorder(new EmptyBorder(0, 2, 0, 10));
            toolBar.add(lblLogoBar);
            toolBar.addSeparator();
        }

        JButton btnHome = new JButton("Inicio", AgronaIcons.crearIconoHome(16, 16));
        btnHome.addActionListener(e -> mostrarPantalla(CARD_HOME));

        JButton btnClientes = new JButton("Clientes", AgronaIcons.crearIconoClientes(16, 16));
        btnClientes.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnPedidos = new JButton("Pedidos", AgronaIcons.crearIconoPedidos(16, 16));
        btnPedidos.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        JButton btnProductos = new JButton("Catálogo", AgronaIcons.crearIconoCatalogo(16, 16));
        btnProductos.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnGamas = new JButton("Gamas", AgronaIcons.crearIconoGamas(16, 16));
        btnGamas.addActionListener(e -> mostrarPantalla(CARD_GAMAS));

        JButton btnPagos = new JButton("Pagos", AgronaIcons.crearIconoPagos(16, 16));
        btnPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));

        JButton btnEmpleados = new JButton("Empleados", AgronaIcons.crearIconoEmpleados(16, 16));
        btnEmpleados.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));

        JButton btnOficinas = new JButton("Oficinas", AgronaIcons.crearIconoOficinas(16, 16));
        btnOficinas.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));

        toolBar.add(btnHome);
        toolBar.addSeparator();
        toolBar.add(btnClientes);
        toolBar.add(btnPedidos);
        toolBar.add(btnProductos);
        toolBar.add(btnGamas);
        toolBar.add(btnPagos);
        toolBar.addSeparator();
        toolBar.add(btnEmpleados);
        toolBar.add(btnOficinas);

        return toolBar;
    }

    public void setServicios(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase,
                             OficinaUseCasePort oficinaUseCase, EmpleadoUseCasePort empleadoUseCase, PagoUseCasePort pagoUseCase) {
        setServicios(clienteUseCase, productoUseCase, pedidoUseCase, oficinaUseCase, empleadoUseCase, pagoUseCase, null);
    }

    public void setServicios(ClienteUseCasePort clienteUseCase, ProductoUseCasePort productoUseCase, PedidoUseCasePort pedidoUseCase,
                             OficinaUseCasePort oficinaUseCase, EmpleadoUseCasePort empleadoUseCase, PagoUseCasePort pagoUseCase,
                             GamaProductoUseCasePort gamaUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.productoUseCase = productoUseCase;
        this.pedidoUseCase = pedidoUseCase;
        this.oficinaUseCase = oficinaUseCase;
        this.empleadoUseCase = empleadoUseCase;
        this.pagoUseCase = pagoUseCase;
        this.gamaUseCase = gamaUseCase;
        if (gamaUseCase != null) {
            registrarPantalla(new PanelGamaProducto(gamaUseCase), CARD_GAMAS);
        }
        actualizarEstadisticasDashboard();
    }

    public GamaProductoUseCasePort getGamaUseCase() {
        return gamaUseCase;
    }

    private JPanel createHomePanel() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(true);
        mainContent.setBackground(new Color(0x16, 0x1F, 0x1A));
        mainContent.setBorder(new EmptyBorder(16, 24, 32, 24));

        // 1. Hero Section de Alta Gama: Fondo Ambiental, Logotipo Grande Flotante y Marca Oficial
        HeroAgronaPanel heroPanel = new HeroAgronaPanel(this::mostrarPantalla);
        heroPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 380));
        mainContent.add(heroPanel);
        mainContent.add(Box.createVerticalStrut(28));

        // 2. Cabecera de Sección de Métricas Ejecutivas y Sincronización
        JPanel kpiHeader = new JPanel(new BorderLayout(16, 0));
        kpiHeader.setOpaque(false);
        kpiHeader.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        kpiHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiHeader.setBorder(new EmptyBorder(0, 4, 10, 4));

        JLabel lblKpiSection = new JLabel("CONTROL OPERATIVO & MÉTRICAS EN TIEMPO REAL");
        lblKpiSection.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblKpiSection.setForeground(new Color(0x22, 0xC5, 0x5E));

        JButton btnRefrescarTop = new JButton("Sincronizar Métricas", AgronaIcons.crearIconoRefresh(14, 14));
        btnRefrescarTop.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescarTop.setBackground(new Color(0x16, 0xA3, 0x4A));
        btnRefrescarTop.setForeground(Color.WHITE);
        btnRefrescarTop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefrescarTop.addActionListener(e -> actualizarEstadisticasDashboard());

        kpiHeader.add(lblKpiSection, BorderLayout.WEST);
        kpiHeader.add(btnRefrescarTop, BorderLayout.EAST);

        mainContent.add(kpiHeader);
        mainContent.add(Box.createVerticalStrut(12));

        // 3. Grilla de Tarjetas KPI (6 módulos conectados en disposición 2x3)
        JPanel kpiGrid = new JPanel(new GridLayout(2, 3, 18, 18));
        kpiGrid.setOpaque(false);
        kpiGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 260));

        lblTotalClientes = new JLabel("0", SwingConstants.LEFT);
        lblTotalProductos = new JLabel("0", SwingConstants.LEFT);
        lblTotalPedidos = new JLabel("0", SwingConstants.LEFT);
        lblTotalPagos = new JLabel("0", SwingConstants.LEFT);
        lblTotalEmpleados = new JLabel("0", SwingConstants.LEFT);
        lblTotalOficinas = new JLabel("0", SwingConstants.LEFT);

        kpiGrid.add(crearTarjetaKpi("CLIENTES ACTIVOS", lblTotalClientes, "Cartera comercial activa", new Color(0x22, 0xC5, 0x5E), AgronaIcons.crearIconoClientes(16, 16)));
        kpiGrid.add(crearTarjetaKpi("CATÁLOGO PRODUCTOS", lblTotalProductos, "Especies botánicas y artículos", new Color(0xBE, 0xF2, 0x64), AgronaIcons.crearIconoCatalogo(16, 16)));
        kpiGrid.add(crearTarjetaKpi("PEDIDOS GESTIONADOS", lblTotalPedidos, "Órdenes de compra registradas", new Color(0x34, 0xD3, 0x99), AgronaIcons.crearIconoPedidos(16, 16)));
        kpiGrid.add(crearTarjetaKpi("TRANSACCIONES PAGO", lblTotalPagos, "Recaudos y cobros procesados", new Color(0x10, 0xB9, 0x81), AgronaIcons.crearIconoPagos(16, 16)));
        kpiGrid.add(crearTarjetaKpi("NÓMINA EMPLEADOS", lblTotalEmpleados, "Equipo técnico y comercial", new Color(0x22, 0xC5, 0x5E), AgronaIcons.crearIconoEmpleados(16, 16)));
        kpiGrid.add(crearTarjetaKpi("SEDES Y OFICINAS", lblTotalOficinas, "Puntos de operación y sucursales", new Color(0xBE, 0xF2, 0x64), AgronaIcons.crearIconoOficinas(16, 16)));

        mainContent.add(kpiGrid);
        mainContent.add(Box.createVerticalStrut(24));

        // 4. Sección Visual de Gráfica Interactiva y Acciones Rápidas
        JPanel visualContainer = new JPanel(new BorderLayout(20, 0));
        visualContainer.setOpaque(false);
        visualContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Componente gráfico personalizado Swing
        graficoBarras = new GraficoBarrasAgrona();
        visualContainer.add(graficoBarras, BorderLayout.CENTER);

        // Panel lateral de accesos directos
        JPanel panelAccesos = new JPanel(new GridLayout(7, 1, 8, 8));
        panelAccesos.setOpaque(false);
        panelAccesos.setPreferredSize(new Dimension(250, 270));

        JButton btnAccesoCli = new JButton("Gestionar Clientes", AgronaIcons.crearIconoClientes(16, 16));
        btnAccesoCli.addActionListener(e -> mostrarPantalla(CARD_CLIENTES));

        JButton btnAccesoProd = new JButton("Catálogo de Productos", AgronaIcons.crearIconoCatalogo(16, 16));
        btnAccesoProd.addActionListener(e -> mostrarPantalla(CARD_PRODUCTOS));

        JButton btnAccesoGamas = new JButton("Gamas de Producto", AgronaIcons.crearIconoGamas(16, 16));
        btnAccesoGamas.addActionListener(e -> mostrarPantalla(CARD_GAMAS));

        JButton btnAccesoPed = new JButton("Despacho de Pedidos", AgronaIcons.crearIconoPedidos(16, 16));
        btnAccesoPed.addActionListener(e -> mostrarPantalla(CARD_PEDIDOS));

        JButton btnAccesoPagos = new JButton("Registro de Pagos", AgronaIcons.crearIconoPagos(16, 16));
        btnAccesoPagos.addActionListener(e -> mostrarPantalla(CARD_PAGOS));

        JButton btnAccesoEmp = new JButton("Directorio de Empleados", AgronaIcons.crearIconoEmpleados(16, 16));
        btnAccesoEmp.addActionListener(e -> mostrarPantalla(CARD_EMPLEADOS));

        JButton btnAccesoOfi = new JButton("Sedes y Oficinas", AgronaIcons.crearIconoOficinas(16, 16));
        btnAccesoOfi.addActionListener(e -> mostrarPantalla(CARD_OFICINAS));

        panelAccesos.add(btnAccesoCli);
        panelAccesos.add(btnAccesoProd);
        panelAccesos.add(btnAccesoGamas);
        panelAccesos.add(btnAccesoPed);
        panelAccesos.add(btnAccesoPagos);
        panelAccesos.add(btnAccesoEmp);
        panelAccesos.add(btnAccesoOfi);

        visualContainer.add(panelAccesos, BorderLayout.EAST);

        mainContent.add(visualContainer);

        // Envolver en ScrollPane para adaptabilidad a cualquier resolución
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(new Color(0x16, 0x1F, 0x1A));
        if (scrollPane.getViewport() != null) {
            scrollPane.getViewport().setOpaque(true);
            scrollPane.getViewport().setBackground(new Color(0x16, 0x1F, 0x1A));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.setBackground(new Color(0x16, 0x1F, 0x1A));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Construye una tarjeta KPI con estética AGRONA: fondo forestal suave (#23302B),
     * línea lateral izquierda de 5px en verde brillante, borde redondeado e ícono vectorial limpio.
     */
    private JPanel crearTarjetaKpi(String titulo, JLabel valorLabel, String pie, Color colorAcento, Icon icon) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setOpaque(true);
        card.setBackground(new Color(0x23, 0x30, 0x2B));

        Border lineaLateral = BorderFactory.createMatteBorder(0, 5, 0, 0, colorAcento);
        Border contorno = new LineBorder(new Color(0x38, 0x4D, 0x44), 1, true);
        Border padding = new EmptyBorder(14, 16, 14, 16);

        card.setBorder(BorderFactory.createCompoundBorder(
                contorno,
                BorderFactory.createCompoundBorder(lineaLateral, padding)
        ));

        // 1. Título formal con ícono vectorial sin caracteres rotos
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topPanel.setOpaque(false);
        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            topPanel.add(lblIcon);
        }
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(colorAcento);
        topPanel.add(lblTitulo);

        // 2. Valor cuantitativo grande, nítido y blanco
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valorLabel.setForeground(new Color(0xF1, 0xF5, 0xF2));

        // 3. Subtexto explicativo al pie
        JLabel lblPie = new JLabel(pie, SwingConstants.LEFT);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPie.setForeground(new Color(0x94, 0xA3, 0x98));

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        card.add(lblPie, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Actualiza las estadísticas asíncronamente con SwingWorker y animación de conteo suave.
     * Cada consulta se aísla de forma independiente para garantizar la carga de datos reales.
     */
    public void actualizarEstadisticasDashboard() {
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int clientes = 0;
                int productos = 0;
                int pedidos = 0;
                int pagos = 0;
                int empleados = 0;
                int oficinas = 0;

                try {
                    if (clienteUseCase != null) clientes = clienteUseCase.obtenerTodosLosClientes().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar clientes: " + ex.getMessage());
                }

                try {
                    if (productoUseCase != null) productos = productoUseCase.obtenerTodosLosProductos().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar productos: " + ex.getMessage());
                }

                try {
                    if (pedidoUseCase != null) pedidos = pedidoUseCase.obtenerTodosLosPedidos().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar pedidos: " + ex.getMessage());
                }

                try {
                    if (pagoUseCase != null) pagos = pagoUseCase.listarPagos().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar pagos: " + ex.getMessage());
                }

                try {
                    if (empleadoUseCase != null) empleados = empleadoUseCase.listarEmpleados().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar empleados: " + ex.getMessage());
                }

                try {
                    if (oficinaUseCase != null) oficinas = oficinaUseCase.listarOficinas().size();
                } catch (Exception ex) {
                    System.err.println("Advertencia al sincronizar oficinas: " + ex.getMessage());
                }

                return new int[]{clientes, productos, pedidos, pagos, empleados, oficinas};
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();

                    // Animación suave de conteo numérico en tarjetas KPI
                    animarContador(lblTotalClientes, stats[0]);
                    animarContador(lblTotalProductos, stats[1]);
                    animarContador(lblTotalPedidos, stats[2]);
                    animarContador(lblTotalPagos, stats[3]);
                    animarContador(lblTotalEmpleados, stats[4]);
                    animarContador(lblTotalOficinas, stats[5]);

                    // Actualizar y animar el componente gráfico con etiquetas limpias
                    if (graficoBarras != null) {
                        List<GraficoBarrasAgrona.BarraDato> barras = new ArrayList<>();
                        barras.add(new GraficoBarrasAgrona.BarraDato("Clientes Activos", stats[0], "", new Color(0x27, 0xAE, 0x60), new Color(0x2E, 0xCC, 0x71)));
                        barras.add(new GraficoBarrasAgrona.BarraDato("Catálogo Variedades", stats[1], "", new Color(0x84, 0xCC, 0x16), new Color(0xA3, 0xE6, 0x35)));
                        barras.add(new GraficoBarrasAgrona.BarraDato("Pedidos Realizados", stats[2], "", new Color(0x05, 0x96, 0x69), new Color(0x34, 0xD3, 0x99)));
                        barras.add(new GraficoBarrasAgrona.BarraDato("Transacciones Pago", stats[3], "", new Color(0x0D, 0x94, 0x88), new Color(0x14, 0xB8, 0xA6)));
                        barras.add(new GraficoBarrasAgrona.BarraDato("Nómina Empleados", stats[4], "", new Color(0x15, 0x80, 0x3D), new Color(0x22, 0xC5, 0x5E)));
                        barras.add(new GraficoBarrasAgrona.BarraDato("Sedes y Oficinas", stats[5], "", new Color(0x65, 0xA3, 0x0D), new Color(0x84, 0xCC, 0x16)));
                        graficoBarras.setDatos(barras);
                    }

                    actualizarBarraEstado("Métricas sincronizadas: " + stats[0] + " clientes | " + stats[1] + " productos | " + stats[2] + " pedidos | " + stats[3] + " pagos");
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    /**
     * Animación de aceleración fluida (ease-out) para los contadores de texto numérico.
     */
    private void animarContador(JLabel label, int valorFinal) {
        if (label == null) return;
        final int steps = 18;
        final int delayMs = 24;
        final int[] currentStep = {0};

        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            currentStep[0]++;
            float ratio = (float) currentStep[0] / steps;
            float ease = 1f - (float) Math.pow(1f - ratio, 3);
            int valorActual = Math.round(valorFinal * ease);
            label.setText(String.format("%,d", valorActual));

            if (currentStep[0] >= steps) {
                label.setText(String.format("%,d", valorFinal));
                timer.stop();
            }
        });
        timer.start();
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

        return panel;
    }

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

    public static void hacerComponentesOpacos(Container parent) {
        if (parent == null || parent instanceof HeroAgronaPanel || parent instanceof GraficoBarrasAgrona) return;

        Color panelBg = new Color(0x12, 0x16, 0x14);

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
            if (child instanceof HeroAgronaPanel || child instanceof GraficoBarrasAgrona) {
                continue;
            }
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
            statusLabel.setText(" " + mensaje + " | Base de datos: Conectada (MySQL Docker) | AGRONA Jardinería v2.0");
        }
    }

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

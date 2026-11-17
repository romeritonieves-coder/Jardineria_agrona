package jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal del sistema (Dashboard) para Jardinería Agrona.
 * Adaptador de entrada primario en la Arquitectura Hexagonal.
 */
public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JLabel statusLabel;

    public static final String CARD_HOME = "CARD_HOME";
    public static final String CARD_CLIENTES = "CARD_CLIENTES";
    public static final String CARD_PEDIDOS = "CARD_PEDIDOS";
    public static final String CARD_PRODUCTOS = "CARD_PRODUCTOS";
    public static final String CARD_EMPLEADOS = "CARD_EMPLEADOS";
    public static final String CARD_OFICINAS = "CARD_OFICINAS";
    public static final String CARD_GAMAS = "CARD_GAMAS";
    public static final String CARD_PAGOS = "CARD_PAGOS";

    public DashboardFrame() {
        setTitle("Jardinería Agrona - Sistema de Gestión Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Inicializar MenuBar
        setJMenuBar(createMenuBar());

        // Layout principal
        setLayout(new BorderLayout());

        // Barra de herramientas superior / navegación rápida
        add(createToolBar(), BorderLayout.NORTH);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Añadir tarjeta de bienvenida inicial
        contentPanel.add(createHomePanel(), CARD_HOME);

        add(contentPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        statusLabel = new JLabel(" Sistema Listo | Conexión: MySQL Docker | Jardinería Agrona v1.0");
        statusLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
        add(statusLabel, BorderLayout.SOUTH);

        // Mostrar pantalla de inicio
        mostrarPantalla(CARD_HOME);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

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

        return menuBar;
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

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Jardinería Agrona");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Sistema de Gestión Empresarial - Arquitectura Hexagonal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        panel.add(subtitleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10);
        JLabel infoLabel = new JLabel("<html><center>Selecciona un módulo en el menú superior o la barra de herramientas<br>para gestionar Clientes, Pedidos, Productos, Pagos y Personal.</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(infoLabel, gbc);

        return panel;
    }

    /**
     * Registra o actualiza una vista dentro del CardLayout del contenedor principal.
     */
    public void registrarPantalla(JPanel panel, String cardName) {
        contentPanel.add(panel, cardName);
    }

    /**
     * Muestra la pantalla correspondiente al identificador de tarjeta.
     */
    public void mostrarPantalla(String cardName) {
        cardLayout.show(contentPanel, cardName);
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


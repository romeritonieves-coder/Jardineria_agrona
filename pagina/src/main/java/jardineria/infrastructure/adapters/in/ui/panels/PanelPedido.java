package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.application.ports.in.ReporteUseCasePort;
import jardineria.domain.model.Cliente;
import jardineria.domain.model.DetallePedido;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.export.ReporteExcelAdapter;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión CRUD completa de Pedidos.
 * Adaptador de entrada primario conectado a PedidoUseCasePort (Arquitectura Hexagonal).
 */
public class PanelPedido extends JPanel {

    private final PedidoUseCasePort pedidoUseCase;
    private ReporteUseCasePort reporteUseCase;

    // Componentes del formulario (Cabecera)
    private JTextField txtCodigoPedido;
    private JTextField txtCodigoCliente;
    private JTextField txtFechaPedido;
    private JTextField txtFechaEsperada;
    private JTextField txtFechaEntrega;
    private JComboBox<String> cmbEstado;
    private JTextField txtComentarios;

    // Componentes opcionales de líneas de detalle
    private JTextField txtCodProductoItem;
    private JTextField txtCantidadItem;
    private JTextField txtPrecioItem;
    private JTable tablaItems;
    private DefaultTableModel modeloItems;
    private JLabel lblTotalEstimado;

    // Botones de acción CRUD
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla del listado general de pedidos
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    public PanelPedido(PedidoUseCasePort pedidoUseCase) {
        this(pedidoUseCase, null);
    }

    public PanelPedido(PedidoUseCasePort pedidoUseCase, ReporteUseCasePort reporteUseCase) {
        this.pedidoUseCase = pedidoUseCase;
        this.reporteUseCase = reporteUseCase;
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarPedidosEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("Gestión y Administración de Pedidos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0x2E, 0xCC, 0x71));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor (Formulario a la izquierda, Tabla general a la derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(true);
        splitPane.setBackground(UIManager.getColor("Panel.background"));
        splitPane.setResizeWeight(0.42);
        splitPane.setContinuousLayout(true);

        // Panel Izquierdo: Formulario de Pedido
        JPanel panelFormularioContainer = createFormularioPanel();
        splitPane.setLeftComponent(panelFormularioContainer);

        // Panel Derecho: Tabla con Listado de Pedidos
        JPanel panelTablaContainer = createTablaPanel();
        splitPane.setRightComponent(panelTablaContainer);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(new TitledBorder("Datos del Pedido"));

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 1. Grid de Cabecera
        JPanel formGrid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 3, 4);

        txtCodigoPedido = new JTextField(12);
        txtCodigoCliente = new JTextField(12);
        txtFechaPedido = new JTextField(LocalDate.now().toString(), 12);
        txtFechaEsperada = new JTextField(LocalDate.now().plusDays(3).toString(), 12);
        txtFechaEntrega = new JTextField(12);
        cmbEstado = new JComboBox<>(new String[]{"Pendiente", "Entregado", "Rechazado"});
        txtComentarios = new JTextField(12);

        int row = 0;
        addFormField(formGrid, gbc, "Cód. Pedido (*):", txtCodigoPedido, row++);
        addFormField(formGrid, gbc, "Cód. Cliente (*):", txtCodigoCliente, row++);
        addFormField(formGrid, gbc, "Fecha Pedido (*):", txtFechaPedido, row++);
        addFormField(formGrid, gbc, "Fecha Esperada (*):", txtFechaEsperada, row++);
        addFormField(formGrid, gbc, "Fecha Entrega:", txtFechaEntrega, row++);
        addFormCombo(formGrid, gbc, "Estado (*):", cmbEstado, row++);
        addFormField(formGrid, gbc, "Comentarios:", txtComentarios, row++);

        formContent.add(formGrid);

        // 2. Sección Maestro-Detalle (Líneas de pedido opcionales)
        JPanel panelLineas = new JPanel(new BorderLayout(4, 4));
        panelLineas.setBorder(new TitledBorder("Líneas de Detalle (Opcional para nuevos pedidos)"));

        JPanel inputLineas = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        txtCodProductoItem = new JTextField(7);
        txtCantidadItem = new JTextField(3);
        txtPrecioItem = new JTextField(5);
        JButton btnAgregarLinea = new JButton("+");
        JButton btnQuitarLinea = new JButton("-");

        inputLineas.add(new JLabel("Prod:"));
        inputLineas.add(txtCodProductoItem);
        inputLineas.add(new JLabel("Cant:"));
        inputLineas.add(txtCantidadItem);
        inputLineas.add(new JLabel("Precio:"));
        inputLineas.add(txtPrecioItem);
        inputLineas.add(btnAgregarLinea);
        inputLineas.add(btnQuitarLinea);

        panelLineas.add(inputLineas, BorderLayout.NORTH);

        String[] colsDetalle = {"#", "Prod", "Cant", "P. Unit", "Subtotal"};
        modeloItems = new DefaultTableModel(colsDetalle, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaItems = new JTable(modeloItems);
        tablaItems.setPreferredScrollableViewportSize(new Dimension(300, 90));
        panelLineas.add(new JScrollPane(tablaItems), BorderLayout.CENTER);

        lblTotalEstimado = new JLabel("Total Pedido: $0.00");
        lblTotalEstimado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelLineas.add(lblTotalEstimado, BorderLayout.SOUTH);

        btnAgregarLinea.addActionListener(e -> accionAgregarLinea());
        btnQuitarLinea.addActionListener(e -> accionQuitarLinea());

        formContent.add(Box.createVerticalStrut(6));
        formContent.add(panelLineas);

        JScrollPane scrollForm = new JScrollPane(formContent);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // 3. Barra de Botones CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("JButton.buttonType", "default");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnGuardar.addActionListener(e -> accionGuardar());
        btnActualizar.addActionListener(e -> accionActualizar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        container.add(panelBotones, BorderLayout.SOUTH);

        return container;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void addFormCombo(JPanel panel, GridBagConstraints gbc, String label, JComboBox<?> combo, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(combo, gbc);
    }

    private JPanel createTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Listado General de Pedidos"));

        String[] columnas = {"Cód. Pedido", "Fecha Pedido", "Fecha Esperada", "Fecha Entrega", "Estado", "Cliente", "Comentarios"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);

        // 1. Interactividad en la Tabla: Clic del mouse para extraer datos y llenar el formulario
        tablaPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablaPedidos.getSelectedRow();
                if (row != -1) {
                    cargarPedidoSeleccionado();
                }
            }
        });

        // Soporte adicional para selección con teclado (flechas arriba/abajo)
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedidos.getSelectedRow() != -1) {
                cargarPedidoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPedidos);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Botones superiores: Refrescar y Exportar Pedidos a Excel
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRecargar = new JButton("Recargar Pedidos");
        btnRecargar.addActionListener(e -> cargarPedidosEnTabla());

        JButton btnExportar = new JButton("Exportar a Excel (.xlsx)");
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportar.addActionListener(e -> accionExportarExcel());

        panelTop.add(btnRecargar);
        panelTop.add(btnExportar);
        panel.add(panelTop, BorderLayout.NORTH);

        return panel;
    }

    private void accionExportarExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Historial de Pedidos en Excel");
        fileChooser.setSelectedFile(new java.io.File("reporte_pedidos.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Hojas de cálculo Excel (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try {
                if (reporteUseCase != null) {
                    reporteUseCase.exportarPedidos(fileToSave);
                } else {
                    new ReporteExcelAdapter().exportarPedidosExcel(pedidoUseCase.obtenerTodosLosPedidos(), fileToSave);
                }

                int opcion = JOptionPane.showConfirmDialog(this,
                        "¡Reporte de pedidos exportado exitosamente!\n\nArchivo guardado en:\n" + fileToSave.getAbsolutePath() +
                        "\n\n¿Deseas abrir el archivo ahora?",
                        "Exportación Exitosa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION && java.awt.Desktop.isDesktopSupported()) {
                    try {
                        java.awt.Desktop.getDesktop().open(fileToSave);
                    } catch (Exception ignored) {}
                }
            } catch (Exception ex) {
                mostrarError("Error en Exportación", "No fue posible generar el archivo Excel: " + ex.getMessage());
            }
        }
    }

    /**
     * Consulta todos los pedidos mediante PedidoUseCasePort y puebla la tabla de pedidos.
     */
    public void cargarPedidosEnTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Pedido> pedidos = pedidoUseCase.obtenerTodosLosPedidos();
            for (Pedido p : pedidos) {
                String clienteCod = (p.getCliente() != null && p.getCliente().getCodigoCliente() != null)
                        ? String.valueOf(p.getCliente().getCodigoCliente())
                        : "-";

                modeloTabla.addRow(new Object[]{
                        p.getCodigoPedido(),
                        p.getFechaPedido() != null ? p.getFechaPedido().toString() : "-",
                        p.getFechaEsperada() != null ? p.getFechaEsperada().toString() : "-",
                        p.getFechaEntrega() != null ? p.getFechaEntrega().toString() : "-",
                        p.getEstado() != null ? p.getEstado() : "-",
                        clienteCod,
                        p.getComentarios() != null ? p.getComentarios() : ""
                });
            }
        } catch (Exception ex) {
            mostrarError("Error al cargar pedidos", ex.getMessage());
        }
    }

    public void cargarHistorialPedidos() {
        cargarPedidosEnTabla();
    }

    private void cargarPedidoSeleccionado() {
        int selectedRow = tablaPedidos.getSelectedRow();
        if (selectedRow == -1) return;

        Object codObj = modeloTabla.getValueAt(selectedRow, 0);
        if (codObj == null) return;

        Integer codigoPedido = Integer.valueOf(codObj.toString());
        pedidoUseCase.obtenerPedidoPorId(codigoPedido).ifPresentOrElse(p -> {
            txtCodigoPedido.setText(String.valueOf(p.getCodigoPedido()));
            txtCodigoPedido.setEnabled(false); // Proteger llave primaria contra modificación

            txtCodigoCliente.setText(p.getCliente() != null && p.getCliente().getCodigoCliente() != null
                    ? String.valueOf(p.getCliente().getCodigoCliente()) : "");

            txtFechaPedido.setText(p.getFechaPedido() != null ? p.getFechaPedido().toString() : "");
            txtFechaEsperada.setText(p.getFechaEsperada() != null ? p.getFechaEsperada().toString() : "");
            txtFechaEntrega.setText(p.getFechaEntrega() != null ? p.getFechaEntrega().toString() : "");

            if (p.getEstado() != null) {
                cmbEstado.setSelectedItem(p.getEstado());
            }
            txtComentarios.setText(p.getComentarios() != null ? p.getComentarios() : "");

            // Cargar líneas de detalle del pedido seleccionado si existen
            modeloItems.setRowCount(0);
            if (p.getDetalles() != null && !p.getDetalles().isEmpty()) {
                for (DetallePedido d : p.getDetalles()) {
                    String codProd = d.getProducto() != null ? d.getProducto().getCodigoProducto() : "-";
                    BigDecimal precio = d.getPrecioUnidad() != null ? d.getPrecioUnidad() : BigDecimal.ZERO;
                    BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(d.getCantidad() != null ? d.getCantidad() : 0));
                    modeloItems.addRow(new Object[]{
                            d.getNumeroLinea(),
                            codProd,
                            d.getCantidad(),
                            "$" + precio,
                            "$" + subtotal
                    });
                }
                recalcularTotalEstimado();
            } else {
                lblTotalEstimado.setText("Total Pedido: $0.00");
            }
        }, () -> {
            // Carga directa de respaldo desde las celdas de la fila seleccionada
            txtCodigoPedido.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 0)));
            txtCodigoPedido.setEnabled(false);
            txtFechaPedido.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 1)));
            txtFechaEsperada.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 2)));
            Object entrega = modeloTabla.getValueAt(selectedRow, 3);
            txtFechaEntrega.setText(entrega != null && !"-".equals(entrega.toString()) ? entrega.toString() : "");
            cmbEstado.setSelectedItem(modeloTabla.getValueAt(selectedRow, 4));
            txtCodigoCliente.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 5)));
            Object comentarios = modeloTabla.getValueAt(selectedRow, 6);
            txtComentarios.setText(comentarios != null ? comentarios.toString() : "");
        });
    }

    private void accionGuardar() {
        try {
            Pedido pedido = construirPedidoDesdeFormulario();
            Pedido guardado = pedidoUseCase.crearPedido(pedido);

            JOptionPane.showMessageDialog(this,
                    "¡Pedido #" + guardado.getCodigoPedido() + " registrado exitosamente en MySQL!\n" +
                    "Cliente: " + guardado.getCliente().getCodigoCliente() + " | Estado: " + guardado.getEstado(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarPedidosEnTabla();

        } catch (DateTimeParseException ex) {
            mostrarError("Formato de Fecha Inválido",
                    "Por favor ingresa las fechas en formato YYYY-MM-DD (ejemplo: " + LocalDate.now() + ").");
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") ||
                    ex.getMessage().contains("1452") || ex.getMessage().contains("cliente"))) {
                mostrarError("Cliente Inexistente",
                        "El Código de Cliente ingresado no existe en la base de datos.\nPor favor verifica los clientes registrados.");
            } else {
                mostrarError("Error de Persistencia", ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "El código de pedido y el código de cliente deben ser números enteros.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionActualizar() {
        String codPedStr = txtCodigoPedido.getText().trim();
        if (codPedStr.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona un pedido de la tabla para actualizar.");
            return;
        }

        try {
            Pedido pedido = construirPedidoDesdeFormulario();
            Pedido actualizado = pedidoUseCase.actualizarPedido(pedido);

            JOptionPane.showMessageDialog(this,
                    "¡Pedido #" + actualizado.getCodigoPedido() + " actualizado exitosamente!\n" +
                    "Estado actual: " + actualizado.getEstado(),
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarPedidosEnTabla();

        } catch (DateTimeParseException ex) {
            mostrarError("Formato de Fecha Inválido",
                    "Por favor ingresa las fechas en formato YYYY-MM-DD (ejemplo: " + LocalDate.now() + ").");
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") ||
                    ex.getMessage().contains("1452") || ex.getMessage().contains("cliente"))) {
                mostrarError("Cliente Inexistente",
                        "El Código de Cliente especificado no existe en la base de datos.");
            } else {
                mostrarError("Error de Persistencia", ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "El código de pedido y cliente deben ser valores enteros válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String codigoTexto = txtCodigoPedido.getText().trim();
        if (codigoTexto.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona en la tabla el pedido que deseas eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el pedido #" + codigoTexto + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int codigo = Integer.parseInt(codigoTexto);
                pedidoUseCase.eliminarPedido(codigo);

                JOptionPane.showMessageDialog(this,
                        "Pedido #" + codigo + " eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarPedidosEnTabla();

            } catch (PersistenceException ex) {
                // Manejo de restricción de integridad por clave foránea (error 1451)
                if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") ||
                        ex.getMessage().contains("1451") || ex.getMessage().contains("Cannot delete or update a parent row"))) {
                    mostrarError("Restricción de Integridad",
                            "No se puede eliminar este pedido porque tiene líneas de detalle o transacciones asociadas en MySQL.\n(Error 1451: Restricción de llave foránea).");
                } else {
                    mostrarError("Error de Persistencia", ex.getMessage());
                }
            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                mostrarError("Regla de Negocio", ex.getMessage());
            } catch (NumberFormatException ex) {
                mostrarError("Código Inválido", "El código del pedido debe ser un número entero.");
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Pedido construirPedidoDesdeFormulario() {
        String codPedStr = txtCodigoPedido.getText().trim();
        String codCliStr = txtCodigoCliente.getText().trim();

        if (codPedStr.isEmpty()) {
            throw new BusinessRuleException("El código del pedido es obligatorio.");
        }
        if (codCliStr.isEmpty()) {
            throw new BusinessRuleException("El código del cliente es obligatorio.");
        }

        int codPedido = Integer.parseInt(codPedStr);
        if (codPedido <= 0) {
            throw new BusinessRuleException("El código del pedido debe ser un entero positivo mayor a cero.");
        }
        int codCliente = Integer.parseInt(codCliStr);
        if (codCliente <= 0) {
            throw new BusinessRuleException("El código del cliente debe ser un entero positivo mayor a cero.");
        }

        LocalDate fechaPedido = !txtFechaPedido.getText().trim().isEmpty()
                ? LocalDate.parse(txtFechaPedido.getText().trim()) : LocalDate.now();

        LocalDate fechaEsperada = !txtFechaEsperada.getText().trim().isEmpty()
                ? LocalDate.parse(txtFechaEsperada.getText().trim()) : fechaPedido.plusDays(3);

        String fechaEntregaStr = txtFechaEntrega.getText().trim();
        LocalDate fechaEntrega = (!fechaEntregaStr.isEmpty() && !"-".equals(fechaEntregaStr))
                ? LocalDate.parse(fechaEntregaStr) : null;

        String estado = (String) cmbEstado.getSelectedItem();
        String comentarios = txtComentarios.getText().trim();

        // Extraer detalles de la tabla de ítems si existen
        List<DetallePedido> detalles = new ArrayList<>();
        for (int i = 0; i < modeloItems.getRowCount(); i++) {
            String codProd = (String) modeloItems.getValueAt(i, 1);
            int cantidad = (Integer) modeloItems.getValueAt(i, 2);

            String precioStr = (String) modeloItems.getValueAt(i, 3);
            BigDecimal precioUnit = null;
            if (precioStr != null && precioStr.startsWith("$")) {
                precioUnit = new BigDecimal(precioStr.substring(1));
            }

            DetallePedido detalle = DetallePedido.builder()
                    .producto(Producto.builder().codigoProducto(codProd).build())
                    .cantidad(cantidad)
                    .precioUnidad(precioUnit)
                    .numeroLinea(i + 1)
                    .build();

            detalles.add(detalle);
        }

        return Pedido.builder()
                .codigoPedido(codPedido)
                .cliente(Cliente.builder().codigoCliente(codCliente).build())
                .fechaPedido(fechaPedido)
                .fechaEsperada(fechaEsperada)
                .fechaEntrega(fechaEntrega)
                .estado(estado)
                .comentarios(comentarios)
                .detalles(detalles)
                .build();
    }

    private void accionAgregarLinea() {
        String codProd = txtCodProductoItem.getText().trim();
        String cantStr = txtCantidadItem.getText().trim();
        String precioStr = txtPrecioItem.getText().trim();

        if (codProd.isEmpty() || cantStr.isEmpty()) {
            mostrarError("Campos Requeridos", "Debes ingresar el código del producto y la cantidad.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                mostrarError("Cantidad Inválida", "La cantidad debe ser mayor a 0.");
                return;
            }

            BigDecimal precioUnitario = BigDecimal.ZERO;
            if (!precioStr.isEmpty()) {
                precioUnitario = new BigDecimal(precioStr);
                if (precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
                    mostrarError("Precio Inválido", "El precio unitario no puede ser negativo.");
                    return;
                }
            }

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            int numLinea = modeloItems.getRowCount() + 1;

            modeloItems.addRow(new Object[]{
                    numLinea,
                    codProd,
                    cantidad,
                    precioUnitario.compareTo(BigDecimal.ZERO) > 0 ? "$" + precioUnitario : "Auto catálogo",
                    precioUnitario.compareTo(BigDecimal.ZERO) > 0 ? "$" + subtotal : "-"
            });

            txtCodProductoItem.setText("");
            txtCantidadItem.setText("");
            txtPrecioItem.setText("");
            recalcularTotalEstimado();

        } catch (NumberFormatException ex) {
            mostrarError("Formato Inválido", "Cantidad y precio deben ser valores numéricos válidos.");
        }
    }

    private void accionQuitarLinea() {
        int selectedRow = tablaItems.getSelectedRow();
        if (selectedRow == -1) {
            mostrarError("Selección", "Selecciona una línea de la tabla para quitarla.");
            return;
        }
        modeloItems.removeRow(selectedRow);
        recalcularTotalEstimado();
    }

    private void recalcularTotalEstimado() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modeloItems.getRowCount(); i++) {
            String subtotalStr = (String) modeloItems.getValueAt(i, 4);
            if (subtotalStr != null && subtotalStr.startsWith("$")) {
                total = total.add(new BigDecimal(subtotalStr.substring(1)));
            }
        }
        lblTotalEstimado.setText("Total Pedido: $" + total);
    }

    private void limpiarFormulario() {
        txtCodigoPedido.setText("");
        txtCodigoPedido.setEnabled(true);
        txtCodigoCliente.setText("");
        txtFechaPedido.setText(LocalDate.now().toString());
        txtFechaEsperada.setText(LocalDate.now().plusDays(3).toString());
        txtFechaEntrega.setText("");
        cmbEstado.setSelectedIndex(0);
        txtComentarios.setText("");
        txtCodProductoItem.setText("");
        txtCantidadItem.setText("");
        txtPrecioItem.setText("");
        modeloItems.setRowCount(0);
        lblTotalEstimado.setText("Total Pedido: $0.00");
        tablaPedidos.clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}

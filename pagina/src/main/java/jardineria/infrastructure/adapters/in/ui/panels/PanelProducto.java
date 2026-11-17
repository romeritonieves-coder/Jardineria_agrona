package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.ProductoUseCasePort;
import jardineria.domain.model.GamaProducto;
import jardineria.domain.model.Producto;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión de Productos e Inventario.
 * Adaptador de entrada primario conectado a ProductoUseCasePort.
 */
public class PanelProducto extends JPanel {

    private final ProductoUseCasePort productoUseCase;

    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtGama;
    private JTextField txtDimensiones;
    private JTextField txtProveedor;
    private JTextArea txtDescripcion;
    private JTextField txtStock;
    private JTextField txtPrecioVenta;
    private JTextField txtPrecioProveedor;

    // Botones
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public PanelProducto(ProductoUseCasePort productoUseCase) {
        this.productoUseCase = productoUseCase;
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarProductosEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("Catálogo de Productos e Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(true);
        splitPane.setBackground(UIManager.getColor("Panel.background"));
        splitPane.setResizeWeight(0.38);
        splitPane.setContinuousLayout(true);

        // Panel Izquierdo: Formulario
        JPanel panelFormularioContainer = createFormularioPanel();
        splitPane.setLeftComponent(panelFormularioContainer);

        // Panel Derecho: Tabla
        JPanel panelTablaContainer = createTablaPanel();
        splitPane.setRightComponent(panelTablaContainer);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(new TitledBorder("Datos del Producto"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        txtCodigo = new JTextField(12);
        txtNombre = new JTextField(12);
        txtGama = new JTextField(12);
        txtDimensiones = new JTextField(12);
        txtProveedor = new JTextField(12);
        txtDescripcion = new JTextArea(3, 12);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtStock = new JTextField(12);
        txtPrecioVenta = new JTextField(12);
        txtPrecioProveedor = new JTextField(12);

        int row = 0;
        addFormField(formGrid, gbc, "Código Producto (*):", txtCodigo, row++);
        addFormField(formGrid, gbc, "Nombre (*):", txtNombre, row++);
        addFormField(formGrid, gbc, "Gama (*):", txtGama, row++);
        addFormField(formGrid, gbc, "Dimensiones:", txtDimensiones, row++);
        addFormField(formGrid, gbc, "Proveedor:", txtProveedor, row++);

        // Campo de descripción en JScrollPane
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        formGrid.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formGrid.add(new JScrollPane(txtDescripcion), gbc);
        row++;

        addFormField(formGrid, gbc, "Stock en Tienda (*):", txtStock, row++);
        addFormField(formGrid, gbc, "Precio Venta (*):", txtPrecioVenta, row++);
        addFormField(formGrid, gbc, "Precio Proveedor:", txtPrecioProveedor, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Barra de botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnGuardar = new JButton("Guardar");
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

    private JPanel createTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Listado de Productos en Inventario"));

        String[] columnas = {"Código", "Nombre", "Gama", "Proveedor", "Stock", "Precio Venta", "Precio Prov."};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);

        // 1. Interactividad en la Tabla: Clic del mouse para extraer datos y llenar el formulario
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablaProductos.getSelectedRow();
                if (row != -1) {
                    cargarProductoSeleccionado();
                }
            }
        });

        // Soporte adicional para selección con teclado (flechas arriba/abajo)
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1) {
                cargarProductoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Botón de refrescar tabla
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRecargar = new JButton("Recargar Catálogo");
        btnRecargar.addActionListener(e -> cargarProductosEnTabla());
        panelTop.add(btnRecargar);
        panel.add(panelTop, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Consulta todos los productos a través de ProductoUseCasePort y llena la tabla del inventario.
     */
    public void cargarProductosEnTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Producto> productos = productoUseCase.obtenerTodosLosProductos();
            for (Producto p : productos) {
                String gamaNombre = (p.getGama() != null && p.getGama().getGama() != null)
                        ? p.getGama().getGama()
                        : "-";

                modeloTabla.addRow(new Object[]{
                        p.getCodigoProducto(),
                        p.getNombre(),
                        gamaNombre,
                        p.getProveedor() != null ? p.getProveedor() : "-",
                        p.getCantidadEnStock(),
                        p.getPrecioVenta() != null ? "$" + p.getPrecioVenta() : "$0.00",
                        p.getPrecioProveedor() != null ? "$" + p.getPrecioProveedor() : "$0.00"
                });
            }
        } catch (Exception ex) {
            mostrarError("Error al cargar productos", ex.getMessage());
        }
    }

    private void cargarProductoSeleccionado() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow == -1) return;

        Object codObj = modeloTabla.getValueAt(selectedRow, 0);
        if (codObj == null) return;

        String codigoProducto = codObj.toString();
        productoUseCase.obtenerProductoPorId(codigoProducto).ifPresentOrElse(p -> {
            txtCodigo.setText(p.getCodigoProducto());
            txtCodigo.setEnabled(false); // Bloquear el campo Código de Producto para proteger la llave primaria
            txtNombre.setText(p.getNombre() != null ? p.getNombre() : "");
            txtGama.setText(p.getGama() != null && p.getGama().getGama() != null ? p.getGama().getGama() : "");
            txtDimensiones.setText(p.getDimensiones() != null ? p.getDimensiones() : "");
            txtProveedor.setText(p.getProveedor() != null ? p.getProveedor() : "");
            txtDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
            txtStock.setText(p.getCantidadEnStock() != null ? p.getCantidadEnStock().toString() : "0");
            txtPrecioVenta.setText(p.getPrecioVenta() != null ? p.getPrecioVenta().toString() : "");
            txtPrecioProveedor.setText(p.getPrecioProveedor() != null ? p.getPrecioProveedor().toString() : "");
        }, () -> {
            // Carga de respaldo directa desde las celdas de la tabla
            txtCodigo.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 0)));
            txtCodigo.setEnabled(false);
            txtNombre.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 1)));
            txtGama.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 2)));
            txtProveedor.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 3)));
            txtStock.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 4)));
        });
    }

    private void accionGuardar() {
        try {
            Producto producto = construirProductoDesdeFormulario();
            productoUseCase.crearProducto(producto);
            JOptionPane.showMessageDialog(this,
                    "¡Producto registrado exitosamente en el catálogo!\nCódigo: " + producto.getCodigoProducto() + " - " + producto.getNombre(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarProductosEnTabla();
        } catch (BusinessRuleException | ResourceNotFoundException | PersistenceException ex) {
            mostrarError("Regla de Negocio / Persistencia", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "Por favor revisa que el stock y los precios sean números válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionActualizar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona un producto de la tabla para actualizar.");
            return;
        }

        try {
            Producto producto = construirProductoDesdeFormulario();
            productoUseCase.actualizarProducto(producto);
            JOptionPane.showMessageDialog(this,
                    "¡Producto actualizado exitosamente en el catálogo!\nCódigo: " + producto.getCodigoProducto() + " - " + producto.getNombre(),
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarProductosEnTabla();
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Persistencia", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "Revisa que los valores numéricos ingresados (stock, precios) sean válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona en la tabla el producto que deseas eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto '" + codigo + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productoUseCase.eliminarProducto(codigo);
                JOptionPane.showMessageDialog(this,
                        "Producto con código '" + codigo + "' eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarProductosEnTabla();
            } catch (BusinessRuleException ex) {
                // ProductoService valida si el producto tiene pedidos
                mostrarError("Restricción de Negocio", ex.getMessage());
            } catch (PersistenceException ex) {
                // Manejo de restricción de integridad por clave foránea (error 1451)
                if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") ||
                        ex.getMessage().contains("1451") || ex.getMessage().contains("Cannot delete or update a parent row"))) {
                    mostrarError("Restricción de Integridad",
                            "No se puede eliminar este producto porque ya figura en pedidos registrados en el sistema.\nDebes eliminar primero los pedidos donde se incluye.");
                } else {
                    mostrarError("Error de Persistencia", ex.getMessage());
                }
            } catch (ResourceNotFoundException ex) {
                mostrarError("No Encontrado", ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Producto construirProductoDesdeFormulario() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            throw new BusinessRuleException("El código del producto es obligatorio.");
        }

        String nombre = txtNombre.getText().trim();
        String gamaStr = txtGama.getText().trim();
        if (gamaStr.isEmpty()) {
            throw new BusinessRuleException("La gama del producto es obligatoria.");
        }
        GamaProducto gama = GamaProducto.builder().gama(gamaStr).build();

        int stock = 0;
        if (!txtStock.getText().trim().isEmpty()) {
            stock = Integer.parseInt(txtStock.getText().trim());
        }

        BigDecimal precioVenta = null;
        if (!txtPrecioVenta.getText().trim().isEmpty()) {
            precioVenta = new BigDecimal(txtPrecioVenta.getText().trim());
        }

        BigDecimal precioProveedor = null;
        if (!txtPrecioProveedor.getText().trim().isEmpty()) {
            precioProveedor = new BigDecimal(txtPrecioProveedor.getText().trim());
        }

        return Producto.builder()
                .codigoProducto(codigo)
                .nombre(nombre)
                .gama(gama)
                .dimensiones(txtDimensiones.getText().trim())
                .proveedor(txtProveedor.getText().trim())
                .descripcion(txtDescripcion.getText().trim())
                .cantidadEnStock(stock)
                .precioVenta(precioVenta)
                .precioProveedor(precioProveedor)
                .build();
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtCodigo.setEnabled(true);
        txtNombre.setText("");
        txtGama.setText("");
        txtDimensiones.setText("");
        txtProveedor.setText("");
        txtDescripcion.setText("");
        txtStock.setText("");
        txtPrecioVenta.setText("");
        txtPrecioProveedor.setText("");
        tablaProductos.clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}


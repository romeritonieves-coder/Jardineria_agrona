package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.ReporteUseCasePort;
import jardineria.domain.model.Cliente;
import jardineria.domain.model.Empleado;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.export.ReporteExcelAdapter;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión CRUD de Clientes.
 * Adaptador de entrada primario conectado al puerto ClienteUseCasePort.
 */
public class PanelCliente extends JPanel {

    private final ClienteUseCasePort clienteUseCase;
    private ReporteUseCasePort reporteUseCase;

    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtNombreContacto;
    private JTextField txtApellidoContacto;
    private JTextField txtTelefono;
    private JTextField txtFax;
    private JTextField txtCiudad;
    private JTextField txtRegion;
    private JTextField txtPais;
    private JTextField txtCodigoPostal;
    private JTextField txtLineaDireccion1;
    private JTextField txtLineaDireccion2;
    private JTextField txtLimiteCredito;
    private JTextField txtRepVentas;

    // Botones
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    public PanelCliente(ClienteUseCasePort clienteUseCase) {
        this(clienteUseCase, null);
    }

    public PanelCliente(ClienteUseCasePort clienteUseCase, ReporteUseCasePort reporteUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.reporteUseCase = reporteUseCase;
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarClientesEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("👥 Gestión de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0x2E, 0xCC, 0x71));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor (Formulario a la izquierda, Tabla a la derecha)
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
        container.setBorder(new TitledBorder("Datos del Cliente"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        txtCodigo = new JTextField(12);
        txtNombre = new JTextField(12);
        txtNombreContacto = new JTextField(12);
        txtApellidoContacto = new JTextField(12);
        txtTelefono = new JTextField(12);
        txtFax = new JTextField(12);
        txtCiudad = new JTextField(12);
        txtRegion = new JTextField(12);
        txtPais = new JTextField(12);
        txtCodigoPostal = new JTextField(12);
        txtLineaDireccion1 = new JTextField(12);
        txtLineaDireccion2 = new JTextField(12);
        txtLimiteCredito = new JTextField(12);
        txtRepVentas = new JTextField(12);

        int row = 0;
        addFormField(formGrid, gbc, "Código Cliente (*):", txtCodigo, row++);
        addFormField(formGrid, gbc, "Nombre Empresa (*):", txtNombre, row++);
        addFormField(formGrid, gbc, "Nombre Contacto:", txtNombreContacto, row++);
        addFormField(formGrid, gbc, "Apellido Contacto:", txtApellidoContacto, row++);
        addFormField(formGrid, gbc, "Teléfono (*):", txtTelefono, row++);
        addFormField(formGrid, gbc, "Fax:", txtFax, row++);
        addFormField(formGrid, gbc, "Ciudad (*):", txtCiudad, row++);
        addFormField(formGrid, gbc, "Región:", txtRegion, row++);
        addFormField(formGrid, gbc, "País (*):", txtPais, row++);
        addFormField(formGrid, gbc, "Código Postal:", txtCodigoPostal, row++);
        addFormField(formGrid, gbc, "Dirección 1 (*):", txtLineaDireccion1, row++);
        addFormField(formGrid, gbc, "Dirección 2:", txtLineaDireccion2, row++);
        addFormField(formGrid, gbc, "Límite Crédito:", txtLimiteCredito, row++);
        addFormField(formGrid, gbc, "ID Rep. Ventas (Emp):", txtRepVentas, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Barra de botones de acción
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

    private JPanel createTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Listado General de Clientes"));

        String[] columnas = {"Código", "Nombre", "Contacto", "Teléfono", "Ciudad", "País", "Límite Crédito", "Rep. Ventas"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getTableHeader().setReorderingAllowed(false);

        // 1. Interactividad en la Tabla: Clic del mouse para extraer datos y llenar el formulario
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablaClientes.getSelectedRow();
                if (row != -1) {
                    cargarClienteSeleccionado();
                }
            }
        });

        // Soporte adicional para selección con teclado (flechas arriba/abajo)
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaClientes.getSelectedRow() != -1) {
                cargarClienteSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Botones superiores: Refrescar y Exportar a Excel
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRecargar = new JButton("Recargar Tabla");
        btnRecargar.addActionListener(e -> cargarClientesEnTabla());

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
        fileChooser.setDialogTitle("Guardar Reporte de Clientes en Excel");
        fileChooser.setSelectedFile(new java.io.File("reporte_clientes.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Hojas de cálculo Excel (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try {
                if (reporteUseCase != null) {
                    reporteUseCase.exportarClientes(fileToSave);
                } else {
                    new ReporteExcelAdapter().exportarClientesExcel(clienteUseCase.obtenerTodosLosClientes(), fileToSave);
                }

                int opcion = JOptionPane.showConfirmDialog(this,
                        "¡Reporte de clientes exportado exitosamente!\n\nArchivo guardado en:\n" + fileToSave.getAbsolutePath() +
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
     * Consulta todos los clientes a través del puerto de entrada (ClienteUseCasePort)
     * y puebla la tabla de la interfaz.
     */
    public void cargarClientesEnTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Cliente> clientes = clienteUseCase.obtenerTodosLosClientes();
            for (Cliente c : clientes) {
                String contacto = (c.getNombreContacto() != null ? c.getNombreContacto() : "") + " " +
                                  (c.getApellidoContacto() != null ? c.getApellidoContacto() : "");
                String repVentas = (c.getEmpleadoRepVentas() != null && c.getEmpleadoRepVentas().getCodigoEmpleado() != null)
                        ? String.valueOf(c.getEmpleadoRepVentas().getCodigoEmpleado())
                        : "-";

                modeloTabla.addRow(new Object[]{
                        c.getCodigoCliente(),
                        c.getNombreCliente(),
                        contacto.trim().isEmpty() ? "-" : contacto.trim(),
                        c.getTelefono() != null ? c.getTelefono() : "-",
                        c.getCiudad() != null ? c.getCiudad() : "-",
                        c.getPais() != null ? c.getPais() : "-",
                        c.getLimiteCredito() != null ? "$" + c.getLimiteCredito() : "$0.00",
                        repVentas
                });
            }
        } catch (Exception ex) {
            mostrarError("Error al cargar lista de clientes", ex.getMessage());
        }
    }

    private void cargarClienteSeleccionado() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) return;

        Object codObj = modeloTabla.getValueAt(selectedRow, 0);
        if (codObj == null) return;

        Integer codigoCliente = Integer.valueOf(codObj.toString());
        clienteUseCase.obtenerClientePorId(codigoCliente).ifPresentOrElse(c -> {
            txtCodigo.setText(String.valueOf(c.getCodigoCliente()));
            txtCodigo.setEnabled(false); // No permitir modificar la clave primaria en actualización
            txtNombre.setText(c.getNombreCliente() != null ? c.getNombreCliente() : "");
            txtNombreContacto.setText(c.getNombreContacto() != null ? c.getNombreContacto() : "");
            txtApellidoContacto.setText(c.getApellidoContacto() != null ? c.getApellidoContacto() : "");
            txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
            txtFax.setText(c.getFax() != null ? c.getFax() : "");
            txtCiudad.setText(c.getCiudad() != null ? c.getCiudad() : "");
            txtRegion.setText(c.getRegion() != null ? c.getRegion() : "");
            txtPais.setText(c.getPais() != null ? c.getPais() : "");
            txtCodigoPostal.setText(c.getCodigoPostal() != null ? c.getCodigoPostal() : "");
            txtLineaDireccion1.setText(c.getLineaDireccion1() != null ? c.getLineaDireccion1() : "");
            txtLineaDireccion2.setText(c.getLineaDireccion2() != null ? c.getLineaDireccion2() : "");
            txtLimiteCredito.setText(c.getLimiteCredito() != null ? c.getLimiteCredito().toString() : "");
            txtRepVentas.setText(c.getEmpleadoRepVentas() != null && c.getEmpleadoRepVentas().getCodigoEmpleado() != null
                    ? String.valueOf(c.getEmpleadoRepVentas().getCodigoEmpleado()) : "");
        }, () -> {
            // Carga directa de respaldo basada en los valores presentes en la tabla
            txtCodigo.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 0)));
            txtCodigo.setEnabled(false);
            txtNombre.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 1)));
            txtTelefono.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 3)));
            txtCiudad.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 4)));
            txtPais.setText(String.valueOf(modeloTabla.getValueAt(selectedRow, 5)));
        });
    }

    private void accionGuardar() {
        try {
            Cliente nuevoCliente = construirClienteDesdeFormulario();
            clienteUseCase.crearCliente(nuevoCliente);
            JOptionPane.showMessageDialog(this,
                    "¡Cliente registrado con éxito en la base de datos!\nCódigo: " + nuevoCliente.getCodigoCliente() + " - " + nuevoCliente.getNombreCliente(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarClientesEnTabla();
        } catch (BusinessRuleException | ResourceNotFoundException | PersistenceException ex) {
            mostrarError("Regla de Negocio / Persistencia", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "Por favor revisa el código, límite de crédito o ID del representante.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionActualizar() {
        String codigoTexto = txtCodigo.getText().trim();
        if (codigoTexto.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona un cliente de la tabla para actualizar.");
            return;
        }

        try {
            Cliente cliente = construirClienteDesdeFormulario();
            clienteUseCase.actualizarCliente(cliente);
            JOptionPane.showMessageDialog(this,
                    "¡Cliente actualizado exitosamente en MySQL!\nCódigo: " + cliente.getCodigoCliente() + " - " + cliente.getNombreCliente(),
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarClientesEnTabla();
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error en Persistencia", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Formato Numérico Inválido", "Revisa que los valores numéricos ingresados (código, límite crédito, representante) sean válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String codigoTexto = txtCodigo.getText().trim();
        if (codigoTexto.isEmpty()) {
            mostrarError("Validación", "Por favor selecciona en la tabla el cliente que deseas eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este cliente (Código: " + codigoTexto + ")?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int codigo = Integer.parseInt(codigoTexto);
                clienteUseCase.eliminarCliente(codigo);
                JOptionPane.showMessageDialog(this,
                        "Cliente con código " + codigo + " eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarClientesEnTabla();
            } catch (PersistenceException ex) {
                // Manejo amigable de clave foránea referenciada en pedidos/pagos
                if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") ||
                        ex.getMessage().contains("1451") || ex.getMessage().contains("Cannot delete or update a parent row"))) {
                    mostrarError("Restricción de Integridad",
                            "No se puede eliminar este cliente porque tiene pedidos o pagos registrados en el sistema.\nDebes eliminar primero sus pedidos y pagos asociados.");
                } else {
                    mostrarError("Error en Persistencia", ex.getMessage());
                }
            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                mostrarError("Regla de Negocio", ex.getMessage());
            } catch (NumberFormatException ex) {
                mostrarError("Código Inválido", "El código debe ser un valor entero numérico.");
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Cliente construirClienteDesdeFormulario() {
        String codTexto = txtCodigo.getText().trim();
        if (codTexto.isEmpty()) {
            throw new BusinessRuleException("El código del cliente es requerido.");
        }
        int codigo = Integer.parseInt(codTexto);

        BigDecimal limiteCredito = null;
        if (!txtLimiteCredito.getText().trim().isEmpty()) {
            limiteCredito = new BigDecimal(txtLimiteCredito.getText().trim());
        }

        Empleado repVentas = null;
        if (!txtRepVentas.getText().trim().isEmpty()) {
            int codRep = Integer.parseInt(txtRepVentas.getText().trim());
            repVentas = Empleado.builder().codigoEmpleado(codRep).build();
        }

        return Cliente.builder()
                .codigoCliente(codigo)
                .nombreCliente(txtNombre.getText().trim())
                .nombreContacto(txtNombreContacto.getText().trim())
                .apellidoContacto(txtApellidoContacto.getText().trim())
                .telefono(txtTelefono.getText().trim())
                .fax(txtFax.getText().trim())
                .ciudad(txtCiudad.getText().trim())
                .region(txtRegion.getText().trim())
                .pais(txtPais.getText().trim())
                .codigoPostal(txtCodigoPostal.getText().trim())
                .lineaDireccion1(txtLineaDireccion1.getText().trim())
                .lineaDireccion2(txtLineaDireccion2.getText().trim())
                .limiteCredito(limiteCredito)
                .empleadoRepVentas(repVentas)
                .build();
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtCodigo.setEnabled(true);
        txtNombre.setText("");
        txtNombreContacto.setText("");
        txtApellidoContacto.setText("");
        txtTelefono.setText("");
        txtFax.setText("");
        txtCiudad.setText("");
        txtRegion.setText("");
        txtPais.setText("");
        txtCodigoPostal.setText("");
        txtLineaDireccion1.setText("");
        txtLineaDireccion2.setText("");
        txtLimiteCredito.setText("");
        txtRepVentas.setText("");
        tablaClientes.clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}


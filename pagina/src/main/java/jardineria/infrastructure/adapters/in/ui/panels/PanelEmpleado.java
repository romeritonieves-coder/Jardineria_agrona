package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.EmpleadoUseCasePort;
import jardineria.application.ports.in.OficinaUseCasePort;
import jardineria.domain.model.Empleado;
import jardineria.domain.model.Oficina;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión CRUD de Empleados.
 * Adaptador de entrada primario conectado a EmpleadoUseCasePort y OficinaUseCasePort.
 */
public class PanelEmpleado extends JPanel {

    private final EmpleadoUseCasePort empleadoUseCase;
    private final OficinaUseCasePort oficinaUseCase;

    // Componentes del formulario
    private JTextField txtCodigoEmpleado;
    private JTextField txtNombre;
    private JTextField txtApellido1;
    private JTextField txtApellido2;
    private JTextField txtExtension;
    private JTextField txtEmail;
    private JTextField txtPuesto;
    private JComboBox<ComboItem<String>> cmbOficina;
    private JComboBox<ComboItem<Integer>> cmbJefe;

    // Botones
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;

    public PanelEmpleado(EmpleadoUseCasePort empleadoUseCase, OficinaUseCasePort oficinaUseCase) {
        this.empleadoUseCase = empleadoUseCase;
        this.oficinaUseCase = oficinaUseCase;

        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarCombos();
        cargarEmpleadosEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("Gestión del Personal y Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(true);
        splitPane.setBackground(UIManager.getColor("Panel.background"));
        splitPane.setResizeWeight(0.38);
        splitPane.setContinuousLayout(true);

        splitPane.setLeftComponent(createFormularioPanel());
        splitPane.setRightComponent(createTablaPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(new TitledBorder("Datos del Empleado"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtCodigoEmpleado = new JTextField(12);
        txtNombre = new JTextField(12);
        txtApellido1 = new JTextField(12);
        txtApellido2 = new JTextField(12);
        txtExtension = new JTextField(12);
        txtEmail = new JTextField(12);
        txtPuesto = new JTextField(12);
        cmbOficina = new JComboBox<>();
        cmbJefe = new JComboBox<>();

        int row = 0;
        addFormField(formGrid, gbc, "Código Empleado (*):", txtCodigoEmpleado, row++);
        addFormField(formGrid, gbc, "Nombre (*):", txtNombre, row++);
        addFormField(formGrid, gbc, "Primer Apellido (*):", txtApellido1, row++);
        addFormField(formGrid, gbc, "Segundo Apellido:", txtApellido2, row++);
        addFormField(formGrid, gbc, "Extensión Tel. (*):", txtExtension, row++);
        addFormField(formGrid, gbc, "Email (*):", txtEmail, row++);
        addFormField(formGrid, gbc, "Puesto / Cargo:", txtPuesto, row++);
        addFormField(formGrid, gbc, "Oficina Asignada (*):", cmbOficina, row++);
        addFormField(formGrid, gbc, "Jefe Directo:", cmbJefe, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Botones de acción
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

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
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
        panel.setBorder(new TitledBorder("Nómina y Directorio de Empleados"));

        String[] columnas = {"Código", "Nombre Completo", "Puesto", "Extensión", "Email", "Oficina", "Jefe"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.getTableHeader().setReorderingAllowed(false);

        tablaEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tablaEmpleados.getSelectedRow() != -1) {
                    cargarEmpleadoSeleccionado();
                }
            }
        });

        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEmpleados.getSelectedRow() != -1) {
                cargarEmpleadoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaEmpleados);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRecargar = new JButton("Recargar Empleados");
        btnRecargar.addActionListener(e -> {
            cargarCombos();
            cargarEmpleadosEnTabla();
        });
        panelTop.add(btnRecargar);
        panel.add(panelTop, BorderLayout.NORTH);

        return panel;
    }

    public void cargarCombos() {
        // Cargar Oficinas
        cmbOficina.removeAllItems();
        try {
            if (oficinaUseCase != null) {
                List<Oficina> oficinas = oficinaUseCase.listarOficinas();
                for (Oficina ofi : oficinas) {
                    String desc = ofi.getCodigoOficina() + " - " + ofi.getCiudad() + " (" + ofi.getPais() + ")";
                    cmbOficina.addItem(new ComboItem<>(ofi.getCodigoOficina(), desc));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar selector de oficinas: " + ex.getMessage());
        }

        // Cargar Jefes (Empleados)
        cmbJefe.removeAllItems();
        cmbJefe.addItem(new ComboItem<>(null, "-- Sin Jefe (Directivo) --"));
        try {
            List<Empleado> empleados = empleadoUseCase.listarEmpleados();
            for (Empleado emp : empleados) {
                String desc = "#" + emp.getCodigoEmpleado() + " - " + emp.getNombre() + " " + emp.getApellido1() +
                        (emp.getPuesto() != null ? " (" + emp.getPuesto() + ")" : "");
                cmbJefe.addItem(new ComboItem<>(emp.getCodigoEmpleado(), desc));
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar selector de jefes: " + ex.getMessage());
        }
    }

    public void cargarEmpleadosEnTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Empleado> lista = empleadoUseCase.listarEmpleados();
            for (Empleado emp : lista) {
                String nombreCompleto = emp.getNombre() + " " + emp.getApellido1() +
                        (emp.getApellido2() != null && !emp.getApellido2().isEmpty() ? " " + emp.getApellido2() : "");

                String oficinaStr = emp.getOficina() != null && emp.getOficina().getCodigoOficina() != null
                        ? emp.getOficina().getCodigoOficina() : "-";

                String jefeStr = emp.getJefe() != null && emp.getJefe().getCodigoEmpleado() != null
                        ? "Jefe #" + emp.getJefe().getCodigoEmpleado() : "Directivo";

                modeloTabla.addRow(new Object[]{
                        emp.getCodigoEmpleado(),
                        nombreCompleto,
                        emp.getPuesto() != null ? emp.getPuesto() : "-",
                        emp.getExtension(),
                        emp.getEmail(),
                        oficinaStr,
                        jefeStr
                });
            }
        } catch (Exception ex) {
            mostrarError("Error al Cargar Nómina", "No fue posible listar empleados: " + ex.getMessage());
        }
    }

    private void cargarEmpleadoSeleccionado() {
        int selectedRow = tablaEmpleados.getSelectedRow();
        if (selectedRow == -1) return;

        Object idObj = modeloTabla.getValueAt(selectedRow, 0);
        if (idObj == null) return;
        Integer codigo = (Integer) idObj;

        empleadoUseCase.obtenerEmpleadoPorId(codigo).ifPresent(emp -> {
            txtCodigoEmpleado.setText(String.valueOf(emp.getCodigoEmpleado()));
            txtCodigoEmpleado.setEnabled(false); // Llave primaria protegida

            txtNombre.setText(emp.getNombre());
            txtApellido1.setText(emp.getApellido1());
            txtApellido2.setText(emp.getApellido2() != null ? emp.getApellido2() : "");
            txtExtension.setText(emp.getExtension());
            txtEmail.setText(emp.getEmail());
            txtPuesto.setText(emp.getPuesto() != null ? emp.getPuesto() : "");

            // Seleccionar Oficina en JComboBox
            if (emp.getOficina() != null && emp.getOficina().getCodigoOficina() != null) {
                seleccionarOficina(emp.getOficina().getCodigoOficina());
            }

            // Seleccionar Jefe en JComboBox
            if (emp.getJefe() != null && emp.getJefe().getCodigoEmpleado() != null) {
                seleccionarJefe(emp.getJefe().getCodigoEmpleado());
            } else {
                cmbJefe.setSelectedIndex(0);
            }
        });
    }

    private void seleccionarOficina(String codigoOficina) {
        for (int i = 0; i < cmbOficina.getItemCount(); i++) {
            ComboItem<String> item = cmbOficina.getItemAt(i);
            if (item != null && codigoOficina.equalsIgnoreCase(item.value())) {
                cmbOficina.setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarJefe(Integer codigoJefe) {
        for (int i = 0; i < cmbJefe.getItemCount(); i++) {
            ComboItem<Integer> item = cmbJefe.getItemAt(i);
            if (item != null && item.value() != null && item.value().equals(codigoJefe)) {
                cmbJefe.setSelectedIndex(i);
                break;
            }
        }
    }

    private void accionGuardar() {
        try {
            Empleado emp = construirEmpleadoDesdeFormulario();
            empleadoUseCase.registrarEmpleado(emp);

            JOptionPane.showMessageDialog(this,
                    "Empleado #" + emp.getCodigoEmpleado() + " (" + emp.getNombre() + " " + emp.getApellido1() + ") registrado exitosamente.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarCombos();
            cargarEmpleadosEnTabla();

        } catch (NumberFormatException ex) {
            mostrarError("Formato Inválido", "El código de empleado debe ser un número entero válido.");
        } catch (BusinessRuleException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Base de Datos", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionActualizar() {
        String codigoTexto = txtCodigoEmpleado.getText().trim();
        if (codigoTexto.isEmpty()) {
            mostrarError("Validación", "Selecciona un empleado de la tabla para actualizar.");
            return;
        }

        try {
            Empleado emp = construirEmpleadoDesdeFormulario();
            empleadoUseCase.actualizarEmpleado(emp);

            JOptionPane.showMessageDialog(this,
                    "Empleado #" + emp.getCodigoEmpleado() + " actualizado exitosamente.",
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarCombos();
            cargarEmpleadosEnTabla();

        } catch (NumberFormatException ex) {
            mostrarError("Formato Inválido", "El código de empleado debe ser un número entero.");
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Validación", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Base de Datos", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String codigoTexto = txtCodigoEmpleado.getText().trim();
        if (codigoTexto.isEmpty()) {
            mostrarError("Validación", "Selecciona un empleado de la tabla para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al empleado #" + codigoTexto + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int codigo = Integer.parseInt(codigoTexto);
                empleadoUseCase.eliminarEmpleado(codigo);

                JOptionPane.showMessageDialog(this,
                        "Empleado #" + codigo + " eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarCombos();
                cargarEmpleadosEnTabla();

            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                mostrarError("Restricción", ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") || ex.getMessage().contains("1451"))) {
                    mostrarError("Restricción de Integridad",
                            "No se puede eliminar este empleado porque es representante de ventas de clientes o jefe de otros empleados (Error 1451).");
                } else {
                    mostrarError("Error de Base de Datos", ex.getMessage());
                }
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Empleado construirEmpleadoDesdeFormulario() {
        String codTexto = txtCodigoEmpleado.getText().trim();
        String nom = txtNombre.getText().trim();
        String ap1 = txtApellido1.getText().trim();
        String ap2 = txtApellido2.getText().trim();
        String ext = txtExtension.getText().trim();
        String email = txtEmail.getText().trim();
        String puesto = txtPuesto.getText().trim();

        if (codTexto.isEmpty()) throw new BusinessRuleException("El código de empleado es obligatorio.");
        if (nom.isEmpty()) throw new BusinessRuleException("El nombre es obligatorio.");
        if (ap1.isEmpty()) throw new BusinessRuleException("El primer apellido es obligatorio.");
        if (ext.isEmpty()) throw new BusinessRuleException("La extensión telefónica es obligatoria.");
        if (email.isEmpty()) throw new BusinessRuleException("El email es obligatorio.");

        int codigo = Integer.parseInt(codTexto);

        // Oficina seleccionada
        ComboItem<String> ofiItem = (ComboItem<String>) cmbOficina.getSelectedItem();
        if (ofiItem == null || ofiItem.value() == null) {
            throw new BusinessRuleException("Debe seleccionar una oficina válida para el empleado.");
        }
        Oficina ofi = Oficina.builder().codigoOficina(ofiItem.value()).build();

        // Jefe seleccionado
        ComboItem<Integer> jefeItem = (ComboItem<Integer>) cmbJefe.getSelectedItem();
        Empleado jefe = null;
        if (jefeItem != null && jefeItem.value() != null) {
            jefe = Empleado.builder().codigoEmpleado(jefeItem.value()).build();
        }

        return Empleado.builder()
                .codigoEmpleado(codigo)
                .nombre(nom)
                .apellido1(ap1)
                .apellido2(ap2.isEmpty() ? null : ap2)
                .extension(ext)
                .email(email)
                .oficina(ofi)
                .jefe(jefe)
                .puesto(puesto.isEmpty() ? null : puesto)
                .build();
    }

    private void limpiarFormulario() {
        txtCodigoEmpleado.setText("");
        txtCodigoEmpleado.setEnabled(true);
        txtNombre.setText("");
        txtApellido1.setText("");
        txtApellido2.setText("");
        txtExtension.setText("");
        txtEmail.setText("");
        txtPuesto.setText("");
        if (cmbOficina.getItemCount() > 0) cmbOficina.setSelectedIndex(0);
        if (cmbJefe.getItemCount() > 0) cmbJefe.setSelectedIndex(0);
        tablaEmpleados.clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Helper record para poblar JComboBox con par valor-etiqueta.
     */
    public record ComboItem<T>(T value, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}

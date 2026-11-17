package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.OficinaUseCasePort;
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
 * Panel de interfaz gráfica para la gestión CRUD de Oficinas.
 * Adaptador de entrada primario conectado al puerto OficinaUseCasePort.
 */
public class PanelOficina extends JPanel {

    private final OficinaUseCasePort oficinaUseCase;

    // Componentes del formulario
    private JTextField txtCodigoOficina;
    private JTextField txtCiudad;
    private JTextField txtPais;
    private JTextField txtRegion;
    private JTextField txtCodigoPostal;
    private JTextField txtTelefono;
    private JTextField txtLineaDireccion1;
    private JTextField txtLineaDireccion2;

    // Botones
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla
    private JTable tablaOficinas;
    private DefaultTableModel modeloTabla;

    public PanelOficina(OficinaUseCasePort oficinaUseCase) {
        this.oficinaUseCase = oficinaUseCase;
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarOficinasEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("Gestión de Oficinas y Sedes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor (Formulario a la izquierda, Tabla a la derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(true);
        splitPane.setBackground(UIManager.getColor("Panel.background"));
        splitPane.setResizeWeight(0.35);
        splitPane.setContinuousLayout(true);

        splitPane.setLeftComponent(createFormularioPanel());
        splitPane.setRightComponent(createTablaPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(new TitledBorder("Datos de la Oficina"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtCodigoOficina = new JTextField(12);
        txtCiudad = new JTextField(12);
        txtPais = new JTextField(12);
        txtRegion = new JTextField(12);
        txtCodigoPostal = new JTextField(12);
        txtTelefono = new JTextField(12);
        txtLineaDireccion1 = new JTextField(12);
        txtLineaDireccion2 = new JTextField(12);

        int row = 0;
        addFormField(formGrid, gbc, "Código Oficina (*):", txtCodigoOficina, row++);
        addFormField(formGrid, gbc, "Ciudad (*):", txtCiudad, row++);
        addFormField(formGrid, gbc, "País (*):", txtPais, row++);
        addFormField(formGrid, gbc, "Región:", txtRegion, row++);
        addFormField(formGrid, gbc, "Código Postal (*):", txtCodigoPostal, row++);
        addFormField(formGrid, gbc, "Teléfono (*):", txtTelefono, row++);
        addFormField(formGrid, gbc, "Dirección 1 (*):", txtLineaDireccion1, row++);
        addFormField(formGrid, gbc, "Dirección 2:", txtLineaDireccion2, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Barra de botones CRUD
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
        panel.setBorder(new TitledBorder("Listado de Oficinas"));

        String[] columnas = {"Código", "Ciudad", "País", "Región", "Teléfono", "Código Postal", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaOficinas = new JTable(modeloTabla);
        tablaOficinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOficinas.getTableHeader().setReorderingAllowed(false);

        tablaOficinas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablaOficinas.getSelectedRow();
                if (row != -1) {
                    cargarOficinaSeleccionada();
                }
            }
        });

        tablaOficinas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaOficinas.getSelectedRow() != -1) {
                cargarOficinaSeleccionada();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaOficinas);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRecargar = new JButton("Recargar Oficinas");
        btnRecargar.addActionListener(e -> cargarOficinasEnTabla());
        panelTop.add(btnRecargar);
        panel.add(panelTop, BorderLayout.NORTH);

        return panel;
    }

    public void cargarOficinasEnTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Oficina> lista = oficinaUseCase.listarOficinas();
            for (Oficina ofi : lista) {
                modeloTabla.addRow(new Object[]{
                        ofi.getCodigoOficina(),
                        ofi.getCiudad(),
                        ofi.getPais(),
                        ofi.getRegion() != null ? ofi.getRegion() : "-",
                        ofi.getTelefono(),
                        ofi.getCodigoPostal(),
                        ofi.getLineaDireccion1()
                });
            }
        } catch (Exception ex) {
            mostrarError("Error al Cargar Oficinas", "No fue posible listar las oficinas: " + ex.getMessage());
        }
    }

    private void cargarOficinaSeleccionada() {
        int selectedRow = tablaOficinas.getSelectedRow();
        if (selectedRow == -1) return;

        String codigo = (String) modeloTabla.getValueAt(selectedRow, 0);
        oficinaUseCase.obtenerOficinaPorId(codigo).ifPresent(ofi -> {
            txtCodigoOficina.setText(ofi.getCodigoOficina());
            txtCodigoOficina.setEnabled(false); // Llave primaria protegida

            txtCiudad.setText(ofi.getCiudad());
            txtPais.setText(ofi.getPais());
            txtRegion.setText(ofi.getRegion() != null ? ofi.getRegion() : "");
            txtCodigoPostal.setText(ofi.getCodigoPostal());
            txtTelefono.setText(ofi.getTelefono());
            txtLineaDireccion1.setText(ofi.getLineaDireccion1());
            txtLineaDireccion2.setText(ofi.getLineaDireccion2() != null ? ofi.getLineaDireccion2() : "");
        });
    }

    private void accionGuardar() {
        try {
            Oficina ofi = construirOficinaDesdeFormulario();
            oficinaUseCase.registrarOficina(ofi);

            JOptionPane.showMessageDialog(this,
                    "Oficina '" + ofi.getCodigoOficina() + "' registrada exitosamente.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarOficinasEnTabla();

        } catch (BusinessRuleException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Base de Datos", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionActualizar() {
        String codigo = txtCodigoOficina.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Validación", "Selecciona una oficina de la tabla para actualizarla.");
            return;
        }

        try {
            Oficina ofi = construirOficinaDesdeFormulario();
            oficinaUseCase.actualizarOficina(ofi);

            JOptionPane.showMessageDialog(this,
                    "Oficina '" + ofi.getCodigoOficina() + "' actualizada exitosamente.",
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarOficinasEnTabla();

        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            mostrarError("Validación", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Base de Datos", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String codigo = txtCodigoOficina.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Validación", "Selecciona una oficina de la tabla para eliminarla.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la oficina con código '" + codigo + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                oficinaUseCase.eliminarOficina(codigo);

                JOptionPane.showMessageDialog(this,
                        "Oficina '" + codigo + "' eliminada exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarOficinasEnTabla();

            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                mostrarError("Restricción", ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getMessage() != null && (ex.getMessage().contains("foreign key") || ex.getMessage().contains("1451"))) {
                    mostrarError("Restricción de Integridad",
                            "No se puede eliminar la oficina porque tiene empleados asignados en MySQL (Error 1451).");
                } else {
                    mostrarError("Error de Base de Datos", ex.getMessage());
                }
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Oficina construirOficinaDesdeFormulario() {
        String codigo = txtCodigoOficina.getText().trim();
        String ciudad = txtCiudad.getText().trim();
        String pais = txtPais.getText().trim();
        String region = txtRegion.getText().trim();
        String cp = txtCodigoPostal.getText().trim();
        String tel = txtTelefono.getText().trim();
        String dir1 = txtLineaDireccion1.getText().trim();
        String dir2 = txtLineaDireccion2.getText().trim();

        if (codigo.isEmpty()) throw new BusinessRuleException("El código de oficina es obligatorio.");
        if (ciudad.isEmpty()) throw new BusinessRuleException("La ciudad es obligatoria.");
        if (pais.isEmpty()) throw new BusinessRuleException("El país es obligatorio.");
        if (tel.isEmpty()) throw new BusinessRuleException("El teléfono es obligatorio.");
        if (cp.isEmpty()) throw new BusinessRuleException("El código postal es obligatorio.");
        if (dir1.isEmpty()) throw new BusinessRuleException("La dirección principal es obligatoria.");

        return Oficina.builder()
                .codigoOficina(codigo)
                .ciudad(ciudad)
                .pais(pais)
                .region(region.isEmpty() ? null : region)
                .codigoPostal(cp)
                .telefono(tel)
                .lineaDireccion1(dir1)
                .lineaDireccion2(dir2.isEmpty() ? null : dir2)
                .build();
    }

    private void limpiarFormulario() {
        txtCodigoOficina.setText("");
        txtCodigoOficina.setEnabled(true);
        txtCiudad.setText("");
        txtPais.setText("");
        txtRegion.setText("");
        txtCodigoPostal.setText("");
        txtTelefono.setText("");
        txtLineaDireccion1.setText("");
        txtLineaDireccion2.setText("");
        tablaOficinas.clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}

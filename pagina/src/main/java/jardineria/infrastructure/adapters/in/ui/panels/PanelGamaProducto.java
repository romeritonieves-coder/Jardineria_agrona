package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.GamaProductoUseCasePort;
import jardineria.domain.model.GamaProducto;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión CRUD y catálogo de Gamas de Productos.
 * Adaptador de entrada primario conectado al puerto GamaProductoUseCasePort.
 */
public class PanelGamaProducto extends JPanel {

    private final GamaProductoUseCasePort gamaUseCase;

    // Componentes del formulario
    private JTextField txtGama;
    private JTextArea txtDescripcionTexto;
    private JTextArea txtDescripcionHtml;
    private JTextField txtImagen;

    // Barra de búsqueda en tiempo real
    private JTextField txtBuscar;

    // Botones de acción
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla y modelo
    private JTable tablaGamas;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PanelGamaProducto(GamaProductoUseCasePort gamaUseCase) {
        this.gamaUseCase = gamaUseCase;
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarGamasEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // 1. Título y Cabecera del Módulo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Gamas y Categorías de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0x2E, 0xCC, 0x71));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Panel Divisor (Formulario a la izquierda, Tabla con buscador a la derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(true);
        splitPane.setBackground(UIManager.getColor("Panel.background"));
        splitPane.setResizeWeight(0.36);
        splitPane.setContinuousLayout(true);

        splitPane.setLeftComponent(createFormularioPanel());
        splitPane.setRightComponent(createTablaPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBorder(new TitledBorder("Datos de la Gama de Producto"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtGama = new JTextField(15);
        txtDescripcionTexto = new JTextArea(4, 15);
        txtDescripcionTexto.setLineWrap(true);
        txtDescripcionTexto.setWrapStyleWord(true);
        JScrollPane scrollTexto = new JScrollPane(txtDescripcionTexto);
        scrollTexto.setPreferredSize(new Dimension(180, 80));

        txtDescripcionHtml = new JTextArea(4, 15);
        txtDescripcionHtml.setLineWrap(true);
        txtDescripcionHtml.setWrapStyleWord(true);
        JScrollPane scrollHtml = new JScrollPane(txtDescripcionHtml);
        scrollHtml.setPreferredSize(new Dimension(180, 80));

        txtImagen = new JTextField(15);

        int row = 0;
        addFormField(formGrid, gbc, "Código / Nombre Gama (*):", txtGama, row++);
        addFormAreaField(formGrid, gbc, "Descripción Texto:", scrollTexto, row++);
        addFormAreaField(formGrid, gbc, "Descripción HTML:", scrollHtml, row++);
        addFormField(formGrid, gbc, "Ruta / URL Imagen:", txtImagen, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Barra inferior de botones CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("JButton.buttonType", "default");
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setEnabled(false);
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
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
        JLabel lbl = new JLabel(label);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void addFormAreaField(JPanel panel, GridBagConstraints gbc, String label, JScrollPane scrollPane, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(scrollPane, gbc);
    }

    private JPanel createTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Listado de Gamas de Productos Registradas"));

        // Barra superior de la tabla: Buscador en tiempo real y botón de refresco
        JPanel panelTop = new JPanel(new BorderLayout(10, 5));
        panelTop.setBorder(new EmptyBorder(4, 6, 8, 6));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelBusqueda.setOpaque(false);
        JLabel lblBuscar = new JLabel("Filtrar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtBuscar = new JTextField(18);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escribe para filtrar gamas...");
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        JPanel panelAccionesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelAccionesTop.setOpaque(false);
        JButton btnRecargar = new JButton("Recargar Gamas");
        btnRecargar.addActionListener(e -> cargarGamasEnTabla());
        panelAccionesTop.add(btnRecargar);

        panelTop.add(panelBusqueda, BorderLayout.WEST);
        panelTop.add(panelAccionesTop, BorderLayout.EAST);
        panel.add(panelTop, BorderLayout.NORTH);

        // Definición de columnas de la tabla
        String[] columnas = {"Gama / Categoría", "Descripción Texto", "Descripción HTML", "Imagen"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaGamas = new JTable(modeloTabla);
        tablaGamas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaGamas.getTableHeader().setReorderingAllowed(false);
        tablaGamas.setRowHeight(24);

        // Habilitar clasificador y filtro dinámico en la tabla
        rowSorter = new TableRowSorter<>(modeloTabla);
        tablaGamas.setRowSorter(rowSorter);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }
        });

        // Eventos de interacción de selección en la tabla
        tablaGamas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablaGamas.getSelectedRow();
                if (row != -1) {
                    cargarGamaSeleccionada();
                }
            }
        });

        tablaGamas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaGamas.getSelectedRow() != -1) {
                cargarGamaSeleccionada();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaGamas);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }

    public void cargarGamasEnTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<GamaProducto> lista = gamaUseCase.listarGamas();
            for (GamaProducto g : lista) {
                modeloTabla.addRow(new Object[]{
                        g.getGama(),
                        g.getDescripcionTexto() != null ? g.getDescripcionTexto() : "",
                        g.getDescripcionHtml() != null ? g.getDescripcionHtml() : "",
                        g.getImagen() != null ? g.getImagen() : ""
                });
            }
        } catch (Exception ex) {
            if (ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(this,
                        "Error de base de datos al cargar las gamas:\n" + ex.getMessage(),
                        "Error de Conexión",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error inesperado al cargar las gamas:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarGamaSeleccionada() {
        int selectedRow = tablaGamas.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = tablaGamas.convertRowIndexToModel(selectedRow);

        String gama = (String) modeloTabla.getValueAt(modelRow, 0);
        String descTexto = (String) modeloTabla.getValueAt(modelRow, 1);
        String descHtml = (String) modeloTabla.getValueAt(modelRow, 2);
        String imagen = (String) modeloTabla.getValueAt(modelRow, 3);

        txtGama.setText(gama);
        txtDescripcionTexto.setText(descTexto);
        txtDescripcionHtml.setText(descHtml);
        txtImagen.setText(imagen);

        // Bloquear edición de la clave primaria para operaciones de actualización
        txtGama.setEditable(false);
        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void accionGuardar() {
        try {
            validarCamposObligatorios();

            GamaProducto nuevaGama = GamaProducto.builder()
                    .gama(txtGama.getText().trim())
                    .descripcionTexto(txtDescripcionTexto.getText().trim().isEmpty() ? null : txtDescripcionTexto.getText().trim())
                    .descripcionHtml(txtDescripcionHtml.getText().trim().isEmpty() ? null : txtDescripcionHtml.getText().trim())
                    .imagen(txtImagen.getText().trim().isEmpty() ? null : txtImagen.getText().trim())
                    .build();

            gamaUseCase.registrarGama(nuevaGama);

            JOptionPane.showMessageDialog(this,
                    "Gama de producto '" + nuevaGama.getGama() + "' registrada exitosamente.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarGamasEnTabla();

        } catch (Exception ex) {
            if (ex instanceof BusinessRuleException) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Regla de Negocio Incumplida",
                        JOptionPane.WARNING_MESSAGE);
            } else if (ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar en la base de datos:\n" + ex.getMessage(),
                        "Error de Persistencia",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error inesperado: " + ex.getMessage(),
                        "Error del Sistema",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionActualizar() {
        try {
            validarCamposObligatorios();

            GamaProducto gamaActualizada = GamaProducto.builder()
                    .gama(txtGama.getText().trim())
                    .descripcionTexto(txtDescripcionTexto.getText().trim().isEmpty() ? null : txtDescripcionTexto.getText().trim())
                    .descripcionHtml(txtDescripcionHtml.getText().trim().isEmpty() ? null : txtDescripcionHtml.getText().trim())
                    .imagen(txtImagen.getText().trim().isEmpty() ? null : txtImagen.getText().trim())
                    .build();

            gamaUseCase.actualizarGama(gamaActualizada);

            JOptionPane.showMessageDialog(this,
                    "Gama de producto '" + gamaActualizada.getGama() + "' actualizada correctamente.",
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarGamasEnTabla();

        } catch (Exception ex) {
            if (ex instanceof BusinessRuleException || ex instanceof ResourceNotFoundException) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
            } else if (ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(this,
                        "Error de base de datos al actualizar:\n" + ex.getMessage(),
                        "Error de Persistencia",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error inesperado: " + ex.getMessage(),
                        "Error del Sistema",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionEliminar() {
        String gamaId = txtGama.getText().trim();
        if (gamaId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor selecciona una gama de la tabla para eliminar.",
                    "Selección Requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar permanentemente la gama '" + gamaId + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                gamaUseCase.eliminarGama(gamaId);

                JOptionPane.showMessageDialog(this,
                        "Gama '" + gamaId + "' eliminada correctamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarGamasEnTabla();

            } catch (Exception ex) {
                if (ex instanceof BusinessRuleException) {
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(),
                            "Restricción de Integridad",
                            JOptionPane.WARNING_MESSAGE);
                } else if (ex instanceof ResourceNotFoundException) {
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(),
                            "No Encontrado",
                            JOptionPane.WARNING_MESSAGE);
                } else if (ex instanceof PersistenceException) {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar en la base de datos:\n" + ex.getMessage(),
                            "Error de Persistencia",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error inesperado: " + ex.getMessage(),
                            "Error del Sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limpiarFormulario() {
        txtGama.setText("");
        txtGama.setEditable(true);
        txtDescripcionTexto.setText("");
        txtDescripcionHtml.setText("");
        txtImagen.setText("");

        tablaGamas.clearSelection();
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void validarCamposObligatorios() {
        if (txtGama.getText().trim().isEmpty()) {
            throw new BusinessRuleException("El Código / Nombre de la Gama es un campo obligatorio.");
        }
    }
}


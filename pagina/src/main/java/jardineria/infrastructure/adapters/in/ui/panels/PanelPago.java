package jardineria.infrastructure.adapters.in.ui.panels;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.application.ports.in.PagoUseCasePort;
import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pago;
import jardineria.infrastructure.adapters.in.ui.DashboardFrame;
import jardineria.infrastructure.adapters.out.persistence.PersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel de interfaz gráfica para la gestión y registro de Pagos y Transacciones.
 * Adaptador de entrada primario conectado a PagoUseCasePort y ClienteUseCasePort.
 */
public class PanelPago extends JPanel {

    private final PagoUseCasePort pagoUseCase;
    private final ClienteUseCasePort clienteUseCase;

    // Componentes del formulario
    private JTextField txtIdTransaccion;
    private JComboBox<ComboItem<Integer>> cmbCliente;
    private JComboBox<String> cmbFormaPago;
    private JTextField txtFechaPago;
    private JTextField txtTotal;

    // Botones
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla y Métricas
    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalAcumulado;

    public PanelPago(PagoUseCasePort pagoUseCase, ClienteUseCasePort clienteUseCase) {
        this.pagoUseCase = pagoUseCase;
        this.clienteUseCase = clienteUseCase;

        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        cargarClientesEnCombo();
        cargarPagosEnTabla();
        DashboardFrame.hacerComponentesOpacos(this);
    }

    private void initComponents() {
        // Título del módulo
        JLabel lblTitulo = new JLabel("Gestión y Registro de Pagos Comerciales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel divisor
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
        container.setBorder(new TitledBorder("Datos de la Transacción de Pago"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtIdTransaccion = new JTextField(14);
        cmbCliente = new JComboBox<>();
        cmbFormaPago = new JComboBox<>(new String[]{"PayPal", "Tarjeta de crédito", "Transferencia", "Cheque"});
        txtFechaPago = new JTextField(LocalDate.now().toString(), 14);
        txtTotal = new JTextField(14);

        int row = 0;
        addFormField(formGrid, gbc, "ID Transacción (*):", txtIdTransaccion, row++);
        addFormField(formGrid, gbc, "Cliente Destino (*):", cmbCliente, row++);
        addFormField(formGrid, gbc, "Forma de Pago (*):", cmbFormaPago, row++);
        addFormField(formGrid, gbc, "Fecha Pago (YYYY-MM-DD):", txtFechaPago, row++);
        addFormField(formGrid, gbc, "Monto Total ($) (*):", txtTotal, row++);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        container.add(scrollForm, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnGuardar = new JButton("Registrar Pago");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnGuardar.addActionListener(e -> accionGuardar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        container.add(panelBotones, BorderLayout.SOUTH);

        return container;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.38;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.62;
        panel.add(field, gbc);
    }

    private JPanel createTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Historial de Pagos y Cobros"));

        String[] columnas = {"ID Transacción", "Cód. Cliente", "Forma de Pago", "Fecha Pago", "Total Pagado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPagos = new JTable(modeloTabla);
        tablaPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPagos.getTableHeader().setReorderingAllowed(false);

        tablaPagos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tablaPagos.getSelectedRow() != -1) {
                    cargarPagoSeleccionado();
                }
            }
        });

        tablaPagos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPagos.getSelectedRow() != -1) {
                cargarPagoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPagos);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Barra superior con botones de recarga y total
        JPanel panelTop = new JPanel(new BorderLayout(10, 4));
        lblTotalAcumulado = new JLabel("Recaudación Total: $0.00");
        lblTotalAcumulado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalAcumulado.setForeground(new Color(0x2E, 0xCC, 0x71));
        lblTotalAcumulado.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnRecargar = new JButton("Recargar Pagos");
        btnRecargar.addActionListener(e -> {
            cargarClientesEnCombo();
            cargarPagosEnTabla();
        });
        panelAcciones.add(btnRecargar);

        panelTop.add(lblTotalAcumulado, BorderLayout.WEST);
        panelTop.add(panelAcciones, BorderLayout.EAST);
        panel.add(panelTop, BorderLayout.NORTH);

        return panel;
    }

    public void cargarClientesEnCombo() {
        cmbCliente.removeAllItems();
        try {
            if (clienteUseCase != null) {
                List<Cliente> clientes = clienteUseCase.obtenerTodosLosClientes();
                for (Cliente cli : clientes) {
                    String desc = "#" + cli.getCodigoCliente() + " - " + cli.getNombreCliente();
                    cmbCliente.addItem(new ComboItem<>(cli.getCodigoCliente(), desc));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar selector de clientes: " + ex.getMessage());
        }
    }

    public void cargarPagosEnTabla() {
        modeloTabla.setRowCount(0);
        BigDecimal totalSuma = BigDecimal.ZERO;

        try {
            List<Pago> lista = pagoUseCase.listarPagos();
            for (Pago p : lista) {
                Integer codCli = p.getCliente() != null ? p.getCliente().getCodigoCliente() : null;
                BigDecimal monto = p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO;
                totalSuma = totalSuma.add(monto);

                modeloTabla.addRow(new Object[]{
                        p.getIdTransaccion(),
                        codCli != null ? codCli : "-",
                        p.getFormaPago(),
                        p.getFechaPago() != null ? p.getFechaPago().toString() : "-",
                        "$" + monto
                });
            }
            lblTotalAcumulado.setText(String.format("Recaudación Total: $%,.2f (%d transacciones)", totalSuma, lista.size()));
        } catch (Exception ex) {
            mostrarError("Error al Cargar Pagos", "No fue posible listar pagos: " + ex.getMessage());
        }
    }

    private void cargarPagoSeleccionado() {
        int selectedRow = tablaPagos.getSelectedRow();
        if (selectedRow == -1) return;

        String idTransaccion = (String) modeloTabla.getValueAt(selectedRow, 0);
        pagoUseCase.obtenerPagoPorId(idTransaccion).ifPresent(p -> {
            txtIdTransaccion.setText(p.getIdTransaccion());
            txtIdTransaccion.setEnabled(false); // Llave primaria protegida

            if (p.getCliente() != null && p.getCliente().getCodigoCliente() != null) {
                seleccionarCliente(p.getCliente().getCodigoCliente());
            }

            if (p.getFormaPago() != null) {
                cmbFormaPago.setSelectedItem(p.getFormaPago());
            }

            txtFechaPago.setText(p.getFechaPago() != null ? p.getFechaPago().toString() : LocalDate.now().toString());
            txtTotal.setText(p.getTotal() != null ? p.getTotal().toPlainString() : "");
        });
    }

    private void seleccionarCliente(Integer codigoCliente) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            ComboItem<Integer> item = cmbCliente.getItemAt(i);
            if (item != null && item.value() != null && item.value().equals(codigoCliente)) {
                cmbCliente.setSelectedIndex(i);
                break;
            }
        }
    }

    private void accionGuardar() {
        try {
            Pago pago = construirPagoDesdeFormulario();
            pagoUseCase.registrarPago(pago);

            JOptionPane.showMessageDialog(this,
                    "Pago con ID '" + pago.getIdTransaccion() + "' registrado exitosamente por monto de $" + pago.getTotal() + ".",
                    "Pago Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarPagosEnTabla();

        } catch (DateTimeParseException ex) {
            mostrarError("Formato de Fecha Inválido", "La fecha de pago debe tener el formato YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            mostrarError("Monto Inválido", "El total del pago debe ser un número decimal válido.");
        } catch (BusinessRuleException ex) {
            mostrarError("Regla de Negocio", ex.getMessage());
        } catch (PersistenceException ex) {
            mostrarError("Error de Base de Datos", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
        }
    }

    private void accionEliminar() {
        String idTransaccion = txtIdTransaccion.getText().trim();
        if (idTransaccion.isEmpty()) {
            mostrarError("Validación", "Selecciona un pago de la tabla para eliminarlo.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular/eliminar el pago con transacción '" + idTransaccion + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pagoUseCase.eliminarPago(idTransaccion);

                JOptionPane.showMessageDialog(this,
                        "Pago '" + idTransaccion + "' eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarPagosEnTabla();

            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                mostrarError("Validación", ex.getMessage());
            } catch (PersistenceException ex) {
                mostrarError("Error de Base de Datos", ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Error Inesperado", ex.getMessage());
            }
        }
    }

    private Pago construirPagoDesdeFormulario() {
        String idTrans = txtIdTransaccion.getText().trim();
        String fechaStr = txtFechaPago.getText().trim();
        String totalStr = txtTotal.getText().trim();
        String formaPago = (String) cmbFormaPago.getSelectedItem();

        if (idTrans.isEmpty()) throw new BusinessRuleException("El ID de transacción es obligatorio.");
        if (totalStr.isEmpty()) throw new BusinessRuleException("El monto total es obligatorio.");

        ComboItem<Integer> clienteItem = (ComboItem<Integer>) cmbCliente.getSelectedItem();
        if (clienteItem == null || clienteItem.value() == null) {
            throw new BusinessRuleException("Debe seleccionar un cliente para el pago.");
        }

        BigDecimal total = new BigDecimal(totalStr);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El monto debe ser estrictamente mayor a 0.");
        }

        LocalDate fecha = fechaStr.isEmpty() ? LocalDate.now() : LocalDate.parse(fechaStr);

        return Pago.builder()
                .idTransaccion(idTrans)
                .cliente(Cliente.builder().codigoCliente(clienteItem.value()).build())
                .formaPago(formaPago != null ? formaPago : "Transferencia")
                .fechaPago(fecha)
                .total(total)
                .build();
    }

    private void limpiarFormulario() {
        txtIdTransaccion.setText("");
        txtIdTransaccion.setEnabled(true);
        if (cmbCliente.getItemCount() > 0) cmbCliente.setSelectedIndex(0);
        cmbFormaPago.setSelectedIndex(0);
        txtFechaPago.setText(LocalDate.now().toString());
        txtTotal.setText("");
        tablaPagos.clearSelection();
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

package jardineria.infrastructure.adapters.out.export;

import jardineria.application.exceptions.ExportException;
import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.ReporteExportPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptador de salida para la exportación de reportes a hojas de cálculo Microsoft Excel (.xlsx)
 * utilizando Apache POI con estilos limpios y profesionales.
 */
@Slf4j
public class ReporteExcelAdapter implements ReporteExportPort {

    @Override
    public void exportarClientesExcel(List<Cliente> clientes, File archivoDestino) {
        log.info("Iniciando exportación de {} clientes a archivo Excel: {}", clientes.size(), archivoDestino.getName());

        String[] cabeceras = {
                "Código", "Nombre Cliente / Empresa", "Nombre Contacto", "Apellido Contacto",
                "Teléfono", "Fax", "Dirección 1", "Dirección 2", "Ciudad", "Región",
                "País", "Código Postal", "Límite Crédito", "ID Rep. Ventas"
        };

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Listado de Clientes");
            sheet.setDisplayGridlines(true);

            CellStyle headerStyle = crearEstiloCabecera(workbook, IndexedColors.DARK_TEAL);
            CellStyle dataStyle = crearEstiloCelda(workbook);
            CellStyle currencyStyle = crearEstiloMoneda(workbook, dataStyle);
            CellStyle intStyle = crearEstiloEntero(workbook, dataStyle);

            // 1. Fila de Encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Filas de Datos
            int rowIdx = 1;
            for (Cliente c : clientes) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);

                crearCeldaEntero(row, 0, c.getCodigoCliente(), intStyle);
                crearCeldaTexto(row, 1, c.getNombreCliente(), dataStyle);
                crearCeldaTexto(row, 2, c.getNombreContacto(), dataStyle);
                crearCeldaTexto(row, 3, c.getApellidoContacto(), dataStyle);
                crearCeldaTexto(row, 4, c.getTelefono(), dataStyle);
                crearCeldaTexto(row, 5, c.getFax(), dataStyle);
                crearCeldaTexto(row, 6, c.getLineaDireccion1(), dataStyle);
                crearCeldaTexto(row, 7, c.getLineaDireccion2(), dataStyle);
                crearCeldaTexto(row, 8, c.getCiudad(), dataStyle);
                crearCeldaTexto(row, 9, c.getRegion(), dataStyle);
                crearCeldaTexto(row, 10, c.getPais(), dataStyle);
                crearCeldaTexto(row, 11, c.getCodigoPostal(), dataStyle);
                crearCeldaMoneda(row, 12, c.getLimiteCredito(), currencyStyle);

                Integer repVentas = (c.getEmpleadoRepVentas() != null)
                        ? c.getEmpleadoRepVentas().getCodigoEmpleado()
                        : null;
                crearCeldaEntero(row, 13, repVentas, intStyle);
            }

            // 3. Ajuste de ancho de columnas automático
            autoajustarColumnas(sheet, cabeceras.length);

            // 4. Escribir archivo físico
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                workbook.write(fos);
            }

            log.info("Reporte de clientes exportado exitosamente.");
        } catch (IOException e) {
            log.error("Error al exportar clientes a Excel: {}", e.getMessage(), e);
            throw new ExportException("No se pudo generar el archivo Excel de clientes: " + e.getMessage(), e);
        }
    }

    @Override
    public void exportarProductosExcel(List<Producto> productos, File archivoDestino) {
        log.info("Iniciando exportación de {} productos a archivo Excel: {}", productos.size(), archivoDestino.getName());

        String[] cabeceras = {
                "Código Producto", "Nombre", "Gama", "Dimensiones", "Proveedor",
                "Descripción", "Stock en Tienda", "Precio Venta", "Precio Proveedor"
        };

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Catálogo de Productos");
            sheet.setDisplayGridlines(true);

            CellStyle headerStyle = crearEstiloCabecera(workbook, IndexedColors.ROYAL_BLUE);
            CellStyle dataStyle = crearEstiloCelda(workbook);
            CellStyle currencyStyle = crearEstiloMoneda(workbook, dataStyle);
            CellStyle intStyle = crearEstiloEntero(workbook, dataStyle);

            // 1. Fila de Encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Filas de Datos
            int rowIdx = 1;
            for (Producto p : productos) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);

                crearCeldaTexto(row, 0, p.getCodigoProducto(), dataStyle);
                crearCeldaTexto(row, 1, p.getNombre(), dataStyle);
                crearCeldaTexto(row, 2, p.getGama() != null ? p.getGama().getGama() : "-", dataStyle);
                crearCeldaTexto(row, 3, p.getDimensiones(), dataStyle);
                crearCeldaTexto(row, 4, p.getProveedor(), dataStyle);
                crearCeldaTexto(row, 5, p.getDescripcion(), dataStyle);
                crearCeldaEntero(row, 6, p.getCantidadEnStock(), intStyle);
                crearCeldaMoneda(row, 7, p.getPrecioVenta(), currencyStyle);
                crearCeldaMoneda(row, 8, p.getPrecioProveedor(), currencyStyle);
            }

            // 3. Ajuste de ancho de columnas automático
            autoajustarColumnas(sheet, cabeceras.length);

            // 4. Escribir archivo físico
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                workbook.write(fos);
            }

            log.info("Reporte de productos exportado exitosamente.");
        } catch (IOException e) {
            log.error("Error al exportar productos a Excel: {}", e.getMessage(), e);
            throw new ExportException("No se pudo generar el archivo Excel de productos: " + e.getMessage(), e);
        }
    }

    @Override
    public void exportarPedidosExcel(List<Pedido> pedidos, File archivoDestino) {
        log.info("Iniciando exportación de {} pedidos a archivo Excel: {}", pedidos.size(), archivoDestino.getName());

        String[] cabeceras = {
                "Cód. Pedido", "Fecha Pedido", "Fecha Esperada", "Fecha Entrega",
                "Estado", "Cód. Cliente", "Comentarios"
        };

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historial de Pedidos");
            sheet.setDisplayGridlines(true);

            CellStyle headerStyle = crearEstiloCabecera(workbook, IndexedColors.MAROON);
            CellStyle dataStyle = crearEstiloCelda(workbook);
            CellStyle intStyle = crearEstiloEntero(workbook, dataStyle);

            // 1. Fila de Encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Filas de Datos
            int rowIdx = 1;
            for (Pedido p : pedidos) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);

                crearCeldaEntero(row, 0, p.getCodigoPedido(), intStyle);
                crearCeldaTexto(row, 1, p.getFechaPedido() != null ? p.getFechaPedido().toString() : "-", dataStyle);
                crearCeldaTexto(row, 2, p.getFechaEsperada() != null ? p.getFechaEsperada().toString() : "-", dataStyle);
                crearCeldaTexto(row, 3, p.getFechaEntrega() != null ? p.getFechaEntrega().toString() : "-", dataStyle);
                crearCeldaTexto(row, 4, p.getEstado(), dataStyle);

                Integer codCli = (p.getCliente() != null) ? p.getCliente().getCodigoCliente() : null;
                crearCeldaEntero(row, 5, codCli, intStyle);
                crearCeldaTexto(row, 6, p.getComentarios(), dataStyle);
            }

            // 3. Ajuste de columnas
            autoajustarColumnas(sheet, cabeceras.length);

            // 4. Escribir archivo físico
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                workbook.write(fos);
            }

            log.info("Reporte de pedidos exportado exitosamente.");
        } catch (IOException e) {
            log.error("Error al exportar pedidos a Excel: {}", e.getMessage(), e);
            throw new ExportException("No se pudo generar el archivo Excel de pedidos: " + e.getMessage(), e);
        }
    }

    // --- Métodos de Utilidad para Estilos Apache POI ---

    private CellStyle crearEstiloCabecera(Workbook workbook, IndexedColors colorFondo) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Segoe UI");

        style.setFont(font);
        style.setFillForegroundColor(colorFondo.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloCelda(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Segoe UI");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloMoneda(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloEntero(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void crearCeldaTexto(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : "-");
        cell.setCellStyle(style);
    }

    private void crearCeldaEntero(Row row, int col, Integer valor, CellStyle style) {
        Cell cell = row.createCell(col);
        if (valor != null) {
            cell.setCellValue(valor);
        } else {
            cell.setCellValue("-");
        }
        cell.setCellStyle(style);
    }

    private void crearCeldaMoneda(Row row, int col, BigDecimal valor, CellStyle style) {
        Cell cell = row.createCell(col);
        if (valor != null) {
            cell.setCellValue(valor.doubleValue());
        } else {
            cell.setCellValue(0.0);
        }
        cell.setCellStyle(style);
    }

    private void autoajustarColumnas(Sheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.max(width + 1200, 3200));
        }
    }
}


package jardineria.infrastructure.adapters.in.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Proveedor de iconos vectoriales ligeros y personalizados para AGRONA Jardinería.
 * Renderizados nativamente en Graphics2D con antialiasing para garantizar:
 * - 0 caracteres rotos (▢).
 * - Alta definición en cualquier resolución y DPI.
 * - Estilo limpio, moderno y minimalista en verde esmeralda y verde lima.
 */
public final class AgronaIcons {

    private AgronaIcons() {}

    public static Icon createHomeIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Techo
            GeneralPath roof = new GeneralPath();
            roof.moveTo(2, h * 0.48f);
            roof.lineTo(w * 0.5f, 2);
            roof.lineTo(w - 2, h * 0.48f);
            g2.draw(roof);
            // Cuerpo de la casa
            GeneralPath body = new GeneralPath();
            body.moveTo(w * 0.2f, h * 0.45f);
            body.lineTo(w * 0.2f, h - 2);
            body.lineTo(w * 0.8f, h - 2);
            body.lineTo(w * 0.8f, h * 0.45f);
            g2.draw(body);
            // Puerta
            g2.drawRect((int) (w * 0.42f), (int) (h * 0.62f), (int) (w * 0.16f), (int) (h * 0.38f));
        });
    }

    public static Icon createClientesIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Cabeza
            int r = (int) (w * 0.22f);
            g2.drawOval((int) (w * 0.5f - r), (int) (h * 0.12f), r * 2, r * 2);
            // Hombros / Torso
            GeneralPath body = new GeneralPath();
            body.moveTo(w * 0.18f, h - 2);
            body.curveTo(w * 0.22f, h * 0.62f, w * 0.78f, h * 0.62f, w * 0.82f, h - 2);
            g2.draw(body);
        });
    }

    public static Icon createPedidosIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Contorno de caja
            g2.drawRect(2, 3, w - 5, h - 6);
            // Líneas de pliegue de la caja
            g2.drawLine(2, (int) (h * 0.38f), w - 3, (int) (h * 0.38f));
            g2.drawLine((int) (w * 0.5f), (int) (h * 0.38f), (int) (w * 0.5f), h - 3);
        });
    }

    public static Icon createCatalogoIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Hoja botánica
            GeneralPath leaf = new GeneralPath();
            leaf.moveTo(w * 0.2f, h - 2);
            leaf.curveTo(w * 0.2f, h * 0.35f, w * 0.45f, 2, w - 2, 2);
            leaf.curveTo(w - 2, h * 0.55f, w * 0.65f, h * 0.8f, w * 0.2f, h - 2);
            g2.draw(leaf);
            // Nervio central
            g2.drawLine((int) (w * 0.2f), h - 2, (int) (w * 0.75f), (int) (h * 0.35f));
        });
    }

    public static Icon createGamasIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Etiqueta de precio / categoría
            GeneralPath tag = new GeneralPath();
            tag.moveTo(2, h * 0.45f);
            tag.lineTo(w * 0.45f, 2);
            tag.lineTo(w - 2, 2);
            tag.lineTo(w - 2, h * 0.55f);
            tag.lineTo(h * 0.55f, w - 2);
            tag.lineTo(2, h * 0.45f);
            g2.draw(tag);
            // Ojal
            g2.drawOval((int) (w * 0.72f), (int) (h * 0.22f), 3, 3);
        });
    }

    public static Icon createPagosIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Tarjeta
            g2.drawRoundRect(2, 3, w - 5, h - 7, 4, 4);
            // Banda magnética
            g2.drawLine(2, (int) (h * 0.42f), w - 3, (int) (h * 0.42f));
            // Chip
            g2.fillRect((int) (w * 0.22f), (int) (h * 0.60f), (int) (w * 0.18f), (int) (h * 0.18f));
        });
    }

    public static Icon createEmpleadosIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Carnet / Credencial
            g2.drawRoundRect(3, 2, w - 6, h - 4, 4, 4);
            // Clip superior
            g2.drawLine((int) (w * 0.4f), 2, (int) (w * 0.6f), 2);
            // Foto miniatura
            g2.drawRect((int) (w * 0.35f), (int) (h * 0.28f), (int) (w * 0.3f), (int) (h * 0.3f));
            // Líneas de texto
            g2.drawLine((int) (w * 0.25f), (int) (h * 0.72f), (int) (w * 0.75f), (int) (h * 0.72f));
            g2.drawLine((int) (w * 0.35f), (int) (h * 0.84f), (int) (w * 0.65f), (int) (h * 0.84f));
        });
    }

    public static Icon createOficinasIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Edificio principal
            g2.drawRect(3, 3, w - 7, h - 5);
            // Ventanas
            int numRows = 3;
            int numCols = 2;
            int winW = 2;
            int winH = 2;
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    int wx = 6 + (c * 6);
                    int wy = 6 + (r * 4);
                    g2.fillRect(wx, wy, winW, winH);
                }
            }
        });
    }

    public static Icon createRefreshIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Arco superior
            g2.drawArc(2, 2, w - 5, h - 5, 45, 230);
            // Flecha en la punta
            int ax = (int) (w * 0.82f);
            int ay = (int) (h * 0.32f);
            g2.drawLine(ax, ay, ax + 3, ay - 3);
            g2.drawLine(ax, ay, ax - 3, ay - 3);
        });
    }

    public static Icon createSearchIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Lente
            int r = (int) (w * 0.32f);
            g2.drawOval(2, 2, r * 2, r * 2);
            // Mango
            g2.drawLine((int) (r * 1.7f), (int) (r * 1.7f), w - 2, h - 2);
        });
    }

    public static Icon createCheckIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath p = new GeneralPath();
            p.moveTo(2, h * 0.52f);
            p.lineTo(w * 0.42f, h - 2);
            p.lineTo(w - 2, 2);
            g2.draw(p);
        });
    }

    public static Icon createArrowUpRightIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Línea diagonal
            g2.drawLine(2, h - 2, w - 2, 2);
            // Flecha
            g2.drawLine(w - 2, (int) (h * 0.45f), w - 2, 2);
            g2.drawLine((int) (w * 0.55f), 2, w - 2, 2);
        });
    }

    // Métodos convenientes con resolución directa
    public static Icon crearIconoHome(int w, int h) { return createHomeIcon(Math.min(w, h), new Color(0x22, 0xC5, 0x5E)); }
    public static Icon crearIconoClientes(int w, int h) { return createClientesIcon(Math.min(w, h), new Color(0x22, 0xC5, 0x5E)); }
    public static Icon crearIconoPedidos(int w, int h) { return createPedidosIcon(Math.min(w, h), new Color(0x34, 0xD3, 0x99)); }
    public static Icon crearIconoCatalogo(int w, int h) { return createCatalogoIcon(Math.min(w, h), new Color(0xBE, 0xF2, 0x64)); }
    public static Icon crearIconoGamas(int w, int h) { return createGamasIcon(Math.min(w, h), new Color(0xA3, 0xE6, 0x35)); }
    public static Icon crearIconoPagos(int w, int h) { return createPagosIcon(Math.min(w, h), new Color(0x10, 0xB9, 0x81)); }
    public static Icon crearIconoEmpleados(int w, int h) { return createEmpleadosIcon(Math.min(w, h), new Color(0x22, 0xC5, 0x5E)); }
    public static Icon crearIconoOficinas(int w, int h) { return createOficinasIcon(Math.min(w, h), new Color(0xBE, 0xF2, 0x64)); }
    public static Icon crearIconoRefresh(int w, int h) { return createRefreshIcon(Math.min(w, h), Color.WHITE); }
    public static Icon crearIconoSearch(int w, int h) { return createSearchIcon(Math.min(w, h), new Color(0xA3, 0xB5, 0xAA)); }

    @FunctionalInterface
    public interface IconPainter {
        void paint(Graphics2D g2, int width, int height);
    }

    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final IconPainter painter;

        public VectorIcon(int width, int height, IconPainter painter) {
            this.width = width;
            this.height = height;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.translate(x, y);
            painter.paint(g2, width, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}

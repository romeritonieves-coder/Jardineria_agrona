package jardineria.infrastructure.adapters.in.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente gráfico personalizado en Swing con animación y estética AGRONA.
 * Dibuja barras horizontales con degradados en verde esmeralda y lima (#2ECC71 -> #A3E635),
 * bordes suaves redondeados y efectos interactivos de hover.
 */
public class GraficoBarrasAgrona extends JPanel {

    public static class BarraDato {
        private final String etiqueta;
        private final int valor;
        private final String icono;
        private final Color colorGradienteInicio;
        private final Color colorGradienteFin;
        private float progresoAnimado = 0f;

        public BarraDato(String etiqueta, int valor, String icono, Color inicio, Color fin) {
            this.etiqueta = etiqueta;
            this.valor = valor;
            this.icono = icono;
            this.colorGradienteInicio = inicio;
            this.colorGradienteFin = fin;
        }

        public String getEtiqueta() { return etiqueta; }
        public int getValor() { return valor; }
        public String getIcono() { return icono; }
        public Color getColorGradienteInicio() { return colorGradienteInicio; }
        public Color getColorGradienteFin() { return colorGradienteFin; }
        public float getProgresoAnimado() { return progresoAnimado; }
        public void setProgresoAnimado(float p) { this.progresoAnimado = p; }
    }

    private final List<BarraDato> datos = new ArrayList<>();
    private int hoveredIndex = -1;
    private Timer animadorTimer;

    public GraficoBarrasAgrona() {
        setOpaque(false);
        setPreferredSize(new Dimension(540, 260));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int oldIndex = hoveredIndex;
                hoveredIndex = detectarBarraEnPunto(e.getY());
                if (oldIndex != hoveredIndex) {
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredIndex != -1) {
                    hoveredIndex = -1;
                    repaint();
                }
            }
        });
    }

    public synchronized void setDatos(List<BarraDato> nuevosDatos) {
        this.datos.clear();
        this.datos.addAll(nuevosDatos);
        iniciarAnimacion();
    }

    public void iniciarAnimacion() {
        if (animadorTimer != null && animadorTimer.isRunning()) {
            animadorTimer.stop();
        }

        for (BarraDato b : datos) {
            b.setProgresoAnimado(0f);
        }

        final long inicioMs = System.currentTimeMillis();
        final int duracionMs = 600;

        animadorTimer = new Timer(16, e -> {
            long transcurrido = System.currentTimeMillis() - inicioMs;
            float ratio = (float) transcurrido / duracionMs;
            if (ratio >= 1.0f) {
                ratio = 1.0f;
                for (BarraDato b : datos) {
                    b.setProgresoAnimado(1.0f);
                }
                animadorTimer.stop();
            } else {
                // Función de aceleración y desaceleración suave (ease-out cubic)
                float ease = 1f - (float) Math.pow(1f - ratio, 3);
                for (BarraDato b : datos) {
                    b.setProgresoAnimado(ease);
                }
            }
            repaint();
        });
        animadorTimer.start();
    }

    private int detectarBarraEnPunto(int y) {
        if (datos.isEmpty()) return -1;
        int alturaFila = getHeight() / Math.max(1, datos.size());
        int index = y / alturaFila;
        return (index >= 0 && index < datos.size()) ? index : -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // 1. Fondo contenedor con estilo cristal oscuro
        g2.setColor(new Color(0x16, 0x1E, 0x19));
        g2.fillRoundRect(0, 0, ancho, alto, 16, 16);

        g2.setColor(new Color(0x28, 0x38, 0x2E));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, ancho - 1, alto - 1, 16, 16);

        if (datos.isEmpty()) {
            g2.setColor(new Color(0x94, 0xA3, 0x98));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            String msg = "Cargando métricas operativas...";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (ancho - fm.stringWidth(msg)) / 2, alto / 2);
            g2.dispose();
            return;
        }

        // 2. Determinar valor máximo para escala
        int maxValor = 1;
        for (BarraDato b : datos) {
            if (b.getValor() > maxValor) {
                maxValor = b.getValor();
            }
        }

        int paddingH = 20;
        int paddingV = 16;
        int areaDibujoAlto = alto - (paddingV * 2);
        int numBarras = datos.size();
        int altoBarra = Math.min(22, (areaDibujoAlto / numBarras) - 12);
        int espacioTotalFila = areaDibujoAlto / numBarras;

        int xInicioBarra = 180;
        int anchoDisponible = ancho - xInicioBarra - paddingH - 60;

        for (int i = 0; i < numBarras; i++) {
            BarraDato dato = datos.get(i);
            int yFila = paddingV + (i * espacioTotalFila);
            int yBarra = yFila + ((espacioTotalFila - altoBarra) / 2);
            boolean isHovered = (i == hoveredIndex);

            // Resaltado de fila al hacer hover
            if (isHovered) {
                g2.setColor(new Color(0x1F, 0x2C, 0x24));
                g2.fillRoundRect(paddingH - 4, yFila, ancho - (paddingH * 2) + 8, espacioTotalFila, 8, 8);
            }

            // Etiqueta del ícono y nombre
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(isHovered ? new Color(0x2E, 0xCC, 0x71) : new Color(0xF1, 0xF5, 0xF2));
            String etiqueta = dato.getIcono() + "  " + dato.getEtiqueta();
            g2.drawString(etiqueta, paddingH, yBarra + altoBarra - 6);

            // Fondo de la barra (track oscuro)
            g2.setColor(new Color(0x20, 0x2A, 0x23));
            g2.fill(new RoundRectangle2D.Float(xInicioBarra, yBarra, anchoDisponible, altoBarra, altoBarra, altoBarra));

            // Barra animada con degradado verde Agrona
            float fraccion = (float) dato.getValor() / maxValor;
            int anchoBarraActual = (int) (anchoDisponible * fraccion * dato.getProgresoAnimado());
            anchoBarraActual = Math.max(altoBarra, anchoBarraActual); // Mantener radio redondo mínimo

            if (anchoBarraActual > 0) {
                GradientPaint gradiente = new GradientPaint(
                        xInicioBarra, yBarra, dato.getColorGradienteInicio(),
                        xInicioBarra + anchoBarraActual, yBarra, dato.getColorGradienteFin()
                );
                g2.setPaint(gradiente);
                g2.fill(new RoundRectangle2D.Float(xInicioBarra, yBarra, anchoBarraActual, altoBarra, altoBarra, altoBarra));

                if (isHovered) {
                    // Brillo sutil en el borde superior
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.draw(new RoundRectangle2D.Float(xInicioBarra, yBarra, anchoBarraActual, altoBarra, altoBarra, altoBarra));
                }
            }

            // Valor numérico a la derecha de la barra
            int valorMostrado = (int) (dato.getValor() * dato.getProgresoAnimado());
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(isHovered ? new Color(0xA3, 0xE6, 0x35) : new Color(0x94, 0xA3, 0x98));
            String valorTexto = String.format("%,d", valorMostrado);
            g2.drawString(valorTexto, xInicioBarra + anchoDisponible + 10, yBarra + altoBarra - 6);
        }

        g2.dispose();
    }
}

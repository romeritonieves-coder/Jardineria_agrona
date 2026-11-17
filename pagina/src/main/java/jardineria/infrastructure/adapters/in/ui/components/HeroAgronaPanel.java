package jardineria.infrastructure.adapters.in.ui.components;

import jardineria.infrastructure.adapters.in.ui.DashboardFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.function.Consumer;

/**
 * Panel Hero de Bienvenida para el Dashboard de AGRONA Jardinería.
 * Replica el diseño de alta gama de la plantilla web oficial con:
 * - Fondo ambiental oscuro con gradientes profundos y texturas vegetales.
 * - Logotipo corporativo de gran escala con animación de flotación suave en Swing.
 * - Tipografía y jerarquía corporativa ("Jardinería con Carácter", "Jardines que se sienten vivos").
 * - Botones de llamada a la acción (CTA) con acentos en verde lima y esmeralda.
 */
public class HeroAgronaPanel extends JPanel {

    private final Consumer<String> navigationHandler;
    private LogoFlotanteComponent logoFlotante;

    public HeroAgronaPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setOpaque(true);
        setBackground(new Color(0x0C, 0x12, 0x0E));
        setLayout(new BorderLayout(24, 0));
        setBorder(new EmptyBorder(32, 40, 32, 40));
        setPreferredSize(new Dimension(980, 380));
        setMinimumSize(new Dimension(800, 340));

        initComponents();
    }

    private void initComponents() {
        // Columna Izquierda: Jerarquía tipográfica y llamadas a la acción
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // 1. Subtítulo superior corporativo con línea en verde lima
        JPanel preTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        preTitlePanel.setOpaque(false);
        preTitlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPreTitle = new JLabel("——  JARDINERÍA CON CARÁCTER");
        lblPreTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPreTitle.setForeground(new Color(0xA3, 0xE6, 0x35)); // Verde Lima brillante
        lblPreTitle.setToolTipText("Lema oficial AGRONA");
        preTitlePanel.add(lblPreTitle);

        leftPanel.add(preTitlePanel);
        leftPanel.add(Box.createVerticalStrut(14));

        // 2. Título Principal en tipografía grande, sólida y blanca
        JLabel lblTitle1 = new JLabel("JARDINES QUE");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblTitle1.setForeground(Color.WHITE);
        lblTitle1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle2 = new JLabel("SE SIENTEN VIVOS.");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblTitle2.setForeground(new Color(0xF1, 0xF5, 0xF2));
        lblTitle2.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblTitle1);
        leftPanel.add(lblTitle2);
        leftPanel.add(Box.createVerticalStrut(14));

        // 3. Párrafo descriptivo de la marca
        JLabel lblDescripcion = new JLabel("<html><p style='width: 440px; line-height: 145%;'>" +
                "Diseñamos, transformamos y cuidamos espacios verdes con una mirada precisa, " +
                "natural y profundamente humana.</p></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescripcion.setForeground(new Color(0xA6, 0xBA, 0xAC));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblDescripcion);
        leftPanel.add(Box.createVerticalStrut(24));

        // 4. Fila de Botones de Acción (CTA)
        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        ctaRow.setOpaque(false);
        ctaRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón principal de estilo lima con texto oscuro de alto contraste
        JButton btnCtaPrimary = new JButton("HABLEMOS DE TU ESPACIO  ↗") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(0x8C, 0xD4, 0x22) :
                           getModel().isRollover() ? new Color(0xB5, 0xF5, 0x48) : new Color(0xA3, 0xE6, 0x35);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCtaPrimary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCtaPrimary.setForeground(new Color(0x0C, 0x14, 0x0E));
        btnCtaPrimary.setContentAreaFilled(false);
        btnCtaPrimary.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnCtaPrimary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCtaPrimary.setFocusPainted(false);
        btnCtaPrimary.addActionListener(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(DashboardFrame.CARD_PRODUCTOS);
            }
        });

        // Botón secundario tipo enlace o ghost
        JButton btnCtaSecondary = new JButton("CONOCE NUESTROS SERVICIOS  ›");
        btnCtaSecondary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCtaSecondary.setForeground(new Color(0x2E, 0xCC, 0x71));
        btnCtaSecondary.setContentAreaFilled(false);
        btnCtaSecondary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x27, 0xAE, 0x60, 140), 1, true),
                new EmptyBorder(9, 18, 9, 18)
        ));
        btnCtaSecondary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCtaSecondary.setFocusPainted(false);
        btnCtaSecondary.addActionListener(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(DashboardFrame.CARD_GAMAS);
            }
        });

        ctaRow.add(btnCtaPrimary);
        ctaRow.add(btnCtaSecondary);
        leftPanel.add(ctaRow);
        leftPanel.add(Box.createVerticalStrut(24));

        // 5. Marca de pie corporativa: (A)(G)(R) Una identidad que crece contigo
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBadgeA = crearBadgeLetra("A");
        JLabel lblBadgeG = crearBadgeLetra("G");
        JLabel lblBadgeR = crearBadgeLetra("R");

        JLabel lblBadgeText = new JLabel("  Una identidad que crece contigo");
        lblBadgeText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBadgeText.setForeground(new Color(0x73, 0x8C, 0x7A));

        badgeRow.add(lblBadgeA);
        badgeRow.add(lblBadgeG);
        badgeRow.add(lblBadgeR);
        badgeRow.add(lblBadgeText);
        leftPanel.add(badgeRow);

        add(leftPanel, BorderLayout.CENTER);

        // Columna Derecha: Componente animado del Logotipo Flotante
        logoFlotante = new LogoFlotanteComponent();
        logoFlotante.setPreferredSize(new Dimension(340, 340));
        add(logoFlotante, BorderLayout.EAST);
    }

    private JLabel crearBadgeLetra(String letra) {
        JLabel lbl = new JLabel(letra, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1B, 0x2A, 0x20));
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.setColor(new Color(0x2E, 0xCC, 0x71, 180));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setPreferredSize(new Dimension(22, 22));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(0xA3, 0xE6, 0x35));
        return lbl;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // 1. Fondo base oscuro profundo
        g2.setColor(new Color(0x0C, 0x12, 0x0E));
        g2.fillRect(0, 0, w, h);

        // 2. Gradiente radial esmeralda brillante detrás del logotipo flotante (derecha)
        Point2D centerGlow = new Point2D.Float(w * 0.78f, h * 0.46f);
        float radiusGlow = Math.max(w * 0.45f, 320f);
        float[] distGlow = {0.0f, 0.40f, 0.75f, 1.0f};
        Color[] colorsGlow = {
                new Color(0x1D, 0x48, 0x29, 130), // Halo esmeralda profundo
                new Color(0x13, 0x2E, 0x1B, 75),
                new Color(0x0E, 0x1A, 0x12, 30),
                new Color(0x0C, 0x12, 0x0E, 0)
        };
        RadialGradientPaint radialGlow = new RadialGradientPaint(centerGlow, radiusGlow, distGlow, colorsGlow);
        g2.setPaint(radialGlow);
        g2.fillRect(0, 0, w, h);

        // 3. Gradiente sutil en la esquina superior izquierda
        Point2D centerTopLeft = new Point2D.Float(w * 0.15f, h * 0.10f);
        float[] distTL = {0.0f, 1.0f};
        Color[] colorsTL = {
                new Color(0x15, 0x2C, 0x1C, 65),
                new Color(0x0C, 0x12, 0x0E, 0)
        };
        RadialGradientPaint radialTL = new RadialGradientPaint(centerTopLeft, 260f, distTL, colorsTL);
        g2.setPaint(radialTL);
        g2.fillRect(0, 0, w, h);

        // 4. Siluetas orgánicas vegetales en las esquinas (Hojas estilizadas sutiles)
        dibujarHojasSiluetas(g2, w, h);

        // 5. Borde inferior suave separador
        g2.setColor(new Color(0x1F, 0x2D, 0x24));
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
    }

    private void dibujarHojasSiluetas(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0x27, 0xAE, 0x60, 18)); // Verde muy sutil y elegante

        // Hoja en esquina superior derecha
        GeneralPath hoja1 = new GeneralPath();
        hoja1.moveTo(w - 60, 0);
        hoja1.curveTo(w - 40, 60, w - 80, 120, w - 10, 140);
        hoja1.curveTo(w - 5, 80, w - 15, 30, w, 0);
        hoja1.closePath();
        g2.fill(hoja1);

        // Hoja en esquina inferior derecha detrás del logo
        GeneralPath hoja2 = new GeneralPath();
        hoja2.moveTo(w - 200, h);
        hoja2.curveTo(w - 170, h - 70, w - 90, h - 90, w - 40, h);
        hoja2.closePath();
        g2.fill(hoja2);

        // Hoja lateral izquierda tenue
        GeneralPath hoja3 = new GeneralPath();
        hoja3.moveTo(0, h - 100);
        hoja3.curveTo(50, h - 120, 80, h - 60, 0, h - 30);
        hoja3.closePath();
        g2.fill(hoja3);
    }

    /**
     * Componente interno que renderiza el logotipo oficial con animación de flotación vertical suave.
     */
    private static class LogoFlotanteComponent extends JComponent {

        private Image logoImage;
        private Timer timerFlotacion;
        private double angulo = 0.0;
        private int offsetY = 0;

        public LogoFlotanteComponent() {
            setOpaque(false);
            cargarLogo();
            iniciarAnimacion();
        }

        private void cargarLogo() {
            ImageIcon icon = DashboardFrame.obtenerLogoIcon(270, 270);
            if (icon != null) {
                this.logoImage = icon.getImage();
            }
        }

        private void iniciarAnimacion() {
            // Temporizador Swing suave a ~33 FPS
            timerFlotacion = new Timer(30, e -> {
                angulo += 0.042; // Velocidad de oscilación suave y natural
                if (angulo >= Math.PI * 2) {
                    angulo = 0.0;
                }
                // Movimiento vertical flotante de +/- 10 píxeles
                offsetY = (int) (Math.sin(angulo) * 10);
                repaint();
            });
            timerFlotacion.start();
        }

        @Override
        public void addNotify() {
            super.addNotify();
            if (timerFlotacion != null && !timerFlotacion.isRunning()) {
                timerFlotacion.start();
            }
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            if (timerFlotacion != null && timerFlotacion.isRunning()) {
                timerFlotacion.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            int centerX = w / 2;
            int centerY = h / 2 - 10;

            // 1. Sombra elíptica dinámica en el suelo: se ensancha y contrae con la flotación
            int anchoSombra = 210 - (offsetY * 2);
            int altoSombra = 28 - (offsetY / 2);
            int sombraY = centerY + 130;

            Point2D centerSombra = new Point2D.Float(centerX, sombraY);
            float[] distSombra = {0.0f, 0.5f, 1.0f};
            Color[] colorsSombra = {
                    new Color(0x05, 0x0A, 0x07, 180),
                    new Color(0x07, 0x12, 0x0A, 80),
                    new Color(0x0C, 0x12, 0x0E, 0)
            };
            RadialGradientPaint radialSombra = new RadialGradientPaint(centerSombra, anchoSombra / 2.0f, distSombra, colorsSombra);
            g2.setPaint(radialSombra);
            g2.fillOval(centerX - anchoSombra / 2, sombraY - altoSombra / 2, anchoSombra, altoSombra);

            // 2. Halo verde suave envolvente directamente detrás del logo
            Point2D centerAura = new Point2D.Float(centerX, centerY + offsetY);
            float[] distAura = {0.0f, 0.65f, 1.0f};
            Color[] colorsAura = {
                    new Color(0x27, 0xAE, 0x60, 60),
                    new Color(0x1E, 0x4B, 0x2E, 25),
                    new Color(0x0C, 0x12, 0x0E, 0)
            };
            RadialGradientPaint radialAura = new RadialGradientPaint(centerAura, 150f, distAura, colorsAura);
            g2.setPaint(radialAura);
            g2.fillOval(centerX - 150, centerY + offsetY - 150, 300, 300);

            // 3. Renderizado del Logotipo Oficial con desplazamiento vertical flotante
            if (logoImage != null) {
                int imgW = logoImage.getWidth(this);
                int imgH = logoImage.getHeight(this);
                if (imgW <= 0 || imgH <= 0) {
                    imgW = 260;
                    imgH = 260;
                }
                int imgX = centerX - imgW / 2;
                int imgY = centerY - imgH / 2 + offsetY;

                g2.drawImage(logoImage, imgX, imgY, imgW, imgH, this);
            }

            g2.dispose();
        }
    }
}


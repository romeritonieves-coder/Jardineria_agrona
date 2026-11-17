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
 * Aplica el estilo visual de alta gama de la plantilla web oficial con:
 * - Paleta forestal suave y elegante (#161F1A / #1B2620).
 * - Logotipo oficial grande flotante animado con Swing Timer.
 * - Iconos vectoriales limpios sin caracteres rotos (AgronaIcons).
 * - Franja corporativa "Nuestra Especialidad: Diseño • Cuidado • Paisajismo • Vida Verde".
 */
public class HeroAgronaPanel extends JPanel {

    private final Consumer<String> navigationHandler;
    private LogoFlotanteComponent logoFlotante;

    public HeroAgronaPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setOpaque(true);
        setBackground(new Color(0x16, 0x1F, 0x1A)); // Fondo forestal suave
        setLayout(new BorderLayout(24, 16));
        setBorder(new EmptyBorder(24, 32, 28, 32));
        setMinimumSize(new Dimension(720, 360));

        initComponents();
    }

    private void initComponents() {
        // Franja superior de marca (Especialidad Agrona)
        JPanel topStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topStrip.setOpaque(false);

        JLabel lblEspecialidadTag = new JLabel("NUESTRA ESPECIALIDAD");
        lblEspecialidadTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEspecialidadTag.setForeground(new Color(0xBE, 0xF2, 0x64)); // Lima suave

        JLabel lblEspecialidadItems = new JLabel("•  DISEÑO   •   CUIDADO   •   PAISAJISMO   •   VIDA VERDE");
        lblEspecialidadItems.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEspecialidadItems.setForeground(new Color(0xE2, 0xEB, 0xE4));

        topStrip.add(lblEspecialidadTag);
        topStrip.add(lblEspecialidadItems);
        add(topStrip, BorderLayout.NORTH);

        // Contenedor Central: Columna de texto a la izquierda, logo flotante a la derecha
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Columna Izquierda: Jerarquía de Marca
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel lblPreTitle = new JLabel("——  JARDINERÍA CON CARÁCTER");
        lblPreTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPreTitle.setForeground(new Color(0xA3, 0xE6, 0x35)); // Verde Lima
        leftPanel.add(lblPreTitle);
        leftPanel.add(Box.createVerticalStrut(10));

        JLabel lblTitle1 = new JLabel("JARDINES QUE");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle1.setForeground(Color.WHITE);
        leftPanel.add(lblTitle1);

        JLabel lblTitle2 = new JLabel("SE SIENTEN VIVOS.");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle2.setForeground(new Color(0xF1, 0xF5, 0xF2));
        leftPanel.add(lblTitle2);
        leftPanel.add(Box.createVerticalStrut(12));

        JLabel lblDescripcion = new JLabel("<html><p style='width: 420px; line-height: 140%;'>" +
                "Diseñamos, transformamos y cuidamos espacios verdes con una mirada precisa, " +
                "natural y profundamente humana.</p></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescripcion.setForeground(new Color(0xA3, 0xB5, 0xAA));
        leftPanel.add(lblDescripcion);
        leftPanel.add(Box.createVerticalStrut(20));

        // Fila de Botones de Acción
        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        ctaRow.setOpaque(false);

        JButton btnCtaPrimary = new JButton("HABLEMOS DE TU ESPACIO", AgronaIcons.createArrowUpRightIcon(14, new Color(0x0C, 0x14, 0x0E))) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(0x8C, 0xD4, 0x22) :
                           getModel().isRollover() ? new Color(0xBE, 0xF2, 0x64) : new Color(0xA3, 0xE6, 0x35);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCtaPrimary.setHorizontalTextPosition(SwingConstants.LEFT);
        btnCtaPrimary.setIconTextGap(8);
        btnCtaPrimary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCtaPrimary.setForeground(new Color(0x0C, 0x14, 0x0E));
        btnCtaPrimary.setContentAreaFilled(false);
        btnCtaPrimary.setBorder(new EmptyBorder(10, 18, 10, 18));
        btnCtaPrimary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCtaPrimary.setFocusPainted(false);
        btnCtaPrimary.addActionListener(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(DashboardFrame.CARD_PRODUCTOS);
            }
        });

        JButton btnCtaSecondary = new JButton("CONOCE NUESTROS SERVICIOS", AgronaIcons.createCheckIcon(13, new Color(0x22, 0xC5, 0x5E)));
        btnCtaSecondary.setHorizontalTextPosition(SwingConstants.LEFT);
        btnCtaSecondary.setIconTextGap(8);
        btnCtaSecondary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCtaSecondary.setForeground(new Color(0x22, 0xC5, 0x5E));
        btnCtaSecondary.setContentAreaFilled(false);
        btnCtaSecondary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x38, 0x4D, 0x44), 1, true),
                new EmptyBorder(9, 16, 9, 16)
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
        leftPanel.add(Box.createVerticalStrut(18));

        // Tagline inferior
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(crearBadgeLetra("A"));
        badgeRow.add(crearBadgeLetra("G"));
        badgeRow.add(crearBadgeLetra("R"));

        JLabel lblBadgeText = new JLabel("  Una identidad que crece contigo");
        lblBadgeText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBadgeText.setForeground(new Color(0x7D, 0x93, 0x86));
        badgeRow.add(lblBadgeText);
        leftPanel.add(badgeRow);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.60;
        gbc.fill = GridBagConstraints.BOTH;
        centerContainer.add(leftPanel, gbc);

        // 2. Columna Derecha: Logotipo grande animado
        logoFlotante = new LogoFlotanteComponent();
        logoFlotante.setPreferredSize(new Dimension(300, 300));
        gbc.gridx = 1;
        gbc.weightx = 0.40;
        gbc.fill = GridBagConstraints.BOTH;
        centerContainer.add(logoFlotante, gbc);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JLabel crearBadgeLetra(String letra) {
        JLabel lbl = new JLabel(letra, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x23, 0x30, 0x2B));
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.setColor(new Color(0x38, 0x4D, 0x44));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setPreferredSize(new Dimension(22, 22));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(0xBE, 0xF2, 0x64));
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

        // Fondo forestal suave #161F1A
        g2.setColor(new Color(0x16, 0x1F, 0x1A));
        g2.fillRect(0, 0, w, h);

        // Gradiente radial esmeralda suave detrás del logo
        Point2D centerGlow = new Point2D.Float(w * 0.78f, h * 0.48f);
        float radiusGlow = Math.max(w * 0.45f, 320f);
        float[] distGlow = {0.0f, 0.45f, 0.80f, 1.0f};
        Color[] colorsGlow = {
                new Color(0x23, 0x3B, 0x2E, 130),
                new Color(0x1B, 0x2A, 0x22, 80),
                new Color(0x16, 0x1F, 0x1A, 30),
                new Color(0x16, 0x1F, 0x1A, 0)
        };
        RadialGradientPaint radialGlow = new RadialGradientPaint(centerGlow, radiusGlow, distGlow, colorsGlow);
        g2.setPaint(radialGlow);
        g2.fillRect(0, 0, w, h);

        // Siluetas orgánicas vegetales tenues
        g2.setColor(new Color(0x22, 0xC5, 0x5E, 16));
        GeneralPath leaf1 = new GeneralPath();
        leaf1.moveTo(w - 70, 0);
        leaf1.curveTo(w - 40, 70, w - 90, 130, w - 10, 160);
        leaf1.curveTo(w - 5, 90, w - 15, 30, w, 0);
        leaf1.closePath();
        g2.fill(leaf1);

        GeneralPath leaf2 = new GeneralPath();
        leaf2.moveTo(0, h - 90);
        leaf2.curveTo(60, h - 110, 90, h - 50, 0, h - 20);
        leaf2.closePath();
        g2.fill(leaf2);

        // Borde inferior sutil
        g2.setColor(new Color(0x28, 0x38, 0x30));
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
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
            ImageIcon icon = DashboardFrame.obtenerLogoIcon(250, 250);
            if (icon != null) {
                this.logoImage = icon.getImage();
            }
        }

        private void iniciarAnimacion() {
            timerFlotacion = new Timer(30, e -> {
                angulo += 0.042;
                if (angulo >= Math.PI * 2) {
                    angulo = 0.0;
                }
                offsetY = (int) (Math.sin(angulo) * 9);
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
            int centerY = h / 2 - 8;

            // Sombra elíptica dinámica en el suelo
            int anchoSombra = 190 - (offsetY * 2);
            int altoSombra = 24 - (offsetY / 2);
            int sombraY = centerY + 120;

            Point2D centerSombra = new Point2D.Float(centerX, sombraY);
            float[] distSombra = {0.0f, 0.55f, 1.0f};
            Color[] colorsSombra = {
                    new Color(0x0E, 0x14, 0x10, 160),
                    new Color(0x12, 0x1A, 0x14, 70),
                    new Color(0x16, 0x1F, 0x1A, 0)
            };
            RadialGradientPaint radialSombra = new RadialGradientPaint(centerSombra, anchoSombra / 2.0f, distSombra, colorsSombra);
            g2.setPaint(radialSombra);
            g2.fillOval(centerX - anchoSombra / 2, sombraY - altoSombra / 2, anchoSombra, altoSombra);

            // Halo suave envolvente
            Point2D centerAura = new Point2D.Float(centerX, centerY + offsetY);
            float[] distAura = {0.0f, 0.65f, 1.0f};
            Color[] colorsAura = {
                    new Color(0x22, 0xC5, 0x5E, 50),
                    new Color(0x1E, 0x3B, 0x2A, 20),
                    new Color(0x16, 0x1F, 0x1A, 0)
            };
            RadialGradientPaint radialAura = new RadialGradientPaint(centerAura, 140f, distAura, colorsAura);
            g2.setPaint(radialAura);
            g2.fillOval(centerX - 140, centerY + offsetY - 140, 280, 280);

            // Logo oficial
            if (logoImage != null) {
                int imgW = logoImage.getWidth(this);
                int imgH = logoImage.getHeight(this);
                if (imgW <= 0 || imgH <= 0) {
                    imgW = 240;
                    imgH = 240;
                }
                int imgX = centerX - imgW / 2;
                int imgY = centerY - imgH / 2 + offsetY;

                g2.drawImage(logoImage, imgX, imgY, imgW, imgH, this);
            }

            g2.dispose();
        }
    }
}

import {
  ArrowDown,
  ArrowUpRight,
  BarChart3,
  Check,
  ChevronRight,
  Download,
  Droplets,
  Layers,
  Menu,
  PhoneCall,
  RefreshCw,
  Scissors,
  ShieldCheck,
  ShoppingBag,
  Sparkles,
  Sprout,
  SunMedium,
  TreePine,
  TrendingUp,
  Users,
  X,
} from "lucide-react";
import { FormEvent, useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

// Logo oficial del proyecto
const logoSrc = "/agrona_logo_icon.png";

// Servicios especializados de AGRONA
const services = [
  {
    number: "01",
    icon: Sprout,
    title: "Diseño & Paisajismo Integral",
    copy: "Creamos composiciones botánicas personalizadas que transforman terrazas, patios y jardines residenciales o corporativos.",
    detail: "Arquitectura Verde · Especies Nativas · Suelos Vivos",
    badge: "Especialidad",
  },
  {
    number: "02",
    icon: Scissors,
    title: "Mantenimiento & Sanidad Vegetal",
    copy: "Planes preventivos y correctivos continuos para preservar el vigor, color y equilibrio biológico de cada espécimen.",
    detail: "Poda Especializada · Bio-Nutrición · Control Orgánico",
    badge: "Preventivo",
  },
  {
    number: "03",
    icon: Droplets,
    title: "Riego Tecnificado & Drenaje",
    copy: "Sistemas inteligentes de microaspersión y goteo por sectores que optimizan hasta un 45% el consumo hídrico.",
    detail: "Automatización · Sensores de Humedad · Eficiencia",
    badge: "Eco-Friendly",
  },
  {
    number: "04",
    icon: TreePine,
    title: "Suministro & Catálogo Exclusivo",
    copy: "Plantas ornamentales aclimatadas, sustratos enriquecidos, macetería de diseño y fertilizantes de grado agronómico.",
    detail: "Aclimatación · Trazabilidad · Garantía Viva",
    badge: "Catálogo",
  },
];

const benefits = [
  "Diagnóstico agronómico y lumínico previo a cada intervención",
  "Catálogo con más de 360 especies rigurosamente seleccionadas",
  "Equipo técnico especializado con enfoque botánico y estético",
  "Acompañamiento post-instalación con monitoreo de crecimiento",
  "Garantía de adaptación vegetal y sustitución programada",
];

const projects = [
  {
    label: "Jardín Zen Residencial",
    category: "Residencial Alto Estándar",
    metric: "450 m²",
    tone: "border-[#2ECC71]/40 bg-gradient-to-br from-[#14231B] to-[#0A140F]",
    accent: "text-[#2ECC71]",
  },
  {
    label: "Terraza Botánica & Lounge",
    category: "Corporativo & Hospitality",
    metric: "280 m²",
    tone: "border-[#A3E635]/40 bg-gradient-to-br from-[#1A2E20] to-[#0D1812]",
    accent: "text-[#A3E635]",
  },
  {
    label: "Parque Sostenible Nativo",
    category: "Desarrollo Inmobiliario",
    metric: "3,200 m²",
    tone: "border-[#2ECC71]/40 bg-gradient-to-br from-[#122219] to-[#09130D]",
    accent: "text-[#2ECC71]",
  },
];

function scrollToSection(id: string, closeMenu?: () => void) {
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({ behavior: "smooth", block: "start" });
  }
  closeMenu?.();
}

export default function Home() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);

  // KPIs dinámicos de operaciones
  const [kpis, setKpis] = useState({
    clientes: 128,
    productos: 364,
    pedidos: 1420,
    areaVerde: 48500,
  });

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  const refrescarMetricas = () => {
    setIsRefreshing(true);
    toast.loading("Sincronizando métricas con la base de datos...", { id: "refresh-kpi" });
    
    setTimeout(() => {
      setKpis({
        clientes: 128 + Math.floor(Math.random() * 3),
        productos: 364 + Math.floor(Math.random() * 2),
        pedidos: 1420 + Math.floor(Math.random() * 5),
        areaVerde: 48500 + Math.floor(Math.random() * 150),
      });
      setIsRefreshing(false);
      toast.success("Métricas operativas actualizadas exitosamente", {
        id: "refresh-kpi",
        description: "Los contadores de Clientes, Inventario y Pedidos están al día.",
      });
    }, 600);
  };

  const handleDescargarReporte = () => {
    toast.success("Generando reporte ejecutivo", {
      description: "Descarga de métricas consolidadas lista en formato Excel (.xlsx).",
    });
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    event.currentTarget.reset();
    toast.success("¡Solicitud recibida correctamente!", {
      description: "Un asesor botánico de AGRONA Jardinería se comunicará contigo hoy mismo.",
    });
  };

  return (
    <div className="site-shell bg-[#0B1410] text-[#F1F5F2]">
      {/* Barra de Navegación Principal */}
      <header className={`site-header ${scrolled ? "site-header-scrolled" : ""}`}>
        <div className="nav-wrap">
          <button
            className="brand-lockup group"
            onClick={() => scrollToSection("inicio")}
            aria-label="Ir al inicio de AGRONA Jardinería"
          >
            <img
              src={logoSrc}
              alt="AGRONA Jardinería Logo Oficial"
              className="brand-logo-img"
            />
            <div className="brand-text-col text-left">
              <span className="brand-wordmark">AGRONA</span>
              <span className="brand-submark">JARDINERÍA</span>
            </div>
          </button>

          <nav
            className={`main-nav ${menuOpen ? "main-nav-open" : ""}`}
            aria-label="Navegación principal"
          >
            <button onClick={() => scrollToSection("metricas", () => setMenuOpen(false))}>
              Dashboard & KPIs
            </button>
            <button onClick={() => scrollToSection("servicios", () => setMenuOpen(false))}>
              Servicios
            </button>
            <button onClick={() => scrollToSection("historia", () => setMenuOpen(false))}>
              Nuestra Identidad
            </button>
            <button onClick={() => scrollToSection("proyectos", () => setMenuOpen(false))}>
              Proyectos
            </button>
            <button onClick={() => scrollToSection("contacto", () => setMenuOpen(false))}>
              Contacto
            </button>
            <button
              className="nav-cta"
              onClick={() => scrollToSection("contacto", () => setMenuOpen(false))}
            >
              Cotizar Proyecto <ArrowUpRight size={16} />
            </button>
          </nav>

          <button
            className="menu-toggle"
            onClick={() => setMenuOpen((open) => !open)}
            aria-label={menuOpen ? "Cerrar menú" : "Abrir menú"}
          >
            {menuOpen ? <X size={26} /> : <Menu size={26} />}
          </button>
        </div>
      </header>

      <main>
        {/* SECCIÓN HERO */}
        <section id="inicio" className="hero-section">
          <div className="hero-grain" />
          <div className="hero-content page-width">
            <div className="hero-copy">
              <div className="eyebrow">
                <span /> Jardinería con identidad & técnica
              </div>
              <h1>
                Jardines que<br />
                <em>se sienten vivos.</em>
              </h1>
              <p className="hero-lead">
                Diseñamos, transformamos y mantenemos espacios verdes con una mirada botánica
                profunda, arquitectura paisajista de vanguardia y tecnología de gestión.
              </p>
              
              <div className="hero-actions">
                <button
                  className="button button-primary"
                  onClick={() => scrollToSection("metricas")}
                >
                  <BarChart3 size={18} /> Ver Dashboard de Gestión
                </button>
                <button
                  className="button button-outline"
                  onClick={() => scrollToSection("contacto")}
                >
                  Solicitar Consulta <ArrowUpRight size={18} />
                </button>
              </div>

              {/* Badges de Confianza */}
              <div className="flex items-center gap-6 mt-10 pt-6 border-t border-[#23382C]/60 text-xs text-[#8FA697]">
                <div className="flex items-center gap-2">
                  <ShieldCheck size={18} className="text-[#2ECC71]" />
                  <span>Garantía de Adaptación Botánica</span>
                </div>
                <div className="flex items-center gap-2">
                  <Sparkles size={18} className="text-[#A3E635]" />
                  <span>Sistemas Hexagonales Certificados</span>
                </div>
              </div>
            </div>

            {/* Emblema Flotante de Marca */}
            <div className="hero-emblem-wrap" aria-label="Emblema de la Criatura Mítica de AGRONA">
              <div className="hero-emblem-glow" />
              <div className="emblem-orbit orbit-one" />
              <div className="emblem-orbit orbit-two" />
              <img
                className="hero-emblem"
                src={logoSrc}
                alt="Logo Oficial AGRONA Jardinería"
              />
            </div>
          </div>
        </section>

        {/* SECCIÓN DASHBOARD DE MÉTRICAS (KPI CARDS) */}
        <section id="metricas" className="dashboard-kpi-section">
          <div className="page-width">
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-2">
              <div>
                <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider text-[#A3E635] bg-[#A3E635]/10 border border-[#A3E635]/30">
                  <span className="w-2 h-2 rounded-full bg-[#A3E635] animate-ping" />
                  Métricas en Tiempo Real
                </div>
                <h2 className="text-3xl md:text-5xl font-extrabold uppercase tracking-tight text-white mt-3">
                  Panel de Rendimiento <span className="text-[#2ECC71]">Operativo</span>
                </h2>
                <p className="text-sm md:text-base text-[#8FA697] max-w-xl mt-1">
                  Monitoreo consolidado de clientes activos, catálogo vivo de especies, pedidos
                  despachados e intervenciones de paisajismo.
                </p>
              </div>

              {/* Botones de acción del Dashboard */}
              <div className="flex items-center gap-3">
                <Button
                  onClick={refrescarMetricas}
                  disabled={isRefreshing}
                  className="bg-[#1A3125] hover:bg-[#234232] text-[#2ECC71] border border-[#2ECC71]/40 gap-2 h-11 px-4 rounded-xl font-bold tracking-wide transition-all"
                >
                  <RefreshCw size={16} className={isRefreshing ? "animate-spin" : ""} />
                  Actualizar Métricas
                </Button>
                <Button
                  onClick={handleDescargarReporte}
                  variant="outline"
                  className="border-[#23382C] bg-[#14211A] hover:bg-[#1A2E23] text-[#F1F5F2] hover:text-[#A3E635] gap-2 h-11 px-4 rounded-xl font-semibold"
                >
                  <Download size={16} />
                  Exportar (.xlsx)
                </Button>
              </div>
            </div>

            {/* Grid de Tarjetas KPI */}
            <div className="kpi-grid">
              {/* KPI 1: Clientes */}
              <div className="kpi-card accent-emerald">
                <div>
                  <div className="kpi-header">
                    <span className="kpi-title">Clientes Registrados</span>
                    <div className="kpi-icon-wrap">
                      <Users size={20} />
                    </div>
                  </div>
                  <div className="kpi-value emerald">{kpis.clientes}</div>
                </div>
                <div className="kpi-footer">
                  <span className="kpi-trend">
                    <TrendingUp size={14} /> +12% este mes
                  </span>
                  <span>99.2% Retención</span>
                </div>
              </div>

              {/* KPI 2: Inventario / Productos */}
              <div className="kpi-card accent-lime">
                <div>
                  <div className="kpi-header">
                    <span className="kpi-title">Catálogo Botánico</span>
                    <div className="kpi-icon-wrap">
                      <Sprout size={20} />
                    </div>
                  </div>
                  <div className="kpi-value lime">{kpis.productos}</div>
                </div>
                <div className="kpi-footer">
                  <span className="kpi-trend lime">
                    <Check size={14} /> Stock Garantizado
                  </span>
                  <span>9 Familias Vivas</span>
                </div>
              </div>

              {/* KPI 3: Pedidos */}
              <div className="kpi-card accent-emerald">
                <div>
                  <div className="kpi-header">
                    <span className="kpi-title">Pedidos Despachados</span>
                    <div className="kpi-icon-wrap">
                      <ShoppingBag size={20} />
                    </div>
                  </div>
                  <div className="kpi-value emerald">{kpis.pedidos.toLocaleString()}</div>
                </div>
                <div className="kpi-footer">
                  <span className="kpi-trend">
                    <Check size={14} /> 99.4% a tiempo
                  </span>
                  <span>0 Incidencias</span>
                </div>
              </div>

              {/* KPI 4: Área Verde */}
              <div className="kpi-card accent-lime">
                <div>
                  <div className="kpi-header">
                    <span className="kpi-title">Superficie Intervenida</span>
                    <div className="kpi-icon-wrap">
                      <TreePine size={20} />
                    </div>
                  </div>
                  <div className="kpi-value lime">
                    {(kpis.areaVerde / 1000).toFixed(1)}k <span className="text-xl">m²</span>
                  </div>
                </div>
                <div className="kpi-footer">
                  <span className="kpi-trend lime">
                    <TrendingUp size={14} /> +4.2k m² Q4
                  </span>
                  <span>Paisajismo Alto Impacto</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* SECCIÓN DE SERVICIOS ESPECIALIZADOS */}
        <section id="servicios" className="services-section">
          <div className="page-width">
            <div className="section-heading">
              <div>
                <div className="eyebrow">
                  <span /> Soluciones Especializadas
                </div>
                <h2>
                  La precisión de un jardín<br />
                  <em>perfectamente cuidado.</em>
                </h2>
              </div>
              <p>
                Un jardín de excelencia requiere equilibrio entre estética visual, nutrición del
                suelo y un plan de mantenimiento riguroso adaptado al microclima.
              </p>
            </div>

            <div className="services-grid">
              {services.map(({ number, icon: Icon, title, copy, detail, badge }) => (
                <article className="service-card group" key={number}>
                  <div>
                    <div className="service-card-top">
                      <span className="card-number">{number}</span>
                      <Badge className="bg-[#1A3125] text-[#2ECC71] border border-[#2ECC71]/30 hover:bg-[#1A3125]">
                        {badge}
                      </Badge>
                    </div>
                    <div className="mt-6 mb-2 text-[#2ECC71] group-hover:text-[#A3E635] transition-colors">
                      <Icon size={34} strokeWidth={1.8} />
                    </div>
                    <h3>{title}</h3>
                    <p>{copy}</p>
                  </div>
                  <div>
                    <div className="service-detail">
                      <span />
                      {detail}
                    </div>
                    <button
                      className="circle-arrow group-hover:scale-105"
                      onClick={() => scrollToSection("contacto")}
                      aria-label={`Consultar sobre ${title}`}
                    >
                      <ArrowUpRight size={20} />
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </div>
        </section>

        {/* SECCIÓN NUESTRA IDENTIDAD / HISTORIA */}
        <section id="historia" className="story-section">
          <div className="page-width story-grid">
            <div className="story-visual">
              <div className="story-panel-main">
                <img src={logoSrc} alt="Emblema de AGRONA Jardinería" />
              </div>
              <div className="story-stamp">
                <span>AG</span>
                <small>Desde la<br />raíz</small>
              </div>
            </div>

            <div className="story-copy">
              <div className="eyebrow">
                <span /> Nuestra Esencia
              </div>
              <h2>
                Más que jardinería.<br />
                <em>Hacemos que los espacios vivan.</em>
              </h2>
              <p className="story-intro">
                En AGRONA combinamos la ciencia agronómica con la sensibilidad paisajística,
                honrando el ciclo biológico de cada especie para que prospere con carácter.
              </p>
              <p>
                Acompañamos a nuestros clientes en cada etapa: desde el anteproyecto conceptual y la
                selección meticulosa de cada ejemplar, hasta la nutrición de raíces y la poda
                quirúrgica que garantiza un follaje exuberante año tras año.
              </p>
              
              <div className="story-signature">
                <span className="signature-line" />
                <span>
                  Técnica Botánica con Intención<br />
                  <strong>Diseño con Criterio Sostenible</strong>
                </span>
              </div>
            </div>
          </div>
        </section>

        {/* SECCIÓN PROYECTOS DESTACADOS */}
        <section id="proyectos" className="py-24 bg-[#0E1813] border-t border-[#23382C]/60">
          <div className="page-width">
            <div className="section-heading mb-14">
              <div>
                <div className="eyebrow">
                  <span /> Portafolio Botánico
                </div>
                <h2>
                  Proyectos que hablan<br />
                  <em>por su carácter.</em>
                </h2>
              </div>
              <p>
                Cada espacio tiene su propia luz, relieve e historia. Nuestro compromiso es darle
                una voz verde imponente y duradera.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {projects.map((proj, idx) => (
                <div
                  key={idx}
                  className={`p-8 rounded-2xl border ${proj.tone} transition-all duration-300 hover:-translate-y-2 hover:shadow-2xl hover:shadow-[#2ECC71]/10 flex flex-col justify-between min-h-[300px]`}
                >
                  <div>
                    <span className="text-xs font-bold uppercase tracking-widest text-[#8FA697]">
                      {proj.category}
                    </span>
                    <h3 className="text-3xl font-extrabold uppercase tracking-tight text-white mt-4 mb-2">
                      {proj.label}
                    </h3>
                  </div>
                  <div className="pt-8 border-t border-[#23382C]/60 flex items-center justify-between">
                    <div>
                      <span className="text-[10px] uppercase tracking-wider text-[#8FA697] block">
                        Superficie Total
                      </span>
                      <span className={`text-2xl font-black ${proj.accent} font-['Barlow_Condensed']`}>
                        {proj.metric}
                      </span>
                    </div>
                    <button
                      onClick={() => scrollToSection("contacto")}
                      className="w-10 h-10 rounded-full border border-[#23382C] flex items-center justify-center text-[#2ECC71] hover:bg-[#2ECC71] hover:text-[#0B1410] transition-colors"
                      aria-label="Consultar proyecto"
                    >
                      <ArrowUpRight size={18} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* SECCIÓN DE CONTACTO & COTIZACIONES */}
        <section id="contacto" className="contact-section">
          <div className="page-width contact-grid">
            <div className="contact-copy">
              <div className="eyebrow">
                <span /> Inicia tu Proyecto
              </div>
              <h2>
                Tu nuevo entorno verde<br />
                <em>comienza aquí.</em>
              </h2>
              <p>
                Cuéntanos tu visión. Evaluaremos el potencial de tu espacio y prepararemos una
                propuesta de diseño, mantenimiento o abastecimiento personalizada.
              </p>

              <div className="mt-10 space-y-4">
                <div className="flex items-center gap-4 text-[#F1F5F2]">
                  <div className="w-11 h-11 rounded-xl bg-[#1A3125] border border-[#2ECC71]/30 flex items-center justify-center text-[#2ECC71]">
                    <PhoneCall size={20} />
                  </div>
                  <div>
                    <span className="text-xs uppercase tracking-wider text-[#8FA697] block">
                      Atención Directa
                    </span>
                    <strong className="text-base text-white">contacto@agronajardineria.com</strong>
                  </div>
                </div>

                <div className="flex items-center gap-4 text-[#F1F5F2]">
                  <div className="w-11 h-11 rounded-xl bg-[#1A3125] border border-[#A3E635]/30 flex items-center justify-center text-[#A3E635]">
                    <SunMedium size={20} />
                  </div>
                  <div>
                    <span className="text-xs uppercase tracking-wider text-[#8FA697] block">
                      Horario de Despacho
                    </span>
                    <strong className="text-base text-white">Lunes a Sábado: 8:00 AM - 6:00 PM</strong>
                  </div>
                </div>
              </div>
            </div>

            {/* Formulario */}
            <form className="contact-form" onSubmit={handleSubmit}>
              <div className="form-row">
                <label>
                  Tu Nombre Completo
                  <input
                    name="name"
                    required
                    placeholder="Ej. Carlos Mendoza"
                  />
                </label>
                <label>
                  Correo Electrónico
                  <input
                    type="email"
                    name="email"
                    required
                    placeholder="carlos@empresa.com"
                  />
                </label>
              </div>

              <div className="form-row">
                <label>
                  Teléfono / WhatsApp
                  <input
                    type="tel"
                    name="phone"
                    placeholder="+57 300 123 4567"
                  />
                </label>
                <label>
                  Tipo de Servicio
                  <input
                    name="service"
                    placeholder="Diseño / Mantenimiento / Compra"
                  />
                </label>
              </div>

              <label>
                ¿Cómo es tu espacio y qué deseas lograr?
                <textarea
                  name="message"
                  required
                  rows={4}
                  placeholder="Detalla metros cuadrados aproximados, ubicación o especies de tu interés..."
                />
              </label>

              <button type="submit" className="button button-primary w-full mt-2">
                Enviar Solicitud de Proyecto <ArrowUpRight size={18} />
              </button>
            </form>
          </div>
        </section>
      </main>

      {/* PIE DE PÁGINA (FOOTER) */}
      <footer className="site-footer">
        <div className="page-width footer-inner">
          <div className="flex items-center gap-3">
            <img
              src={logoSrc}
              alt="AGRONA Logo Oficial"
              className="w-10 h-10 object-contain drop-shadow-[0_2px_8px_rgba(46,204,113,0.3)]"
            />
            <div className="flex flex-col text-left">
              <strong className="text-xl font-black tracking-wider text-white font-['Barlow_Condensed'] leading-none">
                AGRONA <span className="text-[#A3E635]">JARDINERÍA</span>
              </strong>
              <small className="text-[10px] tracking-[0.25em] text-[#2ECC71] uppercase mt-1">
                Espacios que se sienten vivos
              </small>
            </div>
          </div>

          <div className="footer-meta">
            <span>© {new Date().getFullYear()} AGRONA Jardinería S.A.S. Todos los derechos reservados.</span>
          </div>

          <button
            className="footer-top"
            onClick={() => scrollToSection("inicio")}
            aria-label="Volver arriba"
          >
            <ArrowDown size={18} className="rotate-180" />
          </button>
        </div>
      </footer>
    </div>
  );
}

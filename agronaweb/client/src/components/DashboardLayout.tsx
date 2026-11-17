import { useAuth } from "@/_core/hooks/useAuth";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarInset,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  SidebarTrigger,
  useSidebar,
} from "@/components/ui/sidebar";
import { startLogin } from "@/const";
import { useIsMobile } from "@/hooks/useMobile";
import {
  LayoutDashboard,
  LogOut,
  PanelLeft,
  ShoppingBag,
  Sprout,
  TreePine,
  Users,
} from "lucide-react";
import { CSSProperties, useEffect, useRef, useState } from "react";
import { useLocation } from "wouter";
import { DashboardLayoutSkeleton } from './DashboardLayoutSkeleton';
import { Button } from "./ui/button";

const menuItems = [
  { icon: LayoutDashboard, label: "Inicio & Métricas", path: "/" },
  { icon: Sprout, label: "Catálogo & Plantas", path: "/#catalogo" },
  { icon: ShoppingBag, label: "Gestión de Pedidos", path: "/#pedidos" },
  { icon: Users, label: "Clientes", path: "/#clientes" },
  { icon: TreePine, label: "Paisajismo & Proyectos", path: "/#proyectos" },
];

const SIDEBAR_WIDTH_KEY = "sidebar-width";
const DEFAULT_WIDTH = 280;
const MIN_WIDTH = 200;
const MAX_WIDTH = 480;

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [sidebarWidth, setSidebarWidth] = useState(() => {
    const saved = localStorage.getItem(SIDEBAR_WIDTH_KEY);
    return saved ? parseInt(saved, 10) : DEFAULT_WIDTH;
  });
  const { loading, user } = useAuth();

  useEffect(() => {
    localStorage.setItem(SIDEBAR_WIDTH_KEY, sidebarWidth.toString());
  }, [sidebarWidth]);

  if (loading) {
    return <DashboardLayoutSkeleton />
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0B1410] text-[#F1F5F2]">
        <div className="flex flex-col items-center gap-8 p-8 max-w-md w-full rounded-2xl bg-[#14211A] border border-[#23382C] shadow-2xl">
          <div className="flex flex-col items-center gap-4 text-center">
            <img
              src="/agrona_logo_icon.png"
              alt="AGRONA Jardinería"
              className="h-16 w-16 object-contain drop-shadow-[0_4px_16px_rgba(46,204,113,0.4)]"
            />
            <div>
              <h1 className="text-3xl font-extrabold tracking-wide font-['Barlow_Condensed'] uppercase text-white">
                AGRONA <span className="text-[#A3E635]">Jardinería</span>
              </h1>
              <p className="text-xs uppercase tracking-[0.2em] text-[#2ECC71] mt-1 font-semibold">
                Portal de Gestión & Operaciones
              </p>
            </div>
            <p className="text-sm text-[#8FA697] max-w-sm mt-2">
              Inicia sesión con tu cuenta corporativa para acceder al panel de control, inventarios y despacho.
            </p>
          </div>
          <Button
            onClick={() => startLogin()}
            size="lg"
            className="w-full bg-gradient-to-r from-[#2ECC71] to-[#27AE60] text-[#07150F] font-bold uppercase tracking-wider hover:from-[#A3E635] hover:to-[#2ECC71] shadow-lg shadow-[#2ECC71]/20 transition-all"
          >
            Iniciar Sesión
          </Button>
        </div>
      </div>
    );
  }

  return (
    <SidebarProvider
      style={
        {
          "--sidebar-width": `${sidebarWidth}px`,
        } as CSSProperties
      }
    >
      <DashboardLayoutContent setSidebarWidth={setSidebarWidth}>
        {children}
      </DashboardLayoutContent>
    </SidebarProvider>
  );
}

type DashboardLayoutContentProps = {
  children: React.ReactNode;
  setSidebarWidth: (width: number) => void;
};

function DashboardLayoutContent({
  children,
  setSidebarWidth,
}: DashboardLayoutContentProps) {
  const { user, logout } = useAuth();
  const [location, setLocation] = useLocation();
  const { state, toggleSidebar } = useSidebar();
  const isCollapsed = state === "collapsed";
  const [isResizing, setIsResizing] = useState(false);
  const sidebarRef = useRef<HTMLDivElement>(null);
  const activeMenuItem = menuItems.find(item => item.path === location);
  const isMobile = useIsMobile();

  useEffect(() => {
    if (isCollapsed) {
      setIsResizing(false);
    }
  }, [isCollapsed]);

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isResizing) return;

      const sidebarLeft = sidebarRef.current?.getBoundingClientRect().left ?? 0;
      const newWidth = e.clientX - sidebarLeft;
      if (newWidth >= MIN_WIDTH && newWidth <= MAX_WIDTH) {
        setSidebarWidth(newWidth);
      }
    };

    const handleMouseUp = () => {
      setIsResizing(false);
    };

    if (isResizing) {
      document.addEventListener("mousemove", handleMouseMove);
      document.addEventListener("mouseup", handleMouseUp);
      document.body.style.cursor = "col-resize";
      document.body.style.userSelect = "none";
    }

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      document.body.style.cursor = "";
      document.body.style.userSelect = "";
    };
  }, [isResizing, setSidebarWidth]);

  return (
    <>
      <div className="relative" ref={sidebarRef}>
        <Sidebar
          collapsible="icon"
          className="border-r border-[#23382C] bg-[#0E1813] text-[#F1F5F2]"
          disableTransition={isResizing}
        >
          <SidebarHeader className="h-20 justify-center border-b border-[#23382C]/60 px-3">
            <div className="flex items-center gap-3 transition-all w-full">
              <a href="/" className="flex items-center gap-3 min-w-0 flex-1 group">
                <img
                  src="/agrona_logo_icon.png"
                  alt="AGRONA Logo"
                  className="h-10 w-10 object-contain drop-shadow-[0_2px_10px_rgba(46,204,113,0.35)] shrink-0 transition-transform group-hover:scale-105"
                />
                {!isCollapsed && (
                  <div className="flex flex-col min-w-0">
                    <span className="font-extrabold tracking-wider text-lg text-white leading-none font-['Barlow_Condensed']">
                      AGRONA
                    </span>
                    <span className="text-[9px] font-bold tracking-[0.25em] text-[#A3E635] uppercase mt-1 leading-none">
                      JARDINERÍA
                    </span>
                  </div>
                )}
              </a>
              <button
                onClick={toggleSidebar}
                className="h-8 w-8 flex items-center justify-center hover:bg-[#1A2E22] text-[#8FA697] hover:text-[#2ECC71] rounded-lg transition-colors focus:outline-none shrink-0"
                aria-label="Toggle navigation"
              >
                <PanelLeft className="h-4 w-4" />
              </button>
            </div>
          </SidebarHeader>

          <SidebarContent className="gap-0 py-3">
            <SidebarMenu className="px-2 space-y-1">
              {menuItems.map(item => {
                const isActive = location === item.path;
                return (
                  <SidebarMenuItem key={item.path}>
                    <SidebarMenuButton
                      isActive={isActive}
                      onClick={() => setLocation(item.path)}
                      tooltip={item.label}
                      className={`h-11 transition-all rounded-xl ${
                        isActive
                          ? "bg-[#1A3125] text-[#2ECC71] font-semibold border-l-4 border-[#2ECC71]"
                          : "text-[#8FA697] hover:text-[#F1F5F2] hover:bg-[#14231B]"
                      }`}
                    >
                      <item.icon
                        className={`h-5 w-5 ${isActive ? "text-[#A3E635]" : "text-[#8FA697]"}`}
                      />
                      <span className="text-sm">{item.label}</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarContent>

          <SidebarFooter className="p-3 border-t border-[#23382C]/60 bg-[#0B1410]/50">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button className="flex items-center gap-3 rounded-xl px-2 py-2 hover:bg-[#16271E] transition-colors w-full text-left group-data-[collapsible=icon]:justify-center focus:outline-none">
                  <Avatar className="h-9 w-9 border border-[#23382C] shrink-0">
                    <AvatarFallback className="text-xs font-bold bg-[#1A3125] text-[#2ECC71]">
                      {user?.name?.charAt(0).toUpperCase() || "A"}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1 min-w-0 group-data-[collapsible=icon]:hidden">
                    <p className="text-sm font-semibold text-[#F1F5F2] truncate leading-none">
                      {user?.name || "Operador Agrona"}
                    </p>
                    <p className="text-xs text-[#8FA697] truncate mt-1">
                      {user?.email || "operaciones@agrona.com"}
                    </p>
                  </div>
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-52 bg-[#14211A] border-[#23382C] text-[#F1F5F2]">
                <DropdownMenuItem
                  onClick={logout}
                  className="cursor-pointer text-red-400 hover:text-red-300 hover:bg-[#201515] focus:text-red-300 focus:bg-[#201515]"
                >
                  <LogOut className="mr-2 h-4 w-4" />
                  <span>Cerrar Sesión</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarFooter>
        </Sidebar>
        <div
          className={`absolute top-0 right-0 w-1 h-full cursor-col-resize hover:bg-[#2ECC71]/40 transition-colors ${isCollapsed ? "hidden" : ""}`}
          onMouseDown={() => {
            if (isCollapsed) return;
            setIsResizing(true);
          }}
          style={{ zIndex: 50 }}
        />
      </div>

      <SidebarInset className="bg-[#0B1410]">
        {isMobile && (
          <div className="flex border-b border-[#23382C] h-16 items-center justify-between bg-[#0E1813]/95 px-4 backdrop-blur sticky top-0 z-40">
            <div className="flex items-center gap-3">
              <SidebarTrigger className="h-9 w-9 rounded-lg bg-[#14211A] text-[#F1F5F2] border border-[#23382C]" />
              <div className="flex items-center gap-2">
                <img
                  src="/agrona_logo_icon.png"
                  alt="AGRONA"
                  className="h-7 w-7 object-contain"
                />
                <span className="font-bold text-sm tracking-wide text-white uppercase font-['Barlow_Condensed']">
                  AGRONA <span className="text-[#A3E635]">Jardinería</span>
                </span>
              </div>
            </div>
          </div>
        )}
        <main className="flex-1 p-4 md:p-6 bg-[#0B1410] text-[#F1F5F2] min-h-screen">
          {children}
        </main>
      </SidebarInset>
    </>
  );
}

package com.senati.biblioteca.bean;

import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.servicio.LibroService;
import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class NotificacionBean implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(NotificacionBean.class);
    private static final int VENTANA_POR_VENCER = 3;

    @Inject
    private PrestamoService prestamoService;

    @Inject
    private LibroService libroService;

    private List<Prestamo> notificaciones;
    private List<Prestamo> vencidos;
    private List<Prestamo> porVencer;
    private List<Libro> recomendados;
    private int conteo;

    @PostConstruct
    public void init() {
        cargarNotificaciones();
    }

    public void cargarNotificaciones() {
        notificaciones = new ArrayList<>();
        vencidos = new ArrayList<>();
        porVencer = new ArrayList<>();
        conteo = 0;

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        if (session == null) {
            log.warn("[NOTIF] No hay sesion HTTP — saliendo");
            return;
        }

        String codigo = (String) session.getAttribute("codigoUniversitario");
        String rol = (String) session.getAttribute("rol");
        log.info("[NOTIF] codigo='{}' rol='{}' hoy={}", codigo, rol, LocalDate.now());

        if (codigo == null || !"ESTUDIANTE".equals(rol)) {
            log.warn("[NOTIF] Usuario no es ESTUDIANTE — saliendo (codigo={}, rol={})", codigo, rol);
            return;
        }

        try {
            List<Prestamo> activos = prestamoService.buscarPrestamosActivosPorUsuario(codigo);
            log.info("[NOTIF] Prestamos activos encontrados para {}: {}", codigo, activos.size());

            for (Prestamo p : activos) {
                long delta = diasHastaVencimiento(p);
                log.info("[NOTIF]   id={} libro='{}' fechaVenc={} delta={} → {}",
                    p.getId(), p.getLibro().getTitulo(), p.getFechaDevolucionEstimada(), delta,
                    (delta < 0 ? "VENCIDO" : (delta <= VENTANA_POR_VENCER ? "POR VENCER" : "fuera de ventana")));
                if (delta < 0) {
                    vencidos.add(p);
                    notificaciones.add(p);
                    conteo++;
                } else if (delta <= VENTANA_POR_VENCER) {
                    porVencer.add(p);
                    notificaciones.add(p);
                    conteo++;
                }
            }

            vencidos.sort((a, b) -> Long.compare(diasHastaVencimiento(a), diasHastaVencimiento(b)));
            porVencer.sort((a, b) -> Long.compare(diasHastaVencimiento(a), diasHastaVencimiento(b)));
            log.info("[NOTIF] Total notificaciones: {} (vencidos={}, porVencer={})",
                conteo, vencidos.size(), porVencer.size());
        } catch (Exception e) {
            log.error("[NOTIF] Error cargando notificaciones para {}: {}", codigo, e.getMessage(), e);
        }

        recomendados = libroService.listarMejorPuntuados(5);
    }

    public boolean esVencido(Prestamo p) {
        return diasHastaVencimiento(p) < 0;
    }

    public long getDiasVencido(Prestamo p) {
        return Math.abs(diasHastaVencimiento(p));
    }

    public long getDiasRestantes(Prestamo p) {
        return Math.max(0, diasHastaVencimiento(p));
    }

    public String getEtiqueta(Prestamo p) {
        long delta = diasHastaVencimiento(p);
        if (delta < 0) {
            long dias = Math.abs(delta);
            return dias == 1 ? "Vencido hace 1 día" : "Vencido hace " + dias + " días";
        }
        if (delta == 0) return "Vence hoy";
        if (delta == 1) return "Vence mañana";
        return "Vence en " + delta + " días";
    }

    public String getBadge(Prestamo p) {
        long delta = diasHastaVencimiento(p);
        if (delta < 0) return "-" + Math.abs(delta) + "d";
        if (delta == 0) return "Hoy";
        return delta + "d";
    }

    private long diasHastaVencimiento(Prestamo p) {
        LocalDate hoy = LocalDate.now();
        LocalDate vence = toLocalDate(p.getFechaDevolucionEstimada());
        return ChronoUnit.DAYS.between(hoy, vence);
    }

    private LocalDate toLocalDate(Date fecha) {
        if (fecha instanceof java.sql.Date) {
            return ((java.sql.Date) fecha).toLocalDate();
        }
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public List<Prestamo> getNotificaciones() { return notificaciones; }
    public List<Prestamo> getVencidos() { return vencidos; }
    public List<Prestamo> getPorVencer() { return porVencer; }
    public List<Libro> getRecomendados() { return recomendados; }
    public int getConteo() { return conteo; }
    public boolean isHayNotificaciones() { return conteo > 0; }
    public boolean isHayVencidos() { return vencidos != null && !vencidos.isEmpty(); }
    public boolean isHayPorVencer() { return porVencer != null && !porVencer.isEmpty(); }
    public boolean isHayRecomendados() { return recomendados != null && !recomendados.isEmpty(); }
}

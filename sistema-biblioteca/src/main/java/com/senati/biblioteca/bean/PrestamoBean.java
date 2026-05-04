package com.senati.biblioteca.bean;

import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class PrestamoBean implements Serializable {

    @Inject
    private PrestamoService prestamoService;

    private List<Prestamo> prestamosActivos;
    private List<Prestamo> historial;

    public void cargarPrestamosActivos() {
        String codigo = getCodigoSesion();
        if (codigo != null) {
            prestamosActivos = prestamoService.buscarPrestamosActivosPorUsuario(codigo);
        }
    }

    public void cargarHistorial() {
        String codigo = getCodigoSesion();
        if (codigo != null) {
            historial = prestamoService.buscarPrestamosPorUsuario(codigo);
        }
    }

    public String solicitarPrestamo(Long libroId) {
        String codigo = getCodigoSesion();
        if (codigo == null) return "login?faces-redirect=true";
        try {
            prestamoService.registrarPrestamo(codigo, libroId);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Prestamo registrado. Tienes 14 dias para devolverlo."));
            cargarPrestamosActivos();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
        return null;
    }

    public void registrarDevolucion(Long prestamoId) {
        prestamoService.registrarDevolucion(prestamoId);
        cargarPrestamosActivos();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Devolucion registrada correctamente."));
    }

    private String getCodigoSesion() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null ? (String) session.getAttribute("codigoUniversitario") : null;
    }

    public List<Prestamo> getPrestamosActivos() { return prestamosActivos; }
    public List<Prestamo> getHistorial() { return historial; }
}

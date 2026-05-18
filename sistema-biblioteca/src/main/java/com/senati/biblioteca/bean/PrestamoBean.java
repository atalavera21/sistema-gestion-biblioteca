package com.senati.biblioteca.bean;

import com.senati.biblioteca.dao.UsuarioDAO;
import com.senati.biblioteca.dao.ValoracionDAO;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.modelo.Usuario;
import com.senati.biblioteca.modelo.Valoracion;
import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Named
@SessionScoped
public class PrestamoBean implements Serializable {

    @Inject
    private PrestamoService prestamoService;

    @Inject
    private ValoracionDAO valoracionDAO;

    @Inject
    private UsuarioDAO usuarioDAO;

    private List<Prestamo> prestamosActivos;
    private List<Prestamo> historial;
    private Libro libroParaSolicitar;

    @PostConstruct
    public void init() {
        cargarPrestamosActivos();
        cargarHistorial();
    }

    public void prepararSolicitud(Libro libro) {
        this.libroParaSolicitar = libro;
    }

    public void cargarPrestamosActivos() {
        String codigo = getCodigoSesion();
        if (codigo != null) {
            try {
                prestamosActivos = prestamoService.buscarPrestamosActivosPorUsuario(codigo);
            } catch (Exception e) {
                // usuario no encontrado o sin prestamos
            }
        }
    }

    public void cargarHistorial() {
        String codigo = getCodigoSesion();
        if (codigo != null) {
            try {
                historial = prestamoService.buscarPrestamosPorUsuario(codigo);
            } catch (Exception e) {
                // usuario no encontrado o sin historial
            }
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

    public List<Integer> estrellasValoracion(Libro libro, String codigo) {
        return Arrays.asList(1, 2, 3, 4, 5);
    }

    public int ratingActual(Long libroId, String codigo) {
        Optional<Usuario> user = usuarioDAO.findByCodigoUniversitario(codigo);
        if (user.isEmpty()) return 0;
        Optional<Valoracion> val = valoracionDAO.findByLibroAndUsuario(libroId, user.get().getId());
        return val.map(Valoracion::getPuntuacion).orElse(0);
    }

    public boolean yaValorado(Long libroId, String codigo) {
        return ratingActual(libroId, codigo) > 0;
    }

    public void calificar(Long libroId, int puntuacion) {
        String codigo = getCodigoSesion();
        if (codigo == null) return;
        Optional<Usuario> user = usuarioDAO.findByCodigoUniversitario(codigo);
        if (user.isEmpty()) return;

        if (valoracionDAO.findByLibroAndUsuario(libroId, user.get().getId()).isPresent()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya valorado", "Ya has valorado este libro."));
            return;
        }

        valoracionDAO.save(libroId, user.get().getId(), puntuacion);
        cargarHistorial();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Valoracion guardada. Gracias por tu opinion."));
    }

    private String getCodigoSesion() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null ? (String) session.getAttribute("codigoUniversitario") : null;
    }

    public List<Prestamo> getPrestamosActivos() { return prestamosActivos; }
    public List<Prestamo> getHistorial() { return historial; }
    public Libro getLibroParaSolicitar() { return libroParaSolicitar; }
}

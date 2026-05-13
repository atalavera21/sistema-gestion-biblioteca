package com.senati.biblioteca.bean;

import com.senati.biblioteca.servicio.AuthService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    @Inject
    private AuthService authService;

    @Inject
    private NotificacionBean notificacionBean;

    private String username;
    private String password;

    public String login() {
        if (username == null || username.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Ingrese su usuario."));
            return null;
        }

        String usuarioSinDominio = username.contains("@") ? username.split("@")[0] : username;

        if (authService.autenticar(usuarioSinDominio, password)) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
            session.setAttribute("codigoUniversitario", usuarioSinDominio);
            session.setAttribute("autenticado", true);

            String rol = authService.obtenerRol(usuarioSinDominio);
            session.setAttribute("rol", rol);

            if ("ESTUDIANTE".equals(rol)) {
                notificacionBean.cargarNotificaciones();
            }

            if ("ADMIN".equals(rol)) {
                return "/admin/index?faces-redirect=true";
            }
            return "/catalogo?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "Usuario o contrasena invalidos."));
        return null;
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        if (session != null) session.invalidate();
        return "/index?faces-redirect=true";
    }

    public boolean isAutenticado() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null && session.getAttribute("autenticado") != null;
    }

    public String getCodigo() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null ? (String) session.getAttribute("codigoUniversitario") : null;
    }

    public String getRol() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null ? (String) session.getAttribute("rol") : null;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

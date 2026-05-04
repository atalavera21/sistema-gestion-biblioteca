package com.senati.biblioteca.bean;

import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.servicio.LibroService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class DetalleLibroBean implements Serializable {

    @Inject
    private LibroService libroService;

    private Libro libro;

    public Libro getLibro() {
        if (libro == null) {
            String idParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("id");
            if (idParam != null) {
                libro = libroService.buscarPorId(Long.valueOf(idParam)).orElse(null);
            }
        }
        return libro;
    }
}

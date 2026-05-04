package com.senati.biblioteca.bean;

import com.senati.biblioteca.dao.CategoriaDAO;
import com.senati.biblioteca.modelo.Categoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
@FacesConverter(value = "categoriaConverter", managed = true)
public class CategoriaConverter implements Converter<Categoria> {

    @Inject
    private CategoriaDAO categoriaDAO;

    @Override
    public Categoria getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            Long id = Long.valueOf(value);
            return categoriaDAO.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Categoria value) {
        if (value == null || value.getId() == null) return "";
        return value.getId().toString();
    }
}

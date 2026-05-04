package com.senati.biblioteca.bean;

import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.servlet.http.HttpSession;

@FacesValidator("maxLibrosValidator")
public class MaxLibrosValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session == null || session.getAttribute("codigoUniversitario") == null) return;

        String codigo = (String) session.getAttribute("codigoUniversitario");
        PrestamoService service = CDI.current().select(PrestamoService.class).get();
        long activos = service.contarPrestamosActivos(codigo);

        if (activos >= 3) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Limite alcanzado",
                    "Tienes 3 libros sin devolver. Devuelve al menos uno para solicitar otro."));
        }
    }
}

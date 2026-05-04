package com.senati.biblioteca.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("codigoUniversitarioValidator")
public class CodigoUniversitarioValidator implements Validator {

    private static final String REGEX = "^U\\d{9}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        if (value == null) return;
        String codigo = value.toString().trim();
        if (!codigo.matches(REGEX)) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Codigo invalido",
                    "Formato esperado: U seguido de 9 digitos. Ejemplo: U202312345."));
        }
    }
}

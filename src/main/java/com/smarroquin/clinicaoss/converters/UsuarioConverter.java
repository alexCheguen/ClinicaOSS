package com.smarroquin.clinicaoss.converters;

import com.smarroquin.clinicaoss.models.Usuario;
import com.smarroquin.clinicaoss.repositories.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;


@Named("userConverter")
@FacesConverter(value = "userConverter", managed = true) // Ojo: en el xhtml se llama "userConverter"
@ApplicationScoped
public class UsuarioConverter implements Converter<Usuario> {

    @Inject
    private UsuarioRepository repo;

    @Override
    public Usuario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return repo.find(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Usuario value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
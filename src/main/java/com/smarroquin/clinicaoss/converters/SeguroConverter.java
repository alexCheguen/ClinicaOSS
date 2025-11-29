package com.smarroquin.clinicaoss.converters;

import com.smarroquin.clinicaoss.models.Seguro;
import com.smarroquin.clinicaoss.repositories.SeguroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jdk.jfr.Name;

@Named("seguroConverter")
@FacesConverter(value = "seguroConverter", managed = true)
@ApplicationScoped
public class SeguroConverter implements Converter<Seguro> {

    @Inject
    private SeguroRepository repo;

    @Override
    public Seguro getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return repo.find(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Seguro value) {
        return (value == null || value.getId() == null) ? "" : value.getId().toString();
    }
}


package com.smarroquin.clinicaoss.converters;

import com.smarroquin.clinicaoss.models.Descuento;
import com.smarroquin.clinicaoss.repositories.DescuentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("descuentoConverter")
@FacesConverter(value = "descuentoConverter", managed = true)
@ApplicationScoped
public class DescuentoConverter implements Converter<Descuento> {

    @Inject
    private DescuentoRepository repo;

    @Override
    public Descuento getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Descuento value) {
        return (value == null || value.getId() == null) ? "" : value.getId().toString();
    }
}


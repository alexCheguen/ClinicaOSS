package com.smarroquin.clinicaoss.converters;

import com.smarroquin.clinicaoss.models.Tratamiento;
import com.smarroquin.clinicaoss.repositories.TratamientoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;


@Named("tratamientoConverter")
@FacesConverter(value = "tratamientoConverter", managed = true)
@ApplicationScoped
public class TratamientoConverter implements Converter<Tratamiento> {

    @Inject
    private TratamientoRepository repo;

    @Override
    public Tratamiento getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Tratamiento value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
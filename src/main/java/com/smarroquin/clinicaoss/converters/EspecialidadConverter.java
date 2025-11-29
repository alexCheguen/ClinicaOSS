package com.smarroquin.clinicaoss.converters;


import com.smarroquin.clinicaoss.models.Especialidad;
import com.smarroquin.clinicaoss.repositories.CitaRepository;
import com.smarroquin.clinicaoss.repositories.EspecialidadesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("especialidadConverter")
@FacesConverter(value = "especialidadConverter", managed = true)
@ApplicationScoped
public class EspecialidadConverter implements Converter<Especialidad> {

    @Inject
    private EspecialidadesRepository repo;

    @Override
    public Especialidad getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Especialidad value) {
        return (value == null || value.getId() == null) ? "" : value.getId().toString();
    }
}



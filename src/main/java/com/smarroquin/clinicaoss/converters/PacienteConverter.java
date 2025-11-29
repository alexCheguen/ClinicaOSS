package com.smarroquin.clinicaoss.converters;

import com.smarroquin.clinicaoss.models.Paciente;
import com.smarroquin.clinicaoss.repositories.PacienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("pacienteConverter")
@FacesConverter(value = "pacienteConverter", managed = true)
@ApplicationScoped
public class PacienteConverter implements Converter<Paciente> {

    @Inject
    private PacienteRepository repo;

    @Override
    public Paciente getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Paciente value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
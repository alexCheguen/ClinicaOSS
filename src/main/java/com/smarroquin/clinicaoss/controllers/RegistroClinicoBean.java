package com.smarroquin.clinicaoss.controllers;

import com.smarroquin.clinicaoss.models.RegistroClinico;
import com.smarroquin.clinicaoss.models.Paciente;
import com.smarroquin.clinicaoss.models.Usuario;
import com.smarroquin.clinicaoss.models.Cita;
import com.smarroquin.clinicaoss.enums.tipo_archivo;
import com.smarroquin.clinicaoss.service.CatalogService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.file.UploadedFile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class RegistroClinicoBean extends Bean<RegistroClinico> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private transient CatalogService service;


    @Override
    protected RegistroClinico createNew() {
        RegistroClinico r = new RegistroClinico();
        // Valores por defecto para evitar errores de validación si no se llenan
        r.setTipo_contenido("application/pdf");
        r.setBlobName("Registro Físico");
        r.setBlob_url("#");
        return r;
    }

    @Override
    protected List<RegistroClinico> findAll() {
        return service.registrosClinicos();
    }

    @Override
    protected void persist(RegistroClinico entity) {
        service.guardarRegistro(entity);
    }

    @Override
    protected void remove(RegistroClinico entity) {
        service.eliminarRegistro(entity);
    }


    @Override
    protected Map<String, String> fieldLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("numeroRegistro", "Número de registro");
        labels.put("tipo_archivo", "Tipo de archivo");
        labels.put("tipo_contenido", "Tipo de contenido (MIME)");
        labels.put("blob_url", "URL del archivo");
        labels.put("blobName", "Nombre del archivo");
        labels.put("fechaCarga", "Fecha de carga");
        labels.put("paciente", "Paciente");
        labels.put("user", "Usuario");
        labels.put("cita", "Cita asociada");
        return labels;
    }

    @Override
    protected String getFacesClientId() {
        return "frmRegistros:msgRegistro";
    }

    @Override
    protected String successSaveMessage() {
        return "Registro clínico guardado";
    }

    @Override
    protected String successDeleteMessage() {
        return "Registro clínico eliminado";
    }

    // Métodos auxiliares para combos
    public List<Paciente> getPacientes() {
        return service.pacientes();
    }

    public List<Usuario> getUsuarios() {
        return service.odontologos();
    }

    public List<Cita> getCitas() {
        return service.citas();
    }

    public tipo_archivo[] getTiposArchivo() {
        return tipo_archivo.values();
    }
}


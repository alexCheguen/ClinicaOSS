package com.smarroquin.clinicaoss.controllers;

import com.smarroquin.clinicaoss.models.Facturacion;
import com.smarroquin.clinicaoss.models.Tratamiento;
import com.smarroquin.clinicaoss.models.Paciente;
import com.smarroquin.clinicaoss.models.Cita;
import com.smarroquin.clinicaoss.models.Descuento;
import com.smarroquin.clinicaoss.models.Seguro;
import com.smarroquin.clinicaoss.enums.estado_pago;
import com.smarroquin.clinicaoss.service.CatalogService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class FacturacionBean extends Bean<Facturacion> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private transient CatalogService service;

    @Override
    protected Facturacion createNew() {
        Facturacion f = new Facturacion();
        f.setFechaEmision(LocalDateTime.now());
        f.setEstado_pago(estado_pago.PENDIENTE); // Pendiente por defecto
        f.setSubtotal(BigDecimal.ZERO);
        f.setTotal(BigDecimal.ZERO);
        return f;
    }

    public String formatFecha(LocalDateTime fecha) {
        if (fecha == null) return "";
        return fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void onTratamientoSelect() {
        if (selected.getTratamiento() != null) {
            // Asigna el costo del tratamiento al subtotal
            selected.setSubtotal(selected.getTratamiento().getCosto());
        } else {
            selected.setSubtotal(BigDecimal.ZERO);
        }
        // Recalcula el total final
        calcularTotal();
    }

    public void calcularTotal() {
        // Evitar nulos
        BigDecimal sub = selected.getSubtotal() != null ? selected.getSubtotal() : BigDecimal.ZERO;
        BigDecimal descuentoMonto = BigDecimal.ZERO;
        BigDecimal seguroMonto = BigDecimal.ZERO;

        // Calcular Descuento (Si existe)
        if (selected.getDescuento() != null && selected.getDescuento().getDescuentoPromocion() != null) {
            BigDecimal porc = BigDecimal.valueOf(selected.getDescuento().getDescuentoPromocion());
            // Formula: subtotal * (porcentaje / 100)
            descuentoMonto = sub.multiply(porc).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }

        // Calcular Seguro (Si existe)
        if (selected.getSeguro() != null && selected.getSeguro().getPorcentajeDescuento() != null) {
            BigDecimal porc = BigDecimal.valueOf(selected.getSeguro().getPorcentajeDescuento());
            seguroMonto = sub.multiply(porc).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }

        // Total = Subtotal - Descuento - Seguro
        BigDecimal totalFinal = sub.subtract(descuentoMonto).subtract(seguroMonto);

        // Evitar negativos
        if (totalFinal.compareTo(BigDecimal.ZERO) < 0) {
            totalFinal = BigDecimal.ZERO;
        }

        selected.setTotal(totalFinal);
    }

    @Override
    protected List<Facturacion> findAll() {
        return service.facturaciones();
    }

    @Override
    protected void persist(Facturacion entity) {

        if (entity.getFechaEmision() == null) {
            entity.setFechaEmision(LocalDateTime.now());
        }
        if (entity.getSubtotal() == null) entity.setSubtotal(BigDecimal.ZERO);
        if (entity.getTotal() == null) entity.setTotal(BigDecimal.ZERO);
        if (entity.getEstado_pago() == null) {
            if (entity.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                entity.setEstado_pago(estado_pago.PENDIENTE);
            }
            else {
                entity.setEstado_pago(estado_pago.PAGADO);
            }
        }
        service.guardarFacturacion(entity);
    }

    @Override
    protected void remove(Facturacion entity) {
        // En facturación, NUNCA se elimina físicamente, se ANULA.
        entity.setEstado_pago(estado_pago.ANULADO);
        service.guardarFacturacion(entity);
    }

    @Override
    protected Map<String, String> fieldLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("fechaEmision", "Fecha de emisión");
        labels.put("subtotal", "Subtotal");
        labels.put("total", "Total");
        labels.put("estado_pago", "Estado de pago");
        labels.put("tratamiento", "Tratamiento");
        labels.put("paciente", "Paciente");
        labels.put("cita", "Cita");
        labels.put("descuento", "Descuento");
        labels.put("seguro", "Seguro");
        return labels;
    }

    @Override
    protected String getFacesClientId() {
        return "frmFacturacion:msgFacturacion";
    }

    @Override
    protected String successSaveMessage() {
        return "Factura guardada";
    }

    @Override
    protected String successDeleteMessage() {
        return "Factura eliminada";
    }

    // Métodos auxiliares para combos
    public List<Tratamiento> getTratamientos() {
        return service.tratamientos();
    }

    public List<Paciente> getPacientes() {
        return service.pacientes();
    }

    public List<Cita> getCitas() {
        return service.citas();
    }

    public List<Descuento> getDescuentos() {
        return service.descuentos();
    }

    public List<Seguro> getSeguros() {
        return service.seguros();
    }

    public estado_pago[] getEstadosPago() {
        return estado_pago.values();
    }
}

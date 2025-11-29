package com.smarroquin.clinicaoss.controllers;

import com.smarroquin.clinicaoss.models.Facturacion;
import com.smarroquin.clinicaoss.service.CatalogService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.optionconfig.title.Title;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class HomeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CatalogService service;

    // FECHA SELECCIONADA POR EL USUARIO (Default: Hoy)
    private LocalDate fechaFiltro;

    // --- CARDS ---
    private Long pacientesNuevos;
    private Long citasHoy; // Este lo dejaremos como "Citas del Mes" o rango seleccionado
    private BigDecimal ingresosMes;
    private Long tratamientosSemana;
    private Long canceladasHoy;
    private Long odontologosActivos;

    // --- CHARTS ---
    private BarChartModel ingresosChart;
    private DonutChartModel tratamientosChart;
    private BarChartModel doctoresChart;

    @PostConstruct
    public void init() {
        this.fechaFiltro = LocalDate.now(); // Inicia con el mes actual
        filtrar(); // Carga datos iniciales
    }

    /**
     * MÉTODO CLAVE: Se ejecuta al cambiar la fecha en el calendario
     */
    public void filtrar() {
        // Definir el rango del Mes Seleccionado (Desde el día 1 hasta el último día)
        LocalDateTime inicioMes = fechaFiltro.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = fechaFiltro.withDayOfMonth(fechaFiltro.lengthOfMonth()).atTime(LocalTime.MAX);

        // Para métricas semanales/diarias específicas
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime inicioSemana = LocalDate.now().minusDays(7).atStartOfDay();

        // 1. CARGAR TARJETAS (Usamos el mes seleccionado para Ingresos y Staff)
        // Pacientes y Citas del día se pueden mantener "globales" o ajustarse al mes.
        // Aquí ajustaré Ingresos al mes seleccionado.

        this.ingresosMes = service.sumIngresosEnRango(inicioMes, finMes);
        this.odontologosActivos = service.countOdontologosEnRango(inicioMes, finMes);

        // Estas métricas suelen ser "Al día de hoy", las dejamos fijas o las movemos al mes si prefieres
        this.pacientesNuevos = service.countPacientesNuevos(inicioSemana, finHoy);
        this.citasHoy = service.countCitasEnRango(inicioHoy, finHoy);
        this.tratamientosSemana = service.countTratamientosEnRango(inicioSemana, finHoy);
        this.canceladasHoy = service.countCanceladasEnRango(inicioHoy, finHoy);

        // 2. ACTUALIZAR GRÁFICAS CON EL RANGO DEL MES SELECCIONADO
        createIngresosChart(inicioMes, finMes);
        createTratamientosChart(inicioMes, finMes);
        createDoctoresChart(inicioMes, finMes);
    }

    // GRÁFICA DE BARRAS: Ingresos Diarios
    public void createIngresosChart(LocalDateTime inicio, LocalDateTime fin) {
        ingresosChart = new BarChartModel();
        ChartData data = new ChartData();

        List<Facturacion> facturas = service.getFacturasPorRango(inicio, fin);

        // Agrupar por día
        Map<Integer, BigDecimal> porDia = facturas.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getFechaEmision().getDayOfMonth(),
                        Collectors.reducing(BigDecimal.ZERO, Facturacion::getTotal, BigDecimal::add)
                ));

        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Ingresos (Q)");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        // Recorrer todos los días del mes seleccionado
        int diasEnMes = fechaFiltro.lengthOfMonth();
        for (int i = 1; i <= diasEnMes; i++) {
            labels.add(String.valueOf(i));
            values.add(porDia.getOrDefault(i, BigDecimal.ZERO));
            bgColors.add("rgba(75, 192, 192, 0.2)");
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setBorderWidth(1);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        ingresosChart.setData(data);

        // Título Dinámico
        String nombreMes = fechaFiltro.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
        BarChartOptions options = new BarChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Evolución de Ingresos - " + nombreMes + " " + fechaFiltro.getYear());
        options.setTitle(title);
        ingresosChart.setOptions(options);
    }

    // GRÁFICA DE DONA
    public void createTratamientosChart(LocalDateTime inicio, LocalDateTime fin) {
        tratamientosChart = new DonutChartModel();
        ChartData data = new ChartData();

        List<Object[]> resultados = service.getTratamientosPopulares(inicio, fin);

        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};

        int i = 0;
        for (Object[] obj : resultados) {
            labels.add((String) obj[0]);
            values.add((Long) obj[1]);
            bgColors.add(colors[i % colors.length]);
            i++;
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        tratamientosChart.setData(data);
    }

    // GRÁFICA BARRAS DOCTORES
    public void createDoctoresChart(LocalDateTime inicio, LocalDateTime fin) {
        doctoresChart = new BarChartModel();
        ChartData data = new ChartData();
        List<Object[]> resultados = service.getCitasPorDoctor(inicio, fin);

        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Pacientes Atendidos");
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        for (Object[] obj : resultados) {
            labels.add((String) obj[0]);
            values.add((Long) obj[1]);
            bgColors.add("rgba(54, 162, 235, 0.5)");
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("rgb(54, 162, 235)");
        dataSet.setBorderWidth(1);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        doctoresChart.setData(data);
    }

    // Getters y Setters
    public LocalDate getFechaFiltro() { return fechaFiltro; }
    public void setFechaFiltro(LocalDate fechaFiltro) { this.fechaFiltro = fechaFiltro; }

    public Long getPacientesNuevos() { return pacientesNuevos; }
    public Long getCitasHoy() { return citasHoy; }
    public BigDecimal getIngresosMes() { return ingresosMes; }
    public Long getTratamientosSemana() { return tratamientosSemana; }
    public Long getCanceladasHoy() { return canceladasHoy; }
    public Long getOdontologosActivos() { return odontologosActivos; }
    public BarChartModel getIngresosChart() { return ingresosChart; }
    public DonutChartModel getTratamientosChart() { return tratamientosChart; }
    public BarChartModel getDoctoresChart() { return doctoresChart; }
}

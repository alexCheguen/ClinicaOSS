package com.smarroquin.clinicaoss.service;


import com.smarroquin.clinicaoss.dto.*;
import com.smarroquin.clinicaoss.models.*;
import com.smarroquin.clinicaoss.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Named
@ApplicationScoped
public class CatalogService implements Serializable {
    private static final long serialVersionUID = 1L;

    public CatalogService() {
    }

    @Inject
    private UsuarioRepository userRepository;


    @Inject
    private PacienteRepository pacienteRepository;

    @Inject
    private JornadaLaboralRepository jornadalaboralRepository;

    @Inject
    private CitaRepository citaRepository;

    @Inject
    private TratamientoRepository tratamientoRepository;

    @Inject
    private EspecialidadesRepository especialidadesRepository;

    @Inject
    private RegistroClinicoRepository repositorioClinicoRepository;

    @Inject
    private FacturacionRepository facturacionRepository;

    @Inject
    private DescuentoRepository descuentoRepository;

    @Inject
    private SeguroRepository seguroRepository;


    //Users
    public List<Usuario> usuario() {
        return userRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario user) {
        return userRepository.guardar(user);
    }

    public void eliminarUsuario(Usuario user) {
        userRepository.eliminar(user);
    }

    public Usuario findUserById(Long id) {
        return userRepository.find(id);
    }

    //Pacientes
    public List<Paciente> pacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente guardarPaciente(Paciente paciente) {
        return pacienteRepository.guardar(paciente);
    }

    public void eliminarPaciente(Paciente paciente) {
        pacienteRepository.eliminar(paciente);
    }

    public Paciente findPacienteById(Long id) {
        return pacienteRepository.find(id);
    }

    //JornadaLaboral
    public List<JornadaLaboral> jornadasPorUsuario(Usuario userContext) {
        return jornadalaboralRepository.findAll();
    }

    public JornadaLaboral guardarJornada(JornadaLaboral jornadaLaboral) {
        return jornadalaboralRepository.guardar(jornadaLaboral);
    }

    public void eliminarJornada(JornadaLaboral jornadaLaboral) {
        jornadalaboralRepository.eliminar(jornadaLaboral);
    }

    //Citas
    public List<Cita> citas() {
        return citaRepository.findAll();
    }

    public Cita guardarCita(Cita cita) {
        return citaRepository.guardar(cita);
    }

    public void eliminarCita(Cita cita) {
        citaRepository.eliminar(cita);
    }

    public Cita findCitaById(Long id) {
        return citaRepository.find(id);
    }

    public Cita crearCita(CitaDTO citaDTO) {
        // Buscar el usuario por ID
        Usuario usuario = userRepository.find(citaDTO.getUserId());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con id: " + citaDTO.getUserId());
        }

        // Buscar el paciente por ID
        Paciente paciente = pacienteRepository.find(citaDTO.getPacienteId());
        if (paciente == null) {
            throw new RuntimeException("Paciente no encontrado con id: " + citaDTO.getPacienteId());
        }

        // Buscar el tratamiento por ID
        Tratamiento tratamiento = tratamientoRepository.find(citaDTO.getTratamientoId());
        if (tratamiento == null) {
            throw new RuntimeException("Tratamiento no encontrado con id: " + citaDTO.getTratamientoId());
        }

        // Mapear datos de la cita
        Cita cita = new Cita();
        cita.setCodigo(citaDTO.getCodigo());
        cita.setFechaApertura(citaDTO.getFechaApertura());
        cita.setEstado_cita(citaDTO.getEstado_cita());
        cita.setObservacionesCita(citaDTO.getObservacionesCita());

        // Asignar relaciones
        cita.setUser(usuario);
        cita.setPaciente(paciente);
        cita.setTratamiento(tratamiento);

        // Guardar la cita
        return citaRepository.guardar(cita);
    }


    //Tratamientos
    public List<Tratamiento> tratamientos() {
        return tratamientoRepository.findAll();
    }

    public Tratamiento guardarTratamiento(Tratamiento tratamiento) {
        return tratamientoRepository.guardar(tratamiento);
    }

    public void eliminarTratamiento(Tratamiento tratamiento) {
        tratamientoRepository.eliminar(tratamiento);
    }

    public Tratamiento findTratamientoById(Long id) {
        return tratamientoRepository.find(id);
    }

    public Tratamiento crearTratamiento(TratamientoDTO dto) {
        // Buscar la especialidad por ID
        Especialidad especialidad = especialidadesRepository.find(dto.getEspecialidadId());

        if (especialidad == null) {
            throw new RuntimeException("Especialidad no encontrada con id: " + dto.getEspecialidadId());
        }

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setNombreTratamiento(dto.getNombreTratamiento());
        tratamiento.setDescripcion(dto.getDescripcion());
        tratamiento.setCosto(dto.getCosto());
        tratamiento.setDuracionEstimado(dto.getDuracionEstimado());

        // Asignar la especialidad existente
        tratamiento.setEspecialidad(especialidad);

        return tratamientoRepository.guardar(tratamiento);
    }


    //Especialidad
    public List<Especialidad> especialidades() {
        return especialidadesRepository.findAll();
    }

    public Especialidad guardarEspecialidad(Especialidad especialidad) {
        return especialidadesRepository.guardar(especialidad);
    }

    public void eliminarEspecialidad(Especialidad especialidad) {
        especialidadesRepository.eliminar(especialidad);
    }

    public Especialidad findEspecialidadById(Long id) {
        return especialidadesRepository.find(id);
    }


    //Registro Clinico
    public List<RegistroClinico> registrosClinicos() {
        return repositorioClinicoRepository.findAll();
    }

    public RegistroClinico guardarRegistro(RegistroClinico registroClinico) {
        return repositorioClinicoRepository.guardar(registroClinico);
    }

    public void eliminarRegistro(RegistroClinico registroClinico) {
        repositorioClinicoRepository.eliminar(registroClinico);
    }

    public RegistroClinico findRegistroClinicoById(Long id) {
        return repositorioClinicoRepository.find(id);
    }

    //Facturacion
    public List<Facturacion> facturaciones() {
        return facturacionRepository.findAll();
    }

    public Facturacion guardarFacturacion(Facturacion facturacion) {
        return facturacionRepository.guardar(facturacion);
    }

    public void eliminarFacturacion(Facturacion facturacion) {
        facturacionRepository.eliminar(facturacion);
    }

    public Facturacion findFacturacionById(Long id) {
        return facturacionRepository.find(id);
    }

    //Descuento
    public List<Descuento> descuentos() {
        return descuentoRepository.findAll();
    }

    public Descuento guardarDescuento(Descuento descuento) {
        return descuentoRepository.guardar(descuento);
    }

    public void eliminarDescuento(Descuento descuento) {
        descuentoRepository.eliminar(descuento);
    }

    public Descuento findSescuentoById(Long id) {
        return descuentoRepository.find(id);
    }

    //Seguro
    public List<Seguro> seguros() {
        return seguroRepository.findAll();
    }

    public Seguro guardarSeguro(Seguro seguro) {
        return seguroRepository.guardar(seguro);
    }

    public void eliminarSeguro(Seguro seguro) {
        seguroRepository.eliminar(seguro);
    }

    public Seguro findSeguroById(Long id) {
        return seguroRepository.find(id);
    }

    public List<Cita> historialCitas(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public List<Facturacion> historialFacturas(Long pacienteId) {
        return facturacionRepository.findByPacienteId(pacienteId);
    }

    public List<Usuario> odontologos() {
        return userRepository.findByRolOdontologo();
    }

    // Métodos Dashboard Cards
    public Long countPacientesNuevos(LocalDateTime inicio, LocalDateTime fin) {
        return pacienteRepository.contarNuevos(inicio, fin);
    }

    public Long countCitasEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.contarCitas(inicio, fin);
    }

    public BigDecimal sumIngresosEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return facturacionRepository.sumarIngresos(inicio, fin);
    }

    public Long countTratamientosEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return facturacionRepository.contarTratamientos(inicio, fin);
    }

    public Long countCanceladasEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.contarCanceladas(inicio, fin);
    }

    public Long countOdontologosEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.contarOdontologosActivos(inicio, fin);
    }

    // Métodos Dashboard Gráficas
    public List<Object[]> getCitasPorDoctor(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.contarCitasPorDoctor(inicio, fin);
    }

    public List<Object[]> getTratamientosPopulares(LocalDateTime inicio, LocalDateTime fin) {
        return facturacionRepository.tratamientosPopulares(inicio, fin);
    }

    public List<Facturacion> getFacturasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return facturacionRepository.facturasDelMes(inicio, fin); // Reutilizamos el query, solo le pasamos fechas distintas
    }
}

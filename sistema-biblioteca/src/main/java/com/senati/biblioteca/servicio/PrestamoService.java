package com.senati.biblioteca.servicio;

import com.senati.biblioteca.dao.DevolucionDAO;
import com.senati.biblioteca.dao.LibroDAO;
import com.senati.biblioteca.dao.PrestamoDAO;
import com.senati.biblioteca.dao.UsuarioDAO;
import com.senati.biblioteca.modelo.Devolucion;
import com.senati.biblioteca.modelo.EstadoPrestamo;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.modelo.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class PrestamoService {

    @Inject
    private PrestamoDAO prestamoDAO;

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private LibroDAO libroDAO;

    @Inject
    private NotificacionService notificacionService;

    @Inject
    private DevolucionDAO devolucionDAO;

    public Prestamo registrarPrestamo(String codigoUniversitario, Long libroId) {
        Usuario usuario = usuarioDAO.findByCodigoUniversitario(codigoUniversitario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (usuario.isPenalizado()) {
            throw new IllegalStateException("Estudiante penalizado. No puede solicitar prestamos.");
        }

        long activos = prestamoDAO.countByUsuarioIdAndEstadoActivoVencido(usuario.getId());
        if (activos >= 3) {
            throw new IllegalStateException("Limite de 3 libros activos alcanzado.");
        }

        Libro libro = libroDAO.findById(libroId)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));

        long prestados = prestamoDAO.countByLibroIdAndEstadoActivoVencido(libroId);
        if (prestados >= libro.getStockTotal()) {
            throw new IllegalStateException("No hay stock disponible para este libro.");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(new Date());
        int diasPrestamo = libro.getDiasPrestamo() > 0 ? libro.getDiasPrestamo() : 14;
        prestamo.setFechaDevolucionEstimada(sumarDias(new Date(), diasPrestamo));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        return prestamoDAO.save(prestamo);
    }

    public Prestamo registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoDAO.findById(prestamoId);
        if (prestamo == null) {
            throw new IllegalArgumentException("Prestamo no encontrado.");
        }

        Date ahora = new Date();
        prestamo.setFechaDevolucionReal(ahora);
        Usuario usuario = prestamo.getUsuario();

        int diasRetraso = 0;
        boolean conRetraso = false;

        if (ahora.after(prestamo.getFechaDevolucionEstimada())) {
            prestamo.setEstado(EstadoPrestamo.PENALIZADO);
            usuario.setPenalizado(true);
            ajustarPuntuacion(usuario, -5.0);
            usuarioDAO.save(usuario);
            notificacionService.notificarPenalizacion(usuario.getCodigoUniversitario());
            long diff = ahora.getTime() - prestamo.getFechaDevolucionEstimada().getTime();
            diasRetraso = (int) (diff / (1000 * 60 * 60 * 24));
            conRetraso = true;
        } else {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            ajustarPuntuacion(usuario, +2.0);
            usuarioDAO.save(usuario);
        }

        Prestamo guardado = prestamoDAO.save(prestamo);

        Devolucion devolucion = new Devolucion();
        devolucion.setPrestamo(guardado);
        devolucion.setFechaDevolucion(ahora);
        devolucion.setDiasRetraso(diasRetraso);
        devolucion.setaTiempo(!conRetraso);
        devolucionDAO.guardar(devolucion);

        return guardado;
    }

    public List<Prestamo> buscarPrestamosPorUsuario(String codigoUniversitario) {
        Usuario usuario = usuarioDAO.findByCodigoUniversitario(codigoUniversitario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        return prestamoDAO.findHistorialByUsuario(usuario.getId());
    }

    public List<Prestamo> buscarPrestamosActivosPorUsuario(String codigoUniversitario) {
        Usuario usuario = usuarioDAO.findByCodigoUniversitario(codigoUniversitario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        return prestamoDAO.findActivosByUsuario(usuario.getId());
    }

    public List<Prestamo> buscarPrestamosVencidos() {
        return prestamoDAO.findVencidos();
    }

    public List<Prestamo> buscarPrestamosActivos() {
        return prestamoDAO.findAllActivos();
    }

    public List<Prestamo> buscarPrestamosActivosConFiltros(String keyword, Long categoriaId, Long usuarioId) {
        return prestamoDAO.searchActivos(keyword, categoriaId, usuarioId);
    }

    public List<Prestamo> buscarDevolucionesPendientes() {
        return prestamoDAO.findDevolucionesPendientes();
    }

    public List<Prestamo> buscarPrestamosPorRangoFechas(Date inicio, Date fin) {
        return prestamoDAO.findByRangoFechas(inicio, fin);
    }

    public long contarPrestamosActivos(String codigoUniversitario) {
        Usuario usuario = usuarioDAO.findByCodigoUniversitario(codigoUniversitario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        return prestamoDAO.countByUsuarioIdAndEstadoActivoVencido(usuario.getId());
    }

    private Date sumarDias(Date fecha, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    private void ajustarPuntuacion(Usuario usuario, double delta) {
        double actual = usuario.getPuntuacion() != null ? usuario.getPuntuacion() : 50.0;
        double nueva = Math.max(0.0, Math.min(100.0, actual + delta));
        usuario.setPuntuacion(nueva);
    }

    public List<Object[]> getTopLibrosPrestados(int limite) {
        return prestamoDAO.findLibrosMasPrestados(limite);
    }

    public List<Object[]> getTopCategorias(int limite) {
        return prestamoDAO.findLibrosPorCategoria(limite);
    }
}

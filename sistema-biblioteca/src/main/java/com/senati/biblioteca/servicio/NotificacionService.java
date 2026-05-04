package com.senati.biblioteca.servicio;

import com.senati.biblioteca.dao.PrestamoDAO;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;

@Singleton
public class NotificacionService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PrestamoDAO prestamoDAO;

    @Schedule(hour = "2", minute = "0", persistent = false)
    public void ejecutarTareasDiarias() {
        actualizarPrestamosVencidos();
        enviarRecordatorios();
    }

    private void actualizarPrestamosVencidos() {
        em.createQuery(
            "UPDATE Prestamo p SET p.estado = 'VENCIDO' " +
            "WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEstimada < :hoy")
            .setParameter("hoy", new Date())
            .executeUpdate();
    }

    private void enviarRecordatorios() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date enTresDias = cal.getTime();

        var proximos = prestamoDAO.findProximosAVencer(enTresDias);
        for (var p : proximos) {
            String correo = p.getUsuario().getCorreo();
            if (correo != null && !correo.isBlank()) {
                System.out.println("[NOTIFICACION] Recordatorio enviado a " + correo +
                    " — Libro: " + p.getLibro().getTitulo() +
                    " — Vence: " + p.getFechaDevolucionEstimada());
            }
        }
    }

    public void notificarPenalizacion(String codigoUniversitario) {
        System.out.println("[NOTIFICACION] Estudiante " + codigoUniversitario + " ha sido penalizado.");
    }
}

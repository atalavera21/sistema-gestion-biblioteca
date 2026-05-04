package com.senati.biblioteca.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaPrestamo;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaDevolucionEstimada;

    @Temporal(TemporalType.DATE)
    private Date fechaDevolucionReal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;

    @Column(nullable = false)
    private boolean activo;

    public Prestamo() {}

    public Long getId() { return id; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Date getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(Date fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public Date getFechaDevolucionEstimada() { return fechaDevolucionEstimada; }
    public void setFechaDevolucionEstimada(Date fechaDevolucionEstimada) { this.fechaDevolucionEstimada = fechaDevolucionEstimada; }

    public Date getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(Date fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
        this.activo = (estado == EstadoPrestamo.ACTIVO || estado == EstadoPrestamo.VENCIDO);
    }

    public boolean isActivo() { return activo; }
}

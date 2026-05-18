package com.senati.biblioteca.modelo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestamo_id", nullable = false, unique = true)
    private Prestamo prestamo;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaDevolucion;

    @Column(nullable = false)
    private int diasRetraso;

    @Column(nullable = false)
    private boolean aTiempo;

    public Devolucion() {}

    public Long getId() { return id; }

    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }

    public Date getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Date fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public int getDiasRetraso() { return diasRetraso; }
    public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }

    public boolean isaTiempo() { return aTiempo; }
    public void setaTiempo(boolean aTiempo) { this.aTiempo = aTiempo; }
}

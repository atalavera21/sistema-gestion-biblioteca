package com.senati.biblioteca.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String codigoUniversitario;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @Email
    @Column(length = 120)
    private String correo;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private boolean penalizado;

    @OneToMany(mappedBy = "usuario")
    private List<Prestamo> prestamos = new ArrayList<>();

    public Usuario() {}

    public Long getId() { return id; }

    public String getCodigoUniversitario() { return codigoUniversitario; }
    public void setCodigoUniversitario(String codigoUniversitario) { this.codigoUniversitario = codigoUniversitario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isPenalizado() { return penalizado; }
    public void setPenalizado(boolean penalizado) { this.penalizado = penalizado; }

    public List<Prestamo> getPrestamos() { return prestamos; }
}

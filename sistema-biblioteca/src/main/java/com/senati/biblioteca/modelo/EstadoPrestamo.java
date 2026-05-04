package com.senati.biblioteca.modelo;

public enum EstadoPrestamo {
    ACTIVO,      // libro prestado, dentro del plazo
    VENCIDO,     // libro prestado, supero la fecha de devolucion
    DEVUELTO,    // libro devuelto a tiempo
    PENALIZADO   // libro devuelto con retraso
}

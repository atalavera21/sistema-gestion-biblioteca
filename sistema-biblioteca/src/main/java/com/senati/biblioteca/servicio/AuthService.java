package com.senati.biblioteca.servicio;

public interface AuthService {

    boolean autenticar(String username, String password);

    String obtenerRol(String username);

}

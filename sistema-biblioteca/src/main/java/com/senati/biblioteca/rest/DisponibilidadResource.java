package com.senati.biblioteca.rest;

import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.servicio.LibroService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/libros")
@Produces(MediaType.APPLICATION_JSON)
public class DisponibilidadResource {

    @Inject
    private LibroService libroService;

    @GET
    @Path("/{id}/disponibilidad")
    public Response disponibilidad(@PathParam("id") Long id) {
        Libro libro = libroService.buscarPorId(id).orElse(null);
        if (libro == null)
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Libro no encontrado"))
                .build();

        Map<String, Object> result = new HashMap<>();
        result.put("libroId", libro.getId());
        result.put("titulo", libro.getTitulo());
        result.put("autor", libro.getAutor());
        result.put("categoria", libro.getCategoria() != null ? libro.getCategoria().getNombre() : null);
        result.put("stockTotal", libro.getStockTotal());
        result.put("disponible", libroService.isDisponible(libro));

        return Response.ok(result).build();
    }

    @GET
    public Response buscar(@QueryParam("titulo") String titulo,
                           @QueryParam("autor") String autor,
                           @QueryParam("categoria") String categoria) {
        List<Libro> libros = libroService.buscarConFiltros(titulo, autor, categoria);
        List<Map<String, Object>> resultado = libros.stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("titulo", l.getTitulo());
            m.put("autor", l.getAutor());
            m.put("categoria", l.getCategoria() != null ? l.getCategoria().getNombre() : null);
            m.put("stockTotal", l.getStockTotal());
            m.put("disponible", libroService.isDisponible(l));
            return m;
        }).toList();
        return Response.ok(resultado).build();
    }
}

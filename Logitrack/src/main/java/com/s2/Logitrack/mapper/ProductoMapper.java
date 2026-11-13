package com.s2.Logitrack.mapper;

import com.s2.Logitrack.dto.request.ProductoRequest;
import com.s2.Logitrack.dto.response.ProductoResponse;
import com.s2.Logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        return producto;
    }

    // Para PUT: actualiza datos descriptivos. El stock NO se toca aquí.
    public void actualizar(Producto producto, ProductoRequest request) {
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
    }

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getStock(),
                producto.getPrecio());
    }
}

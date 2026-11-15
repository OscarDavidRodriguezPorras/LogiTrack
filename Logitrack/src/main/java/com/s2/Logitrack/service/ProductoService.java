package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.request.ProductoRequest;
import com.s2.Logitrack.dto.response.ProductoResponse;
import com.s2.Logitrack.exception.ProductoNoEncontradoException;
import com.s2.Logitrack.mapper.ProductoMapper;
import com.s2.Logitrack.model.Producto;
import com.s2.Logitrack.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final int LIMITE_STOCK_BAJO = 10;

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return productoMapper.toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarStockBajo() {
        return productoRepository.findByStockLessThanOrderByStockAsc(LIMITE_STOCK_BAJO).stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    // El producto nace con stock 0; el stock sube con movimientos de ENTRADA
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto guardado = productoRepository.save(productoMapper.toEntity(request));
        return productoMapper.toResponse(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        productoMapper.actualizar(producto, request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) throws BadRequestException {
        Producto producto = buscar(id);

        if (producto.getStock() > 0) {
            throw new BadRequestException("No se puede eliminar el producto: todavía tiene "
                    + producto.getStock() + " unidades en stock");
        }
        // Si tiene movimientos o inventario asociado, la FK lo impide y el handler responde 400
        productoRepository.delete(producto);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto", id));
    }
}

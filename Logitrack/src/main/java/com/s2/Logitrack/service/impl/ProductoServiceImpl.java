package com.s2.Logitrack.service.impl;

import com.s2.Logitrack.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Override
    public ProductoDtoResponse guarda(ProductoDtoRequest dto) {
        Producto p = productoMapper.DtoToEntety(dto);
        return productoMapper.entityToDto(productoRepository.save(p));
    }

    @Override
    public ProductoDtoResponse actualizar(ProductoDtoRequest dto, Long id) {
        Producto p = buscarEntidad(id);
        productoMapper.updateEntityFromDto(p, dto);
        return productoMapper.entityToDto(productoRepository.save(p));
    }

    @Override
    public void eliminar(Long id) {
        Producto p = productoRepository.findById(id).orElseThrow(()-> new DatabaseRuleException("El producto a eliminar no existe"));
        if(detalleVentaRepository.existsByProductoId(p.getId()))
            throw new IllegalStateException("Este producto pertenece a un detalle de venta");
        productoRepository.delete(p);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDtoResponse> listar() {
        return productoRepository.findAll().stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDtoResponse buscarId(Long id) {
        return productoMapper.entityToDto(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDtoResponse> buscarMarca(String marca) {
        return productoRepository.findByMarca(marca).stream().map(productoMapper::entityToDto).toList();
    }

    private Producto buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("No se encontro el producto con id " + id));
    }
}

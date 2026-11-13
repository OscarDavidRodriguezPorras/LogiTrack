package com.s2.Logitrack.service.impl;

import com.s2.Logitrack.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaMapper detalleVentaMapper;
    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public DetalleVentaDtoResponse guardar(DetalleVentaDtoRequest dto) {
        Venta venta = buscarVenta(dto.ventaId());
        Producto producto = buscarProducto(dto.productoId());

        descontarStock(producto, dto.cantidad());
        BigDecimal subtotal = calcularSubtotal(producto, dto.cantidad());

        DetalleVenta detalle = detalleVentaMapper.dtoToEntity(venta, producto, dto.cantidad(), subtotal);
        DetalleVenta guardado = detalleVentaRepository.save(detalle);

        recalcularTotal(venta);
        return detalleVentaMapper.entityToDto(guardado);
    }

    @Override
    public DetalleVentaDtoResponse actualizar(DetalleVentaDtoRequest dto, Long id) {
        DetalleVenta detalle = buscarEntidad(id);
        Venta ventaAnterior = detalle.getVenta();

        // 1. Se devuelve el stock de la cantidad anterior
        Producto productoAnterior = detalle.getProducto();
        productoAnterior.setStock(productoAnterior.getStock() + detalle.getCantidad());
        productoRepository.save(productoAnterior);

        // 2. Se aplican los nuevos datos
        Venta ventaNueva = buscarVenta(dto.ventaId());
        Producto productoNuevo = buscarProducto(dto.productoId());
        descontarStock(productoNuevo, dto.cantidad());
        BigDecimal subtotal = calcularSubtotal(productoNuevo, dto.cantidad());

        detalleVentaMapper.updateEntity(detalle, ventaNueva, productoNuevo, dto.cantidad(), subtotal);
        DetalleVenta actualizado = detalleVentaRepository.save(detalle);

        // 3. Se recalculan los totales (de la venta anterior si cambio)
        recalcularTotal(ventaNueva);
        if (!ventaAnterior.getId().equals(ventaNueva.getId())) {
            recalcularTotal(ventaAnterior);
        }
        return detalleVentaMapper.entityToDto(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        DetalleVenta detalle = buscarEntidad(id);
        Venta venta = detalle.getVenta();

        Producto producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
        productoRepository.save(producto);

        detalleVentaRepository.delete(detalle);
        detalleVentaRepository.flush();
        recalcularTotal(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVentaDtoResponse> listar() {
        return detalleVentaRepository.findAll().stream().map(detalleVentaMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleVentaDtoResponse buscarId(Long id) {
        return detalleVentaMapper.entityToDto(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVentaDtoResponse> buscarVentaId(Long id) {
        buscarVenta(id);
        return detalleVentaRepository.findByVentaId(id).stream()
                .map(detalleVentaMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVentaDtoResponse> buscarProductoId(Long id) {
        buscarProducto(id);
        return detalleVentaRepository.findByProductoId(id).stream()
                .map(detalleVentaMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVentaDtoResponse> filtrarProductoSubtotal(Long id, BigDecimal subtotal) {
        return detalleVentaRepository.findByProductoIdAndSubtotalGreaterThanEqual(id, subtotal).stream()
                .map(detalleVentaMapper::entityToDto).toList();
    }

    // ---------- metodos privados ----------

    private DetalleVenta buscarEntidad(Long id) {
        return detalleVentaRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("No se encontro el detalle de venta con id " + id));
    }

    private Venta buscarVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("No se encontro la venta con id " + id));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("No se encontro el producto con id " + id));
    }

    private void descontarStock(Producto producto, Integer cantidad) {
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para '" + producto.getNombre()
                    + "'. Disponible: " + producto.getStock() + ", solicitado: " + cantidad);
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    private BigDecimal calcularSubtotal(Producto producto, Integer cantidad) {
        return producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
    }

    private void recalcularTotal(Venta venta) {
        BigDecimal total = detalleVentaRepository.findByVentaId(venta.getId()).stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venta.setTotal(total);
        ventaRepository.save(venta);
    }
}

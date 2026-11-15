package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.request.DetalleMovimientoRequest;
import com.s2.Logitrack.dto.request.MovimientoRequest;
import com.s2.Logitrack.dto.response.MovimientoResponse;
import com.s2.Logitrack.exception.ProductoNoEncontradoException;
import com.s2.Logitrack.mapper.MovimientoMapper;
import com.s2.Logitrack.model.*;
import com.s2.Logitrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoMapper movimientoMapper;


    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request, String username) throws BadRequestException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ProductoNoEncontradoException("Usuario '" + username + "' no encontrado"));

        validarBodegasSegunTipo(request);
        validarSinProductosRepetidos(request.detalles());

        Bodega origen = request.bodegaOrigenId() != null ? buscarBodega(request.bodegaOrigenId()) : null;
        Bodega destino = request.bodegaDestinoId() != null ? buscarBodega(request.bodegaDestinoId()) : null;

        // Si entra mercancía a una bodega, debe caber
        if (destino != null) {
            validarCapacidad(destino, totalUnidades(request.detalles()));
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(request.tipo());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(origen);
        movimiento.setBodegaDestino(destino);
        movimiento.setObservacion(request.observacion());

        for (DetalleMovimientoRequest d : request.detalles()) {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException("Producto", d.productoId()));
            int cantidad = d.cantidad();

            switch (request.tipo()) {
                case ENTRADA -> {
                    sumarInventario(destino, producto, cantidad);
                    producto.setStock(producto.getStock() + cantidad);
                }
                case SALIDA -> {
                    restarInventario(origen, producto, cantidad);
                    producto.setStock(producto.getStock() - cantidad);
                }
                case TRANSFERENCIA -> {
                    // El stock total del producto no cambia, solo de bodega
                    restarInventario(origen, producto, cantidad);
                    sumarInventario(destino, producto, cantidad);
                }
            }

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            movimiento.agregarDetalle(detalle);
        }

        Movimiento guardado = movimientoRepository.save(movimiento);
        return movimientoMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAllByOrderByFechaDesc().stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtener(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Movimiento", id));
        return movimientoMapper.toResponse(movimiento);
    }

    // Rango de fechas inclusivo: desde 00:00:00 del día "desde" hasta 23:59:59 del día "hasta"
    @Transactional(readOnly = true)
    public List<MovimientoResponse> buscarPorFechas(LocalDate desde, LocalDate hasta) throws BadRequestException {
        if (desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }
        return movimientoRepository
                .findByFechaBetweenOrderByFechaDesc(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX))
                .stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    private void validarBodegasSegunTipo(MovimientoRequest request) throws BadRequestException {
        Long origen = request.bodegaOrigenId();
        Long destino = request.bodegaDestinoId();

        switch (request.tipo()) {
            case ENTRADA -> {
                if (destino == null) {
                    throw new BadRequestException("La ENTRADA requiere bodega destino");
                }
                if (origen != null) {
                    throw new BadRequestException("La ENTRADA no debe tener bodega origen");
                }
            }
            case SALIDA -> {
                if (origen == null) {
                    throw new BadRequestException("La SALIDA requiere bodega origen");
                }
                if (destino != null) {
                    throw new BadRequestException("La SALIDA no debe tener bodega destino");
                }
            }
            case TRANSFERENCIA -> {
                if (origen == null || destino == null) {
                    throw new BadRequestException("La TRANSFERENCIA requiere bodega origen y bodega destino");
                }
                if (origen.equals(destino)) {
                    throw new BadRequestException("La bodega origen y la bodega destino deben ser distintas");
                }
            }
        }
    }

    private void validarSinProductosRepetidos(List<DetalleMovimientoRequest> detalles) throws BadRequestException {
        Set<Long> vistos = new HashSet<>();
        for (DetalleMovimientoRequest d : detalles) {
            if (!vistos.add(d.productoId())) {
                throw new BadRequestException("El producto con id " + d.productoId()
                        + " está repetido en el movimiento; agrupa las cantidades en una sola línea");
            }
        }
    }

    private int totalUnidades(List<DetalleMovimientoRequest> detalles) {
        return detalles.stream().mapToInt(DetalleMovimientoRequest::cantidad).sum();
    }

    private void validarCapacidad(Bodega bodega, int unidadesQueEntran) throws BadRequestException {
        int ocupado = inventarioRepository.findByBodegaId(bodega.getId()).stream()
                .mapToInt(Inventario::getCantidad)
                .sum();
        int disponible = bodega.getCapacidad() - ocupado;
        if (unidadesQueEntran > disponible) {
            throw new BadRequestException("La bodega '" + bodega.getNombre() + "' no tiene capacidad suficiente "
                    + "(disponible: " + disponible + ", se intentan ingresar: " + unidadesQueEntran + ")");
        }
    }

    private void sumarInventario(Bodega bodega, Producto producto, int cantidad) {
        Inventario inventario = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setBodega(bodega);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    return inventarioRepository.save(nuevo);
                });
        inventario.setCantidad(inventario.getCantidad() + cantidad);
    }

    private void restarInventario(Bodega bodega, Producto producto, int cantidad) throws BadRequestException {
        Inventario inventario = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseThrow(() -> new BadRequestException("El producto '" + producto.getNombre()
                        + "' no tiene existencias en la bodega '" + bodega.getNombre() + "'"));

        if (inventario.getCantidad() < cantidad) {
            throw new BadRequestException("Stock insuficiente de '" + producto.getNombre()
                    + "' en la bodega '" + bodega.getNombre() + "' (disponible: "
                    + inventario.getCantidad() + ", solicitado: " + cantidad + ")");
        }
        inventario.setCantidad(inventario.getCantidad() - cantidad);
    }

    private Bodega buscarBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Bodega", id));
    }
}
package com.s2.Logitrack.mapper;

import com.s2.Logitrack.dto.request.BodegaRequest;
import com.s2.Logitrack.dto.response.BodegaResponse;
import com.s2.Logitrack.model.Bodega;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public Bodega toEntity(BodegaRequest request) {
        Bodega bodega = new Bodega();
        actualizar(bodega, request);
        return bodega;
    }

    // Copia los datos del request sobre una bodega existente (para PUT)
    public void actualizar(Bodega bodega, BodegaRequest request) {
        bodega.setNombre(request.nombre());
        bodega.setUbicacion(request.ubicacion());
        bodega.setCapacidad(request.capacidad());
        bodega.setEncargado(request.encargado());
    }

    public BodegaResponse toResponse(Bodega bodega) {
        return new BodegaResponse(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getUbicacion(),
                bodega.getCapacidad(),
                bodega.getEncargado());
    }
}

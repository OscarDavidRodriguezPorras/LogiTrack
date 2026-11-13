package com.s2.Logitrack.dto.response;

public record StockBodegaResponse(
        Long bodegaId,
        String bodega,
        Long stockTotal
) {
}

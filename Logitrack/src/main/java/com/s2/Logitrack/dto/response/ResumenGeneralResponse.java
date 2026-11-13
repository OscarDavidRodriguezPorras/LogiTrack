package com.s2.Logitrack.dto.response;

import java.util.List;

public record ResumenGeneralResponse(
        List<StockBodegaResponse> stockPorBodega,
        List<ProductoMovidoResponse> productosMasMovidos
) {
}

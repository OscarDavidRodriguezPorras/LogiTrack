package com.s2.Logitrack.auditoria;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;


final class JsonMapa {

    private JsonMapa() {
    }

    static String aJson(Map<String, Object> mapa) {
        if (mapa == null) {
            return null;
        }
        return valor(mapa);
    }

    private static String valor(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof BigDecimal numero) {
            return numero.toPlainString();
        }
        if (v instanceof Number || v instanceof Boolean) {
            return v.toString();
        }
        if (v instanceof Map<?, ?> mapa) {
            StringBuilder sb = new StringBuilder("{");
            boolean primero = true;
            for (Map.Entry<?, ?> e : mapa.entrySet()) {
                if (!primero) {
                    sb.append(",");
                }
                primero = false;
                sb.append(texto(String.valueOf(e.getKey()))).append(":").append(valor(e.getValue()));
            }
            return sb.append("}").toString();
        }
        if (v instanceof Collection<?> lista) {
            StringBuilder sb = new StringBuilder("[");
            boolean primero = true;
            for (Object item : lista) {
                if (!primero) {
                    sb.append(",");
                }
                primero = false;
                sb.append(valor(item));
            }
            return sb.append("]").toString();
        }
        return texto(v.toString());
    }

    private static String texto(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append("\"").toString();
    }
}
package conversor;

import java.math.BigDecimal;


public class NumberParser {

    public static BigDecimal parseToBigDecimal(String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("valor vazio");
        }
        String v = s.trim();

        if (v.contains(",") && v.contains(".")) {
            v = v.replace(".", "").replace(",", ".");
        } else if (v.contains(",") && !v.contains(".")) {
            v = v.replace(",", ".");
        } else {
            v = v.replace(",", "");
        }
        // tratamento de erro BigDecimal
        try {
            return new BigDecimal(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("formato numérico inválido: " + s);
        }
    }
}

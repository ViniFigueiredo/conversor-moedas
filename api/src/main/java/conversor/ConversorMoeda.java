package conversor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface ConversorMoeda {
    Map<String, Object> convertSync(String from, String to, BigDecimal amount);

    // conversão assíncrona
    CompletableFuture<Map<String, Object>> convertAsync(String from, String to, BigDecimal amount);
}

package conversor;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component("usdConverter")
public class USDtoBRLConverter implements ConversorMoeda {

    private final RestTemplate rest;
    private final Executor executor;
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    public USDtoBRLConverter(RestTemplateBuilder builder, Executor asyncExecutor) {
        this.rest = builder.build();
        this.executor = asyncExecutor;
    }

    @Override
    public Map<String, Object> convertSync(String from, String to, BigDecimal amount) {
        @SuppressWarnings("unchecked")
        Map<String,Object> resp = rest.getForObject(BASE_URL, Map.class);
        BigDecimal rate = extractBrlRate(resp);
        BigDecimal converted = rate == null || amount == null ? null : amount.multiply(rate);
        return Map.of(
                "from", "USD",
                "to", "BRL",
                "rate", rate,
                "converted", converted
        );
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Map<String, Object>> convertAsync(String from, String to, BigDecimal amount) {
        // supplyAsync wraps the synchronous call into the executor to avoid blocking caller thread
        return CompletableFuture.supplyAsync(() -> convertSync(from, to, amount), executor);
    }

    @SuppressWarnings("unchecked")
    private BigDecimal extractBrlRate(Map<String, Object> resp) {
        if (resp == null) return null;
        Object ratesObj = resp.get("rates");
        if (!(ratesObj instanceof Map)) return null;
        Map<String, Object> rates = (Map<String, Object>) ratesObj;
        Object brl = rates.get("BRL");
        if (brl == null) return null;
        if (brl instanceof Number) {
            return BigDecimal.valueOf(((Number) brl).doubleValue());
        }
        try {
            return new BigDecimal(brl.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

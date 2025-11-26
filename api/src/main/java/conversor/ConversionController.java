package conversor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/convert")
public class ConversionController {

    private final ConversorMoeda usdConverter;
    private final ConversorMoeda eurConverter;

    public ConversionController(@Qualifier("usdConverter") ConversorMoeda usdConverter,
                                @Qualifier("eurConverter") ConversorMoeda eurConverter) {
        this.usdConverter = usdConverter;
        this.eurConverter = eurConverter;
    }

    @GetMapping("/both")
    public CompletableFuture<Map<String, Object>> convertBoth(@RequestParam String amount) {
        BigDecimal amt;
        try {
            amt = NumberParser.parseToBigDecimal(amount);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        CompletableFuture<Map<String, Object>> f1 = usdConverter.convertAsync("USD", "BRL", amt);
        CompletableFuture<Map<String, Object>> f2 = eurConverter.convertAsync("EUR", "BRL", amt);

        return CompletableFuture.allOf(f1, f2).thenApply(v -> Map.of(
                "usd_to_brl", f1.join(),
                "eur_to_brl", f2.join()
        ));
    }

    @GetMapping("/usd")
    public CompletableFuture<Map<String, Object>> convertUsd(@RequestParam String amount) {
        BigDecimal amt = NumberParser.parseToBigDecimal(amount);
        return usdConverter.convertAsync("USD", "BRL", amt);
    }

    @GetMapping("/eur")
    public CompletableFuture<Map<String, Object>> convertEur(@RequestParam String amount) {
        BigDecimal amt = NumberParser.parseToBigDecimal(amount);
        return eurConverter.convertAsync("EUR", "BRL", amt);
    }
}

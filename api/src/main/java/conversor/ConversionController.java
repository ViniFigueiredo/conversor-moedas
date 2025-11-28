package conversor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;


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
